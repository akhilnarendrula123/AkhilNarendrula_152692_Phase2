package com.cg.paymentwallet.dbutil;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
public class DBUtil {
	static Connection con;
	static Properties prop;
	
	static
	{
		try
		{
			prop= new Properties();
			File file = new File("db.properties");
			FileInputStream fin = new FileInputStream(file);
			prop.load(fin);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Connection getConnect() 
	{
		
			String driver = prop.getProperty("driver");
			String user = prop.getProperty("username");
			String pass = prop.getProperty("password");
			String url = prop.getProperty("url");
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				con = DriverManager.getConnection(url,user,pass);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("connection established ");
			return con;
		}
	
}
