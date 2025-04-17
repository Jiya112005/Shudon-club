package com.example.demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
@Service
public class FriendService {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private FriendRequestRepository friendRequestRepo;

    
    @Autowired
    private FriendRepository friendRepository;
    public List<Customer> findAvailableUsers(Long currentUserId) {
        Customer currentUser = customerRepo.findById(currentUserId).orElse(null);
        if (currentUser == null) return new ArrayList<>();

        List<Customer> allUsers = customerRepo.findAll();

        List<Customer> findFriends = allUsers.stream()
            .filter(u -> !u.getId().equals(currentUser.getId()))
            .filter(u -> !currentUser.getFriends().contains(u))
            .filter(u -> !currentUser.getSentRequests().contains(u))
            .filter(u -> !currentUser.getReceivedRequests().contains(u))
            .collect(Collectors.toList());

        return findFriends; // ✅ FIXED LINE
    }


    public String sendFriendRequest(Long senderId, Long receiverId) {
        if (friendRequestRepo.findBySenderIdAndReceiverIdAndStatus(senderId, receiverId, FriendRequest.Status.PENDING).isPresent()) {
            return "Friend request already sent.";
        }

        FriendRequest request = new FriendRequest();
        request.setSender(customerRepo.findById(senderId).orElseThrow());
        request.setReceiver(customerRepo.findById(receiverId).orElseThrow());
        request.setStatus(FriendRequest.Status.PENDING);
        friendRequestRepo.save(request);
        return "Friend request sent successfully.";
    }

    public List<FriendRequest> getPendingRequests(Long receiverId) {
        return friendRequestRepo.findByReceiverIdAndStatus(receiverId, FriendRequest.Status.PENDING);
    }

    public String respondToRequest(Long requestId, String action) {
        FriendRequest request = friendRequestRepo.findById(requestId).orElseThrow();
        if (action.equalsIgnoreCase("accept")) {
            request.setStatus(FriendRequest.Status.ACCEPTED);
            Customer sender = request.getSender();
            Customer receiver = request.getReceiver();

            sender.getFriends().add(receiver);
            receiver.getFriends().add(sender);

            customerRepo.save(sender);
            customerRepo.save(receiver);
            friendRequestRepo.save(request);

            return "Friend request accepted.";
        } else {
            request.setStatus(FriendRequest.Status.REJECTED);
            friendRequestRepo.save(request);
            return "Friend request rejected.";
        }
    }

    public List<Customer> getFriends(Long customerId) {
        return customerRepo.findFriendsByCustomerId(customerId);
    }


    public String removeFriend(Long customerId, Long friendId) {
        Customer customer = customerRepo.findById(customerId).orElseThrow();
        Customer friend = customerRepo.findById(friendId).orElseThrow();

        customer.getFriends().remove(friend);
        friend.getFriends().remove(customer);

        customerRepo.save(customer);
        customerRepo.save(friend);
        return "Friend removed successfully.";
    }

    public List<Customer> getPendingSentRequests(Long senderId) {
        return friendRequestRepo.findPendingSentRequests(senderId);
    }


    public List<FriendRequest> getSentRequests(Long senderId) {
        return friendRequestRepo.findBySenderIdAndStatus(senderId, "PENDING");
    }
}