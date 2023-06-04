package model.account;

import db.DBConnection;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AccountDAO {
    private Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    // 계좌생성 (기본 1000원 입금 되어야 함)
    public void createAccount(String accountNumber, String accountPassword, int accountBalance) throws SQLException {
        String query = "INSERT INTO account_tb (account_number, account_password, account_balance, account_created_at) VALUES (?, ?, ?, now())";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            statement.setString(2, accountPassword);
            statement.setInt(3, accountBalance);
            statement.executeUpdate();
        }
    }

    // 계좌 상세보기
    public Account getAccountByNumber(int accountNumber) throws SQLException {
        String query = "SELECT * FROM account_tb WHERE account_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return buildAccountFromResultSet(resultSet);
                }
            }
        }
        return null; // Account not found
    }

    // 전체 계좌 조회
    public List<Account> getAllAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM account_tb";
        try (Statement statement = connection.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery(query)){
                while (resultSet.next()) {
                    Account account = buildAccountFromResultSet(resultSet);
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }

    // 계좌 잔액 수정
    public void updateAccount(int accountBalance, int accountNumber) throws SQLException {
        String query = "UPDATE account_tb SET account_balance = ?  WHERE account_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountBalance);
            statement.setInt(2, accountNumber);
            statement.executeUpdate();
        }
    }

    // 계좌 삭제
    public void deleteAccount(String accountNumber) throws SQLException {
        String query = "DELETE FROM account_tb WHERE account_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            statement.executeUpdate();
        }
    }

    private Account buildAccountFromResultSet(ResultSet resultSet) throws SQLException {
        int accountNumber = resultSet.getInt("account_number");
        String accountPassword = resultSet.getString("account_password");
        int accountBalance = resultSet.getInt("account_balance");
        Timestamp accountCreatedAt = resultSet.getTimestamp("account_created_at");

        return Account.builder()
                .accountNumber(accountNumber)
                .accountPassword(accountPassword)
                .accountBalance(accountBalance)
                .accountCreatedAt(accountCreatedAt)
                .build();
    }
}
