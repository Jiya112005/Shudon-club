package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.FriendRequest.Status;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
  
    List<FriendRequest> findBySenderIdAndStatus(Long senderId, String status);
   
  

	boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
	boolean existsBySenderAndReceiverAndStatus(Customer sender, Customer receiver, Status pending);
	


    // You can also add this for senders:
    List<FriendRequest> findBySenderIdAndStatus(Long senderId, FriendRequest.Status status);

	
	  List<FriendRequest> findByReceiverIdAndStatus(Long receiverId, FriendRequest.Status status);


	  List<FriendRequest> findByReceiverIdAndStatus(Long receiverId, String status);
	    Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, String status);
	    List<FriendRequest> findBySenderIdOrReceiverIdAndStatus(Long id1, Long id2, String status);


	    @Query("SELECT fr.receiver FROM  FriendRequest fr WHERE fr.sender.id = :senderId AND fr.status = 'PENDING'")
	    List<Customer> findPendingSentRequests(@Param("senderId") Long senderId);

	List<FriendRequest> findBySenderIdOrReceiverIdAndStatus(Long customerId, Long customerId2, Status accepted);

	

    Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, FriendRequest.Status status);





}