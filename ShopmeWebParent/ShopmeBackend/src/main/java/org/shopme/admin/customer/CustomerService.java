package org.shopme.admin.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Customer;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.FileUtil;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    @Value("${app.table.page.size:35}")
    private int PAGE_SIZE;
    private static final String FILE_PATH = "/customer/";
    private static final String DEFAULT_PHOTO_NAME = "default.svg";

    private final CustomerRepository repository;

    public PaginationResult findAllPaginated(int pageNumber) {
        var page = repository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
        return PageUtil.prepareResult(page);
    }

    public PaginationResult searchCustomer(String text, int pageNum) {
        var page = repository.findAllByFirstnameContainingOrLastnameContainingOrEmailContaining(text, text, text,
                PageRequest.of(pageNum, PAGE_SIZE));
        return PageUtil.prepareResult(page);
    }

    public JpaResult updateCustomer(Customer customer, MultipartFile file) {

        try {
            var existingUserOptional = findByEmail(customer.getEmail());
            if (existingUserOptional.isEmpty()) {
                return new JpaResult(JpaResultType.FAILED, "Customer " + customer.getEmail() + " does not exist!");
            }

            var existingCustomer = existingUserOptional.get();
            handleFile(file, customer);

            // Only update non-empty fields
            if (StringUtils.hasText(customer.getFirstname())) {
                existingCustomer.setFirstname(customer.getFirstname());
            }
            if (StringUtils.hasText(customer.getLastname())) {
                existingCustomer.setLastname(customer.getLastname());
            }

            // For primitives/booleans, check separately
            existingCustomer.setEnabled(customer.isEnabled());

            var savedUser = repository.save(existingCustomer);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully updated customer " + savedUser.getEmail());
        } catch (Exception ex) {
            log.error("CustomerService.update :: Error Occurred {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED,
                    "Failed to save/update customer: " + customer.getEmail() + ". Please try again!");
        }
    }

    public Optional<Customer> findByEmail(String email) {
        return repository.findByEmail(email);
    }


    public Optional<Customer> findById(long id) {
        return repository.findById(id);
    }

    public JpaResult delete(long id) {
        Optional<Customer> user = findById(id);
        if (user.isEmpty()) {
            return new JpaResult(JpaResultType.NOT_FOUND, "Customer does not exists!");
        }
        FileUtil.deleteFile(FILE_PATH, user.get().getPhoto());
        repository.delete(user.get());
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted customer.");
    }

    public byte[] csvData() {

        var customers = repository.findAll();
        StringBuilder data = new StringBuilder("Id,First Name,Last Name,Email,Photo,Enables,Joined,Phone Number,Country,State,Address,postalCode\n");
        for (var user : customers) {
            data.append(user.getId())
                    .append(",")
                    .append(user.getFirstname())
                    .append(",")
                    .append(user.getLastname())
                    .append(",")
                    .append(user.getEmail())
                    .append(",")
                    .append(user.getPhoto())
                    .append(",")
                    .append(user.isEnabled())
                    .append(",")
                    .append(user.getJoined())
                    .append(",")
                    .append("\n");
        }

        return data.toString().getBytes(StandardCharsets.UTF_8);
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
}
