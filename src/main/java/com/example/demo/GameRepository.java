package com.example.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface GameRepository extends JpaRepository<Games, Long>{

	Optional<Games> findByTitle(String title);



}
