package org.shopme.admin.shippingRate;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.country.CountryService;
import org.shopme.admin.state.StateService;
import org.shopme.common.entity.ShippingRate;
import org.shopme.common.pojo.TableUrlPojo;
import org.shopme.common.util.JpaResultType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingRateService service;
    private final CountryService countryService;
    private final StateService stateService;

    private final TableUrlPojo pageUrl = new TableUrlPojo("/shipping/search", "/shipping",
            "/shipping/export-csv", "/shipping/create-page");

    private static final String PAGINATION_RESULT = "shippingResult";
    private static final String TABLE_URL = "tableUrl";
    private static final String PAGE = "rates";
    private static final String POJO_NAME = "rate";
    private static final String CREATE_PAGE = "create_shipping_rate";
    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String SAVING_CONDITION = "savingRate";
    private static final String DELETED_CONDITION = "deletedSuccessfully";
    private static final String DELETING_CONDITION = "deletingRate";
    private static final String MESSAGE = "message";

    @GetMapping
    public String page(@RequestParam(defaultValue = "0") int page, Model model) {
        var result = service.findAllPaginated(page);
        model.addAttribute(PAGINATION_RESULT, result);
        model.addAttribute(TABLE_URL, pageUrl);

        return PAGE;
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var result = service.search(text, page);
        model.addObject(PAGINATION_RESULT, result);
        model.addObject(TABLE_URL, pageUrl);
        model.setViewName(PAGE);

        return model;
    }

    @GetMapping("/create-page")
    public ModelAndView createPage(ModelAndView model) {

        var countries = countryService.findAll();
        var states = stateService.findAll();
        model.addObject(POJO_NAME, new ShippingRate());
        model.addObject("countries", countries);
        model.addObject("states", states);
        model.setViewName(CREATE_PAGE);

        return model;
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute ShippingRate rate,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        var result = rate.getId() != 0 ? service.update(rate) : service.save(rate);
        redirectAttributes.addFlashAttribute(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        redirectAttributes.addFlashAttribute(SAVING_CONDITION, true);
        redirectAttributes.addFlashAttribute(MESSAGE, result.message());

        if (result.type().equals(JpaResultType.NOT_UNIQUE)) {
            var countries = countryService.findAll();
            var states = stateService.findAll();

            model.addAttribute(SAVING_CONDITION, true);
            model.addAttribute(MESSAGE, result.message());
            model.addAttribute(POJO_NAME, rate);
            model.addAttribute("countries", countries);
            model.addAttribute("states", states);
            return CREATE_PAGE;
        } else {
            return "redirect:/" + PAGE;
        }
    }

    @GetMapping("/update_page/{id}")
    public ModelAndView updatePage(@PathVariable int id, ModelAndView model) {

        var rate = service.findById(id);
        if (rate.isEmpty()) {
            var rates = service.findAllPaginated(0);
            model.addObject(TABLE_URL, pageUrl);
            model.addObject(PAGINATION_RESULT, rates);
            model.setViewName(PAGE);
            model.addObject(MESSAGE, "Shipping Rate does not exists!");
            return model;
        }

        var countries = countryService.findAll();
        var states = stateService.findAll();
        model.addObject("countries", countries);
        model.addObject("states", states);
        model.addObject(POJO_NAME, rate.get());
        model.setViewName(CREATE_PAGE);
        return model;
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable int id, RedirectAttributes redirectAttributes) {

        var result = service.delete(id);
        redirectAttributes.addFlashAttribute(DELETED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        redirectAttributes.addFlashAttribute(DELETING_CONDITION, true);
        redirectAttributes.addFlashAttribute(MESSAGE, result.message());
        return "redirect:/" + PAGE;
    }


    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        var data = service.csvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=shipping_rates.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
