package com.recyclingprojectbackend.user.model;

import com.recyclingprojectbackend.item.model.Item;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String password;
    @Column(unique = false, nullable = false)
    private String name;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Item> items;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String postalCode;


    public User(Long id, String name, String email, String password, List<Item> items, String city, String postalCode) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.items = items;
        this.city = city;
        this.postalCode = postalCode;
    }

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
