package com.tweetapp.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tweetapp.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String username);

}
