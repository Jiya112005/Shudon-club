package com.example.demo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@org.springframework.stereotype.Controller 

@RequestMapping("/auth")
public class Controller {

   
	@Autowired
	private  CustomerRepository customerRepository;
	
	@Autowired
	private GameRepository gameRepository;
	
	@Autowired
	private JavaMailSender mailsender;
	
	@Autowired
	private BookingService bookingservice;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	
	@Autowired
	private MailService mailservice;
	private Map<String, String> otpstorage=new HashMap<>();
	
	
	 @GetMapping("/friends")
	    public String friendsPage() {
	        return "friends"; // This will load friends.html
	    }
	 
	 
	@GetMapping("/home")
	public String home(HttpSession session) {
	    System.out.println("Session Attributes on Home Page Load:");
	    Enumeration<String> attributeNames = session.getAttributeNames();
	    while (attributeNames.hasMoreElements()) {
	        String attributeName = attributeNames.nextElement();
	        System.out.println(attributeName + " = " + session.getAttribute(attributeName));
	    }

	    return "home"; // Make sure this points to your HTML file
	}


	 @GetMapping("/userlogin")
	    public String showLoginPage() {
		 System.out.println("✅ GET /auth/login called");
		
	        return "userlogin";
	    }
	 
	

	    // ✅ Process Login and Redirect to Home with Toast
	    @PostMapping("/userlogin")
	    public String login(@RequestParam String email, 
	                        @RequestParam String password,
	                        HttpSession session,
	                        RedirectAttributes redirectAttributes,
	                        Model model) {
	        Optional<Customer> user = customerRepository.findByEmail(email);
	        if (user.isPresent() && user.get().getPassword().equals(password)) {
	        	 Customer users = user.get();
	             System.out.println("✅ Login successful: " + users.getEmail());

	             session.setAttribute("customerID", users.getId());
	             session.setAttribute("loggedInUser", users);
	             session.setAttribute("loggedIn", true);

	             redirectAttributes.addFlashAttribute("message", "Login Successful!");
	            model.addAttribute("success","Login successfull ");
	            redirectAttributes.addFlashAttribute("success", "Login successful!");
	            return "redirect:/auth/userlogin";

// Redirect to home with query param
	        } else {
	        	redirectAttributes.addFlashAttribute("message", "Invalid credentials!");
	        	
	            model.addAttribute("error", "Invalid Credentials!");
	          
	            return "userlogin";
	        }
	    }
	    

		 @GetMapping("/gamesss")
		 public String game(@RequestParam(required = false)String genre,Model model) {
			 List<Games> gamesList=gameRepository.findAll();
			  System.out.println("Fetched Games: " + gamesList);
			 model.addAttribute("games",gamesList);
			 return "gamesss";
			 
			 
		 }
		  @GetMapping("/game-details")
		    public String gameDetails(@RequestParam(name = "id", required = true) Long id, Model model) {
			  System.out.println("Fetching details for Game ID: " + id);
		        Optional<Games> game = gameRepository.findById(id);
		        if (game.isPresent()) {
		            model.addAttribute("game", game.get());
		            return "game-details";  // This refers to game-details.html in Thymeleaf
		        } else {
		            return "error";  // Show error page if game is not found
		        }
		    }
	    @GetMapping("/logout")
	    public String logout(HttpSession session) {
	    	session.invalidate();
	    	return"redirect:/auth/home";
	    }
	    
	    @GetMapping("/check-email")
	    @ResponseBody
	    public Map<String,Boolean> checkEmail(@RequestParam String email)
	    {
	    	boolean exists=customerRepository.findByEmail(email).isPresent();
	    	return Collections.singletonMap("exists", exists);
	    	
	    }
	    
	    @GetMapping("/register")
	    public String showRegisterPage(Model model) {
	    	 model.addAttribute("customer", new Customer());
	        return "register";
	    }
	    

	    // ✅ Process Registration and Redirect to Home with Toast
	    @PostMapping("/register")
	    public String registerUser(@ModelAttribute("customer") Customer customer, RedirectAttributes redirectAttributes, Model model, HttpSession session) {

	        Optional<Customer> existingUser = customerRepository.findByEmail(customer.getEmail());

	        if (existingUser.isPresent()) {
	            model.addAttribute("error", "❌ User already exists!");
	            return "register";
	        }
	        Customer savedUser = customerRepository.save(customer);

	        // ✅ Store in session
	        session.setAttribute("loggedInUser", savedUser);

	        // ✅ Redirect with success flag (GET /auth/register?success)
	        redirectAttributes.addAttribute("success", "true");
	        return "redirect:/auth/register";
	    }
				    // ✅ Show Forgot Password Page
				    @GetMapping("/forgot-password")
				    public String showForgotPasswordPage() {
				        return "forgot"; // This will return the Thymeleaf page
				    }
				    
	    @PostMapping("/request-otp")
	    public ResponseEntity<String> requestotp(@RequestParam String email){
	    	Optional<Customer> user=customerRepository.findByEmail(email);
	    	if(user.isEmpty()) {
	    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not register");
	    	}
	    	String otp=String.valueOf(new Random().nextInt(900000)+100000);
	    	otpstorage.put(email, otp);
	    	
	    	sendmail(email,"Your OTP Code","Your OTP for password reset is: "+otp);
	    	System.out.println("otp sent");
	    	return ResponseEntity.ok("OTP sent syuccessfully");
	    }
	    private void sendmail(String TO,String Subject,String text) {
	    	SimpleMailMessage message=new SimpleMailMessage();
	    	message.setTo(TO);
	    	message.setSubject(Subject);
	    	message.setText(text);
	    	mailsender.send(message);
	    	System.out.println("mail");
	    }
	    
	    @PostMapping("/verify-otp")
	    public ResponseEntity<String> verifyOtp(@RequestParam String email,@RequestParam String otp){
	    	if(otpstorage.containsKey(email) && otpstorage.get(email).equals(otp)) {
	    		return ResponseEntity.ok("OTP verified");
	    	}
	    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid OTP");
	    }
	    
	    

	    // ✅ Handle Password Reset Request
	    @PostMapping("/reset-password")
	    @ResponseBody
	    public ResponseEntity<String> resetPassword(
	            @RequestParam String email,
	            @RequestParam String newPassword) {

	        Optional<Customer> userOptional = customerRepository.findByEmail(email);

	        if (userOptional.isPresent()) {
	            Customer user = userOptional.get();
	            user.setPassword(newPassword); // 🔹 Ideally, use password encryption
	            customerRepository.save(user);
	            return ResponseEntity.ok("Password updated successfully!");
	        } else {
	            return ResponseEntity.badRequest().body("Email not found!");
	        }

          }
	    @GetMapping("/book-session")
	    public String bookSession(@RequestParam("id") Long gameId, Model model,HttpSession session, @RequestParam("pass") String passName,
	            @RequestParam("price") String passPrice) {
	        // Fetch game details using the gameId
	    	if (session.getAttribute("loggedInUser") == null) {
	            return "redirect:/auth/userlogin"; // Redirect to login if not logged in
	        }
	        Optional<Games> game = gameRepository.findById(gameId);
	        
	        // Pass game details to the book session page
	        model.addAttribute("game", game.get());
	        model.addAttribute("passName", passName);
	        model.addAttribute("passPrice", passPrice);
	       
	        return "book-session";  // Return the booking page
	    }
	    
	   
	    
	    @GetMapping("/book-session2")
	    public String bookSession2(Model model,HttpSession session) {
	    	// Fetch game details using the gameId
	    	
	    	Object loggedInUser = session.getAttribute("loggedInUser");

	        if (loggedInUser == null) {
	        	System.out.println("User is null");
	            return "redirect:/auth/userlogin"; // Redirect if not logged in
	        }

	        // Now safely cast since we know it's not null
	        Customer user = (Customer) loggedInUser;
	        
	        System.out.println("Logged-in User: " + user);
	        return "book-session2";  // Return the booking page the booking page
	    }
	    @GetMapping("/aboutus")
	    public String aboutUs() {
	        return "/fragments/Aboutus";  // Make sure aboutus.html exists in src/main/resources/templates/
	    }
	    @GetMapping("/game-details2")
	    public String showGameDetails(Model model) {
	    	 List<Games> gamesList=gameRepository.findAll();
			  System.out.println("Fetched Games: " + gamesList);
			 model.addAttribute("games",gamesList);
	        return "game-details2";  // This maps to game-details.html
	    }
	    @PostMapping("/book-session")
	    public ResponseEntity<String> bookGameSession(
	            @RequestParam Long id, // gameId from URL
	            @RequestBody Map<String, Object> bookingData) {

	    	System.out.println("Received ID: " + id);
	    	System.out.println("Booking data: " + bookingData);
	    	String email = (String) bookingData.get("email");
	    	System.out.println(email);

	    	if (email == null || email.isBlank()) {
	    	    return ResponseEntity.badRequest().body("Email is required");
	    	}

	        // ✅ Step 2: Validate Game ID
	        Optional<Games> gameOpt = gameRepository.findById(id);
	        if (gameOpt.isEmpty()) {
	            return ResponseEntity.badRequest().body("Invalid game ID");
	        }

	        // ✅ Step 3: Validate Customer
	        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
	        if (customerOpt.isEmpty()) {
	            return ResponseEntity.badRequest().body("Invalid customer");
	        }
	        Customer customer = customerOpt.get();
	        	
	        // ✅ Step 4: Create Booking
	        Booking booking = new Booking();
	        booking.setGame(gameOpt.get());
	        booking.setCustomer(customer);
	        booking.setDate(LocalDate.parse((String) bookingData.get("date")));
	        booking.setTimeSlot((String) bookingData.get("timeSlot"));
	        booking.setCreatedAt(null); // ❌ this will override it


	        try {
	            if (bookingData.containsKey("amount")) {
	                booking.setAmount(Integer.parseInt(bookingData.get("amount").toString()));
	            }
	            if (bookingData.containsKey("players")) {
	                booking.setPlayers(Integer.parseInt(bookingData.get("players").toString()));
	            }
	        } catch (NumberFormatException e) {
	            return ResponseEntity.badRequest().body("Invalid number format in amount/players");
	        }
	        if (bookingData.containsKey("paymentId")) {
	            booking.setPaymentId((String) bookingData.get("paymentId"));
	        }
	        
	        if (bookingData.containsKey("orderId")) {
	            booking.setOrderId((String) bookingData.get("orderId"));
	        }

	        bookingRepository.save(booking);
	        
	        
	        try {
	            mailservice.sendBookingConfirmation(email, booking);
	        } catch (Exception e) {
	            System.err.println("❌ Failed to send email: " + e.getMessage());
	        }
	        
	        return ResponseEntity.ok("Booking saved successfully!");
	    }

	    @GetMapping("/check-login")
	    public ResponseEntity<Map<String, Boolean>> checkLoginStatus(Principal principal) {
	        boolean loggedIn = (principal != null);
	        return ResponseEntity.ok(Collections.singletonMap("loggedInUser", loggedIn));
	    }
	    
	    @GetMapping("/profile")
	    public String userProfile(Model model, HttpSession session) {
	        // Retrieve logged-in user from session
	        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");

	        if (loggedInUser == null) {
	            return "redirect:/auth/userlogin"; // Redirect if not logged in
	        }

	        model.addAttribute("customer", loggedInUser);
	        return "profile"; // Render profile.html
	    }
	    
	    @PostMapping("/update-profile")
	    public String updateProfile(
	    	    @RequestParam("firstName") String firstName, // ✅ Handle fields separately
	    	    @RequestParam("lastName") String lastName,
	    	    @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture, // ✅ Handle file separately
	    	    HttpSession session,
	    	    RedirectAttributes redirectAttributes
	    	) {
	    	    // ✅ Get logged-in user from session
	    	    Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");
	    	    if (loggedInUser == null) {
	    	        return "redirect:/auth/userlogin";
	    	    }

	    	    loggedInUser.setFirstName(firstName);
	    	    loggedInUser.setLastName(lastName);

	    	    // ✅ Handle file upload correctly
	    	    if (profilePicture != null && !profilePicture.isEmpty()) {
	    	        try {
	    	            String fileName = System.currentTimeMillis() + "_" + profilePicture.getOriginalFilename();
	    	            Path uploadPath = Paths.get("src/main/resources/static/uploads/");
	    	            Files.createDirectories(uploadPath);
	    	            Path filePath = uploadPath.resolve(fileName);
	    	            Files.copy(profilePicture.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	    	            loggedInUser.setProfilePicture(fileName); // ✅ Save only filename in database
	    	        } catch (IOException e) {
	    	            e.printStackTrace();
	    	            redirectAttributes.addFlashAttribute("error", "Error saving profile picture.");
	    	        }
	    	    }
	    	    System.out.println(loggedInUser.getProfilePicture());
	    	    customerRepository.save(loggedInUser);
	    	    session.setAttribute("loggedInUser", loggedInUser); // ✅ Update session

	    	    redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
	    	    return "redirect:/auth/profile";
	    	}
	    
	    @GetMapping("/bookings")
	    public String showBookings(Model model, HttpSession session) {
	        // Get current user (from session or your method)
	        Customer currentUser = (Customer) session.getAttribute("loggedInUser");

	        if (currentUser != null) {
	            List<Booking> allBookings = bookingRepository.findByCustomer(currentUser);

	            List<Booking> upcomingBookings = new ArrayList<>();
	            List<Booking> pastBookings = new ArrayList<>();

	            LocalDate today = LocalDate.now();

	            for (Booking booking : allBookings) {
	                if (booking.getDate().isBefore(today)) {
	                    pastBookings.add(booking);
	                } else {
	                    upcomingBookings.add(booking);
	                }
	            }

	            model.addAttribute("upcomingBookings", upcomingBookings);
	            model.addAttribute("pastBookings", pastBookings);
	        }

	        return "bookings"; // your HTML page
	    }
	    
	    @GetMapping("/receipt")
	    public String getReceiptPage(@RequestParam Long bookingId, Model model) {
	        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
	        
	        if (optionalBooking.isPresent()) {
	            model.addAttribute("booking", optionalBooking.get());
	            return "receipt"; // Thymeleaf template
	        } else {
	            // Handle booking not found, e.g., redirect or error page
	            return "error"; // Or use a custom "not-found" page
	        }
	    }
	    
	    @GetMapping("/sessions")
	    public String showUserSessions(Model model) {
	        List<String> fixedSessions = Arrays.asList(
	            "10:00 AM - 12:00 PM",
	            "12:00 PM - 2:00 PM",
	            "2:00 PM - 4:00 PM",
	            "4:00 PM - 6:00 PM",
	            "6:00 PM - 8:00 PM"
	        );
	        model.addAttribute("fixedSessions", fixedSessions);
	        return "book-session"; // Thymeleaf page
	    }
}
