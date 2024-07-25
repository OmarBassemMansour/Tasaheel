package Graduation.Project.Tasaheel.services;

import Graduation.Project.Tasaheel.EmailValidator;
import Graduation.Project.Tasaheel.models.Interest;
import Graduation.Project.Tasaheel.models.User;
import Graduation.Project.Tasaheel.models.acceptedByAdmin;
import Graduation.Project.Tasaheel.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class userServices {

    @Autowired
    private UserRepository userRepository;


    public userServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String setRate(Long userId, double rate) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Add the new rate and automatically calculate the average
            user.addRate(rate);

            // Save the user with the new rate
            userRepository.save(user);
            return "Rate successful";
        } else {
            throw new RuntimeException("User not found");
        }
    }


    public String modifyUser(Long userId, User newUserDetails) {
        // Fetch the current user details from the database.
        Optional<User> currentUserOpt = userRepository.findById(userId);

        if (!currentUserOpt.isPresent()) {
            return "User not found";
        }
        User currentUser = currentUserOpt.get();

        // Check if the new username is different from the current and if it already exists.
        if (newUserDetails.getUsername() != null && !newUserDetails.getUsername().equals(currentUser.getUsername())) {
            Optional<User> existingUserOpt = userRepository.findByUsername(newUserDetails.getUsername());
            if (existingUserOpt.isPresent() && !existingUserOpt.get().getId().equals(currentUser.getId())) {
                return "Username " + newUserDetails.getUsername() + " is already taken by another user";
            }
            currentUser.setUsername(newUserDetails.getUsername());  // Set new username after checking it's not taken
        }

        // Update other fields if they are not null and have changed.
        if (newUserDetails.getCity() != null && !newUserDetails.getCity().equals(currentUser.getCity())) {
            currentUser.setCity(newUserDetails.getCity());
        }
        if (newUserDetails.getCountry() != null && !newUserDetails.getCountry().equals(currentUser.getCountry())) {
            currentUser.setCountry(newUserDetails.getCountry());
        }
        if (newUserDetails.getEmail() != null && !newUserDetails.getEmail().equals(currentUser.getEmail())) {
            currentUser.setEmail(newUserDetails.getEmail());
        }
        if (newUserDetails.getPhoneNb() != null && !newUserDetails.getPhoneNb().equals(currentUser.getPhoneNb())) {
            currentUser.setPhoneNb(newUserDetails.getPhoneNb());
        }
        if (newUserDetails.getPassword() != null && !newUserDetails.getPassword().isEmpty()) {
            currentUser.setPassword(encryptPassword(newUserDetails.getPassword())); // Assume encryptPassword is a method to hash the password
        }
        if (newUserDetails.getInterests() != null && !newUserDetails.getInterests().isEmpty()) {
            currentUser.getInterests().clear();
            currentUser.getInterests().addAll(newUserDetails.getInterests());
        }

        // Save the updated user
        userRepository.save(currentUser);
        return "User modified successfully";
    }

    public String deleteUser (Long userid)
    {
        Optional<User> optionalUser = userRepository.findById(userid);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userRepository.delete(user);
            return "User deleted successfully";
        }
        else
        {
            return "User not found";
        }
    }



    private String encryptPassword(String password) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        return bcrypt.encode(password);
    }


    public String getUsernameById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getUsername();
        } else {
            throw new RuntimeException("User not found for id: " + userId);
        }
    }

    public String getPhoneById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getPhoneNb();
        } else {
            throw new RuntimeException("User not found for id: " + userId);
        }
    }


    public Long getUserIdByUsername ( String username)
    {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getId();
        }
        return null;
    }
}
