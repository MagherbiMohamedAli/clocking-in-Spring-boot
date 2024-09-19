package tn.biramgroup.pointage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.biramgroup.pointage.Repository.AbsenceRepository;
import tn.biramgroup.pointage.Repository.UserRepository;
import tn.biramgroup.pointage.model.Absence;
import tn.biramgroup.pointage.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/abs")
@CrossOrigin(origins = "https://biramgroup.vercel.app/")
public class AbsenceController {

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/pending")
    public ResponseEntity<List<Absence>> getPendingAbsences() {
        List<Absence> pendingAbsences = absenceRepository.findByAcceptedIsNull();
        return ResponseEntity.ok(pendingAbsences);
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Absence>> getApprovedAbsences() {
        List<Absence> approvedAbsences = absenceRepository.findByAccepted(true);
        return ResponseEntity.ok(approvedAbsences);
    }

    @GetMapping("/denied")
    public ResponseEntity<List<Absence>> getDeniedAbsences() {
        List<Absence> deniedAbsences = absenceRepository.findByAccepted(false);
        return ResponseEntity.ok(deniedAbsences);
    }

    @PostMapping("/request")
    public ResponseEntity<?> addAbsenceRequest(@RequestBody Absence absence, @RequestParam Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getAbsence() == null) {
                absence.setAccepted(null);
                absence.setDateStart(LocalDate.now());
                absenceRepository.save(absence);

                user.setAbsence(absence);
                userRepository.save(user);

                return ResponseEntity.ok("Absence request submitted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already has an absence.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{absenceId}/approve")
    public ResponseEntity<Map<String, String>> approveAbsence(@PathVariable Long absenceId, @RequestParam Boolean accepted) {
        Optional<Absence> absenceOpt = absenceRepository.findById(absenceId);

        if (absenceOpt.isPresent()) {
            Absence absence = absenceOpt.get();
            absence.setAccepted(accepted);
            absenceRepository.save(absence);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Absence approval status updated.");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
