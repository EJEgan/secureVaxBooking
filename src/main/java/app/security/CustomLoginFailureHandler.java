package app.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import app.model.IncorrectLogin;
import app.model.User;
import app.repository.IncorrectLoginRepo;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

// Class added while following this guide: https://www.codejava.net/frameworks/spring-boot/spring-security-limit-login-attempts-example
@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private IncorrectLoginRepo incorrectLoginRepo;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException, UsernameNotFoundException {
        String email = request.getParameter("username");
        System.out.println("email used is " + email);
        try {
            User user = userService.getByEmail(email);

        System.out.println(user.getName());
        System.out.println("entered login failure handler");
        if (user != null) {
            System.out.println("user is not null");
            if (user.isAccountNonLocked()) {
                if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS - 1) {
                    System.out.println("login attempt failed, less than 3 failed attempts");
                    userService.increaseFailedAttempts(user);

                    // Check the ipAddress used for this request and lock it if need be
                    String ipAddress = request.getRemoteAddr();
                    System.out.println("ip is: " + ipAddress);

                    // Check if this login has tried to login unsuccessfuly twice previously and lock it if so
                    // otherwise just increment the number of attempts
                    IncorrectLogin incorrectLogin = incorrectLoginRepo.findByip(ipAddress);
                    if (incorrectLogin.getNumAttempts() > 2)

                } else {
                    System.out.println("login attempt failed, more than 3 failed attempts");
                    userService.lock(user);
                    exception = new LockedException("Your account has been locked due to 3 failed attempts."
                            + " It will be unlocked after 24 hours.");
                }
            } else if (!user.isAccountNonLocked()) {
                System.out.println("Account is locked");
                if (userService.unlockWhenTimeExpired(user)) {
                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
                }
            }

        }
        } catch (Exception e) {
            throw new UsernameNotFoundException("username: " + email + " could not be found");
        }

        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }

}
