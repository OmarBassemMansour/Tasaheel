package TasaheelMicroTwo.TasaheelMicroTwo.service;

import TasaheelMicroTwo.TasaheelMicroTwo.entity.Request;
import TasaheelMicroTwo.TasaheelMicroTwo.entity.RequestStatus;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static TasaheelMicroTwo.TasaheelMicroTwo.service.DateUtil.formatTimestamp;


@Service
public class RequestService {

    private static final String COLLECTION_NAME = "requests";


    public Request createRequest(Request request, MultipartFile[] images) throws ExecutionException, InterruptedException, IOException {
        // Generating a new Request ID
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference requests = db.collection(COLLECTION_NAME);
        int maxRequestId = getMaxRequestId(requests);
        request.setRequestId(maxRequestId + 1);
        request.setStatus(RequestStatus.PENDING);
        request.setTimestampNow(); // Set the current timestamp

        // Handling image upload if images are provided
        if (images != null && images.length > 0) {
            List<String> encodedImages = encodeImages(images);
            request.setImages(encodedImages); // Setting the encoded images to the request
        }

        // Constructing the request map for Firestore
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestId", request.getRequestId());
        requestMap.put("userId", request.getUserId());
        requestMap.put("title", request.getTitle());
        requestMap.put("description", request.getDescription());
        requestMap.put("category", request.getCategory());
        requestMap.put("suggestedAmount", request.getSuggestedAmount());
        requestMap.put("status", request.getStatus().toString());
        requestMap.put("address", request.getAddress());
        requestMap.put("paymentMethod", request.getPaymentMethod().toString());
        requestMap.put("timestamp", request.getTimestamp());
        requestMap.put("images", request.getImages()); // Save encoded images directly

        // Saving the request to Firestore
        requests.document(String.valueOf(request.getRequestId())).set(requestMap);

        return request;
    }

    // Helper method to encode images to Base64 strings
    private List<String> encodeImages(MultipartFile[] images) throws IOException {
        List<String> encodedImages = new ArrayList<>();
        for (MultipartFile file : images) {
            byte[] bytes = file.getBytes();
            String encoded = Base64.getEncoder().encodeToString(bytes);
            encodedImages.add(encoded);
        }
        return encodedImages;
    }


    // Get the current max requestId from Firestore
    private int getMaxRequestId(CollectionReference requests) throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = requests.get().get().getDocuments();
        return documents.stream()
                .map(doc -> doc.toObject(Request.class))
                .mapToInt(Request::getRequestId)
                .max()
                .orElse(0);
    }


    // Get a request by its ID with formatted timestamp
    public Request getRequestById(int requestId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        Request request = db.collection(COLLECTION_NAME).document(String.valueOf(requestId)).get().get().toObject(Request.class);

        if (request != null) {
            request.setFormattedDate(formatTimestamp(request.getTimestamp()));
        }

        return request;
    }



    // Retrieve all requests for a specific user with formatted dates
    public List<Request> getRequestsByUserId(int userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference requests = db.collection(COLLECTION_NAME);
        try {
            List<Request> result = requests.whereEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().get().getDocuments()
                    .stream()
                    .map(doc -> doc.toObject(Request.class))
                    .collect(Collectors.toList());
            result.forEach(request -> request.setFormattedDate(formatTimestamp(request.getTimestamp())));
            result.sort((r1, r2) -> r2.getFormattedDate().compareTo(r1.getFormattedDate()));
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Fail to get requests", e);
        }
    }

    public List<Request> getAllRequests() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference requests = db.collection(COLLECTION_NAME);
        try {
            List<Request> resultList = requests.orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp descending
                    .get().get().getDocuments()
                    .stream()
                    .map(doc -> doc.toObject(Request.class))
                    .collect(Collectors.toList());
            resultList.forEach(request -> request.setFormattedDate(formatTimestamp(request.getTimestamp()))); // Set formatted date
            return resultList;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to get all requests", e);
        }
    }


    // Retrieve all the titles with their formatted timestamps
    public List<Map<String, String>> getAllRequestTitles() {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference requests = db.collection(COLLECTION_NAME);
        try {
            return requests.orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp
                    .get().get().getDocuments()
                    .stream()
                    .map(doc -> {
                        Request request = doc.toObject(Request.class);
                        Map<String, String> titleWithDate = new HashMap<>();
                        titleWithDate.put("title", request.getTitle());
                        titleWithDate.put("formattedDate", formatTimestamp(request.getTimestamp())); // Assuming formatTimestamp() is defined
                        return titleWithDate;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Fail to get request titles", e);
        }
    }


    // Update Request
    public Request updateRequest(int requestId, int userId, Request updatedRequest) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference requests = db.collection(COLLECTION_NAME);

        Request existingRequest = requests.document(String.valueOf(requestId)).get().get().toObject(Request.class);

        if (existingRequest == null || existingRequest.getUserId() != userId) {
            throw new IllegalStateException("Unauthorized to update this request or request does not exist.");
        }

        if (updatedRequest.getTitle() != null) {
            existingRequest.setTitle(updatedRequest.getTitle());
        }
        if (updatedRequest.getDescription() != null) {
            existingRequest.setDescription(updatedRequest.getDescription());
        }
        if (updatedRequest.getCategory() != null) {
            existingRequest.setCategory(updatedRequest.getCategory());
        }
        if (updatedRequest.getSuggestedAmount() != 0) {
            existingRequest.setSuggestedAmount(updatedRequest.getSuggestedAmount());
        }
        if (updatedRequest.getAddress() != null) {
            existingRequest.setAddress(updatedRequest.getAddress());
        }
        if (updatedRequest.getPaymentMethod() != null) {
            existingRequest.setPaymentMethod(updatedRequest.getPaymentMethod());
        }

        requests.document(String.valueOf(requestId)).set(existingRequest);
        return existingRequest;
    }


    public void deleteRequest(int requestId, int userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference requestRef = db.collection(COLLECTION_NAME).document(String.valueOf(requestId));

        // Retrieve the request to check its existence and authorization
        DocumentSnapshot requestDocument = requestRef.get().get();
        if (!requestDocument.exists()) {
            throw new IllegalStateException("Request does not exist.");
        }

        Request existingRequest = requestDocument.toObject(Request.class);
        if (existingRequest == null || existingRequest.getUserId() != userId) {
            throw new IllegalStateException("Unauthorized to delete this request.");
        }

        // Query and delete all offers associated with this request
        CollectionReference offers = db.collection("Offers");
        QuerySnapshot offersSnapshot = offers.whereEqualTo("requestId", requestId).get().get();
        for (DocumentSnapshot offerSnapshot : offersSnapshot.getDocuments()) {
            deleteServicesAssociatedWithOffer(db, offerSnapshot.getId());
            offers.document(offerSnapshot.getId()).delete().get(); // Ensure each delete completes
        }
        // Query and delete all services directly associated with this request if applicable
        CollectionReference services = db.collection("Services");
        QuerySnapshot servicesSnapshot = services.whereEqualTo("requestId", requestId).get().get();
        for (DocumentSnapshot serviceSnapshot : servicesSnapshot.getDocuments()) {
            services.document(serviceSnapshot.getId()).delete().get();
        }

        // Delete the request after all associated offers have been handled
        requestRef.delete().get();
    }

    private void deleteServicesAssociatedWithOffer(Firestore db, String offerId) throws ExecutionException, InterruptedException {
        CollectionReference services = db.collection("Services");
        QuerySnapshot serviceQuery = services.whereEqualTo("offerId", offerId).get().get();
        for (DocumentSnapshot serviceSnapshot : serviceQuery.getDocuments()) {
            services.document(serviceSnapshot.getId()).delete().get();
        }
    }

    // Search by category with formatted dates (case-insensitive and partial matches)
    public List<Request> searchRequestsByCategory(String category) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference requests = db.collection(COLLECTION_NAME);
        try {
            // Fetch all documents
            List<Request> allRequests = requests.get().get().getDocuments()
                    .stream()
                    .map(doc -> doc.toObject(Request.class))
                    .collect(Collectors.toList());

            // Filter documents by category (case-insensitive and partial matches)
            List<Request> filteredRequests = allRequests.stream()
                    .filter(request -> request.getCategory().toLowerCase().contains(category.toLowerCase()))
                    .collect(Collectors.toList());

            // Sort the filtered documents by timestamp in descending order
            filteredRequests.sort((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));

            // Format the timestamps for the filtered documents
            filteredRequests.forEach(request -> request.setFormattedDate(formatTimestamp(request.getTimestamp())));

            return filteredRequests;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Fail to search requests by category", e);
        }
    }


    // Retrieve all requests with status PENDING, ordered by timestamp with formatted dates
    public List<Request> getRequestsByStatusPending() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference requests = db.collection(COLLECTION_NAME);
        try {
            List<Request> result = requests.whereEqualTo("status", RequestStatus.PENDING.toString())
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().get().getDocuments()
                    .stream()
                    .map(doc -> doc.toObject(Request.class))
                    .collect(Collectors.toList());
            result.forEach(request -> request.setFormattedDate(formatTimestamp(request.getTimestamp())));
            return result;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Fail to get requests by status pending", e);
        }
    }


    // Update Request Status
    public void updateRequestStatus(int requestId, RequestStatus status) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference requests = db.collection(COLLECTION_NAME);
        Request request = requests.document(String.valueOf(requestId)).get().get().toObject(Request.class);
        if (request != null) {
            request.setStatus(status);
            requests.document(String.valueOf(requestId)).set(request);
        }
    }

}