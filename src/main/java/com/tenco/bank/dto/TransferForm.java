package com.tenco.bank.dto;

import lombok.Data;

@Data
public class TransferForm {
	private Long amount;
	private String wAccountNumber;
	private String wAccountPassword;
	private String dAccountNumber;
}
