package com.recyclingprojectbackend.item_like.model;

import com.recyclingprojectbackend.item.model.Item;
import com.recyclingprojectbackend.user.model.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "item_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "item_id"})
})
public class ItemLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public ItemLike(long id, User user, Item item, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.item = item;
        this.createdAt = createdAt;
    }

    public ItemLike() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
