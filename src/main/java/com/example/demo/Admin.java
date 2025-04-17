package com.example.demo;


import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class Admin {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment Admin_ID
	    @Column(name = "Admin_ID")
	    private Long id; 


    @Column(name = "admin_email", unique = true, nullable = false)
    private String adminEmail;

    @Column(name = "admin_nm", nullable = false)
    private String adminNm;

    @Column(name = "password", nullable = false)
    private String password;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    public String getAdminNm() { return adminNm; }
    public void setAdminNm(String adminNm) { this.adminNm = adminNm; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
