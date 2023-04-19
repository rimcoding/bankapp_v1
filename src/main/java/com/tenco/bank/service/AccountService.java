package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.DepositFormDto;
import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.dto.WithdrawFormDto;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;

@Service	//Ioc 대상 + 싱글톤으로 관리
public class AccountService {

	@Autowired // DI처리 (객체 생성시 의존 주입 처리)
	private AccountRepository accountRepository;
	
	@Autowired
	private HistoryRepository historyRepository;
	/**
	 * 
	 * 계좌 생성 기능
	 * @param saveFormDto
	 * @param principalId
	 */
	@Transactional
	public void createAccount(SaveFormDto saveFormDto,Integer principalId) {
		
		Account account = new Account();
		account.setNumber(saveFormDto.getNumber());
		account.setPassword(saveFormDto.getPassword());
		account.setBalance(saveFormDto.getBalance());
		account.setUserId(principalId);
		int resultRowCount = accountRepository.insert(account);
		System.out.println(resultRowCount);
		if (resultRowCount != 1) {
			throw new CustomRestfullException("계좌 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	// 계좌 목록 보기 기능
	
	@Transactional
	public List<Account> readAccountList(Integer userId){
		List<Account> list = accountRepository.findByUserId(userId);
		return list;
	}
	
	// 출금 기능 로직 고민해 보기
	// 1. 계좌 존재 여부 확인 -> select query
	// 2. 계좌 비번 확인
	// 3. 잔액 여부 확인
	// 4. 출금 처리 -> update query
	// 5. 거래 내역 등록 -> insert query
	// 6. 트랜젝션 처리
	

	@Transactional
	public void updateAccountWithdraw(WithdrawFormDto withdrawFormDto, Integer principalId) {
		
		Account accountEntity =  accountRepository.findByNumber(withdrawFormDto.getWAccountNumber());
		// 1번
		if (accountEntity == null) {
			throw new CustomRestfullException("계좌가 없습니다", HttpStatus.BAD_REQUEST);
		}
		// 2번
		if (accountEntity.getUserId() != principalId) {
			throw new CustomRestfullException("본인 소유 계좌가 아닙니다", HttpStatus.UNAUTHORIZED);
		}
		// 3번
		if (accountEntity.getPassword().equals(withdrawFormDto.getWAccountPassword()) == false) {
			throw new CustomRestfullException("출금 계좌 비밀번호가 틀렸습니다", HttpStatus.UNAUTHORIZED);
		}
		// 4번
		if (accountEntity.getBalance() < withdrawFormDto.getAmount()) {
			throw new CustomRestfullException("잔액이 부족 합니다", HttpStatus.BAD_REQUEST);
		}
		// 5번 (모델 객체 상태값 변경 처리)
		//model안에 메서드 기능을 만들어 놔서 불러왔다
		//accountEntity.setBalance(accountEntity.getBalance() - withdrawFormDto.getAmount());
		accountEntity.withdraw(withdrawFormDto.getAmount());
		accountRepository.updateById(accountEntity);
		// 6번 거래내역 등록
		// history.xml파일에 있는 쿼리문을 들고와서 보면서 하는게 좋다.
		History history = new History();
		history.setAmount(withdrawFormDto.getAmount());
		history.setWBalance(accountEntity.getBalance());
		history.setDBalance(null);
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);
		
		int resultRowCount = historyRepository.insert(history);
		
		if (resultRowCount != 1) {
			throw new CustomRestfullException("정상 처리 되지 않았습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	// 입금 처리 기능
	// 트랜젝션 처리
	// 1. 계좌존재 여부 확인 (입금 할려고 하는데 계좌가 없을 수도 있어서)
	// 2. 입금 처리 -> update
	// 3. 거래 내역 등록 처리 -> insert
	@Transactional
	public void updateAccountDeposit(DepositFormDto depositFormDto) {
		
		Account accountEntity = accountRepository.findByNumber(depositFormDto.getDAccountNumber());
		if (accountEntity == null) {
			throw new CustomRestfullException("해당 계좌가 존재하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// 객체 상태값 변경
		accountEntity.deposit(depositFormDto.getAmount());
		accountRepository.updateById(accountEntity);
		
		History history = new History();
		history.setAmount(depositFormDto.getAmount());
		history.setWBalance(null);
		history.setDBalance(accountEntity.getBalance());
		history.setWAccountId(null);
		history.setDAccountId(accountEntity.getId());
		int resultRowCount = historyRepository.insert(history);
		if (resultRowCount != 1) {
			throw new CustomRestfullException("정상 처리가 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
