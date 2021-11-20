package com.mih.spring.magic;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Component
public class ProfileArgumentResolver implements HandlerMethodArgumentResolver {

    private final ProfileRepository profileRepository;

    public ProfileArgumentResolver(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Profile.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new IllegalStateException("Current request is not of type HttpServletRequest: " + webRequest);
        } else {
            Cookie cookieValue = WebUtils.getCookie(request, parameter.getParameterName());
            Profile profile = profileRepository.getProfile(cookieValue.getValue());
            return profile;
        }
    }
}
