package org.shopme.admin.address;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Address;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.pojo.TableUrlPojo;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;

    private final TableUrlPojo pageUrl = new TableUrlPojo("/address/search", "/address",
            "/address/export-csv", "/address/create-page");

    private static final String PAGINATION_RESULT = "result";
    private static final String TABLE_URL = "tableUrl";
    private static final String PAGE = "address";
    private static final String DELETED_CONDITION = "deletedSuccessfully";
    private static final String DELETING_CONDITION = "deletingAddress";
    private static final String MESSAGE = "message";

    @GetMapping("/{customerId}")
    public String page(@PathVariable int customerId, @RequestParam(defaultValue = "0") int page, Model model) {
        PaginationResult result = addressService.findAllByCustomer(customerId, page);
        model.addAttribute(TABLE_URL, pageUrl);
        model.addAttribute(PAGINATION_RESULT, result);
        model.addAttribute(DELETED_CONDITION, false);
        model.addAttribute(DELETING_CONDITION, false);
        model.addAttribute(MESSAGE, "");
        return PAGE;
    }

    @GetMapping("/search")
    public ModelAndView searchUsers(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "0") int page,
            ModelAndView model
    ) {
        var result = addressService.search(text, page);
        model.addObject(PAGINATION_RESULT, result);
        model.addObject(TABLE_URL, pageUrl);
        model.setViewName(PAGE);
        return model;
    }

    @GetMapping("/delete/{id}")
    public String deleteAddress(@PathVariable long id, RedirectAttributes redirectAttributes) {

        JpaResult result = addressService.delete(id);
        redirectAttributes.addFlashAttribute(DELETED_CONDITION, result.type().equals(JpaResultType.SUCCESSFUL));
        redirectAttributes.addFlashAttribute(DELETING_CONDITION, true);
        redirectAttributes.addFlashAttribute(MESSAGE, result.message());

        Address entity = (Address) result.entity();

        return "redirect:/" + PAGE + "/" + entity.getCustomer().getId();
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        var data = addressService.csvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=address.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
