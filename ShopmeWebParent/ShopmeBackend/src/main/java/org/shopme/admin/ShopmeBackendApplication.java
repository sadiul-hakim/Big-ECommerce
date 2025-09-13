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
//    private final RoleRepository roleRepository;
//    private final UserRepository userRepository;
//    private final PasswordEncoder password;
//    private final CategoryRepository categoryRepository;
//    private final ProductService productService;
//    private final CurrencyRepository currencyRepository;
//    private final SettingRepository settingRepository;

    public static void main(String[] args) {
        SpringApplication.run(ShopmeBackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        var admin = new Role(0, "ROLE_ADMIN", "Manage everything");
//        var sales = new Role(0, "ROLE_SALESPERSON", "Manage Product price,customers,shipping,orders and sales report");
//        var editor = new Role(0, "ROLE_EDITOR", "Manage categories,brands,products,articles and menus");
//        var shipper = new Role(0, "ROLE_SHIPPER", "View products,view orders and update order status");
//        var assistant = new Role(0, "ROLE_ASSISTANT", "Manage questions and reviews");
//        roleRepository.save(sales);
//        roleRepository.save(editor);
//        roleRepository.save(shipper);
//        roleRepository.save(assistant);
//        roleRepository.save(admin);

//        Role admin = roleRepository.findById(6).orElse(null);
//
//        User adminUser = new User(
//                "sadiulhakim@gmail.com", password.encode("hakim@123"), "Sadiul",
//                "Hakim", null, true, Set.of(admin)
//        );
////
//        userRepository.save(adminUser);
////
//        categoryRepository.save(new Category(0, "Main", "main", 0, true, List.of()));
////
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
//        product.setWeight(2);
//        product.setWidth(14);
//        product.setHeight(4);
//
//        productService.save(product);
//
//        List<Currency> currencies = List.of(
//                new Currency(0, "United State Dollar", "$", "USD"),
//                new Currency(0, "British Pound", "£", "GPB"),
//                new Currency(0, "Japanese Yen", "¥", "JPY"),
//                new Currency(0, "Euro", "€", "EUR"),
//                new Currency(0, "Russian Ruble", "₽", "RUB"),
//                new Currency(0, "South Korean Won", "₩", "KRW"),
//                new Currency(0, "Chinese Yuan", "¥", "CNY"),
//                new Currency(0, "Chinese Yuan", "¥", "CNY"),
//                new Currency(0, "Brazilian Real", "R$", "BRL"),
//                new Currency(0, "Australian Dollar", "$", "AUD"),
//                new Currency(0, "Canadian Dollar", "$", "CAD"),
//                new Currency(0, "Vietnamese Dong", "₫", "VND"),
//                new Currency(0, "Pakistani rupee", "₨", "PKR"),
//                new Currency(0, "Bangladeshi Taka", "৳", "BDT")
//        );
//
//        currencyRepository.saveAll(currencies);
//
//        List<Setting> settings = List.of(
//                new Setting(GeneralSettingBag.COPYRIGHT, "CopyRight (c) 2025 Shopme Inc.", SettingCategory.GENERAL),
//                new Setting(GeneralSettingBag.CURRENCY_ID, "1", SettingCategory.CURRENCY),
//                new Setting(GeneralSettingBag.CURRENCY_SYMBOL, "$", SettingCategory.CURRENCY),
//                new Setting(GeneralSettingBag.CURRENCY_SYMBOL_POSITION, "before", SettingCategory.CURRENCY),
//                new Setting(GeneralSettingBag.DECIMAL_DIGITS, "2", SettingCategory.CURRENCY),
//                new Setting(GeneralSettingBag.DECIMAL_POINT_TYPE, "POINT", SettingCategory.CURRENCY),
//                new Setting(GeneralSettingBag.SITE_LOGO, "logo.webp", SettingCategory.GENERAL),
//                new Setting(GeneralSettingBag.SITE_NAME, "ShopmeAdmin", SettingCategory.GENERAL),
//                new Setting(GeneralSettingBag.THOUSAND_POINT_TYPE, "COMMA", SettingCategory.CURRENCY)
//        );
////
//        settingRepository.saveAll(settings);
////
//        roleRepository.save(new Role(0, "ROLE_CUSTOMER", "Customer Role"));
    }
}
