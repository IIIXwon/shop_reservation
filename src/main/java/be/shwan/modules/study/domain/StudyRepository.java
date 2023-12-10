package be.shwan.modules.study.domain;

import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryExtension {
    boolean existsByPath(String path);

    @EntityGraph(attributePaths = {"managers", "members", "tags", "zones"}, type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(attributePaths = {"tags", "managers"})
    Study findStudyWithTagByPath(String path);

    @EntityGraph(attributePaths = {"zones", "managers"})
    Study findStudyWithZoneByPath(String path);

    @EntityGraph(attributePaths = {"managers"})
    Study findStudyWithManagerByPath(String path);

    @EntityGraph(attributePaths = {"members", "managers"})
    Study findStudyWithMembersAndManagersByPath(String path);

    Study findStudyOnlyByPath(String path);

    @EntityGraph(attributePaths = {"tags", "zones"})
    Study findStudyWithTagsAndZonesById(Long id);

    List<Study> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);
}
