package org.shopme.admin;

import java.util.HashSet;
import java.util.Set;

import org.shopme.admin.role.RoleRepository;
import org.shopme.admin.user.UserRepository;
import org.shopme.common.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EntityScan({"org.shopme.common.entity"})
@RequiredArgsConstructor
public class ShopmeBackendApplication implements CommandLineRunner{
//	private final RoleRepository roleRepository;
//	private final UserRepository userRepository;
//	private final PasswordEncoder password;

	public static void main(String[] args) {
		SpringApplication.run(ShopmeBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		var admin = new Role(0,"ROLE_ADMIN","Manage everything");
//		var sales = new Role(0,"ROLE_SALESPERSON","Manage Product price,customers,shipping,orders and sales report");
//		var editor = new Role(0,"ROLE_EDITOR","Manage categories,brands,products,articles and menus");
//		var shipper = new Role(0,"ROLE_SHIPPER","View products,view orders and update order status");
//		var assistant = new Role(0,"ROLE_ASSISTANT","Manage questions and reviews");
//		roleRepository.save(sales);
//		roleRepository.save(editor);
//		roleRepository.save(shipper);
//		roleRepository.save(assistant);
		
//		var role = roleRepository.findById(1).orElse(null);
//		
//		User admin = new User(
//				0,"sadiulhakim@gmail.com",password.encode("hakim@123"),"Sadiul","Hakim",null,true,Set.of(role)
//				);
//		
//		userRepository.save(admin);
	}
}
