package be.shwan.modules.account.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccount is a Querydsl query type for Account
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccount extends EntityPathBase<Account> {

    private static final long serialVersionUID = -1474647243L;

    public static final QAccount account = new QAccount("account");

    public final BooleanPath active = createBoolean("active");

    public final StringPath bio = createString("bio");

    public final DatePath<java.time.LocalDate> createDate = createDate("createDate", java.time.LocalDate.class);

    public final StringPath email = createString("email");

    public final StringPath emailCheckToken = createString("emailCheckToken");

    public final DateTimePath<java.time.LocalDateTime> emailCheckTokenIssueTime = createDateTime("emailCheckTokenIssueTime", java.time.LocalDateTime.class);

    public final StringPath emailLoginToken = createString("emailLoginToken");

    public final DateTimePath<java.time.LocalDateTime> emailLoginTokenIssueTime = createDateTime("emailLoginTokenIssueTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> lastUpdateDate = createDate("lastUpdateDate", java.time.LocalDate.class);

    public final StringPath location = createString("location");

    public final StringPath nickname = createString("nickname");

    public final StringPath occupation = createString("occupation");

    public final StringPath password = createString("password");

    public final StringPath profileImage = createString("profileImage");

    public final BooleanPath studyCreatedByEmail = createBoolean("studyCreatedByEmail");

    public final BooleanPath studyCreatedByWeb = createBoolean("studyCreatedByWeb");

    public final BooleanPath studyEnrollmentResultByEmail = createBoolean("studyEnrollmentResultByEmail");

    public final BooleanPath studyEnrollmentResultByWeb = createBoolean("studyEnrollmentResultByWeb");

    public final BooleanPath studyUpdatedByEmail = createBoolean("studyUpdatedByEmail");

    public final BooleanPath studyUpdatedByWeb = createBoolean("studyUpdatedByWeb");

    public final SetPath<be.shwan.modules.tag.domain.Tag, be.shwan.modules.tag.domain.QTag> tags = this.<be.shwan.modules.tag.domain.Tag, be.shwan.modules.tag.domain.QTag>createSet("tags", be.shwan.modules.tag.domain.Tag.class, be.shwan.modules.tag.domain.QTag.class, PathInits.DIRECT2);

    public final StringPath url = createString("url");

    public final SetPath<be.shwan.modules.zone.domain.Zone, be.shwan.modules.zone.domain.QZone> zones = this.<be.shwan.modules.zone.domain.Zone, be.shwan.modules.zone.domain.QZone>createSet("zones", be.shwan.modules.zone.domain.Zone.class, be.shwan.modules.zone.domain.QZone.class, PathInits.DIRECT2);

    public QAccount(String variable) {
        super(Account.class, forVariable(variable));
    }

    public QAccount(Path<? extends Account> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccount(PathMetadata metadata) {
        super(Account.class, metadata);
    }

}

