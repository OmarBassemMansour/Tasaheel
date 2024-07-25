package TasaheelMicroTwo.TasaheelMicroTwo.service;

import TasaheelMicroTwo.TasaheelMicroTwo.dto.SaveOfferDTO;
import TasaheelMicroTwo.TasaheelMicroTwo.dto.ServiceDTO;
import TasaheelMicroTwo.TasaheelMicroTwo.entity.Offer;
import TasaheelMicroTwo.TasaheelMicroTwo.entity.OfferStatus;
import TasaheelMicroTwo.TasaheelMicroTwo.entity.Request;
import TasaheelMicroTwo.TasaheelMicroTwo.entity.RequestStatus;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.Optional;


import static TasaheelMicroTwo.TasaheelMicroTwo.service.DateUtil.formatTimestamp;

@Service
public class OfferService {

    private static final String COLLECTION_NAME = "Offers";
    @Autowired
    private RequestService requestService;
    @Autowired
    private ServiceManagementService serviceManagementService;

    public Optional<Offer> createOffer(SaveOfferDTO saveOfferDTO) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference offers = db.collection(COLLECTION_NAME);
        CollectionReference requests = db.collection("requests");  // Assuming the collection name for requests

        // Check the status of the request
        Request request = requests.document(String.valueOf(saveOfferDTO.getRequestId()))
                .get().get().toObject(Request.class);
        if (request == null || request.getStatus() == RequestStatus.TAKEN_BY_USER) {
            return Optional.empty(); // Request is not available for new offers
        }

        // Check if the provider has already made an offer for this request
        List<Offer> existingOffers = offers.whereEqualTo("requestId", saveOfferDTO.getRequestId())
                .whereEqualTo("providerId", saveOfferDTO.getProviderId())
                .get().get().getDocuments()
                .stream()
                .map(document -> document.toObject(Offer.class))
                .collect(Collectors.toList());

        if (!existingOffers.isEmpty()) {
            return Optional.empty();  // Provider has already made an offer
        }

        // Create a new offer if no existing offer is found
        Offer offer = new Offer();
        int maxOfferId = getMaxOfferId(offers);
        offer.setOfferId(maxOfferId + 1);
        offer.setOfferStatus(OfferStatus.PENDING);
        offer.setProviderId(saveOfferDTO.getProviderId());
        offer.setRequestId(saveOfferDTO.getRequestId());
        offer.setOfferValue(saveOfferDTO.getOfferValue());
        offer.setTimestamp(Timestamp.now());
        offer.setFormattedDate(formatTimestamp(offer.getTimestamp()));
        offers.document(String.valueOf(offer.getOfferId())).set(offer);
        return Optional.of(offer);
    }


    // get the current max OfferId from Firestore
    private int getMaxOfferId(CollectionReference offers) throws ExecutionException, InterruptedException {
        List<Offer> offerList = offers.get().get().getDocuments()
                .stream()
                .map(doc -> doc.toObject(Offer.class))
                .collect(Collectors.toList());

        return offerList.stream()
                .mapToInt(Offer::getOfferId)
                .max()
                .orElse(0);
    }

    // Get offers by requestId with formatted dates in ascending order
    public List<Offer> getOffersByRequestId(int requestId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference offers = db.collection(COLLECTION_NAME);
        List<Offer> result = offers.whereEqualTo("requestId", requestId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get().get().getDocuments()
                .stream()
                .map(doc -> doc.toObject(Offer.class))
                .collect(Collectors.toList());
        result.forEach(offer -> offer.setFormattedDate(formatTimestamp(offer.getTimestamp())));
        return result;
    }


    public Offer acceptOffer(int offerId, int requestId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference offers = db.collection("Offers");

        // Retrieve the current status of the request to ensure it is still pending
        Request correspondingRequest = requestService.getRequestById(requestId);
        if (correspondingRequest == null || correspondingRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("This request is no longer pending and cannot accept new offers.");
        }

        // Retrieve the offer to be accepted
        Offer acceptedOffer = offers.document(String.valueOf(offerId)).get().get().toObject(Offer.class);
        if (acceptedOffer != null && acceptedOffer.getRequestId() == requestId && acceptedOffer.getOfferStatus() == OfferStatus.PENDING) {
            // Set the offer status to ACCEPTED
            acceptedOffer.setOfferStatus(OfferStatus.ACCEPTED);
            offers.document(String.valueOf(offerId)).set(acceptedOffer);

            // Update all other offers to status Rejected
            updateOtherOffersStatus(offers, requestId, offerId);

            // Create a DTO for the service creation
            ServiceDTO serviceDTO = new ServiceDTO();
            serviceDTO.setUserId(correspondingRequest.getUserId());
            serviceDTO.setProviderId(acceptedOffer.getProviderId());
            serviceDTO.setOfferId(acceptedOffer.getOfferId());
            serviceDTO.setRequestId(correspondingRequest.getRequestId());

            // Create service since the offer is being accepted
            ServiceManagementService serviceManagementService = new ServiceManagementService();  // Assume this service is autowired or appropriately instantiated
            serviceManagementService.createService(serviceDTO);

            // Update the request's status to TAKEN_BY_USER
            requestService.updateRequestStatus(requestId, RequestStatus.TAKEN_BY_USER);

            return acceptedOffer;
        } else {
            throw new IllegalStateException("Offer cannot be accepted. It may have been accepted already, does not exist, or is not pending.");
        }
    }

    // Helper method to set other offers' statuses to REJECTED
    private void updateOtherOffersStatus(CollectionReference offers, int requestId, int acceptedOfferId) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> allOffers = offers.whereEqualTo("requestId", requestId).get().get().getDocuments();
        for (QueryDocumentSnapshot document : allOffers) {
            Offer offer = document.toObject(Offer.class);
            if (offer.getOfferId() != acceptedOfferId && offer.getOfferStatus() == OfferStatus.PENDING) {
                offer.setOfferStatus(OfferStatus.REJECTED);
                offers.document(String.valueOf(offer.getOfferId())).set(offer);
            }
        }
    }


    public Optional<Offer> updateOfferValue(int offerId, float newOfferValue , int providerId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference offers = db.collection(COLLECTION_NAME);
        DocumentSnapshot document = offers.document(String.valueOf(offerId)).get().get();

        if (!document.exists()) {
            return Optional.empty(); // Offer does not exist
        }

        Offer offer = document.toObject(Offer.class);
        if (offer.getOfferStatus() == OfferStatus.ACCEPTED) {
            // If offer is accepted, do not allow updates
            return Optional.empty(); // Update not allowed if the offer is accepted
        }

        if (offer.getProviderId() != providerId) {
            return Optional.empty(); // Unauthorized to update
        }

        // Proceed with updating the offer value if it's not accepted
        offer.setOfferValue(newOfferValue);
        offers.document(String.valueOf(offer.getOfferId())).set(offer);
        return Optional.of(offer);
    }



    public boolean deleteOffer(int offerId, int providerId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference offerRef = db.collection("Offers").document(String.valueOf(offerId));
        DocumentSnapshot offerSnapshot = offerRef.get().get();

        if (!offerSnapshot.exists()) {
            System.out.println("Offer does not exist.");
            return false;
        }

        Offer offer = offerSnapshot.toObject(Offer.class);
        if (offer.getProviderId() != providerId) {
            System.out.println("Unauthorized to delete this offer.");
            return false;
        }

        // Process to delete the offer
        offerRef.delete().get();  // Synchronously wait for deletion to complete

        if (offer.getOfferStatus() == OfferStatus.ACCEPTED) {
            serviceManagementService.deleteServiceByOfferId(offerId);
            DocumentReference requestRef = db.collection("requests").document(String.valueOf(offer.getRequestId()));
            DocumentSnapshot requestSnapshot = requestRef.get().get();
            Request request = requestSnapshot.toObject(Request.class);

            // Check and reset other offers
            CollectionReference offersCollection = db.collection("Offers");
            ApiFuture<QuerySnapshot> querySnapshot = offersCollection.whereEqualTo("requestId", offer.getRequestId()).get();
            boolean hasOtherAcceptedOffers = false;

            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                Offer otherOffer = doc.toObject(Offer.class);
                if (otherOffer.getOfferId() != offerId && otherOffer.getOfferStatus() == OfferStatus.ACCEPTED) {
                    hasOtherAcceptedOffers = true;
                } else {
                    // Reset offer status to PENDING
                    doc.getReference().update("offerStatus", OfferStatus.PENDING);
                }
            }

            if (!hasOtherAcceptedOffers) {
                // No other accepted offers, update request status
                requestRef.update("status", RequestStatus.PENDING);
            }
        }
        return true;
    }

    public Optional<Offer> getOfferById(int offerId, int providerId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference offers = db.collection(COLLECTION_NAME);
        DocumentSnapshot document = offers.document(String.valueOf(offerId)).get().get();

        if (!document.exists()) {
            System.out.println("Offer does not exist.");
            return Optional.empty();
        }

        Offer offer = document.toObject(Offer.class);
        if (offer != null && offer.getProviderId() == providerId) {
            offer.setFormattedDate(DateUtil.formatTimestamp(offer.getTimestamp())); // Formatting the date if required
            return Optional.of(offer);
        } else {
            System.out.println("Unauthorized access or offer does not match provider.");
            return Optional.empty();
        }
    }
    // Method to retrieve all offers made by a specific provider
    public List<Offer> getAllOffersByProvider(int providerId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference offers = db.collection(COLLECTION_NAME);

        List<Offer> result = offers.whereEqualTo("providerId", providerId)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Retrieve offers in descending order by timestamp
                .get().get().getDocuments()
                .stream()
                .map(document -> document.toObject(Offer.class))
                .collect(Collectors.toList());

        result.forEach(offer -> offer.setFormattedDate(formatTimestamp(offer.getTimestamp()))); // Setting formatted date for each offer
        return result;
    }

}
