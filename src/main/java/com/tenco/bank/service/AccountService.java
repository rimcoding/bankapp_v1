package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.DepositFormDto;
import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.dto.TransferFormDto;
import com.tenco.bank.dto.WithdrawFormDto;
import com.tenco.bank.dto.response.HistoryDto;
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
	// 단일 계좌 검색 기능
	@Transactional
	public Account readAccount(Integer id) {
		Account accountEntity = accountRepository.findById(id);
		if (accountEntity == null) {
			throw new CustomRestfullException("해당 계좌를 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return accountEntity;
	} 

	/**
	 * 
	 * @param type = [all, deposity, withdraw]
	 * @param id (account_id)
	 * @return 입금,출금,입출금 거래내역 (3가지 타입)
	 */
	@Transactional
	public List<HistoryDto> readHistoryListByAccount(String type,Integer id){
	
		List<HistoryDto> historyDtos = historyRepository.findByIdHistoryType(type, id);
		
		historyDtos.forEach(e ->{
			// historyDtos = e 
			System.out.println(e);
		});
		
		return historyDtos;
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
	
	// 이체 기능 만들기
	// 트랜젝션 처리
	// 1. 출금 계좌 존재 여부 확인 - select
	// 2. 입금 계좌 존재 여부 확인 - select
	// 3. 출금 계좌 본인 소유 확인 - 1(E) == (principal)
	// 4. 출금 계좌 비번 확인 - 1(E) == (Dto)
	// 5. 출금 계좌 잔액 여부 확인 -1(E) == (Dto)
	// 6. 출금 계좌 잔액 변경 - update
	// 7. 입금 계좌 잔액 변경 - update
	// 8. 거래 내역 저장 - insert
	@Transactional
	public void updateAccountTransfer(TransferFormDto transferFormDto,Integer principalId) {
		//1  출금 계좌 존재 여부 확인 - select
		Account withdrawAccountEntity = accountRepository.findByNumber(transferFormDto.getWAccountNumber());
		if (withdrawAccountEntity == null) {
			throw new CustomRestfullException("출금 계좌가 존재하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		//2 입금 계좌 존재 여부 확인 - select
		Account depositAccountEnttity = accountRepository.findByNumber(transferFormDto.getDAccountNumber());
		if (depositAccountEnttity == null) {
			throw new CustomRestfullException("입금 계좌가 존재하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		//3 출금 계좌 본인 소유 확인 - 1(E) == (principal)
		withdrawAccountEntity.checkOwner(principalId);
		//4  출금 계좌 비번 확인 - 1(E) == (Dto)
		withdrawAccountEntity.checkPassword(transferFormDto.getWAccountPassword());
		//5 출금 계좌 잔액 여부 확인 -1(E) == (Dto)
		withdrawAccountEntity.checkBalance(transferFormDto.getAmount());
		// 6 (출금 객체 상태값 변경 - 계좌 잔액 수정 처리) 
		withdrawAccountEntity.withdraw(transferFormDto.getAmount());
		// 변경된 출금 객체 상태값으로 DB 업데이트 처리
		accountRepository.updateById(withdrawAccountEntity);
		// 7 (입금 객체 상태값 변경 - 계좌 잔액 수정 처리)
		depositAccountEnttity.deposit(transferFormDto.getAmount());
		// 변경된 입금 객체 상태값으로 DB 업데이트 처리
		accountRepository.updateById(depositAccountEnttity);
		
		// 8 거래내역 저장
		History history = new History();
		history.setAmount(transferFormDto.getAmount());
		history.setWAccountId(withdrawAccountEntity.getId());
		history.setDAccountId(depositAccountEnttity.getId());
		history.setWBalance(withdrawAccountEntity.getBalance());
		history.setDBalance(depositAccountEnttity.getBalance());
		int resultRowcount = historyRepository.insert(history);
		if (resultRowcount != 1) {
			throw new CustomRestfullException("정상 처리 되지 않았습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
}
