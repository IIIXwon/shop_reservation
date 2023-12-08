package be.shwan.modules.tag.application.impl;

import be.shwan.modules.tag.application.TagService;
import be.shwan.modules.tag.domain.Tag;
import be.shwan.modules.tag.domain.TagRepository;
import be.shwan.modules.tag.dto.RequestTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SimpleTagService implements TagService {
    private final TagRepository tagRepository;

    @Override
    public Tag getTag(RequestTagDto tagDto) {
        Tag tag = tagRepository.findByTitle(tagDto.tagTitle());
        if (tag == null) {
            tag = tagRepository.save(new Tag(tagDto));
        }
        return tag;
    }

    @Override
    public Tag findTag(RequestTagDto tagDto) {
        Tag tag = tagRepository.findByTitle(tagDto.tagTitle());
        if (tag == null ) {
            throw new IllegalArgumentException();
        }
        return tag;
    }
}
