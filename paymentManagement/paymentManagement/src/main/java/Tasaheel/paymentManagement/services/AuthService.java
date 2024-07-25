package Tasaheel.paymentManagement.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    private final String userManagementUrl = "https://usermanagement-youssef-labatia-dev.apps.sandbox-m4.g2pi.p1.openshiftapps.com/auth/validateToken";

    public ResponseEntity<Map> validateToken(String token) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(userManagementUrl)
                .queryParam("token", token);

        ResponseEntity<Map> response = restTemplate.postForEntity(uriBuilder.toUriString(), null, Map.class);

        return response;
    }
}