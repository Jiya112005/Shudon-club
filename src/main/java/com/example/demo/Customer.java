package com.example.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "C_id") 
    private Long id;

    @Column(name = "c_fname")
    private String firstName;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "friends",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<Customer> friends = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "friend_requests",
        joinColumns = @JoinColumn(name = "sender_id"),
        inverseJoinColumns = @JoinColumn(name = "receiver_id"))
    private List<Customer> sentRequests;

    @JsonIgnore
    @ManyToMany(mappedBy = "sentRequests")
    private List<Customer> receivedRequests;
  
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id != null && id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


	
	


	public Set<Customer> getFriends() {
		return friends;
	}

	public void setFriends(Set<Customer> friends) {
		this.friends = friends;
	}

	public List<Customer> getSentRequests() {
		return sentRequests;
	}

	public void setSentRequests(List<Customer> sentRequests) {
		this.sentRequests = sentRequests;
	}

	public List<Customer> getReceivedRequests() {
		return receivedRequests;
	}

	public void setReceivedRequests(List<Customer> receivedRequests) {
		this.receivedRequests = receivedRequests;
	}

	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Customer(String firstName, String lastName, String email, String password, int age) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.age = age;
	}
	@Column(name = "c_lname")
    private String lastName;

    private String email;
    private String password;
    private int age;
    
   
    
    
    
    
    @Column(name = "profile_picture") // 🔥 Make sure it maps correctly!
    private String profilePicture;
    
    
  
    public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	@Override
    public String toString() {
        return "Customer [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +
                ", email=" + email + ", password=" + password + ", age=" + age + "]";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
