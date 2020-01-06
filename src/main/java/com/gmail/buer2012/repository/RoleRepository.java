package com.gmail.buer2012.repository;

import com.gmail.buer2012.entity.Role;
import com.gmail.buer2012.entity.RoleName;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByName(RoleName roleName);
}
