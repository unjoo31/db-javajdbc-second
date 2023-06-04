package model.transaction;

import db.DBConnection;
import lombok.Getter;
import model.account.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
public class TransactionDAO {
    private Connection connection;

    public TransactionDAO(Connection connection) {
        this.connection = connection;
    }

    public void transfer(Transaction transaction) throws SQLException{
        String query = "INSERT INTO transaction_tb (transaction_amount, transaction_w_balance, transaction_d_balance, transaction_w_account_number, transaction_d_account_number, transaction_created_at) VALUES (?, ?, ?, ?, ?, now())";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, transaction.getTransactionAmount());
            statement.setInt(2, transaction.getTransactionWBalance());
            statement.setInt(3, transaction.getTransactionDBalance());
            statement.setInt(4, transaction.getTransactionWAccountNumber());
            statement.setInt(5, transaction.getTransactionDAccountNumber());
            statement.executeUpdate();
        }
    }


}
