package be.shwan.study.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long> {
    boolean existsByPath(String path);

    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(value = "Study.tagAndManager", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithTagByPath(String path);

    @EntityGraph(value = "Study.zoneAndManager", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithZoneByPath(String path);

    @EntityGraph(value = "Study.manager", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithManagerByPath(String path);

}
