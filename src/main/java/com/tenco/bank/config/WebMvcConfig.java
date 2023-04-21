package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthInterceptor;

@Configuration // Ioc 등록 - 2개 이상 빈으로 등록 될때 사용
public class WebMvcConfig implements WebMvcConfigurer {

	
	@Autowired // DI
	private AuthInterceptor authInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/account/**")
				.addPathPatterns("/auth/**"); // 1.path 더 추가 하는 방법
		// account안에 실행되는것은 인증처리 해줘라
		

			
	}
}
