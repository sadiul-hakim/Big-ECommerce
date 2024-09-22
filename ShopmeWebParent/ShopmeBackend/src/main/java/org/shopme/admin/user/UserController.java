package org.shopme.admin.user;

import org.shopme.admin.role.RoleService;
import org.shopme.common.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	private final UserService userService;
	private final RoleService roleService;
	
	@GetMapping
	public ModelAndView users(ModelAndView model) {
		var users = userService.findAll();
		model.addObject("users", users);
		model.setViewName("users");
		
		return model;
	}
	
	@GetMapping("/create")
	public ModelAndView createUsers(ModelAndView model) {
		
		var user = new User();
		var roles = roleService.findAll();
		model.addObject("user", user);
		model.addObject("roles", roles);
		model.setViewName("create_user");
		
		return model;
	}
	
	@PostMapping("/save")
	public ModelAndView save(@ModelAttribute User user,ModelAndView model) {
		
		var savedUser = userService.save(user);
		if(savedUser != null) {
			model.addObject("savedUser", true);
			model.addObject("savedUserName", savedUser.getLastname());
		}
		
		var users = userService.findAll();
		model.addObject("users", users);
		model.setViewName("users");
		return model;
	}
	
	@GetMapping("/delete/{id}")
	public ModelAndView deleteUser(@PathVariable int id,ModelAndView model) {
		var deleted = userService.delete(id);
		if(deleted) {
			model.addObject("userDeleted", deleted);
		}
		
		var users = userService.findAll();
		model.addObject("users", users);
		model.setViewName("users");
		return model;
	}
}
