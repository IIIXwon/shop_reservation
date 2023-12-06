package be.shwan.tag.application.impl;

import be.shwan.tag.application.TagService;
import be.shwan.tag.domain.Tag;
import be.shwan.tag.domain.TagRepository;
import be.shwan.tag.dto.RequestTagDto;
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
