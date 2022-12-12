package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    private SQLiteManager sqLiteManager;
    public PersistentAccountDAO(SQLiteManager sqLiteManager){
        this.sqLiteManager = sqLiteManager;
    }

    @Override
    public List<String> getAccountNumbersList() {
        return sqLiteManager.getAccountNumbersList();
    }

    @Override
    public List<Account> getAccountsList() {
        return sqLiteManager.getAllaccount();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account result = sqLiteManager.getAccount(accountNo);
        if(result==null){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        return sqLiteManager.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        sqLiteManager.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if(!sqLiteManager.isAccountValid(accountNo)){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        sqLiteManager.removeAccount(accountNo);
    }
    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if(!sqLiteManager.isAccountValid(accountNo)){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = getAccount(accountNo);
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        sqLiteManager.updateBalance(accountNo,account.getBalance());
    }
}
