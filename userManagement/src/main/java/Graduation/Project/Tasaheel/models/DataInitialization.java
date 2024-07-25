package Graduation.Project.Tasaheel.models;

import Graduation.Project.Tasaheel.models.Interest;
import Graduation.Project.Tasaheel.repos.InterestRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitialization {

    @Autowired
    private InterestRepository interestRepository;

    @PostConstruct
    public void initialize() {
        List<String> hardcodedInterests = Arrays.asList(
                "Plumbing",
                "Electricity",
                "Painting",
                "Cleaning",
                "Wood",
                "Lifting",
                "Teaching"
        );

        for (String interestName : hardcodedInterests) {
            if (!interestRepository.findByName(interestName).isPresent()) {
                Interest interest = new Interest();
                interest.setName(interestName);
                interestRepository.save(interest);
            }
        }
    }
}
