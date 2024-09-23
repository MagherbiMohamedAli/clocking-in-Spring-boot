package tn.biramgroup.pointage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.biramgroup.pointage.Repository.StatusRepository;
import tn.biramgroup.pointage.model.Status;

import java.util.List;

@RestController
@RequestMapping("/api/status")
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "https://biramgroup.vercel.app/")
public class StatusController {
    @Autowired
    private StatusRepository statusRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Status>> getAllStatuses() {
        List<Status> statuses = statusRepository.findAll();
        return ResponseEntity.ok(statuses);
    }
}
