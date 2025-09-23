package org.shopme.site.address;

import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Address;
import org.shopme.common.entity.Customer;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.site.customer.CustomerService;
import org.shopme.site.security.CustomUserDetails;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AddressService {

    private final AddressRepository repository;
    private final CustomerService customerService;

    public AddressService(AddressRepository repository, @Lazy CustomerService customerService) {
        this.repository = repository;
        this.customerService = customerService;
    }

    public void save(Address address) {

        if (address.getAddress() == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            Customer customer = principal.customer();
            address.setCustomer(customer);
        }
        repository.save(address);
    }

    public List<Address> findByCustomer(int customerId) {
        Optional<Customer> customerOptional = customerService.findById(customerId);
        if (customerOptional.isEmpty()) {
            return List.of();
        }

        Customer customer = customerOptional.get();
        return repository.findAllByCustomer(customer);
    }

    @Transactional
    public JpaResult setPrimary(long addressId) {
        Optional<Address> optional = repository.findById(addressId);
        if (optional.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "Address does not exist.");
        }

        Address address = optional.get();
        Optional<Address> primaryAddress = repository.findByCustomerAndSelected(address.getCustomer(), true);
        if (primaryAddress.isPresent()) {
            Address pAddress = primaryAddress.get();
            if (pAddress.getId() == addressId) {
                return new JpaResult(JpaResultType.FAILED, "This is already a primary address.");
            }

            pAddress.setSelected(false);
        }

        address.setSelected(true);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully set the address as primary address.");
    }

    public JpaResult delete(long addressId) {
        Optional<Address> optional = repository.findById(addressId);
        if (optional.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "Address does not exist.");
        }

        Address address = optional.get();
        if (address.isSelected()) {
            return new JpaResult(JpaResultType.FAILED, "The address is the primary address.");
        }

        repository.delete(address);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted the address.");
    }
}
