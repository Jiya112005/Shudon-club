package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByReceiverIdAndStatus(Long receiverId, String status);

	

    List<Invitation> findBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, String status);

    
    
}