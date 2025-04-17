package com.example.demo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM Friend f WHERE (f.customer.id = :customerId AND f.friend.id = :friendId) " +
            "OR (f.customer.id = :friendId AND f.friend.id = :customerId)")
    Optional<Friend> findFriendship(@Param("customerId") Long customerId, @Param("friendId") Long friendId);
    
 
    
    @Query("SELECT f FROM Friend f WHERE (f.customer.id = :customerId AND f.friend.id = :friendId) OR (f.customer.id = :friendId AND f.friend.id = :customerId)")
    Optional<Friend> findByCustomerAndFriend(@Param("customerId") Long customerId, @Param("friendId") Long friendId);


    @Modifying
    @Query("DELETE FROM Friend f WHERE (f.customer.id = :customerId AND f.friend.id = :friendId) OR (f.customer.id = :friendId AND f.friend.id = :customerId)")
    void deleteBothDirections(@Param("customerId") Long customerId, @Param("friendId") Long friendId);


	List<Friend> findByCustomerId(Long customerId);



	List<Friend> findByFriendId(Long customerId);



	



	List<Customer> findFriendsByCustomerId(Long customerId);



	

}
