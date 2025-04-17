package com.example.demo;
import jakarta.persistence.*;

@Entity
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private Customer friend;

    // Constructors, Getters, and Setters
    public Friend() {}

    public Friend(Customer customer, Customer friend) {
        this.customer = customer;
        this.friend = friend;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Customer getFriend() {
		return friend;
	}

	public void setFriend(Customer friend) {
		this.friend = friend;
	}

   
}
