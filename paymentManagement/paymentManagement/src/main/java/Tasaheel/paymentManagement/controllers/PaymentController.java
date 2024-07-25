package Tasaheel.paymentManagement.controllers;
import Tasaheel.paymentManagement.models.PaymentMethod;
import Tasaheel.paymentManagement.models.Transaction;
import Tasaheel.paymentManagement.repositories.WalletRepository;
import Tasaheel.paymentManagement.services.AuthService;
import Tasaheel.paymentManagement.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AuthService authService;


    @PostMapping("/initiateWallet/{userId}")
    public void initiateWallet(@PathVariable Long userId) {
        // Initiate wallet
        paymentService.initiateWallet(userId);
    }


    @PostMapping("/initiateTransaction")
    public ResponseEntity<String> initiateTransaction(@RequestParam Long senderId,
                                                      @RequestParam Long receiverId,
                                                      @RequestParam int amount ,
                                                      @RequestParam PaymentMethod paymentMethod) {

         return paymentService.initiateTransaction(senderId, receiverId, amount, paymentMethod);
    }

    @PostMapping("/ProcessTransaction/{transid}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> processTransaction(@RequestParam boolean approval1 ,
                                                     @PathVariable Long transid ,
                                                     @RequestHeader("Authorization") String bearerToken)
    {
        String token = bearerToken.replace("Bearer ", "");
        ResponseEntity<Map> validationResponse = authService.validateToken(token);
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            throw new ResponseStatusException(validationResponse.getStatusCode(), "Token validation failed");
        }

        // Extract roles and verify the required role
        Map<String, Object> body = validationResponse.getBody();
        List<String> roles = (List<String>) body.get("roles");
        if (!roles.contains("ROLE_USER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role");
        }

        return paymentService.processTransaction(approval1,transid);
    }

    @PostMapping ("/RefundtoWallet")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> RefundtoWallet(@RequestBody Transaction transaction ,@RequestParam int amount  ,@RequestHeader("Authorization") String bearerToken)
    {
        String token = bearerToken.replace("Bearer ", "");
        ResponseEntity<Map> validationResponse = authService.validateToken(token);
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            throw new ResponseStatusException(validationResponse.getStatusCode(), "Token validation failed");
        }

        // Extract roles and verify the required role
        Map<String, Object> body = validationResponse.getBody();
        List<String> roles = (List<String>) body.get("roles");
        if (!roles.contains("ROLE_USER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized role");
        }

        return paymentService.refundToWallet(transaction, amount);
    }


    @GetMapping ("/GetBalance/{userId}")
    public int GetWalletBalance(@PathVariable Long userId )
    {
        return walletRepository.findByUserId(userId).getBalance();
    }

}
