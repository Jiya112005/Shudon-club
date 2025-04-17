package com.example.demo;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/invitations")
public class InvitationController {

    @Autowired
    private InvitationRepository invitationRepository;
    
    
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CustomerRepository customerRepository;
    
    
    
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    	@Autowired
    	private InvitationService invitationService;
    	
    	@Autowired
    	private MailService sendEmail;
    	
    // POST: Send an invitation
    @PostMapping("/invite")
    public ResponseEntity<String> inviteFriend(@RequestBody Invitation invitation) {
    	System.out.println(invitation.getGameName());
    	System.out.println(invitation.getSessionDate());
    	System.out.println(invitation.getSender());
    	   // Get full receiver object from DB using ID
        Long receiverId = invitation.getReceiver().getId();
        Customer fullReceiver = customerRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Set full receiver into invitation
        invitation.setReceiver(fullReceiver);

        // Now you can get email properly
        System.out.println("Receiver Email: " + fullReceiver.getEmail());
        // Fetch full sender too (to get name)
        Long senderId = invitation.getSender().getId();
        Customer fullSender = customerRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        invitation.setSender(fullSender);

        // Save invitation
        invitation.setStatus("PENDING");
        invitationRepository.save(invitation);

        // Prepare email
        String to = fullReceiver.getEmail();
        String subject = "🎲 Game Session Invite from " + fullSender.getFirstName()+fullSender.getLastName();
        String body = "Hello " + fullReceiver.getFirstName() +fullReceiver.getLastName()+ ",\n\n" +
        
                      fullSender.getFirstName() + " has invited you to a game session!\n\n" +
                      "🕹 Game: " + invitation.getGameName() + "\n" +
                      "📅 Date: " + invitation.getSessionDate() + "\n" +
                      "⏰ Time: " + invitation.getSessionTime() + "\n\n" +
                      "👉 Click below to view the invitation and join:\n" +
                      "🔗 https://742c-2409-40c1-1015-36ad-51a1-225c-5033-952a.ngrok-free.app/auth/home\n\n" +
                      "Get ready to play and have fun!\n\n" +
                      "Regards,\nShudon Board Game Club";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Invitation saved, but failed to send email.");
        }
    	
        invitation.setStatus("PENDING");
        invitationRepository.save(invitation);
        return ResponseEntity.ok("Invitation sent successfully.");
    }

   
    @PostMapping("/respond/{invitationId}")
    public ResponseEntity<String> handleInviteAcceptanceWithPayment(
            @PathVariable Long invitationId,
            @RequestBody Map<String, String> paymentInfo
          ) {

        Optional<Invitation> optional = invitationRepository.findById(invitationId);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Invitation invite = optional.get();
        invite.setStatus("ACCEPTED");
        invite.setPlayerCount(invite.getPlayerCount() + 1);
        invitationRepository.save(invite);

        
        Customer sender = invite.getSender(); 
        // 2. Get receiver (the invited user)
        Customer receiver = customerRepository.findById(invite.getReceiver().getId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        Games game = gameRepository.findByTitle(invite.getGameName())
                .orElseThrow(() -> new RuntimeException("Game not found"));
        System.out.println(receiver.getEmail());
        System.out.println(game);
        System.out.println(invite.getSessionDate());
        System.out.println(invite.getSessionTime());
        System.out.println(sender.getEmail());
        Booking booking = new Booking();
        booking.setCustomer(receiver);
        booking.setGame(game);
        booking.setDate(invite.getSessionDate());
        booking.setTimeSlot(invite.getSessionTime());
        booking.setAmount(299);
        booking.setPlayers(2);
        booking.setPaymentId(paymentInfo.get("paymentId"));
        booking.setOrderId(paymentInfo.get("orderId"));
        booking.setInvitation(invite); // 🔥 Link the invitation
     // 🔔 To Sender: Notification that the friend accepted the invite
        sendEmail.sendEmail(
            sender.getEmail(),
            "🎉 Your Friend Accepted the Invite!",
            "Hi " + sender.getFirstName() + ",\n\n" +
            "Great news! 🎊 Your friend " + receiver.getFirstName() + " has accepted your invitation to play **" +
            game.getTitle() + "**.\n\n" +
            "📅 Date: " + invite.getSessionDate() + "\n" +
            "⏰ Time: " + invite.getSessionTime() + "\n\n" +
            "We’ve confirmed your booking for 2 players. Get ready for an exciting session! 🎮\n\n" +
            "See you at the club!\n" +
            "– Team Shudon"
        );

        // ✅ To Receiver: Booking confirmation
        sendEmail.sendEmail(
            receiver.getEmail(),
            "✅ Booking Confirmed: " + game.getTitle(),
            "Hi " + receiver.getFirstName() + ",\n\n" +
            "You're all set! 🙌 Your booking for **" + game.getTitle() + "** has been confirmed.\n\n" +
            "📅 Date: " + invite.getSessionDate() + "\n" +
            "⏰ Time: " + invite.getSessionTime() + "\n" +
            "👥 Players: 2 (You and " + sender.getFirstName() + ")\n\n" +
            "Enjoy your game and have a blast! 🎮\n\n" +
            "Cheers,\nTeam Shudon"
        );
        bookingRepository.save(booking);

        return ResponseEntity.ok("Invitation accepted and booking created.");
    }

 
  
    
    @GetMapping("/invitations/pending/{userId}")
    public ResponseEntity<List<Map<String, String>>> getPendingInvites(@PathVariable Long userId) {
        List<Invitation> invitations = invitationService.getPendingInvitationsForUser(userId);

        List<Map<String, String>> response = invitations.stream().map(invite -> {
            Map<String, String> map = new HashMap<>();
            map.put("inviteId", String.valueOf(invite.getId()));
            map.put("senderFirstName", invite.getSender().getFirstName());
            map.put("gameName", invite.getGameName());
            map.put("sessionDate", invite.getSessionDate() != null ? invite.getSessionDate().toString() : "N/A");
            map.put("sessionTime", invite.getSessionTime() != null ? invite.getSessionTime() : "N/A");
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    
    
    @GetMapping("/check-invitation-status")
    public ResponseEntity<Integer> checkInvitationStatus(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {

        List<Invitation> invitations = invitationRepository.findBySenderIdAndReceiverIdAndStatus(senderId, receiverId, "ACCEPTED");

        if (!invitations.isEmpty()) {
            int totalCount = invitations.stream()
                .mapToInt(Invitation::getPlayerCount)
                .max() // or sum(), depending on your logic
                .orElse(1);

            return ResponseEntity.ok(totalCount);
        } else {
            return ResponseEntity.ok(-1); // no accepted request found
        }
    }

}