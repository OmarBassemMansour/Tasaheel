package Graduation.Project.Tasaheel.controllers;

import Graduation.Project.Tasaheel.config.JwtGeneratorValidator;
import Graduation.Project.Tasaheel.services.DefaultUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.GrantedAuthority;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtGeneratorValidator jwtGenVal;

    @Autowired
    private DefaultUserService defaultUserService;

    @PostMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        try {
            String username = jwtGenVal.extractUsername(token);
            UserDetails userDetails = defaultUserService.loadUserByUsername(username);
            if (jwtGenVal.validateToken(token, userDetails)) {
                Map<String, Object> response = new HashMap<>();
                response.put("username", username);
                response.put("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error validating token: " + e.getMessage());
        }
    }
}