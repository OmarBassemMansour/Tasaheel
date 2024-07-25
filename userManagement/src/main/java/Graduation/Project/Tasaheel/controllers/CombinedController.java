package Graduation.Project.Tasaheel.controllers;//package Graduation.Project.Tasaheel.controllers;

import Graduation.Project.Tasaheel.models.*;
import Graduation.Project.Tasaheel.repos.UserRepository;
import Graduation.Project.Tasaheel.config.JwtGeneratorValidator;
import Graduation.Project.Tasaheel.services.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class CombinedController {

    @Autowired
    private userServices userService;

    @Autowired
    UserRepository userRepo;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtGeneratorValidator jwtGenVal;

    @Autowired
    BCryptPasswordEncoder bcCryptPasswordEncoder;

    @Autowired
    DefaultUserService defaultUserService;

    @Autowired
    TokenBlacklistService tokenBlacklistService;

    @Autowired
    private imageService imageService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserManagementServices userManagementServices;

    // Registration endpoint
    @PostMapping(path = "/register" , consumes = { "multipart/form-data" } , headers = {"content-type=multipart/form-data"})
        public ResponseEntity<Object> registerUser(@RequestPart String userDtoJson,
                                               @RequestPart("profileImage") MultipartFile profileImage,
                                               @RequestPart("frontId") MultipartFile frontId,
                                               @RequestPart("backId") MultipartFile backId) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDto = objectMapper.readValue(userDtoJson, UserDTO.class);

        boolean usernameExists = userRepo.findByUsername(userDto.getUsername()).isPresent();
        if (usernameExists) {
            return generateResponse("Username already exists", HttpStatus.BAD_REQUEST, null);
        }

        boolean emailExists = userRepo.findByEmail(userDto.getEmail()).isPresent();
        if (emailExists) {
            return generateResponse("Email already exists", HttpStatus.BAD_REQUEST, null);
        }

        User user = defaultUserService.save(userDto);
        if (user == null) {
            return generateResponse("Not able to save user", HttpStatus.BAD_REQUEST, userDto);
        }

        // Upload files
        imageService.uploadProfilePicture(profileImage , user.getId());
        imageService.uploadFrontId(frontId , user.getId());
        imageService.uploadBackId(backId , user.getId());

        String paymentApiUrl = "https://paymentmanagement-youssef-labatia-dev.apps.sandbox-m4.g2pi.p1.openshiftapps.com/payments/initiateWallet/" + user.getId();
        restTemplate.postForEntity(paymentApiUrl, null, Void.class);

        return generateResponse("User registered successfully !!", HttpStatus.OK, user);
    }


    // Login endpoint with token generation
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");  // Corrected to "username" to match your JSON key
        String password = loginRequest.get("password");
        Optional <User> optionalUser = userRepo.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getAcceptedByAdmin() == acceptedByAdmin.pending)
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your account is not accepted by the Admin yet");
            }
        }


        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user != null && user.isBanned() && LocalDateTime.now().isBefore(user.getBanExpiration())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account is banned until " + user.getBanExpiration());
            }
        }

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtGenVal.generateToken(authentication , user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AuthenticationException e) {
            // Logs the error internally and sends a user-friendly message
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // For unexpected exceptions, log them and send a generic error message
            return new ResponseEntity<>("Login error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
            return new ResponseEntity<>("Logout successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Token not provided", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/setRate/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String setRate(@PathVariable Long userId, @RequestParam Double rate) {
        return userService.setRate(userId, rate);
    }

    @PutMapping("/modifyUser/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String modifyUser(@PathVariable Long id, @RequestBody User user) {
        return userService.modifyUser(id, user);
    }

    @DeleteMapping("/deleteUser/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId , @RequestParam String password) {

         Optional<User> optionalUser = userRepo.findById(userId);
         if (optionalUser.isPresent()) {
             User user = optionalUser.get();
             String encryptedPassword = bcCryptPasswordEncoder.encode(password);

             if (user.getPassword().equals(encryptedPassword)) {
                 userService.deleteUser(userId);
                 return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
             }
             else {
                 return new ResponseEntity<>("Invalid password.", HttpStatus.UNAUTHORIZED);
             }
         }
         else {
             return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
         }

    }

    @GetMapping("/getProfilePic/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<String> GetProfilePic(@PathVariable Long userId) {
        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String base64Image = user.getProfileImage();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN); // Return as plain text
            return new ResponseEntity<>(base64Image, headers, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/updateProfilePic/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public ResponseEntity<String> updateProfilePic(@PathVariable Long userId, @RequestParam MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String result = imageService.changeProfilePicture(file , userId);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/getUsername/{userId}")
    public String getUsername(@PathVariable Long userId) {

        return userService.getUsernameById(userId);
    }

    @GetMapping("/getPhoneNb/{userId}")
    public String getPhoneById(@PathVariable Long userId) {

        return userService.getPhoneById(userId);
    }

    @GetMapping("/interests")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<Interest>> getAllInterests() {
        List<Interest> interests = userManagementServices.getAllInterests();
        return ResponseEntity.ok(interests);
    }

    @GetMapping("/getUserDetails/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public User getUserDetails(@PathVariable Long userId) {
       Optional<User> User = userRepo.findById(userId);
       if (User.isPresent()) {
           return User.get();
       }
        else
            return null;

    } 

    @GetMapping("/Accumulative/{userId}")
    public double getAccumulative(@PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRate();
    }

    @GetMapping("ListRates/{userId}")
    @PreAuthorize(("hasAuthority('ROLE_USER')"))
    public List<Double> getSetOfRates(@PathVariable Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRates();
    }



    private ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("Status", status.value());
        map.put("data", responseObj);
        return new ResponseEntity<>(map, status);
    }


}
