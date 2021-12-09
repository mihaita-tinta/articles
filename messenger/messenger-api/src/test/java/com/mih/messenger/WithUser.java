package com.mih.messenger;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import com.mih.messenger.domain.User;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(
        factory = WithUser.WithMockUserSecurityContextFactory.class
)
public @interface WithUser {
    String value() default "user";

    String username() default "";

    String[] authorities() default {};

    long id() default 1;

    @AliasFor(
            annotation = WithSecurityContext.class
    )
    TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;



    final class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithUser> {
        WithMockUserSecurityContextFactory() {
        }

        public SecurityContext createSecurityContext(WithUser withUser) {
            String username = StringUtils.hasLength(withUser.username()) ? withUser.username() : withUser.value();
            Assert.notNull(username, () -> {
                return withUser + " cannot have null username on both username and value properties";
            });
            List<GrantedAuthority> grantedAuthorities = new ArrayList();
            String[] authorities = withUser.authorities();

            for(int i = 0; i < authorities.length; i++) {
                String role = authorities[i];
                grantedAuthorities.add(new SimpleGrantedAuthority(role));
            }

            User principal = new User();
            principal.setId(withUser.id());
            principal.setUsername(username);
            principal.setDateCreated(new Date());
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            return context;
        }
    }

}

