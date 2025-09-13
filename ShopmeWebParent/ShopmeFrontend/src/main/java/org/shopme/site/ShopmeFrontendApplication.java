package org.shopme.site;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"org.shopme.common.entity"})
public class ShopmeFrontendApplication implements CommandLineRunner {

//    private final CustomerRepository customerRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final CountryRepository countryRepository;
//    private final StateRepository stateRepository;
//
//    public ShopmeFrontendApplication(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
//                                     CountryRepository countryRepository, StateRepository stateRepository) {
//        this.customerRepository = customerRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.countryRepository = countryRepository;
//        this.stateRepository = stateRepository;
//    }

    public static void main(String[] args) {
        SpringApplication.run(ShopmeFrontendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

//        Country country = countryRepository.findById(1).orElse(null);
//        State state = stateRepository.findById(1).orElse(null);
//        Customer customer = new Customer("hk1234private@gmail.com", passwordEncoder.encode("hakim@123"), "hk",
//                "private", "", true, "013xxxxxxxxxxxx", "kushtia, Bangladesh", country, state, "7010");
//
//        customerRepository.save(customer);
    }
}
