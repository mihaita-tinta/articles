package com.mih.spring.magic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
public class SpringMagicApplication implements WebMvcConfigurer {

	@Autowired
	private ProfileArgumentResolver profileArgumentResolver;

	@Override
	public void addArgumentResolvers(
			List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(profileArgumentResolver);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringMagicApplication.class, args);
	}

}
