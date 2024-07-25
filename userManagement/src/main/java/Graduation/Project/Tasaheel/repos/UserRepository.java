package Graduation.Project.Tasaheel.repos;

import Graduation.Project.Tasaheel.models.acceptedByAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import Graduation.Project.Tasaheel.models.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

     Optional<User> findByEmail(String email);

     Optional<User> findByUsername(String name);

     List<User> findByAcceptedByAdmin(acceptedByAdmin status);


}

