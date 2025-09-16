1. Add Suggestion in Search Bars - Brand, Categories, Product
2. Forgot Password

### 🔹 Steps in Forgot Password Flow

1. **User submits email**

    * You have a form like `/forgot-password` where user enters their email.
    * On submit → You check if the email exists in your `Customer` (or `User`) table.
    * If not found → return with error message.
    * If found → generate a **reset token (random number or UUID)**.

2. **Generate & store reset token**

    * Generate a random token (secure, not just random int).
    * Store token in DB with expiry time (e.g., 15 minutes). Example table:

      ```sql
      password_reset_tokens (
         id BIGINT,
         user_id BIGINT,
         token VARCHAR(64),
         expiry DATETIME
      )
      ```
    * Or add columns in your `Customer` entity (resetToken, resetTokenExpiry).

3. **Send reset email**

    * Send an email with link or code.
    * Two approaches:

        * ✅ **Link-based** (recommended):
          `https://yourdomain.com/reset-password?token=xxxx`
        * ❌ **Code-based** (like you said: user has to type number manually). Works but UX is a bit worse.

4. **Verify token/code**

    * User clicks link or enters code.
    * Backend validates token/code:

        * Exists?
        * Not expired?
        * Matches user?

5. **Show new password form**

    * If token valid → show `reset_password.html` (with new password + confirm password).

6. **Update password**

    * Validate new password (strength, confirm match).
    * Encode using `PasswordEncoder`.
    * Save into DB.
    * Invalidate token (delete it or mark as used).

---

### 🔹 Code Sketch

Here’s a simple sketch (Spring MVC style):

**Controller**

```java
@Controller
public class ForgotPasswordController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        Optional<Customer> userOpt = customerService.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Email not found");
            return "forgot_password";
        }

        String token = UUID.randomUUID().toString();
        customerService.createPasswordResetToken(userOpt.get(), token);

        String resetLink = "http://localhost:8090/reset-password?token=" + token;
        // sendEmail(email, resetLink);

        model.addAttribute("message", "Password reset link sent to your email");
        return "forgot_password";
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam String token, Model model) {
        if (!customerService.isValidResetToken(token)) {
            model.addAttribute("error", "Invalid or expired token");
            return "reset_password";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String processReset(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/reset-password?token=" + token;
        }

        if (!customerService.resetPassword(token, password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token");
            return "redirect:/reset-password?token=" + token;
        }

        redirectAttributes.addFlashAttribute("message", "Password reset successful");
        return "redirect:/login";
    }
}
```

---

### 🔹 Security Considerations

* Use **`BCryptPasswordEncoder`** (or Argon2) to store new password.
* Token should be **random UUID** or secure random string (not just `Random.nextInt()`).
* Token must **expire** (avoid infinite reset links).
* Never show whether email exists in system (use a generic success message to avoid user enumeration).

