package be.shwan.modules.tag.application;

import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.tag.dto.RequestTagDto;

public interface TagService {
    Tag getTag(RequestTagDto tagDto);

    Tag findTag(RequestTagDto tagDto);

}
