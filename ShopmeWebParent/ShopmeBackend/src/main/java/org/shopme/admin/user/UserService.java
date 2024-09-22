package org.shopme.admin.user;

import java.util.List;

import org.shopme.common.entity.User;
import org.shopme.common.exception.JpaOperationFailedException;
import org.shopme.common.exception.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder encoder;
	
	public User save(User user) {
		try {
			user.setPassword(encoder.encode(user.getPassword()));
			if(user.getPhoto() == null || user.getPhoto().isEmpty()) {
				user.setPhoto("default_user.svg");
			}
			
			return userRepository.save(user);
		}catch(Exception ex) {
			throw new JpaOperationFailedException("UserService.save() :: "+ex.getMessage());
		}
	}
	
	public User findById(int id) {
		return userRepository.findById(id)
		.orElseThrow(()-> new NotFoundException("Could not find user with id : "+id));
	}
	
	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
		.orElseThrow(()-> new NotFoundException("Could not find user with email : "+email));
	}
	
	public List<User> findAll(){
		return userRepository.findAll();
	}
	
	public boolean delete(int id) {
		userRepository.deleteById(id);
		return true;
	}
}
