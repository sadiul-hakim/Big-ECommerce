package org.shopme.site.customer;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Customer;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.MailServerSettingBag;
import org.shopme.site.country.CountryRepository;
import org.shopme.site.setting.SettingService;
import org.shopme.site.state.StateRepository;
import org.shopme.site.util.AppUtility;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;
    private final SettingService settingService;

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
