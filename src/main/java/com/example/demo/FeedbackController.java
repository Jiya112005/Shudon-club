package com.example.demo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.qos.logback.core.model.Model;

@org.springframework.stereotype.Controller
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/submit-feedback")
    public String showFeedback(Model model) {
    	return "feedback";
    }
    
    @PostMapping("/submit-feedback")
    @ResponseBody
    public Map<String, String> handleFeedback(@RequestParam String name,
                                              @RequestParam String email,
                                              @RequestParam String experience,
                                              @RequestParam int rating) {
        Feedback feedback = new Feedback();
        feedback.setName(name);
        feedback.setEmail(email);
        feedback.setExperience(experience);
        feedback.setRating(rating);
        feedback.setSubmittedAt(LocalDateTime.now()); 
        feedbackRepository.save(feedback);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Feedback submitted successfully");
        return response;
    }
}
