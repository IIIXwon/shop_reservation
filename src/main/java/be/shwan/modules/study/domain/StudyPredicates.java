package be.shwan.modules.study.domain;

import be.shwan.modules.account.domain.Account;
import com.querydsl.core.types.Predicate;

public class StudyPredicates {
    public static Predicate findByManagers(Account account) {
        QStudy study = QStudy.study;
        return study.closed.isFalse().and(study.managers.any().in(account));
    }

    public static Predicate findByMembers(Account account) {
        QStudy study = QStudy.study;
        return study.published.isTrue().and(study.members.any().in(account));
    }
}
