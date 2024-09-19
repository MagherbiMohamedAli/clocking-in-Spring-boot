package tn.biramgroup.pointage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.biramgroup.pointage.Repository.RoleRepository;
import tn.biramgroup.pointage.Repository.StatusRepository;
import tn.biramgroup.pointage.Repository.UserRepository;
import tn.biramgroup.pointage.model.*;
import tn.biramgroup.pointage.services.implement.StatusService;
import tn.biramgroup.pointage.services.implement.UserService;
import tn.biramgroup.pointage.springSecurityJwt.Message;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "https://biramgroup.vercel.app/")
//@CrossOrigin(origins = "http://localhost:4200")

public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    StatusRepository statusRepository;
    @Autowired
    StatusService statusService;
    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id){
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent())
            return new ResponseEntity<>(user.get(),HttpStatus.OK);
        else
            return new ResponseEntity<>(new Message("Utilisateur Introuvable"), HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/employees")
    public ResponseEntity<List<User>> getAllEmployees() {
        Role employeeRole = roleRepository.findByRole(ERole.ROLE_EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        List<User> employees = userRepository.findByRoles(Set.of(employeeRole));

        return ResponseEntity.ok(employees);
    }
    @GetMapping("/byStatus")
    public ResponseEntity<List<User>> getUsersByStatus(@RequestParam Long statusId) {
        List<User> users = userRepository.findByStatusId(statusId);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addUserStatus(@RequestParam Long userId, @RequestParam Long statusId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status not found"));
        EStatus newStatus = EStatus.valueOf(status.getStatus().name());
        statusService.updateUserStatus(userId, newStatus);

        return ResponseEntity.ok().build();
    }

//    @GetMapping("/verify")
//    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
//        String decodedToken = URLDecoder.decode(token, StandardCharsets.UTF_8);
//
//        User user = userService.findByVerificationToken(decodedToken);
//
//        if (user == null) {
//            return ResponseEntity.badRequest().body("Invalid token");
//        }
//
//        user.setEnabled(true);
//        user.setVerificationToken(null);
//        userRepository.save(user);
//
//        return ResponseEntity.ok("Email verified successfully");
//    }

    @GetMapping("/{userId}/total-time")
    public ResponseEntity<Long> getTotalTimeWorked(@PathVariable Long userId) {
        long totalMinutes = statusService.getTotalMinutesWorked(userId);
        return ResponseEntity.ok(totalMinutes);
    }
    @GetMapping("/all/total-time")
    public ResponseEntity<Map<Long, Long>> getTotalTimeWorkedForAllUsers() {
        Map<Long, Long> totalTimeMap = statusService.getTotalTimeWorkedForAllUsers();
        return ResponseEntity.ok(totalTimeMap);
    }

    @PostMapping("/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> payload) {

        Long statusId = Long.valueOf(payload.get("statusId").toString());
        Optional<String> workMode = Optional.ofNullable((String) payload.get("workMode"));
        userService.updateUserStatusAndWorkMode(userId, statusId, workMode);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            userRepository.delete(user.get());
            return new ResponseEntity<>(new Message("User deleted successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("User not found"), HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody User updatedUser) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            user.setNom(updatedUser.getNom());
            user.setPrenom(updatedUser.getPrenom());
            user.setEmail(updatedUser.getEmail());

            userRepository.save(user);

            return new ResponseEntity<>(new Message("User updated successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("User not found"), HttpStatus.NOT_FOUND);
        }
    }


}
