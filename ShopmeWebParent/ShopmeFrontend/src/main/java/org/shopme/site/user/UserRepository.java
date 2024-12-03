package org.shopme.site.user;

import org.shopme.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByEmail(String email);
	Page<User> findAllByFirstnameContainingOrLastnameContainingOrEmailContaining(String text1,String text2,String text3,Pageable page);
}
