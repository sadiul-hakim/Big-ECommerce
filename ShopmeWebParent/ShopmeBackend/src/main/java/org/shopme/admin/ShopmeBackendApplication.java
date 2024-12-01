package org.shopme.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"org.shopme.common.entity"})
@RequiredArgsConstructor
public class ShopmeBackendApplication implements CommandLineRunner {
    //	private final RoleRepository roleRepository;
//	private final UserRepository userRepository;
//  private final PasswordEncoder password;
//    private final CategoryRepository categoryRepository;
//    private final ProductService productService;

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

//        categoryRepository.save(new Category(0, "Main", "main", null, new HashSet<>(), true));

//        var product = new Product();
//        product.setName("HP EliteBook 840 G3");
//        product.setAlias("hp_laptop");
//        product.setCategory("Laptops");
//        product.setBrand("HP");
//        product.setCost(64000);
//        product.setPrice(75000);
//        product.setDiscountPrice(72000);
//        product.setEnabled(true);
//        product.setCreatedTime(LocalDateTime.now());
//        product.setUpdatedTime(LocalDateTime.now());
//        product.setShortDescription("This is a laptop.");
//        product.setFullDescription("This is my first laptop.");
//        product.setInStock(true);
//        product.setWeight(2);
//        product.setWidth(14);
//        product.setHeight(4);
//
//        productService.save(product);
    }
}
