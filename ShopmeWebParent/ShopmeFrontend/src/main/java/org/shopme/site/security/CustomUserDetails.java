package org.shopme.site.security;

import java.util.Collection;

import org.shopme.common.entity.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record CustomUserDetails(
			Customer customer
		) implements UserDetails{

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

    @Override
	public String getPassword() {
		return customer.getPassword();
	}

	@Override
	public String getUsername() {
		return customer.getEmail();
	}
}
