package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.tenco.bank.dto.SignInFormDto;
import com.tenco.bank.dto.SignUpFormDto;
import com.tenco.bank.repository.model.User;

@Mapper	// MyBatis 의존 설정 함 (build.gradle 파일)
public interface UserRepository {

	// 무엇을 기반으로 변경하거나 찾는지 정확하게 명시 해준다.
	// principal 접근 주체
	// 한번에 메서드를 넣을수 있다 ex)HistoryRepository 맴버변수 선언
	public int insert(SignUpFormDto signUpFormDto);
	public int updateById(User user);
	public int deleteById(Integer id);
	public User findById(Integer id);
	// 한사람의 정보만 담음
	public List<User> findAll();
	// 여러명의 정보를 담음
	public User findByUsernameAndPassword(SignInFormDto signInFormDto);
}
