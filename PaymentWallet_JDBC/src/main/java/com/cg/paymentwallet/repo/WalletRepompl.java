package com.cg.paymentwallet.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.cg.paymentwallet.beans.Customer;
import com.cg.paymentwallet.dbutil.DBUtil;
import com.cg.paymentwallet.exception.IPaymentWalletException;
import com.cg.paymentwallet.exception.PaymentWalletException;

public class WalletRepompl implements IWalletRepo {

	private static Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	static {
		connection = new DBUtil().getConnect();

	}

	public String createAccount(Customer customerBean) {
		String sql = "INSERT into Customer_Details values (?, ?, ?, ?, ?, ?, ?)";
		String sql1="INSERT into transactions values(?,?)";
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, customerBean.getName());
			preparedStatement.setString(2, String.valueOf(customerBean.getAge()));
			preparedStatement.setString(3, customerBean.getEmailId());
			preparedStatement.setString(4, customerBean.getPassword());
			preparedStatement.setString(5, customerBean.getMobileNo());
			preparedStatement.setString(6, String.valueOf(customerBean.getBalance()));
			preparedStatement.setString(7, customerBean.getGender());
			preparedStatement.executeUpdate();
			preparedStatement = connection.prepareStatement(sql1);
			preparedStatement.setString(1, customerBean.getName());
			preparedStatement.setString(2, customerBean.getMobileNo());
			preparedStatement.executeUpdate();
			
		} catch (SQLException sqlException) {
			System.out.println(sqlException.getMessage());
		}
		String phone = customerBean.getMobileNo();
		return phone;
	}

	public Customer getCustomerDetails(String mob) {
		Customer search = null;
		String sql = "SELECT * FROM Customer_Details WHERE custContact = ?";
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, mob);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				String name = resultSet.getString("custName");
				String mobileNo = resultSet.getString("custContact");
				String password = resultSet.getString("password");
				int age = resultSet.getInt("custAge");
				String gender = resultSet.getString("custGender");
				String email = resultSet.getString("custEmail");
				double balance = resultSet.getDouble("custBalance");
				search = new Customer();

				search.setName(name);
				search.setMobileNo(mobileNo);
				search.setAge(age);
				search.setPassword(password);
				search.setGender(gender);
				search.setEmailId(email);
				search.setBalance(balance);
			}
		} catch (SQLException sqlException) {
			System.out.println(sqlException.getMessage());
		}
		return search;
	}

	public Customer showBalance(String custContact) {
		Customer search = null;
		String sql = "SELECT custName,custContact,custBalance FROM Customer_Details  WHERE custContact = ?";
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, custContact);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				String mobileNo = resultSet.getString("custContact");
				String name = resultSet.getString("custName");
				double balance = resultSet.getDouble("custBalance");
				search = new Customer();

				search.setName(name);
				search.setMobileNo(mobileNo);
				search.setBalance(balance);
			}
		} catch (SQLException sqlException) {
			System.out.println(sqlException.getMessage());
		}

		return search;
	}

	public boolean withdrawAmount(double withdrawAmt, String custContact) {
		boolean result = false;
		Customer bean = getCustomerDetails(custContact);
		if (bean != null) {
			if (bean.getBalance() > withdrawAmt) {
				bean.setBalance(bean.getBalance() - withdrawAmt);

				try {
					String sql = "UPDATE Customer_Details set custBalance = custBalance-? where custContact = ?";
					
					PreparedStatement pstmt = connection.prepareStatement(sql);
					pstmt.setString(1, String.valueOf(withdrawAmt));
					pstmt.setString(2, custContact);
					result = pstmt.execute();
					
					 String QueryT1 = "select transactions from transactions where custContact=?";
					 PreparedStatement pstmt2 = connection.prepareStatement(QueryT1);
					
					    pstmt2.setString(1, custContact);
					    ResultSet tra= pstmt2.executeQuery();
					    String trans = null;
					    while(tra.next()) {
					    	trans=tra.getString(1);
					    }
					     
	               String transactions = trans.concat(" Withdrawn: " + withdrawAmt);
			    String QueryT = "Update transactions set transactions=? where custContact=?";
				PreparedStatement pstmt1 = connection.prepareStatement(QueryT);
				    pstmt1.setString(1,transactions);
				    pstmt1.setString(2, custContact);
				    
					pstmt1.executeUpdate();
				
				} catch (SQLException e) {
					e.printStackTrace();
				}
				result = true;
			}
		}
		return result;
	}

	public boolean depositAmount(double depositAmt, String custContact) {

		boolean result = false;
		Customer bean = getCustomerDetails(custContact);
		if (bean != null) {
			//String sql = "UPDATE Customer_Details set custBalance = custBalance+? where custContact = ?";
			bean.setBalance(bean.getBalance() + depositAmt);

			try {
//				PreparedStatement pstmt = connection.prepareStatement(sql);
//				pstmt.setDouble(1, depositAmt);
//				pstmt.setString(2, custContact);
//				String transactions = "Deposited: " + depositAmt;
//				String QueryT = "INSERT into Transactions values(" + transactions + "," + custContact + ")";
//				PreparedStatement pstmt1 = connection.prepareStatement(QueryT);
//				pstmt1.executeUpdate();
//				result = pstmt.execute();
				String sql = "UPDATE Customer_Details set custBalance = custBalance+? where custContact = ?";
				
				PreparedStatement pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, String.valueOf(depositAmt));
				pstmt.setString(2, custContact);
				result = pstmt.execute();
				
				 String QueryT1 = "select transactions from transactions where custContact=?";
				 PreparedStatement pstmt2 = connection.prepareStatement(QueryT1);
				
				    pstmt2.setString(1, custContact);
				    ResultSet tra= pstmt2.executeQuery();
				    String trans = null;
				    while(tra.next()) {
				    	trans=tra.getString(1);
				    }
				     
               String transactions = trans.concat(" Deposited: " + depositAmt);
		    String QueryT = "Update transactions set transactions=? where custContact=?";
			PreparedStatement pstmt1 = connection.prepareStatement(QueryT);
			    pstmt1.setString(1,transactions);
			    pstmt1.setString(2, custContact);
			    
				pstmt1.executeUpdate();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			result = true;
		}
		return result;
	}

	public boolean fundTransfer(String senderCont, String receiverCont, double custAmount)
			throws PaymentWalletException, SQLException {
		
		withdrawAmount(custAmount,senderCont);
		depositAmount(custAmount,receiverCont);
		/*boolean result = false;
		Customer sender = getCustomerDetails(senderCont);
		Customer receiver = getCustomerDetails(receiverCont);
		connection = DBUtil.getConnect();
		if (sender != null && receiver != null) {
			if (sender.getBalance() > custAmount) {
				String RQuery = "SELECT * from Customer_Details where custContact=?";
				String SQuery = "UPDATE Customer_Details  SET custBalance = custBalance-? where custContact=?";

				PreparedStatement recieverPstmt = connection.prepareStatement(RQuery);
				PreparedStatement senderPstmt = connection.prepareStatement(SQuery);

				recieverPstmt.setString(1, receiverCont);
				senderPstmt.setDouble(1, custAmount);
				senderPstmt.setString(2, senderCont);
				String transactions = "Transferred: " + custAmount + " to " + receiverCont;
				String QueryT = "INSERT into Transactions values(" + transactions + "," + senderCont + ")";
				PreparedStatement pstmt1 = connection.prepareStatement(QueryT);
				pstmt1.executeUpdate();

				ResultSet rs = recieverPstmt.executeQuery();
				while (rs.next()) {
					String recieverQuery1 = "UPDATE Customer_Details SET custBalance= custBalance+? where custContact=?";
					PreparedStatement recieverPstmt1 = connection.prepareStatement(recieverQuery1);

					recieverPstmt1.setDouble(1, custAmount);
					recieverPstmt1.setString(2, receiverCont);
					recieverPstmt1.executeUpdate();
					senderPstmt.executeUpdate();

					String transaction1 = "Received: " + custAmount + " from " + senderCont;
					String QueryR = "INSERT into Transactions values(" +  transaction1+ "," + receiverCont + ")";
					PreparedStatement pstmt2 = connection.prepareStatement(QueryR);
					pstmt2.executeUpdate();
					result = true;
				}
			} else {
				result = false;
			}
			}
*/		
		

		return true;
	}

	public Customer login(String mobileNo, String password) throws PaymentWalletException {
		Customer custLogin = getCustomerDetails(mobileNo);

		if (custLogin.getMobileNo().equals(mobileNo) && custLogin.getPassword().equals(password)) {
			return custLogin;
		} else
			throw new PaymentWalletException(IPaymentWalletException.ERROR7);
	}

	public StringBuilder printTransactions(String mobileNumber) {
		String sql = "SELECT * from Transactions where custContact = ?";
		String transactions = null;
		StringBuilder allTransactions = new StringBuilder();
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, mobileNumber);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				transactions = resultSet.getString("transactions");
				allTransactions.append(transactions + "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return allTransactions;
	}
}