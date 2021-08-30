package com.softtech.payday.repository;

import com.softtech.payday.model.role.Role;
import com.softtech.payday.model.role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(RoleName name);
}
