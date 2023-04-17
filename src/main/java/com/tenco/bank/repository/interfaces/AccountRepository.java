package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tenco.bank.repository.model.Account;

@Mapper	//mybatis 연결
public interface AccountRepository {
	
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(int id);
	
	public List<Account> findAll();	// 고민
	// 한사람의 2개이상의 계좌정보 (관리자가 쓸 기능 만들 예정)
	public Account findById(int id);
}
