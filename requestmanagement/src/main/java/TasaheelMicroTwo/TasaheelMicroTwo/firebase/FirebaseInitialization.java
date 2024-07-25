package TasaheelMicroTwo.TasaheelMicroTwo.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.FileInputStream;


@Service
public class FirebaseInitialization {


    @PostConstruct
    public void initialization(){
        FileInputStream serviceAccount =
                null;
        try {
            serviceAccount = new FileInputStream("D:/Computer science/Tasaheel/TasaheelMicroTwo/TasaheelMicroTwo/serviceAccountKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}