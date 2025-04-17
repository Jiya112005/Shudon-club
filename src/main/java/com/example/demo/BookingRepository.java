package com.example.demo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	 @Query("SELECT b.timeSlot FROM Booking b WHERE b.date = :date")
	    List<String> findTimeSlotsByDate(@Param("date") LocalDate date);

	List<Booking> findByCustomer(Customer loggedInUser);
	List<Booking> findByDate(LocalDate date); // ✅ Correct

	Optional<Booking> findByGame(Games game);

}