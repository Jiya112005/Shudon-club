package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendBookingConfirmation(String toEmail, Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("✅ Booking Confirmed - Shudon Board Club");
        message.setText("Hi " + booking.getCustomer().getFirstName() + ",\n\n"
                + "Your booking is confirmed for the game: " + booking.getGame().getTitle() + "\n"
                + "Date: " + booking.getDate() + "\n"
                + "Time: " + booking.getTimeSlot() + "\n"
                + "Players: " + booking.getPlayers() + "\n"
                + "Amount Paid: ₹" + booking.getAmount() + "\n\n"
                + "Razorpay Payment ID: " + booking.getPaymentId() + "\n"
                + "Thank you for booking with Shudon! 🎲");

        mailSender.send(message);
    }
    
    
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Mail sent to " + to);
        } catch (Exception e) {
            System.out.println("Failed to send email to " + to);
            e.printStackTrace(); // <-- See if any error is printed
        }
    }
    
    public void sendCancellationEmail(String to, String name, String gameTitle, String date, String timeSlot) {
        String subject = "Booking Cancellation Notice";

        String content = """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                <p>Dear %s,</p>

                <p>We regret to inform you that your recent booking for 
                <strong>%s</strong> on <strong>%s</strong> at <strong>%s</strong> 
                has been <span style="color:red;">cancelled</span> by the administrator.</p>

                <p>This cancellation may have occurred due to internal session issues or unforeseen circumstances.
                Please accept our sincere apologies for the inconvenience this may have caused.</p>

                <p>We assure you that a full refund will be processed and credited to your original payment method within the next 7 business days.</p>

                <p>If you have any questions or need further assistance, feel free to reach out to our support team.</p>

                <p>Thank you for your understanding and continued support.</p>

                <br>

                <p>Warm regards,<br>
                The Shudon Board Club Team</p>
            </body>
            </html>
        """.formatted(name, gameTitle, date, timeSlot);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true enables HTML formatting
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}