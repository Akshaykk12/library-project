package com.capgemini.library_project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capgemini.library_project.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
