package com.tenco.bank.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.handler.exception.CustomPageException;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.User;

@Controller
@RequestMapping("/account")
public class AccountController {
	@Autowired
	private HttpSession session;
	
	//todo
	// 계좌목록 페이지
	// 입금 페이지
	// 출금 페이지
	// 이체 페이지
	// 계좌 상세보기
	// 계좌 생성 페이지
	
	// http:localhost:8080/account/list
	// http:localhost:8080/account/
	/**
	 * 계좌 목록 페이지
	 * @return 목록 페이지 이동
	 */
	@GetMapping({"list", "/"})
	public String list() {
		
		// todo 예외 테스트 - 삭제 예정
		//throw new CustomRestfullException("인증되지 않은 사용자 입니다.", HttpStatus.UNAUTHORIZED);
		//throw new CustomPageException("페이지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		
		// prefix
		// subfix
		
		// 인증검사 처리
		User principal = (User)session.getAttribute("principal");
		if (principal == null) {
			throw new UnAuthorizedException("인증이 안된 사용자 입니다.",HttpStatus.UNAUTHORIZED);
		}
		 return "/account/list";
	}
	// 출금 페이지
	@GetMapping("/withdraw")
	public String withdraw() {
		return "/account/withdrawForm";
	}
	
	// 입금 페이지
	@GetMapping("/deposit")
	public String deposit() {
		return "/account/depositForm";
	}
	
	// 이체 페이지
	@GetMapping("/transfer")
	public String transfer() {
		return "/account/transferForm";
	}
	
	// 계좌 생성 페이지
	@GetMapping("/save")
	public String save() {
		
		return "/account/saveForm";
	}
	
	// 계좌 상세 보기 페이지
	@GetMapping("/detail")
	public String detail() {
		return "";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
