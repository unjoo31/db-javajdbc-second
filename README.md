# 입출금 내역 JDBC

## 테이블
```sql
create database metadb;
use metadb;
    
CREATE TABLE account_tb(
    account_number int primary key,
    account_password varchar(100) not null,
    account_balance int not null,
    account_created_at timestamp not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE transaction_tb(
    transaction_number int auto_increment primary key,
    transaction_amount int not null,
    transaction_w_balance int,
    transaction_d_balance int,
    transaction_w_account_number int,
    transaction_d_account_number int,
    transaction_created_at timestamp not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 더미데이터
```sql
-- 출금
insert into transaction_tb(transaction_amount, transaction_w_account_number, transaction_d_account_number, transaction_w_balance, transaction_d_balance, transaction_created_at)
values(100, 1111, 2222, 900, 1100, now());
insert into transaction_tb(transaction_amount, transaction_w_account_number, transaction_d_account_number, transaction_w_balance, transaction_d_balance, transaction_created_at)
values(100, 1111, 2222, 800, 1200, now());
insert into transaction_tb(transaction_amount, transaction_w_account_number, transaction_d_account_number, transaction_w_balance, transaction_d_balance, transaction_created_at)
values(100, 1111, 2222, 700, 1300, now());

-- 입금
insert into transaction_tb(transaction_amount, transaction_w_account_number, transaction_d_account_number, transaction_w_balance, transaction_d_balance, transaction_created_at)
values(200, 2222, 1111, 1100, 900, now());
```

## 쿼리 연습
```sql
-- 1111 계좌 입금 내역 상세보기
select *
from account_tb ac
         inner join transaction_tb ts on ac.account_number = ts.transaction_d_account_number
where ac.account_number = 1111;

-- 1111 계좌 출금 내역 상세보기
select *
from account_tb ac
         inner join transaction_tb ts on ac.account_number = ts.transaction_w_account_number
where ac.account_number = 1111;

-- 1111 계좌 입출금 내역 상세보기
select
    ac.account_number,
    ac.account_balance,
    ts.transaction_w_account_number,
    ts.transaction_d_account_number,
    ts.transaction_amount,
    ts.transaction_w_balance,
    ts.transaction_d_balance,
    ts.transaction_created_at
from account_tb ac
         inner join transaction_tb ts on ac.account_number = ts.transaction_w_account_number
    OR ac.account_number = ts.transaction_d_account_number
where ac.account_number = 1111;

select
    ac.account_number,
    ac.account_balance,
    ts.transaction_w_account_number sender,
    ts.transaction_d_account_number receiver,
    ts.transaction_amount amount,
    if(ts.transaction_w_account_number=1111,ts.transaction_w_balance, ts.transaction_d_balance) balance,
    ts.transaction_created_at transfer_date
from account_tb ac
         inner join transaction_tb ts on ac.account_number = ts.transaction_w_account_number
    OR ac.account_number = ts.transaction_d_account_number
where ac.account_number = 1111;
```

## 계좌 이체 비지니스 로직
```java
import db.DBConnection;
import model.account.Account;
import model.account.AccountDAO;
import model.transaction.Transaction;
import model.transaction.TransactionDAO;
import java.sql.Connection;
public class BankApp {
    public static void main(String[] args) {
        Connection connection = DBConnection.getInstance();
        AccountDAO accountDAO = new AccountDAO(connection);
        TransactionDAO transactionDAO = new TransactionDAO(connection);
        try {
            // 이체 요청 정보
            String wAccountPassword = "1234";
            int wAccountNumber = 4444;
            int dAccountNumber = 3333;
            int amount = 50000;
            // 0원 이체 확인하기 (컨트롤러에서 체크)
            if(amount <= 0){
                System.out.println("[유효성 오류] 0원 이하의 금액을 이체할 수 없습니다");
                return;
            }
            // 동일 계좌 이체 확인하기 (컨트롤러에서 체크)
            if(wAccountNumber == dAccountNumber){
                System.out.println("[유효성 오류] 입출금 계좌가 동일할 수 없습니다");
                return;
            }
            // --------------------------------------------- 트랜잭션 시작
            connection.setAutoCommit(false);
            // 계좌 찾기 (서비스에서 체크)
            Account wAccount = accountDAO.getAccountByNumber(wAccountNumber);
            Account dAccount = accountDAO.getAccountByNumber(dAccountNumber);
            // 계좌 존재 확인 (서비스에서 체크)
            if(wAccount == null){
                throw new RuntimeException("출금 계좌가 존재하지 않습니다");
            }
            if(dAccount == null){
                throw new RuntimeException("입금 계좌가 존재하지 않습니다");
            }
            // 계좌 비밀번호 확인 (서비스에서 체크)
            if(!wAccount.getAccountPassword().equals(wAccountPassword)){
                throw new RuntimeException("출금 계좌의 비밀번호가 올바르지 않습니다");
            }
            // 계좌 잔액 확인 (서비스에서 체크)
            if(wAccount.getAccountBalance() < amount){
                throw new RuntimeException("출금 계좌의 잔액이 부족합니다");
            }
            // 계좌 업데이트 (서비스에서 업데이트)
            int wBalance = wAccount.getAccountBalance() - amount;
            int dBalance = dAccount.getAccountBalance() + amount;
            accountDAO.updateAccount(wBalance, wAccountNumber);
            accountDAO.updateAccount(dBalance, dAccountNumber);
            // 트랜잭션 이력 남기기 (서비스에서 인서트)
            Transaction transaction = Transaction.builder()
                    .transactionAmount(amount)
                    .transactionWAccountNumber(wAccountNumber)
                    .transactionDAccountNumber(dAccountNumber)
                    .transactionWBalance(wBalance)
                    .transactionDBalance(dBalance)
                    .build();
            transactionDAO.transfer(transaction);
            connection.commit();
            // --------------------------------------------- 트랜잭션 종료
        } catch (Exception e) {
            try {
                connection.rollback();
                System.out.println("[catch] "+e.getMessage());
            }catch (Exception innerEx){
                innerEx.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
```