package org.shopme.admin.customer;

import org.shopme.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findAllByFirstnameContainingOrLastnameContainingOrEmailContaining(String text1, String text2, String text3, Pageable page);

    Optional<Customer> findByEmail(String email);
}
