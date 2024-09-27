package org.shopme.admin.user;

import java.util.List;
import java.util.Optional;

import org.shopme.common.entity.User;
import org.shopme.common.pojo.ChangePasswordPojo;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder encoder;

	public JpaResult save(User user) {
		try {
			
			user.setPassword(encoder.encode(user.getPassword()));
			if (user.getPhoto() == null || user.getPhoto().isEmpty()) {
				user.setPhoto("default_user.svg");
			}

			var existingUser = findByEmail(user.getEmail());
			if (existingUser.isPresent()) {
				return new JpaResult(JpaResultType.NOT_UNIQUE, "User " + user.getEmail() + " already exists!");
			}

			var savedUser = userRepository.save(user);
			return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved user " + savedUser.getEmail());
		} catch (Exception ex) {
			log.error("UserService.save :: Error Occurred {}", ex.getMessage());
			return new JpaResult(JpaResultType.FAILED,
					"Failed to save/update user: " + user.getEmail() + ". Please try again!");
		}
	}
	
	public JpaResult updateUser(User user) {
		
		var existingUserOptional = findByEmail(user.getEmail());
		if (!existingUserOptional.isPresent()) {
			return new JpaResult(JpaResultType.FAILED, "User " + user.getEmail() + " does not exist!");
		}
		
		var existingUser = existingUserOptional.get();
		if(StringUtils.hasText(user.getPhoto())) {
			existingUser.setPhoto(user.getPhoto());
		}
		
		existingUser.setFirstname(user.getFirstname());
		existingUser.setLastname(user.getLastname());
		existingUser.setEnabled(user.isEnabled());
		existingUser.setRoles(user.getRoles());
		
		var savedUser = userRepository.save(existingUser);
		return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully updated user " + savedUser.getEmail());
	}
	
	public JpaResult updatePassword(ChangePasswordPojo pojo,int userId) {
		
		if(!pojo.getNewPassword().equals(pojo.getConfirmPassword())) {
			return new JpaResult(JpaResultType.FAILED, "Confirm password does not match!");
		}
		
		var existingUserOptional = findById(userId);
		if (!existingUserOptional.isPresent()) {
			return new JpaResult(JpaResultType.FAILED, "User does not exist!");
		}
		
		var matches = encoder.matches(pojo.getCurrentPassword(), existingUserOptional.get().getPassword());
		if(!matches) {
			return new JpaResult(JpaResultType.FAILED, "Invalid Password!");
		}
		
		var user = existingUserOptional.get();
		user.setPassword(encoder.encode(pojo.getNewPassword()));
		var savedUser = userRepository.save(user);
		return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully updated user " + savedUser.getEmail());
	}

	public Optional<User> findById(int id) {
		return userRepository.findById(id);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public JpaResult delete(int id) {
		userRepository.deleteById(id);
		return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted user.");
	}
}
