package org.shopme.admin.security;

import java.util.Collection;

import org.shopme.common.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record CustomUserDetails(
			User user
		) implements UserDetails{

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		var roles = user.getRoles();
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}
}
