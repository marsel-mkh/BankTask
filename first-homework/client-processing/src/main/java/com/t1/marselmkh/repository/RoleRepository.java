package com.t1.marselmkh.repository;

import com.t1.marselmkh.entity.Role;
import com.t1.marselmkh.entity.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository  extends JpaRepository<Role,Long> {
    Optional<Role> findByName(RoleEnum roleEnum);
}
