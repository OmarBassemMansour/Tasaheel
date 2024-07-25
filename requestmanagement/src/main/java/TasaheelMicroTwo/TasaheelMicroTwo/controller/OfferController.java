package TasaheelMicroTwo.TasaheelMicroTwo.controller;

import TasaheelMicroTwo.TasaheelMicroTwo.dto.SaveOfferDTO;
import TasaheelMicroTwo.TasaheelMicroTwo.entity.Offer;
import TasaheelMicroTwo.TasaheelMicroTwo.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
@RestController
@RequestMapping("/Offer")
public class OfferController {

        @Autowired
        private OfferService offerService;

        @PostMapping("/makeOffer")
        public ResponseEntity<String> makeAnOffer(@RequestBody SaveOfferDTO saveOfferDTO) throws ExecutionException, InterruptedException {
                Optional<Offer> offerResult = offerService.createOffer(saveOfferDTO);

                if (!offerResult.isPresent()) {
                        return ResponseEntity.badRequest().body("No offer created. Either the request is taken, or you have already made an offer.");
                }

                return ResponseEntity.ok("Offer created successfully");
        }


        //Get offers for a specific request
        @GetMapping("/offers/{requestId}")
        public List<Offer> getOffersByRequestId(@PathVariable int requestId) throws ExecutionException, InterruptedException {
                return offerService.getOffersByRequestId(requestId);
        }

        @PutMapping("/offers/{requestId}/accept/{offerId}")
        public ResponseEntity<?> acceptOffer(@PathVariable int requestId, @PathVariable int offerId) {
                try {
                        Offer acceptedOffer = offerService.acceptOffer(offerId, requestId);
                        return ResponseEntity.ok(acceptedOffer);
                } catch (IllegalStateException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                } catch (ExecutionException | InterruptedException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't accept offer");
                }
        }

        @PutMapping("/offers/{offerId}/value/{providerId}")
        public ResponseEntity<?> updateOfferValue(@PathVariable int offerId, @PathVariable int providerId, @RequestParam float newOfferValue) throws ExecutionException, InterruptedException {
                Optional<Offer> updatedOffer = offerService.updateOfferValue(offerId, newOfferValue, providerId);

                if (!updatedOffer.isPresent()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to update the offer.");
                }

                return ResponseEntity.ok(updatedOffer.get());
        }


        @DeleteMapping("/offers/{offerId}/{providerId}")
        public ResponseEntity<?> deleteOffer(@PathVariable int offerId, @PathVariable int providerId) throws ExecutionException, InterruptedException {
                boolean success = offerService.deleteOffer(offerId, providerId);

                if (!success) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete this offer or it does not exist.");
                }

                return ResponseEntity.ok().body("Offer deleted successfully.");
        }

        @GetMapping("/offer/{offerId}/{providerId}")
        public ResponseEntity<?> getOfferById(@PathVariable int offerId, @PathVariable int providerId) {
                try {
                        Optional<Offer> offer = offerService.getOfferById(offerId, providerId);
                        if (offer.isPresent()) {
                                return ResponseEntity.ok(offer.get());
                        } else {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Offer not found or access denied.");
                        }
                } catch (ExecutionException | InterruptedException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving the offer.");
                }
        }

        @GetMapping("/providerOffers/{providerId}")
        public ResponseEntity<?> getOffersByProvider(@PathVariable int providerId) {
                try {
                        List<Offer> offers = offerService.getAllOffersByProvider(providerId);
                        if (offers.isEmpty()) {
                                return ResponseEntity.noContent().build(); // No offers found
                        }
                        return ResponseEntity.ok(offers); // Successful retrieval
                } catch (ExecutionException e) {
                        // Log or return the stack trace of the ExecutionException
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().body("Error due to execution exception.");
                } catch (InterruptedException e) {
                        // Handle the InterruptedException specifically
                        Thread.currentThread().interrupt(); // Set interrupt flag
                        return ResponseEntity.internalServerError().body("Operation was interrupted.");
                } catch (Exception e) {
                        // General catch block for other exceptions
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().body("An unexpected error occurred.");
                }
        }

}
