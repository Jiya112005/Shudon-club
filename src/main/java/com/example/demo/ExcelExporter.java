package com.example.demo;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    private final List<Customer> customers;
    private final List<Games> games;
    private final List<Booking> bookings;
    private final List<Feedback> feedbacks;

    
    public ExcelExporter() {
		this.customers = null;
		this.games = null;
		this.bookings = null;
		this.feedbacks = null;
		
	}
   

    public void export(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        writeCustomersSheet(workbook);
        writeGamesSheet(workbook);
        writeBookingsSheet(workbook);
        writeFeedbacksSheet(workbook);

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void writeCustomersSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Customers");
        Row header = sheet.createRow(0);

        String[] columns = {"ID", "First Name", "Last Name", "Email", "Password", "Age"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (Customer c : customers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getId());
            row.createCell(1).setCellValue(c.getFirstName());
            row.createCell(2).setCellValue(c.getLastName());
            row.createCell(3).setCellValue(c.getEmail());
            row.createCell(4).setCellValue(c.getPassword());
            row.createCell(5).setCellValue(c.getAge());
        }
    }

    private void writeGamesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Games");
        Row header = sheet.createRow(0);

        String[] columns = {"ID", "Title", "Description", "Genre", "ImageURL", "Players", "Time"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (Games g : games) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(g.getId());
            row.createCell(1).setCellValue(g.getTitle());
            row.createCell(2).setCellValue(g.getDescription());
            row.createCell(3).setCellValue(g.getGenre());
            row.createCell(4).setCellValue(g.getImageUrl());
            row.createCell(5).setCellValue(g.getPlayers());
            row.createCell(6).setCellValue(g.getTime());
        }
    }

    private void writeBookingsSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Bookings");
        Row header = sheet.createRow(0);

        String[] columns = {"ID", "Customer", "Game", "Date", "Time Slot", "Amount", "Players"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (Booking b : bookings) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(b.getId());
            row.createCell(1).setCellValue(b.getCustomer().getFirstName() + " " + b.getCustomer().getLastName());
            row.createCell(2).setCellValue(b.getGame().getTitle());
            row.createCell(3).setCellValue(String.valueOf(b.getDate()));
            row.createCell(4).setCellValue(b.getTimeSlot());
            row.createCell(5).setCellValue(b.getAmount());
            row.createCell(6).setCellValue(b.getPlayers());
        }
    }

    private void writeFeedbacksSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Feedbacks");
        Row header = sheet.createRow(0);

        String[] columns = {"ID", "Name", "Email", "Rating", "Experience", "Submitted At"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (Feedback f : feedbacks) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(f.getId());
            row.createCell(1).setCellValue(f.getName());
            row.createCell(2).setCellValue(f.getEmail());
            row.createCell(3).setCellValue(f.getRating());
            row.createCell(4).setCellValue(f.getExperience());
            row.createCell(5).setCellValue(String.valueOf(f.getSubmittedAt()));
        }
    }
    public void exportCustomers(HttpServletResponse response, List<Customer> customers) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Customers");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("First Name");
        header.createCell(2).setCellValue("Last Name");
        header.createCell(3).setCellValue("Email");
        header.createCell(4).setCellValue("Password");
        header.createCell(5).setCellValue("Age");

        int rowCount = 1;
        for (Customer c : customers) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(c.getId());
            row.createCell(1).setCellValue(c.getFirstName());
            row.createCell(2).setCellValue(c.getLastName());
            row.createCell(3).setCellValue(c.getEmail());
            row.createCell(4).setCellValue(c.getPassword());
            row.createCell(5).setCellValue(c.getAge());
        }

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();
    }
    
    public void exportGames(HttpServletResponse response, List<Games> games) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Games");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Title");
        header.createCell(2).setCellValue("Description");
        header.createCell(3).setCellValue("Genre");
        header.createCell(4).setCellValue("Image URL");
        header.createCell(5).setCellValue("Players");
        header.createCell(6).setCellValue("Time");

        int rowCount = 1;
        for (Games g : games) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(g.getId());
            row.createCell(1).setCellValue(g.getTitle());
            row.createCell(2).setCellValue(g.getDescription());
            row.createCell(3).setCellValue(g.getGenre());
            row.createCell(4).setCellValue(g.getImageUrl());
            row.createCell(5).setCellValue(g.getPlayers());
            row.createCell(6).setCellValue(g.getTime());
        }

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();
    }
    public void exportBookings(HttpServletResponse response, List<Booking> bookings) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Bookings");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Customer Name");
        header.createCell(2).setCellValue("Game Title");
        header.createCell(3).setCellValue("Date");
        header.createCell(4).setCellValue("Time Slot");
        header.createCell(5).setCellValue("Amount");
        header.createCell(6).setCellValue("Players");

        int rowCount = 1;
        for (Booking b : bookings) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(b.getId());
            row.createCell(1).setCellValue(b.getCustomer().getFirstName() + " " + b.getCustomer().getLastName());
            row.createCell(2).setCellValue(b.getGame().getTitle());
            row.createCell(3).setCellValue(String.valueOf(b.getDate()));
            row.createCell(4).setCellValue(b.getTimeSlot());
            row.createCell(5).setCellValue(b.getAmount());
            row.createCell(6).setCellValue(b.getPlayers());
        }

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();
    }
    public void exportFeedbacks(HttpServletResponse response, List<Feedback> feedbacks) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Feedbacks");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Email");
        header.createCell(3).setCellValue("Rating");
        header.createCell(4).setCellValue("Experience");
        header.createCell(5).setCellValue("Submitted At");

        int rowCount = 1;
        for (Feedback f : feedbacks) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(f.getId());
            row.createCell(1).setCellValue(f.getName());
            row.createCell(2).setCellValue(f.getEmail());
            row.createCell(3).setCellValue(f.getRating());
            row.createCell(4).setCellValue(f.getExperience());
            row.createCell(5).setCellValue(f.getSubmittedAt().toString());
        }

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();
    }

	


}