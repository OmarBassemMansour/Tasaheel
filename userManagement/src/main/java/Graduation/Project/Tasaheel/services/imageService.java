package Graduation.Project.Tasaheel.services;

import Graduation.Project.Tasaheel.models.Image;
import Graduation.Project.Tasaheel.models.User;
import Graduation.Project.Tasaheel.repos.UserRepository;
import Graduation.Project.Tasaheel.repos.imageRepository;
import Graduation.Project.Tasaheel.models.imageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class imageService {

    @Autowired
    private imageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;


    public void uploadProfilePicture(MultipartFile file, Long userId) throws IOException {

       Optional<User> user = userRepository.findById(userId);

       if (!user.isPresent()) {
           throw new RuntimeException("User not found");
       }

        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file");
        }
        User newUser = user.get();

        newUser.setProfileImage(encodeImage(file));
        userRepository.save(newUser);
    }

    public void uploadFrontId (MultipartFile file, Long userId) throws IOException {

        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }

        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file");
        }
        User newUser = user.get();

        newUser.setFrontId(encodeImage(file));
        userRepository.save(newUser);
    }


    public void uploadBackId (MultipartFile file, Long userId) throws IOException {

        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }

        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file");
        }
        User newUser = user.get();

        newUser.setBackId(encodeImage(file));
        userRepository.save(newUser);
    }



    private String encodeImage(MultipartFile image) throws IOException {
        byte[] bytes = image.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public String changeProfilePicture(MultipartFile file, Long userId) throws IOException {
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) {
            throw new RuntimeException("User not found");
        }

        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file");
        }
        User newUser = user.get();
        newUser.setProfileImage(encodeImage(file));
        userRepository.save(newUser);
        return "Profile Picture Changed Successfully";
    }


}