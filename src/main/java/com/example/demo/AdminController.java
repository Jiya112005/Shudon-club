package com.example.demo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GameRepository gamerepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BookingService bookingService;
    
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    
    @Autowired
    private SessionRepository sessionRepository;

    private final MailService emailService;

    public AdminController(MailService emailService) {
        this.emailService = emailService;
    }
    

    @GetMapping("/")
    public String showLoginPage(Model model, HttpServletRequest request) {
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (flashMap != null && flashMap.containsKey("accessDenied")) {
            model.addAttribute("accessDenied", true);
        } else {
            model.addAttribute("accessDenied", false);
        }

        return "admin_login";
    }

    
    @GetMapping("/logout")
    public String logoutAdmin(HttpSession session) {
        session.invalidate(); // clear the session
        return "redirect:/admin/"; // send back to admin login
    }
    @PostMapping("/login")
    public String loginAdmin(@RequestParam String email,
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {

        // 🔐 Hardcoded admin credentials (for demo/testing only)
    	 if ("user@shudon".equals(email) && "raval123".equals(password)) {
    		 System.out.println("matched !!");
    	        session.setAttribute("admin", true);
    	        return "redirect:/admin/dashboard";
    	    } else {
    	        model.addAttribute("error", "Invalid admin credentials");
    	        return "admin_login";
    	    }
    }
    @GetMapping("/dashboard")
    public String adminDashboard(Model model,HttpSession session,RedirectAttributes redirectAttributes) {
    	 long totalCustomers = customerRepository.count();
    	    long totalGames = gamerepository.count();
    	    long totalBookings = bookingRepository.count();
    	    long totalFeedback=feedbackRepository.count();
    	    List<Customer> customers = customerRepository.findAll();
    	    List<Games> games = gamerepository.findAll();
    	    List<Booking> bookings = bookingRepository.findAll();

    	    
    	    Boolean isAdmin = (Boolean) session.getAttribute("admin");
    	    
    	    
    	    if (isAdmin != null && isAdmin) {
    	     
    	        model.addAttribute("totalCustomers", totalCustomers);
        	    model.addAttribute("totalGames", totalGames);
        	    model.addAttribute("totalBookings", totalBookings);
        	    model.addAttribute("totalFeedback",totalFeedback);
        	    model.addAttribute("feedback", feedbackRepository.findAll());
        	    model.addAttribute("customers", customers);
        	    model.addAttribute("games", games);
        	    model.addAttribute("bookings", bookings);
        	    model.addAttribute("sessions", sessionRepository.findAll());
        	    return "admin_dashboard"; // HTML page
    	    }
    	    redirectAttributes.addFlashAttribute("accessDenied", true);
    	    return "redirect:/admin/";
    }
    @PostMapping("/addgames")
    public String addGame(@RequestParam String title,
                          @RequestParam String description,
                          @RequestParam String genre,
                          @RequestParam String imageUrl,
                          @RequestParam String players,
                          @RequestParam String time,
                          RedirectAttributes redirectAttributes) {
        Games newGame = new Games(title, description, genre, imageUrl, players, time);
        gamerepository.save(newGame);
        redirectAttributes.addFlashAttribute("success", "Game added successfully!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/removeBooking")
    public String removeBooking(@RequestParam("id") Long bookingId, @RequestParam String section, RedirectAttributes redirectAttributes) {
    	Optional<Booking> optionalBooking = bookingService.getBookingById(bookingId);
        
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();

            // ✅ Extract details for the email
            String userEmail = booking.getCustomer().getEmail();
            String customerName = booking.getCustomer().getFirstName();
            String gameTitle = booking.getGame().getTitle();
            String date = booking.getDate().toString();
            String timeSlot = booking.getTimeSlot();
    	        
        emailService.sendCancellationEmail(userEmail, customerName, gameTitle, date, timeSlot);
    	bookingService.deleteBooking(bookingId);
        redirectAttributes.addFlashAttribute("activeSection", section);
       
    } return "redirect:/admin/dashboard";
    }
    @PostMapping("/removeCustomer")
    public String removeCustomer(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        customerRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Customer removed successfully!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/removeGame")
    public String removeGame(@RequestParam("id") Long gameId, RedirectAttributes redirectAttributes) {
        gamerepository.deleteById(gameId);
        redirectAttributes.addFlashAttribute("success", "Game removed successfully!");
        return "redirect:/admin/dashboard";
    }
    @PostMapping("/removeFeedback")
    public String removeFeedback(@RequestParam("id") Long id) {
        feedbackRepository.deleteById(id);
        return "redirect:/admin/dashboard"; // or your actual page
    }
    @GetMapping("/export/customers")
    public void exportCustomers(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=customers.xlsx");

        List<Customer> customers = customerRepository.findAll();
        new ExcelExporter().exportCustomers(response, customers);
    }

    @GetMapping("/export/games")
    public void exportGames(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=games.xlsx");

        List<Games> games = gamerepository.findAll();
        new ExcelExporter().exportGames(response, games);
    }

    @GetMapping("/export/bookings")
    public void exportBookings(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=bookings.xlsx");

        List<Booking> bookings = bookingRepository.findAll();
        new ExcelExporter().exportBookings(response, bookings);
    }

    @GetMapping("/export/feedbacks")
    public void exportFeedbacks(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=feedbacks.xlsx");

        List<Feedback> feedbacks = feedbackRepository.findAll();
        new ExcelExporter().exportFeedbacks(response, feedbacks);
    }

    
    @PostMapping("/addSession")
    public String addSession(@RequestParam String session) {
        Session newSession = new Session();
        newSession.setSession(session);
        sessionRepository.save(newSession);
        return "redirect:/admin/dashboard";
    }

    // Remove session
    @PostMapping("/removeSession")
    public String removeSession(@RequestParam Long id) {
        sessionRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }
}