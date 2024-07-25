package TasaheelMicroTwo.TasaheelMicroTwo.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutionException;

import TasaheelMicroTwo.TasaheelMicroTwo.dto.ServiceDTO;
import TasaheelMicroTwo.TasaheelMicroTwo.entity.ServiceRecord;

@Service
public class ServiceManagementService {

    private static final String COLLECTION_NAME = "Services";


    public ServiceRecord createService(ServiceDTO serviceDTO) throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();
        CollectionReference services = db.collection(COLLECTION_NAME);

        ServiceRecord service = new ServiceRecord();
        service.setUserId(serviceDTO.getUserId());
        service.setProviderId(serviceDTO.getProviderId());
        service.setOfferId(serviceDTO.getOfferId());
        service.setRequestId(serviceDTO.getRequestId());
        int maxServiceId = getMaxServiceId(services);
        service.setServiceId(maxServiceId + 1);
        services.document(String.valueOf(service.getServiceId())).set(service);
        return service;
    }

    private int getMaxServiceId(CollectionReference services) throws ExecutionException, InterruptedException {
        return services.get().get().getDocuments().stream()
                .mapToInt(doc -> {
                    ServiceRecord service = doc.toObject(ServiceRecord.class);
                    return service.getServiceId();
                })
                .max()
                .orElse(0);
    }

    public void deleteServiceByOfferId(int offerId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference services = db.collection("Services");
        services.whereEqualTo("offerId", offerId).get().get().getDocuments().forEach(doc -> {
            services.document(doc.getId()).delete();
        });
    }

}
