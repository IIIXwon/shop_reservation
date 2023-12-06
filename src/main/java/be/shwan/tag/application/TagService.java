package be.shwan.tag.application;

import be.shwan.tag.domain.Tag;
import be.shwan.tag.dto.RequestTagDto;

public interface TagService {
    Tag getTag(RequestTagDto tagDto);

    Tag findTag(RequestTagDto tagDto);

}
