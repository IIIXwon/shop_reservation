package be.shwan.modules.study.presentation;


import be.shwan.infra.AbstractContainerBaseTest;
import be.shwan.infra.MockMvcTest;
import be.shwan.modules.account.AccountFactory;
import be.shwan.modules.account.domain.Account;
import be.shwan.modules.event.EventFactory;
import be.shwan.modules.study.StudyFactory;
import be.shwan.modules.study.application.StudyService;
import be.shwan.modules.study.domain.Study;
import be.shwan.modules.study.domain.StudyRepository;
import be.shwan.modules.study.dto.StudyRequestDto;
import be.shwan.modules.tag.dto.RequestTagDto;
import be.shwan.modules.zone.dto.RequestZoneDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@MockMvcTest
class SearchControllerTest extends AbstractContainerBaseTest {
    private final String USER_NAME = "seunghwan";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyFactory studyFactory;

    @Autowired
    EventFactory eventFactory;

    @Autowired
    StudyService studyService;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    AccountFactory accountFactory;

    @DisplayName("[GET] /search/study")
    @Test
    void studySearchPage() throws Exception {
        Account studyManager = accountFactory.createAccount(USER_NAME);
        StudyRequestDto testDto1 = new StudyRequestDto("test1", "서울", StudyFactory.DEFAULT_SHORT_DESCRIPTION, StudyFactory.DEFAULT_FULL_DESCRIPTION);
        StudyRequestDto testDto2 = new StudyRequestDto("test2", StudyFactory.DEFAULT_TITLE, StudyFactory.DEFAULT_SHORT_DESCRIPTION, StudyFactory.DEFAULT_FULL_DESCRIPTION);
        StudyRequestDto testDto3 = new StudyRequestDto("test3", "StudyFactory.DEFAULT_TITLE", StudyFactory.DEFAULT_SHORT_DESCRIPTION, StudyFactory.DEFAULT_FULL_DESCRIPTION);
        Study study1 = studyFactory.createStudy(studyManager, testDto1);
        Study study2 = studyFactory.createStudy(studyManager, testDto2);
        Study study3 = studyFactory.createStudy(studyManager, testDto3);
        study1.publish();
        study2.publish();
        study3.publish();
        RequestTagDto tagDto = new RequestTagDto("서울");
        studyService.addTag(study2, tagDto);

        RequestZoneDto requestZoneDto = new RequestZoneDto("Seoul(서울특별시)/none");
        studyService.addZone(study3, requestZoneDto);

        String keyword = "서울";
        mockMvc.perform(get("/search/study")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(view().name(SearchController.STUDY_SEARCH_VIEW))
                .andExpect(model().attributeExists("account", "studyPage"))
                .andExpect(unauthenticated())
        ;
    }
}
