package com.proxym.poleactualities.Repository;

import com.proxym.poleactualities.Models.Role;
import com.proxym.poleactualities.Models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
