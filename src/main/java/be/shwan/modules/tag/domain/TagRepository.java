package be.shwan.modules.tag.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByTitle(String title);

    Tag findByTitle(String title);
}
