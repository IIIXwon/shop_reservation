package be.shwan.modules.study.domain;

import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.zone.domain.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface StudyRepositoryExtension {
    Page<Study> findByKeyword(String keyword, Pageable pageable);

    List<Study> findDefault();

    List<Study> findStudyListWithTagsAndZonesByAccount(Set<Tag> tags, Set<Zone> zones);
}
