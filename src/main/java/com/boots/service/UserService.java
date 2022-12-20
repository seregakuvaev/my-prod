package com.boots.service;

import com.boots.entity.Logs;
import com.boots.entity.Role;
import com.boots.entity.User;
import com.boots.repository.LogsRepository;
import com.boots.repository.RoleRepository;
import com.boots.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    LogsRepository logsRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        userRepository.findAllUsers();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public Optional<Role> findRoleById(Long id){
        return roleRepository.findById(id);
    }

    public boolean saveUser(User user, int role) {
        userRepository.findAllUsers();
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            return false;
        }
        if(role == 1){
            user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        } else {
            user.setRoles(Collections.singleton(new Role(2L, "ROLE_ADMIN")));
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public void updateUser(Long id, String firstname, String lastname, int age, String username, String password, Set<Role> role) {
        Optional<User> users = userRepository.findById(id);
        if (users.isPresent()){
            User user = users.get();
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setAge(age);
            user.setUsername(username);
            user.setRoles(role);
            if(password.length() > 0) {
                user.setPassword(bCryptPasswordEncoder.encode(password));
            }
            userRepository.save(user);
        }
    }

    public void addLogs(String instigator, String change, String username_changed, String username,
                   String password, String firstname, String lastname, int age, String roles){
        Logs log = new Logs();
        log.setInstigator(instigator);
        log.setChange(change);
        log.setUsername_changed(username_changed);
        log.setUsername(username);
        log.setPassword(password);
        log.setFirstname(firstname);
        log.setLastname(lastname);
        log.setAge(age);
        log.setRoles(roles);
        log.setTime(LocalTime.now());
        logsRepository.save(log);
    }

    public void addLogs(Logs log){
        logsRepository.save(log);
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }
}
