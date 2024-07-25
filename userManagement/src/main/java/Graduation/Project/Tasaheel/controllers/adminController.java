package Graduation.Project.Tasaheel.controllers;

import Graduation.Project.Tasaheel.models.Image;
import Graduation.Project.Tasaheel.models.User;
import Graduation.Project.Tasaheel.models.UserDTO;
import Graduation.Project.Tasaheel.repos.UserRepository;
import Graduation.Project.Tasaheel.services.adminService;
import Graduation.Project.Tasaheel.services.imageService;
import Graduation.Project.Tasaheel.services.userServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class adminController {

    @Autowired
    adminService adminService;

    @Autowired
    private imageService imageService;

    @Autowired
    private userServices userService;

    @Autowired
    UserRepository userRepo;

    @PutMapping("/ban")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> banUser(@RequestParam String username, @RequestParam int days) {
        return adminService.banUser(username, days);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getPendingUsers() {
        List<User> pendingUsers = adminService.getPendingUsers();
        return ResponseEntity.ok(pendingUsers);
    }

    @GetMapping("/getUserDetails/{user_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable Long user_id) {
        return adminService.listUserDetails(user_id);
    }

    @PutMapping("/acceptUser/{user_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> acceptUser(@PathVariable Long user_id) {
            return adminService.acceptUser(user_id);
    }

    @PutMapping("/rejectUser/{user_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> rejectUser(@PathVariable Long user_id) {
        return adminService.rejectUser(user_id);
    }

    @GetMapping("/GetFrontId/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> GetFrontId(@PathVariable Long userId) {
            Optional<User> optionalUser = userRepo.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String base64Image = user.getFrontId();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_PLAIN); // Return as plain text
                return new ResponseEntity<>(base64Image, headers, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    // Endpoint to get back ID image
    @GetMapping("/GetBackId/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getBackId(@PathVariable Long userId) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String base64Image = user.getBackId();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN); // Return as plain text
            return new ResponseEntity<>(base64Image, headers, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/deleteUser/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

}
