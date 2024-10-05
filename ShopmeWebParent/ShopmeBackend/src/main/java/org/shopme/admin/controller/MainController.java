package org.shopme.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
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
}
