package be.shwan.modules.event.domain.impl;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.event.domain.Enrollment;
import be.shwan.modules.event.domain.EnrollmentRepositoryExtension;
import be.shwan.modules.event.domain.QEnrollment;
import be.shwan.modules.event.domain.QEvent;
import be.shwan.modules.study.domain.QStudy;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class EnrollmentRepositoryExtensionImpl extends QuerydslRepositorySupport implements EnrollmentRepositoryExtension {
    public EnrollmentRepositoryExtensionImpl() {
        super(Enrollment.class);
    }

    @Override
    public List<Enrollment> findAcceptedEnrollmentByAccount(Account account) {
        QEnrollment enrollment = QEnrollment.enrollment;
        QEvent event = QEvent.event;
        QStudy study = QStudy.study;
        JPQLQuery<Enrollment> query = from(enrollment).where(enrollment.accepted.isTrue().and(study.closed.isFalse())
                .and(enrollment.account.eq(account)))
                .leftJoin(enrollment.event, event).fetchJoin()
                .leftJoin(event.study, study).fetchJoin()
                .distinct()
                .orderBy(enrollment.enrollAt.desc())
                .limit(4)
                ;
        return query.fetch();
    }
}
