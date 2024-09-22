package org.shopme.admin.role;

import java.util.List;

import org.shopme.common.entity.Role;
import org.shopme.common.exception.NotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
	private final RoleRepository repository;
	
	public Role findByName(String name) {
		return repository.findByName(name)
		.orElseThrow(() -> new NotFoundException("User not found with name: "+name));
	}
	
	public Role findById(int id) {
		return repository.findById(id)
		.orElseThrow(() -> new NotFoundException("User not found with id: "+id));
	}
	
	public List<Role> findAll(){
		return repository.findAll();
	}
}
