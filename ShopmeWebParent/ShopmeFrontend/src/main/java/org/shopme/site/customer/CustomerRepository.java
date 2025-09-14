package org.shopme.site.customer;

import java.util.Optional;

import org.shopme.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{
	Optional<Customer> findByEmail(String email);
	Page<Customer> findAllByFirstnameContainingOrLastnameContainingOrEmailContaining(String text1, String text2, String text3, Pageable page);

	Optional<Customer> findByVerificationCode(String verificationCode);

	@Modifying
	@Query(value = "update Customer c set c.enabled = true,c.verificationCode = null where c.id = :customerId")
	void enable(@Param("customerId") int id);
}
