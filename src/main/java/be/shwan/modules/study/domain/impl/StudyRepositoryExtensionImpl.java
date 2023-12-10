package be.shwan.modules.study.domain.impl;

import be.shwan.modules.account.domain.QAccount;
import be.shwan.modules.study.domain.QStudy;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepositoryExtension;
import be.shwan.modules.tag.domain.QTag;
import be.shwan.modules.zone.domain.QZone;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Objects;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {

    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                .and(study.title.containsIgnoreCase(keyword))
                .or(study.published.isTrue().and(study.tags.any().title.containsIgnoreCase(keyword))
                .or(study.published.isTrue().and(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .distinct()
                ;
        JPQLQuery<Study> pageableQuery = Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);
        QueryResults<Study> studyQueryResults = pageableQuery.fetchResults();
        return new PageImpl<>(studyQueryResults.getResults(), pageable, studyQueryResults.getTotal());
    }

    @Override
    public List<Study> findDefault() {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue().and(study.closed.isFalse()))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .orderBy(study.publishedDateTime.desc())
                .limit(9L);
        return query.fetch();
    }
}
