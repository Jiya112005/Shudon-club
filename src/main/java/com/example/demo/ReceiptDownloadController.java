package com.example.demo;

import java.io.InputStream;
import java.io.OutputStream;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;

@org.springframework.stereotype.Controller
public class ReceiptDownloadController {

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/download-receipt")
    public void downloadReceipt(@RequestParam Long bookingId, HttpServletResponse response) {
        try {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            
            Customer customer = booking.getCustomer(); // Directly get the customer
            String customerName = customer != null ? customer.getFirstName() : "Unknown";

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=receipt.pdf");

            InputStream templateStream = getClass().getResourceAsStream("/static/receiptformat.pdf"); // ensure the PDF is in resources/static
            PdfReader reader = new PdfReader(templateStream);
            PdfStamper stamper = new PdfStamper(reader, response.getOutputStream());
            PdfContentByte canvas = stamper.getOverContent(1);

            BaseFont timesRoman = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.WINANSI, BaseFont.EMBEDDED);
            canvas.beginText();
            canvas.setFontAndSize(timesRoman, 27);
            
            // Adjust X, Y positions as per your teplate
            canvas.showTextAligned(Element.ALIGN_LEFT, "Customer Name: " + customerName, 50, 620, 0);
            canvas.showTextAligned(Element.ALIGN_LEFT, "Booking ID: BK" + booking.getId(), 50, 585, 0);
            canvas.showTextAligned(Element.ALIGN_LEFT, "Game: " + booking.getGame().getTitle(), 50, 550, 0);
            canvas.showTextAligned(Element.ALIGN_LEFT, "Date: " + booking.getDate().toString(), 50, 515, 0);
            canvas.showTextAligned(Element.ALIGN_LEFT, "Time Slot: " + booking.getTimeSlot(), 50, 480, 0);
            canvas.showTextAligned(Element.ALIGN_LEFT, "Players: " + booking.getPlayers(), 50, 445, 0);
            canvas.showTextAligned(Element.ALIGN_LEFT, "Total Paid: ₹" + booking.getAmount(), 50, 410, 0);
            canvas.showTextAligned(Element.ALIGN_LEFT, "Booked on : " + booking.getCreatedAt(), 50, 375, 0);
            
            canvas.endText();
            stamper.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}