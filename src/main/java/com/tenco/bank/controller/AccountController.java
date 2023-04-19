package com.tenco.bank.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.handler.exception.CustomPageException;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;
import com.tenco.bank.utils.Define;

@Controller
@RequestMapping("/account")
public class AccountController {
	@Autowired
	private HttpSession session;
	@Autowired
	private AccountService accountService;
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
		// 1번 유저가 10이라는 값을 가져갔는데 2번유저가 들고간 값을 달라고 할수 없다.
		User principal = (User)session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요.",HttpStatus.UNAUTHORIZED);
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
		// 인증 검사 처리
		User user = (User)session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요.",HttpStatus.UNAUTHORIZED);
		}
		return "/account/saveForm";
	}
	/**
	 * 계좌 생성
	 * 인증검사
	 * 유효성 검사 처리 - 0원 입력 가능, 마이너스 입력 불가
	 * @param saveFormDto
	 * @return 계좌 목록 페이지
	 */
	@PostMapping("/save-proc")
	public String saveProc(SaveFormDto saveFormDto) {
		
		User user = (User)session.getAttribute(Define.PRINCIPAL);
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요.",HttpStatus.UNAUTHORIZED);
		}
		
		// 유효성 검사 하기
		if (saveFormDto.getNumber() == null || saveFormDto.getNumber().isEmpty()) {
			throw new CustomRestfullException("계좌번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		if (saveFormDto.getPassword() == null || saveFormDto.getPassword().isEmpty()) {
			throw new CustomRestfullException("비밀번호를 입력해주세요", HttpStatus.BAD_REQUEST);
		}
		if (saveFormDto.getBalance() == null || saveFormDto.getBalance() < 0) {
			throw new CustomRestfullException("잘못된 금액 입니다", HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 호출
		accountService.createAccount(saveFormDto, user.getId());
		
		return "redirect:/account/list";
	}
	// 계좌 상세 보기 페이지
	@GetMapping("/detail")
	public String detail() {
		return "";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
