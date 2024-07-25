package Graduation.Project.Tasaheel.services;

import org.springframework.security.core.userdetails.UserDetailsService;

import Graduation.Project.Tasaheel.models.User;
import Graduation.Project.Tasaheel.models.UserDTO;

public interface DefaultUserService extends UserDetailsService{
    User save(UserDTO userRegisteredDTO);

}