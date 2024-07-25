package Graduation.Project.Tasaheel.repos;

import Graduation.Project.Tasaheel.models.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Optional<Interest> findByName(String name);
}
