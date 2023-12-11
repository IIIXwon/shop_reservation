package be.shwan.modules.main;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler
    public String handleRuntimeException(@CurrentUser Account account, HttpServletRequest request, RuntimeException e) {
        if ( account != null) {
            log.info("'{}' account request {}", account.getNickname(), request.getRequestURI());
        } else {
            log.info("request {}", request.getRequestURI()); ;
        }
        log.error("bad request", e);
        return "error";
    }
}
