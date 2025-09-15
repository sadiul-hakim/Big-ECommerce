package org.shopme.site.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Customer;
import org.shopme.common.pojo.ChangePasswordPojo;
import org.shopme.common.util.FileUtil;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.VerificationCodeGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final String FILE_PATH = "/customer/";
    private static final String DEFAULT_PHOTO_NAME = "default.svg";

    private final CustomerRepository repository;
    private final PasswordEncoder encoder;

    public JpaResult save(Customer customer, MultipartFile file) {
        try {

            // Encode the password
            customer.setPassword(encoder.encode(customer.getPassword()));
            customer.setVerificationCode(VerificationCodeGenerator.generateCode(64));

            // Check if customer exists
            var existingUser = findByEmail(customer.getEmail());
            if (existingUser.isPresent()) {
                return new JpaResult(JpaResultType.NOT_UNIQUE, "Customer " + customer.getEmail() + " already exists!");
            }

            handleFile(file, customer);

            var savedUser = repository.save(customer);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved customer " + savedUser.getEmail(), savedUser.getId(), savedUser);
        } catch (Exception ex) {
            log.error("CustomerService.save :: Error Occurred {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED,
                    "Failed to save/update customer: " + customer.getEmail() + ". Please try again!");
        }
    }

    public JpaResult updatePassword(ChangePasswordPojo pojo, Customer customer) {

        if (!pojo.getNewPassword().equals(pojo.getConfirmPassword())) {
            return new JpaResult(JpaResultType.FAILED, "Confirm password does not match!");
        }

        var matches = encoder.matches(pojo.getCurrentPassword(), customer.getPassword());
        if (!matches) {
            return new JpaResult(JpaResultType.FAILED, "Invalid Password!");
        }

        customer.setPassword(encoder.encode(pojo.getNewPassword()));
        var savedCustomer = repository.save(customer);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully updated customer " + savedCustomer.getEmail());
    }

    public JpaResult updateCustomer(Customer customer, MultipartFile file) {

        try {
            var existingUserOptional = findByEmail(customer.getEmail());
            if (existingUserOptional.isEmpty()) {
                return new JpaResult(JpaResultType.FAILED, "Customer " + customer.getEmail() + " does not exist!");
            }

            var existingCustomer = existingUserOptional.get();
            handleFile(file, existingCustomer);

            // Only update non-empty fields
            if (StringUtils.hasText(customer.getFirstname())) {
                existingCustomer.setFirstname(customer.getFirstname());
            }
            if (StringUtils.hasText(customer.getLastname())) {
                existingCustomer.setLastname(customer.getLastname());
            }
            if (StringUtils.hasText(customer.getAddress())) {
                existingCustomer.setAddress(customer.getAddress());
            }
            if (customer.getCountry() != null) {
                existingCustomer.setCountry(customer.getCountry());
            }
            if (customer.getState() != null) {
                existingCustomer.setState(customer.getState());
            }
            if (StringUtils.hasText(customer.getPhoneNumber())) {
                existingCustomer.setPhoneNumber(customer.getPhoneNumber());
            }
            if (StringUtils.hasText(customer.getPostalCode())) {
                existingCustomer.setPostalCode(customer.getPostalCode());
            }

            var savedUser = repository.save(existingCustomer);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully updated customer " + savedUser.getEmail());
        } catch (Exception ex) {
            log.error("CustomerService.update :: Error Occurred {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED,
                    "Failed to save/update customer: " + customer.getEmail() + ". Please try again!");
        }
    }

    private void handleFile(MultipartFile file, Customer customer) {
        if (file == null || !StringUtils.hasText(file.getOriginalFilename())) {

            if (!StringUtils.hasText(customer.getPhoto())) {
                customer.setPhoto(DEFAULT_PHOTO_NAME);
            }
            return;
        }
        var filePath = FileUtil.uploadFile(file, FILE_PATH);
        if (!StringUtils.hasText(filePath)) {
            log.warn("CustomerService.handleFile :: Could not upload file!");
            if (!StringUtils.hasText(customer.getPhoto())) {
                customer.setPhoto(DEFAULT_PHOTO_NAME);
            }
            return;
        }

        if (customer.getPhoto() != null && !customer.getPhoto().equals(DEFAULT_PHOTO_NAME)) {
            FileUtil.deleteFile(FILE_PATH, customer.getPhoto());
        }
        customer.setPhoto(filePath);
    }

    public JpaResult updatePassword(ChangePasswordPojo pojo, int userId) {

        if (!pojo.getNewPassword().equals(pojo.getConfirmPassword())) {
            return new JpaResult(JpaResultType.FAILED, "Confirm password does not match!");
        }

        var existingCustomerOptional = findById(userId);
        if (existingCustomerOptional.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "Customer does not exist!");
        }

        var matches = encoder.matches(pojo.getCurrentPassword(), existingCustomerOptional.get().getPassword());
        if (!matches) {
            return new JpaResult(JpaResultType.FAILED, "Invalid Password!");
        }

        var user = existingCustomerOptional.get();
        user.setPassword(encoder.encode(pojo.getNewPassword()));
        var savedUser = repository.save(user);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully updated customer " + savedUser.getEmail());
    }

    public Optional<Customer> findById(int id) {
        return repository.findById(id);
    }

    public Optional<Customer> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional
    public boolean verify(String code) {

        try {

            Optional<Customer> customerOpt = repository.findByVerificationCode(code);
            if (customerOpt.isEmpty()) {

                log.warn("No Customer found with code {}", code);
                return false;
            }

            Customer customer = customerOpt.get();
            if (customer.isEnabled()) {
                return false;
            }

            repository.enable(customer.getId());
            return true;
        } catch (Exception ex) {
            log.error("Failed to verify customer with code {}, error {}", code, ex.getMessage());
            return false;
        }
    }
}
