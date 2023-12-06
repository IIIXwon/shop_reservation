package be.shwan.study.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudyController {
    @GetMapping(value = {"/new-study"})
    public String studyFormPage(Model model) {
        return "";
    }
}
