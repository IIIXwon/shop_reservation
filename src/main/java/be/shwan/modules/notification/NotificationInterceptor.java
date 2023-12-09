package be.shwan.modules.notification;

import be.shwan.modules.account.domain.Account;
import be.shwan.modules.account.domain.UserAccount;
import be.shwan.modules.notification.domain.NotificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {
    private final NotificationRepository notificationRepository;
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (modelAndView != null && !isRedirectView(modelAndView) && authentication != null
                && authentication.getPrincipal() instanceof UserAccount) {
            Account account = ((UserAccount) authentication.getPrincipal()).getAccount();
            long uncheckedCount = notificationRepository.countByAccountAndChecked(account, false);
            modelAndView.addObject("hasNotification", uncheckedCount > 0);
        }
    }

    private boolean isRedirectView(ModelAndView modelAndView) {
        return Objects.requireNonNull(modelAndView.getViewName()).startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
    }
}
