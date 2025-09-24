package org.shopme.admin.address;

import org.shopme.common.entity.Address;
import org.shopme.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Page<Address> findAllByCustomer(Customer customer, Pageable pageable);

    Optional<Address> findByCustomerAndSelected(Customer customer, boolean selected);

    Page<Address> findAllByPhoneNumberContainingOrAddressContainingOrPostalCodeContainingOrCityContaining(
            String phoneNumber,
            String address, String postalCode, String city, Pageable pageable
    );
}
