package org.shopme.admin.address;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.admin.customer.CustomerService;
import org.shopme.common.entity.Address;
import org.shopme.common.entity.Customer;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    @Value("${app.table.page.size:35}")
    private int PAGE_SIZE;

    private final AddressRepository repository;
    private final CustomerService customerService;

    public PaginationResult findAllByCustomer(int customerId, int pageNumber) {

        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (customerOptional.isEmpty()) {
            return new PaginationResult();
        }
        Customer customer = customerOptional.get();
        Page<Address> page = repository.findAllByCustomer(customer, PageRequest.of(pageNumber, PAGE_SIZE));
        return PageUtil.prepareResult(page);
    }

    public JpaResult delete(long addressId) {
        Optional<Address> optional = repository.findById(addressId);
        if (optional.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "Address does not exist.");
        }

        Address address = optional.get();
        repository.delete(address);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted the address.", 0, address);
    }

    public PaginationResult search(String text, int pageNumber) {
        Page<Address> page = repository.findAllByPhoneNumberContainingOrAddressContainingOrPostalCodeContaining(
                text, text, text,
                PageRequest.of(pageNumber, PAGE_SIZE)
        );
        return PageUtil.prepareResult(page);
    }

    public byte[] csvData() {
        var addresses = repository.findAll();
        StringBuilder data = new StringBuilder("Id,Customer,phoneNumber,alternativePhoneNumber,address,country,state,postalCode,selected\n");
        for (var address : addresses) {
            data.append(address.getId())
                    .append(",")
                    .append(address.getCustomer().getFullName())
                    .append(",")
                    .append(address.getPhoneNumber())
                    .append(",")
                    .append(address.getAlternativePhoneNumber())
                    .append(",")
                    .append(address.getAddress())
                    .append(",")
                    .append(address.getCountry().getName())
                    .append(",")
                    .append(address.getState().getName())
                    .append(",")
                    .append(address.getPostalCode())
                    .append(",")
                    .append(address.isSelected())
                    .append("\n");
        }

        return data.toString().getBytes(StandardCharsets.UTF_8);
    }
}
