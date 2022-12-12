package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SQLiteManager extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BankDetails.db";
    public SQLiteManager( Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE if not exists account (acc_no VARCHAR(10) primary key, bank TEXT(50), acc_holder TEXT(50),balance NUMERIC(14,2),deleted INT(1) default 0)");
        sqLiteDatabase.execSQL("CREATE TABLE if not exists transactions (transaction_id INTEGER primary key AUTOINCREMENT,acc_no VARCHAR(10) , transaction_date DATE, expenseType TEXT(15),amount NUMERIC(14,2), deleted INT(1) default 0, FOREIGN KEY(acc_no) REFERENCES account(acc_no))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS account");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(sqLiteDatabase);
    }

    public void addAccount (Account account){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("acc_no",account.getAccountNo());
        contentValues.put("bank", account.getBankName());
        contentValues.put("acc_holder", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());
        sqLiteDatabase.insert("account", null, contentValues);

    }
    public void removeAccount (String acc_no){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deleted",1);
        sqLiteDatabase.update("account",contentValues,"acc_no = ?", new String[]{acc_no});
    }

    public void addtransaction (String date, String accountNo, String expenseType, double amount){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("acc_no", accountNo);
        contentValues.put("expenseType", expenseType);
        contentValues.put("transaction_date", date);
        contentValues.put("amount", amount);
        sqLiteDatabase.insert("transactions", null, contentValues);
    }
    public void updateBalance (String acc_no,double balance){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("balance",balance);
        sqLiteDatabase.update("account",contentValues,"acc_no = ? ", new String[]{acc_no});
    }
    public ArrayList<Account> getAllaccount(){
        ArrayList<Account> arrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM account where deleted = 0",null);
        c.moveToFirst();
        while(c.isAfterLast()==false){
            Account account = new Account(c.getString(c.getColumnIndex("acc_no")),c.getString(c.getColumnIndex("bank")),c.getString(c.getColumnIndex("acc_holder")),c.getDouble(c.getColumnIndex("balance")));
            arrayList.add(account);
            c.moveToNext();
        }
        return arrayList;
    }

    public Account getAccount(String accNo){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM account WHERE acc_no= '"+accNo +"' and deleted=0", null );
        c.moveToFirst();
        while(c.isAfterLast() == false){
            return new Account(c.getString(c.getColumnIndex("acc_no")),c.getString(c.getColumnIndex("bank")),c.getString(c.getColumnIndex("acc_holder")),c.getDouble(c.getColumnIndex("balance")));
        }
        return null;
    }

    public ArrayList<String> getAccountNumbersList(){
        ArrayList<String> accnumlist = new ArrayList<String>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c =  sqLiteDatabase.rawQuery( "SELECT * FROM account WHERE deleted=0", null );
        c.moveToFirst();

        while(c.isAfterLast() == false){
            accnumlist.add(c.getString(c.getColumnIndex("acc_no")));
            c.moveToNext();
        }
        return accnumlist;
    }

    public ArrayList<Transaction> getAllTransactionLogs(){
        ArrayList<Transaction> translog = new ArrayList<Transaction>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c =  sqLiteDatabase.rawQuery( "SELECT * FROM transactions WHERE deleted=0", null );
        c.moveToFirst();
        while(c.isAfterLast() == false){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = dateFormat.parse(c.getString(c.getColumnIndex("transaction_date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction account;
            if(c.getString(c.getColumnIndex("expenseType"))== "INCOME") {
                account = new Transaction(date, c.getString(c.getColumnIndex("acc_no")),ExpenseType.INCOME , c.getDouble(c.getColumnIndex("amount")));
            }else{
                account = new Transaction(date, c.getString(c.getColumnIndex("acc_no")),ExpenseType.EXPENSE , c.getDouble(c.getColumnIndex("amount")));
            }
            translog.add(account);
            c.moveToNext();
        }
        return translog;
    }

    public ArrayList<Transaction> getPaginatedTransactionLogs(int limit){
        ArrayList<Transaction> pagtranslog = new ArrayList<Transaction>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c =  sqLiteDatabase.rawQuery( "SELECT * FROM transactions WHERE deleted=0 ORDER BY transaction_id DESC LIMIT "+Integer.toString(limit), null );
        c.moveToFirst();
        while(c.isAfterLast() == false){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = dateFormat.parse(c.getString(c.getColumnIndex("transaction_date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction account;
            if(c.getString(c.getColumnIndex("expenseType"))== "INCOME") {
                account = new Transaction(date, c.getString(c.getColumnIndex("acc_no")),ExpenseType.INCOME , c.getDouble(c.getColumnIndex("amount")));
            }else{
                account = new Transaction(date, c.getString(c.getColumnIndex("acc_no")),ExpenseType.EXPENSE , c.getDouble(c.getColumnIndex("amount")));
            }
            pagtranslog.add(account);
            c.moveToNext();
        }
        return pagtranslog;
    }
    public boolean isAccountValid(String accountNo) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c =  sqLiteDatabase.rawQuery( "SELECT * FROM account WHERE acc_no= '"+accountNo +"' AND deleted=0", null );
        c.moveToFirst();
        while(c.isAfterLast() == false){
            return true;
        }
        return false;
    }

}
