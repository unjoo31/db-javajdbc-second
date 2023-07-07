import com.mysql.cj.util.StringUtils;
import db.DBConnection;
import dto.AccountDetailDTO;
import model.account.Account;
import model.account.AccountDAO;
import model.transaction.Transaction;
import model.transaction.TransactionDAO;
import util.MyStringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BankApp {
    public static void main(String[] args) {
        Connection connection = DBConnection.getInstance();

        // DI
        AccountDAO accountDAO = new AccountDAO(connection);
        TransactionDAO transactionDAO = new TransactionDAO(connection);

        try {
            int accountNumber = 1111;
            List<AccountDetailDTO> dtos = transactionDAO.details(accountNumber);
            System.out.println("=============000님의 계좌 상세===============");
            System.out.println("본인 계좌번호 : "+dtos.get(0).getAccountNumber());
            System.out.println("본인 계좌현재잔액 : "+dtos.get(0).getAccountBalance());
            System.out.println("=============트랜잭션 내역 시작===============");
            dtos.forEach(dto -> {
                System.out.print("[");
                if(dto.getSender() == accountNumber){
                    System.out.println("**출금**");
                }else{
                    System.out.println("**입금**");
                }
                System.out.println("    보낸 계좌 : "+dto.getSender());
                System.out.println("    받은 계좌 : "+dto.getReceiver());
                System.out.println("    이체 금액 : "+dto.getAmount());
                System.out.println("    잔액 : "+dto.getBalance());
                System.out.println("    날짜 : "+MyStringUtils.dateFormat(dto.getTransferDate()));
                System.out.println("]");
                System.out.println();
            });
            System.out.println("=============트랜잭션 내역 끝===============");
        }catch (Exception e){

        }




    }
}