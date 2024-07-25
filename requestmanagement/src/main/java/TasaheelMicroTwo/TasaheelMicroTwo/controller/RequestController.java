package TasaheelMicroTwo.TasaheelMicroTwo.controller;

import TasaheelMicroTwo.TasaheelMicroTwo.entity.PaymentMethod;
import TasaheelMicroTwo.TasaheelMicroTwo.entity.Request;  // Correct import
import TasaheelMicroTwo.TasaheelMicroTwo.entity.RequestStatus;
import TasaheelMicroTwo.TasaheelMicroTwo.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class RequestController {

    @Autowired
    private RequestService requestService;

//    // Create a Request for a specific user
//    @PostMapping("/users/{userId}/requests")
//    public Request createRequest(@PathVariable int userId, @RequestBody Request request) throws ExecutionException, InterruptedException {
//        request.setUserId(userId);
//        return requestService.saveRequest(request);
//    }


//    // Create a Request for a specific user with image uploads
//    @PostMapping("/users/{userId}/requests")
//    public Request createRequest(@PathVariable int userId,
//                                 @RequestBody Request request,
//                                 @RequestParam("images") List<MultipartFile> images) throws ExecutionException, InterruptedException, IOException {
//        request.setUserId(userId);
//        return requestService.saveRequest(request, images);
//    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<?> createRequest(@PathVariable int userId,
                                           @RequestParam("title") String title,
                                           @RequestParam("description") String description,
                                           @RequestParam("category") String category,
                                           @RequestParam("suggestedAmount") int suggestedAmount,
                                           @RequestParam("address") String address,
                                           @RequestParam("paymentMethod") String paymentMethod,
                                           @RequestParam(value = "images", required = false) MultipartFile[] images) {
        try {
            // Prepare and save the request
            Request request = new Request();
            request.setUserId(userId);
            request.setTitle(title);
            request.setDescription(description);
            request.setCategory(category);
            request.setSuggestedAmount(suggestedAmount);
            request.setAddress(address);
            request.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
            request.setStatus(RequestStatus.PENDING); // Assuming you have a default status or it's set inside the service

            // Call service to save request
            Request savedRequest = requestService.createRequest(request, images);

            return ResponseEntity.ok(savedRequest);
        } catch (IllegalArgumentException e) {
            // This handles incorrect enum conversion or any other argument issues
            return ResponseEntity.badRequest().body("Error in request parameters: " + e.getMessage());
        } catch (Exception e) {
            // Generic exception handler for other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating request: " + e.getMessage());
        }
    }


    // Retrieve all requests for a specific user
    @GetMapping("/users/{userId}/requests")
    public List<Request> getUserRequests(@PathVariable int userId) throws ExecutionException, InterruptedException {
        return requestService.getRequestsByUserId(userId);
    }

    // Retrieve all requests in the database
    @GetMapping("/allrequests")
    public List<Request> getAllRequests() throws ExecutionException, InterruptedException {
        return requestService.getAllRequests();
    }

    @GetMapping("/requests/alltitles")
    public List<Map<String, String>> getAllRequestTitles() {
        return requestService.getAllRequestTitles();
    }


    // Update request endpoint
    @PutMapping("/requests/{requestId}")
    public ResponseEntity<?> updateRequest(@PathVariable int requestId, @RequestParam int userId, @RequestBody Request updatedRequest) throws ExecutionException, InterruptedException {
        try {
            Request updated = requestService.updateRequest(requestId, userId, updatedRequest);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // Delete request endpoint
    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<?> deleteRequest(@PathVariable int requestId, @RequestParam int userId) throws ExecutionException, InterruptedException {
        try {
            requestService.deleteRequest(requestId, userId);
            return ResponseEntity.ok("Request deleted successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }


    @GetMapping("/requests/search")
    public List<Request> searchRequestsByCategory(@RequestParam String category) throws ExecutionException, InterruptedException {
        return requestService.searchRequestsByCategory(category);
    }

    // Get a request by its ID
    @GetMapping("/requests/{requestId}")
    public Request getRequestById(@PathVariable int requestId) throws ExecutionException, InterruptedException {
        return requestService.getRequestById(requestId);
    }

    // Retrieve all requests with status 0
    @GetMapping("/requests/status/pending")
    public List<Request> getRequestsByStatusPending() throws ExecutionException, InterruptedException {
        return requestService.getRequestsByStatusPending();
    }
}
