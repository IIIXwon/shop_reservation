package be.shwan.modules.account.domain;

import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.zone.domain.Zone;
import com.querydsl.core.types.Predicate;

import java.util.Set;

public class AccountPredicates {
    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).or(account.tags.any().in(tags));
    }
}
