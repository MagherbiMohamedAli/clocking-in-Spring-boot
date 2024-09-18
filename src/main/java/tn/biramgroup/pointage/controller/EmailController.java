package tn.biramgroup.pointage.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.biramgroup.pointage.services.implement.EmailService;
import tn.biramgroup.pointage.springSecurityJwt.EmailRequest;

import java.io.IOException;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "https://clocking-in-angular-3aee.vercel.app/")
public class EmailController {
//
//    @Autowired
//    private EmailService emailService;
//
//    @PostMapping("/send")
//    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
//        try {
//            emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
//            return ResponseEntity.ok("Email sent successfully");
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().body("Failed to send email: " + e.getMessage());
//        }
//    }
}
