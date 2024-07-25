package Graduation.Project.Tasaheel.repos;
import Graduation.Project.Tasaheel.models.Image;
import Graduation.Project.Tasaheel.models.imageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface imageRepository extends JpaRepository<Image, Long> {

    // Find all images by user ID
    Image findByUserIdAndType(Long userId , imageType type);

    // Find all images by user ID and post ID
    List<Image> findByUserIdAndPostId(Long userId, Long postId);

}
