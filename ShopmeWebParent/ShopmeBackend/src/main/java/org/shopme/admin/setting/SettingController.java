package org.shopme.admin.setting;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.country.CountryService;
import org.shopme.admin.currency.CurrencyRepository;
import org.shopme.admin.state.StateService;
import org.shopme.common.entity.Currency;
import org.shopme.common.entity.Setting;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
        model.addObject("currencies", currencies);
        model.addObject("countries", countryService.findAll());
        model.addObject("states", stateService.findAll());

        for (Setting setting : settings) {
            model.addObject(setting.getKey(), setting.getValue());
        }
        model.setViewName(PAGE);

        return model;
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
