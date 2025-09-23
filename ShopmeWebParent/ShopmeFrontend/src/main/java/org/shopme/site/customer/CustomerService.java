package org.shopme.site.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Address;
import org.shopme.common.entity.Customer;
import org.shopme.common.entity.MailToken;
import org.shopme.common.enumeration.MailTokenType;
import org.shopme.common.pojo.ChangePasswordPojo;
import org.shopme.common.pojo.CustomerRegistrationPojo;
import org.shopme.common.util.FileUtil;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.VerificationCodeGenerator;
import org.shopme.site.address.AddressService;
import org.shopme.site.mailToken.MailTokenService;
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
    private final MailTokenService mailTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AddressService addressService;

    @Transactional
    public JpaResult register(CustomerRegistrationPojo pojo, MultipartFile file) {
        try {

            // Check if customer exists
            var existingUser = findByEmail(pojo.getEmail());
            if (existingUser.isPresent()) {
                return new JpaResult(JpaResultType.NOT_UNIQUE, "Customer " + pojo.getEmail() + " already exists!");
            }

            Customer customer = new Customer(pojo.getEmail(), "", pojo.getFirstname(), pojo.getLastname(),
                    false);

            // Encode the password
            customer.setPassword(encoder.encode(pojo.getPassword()));

            handleFile(file, customer);
            var savedCustomer = repository.save(customer);

            Address primaryAddress = new Address(savedCustomer, pojo.getPhoneNumber(), pojo.getAddress(),
                    pojo.getCountry(), pojo.getState(), pojo.getPostalCode(), true);
            addressService.save(primaryAddress);

            String token = VerificationCodeGenerator.generateCode(64);
            mailTokenService.save(savedCustomer.getId(), token, MailTokenType.EMAIL_VERIFICATION, 1440);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved customer " + savedCustomer.getEmail(),
                    savedCustomer.getId(), savedCustomer);
        } catch (Exception ex) {
            log.error("CustomerService.save :: Error Occurred {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED,
                    "Failed to save/update customer: " + pojo.getEmail() + ". Please try again!");
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

    public Optional<Customer> findById(int id) {
        return repository.findById(id);
    }

    public Optional<Customer> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional
    public boolean verify(String token) {
        JpaResult result = mailTokenService.verify(token, true);
        if (!result.type().equals(JpaResultType.SUCCESSFUL)) {
            return false;
        }

        MailToken mailToken = mailTokenService.findByToken(token);
        Optional<Customer> customerOptional = findById(mailToken.getCustomerId());
        if (customerOptional.isEmpty()) {
            return false;
        }

        Customer customer = customerOptional.get();
        customer.setEnabled(true);

        return true;
    }

    @Transactional
    public JpaResult newPassword(String token, int customerId, ChangePasswordPojo pojo) {

        Optional<Customer> customerOptional = findById(customerId);
        if (customerOptional.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "Customer does not exists!");
        }

        Customer customer = customerOptional.get();
        MailToken mailToken = mailTokenService.findByToken(token);
        if (mailToken == null) {
            return new JpaResult(JpaResultType.FAILED, "You are not allowed to reset password!");
        }

        Optional<Customer> tokenCustomer = findById(mailToken.getCustomerId());
        if (tokenCustomer.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "You are not allowed to reset password!");
        }

        Customer cs = tokenCustomer.get();
        if (!cs.getEmail().equals(customer.getEmail())) {
            return new JpaResult(JpaResultType.FAILED, "You are not allowed to reset password!");
        }

        if (!pojo.getNewPassword().equals(pojo.getConfirmPassword())) {
            return new JpaResult(JpaResultType.FAILED, "Confirm password does not match!");
        }

        mailToken.setUsed(true);
        customer.setPassword(passwordEncoder.encode(pojo.getNewPassword()));
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully reset your password!");
    }
}
