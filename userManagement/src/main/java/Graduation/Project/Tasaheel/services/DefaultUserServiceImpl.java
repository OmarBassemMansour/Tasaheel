package Graduation.Project.Tasaheel.services;

import java.util.Collection;
import java.util.List;

import Graduation.Project.Tasaheel.models.acceptedByAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import Graduation.Project.Tasaheel.models.Role;
import Graduation.Project.Tasaheel.models.User;
import Graduation.Project.Tasaheel.models.UserDTO;
import Graduation.Project.Tasaheel.repos.RoleRepository;
import Graduation.Project.Tasaheel.repos.UserRepository;

@Service
public class DefaultUserServiceImpl implements DefaultUserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRole())
        );
    }

    public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority(role.getRole()));
    }

    @Override
    public User save(UserDTO userRegisteredDTO) {

        Role role = roleRepo.findByRole(userRegisteredDTO.getRole());
        if (role == null) {
            role = new Role(userRegisteredDTO.getRole());
            roleRepo.save(role);
        }

        User user = new User();
        user.setEmail(userRegisteredDTO.getEmail());
        user.setUsername(userRegisteredDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
        user.setCity(userRegisteredDTO.getCity());
        user.setCountry(userRegisteredDTO.getCountry());
        user.setNationalId(userRegisteredDTO.getNationalId());
        user.setPhoneNb(userRegisteredDTO.getPhoneNb());
        user.setGender(userRegisteredDTO.getGender());
        user.setRole(role);
        user.setRate(5.0);
        user.setAcceptedByAdmin(acceptedByAdmin.pending);

        return userRepo.save(user);
    }
}