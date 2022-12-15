package com.boots.repository;

import com.boots.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query("select distinct c from User c left join fetch c.roles")
    List<User> findAllUsers();

}
