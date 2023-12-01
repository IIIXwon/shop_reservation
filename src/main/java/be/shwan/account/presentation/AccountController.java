package be.shwan.account.presentation;

import be.shwan.account.dto.SignUpRequestDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountController {
    @GetMapping(value = {"/sign-up"})
    public String signUpPage(Model model) {
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("")
                .password("")
                .nickname("")
                .build();
        model.addAttribute(signUpRequestDto);
        return "accounts/sign-up";
    }
}
