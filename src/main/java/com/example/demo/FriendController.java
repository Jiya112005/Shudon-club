package com.example.demo;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
@RestController
@RequestMapping("/auth/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;
    
    @Autowired
    private CustomerRepository customerRepo;

    @GetMapping("/find-friends")
    @ResponseBody
    public List<Customer> findFriends(@SessionAttribute("loggedInUser") Customer loggedInUser) {
        return friendService.findAvailableUsers(loggedInUser.getId());
    }

    @PostMapping("/send-request/{receiverId}")
    @ResponseBody // ✅ Important: tells Spring to return the raw string, not a redirect
    public ResponseEntity<String> sendRequest(@SessionAttribute("loggedInUser") Customer sender,
                                              @PathVariable Long receiverId) {
        Long senderId = sender.getId();
        friendService.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.ok("Friend request sent successfully");
    }

    @GetMapping("/pending")
    public List<FriendRequest> pendingRequests(@RequestParam Long receiverId) {
        return friendService.getPendingRequests(receiverId);
    }

    @PostMapping("/respond/{requestId}/{action}")
    public String respondToRequest(@PathVariable Long requestId,
                                   @PathVariable String action) {
        return friendService.respondToRequest(requestId, action);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<Customer>> getFriends(@RequestParam Long customerId) {
        List<Customer> friends = friendService.getFriends(customerId);
        return ResponseEntity.ok(friends);
    }

    @DeleteMapping("/remove/{friendId}")
    public String removeFriend(@RequestParam Long customerId,
                               @PathVariable Long friendId) {
        return friendService.removeFriend(customerId, friendId);
    }
    
    @GetMapping("/sent-requests")
    public ResponseEntity<List<Customer>> getPendingSentRequests(@RequestParam Long senderId) {
        List<Customer> pendingSentRequests = friendService.getPendingSentRequests(senderId);
        return ResponseEntity.ok(pendingSentRequests);
    }
}
