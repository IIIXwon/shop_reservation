package be.shwan.modules.study.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudy is a Querydsl query type for Study
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudy extends EntityPathBase<Study> {

    private static final long serialVersionUID = -959053067L;

    public static final QStudy study = new QStudy("study");

    public final BooleanPath closed = createBoolean("closed");

    public final DateTimePath<java.time.LocalDateTime> closedDateTime = createDateTime("closedDateTime", java.time.LocalDateTime.class);

    public final StringPath fullDescription = createString("fullDescription");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final SetPath<be.shwan.modules.account.domain.Account, be.shwan.modules.account.domain.QAccount> managers = this.<be.shwan.modules.account.domain.Account, be.shwan.modules.account.domain.QAccount>createSet("managers", be.shwan.modules.account.domain.Account.class, be.shwan.modules.account.domain.QAccount.class, PathInits.DIRECT2);

    public final SetPath<be.shwan.modules.account.domain.Account, be.shwan.modules.account.domain.QAccount> members = this.<be.shwan.modules.account.domain.Account, be.shwan.modules.account.domain.QAccount>createSet("members", be.shwan.modules.account.domain.Account.class, be.shwan.modules.account.domain.QAccount.class, PathInits.DIRECT2);

    public final StringPath path = createString("path");

    public final BooleanPath published = createBoolean("published");

    public final DateTimePath<java.time.LocalDateTime> publishedDateTime = createDateTime("publishedDateTime", java.time.LocalDateTime.class);

    public final BooleanPath recruiting = createBoolean("recruiting");

    public final DateTimePath<java.time.LocalDateTime> recruitingUpdateDateTime = createDateTime("recruitingUpdateDateTime", java.time.LocalDateTime.class);

    public final StringPath shortDescription = createString("shortDescription");

    public final SetPath<be.shwan.modules.tag.domain.Tag, be.shwan.modules.tag.domain.QTag> tags = this.<be.shwan.modules.tag.domain.Tag, be.shwan.modules.tag.domain.QTag>createSet("tags", be.shwan.modules.tag.domain.Tag.class, be.shwan.modules.tag.domain.QTag.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final BooleanPath useBanner = createBoolean("useBanner");

    public final SetPath<be.shwan.modules.zone.domain.Zone, be.shwan.modules.zone.domain.QZone> zones = this.<be.shwan.modules.zone.domain.Zone, be.shwan.modules.zone.domain.QZone>createSet("zones", be.shwan.modules.zone.domain.Zone.class, be.shwan.modules.zone.domain.QZone.class, PathInits.DIRECT2);

    public QStudy(String variable) {
        super(Study.class, forVariable(variable));
    }

    public QStudy(Path<? extends Study> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudy(PathMetadata metadata) {
        super(Study.class, metadata);
    }

}

