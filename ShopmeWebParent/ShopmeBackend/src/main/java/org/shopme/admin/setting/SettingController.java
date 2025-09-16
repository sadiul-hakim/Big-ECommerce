package org.shopme.admin.setting;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.country.CountryService;
import org.shopme.admin.currency.CurrencyRepository;
import org.shopme.admin.state.StateService;
import org.shopme.common.entity.Currency;
import org.shopme.common.entity.Setting;
import org.shopme.common.enumeration.SettingCategory;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {
    private static final String PAGE = "settings";
    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String SAVING_CONDITION = "savingSetting";
    private static final String MESSAGE = "message";

    private final SettingService service;
    private final CurrencyRepository currencyRepository;
    private final CountryService countryService;
    private final StateService stateService;

    @GetMapping
    public ModelAndView page(ModelAndView model) {
        List<Setting> settings = service.findAll();
        List<Currency> currencies = currencyRepository.findAllByOrderByNameAsc();

        Map<String, String> generalSetting = settings.stream().filter(setting -> setting.getCategory()
                        .equals(SettingCategory.GENERAL) || setting.getCategory().equals(SettingCategory.CURRENCY))
                .collect(Collectors.toMap(Setting::getKey, Setting::getValue));

        Map<String, String> mailServerSetting = settings.stream().filter(setting -> setting.getCategory()
                        .equals(SettingCategory.MAIL_SERVICE))
                .collect(Collectors.toMap(Setting::getKey, Setting::getValue));

        Map<String, String> mailTemplateSetting = settings.stream().filter(setting -> setting.getCategory()
                        .equals(SettingCategory.MAIL_TEMPLATES))
                .collect(Collectors.toMap(Setting::getKey, Setting::getValue));

        model.addObject("currencies", currencies);
        model.addObject("countries", countryService.findAll());
        model.addObject("states", stateService.findAll());
        model.addObject("generalSetting", generalSetting);
        model.addObject("mailServerSetting", mailServerSetting);
        model.addObject("mailTemplateSetting", mailTemplateSetting);

        model.setViewName(PAGE);

        return model;
    }

    @PostMapping("/save-mail-template")
    public String saveCustomerVerificationMailTemplate(
            @RequestParam(defaultValue = "") String CUSTOMER_VERIFIED_SUBJECT,
            @RequestParam(defaultValue = "") String CUSTOMER_VERIFIED_CONTENT,
            @RequestParam(defaultValue = "") String ORDER_CONFIRMATION_SUBJECT,
            @RequestParam(defaultValue = "") String ORDER_CONFIRMATION_CONTENT,
            @RequestParam(defaultValue = "") String FORGOT_PASSWORD_SUBJECT,
            @RequestParam(defaultValue = "") String FORGOT_PASSWORD_CONTENT,
            RedirectAttributes attributes
    ) {
        service.saveTemplate(CUSTOMER_VERIFIED_SUBJECT, CUSTOMER_VERIFIED_CONTENT, ORDER_CONFIRMATION_SUBJECT,
                ORDER_CONFIRMATION_CONTENT, FORGOT_PASSWORD_SUBJECT, FORGOT_PASSWORD_CONTENT);

        attributes.addFlashAttribute(MESSAGE, "Setting is saved successfully!");
        attributes.addFlashAttribute(SAVING_CONDITION, true);
        attributes.addFlashAttribute(SAVED_CONDITION, true);

        return "redirect:/settings";
    }

    @PostMapping("/save-mail-server")
    public String saveMailServerSetting(
            @RequestParam String MAIL_HOST,
            @RequestParam String MAIL_PORT,
            @RequestParam String MAIL_USERNAME,
            @RequestParam String MAIL_PASSWORD,
            @RequestParam String MAIL_FROM,
            @RequestParam String MAIL_SENDER_NAME,
            @RequestParam(defaultValue = "false") boolean SMTP_AUTH,
            @RequestParam(defaultValue = "false") boolean SMTP_SECURED,
            RedirectAttributes attributes
    ) {
        service.saveMailServerSettings(MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD, MAIL_FROM, MAIL_SENDER_NAME,
                SMTP_AUTH, SMTP_SECURED);

        attributes.addFlashAttribute(MESSAGE, "Setting is saved successfully!");
        attributes.addFlashAttribute(SAVING_CONDITION, true);
        attributes.addFlashAttribute(SAVED_CONDITION, true);

        return "redirect:/settings";
    }

    @PostMapping("/save_general")
    public String saveGeneralSetting(
            @RequestParam MultipartFile SITE_LOGO,
            @RequestParam String SITE_NAME,
            @RequestParam String COPYRIGHT,
            @RequestParam String THOUSAND_POINT_TYPE,
            @RequestParam String CURRENCY_SYMBOL_POSITION,
            @RequestParam String DECIMAL_POINT_TYPE,
            @RequestParam String CURRENCY_ID,
            @RequestParam String DECIMAL_DIGITS,
            RedirectAttributes attributes
    ) {

        service.saveGeneralSettings(
                SITE_LOGO,
                SITE_NAME,
                COPYRIGHT,
                THOUSAND_POINT_TYPE,
                CURRENCY_SYMBOL_POSITION,
                DECIMAL_POINT_TYPE,
                CURRENCY_ID,
                DECIMAL_DIGITS
        );

        attributes.addFlashAttribute(MESSAGE, "Setting is saved successfully!");
        attributes.addFlashAttribute(SAVING_CONDITION, true);
        attributes.addFlashAttribute(SAVED_CONDITION, true);

        return "redirect:/settings";
    }

    @PostMapping("/save_country")
    public String saveCountry(@RequestParam String name,
                              @RequestParam String code,
                              RedirectAttributes attributes
    ) {
        JpaResult result = countryService.save(name, code);

        attributes.addFlashAttribute(MESSAGE, result.message());
        attributes.addFlashAttribute(SAVING_CONDITION, true);
        attributes.addFlashAttribute(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));

        return "redirect:/settings";
    }

    @GetMapping("/delete_country/{name}")
    public String deleteCountry(@PathVariable String name, RedirectAttributes attributes) {
        JpaResult result = countryService.delete(name);
        attributes.addFlashAttribute(MESSAGE, result.message());
        attributes.addFlashAttribute(SAVING_CONDITION, true);
        attributes.addFlashAttribute(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        return "redirect:/settings";
    }

    @PostMapping("/save_state")
    public String saveState(@RequestParam String name,
                            @RequestParam String country,
                            RedirectAttributes attributes
    ) {
        JpaResult result = stateService.save(name, country);

        attributes.addFlashAttribute(MESSAGE, result.message());
        attributes.addFlashAttribute(SAVING_CONDITION, true);
        attributes.addFlashAttribute(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));

        return "redirect:/settings";
    }

    @GetMapping("/delete_state/{id}")
    public String deleteState(@PathVariable int id, RedirectAttributes attributes) {
        JpaResult result = stateService.delete(id);
        attributes.addFlashAttribute(MESSAGE, result.message());
        attributes.addFlashAttribute(SAVING_CONDITION, true);
        attributes.addFlashAttribute(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        return "redirect:/settings";
    }
}
