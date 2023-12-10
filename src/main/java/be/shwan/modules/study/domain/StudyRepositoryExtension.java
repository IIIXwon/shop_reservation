package be.shwan.modules.study.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface StudyRepositoryExtension {
    Page<Study> findByKeyword(String keyword, Pageable pageable);
}
