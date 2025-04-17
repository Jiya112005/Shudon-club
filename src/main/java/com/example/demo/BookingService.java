package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
	
    @Autowired
    private BookingRepository bookingRepository;
    
   
    
    @Autowired
    private GameRepository gameRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

public List<Booking> getBookingsByCustomerId(Long customerId) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new RuntimeException("Customer not found"));
    
    return bookingRepository.findByCustomer(customer);
}
    
    public Booking bookSession(Long gameId, Long customerId, LocalDate date, String timeSlot) {
        Games game = gameRepository.findById((long) gameId).orElseThrow(() -> new RuntimeException("Game not found"));
        Customer customer = customerRepository.findById((long) customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Booking booking = new Booking(game, customer, date, timeSlot);
        return bookingRepository.save(booking);
    }
    
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
}
