package org.shopme.admin.user;

import java.util.Optional;

import org.shopme.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByEmail(String email);
	Page<User> findAllByFirstnameContainingOrLastnameContainingOrEmailContaining(String text1,String text2,String text3,Pageable page);
}
