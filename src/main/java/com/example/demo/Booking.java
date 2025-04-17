package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="booking")
public class Booking {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name = "booking_id")
   // private int bookingId;

    private Long id;
	
	
	private int amount;
    private int players;

    private String paymentId;

    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
   

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "order_id")
    private String orderId;
    
    
    public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getPlayers() {
		return players;
	}

	public void setPlayers(int players) {
		this.players = players;
	}
	
	@ManyToOne
	@JoinColumn(name = "invitation_id") 
	private Invitation invitation;

	
	public Invitation getInvitation() {
	    return invitation;
	}

	public void setInvitation(Invitation invitation) {
	    this.invitation = invitation;
	}


	@ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Games game;

    @ManyToOne
    @JoinColumn(name = "c_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private LocalDate date;

    @Column( name="time_slot",nullable = false)
    private String timeSlot;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Games getGame() {
		return game;
	}

	public void setGame(Games game) {
		this.game = game;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	 public String getTimeSlot() {
	        return timeSlot;
	    }


	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

	

    public Booking() {
		super();
	}

	public Booking(Games game, Customer customer, LocalDate date, String timeSlot) {
        this.game = game;
        this.customer = customer;
        this.date = date;
        this.timeSlot = timeSlot;
    }


    // Getters and Setters
}

