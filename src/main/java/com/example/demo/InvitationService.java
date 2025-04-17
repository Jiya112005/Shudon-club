package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class InvitationService {
	
	
	@Autowired
	private InvitationRepository invitationRepository;
	@Transactional
	public void acceptInvitation(Long invitationId) {
	    Invitation invitation = invitationRepository.findById(invitationId)
	        .orElseThrow(() -> new RuntimeException("Invitation not found"));

	    if (!"PENDING".equals(invitation.getStatus())) {
	        throw new IllegalStateException("Invitation already handled.");
	    }

	    invitation.setStatus("ACCEPTED");

	    // 👇 Increase player count
	    invitation.setPlayerCount(invitation.getPlayerCount() + 1);
	    System.out.println("Saving invitation with updated player count: " + invitation.getPlayerCount());

	    invitationRepository.save(invitation);
	}
	public List<Invitation> getPendingInvitationsForUser(Long userId) {
	    return invitationRepository.findByReceiverIdAndStatus(userId, "PENDING");
	}



}
