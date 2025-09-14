package org.shopme.admin.customer;

import lombok.RequiredArgsConstructor;
import org.shopme.admin.country.CountryService;
import org.shopme.admin.state.StateService;
import org.shopme.common.entity.Country;
import org.shopme.common.entity.Customer;
import org.shopme.common.entity.State;
import org.shopme.common.pojo.TableUrlPojo;
import org.shopme.common.util.JpaResultType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;
    private final CountryService countryService;
    private final StateService stateService;

    private final TableUrlPojo pageUrl = new TableUrlPojo("/customers/search", "/customers",
            "/customers/export-csv", "/customers/create");

    private static final String PAGINATION_RESULT = "customerResult";
    private static final String TABLE_URL = "tableUrl";
    private static final String PAGE = "customers";
    private static final String POJO_NAME = "customer";
    private static final String CREATE_PAGE = "create_customer";
    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String SAVING_CONDITION = "savingCustomer";
    private static final String DELETED_CONDITION = "deletedSuccessfully";
    private static final String DELETING_CONDITION = "deletingCustomer";
    private static final String MESSAGE = "message";

    @GetMapping
    public ModelAndView customersPage(@RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var result = service.findAllPaginated(page);
        model.addObject(PAGINATION_RESULT, result);
        model.addObject(TABLE_URL, pageUrl);

        model.setViewName(PAGE);

        return model;
    }

    @GetMapping("/search")
    public ModelAndView searchUsers(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "0") int page,
            ModelAndView model
    ) {
        var userResult = service.searchCustomer(text, page);
        model.addObject(PAGINATION_RESULT, userResult);
        model.addObject(TABLE_URL, pageUrl);
        model.setViewName(PAGE);

        return model;
    }

    @PostMapping("/update")
    public String update(
            @ModelAttribute Customer customer,
            @RequestParam MultipartFile file,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        var result = service.updateCustomer(customer, file);

        if (result.type().equals(JpaResultType.NOT_UNIQUE)) {
            model.addAttribute(SAVING_CONDITION, true);
            model.addAttribute(MESSAGE, result.message());
            model.addAttribute(POJO_NAME, customer);
            return CREATE_PAGE;
        } else {
            redirectAttributes.addFlashAttribute(SAVED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
            redirectAttributes.addFlashAttribute(SAVING_CONDITION, true);
            redirectAttributes.addFlashAttribute(MESSAGE, result.message());
            return "redirect:/" + PAGE;
        }
    }

    @GetMapping("/update_page/{id}")
    public String updatePage(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {

        var user = service.findById(id);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute(MESSAGE, "User does not exists!");
            return "redirect:/" + PAGE;
        }

        List<Country> countries = countryService.findAll();
        List<State> states = stateService.findAll();
        model.addAttribute("countries", countries);
        model.addAttribute("states", states);
        model.addAttribute(POJO_NAME, user.get());
        return CREATE_PAGE;
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id, RedirectAttributes model) {
        var result = service.delete(id);
        model.addFlashAttribute(DELETED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        model.addFlashAttribute(DELETING_CONDITION, true);
        model.addFlashAttribute(MESSAGE, result.message());

        return "redirect:/" + PAGE;
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        var data = service.csvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=customers.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
