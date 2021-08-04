package com.ideaportal.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ideaportal.models.User;

public interface UserRepository extends JpaRepository<User, Long>
{

}
