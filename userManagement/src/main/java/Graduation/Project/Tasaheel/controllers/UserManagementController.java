package Graduation.Project.Tasaheel.controllers;

import Graduation.Project.Tasaheel.models.Interest;
import Graduation.Project.Tasaheel.services.UserManagementServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user-management")
public class UserManagementController {

    //Admin add new interests fel 3omoum
    @Autowired
    private UserManagementServices userManagementServices;
    @Autowired
    private Graduation.Project.Tasaheel.services.userServices userServices;

    @PostMapping("/{userId}/interests")
    public ResponseEntity<String> addInterests(@PathVariable Long userId, @RequestBody Set<String> interestNames) {
        String result = userManagementServices.addInterestsToUser(userId, interestNames);
        if (result.startsWith("Interest not found:")) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } else if (result.equals("User not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else if (result.equals("Interests added successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addInterests")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addInterests(@RequestBody List<String> interestNames) {
        String result = userManagementServices.addInterests(interestNames);
        if (result.equals("Interests added successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result.equals("All interests already exist")) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/interests")
    public ResponseEntity<List<Interest>> getAllInterests() {
        List<Interest> interests = userManagementServices.getAllInterests();
        return ResponseEntity.ok(interests);
    }

    @GetMapping("/{userId}/interests")
    public ResponseEntity<Set<Interest>> getInterestsForLoggedInUser(@PathVariable("userId") Long userId) {
        try {
            Set<Interest> interests = userManagementServices.getInterestsForLoggedInUser(userId);
            return new ResponseEntity<>(interests, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{userId}/interests/{interestName}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> removeInterest(@PathVariable Long userId, @PathVariable String interestName) {
        String result = userManagementServices.removeInterestFromUser(userId, interestName);
        if (result.equals("Interest removed successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result.equals("User must be logged in to remove interests") ||
                result.equals("Interest not found for user") ||
                result.equals("User not found")) {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping ("/getUserId")
    public Long getUserId( @RequestParam String username) {
        return userServices.getUserIdByUsername(username);
    }
}
