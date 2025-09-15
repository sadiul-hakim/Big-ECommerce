package org.shopme.site.customer;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Customer;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.MailServerSettingBag;
import org.shopme.site.country.CountryRepository;
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

    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String SAVING_CONDITION = "savingCustomer";
    private static final String MESSAGE = "message";
    private static final String POJO_NAME = "customer";

    @GetMapping("/profile")
    public String profile(@RequestParam int id, Model model) {
        Optional<Customer> customerOpt = customerService.findById(id);

        if (customerOpt.isEmpty()) {
            return "redirect:/";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof CustomUserDetails(Customer customer)) {

            if(customerOpt.get().getId() != customer.getId()){
                return "redirect:/";
            }
        }

        model.addAttribute("countries", countryRepository.findAll());
        model.addAttribute("states", stateRepository.findAll());
        model.addAttribute(POJO_NAME, customerOpt.get());
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
    String register(@ModelAttribute @Valid Customer customer, BindingResult result, @RequestParam MultipartFile picture,
                    Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("countries", countryRepository.findAll());
            model.addAttribute("states", stateRepository.findAll());
            return "registerPage";
        }

        JpaResult savedResult = customerService.save(customer, picture);
        sendVerificationMail(request, (Customer) savedResult.entity());

        return "registrationSuccess";
    }

    private void sendVerificationMail(HttpServletRequest request, Customer entity) {
        MailServerSettingBag mailServerSettingBag = settingService.getMailServerSettingBag();
        JavaMailSenderImpl javaMailSender = AppUtility.prepareMailSender(mailServerSettingBag);

        String toAddress = entity.getEmail();
        String subject = mailServerSettingBag.getCustomerVerifiedSubject();
        String content = mailServerSettingBag.getCustomerVerifiedContent();

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(mailServerSettingBag.getFromAddress(), mailServerSettingBag.getSenderName());
            helper.setTo(toAddress);
            helper.setSubject(subject);

            content = content.replace("[[name]]", entity.getFullName());
            String verifyUrl = AppUtility.getSiteUrl(request) + "/verify?code=" + entity.getVerificationCode();
            content = content.replace("[[url]]", verifyUrl);

            helper.setText(content, true);

            javaMailSender.send(message);
            log.info("Sent verification email to {}", entity.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to {} , error {}", entity.getEmail(), e.getMessage());
        }
    }
}
