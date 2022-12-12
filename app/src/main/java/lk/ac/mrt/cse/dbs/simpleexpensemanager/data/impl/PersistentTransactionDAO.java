package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteManager sqLiteManager;

    public PersistentTransactionDAO(SQLiteManager sqLiteManager){
        this.sqLiteManager = sqLiteManager;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String strDate = simpleDateFormat.format(date);
        sqLiteManager.addtransaction( strDate, accountNo, expenseType.toString(), amount);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return sqLiteManager.getAllTransactionLogs();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        return sqLiteManager.getPaginatedTransactionLogs(limit);
    }
}
