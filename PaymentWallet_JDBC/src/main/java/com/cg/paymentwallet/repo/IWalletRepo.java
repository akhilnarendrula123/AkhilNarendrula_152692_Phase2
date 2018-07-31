package com.cg.paymentwallet.repo;

import java.sql.SQLException;

import com.cg.paymentwallet.beans.Customer;
import com.cg.paymentwallet.exception.PaymentWalletException;

public interface IWalletRepo {
	public String createAccount(Customer customerBean);

	public Customer showBalance(String custContact);

	public boolean withdrawAmount(double withdrawAmt, String custContact);

	public boolean depositAmount(double depositAmt, String custContact);

	public boolean fundTransfer(String senderCont, String receiverCont, double custAmount)
			throws PaymentWalletException, SQLException;

	public StringBuilder printTransactions(String mob);

	public Customer login(String mobileNum, String password) throws PaymentWalletException;

	public Customer getCustomerDetails(String mob);
}
