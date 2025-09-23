package org.shopme.site.address;

import org.shopme.common.entity.Address;
import org.shopme.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByCustomer(Customer customer);

    Optional<Address> findByCustomerAndSelected(Customer customer, boolean selected);
}
