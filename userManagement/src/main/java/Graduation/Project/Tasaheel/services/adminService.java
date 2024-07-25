package Graduation.Project.Tasaheel.services;

import Graduation.Project.Tasaheel.models.User;
import Graduation.Project.Tasaheel.models.UserDTO;
import Graduation.Project.Tasaheel.models.acceptedByAdmin;
import Graduation.Project.Tasaheel.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class adminService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<String> banUser(String username, int days) {
        Long id = getIdByUsername(username);

        if (id == null) {
            return ResponseEntity.notFound().build(); // Returns a 404 Not Found status
        }
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setBanned(true);
        user.setBanExpiration(LocalDateTime.now().plusDays(days));
        userRepository.save(user);

        return ResponseEntity.ok("User banned for " + days + " days"); // Returns a 200 OK status with a message
    }
    public List<User> getPendingUsers() {
        return userRepository.findByAcceptedByAdmin(acceptedByAdmin.pending);
    }

    public ResponseEntity<UserDTO> listUserDetails(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhoneNb(),
                        user.getGender(),
                        user.getNationalId(),
                        user.getCountry(),
                        user.getCity()
                ))  // Convert User to UserDTO, excluding sensitive data
                .map(ResponseEntity::ok)  // Wrap UserDTO in ResponseEntity
                .orElseGet(() -> ResponseEntity.notFound().build());  // Handle user not found scenario
    }


    public ResponseEntity<String> acceptUser(Long userId) {
        // Retrieve the user by ID
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            // If the user is not found, return a 404 Not Found response
            return ResponseEntity.notFound().build();
        }
        // Update the user's acceptance status
        user.setAcceptedByAdmin(acceptedByAdmin.accepted);
        userRepository.save(user); // Save the updated user

        // Return a 200 OK response indicating successful operation
        return ResponseEntity.ok("User with ID: " + userId + " has been accepted successfully.");
    }

    public ResponseEntity<String> rejectUser(Long userId) {
        // Retrieve the user by ID
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            // If the user is not found, return a 404 Not Found response
            return ResponseEntity.notFound().build();
        }
        // Update the user's acceptance status
        user.setAcceptedByAdmin(acceptedByAdmin.rejected);
        userRepository.save(user);
        return ResponseEntity.ok("User with ID: " + userId + " has been rejected successfully.");
    }



    public Long getIdByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get().getId();
        } else {
            return null; // Return 404 if the user is not found
        }
    }


}
