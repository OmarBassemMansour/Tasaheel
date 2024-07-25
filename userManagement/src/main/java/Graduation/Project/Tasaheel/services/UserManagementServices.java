package Graduation.Project.Tasaheel.services;

import Graduation.Project.Tasaheel.models.Interest;
import Graduation.Project.Tasaheel.models.User;
import Graduation.Project.Tasaheel.repos.InterestRepository;
import Graduation.Project.Tasaheel.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserManagementServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private userServices userService;

    @Transactional
    public String addInterestsToUser(Long userId, Set<String> interestNames) {

        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return "User not found";
            }

            User user = userOpt.get();

            Set<Interest> interests = interestNames.stream()
                    .map(name -> interestRepository.findByName(name)
                            .orElseThrow(() -> new RuntimeException("Interest not found: " + name)))
                    .collect(Collectors.toSet());

            user.getInterests().addAll(interests);

            userRepository.save(user);

            return "Interests added successfully";
        } catch (RuntimeException e) {
            return e.getMessage();  // Return the error message indicating which interest was not found
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while adding interests to the user.";
        }
    }

    public List<Interest> getAllInterests() {
        return interestRepository.findAll();
    }

    @Transactional
    public String removeInterestFromUser(Long userId, String interestName) {

        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return "User not found";
            }

            User user = userOpt.get();
            Optional<Interest> interestOpt = user.getInterests().stream()
                    .filter(interest -> interest.getName().equals(interestName))
                    .findFirst();

            if (!interestOpt.isPresent()) {
                return "Interest not found for user";
            }

            user.getInterests().remove(interestOpt.get());

            userRepository.save(user);

            return "Interest removed successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while removing interest from the user.";
        }
    }

    public Set<Interest> getInterestsForLoggedInUser(Long userId) {


        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        return user.getInterests();
    }

    public String addInterests(List<String> interestNames) {
        List<String> existingInterests = interestRepository.findAll()
                .stream()
                .map(Interest::getName)
                .collect(Collectors.toList());

        List<String> newInterests = interestNames.stream()
                .filter(name -> !existingInterests.contains(name))
                .collect(Collectors.toList());

        for (String interestName : newInterests) {
            Interest interest = new Interest();
            interest.setName(interestName);
            interestRepository.save(interest);
        }

        if (newInterests.isEmpty()) {
            return "All interests already exist";
        } else {
            return "Interests added successfully";
        }
    }
}
