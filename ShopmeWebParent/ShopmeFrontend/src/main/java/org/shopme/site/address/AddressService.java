package org.shopme.site.address;

import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Address;
import org.shopme.common.entity.Customer;
import org.shopme.common.pojo.AddressPojo;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.site.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AddressService {

    private final AddressRepository repository;

    public AddressService(AddressRepository repository) {
        this.repository = repository;
    }

    public Optional<Address> findById(long id) {
        return repository.findById(id);
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

    @Transactional
    public JpaResult update(AddressPojo address) {

        Optional<Address> oldAddressOptional = repository.findById(address.getId());
        if (oldAddressOptional.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "Address does not exist.");
        }

        Address oldAddress = oldAddressOptional.get();
        if (address.getCountry() != null) {
            oldAddress.setCountry(address.getCountry());
        }

        if (address.getState() != null) {
            oldAddress.setState(address.getState());
        }

        if (StringUtils.hasText(address.getAddress())) {
            oldAddress.setAddress(address.getAddress());
        }

        if (StringUtils.hasText(address.getPhoneNumber())) {
            oldAddress.setPhoneNumber(address.getPhoneNumber());
        }

        if (StringUtils.hasText(address.getAlternativePhoneNumber())) {
            oldAddress.setAlternativePhoneNumber(address.getAlternativePhoneNumber());
        }

        if (StringUtils.hasText(address.getPostalCode())) {
            oldAddress.setPostalCode(address.getPostalCode());
        }

        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved address.");
    }

    public JpaResult save(AddressPojo pojo) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            Customer customer = principal.customer();
            Address address = new Address(customer, pojo.getPhoneNumber(), pojo.getAlternativePhoneNumber(), pojo.getAddress(), pojo.getCountry(),
                    pojo.getState(), pojo.getPostalCode(), pojo.isSelected());
            repository.save(address);

            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved Address.");
        } catch (Exception e) {
            log.error("Saving address, error {} ", e.getMessage());
            return new JpaResult(JpaResultType.FAILED, "Failed to save address.");
        }
    }

    public List<Address> currentCustomerAddress() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Customer customer = principal.customer();
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
