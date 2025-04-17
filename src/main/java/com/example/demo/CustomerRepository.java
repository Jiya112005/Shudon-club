package com.example.demo;



import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.Param;




@org.springframework.stereotype.Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	
	
	
	Optional<Customer> findByEmail(String email);
	

	 @Query("SELECT c FROM Customer c WHERE c.id <> :customerId")
	    List<Customer> findAllExceptCurrentUser(@Param("customerId") Long customerId);
	 
	 List<Customer> findAllByIdNot(Long id);


	 @Query(value = "SELECT * FROM customer WHERE c_id IN (SELECT friend_id FROM friends WHERE customer_id = ?1)", nativeQuery = true)
	 List<Customer> findFriendsByCustomerId(Long customerId);

	
 Optional<Customer> findByFirstName(String firstName);


	//List<Customer> findFriendsByCustomerId(Long customerId);
	 

	
}
