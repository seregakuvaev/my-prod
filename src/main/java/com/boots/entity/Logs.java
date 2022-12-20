package com.boots.entity;

import java.time.LocalTime;
import javax.persistence.*;

@Entity
@Table(name = "user_changes_log")
public class Logs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalTime time;
    private String instigator; // Зачинщик изменения
    private String change;   // как измененил
    private String username_changed; // кого изменил
    private String username; // поля с дефольным значением не изменены
    private String password;
    private String firstname;
    private String lastname;
    private int age;
    private String roles;

    public Logs() {}

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstigator() {
        return instigator;
    }

    public void setInstigator(String instigator) {
        this.instigator = instigator;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getUsername_changed() {
        return username_changed;
    }

    public void setUsername_changed(String username_changed) {
        this.username_changed = username_changed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
