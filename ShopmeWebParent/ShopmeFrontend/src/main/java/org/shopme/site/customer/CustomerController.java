package org.shopme.site.customer;

import org.shopme.common.entity.Customer;
import org.shopme.site.country.CountryRepository;
import org.shopme.site.state.StateRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;
	private final StateRepository stateRepository;
	private final CountryRepository countryRepository;

	@GetMapping("/registerPage")
	public ModelAndView registerPage(ModelAndView model) {
		model.addObject("countries", countryRepository.findAll());
		model.addObject("states", stateRepository.findAll());
		model.addObject("customer", new Customer());
		model.setViewName("registerPage");
		return model;
	}

	@PostMapping("/register")
	String register(@ModelAttribute @Valid Customer customer, BindingResult result, @RequestParam MultipartFile picture,
			Model model) {
		if (result.hasErrors()) {
			model.addAttribute("countries", countryRepository.findAll());
			model.addAttribute("states", stateRepository.findAll());
			return "registerPage";
		}

		customerService.save(customer, picture);
		return "redirect:/loginPage";
	}

}
