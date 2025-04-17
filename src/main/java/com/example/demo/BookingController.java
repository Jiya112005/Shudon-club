package com.example.demo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;
    
    private final MailService emailService;

    public BookingController(MailService emailService) {
        this.emailService = emailService;
    }
    
    
    @GetMapping("/date/{date}")
    public Map<String, List<Map<String, Object>>> getDetailedBookedSlotsForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Booking> bookings = bookingRepository.findByDate(date);

        return bookings.stream().collect(Collectors.groupingBy(
            Booking::getTimeSlot,
            Collector.of(
                ArrayList::new,
                (list, booking) -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("gameId", booking.getGame().getId()); // Replace with getGameId() if needed
                    list.add(data);
                },
                (left, right) -> { left.addAll(right); return left; }
            )
        ));
    }
    
    @DeleteMapping("/cancel-booking/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }

        Booking booking = bookingOpt.get();
        // ❌ Option 1: Delete it
        bookingRepository.delete(booking);

        // ✅ Option 2: Or just mark it cancelled (recommended if you want to keep history)
        // booking.setCancelled(true);
        // bookingRepository.save(booking);
        
        String adminEmail = "harshilpraja961@gmail.com";
        String subject = "Booking Cancelled";
        String message = "User " + booking.getCustomer().getFirstName() +  booking.getCustomer().getLastName()+	
                         " (Email: " + booking.getCustomer().getEmail() + ") cancelled booking ID " + id;

        emailService.sendEmail(adminEmail, subject, message);
        emailService.sendEmail(
        	    booking.getCustomer().getEmail(),
        	    "Booking Cancelled",
        	    "Your booking for " + booking.getGame().getTitle() + " on " + booking.getDate() + " has been cancelled. Note: This was a non-refundable cancellation."
        	);
        
        return ResponseEntity.ok("Booking cancelled successfully");
    }
    
    
    @GetMapping("/validate/{bookingId}")
    public ResponseEntity<String> validateBooking(@PathVariable Long bookingId) {
        boolean exists = bookingRepository.existsById(bookingId); // Or any check you want
        if (exists) {
            return ResponseEntity.ok("Booking valid");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");
        }
    }
}