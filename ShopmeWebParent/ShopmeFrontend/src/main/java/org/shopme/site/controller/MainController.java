package org.shopme.site.controller;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Customer;
import org.shopme.site.country.CountryService;
import org.shopme.site.customer.CustomerService;
import org.shopme.site.state.StateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CountryService countryService;
    private final StateService stateService;
    private final CustomerService customerService;

    @GetMapping("/")
    public ModelAndView viewHomePage(ModelAndView model) {
        model.setViewName("index");
        return model;
    }

    @GetMapping("/loginPage")
    public ModelAndView loginPage(ModelAndView model) {
        model.setViewName("loginPage");
        return model;
    }

    @GetMapping("/registerPage")
    public ModelAndView registerPage(ModelAndView model) {

        model.addObject("states", stateService.findAll());
        model.addObject("countries", countryService.findAll());
        model.addObject("customer", new Customer());
        model.setViewName("registerPage");
        return model;
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String code){
        boolean verified = customerService.verify(code);
        return verified ? "verified_successfully" : "verification_failed";
    }
}
