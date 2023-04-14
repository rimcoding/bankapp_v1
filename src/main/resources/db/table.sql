CREATE TABLE user_tb(
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(30) NOT NULL,
    fullname VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE account_tb(
    id INT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(20) NOT NULL,
    balance BIGINT NOT NULL COMMENT '계좌 잔액',
    user_id INT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE history_tb(
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '거래내역',
    amount BIGINT NOT NULL COMMENT '거래금액',
    w_account_id INT COMMENT '출금 계좌 id',
    d_account_id INT COMMENT '입금 계좌 id',
    w_balance BIGINT COMMENT '출금 요청된 계좌 잔액',
    d_balance BIGINT COMMENT '입금 요청된 계좌 잔액',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);