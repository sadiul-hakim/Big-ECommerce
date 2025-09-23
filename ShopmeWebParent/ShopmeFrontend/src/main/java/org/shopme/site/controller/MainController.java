package org.shopme.site.controller;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Customer;
import org.shopme.common.entity.MailToken;
import org.shopme.common.pojo.ChangePasswordPojo;
import org.shopme.common.pojo.CustomerRegistrationPojo;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.site.country.CountryService;
import org.shopme.site.customer.CustomerService;
import org.shopme.site.mailToken.MailTokenService;
import org.shopme.site.state.StateService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CountryService countryService;
    private final StateService stateService;
    private final CustomerService customerService;
    private final MailTokenService mailTokenService;

    @GetMapping("/")
    public String viewHomePage() {

        return "index";
    }

    @GetMapping("/loginPage")
    public String loginPage() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "loginPage";
        }
        return "redirect:/";
    }

    @GetMapping("/registerPage")
    public String registerPage(Model model) {

        model.addAttribute("states", stateService.findAll());
        model.addAttribute("countries", countryService.findAll());

        if (model.getAttribute("customer") == null) {
            model.addAttribute("customer", new CustomerRegistrationPojo());
        }
        return "registerPage";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String code) {
        boolean verified = customerService.verify(code);
        return verified ? "verified_successfully" : "verification_failed";
    }

    @GetMapping("/verify_forgot_password_token")
    public String verifyForgotPasswordToken(@RequestParam String code, Model model) {
        JpaResult result = mailTokenService.verify(code, false);

        MailToken mailToken = (MailToken) result.entity();
        model.addAttribute("customerId", mailToken.getCustomerId());
        model.addAttribute("token", code);
        model.addAttribute("changePasswordPojo", new ChangePasswordPojo());

        return result.type().equals(JpaResultType.SUCCESSFUL) ? "forgot_password/new_password_page" :
                "forgot_password/verification_failed";
    }

    @GetMapping("/forgot_password")
    public String forgotPasswordEmailPage() {
        return "forgot_password/forgot_password_email_page";
    }
}
