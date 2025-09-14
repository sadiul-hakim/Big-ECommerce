package org.shopme.site.customer;

import java.util.Optional;
import java.util.UUID;

import org.shopme.common.entity.Customer;
import org.shopme.common.pojo.ChangePasswordPojo;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final int USER_LIST_LIMIT = 35;
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

    public JpaResult updateCustomer(Customer customer, MultipartFile file) {

        try {
            var existingUserOptional = findByEmail(customer.getEmail());
            if (existingUserOptional.isEmpty()) {
                return new JpaResult(JpaResultType.FAILED, "Customer " + customer.getEmail() + " does not exist!");
            }

            var existingCustomer = existingUserOptional.get();
            handleFile(file, customer);
            existingCustomer.setFirstname(customer.getFirstname());
            existingCustomer.setLastname(customer.getLastname());
            existingCustomer.setEnabled(customer.isEnabled());
            existingCustomer.setAddress(customer.getAddress());
            existingCustomer.setCountry(customer.getCountry());
            existingCustomer.setState(customer.getState());
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
            existingCustomer.setPostalCode(customer.getPostalCode());
            existingCustomer.setVerificationCode(customer.getVerificationCode());

            var savedUser = repository.save(existingCustomer);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully updated user " + savedUser.getEmail());
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

    public PaginationResult findAllPaginated(int pageNumber) {
        var page = repository.findAll(PageRequest.of(pageNumber, USER_LIST_LIMIT));
        return PageUtil.prepareResult(page);
    }

    public PaginationResult searchUser(String text, int pageNum) {
        var page = repository.findAllByFirstnameContainingOrLastnameContainingOrEmailContaining(text, text, text,
                PageRequest.of(pageNum, USER_LIST_LIMIT));
        return PageUtil.prepareResult(page);
    }

    public JpaResult delete(int id) {
        Optional<Customer> user = findById(id);
        if (user.isEmpty()) {
            return new JpaResult(JpaResultType.NOT_FOUND, "Customer does not exists!");
        }
        FileUtil.deleteFile(FILE_PATH, user.get().getPhoto());
        repository.delete(user.get());
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted customer.");
    }
}
