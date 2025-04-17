package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Invitation {
    @Id @GeneratedValue
    private Long id;

 
   
    private String gameName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate sessionDate;
 // or LocalDate
    private String sessionTime; // or LocalTime

    private String status; 

	
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Customer sender;

    public Customer getSender() {
		return sender;
	}


    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Customer receiver;
	




	public void setSender(Customer sender) {
		this.sender = sender;
	}
	public Customer getReceiver() {
		return receiver;
	}


	public void setReceiver(Customer receiver) {
		this.receiver = receiver;
	}

	public Invitation() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	@Column(name = "player_count")
	private int playerCount;

	public int getPlayerCount() {
		return playerCount;
	}
	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}
	public Invitation(Long id, String gameName, LocalDate sessionDate,
			String sessionTime, String status) {
		super();
		this.id = id;
		
		
		this.gameName = gameName;
		this.sessionDate = sessionDate;
		this.sessionTime = sessionTime;
		this.status = status;
	}

	public LocalDate getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(LocalDate sessionDate) {
		this.sessionDate = sessionDate;
	}

	public String getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(String sessionTime) {
		this.sessionTime = sessionTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
}
