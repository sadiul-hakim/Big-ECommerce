package org.shopme.site.address;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Address;
import org.shopme.common.pojo.AddressPojo;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.site.country.CountryRepository;
import org.shopme.site.state.StateRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private static final String SAVING_CONDITION = "savingAddress";
    private static final String MESSAGE = "message";
    private static final String SAVED_CONDITION = "savedSuccessfully";
    private static final String PAGE_URL = "address";
    private static final String PAGE = "address_page";
    private static final String POJO_NAME = "pojo";

    private final AddressService addressService;
    private final StateRepository stateRepository;
    private final CountryRepository countryRepository;

    @GetMapping
    public String addressPage(Model model) {

        if (model.getAttribute(SAVING_CONDITION) == null) {
            model.addAttribute(SAVING_CONDITION, false);
        }

        if (model.getAttribute(SAVED_CONDITION) == null) {
            model.addAttribute(SAVED_CONDITION, false);
        }

        if (model.getAttribute(MESSAGE) == null) {
            model.addAttribute(MESSAGE, "");
        }

        List<Address> addresses = addressService.currentCustomerAddress();
        model.addAttribute("addresses", addresses);
        return PAGE;
    }

    @PostMapping("/save")
    public String saveAddress(@ModelAttribute @Valid AddressPojo addressPojo, BindingResult result,
                              RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("countries", countryRepository.findAll());
            model.addAttribute("states", stateRepository.findAll());
            return "create_address_page";
        }

        JpaResult jpaResult = addressPojo.getId() == 0 ? addressService.save(addressPojo) :
                addressService.update(addressPojo);

        redirectAttributes.addFlashAttribute(SAVING_CONDITION, true);
        redirectAttributes.addFlashAttribute(SAVED_CONDITION, jpaResult.type().equals(JpaResultType.SUCCESSFUL));
        redirectAttributes.addFlashAttribute(MESSAGE, jpaResult.message());

        return "redirect:/" + PAGE_URL;
    }

    @GetMapping("/create_page")
    public String createAddressPage(Model model) {

        model.addAttribute("countries", countryRepository.findAll());
        model.addAttribute("states", stateRepository.findAll());
        model.addAttribute(POJO_NAME, new AddressPojo());

        return "create_address_page";
    }

    @GetMapping("/update_page/{id}")
    public String updateAddressPage(@PathVariable long id, Model model) {

        Optional<Address> optional = addressService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/" + PAGE_URL;
        }

        model.addAttribute("countries", countryRepository.findAll());
        model.addAttribute("states", stateRepository.findAll());
        model.addAttribute(POJO_NAME, optional.get());

        return "create_address_page";
    }

    @GetMapping("/set-primary/{id}")
    public String setPrimary(@PathVariable long id, RedirectAttributes redirectAttributes) {

        JpaResult jpaResult = addressService.setPrimary(id);

        redirectAttributes.addFlashAttribute(SAVING_CONDITION, true);
        redirectAttributes.addFlashAttribute(SAVED_CONDITION, jpaResult.type().equals(JpaResultType.SUCCESSFUL));
        redirectAttributes.addFlashAttribute(MESSAGE, jpaResult.message());

        return "redirect:/" + PAGE_URL;
    }
}
