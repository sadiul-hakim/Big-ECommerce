package org.shopme.site.customer;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Customer;
import org.shopme.common.entity.MailToken;
import org.shopme.common.enumeration.MailTokenType;
import org.shopme.common.pojo.ChangePasswordPojo;
import org.shopme.common.pojo.CustomerRegistrationPojo;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.MailServerSettingBag;
import org.shopme.common.util.VerificationCodeGenerator;
import org.shopme.site.country.CountryRepository;
import org.shopme.site.mailToken.MailTokenService;
import org.shopme.site.security.CustomUserDetails;
import org.shopme.site.setting.SettingService;
import org.shopme.site.state.StateRepository;
import org.shopme.site.util.AppUtility;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;
    private final SettingService settingService;
    private final MailTokenService mailTokenService;

    private static final String UPDATING_PASSWORD = "updatingPassword";
    private static final String UPDATED_PASSWORD = "updatedPassword";
    private static final String SAVING_CONDITION = "savingCustomer";
    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String REGISTRATION_ERROR = "registrationError";
    private static final String MESSAGE = "message";
    private static final String POJO_NAME = "customer";

    @PostMapping("/new_password")
    public String newPassword(@RequestParam String token, @RequestParam int customerId, @ModelAttribute @Valid ChangePasswordPojo changePasswordPojo,
                              BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("customerId", customerId);
            return "forgot_password/new_password_page";
        }

        JpaResult jpaResult = customerService.newPassword(token, customerId, changePasswordPojo);
        if (jpaResult.type().equals(JpaResultType.FAILED)) {

            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute(MESSAGE, jpaResult.message());
            return "redirect:/forgot_password/new_password_page";
        }

        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute(MESSAGE, jpaResult.message());

        return "redirect:/loginPage";
    }

    // Either specify a name of pojo in @ModelAttribute or use default naming convention
    @PostMapping("/change_password")
    public String changePassword(@RequestParam int customerId, @ModelAttribute @Valid ChangePasswordPojo changePasswordPojo,
                                 BindingResult result, RedirectAttributes redirectAttributes, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("customerId", customerId);
            return "change_password";
        }

        Optional<Customer> customerOpt = customerService.findById(customerId);
        if (customerOpt.isEmpty()) {
            return "redirect:/";
        }

        Customer customer = customerOpt.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof CustomUserDetails(Customer c)) {

            if (customer.getId() != c.getId()) {
                return "redirect:/";
            }
        }

        JpaResult jpaResult = customerService.updatePassword(changePasswordPojo, customer);

        redirectAttributes.addFlashAttribute(UPDATING_PASSWORD, true);
        redirectAttributes.addFlashAttribute(UPDATED_PASSWORD, jpaResult.type().equals(JpaResultType.SUCCESSFUL));
        redirectAttributes.addFlashAttribute(MESSAGE, jpaResult.message());
        return "redirect:/customer/change_password_page?id=" + customerId;
    }

    @GetMapping("/change_password_page")
    public String changePasswordPage(@RequestParam int id, Model model) {
        Optional<Customer> customerOpt = customerService.findById(id);
        if (customerOpt.isEmpty()) {
            return "redirect:/";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof CustomUserDetails(Customer customer)) {

            if (customerOpt.get().getId() != customer.getId()) {
                return "redirect:/";
            }
        }

        model.addAttribute("changePasswordPojo", new ChangePasswordPojo());
        model.addAttribute("customerId", id);

        return "change_password";
    }

    @GetMapping("/profile")
    public String profile(@RequestParam int id, Model model) {

        Optional<Customer> customerOpt = customerService.findById(id);
        if (customerOpt.isEmpty()) {
            return "redirect:/";
        }

        Customer customer = customerOpt.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof CustomUserDetails(Customer c)) {

            if (customer.getId() != c.getId()) {
                return "redirect:/";
            }
        }

        model.addAttribute(POJO_NAME, customer);
        model.addAttribute(SAVING_CONDITION, false);
        model.addAttribute(SAVED_CONDITION, false);
        model.addAttribute(MESSAGE, "");

        return "profile";
    }

    @PostMapping("/update")
    public String update(
            @ModelAttribute Customer customer,
            @RequestParam MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {

        var result = customerService.updateCustomer(customer, file);

        if (result.type().equals(JpaResultType.FAILED)) {
            redirectAttributes.addFlashAttribute(SAVING_CONDITION, true);
            redirectAttributes.addFlashAttribute(MESSAGE, result.message());
            redirectAttributes.addFlashAttribute(POJO_NAME, customer);
        } else {
            redirectAttributes.addFlashAttribute(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
            redirectAttributes.addFlashAttribute(SAVING_CONDITION, true);
            redirectAttributes.addFlashAttribute(MESSAGE, result.message());
        }
        return "redirect:/customer/profile?id=" + customer.getId();
    }

    @PostMapping("/register")
    String register(@ModelAttribute @Valid CustomerRegistrationPojo customer, BindingResult result, @RequestParam MultipartFile picture,
                    Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("countries", countryRepository.findAll());
            model.addAttribute("states", stateRepository.findAll());
            return "registerPage";
        }

        JpaResult savedResult = customerService.register(customer, picture);
        Customer entity = (Customer) savedResult.entity();

        if (entity == null || savedResult.type().equals(JpaResultType.FAILED)) {

            redirectAttributes.addFlashAttribute(REGISTRATION_ERROR, true);
            redirectAttributes.addFlashAttribute(MESSAGE, savedResult.message());
            redirectAttributes.addFlashAttribute("customer", customer);
            return "redirect:/registerPage";
        }

        try {

            MailToken token = mailTokenService.findByCustomerIdAndType(entity.getId(), MailTokenType.EMAIL_VERIFICATION);
            String verifyUrl = AppUtility.getSiteUrl(request) + "/verify?code=" + token.getToken();
            sendVerificationMail(entity, verifyUrl, MailTokenType.EMAIL_VERIFICATION);
        } catch (Exception ex) {
            log.error("Failed to send Send mail, error {}", ex.getMessage());
        }

        return "registrationSuccess";
    }

    @GetMapping("/validate_email_send_token")
    public String validateEmailSendToken(@RequestParam String email, RedirectAttributes attributes, HttpServletRequest request) {

        Optional<Customer> customerOptional = customerService.findByEmail(email);
        if (customerOptional.isEmpty()) {
            attributes.addFlashAttribute("error", true);
            attributes.addFlashAttribute(MESSAGE, "Could not find any registered account with this email!");
            return "redirect:/forgot_password";
        }

        String token = VerificationCodeGenerator.generateCode(64);
        Customer entity = customerOptional.get();
        mailTokenService.save(entity.getId(), token, MailTokenType.FORGOT_PASSWORD, 15);
        String verifyUrl = AppUtility.getSiteUrl(request) + "/verify_forgot_password_token?code=" + token;
        sendVerificationMail(entity, verifyUrl, MailTokenType.FORGOT_PASSWORD);
        attributes.addFlashAttribute("ok", true);
        attributes.addFlashAttribute(MESSAGE, "Successfully send a Reset Password link to this email.");

        return "redirect:/forgot_password";
    }

    private void sendVerificationMail(Customer entity, String verifyUrl, MailTokenType type) {
        MailServerSettingBag mailServerSettingBag = settingService.getMailServerSettingBag();
        JavaMailSenderImpl javaMailSender = AppUtility.prepareMailSender(mailServerSettingBag);

        String toAddress = entity.getEmail();
        String subject;
        String content;

        if (type.equals(MailTokenType.EMAIL_VERIFICATION)) {
            subject = mailServerSettingBag.getCustomerVerifiedSubject();
            content = mailServerSettingBag.getCustomerVerifiedContent();
        } else if (type.equals(MailTokenType.FORGOT_PASSWORD)) {
            subject = mailServerSettingBag.getForgotPasswordSubject();
            content = mailServerSettingBag.getForgotPasswordContent();
        } else if (type.equals(MailTokenType.ORDER_CONFIRMATION)) {
            subject = mailServerSettingBag.getOrderConfirmationSubject();
            content = mailServerSettingBag.getOrderConfirmationContent();
        } else {
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(mailServerSettingBag.getFromAddress(), mailServerSettingBag.getSenderName());
            helper.setTo(toAddress);
            helper.setSubject(subject);

            content = content.replace("[[name]]", entity.getFullName());
            content = content.replace("[[url]]", verifyUrl);

            helper.setText(content, true);

            javaMailSender.send(message);
            log.info("Sent verification email to {}", entity.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to {} , error {}", entity.getEmail(), e.getMessage());
        }
    }
}
