package com.accurate.solutions.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.codehaus.jackson.map.ObjectMapper;

import com.accurate.solutions.entity.Company;

/**
 * Servlet implementation class CompanyInfo
 */
@WebServlet("/companyInfo")
public class CompanyInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		PrintWriter out = response.getWriter();
		try {
			// JNDI
			System.out.println("==============================================JNDI====================================");
			Context aContext = new InitialContext();
			DataSource aDataSource = (DataSource) aContext.lookup("java:comp/env/jdbc/accurate");
			conn = aDataSource.getConnection();
			System.out.println("============================================conn======================================"+conn);
			// JNDI end

			// raw JDBC for the same SAMPLEsample DB2 database
			// Class.forName("COM.ibm.db2.jdbc.app.DB2Driver"); //need db2java.zip in the
			// path
			// conn = DriverManager.getConnection("jdbc:db2:sample"); //DB2
			// raw JDBC end

			String sql = "select Id, companyname, companyphone,companyaddress,stockvalue from accurate";
			Statement stm = conn.createStatement();
			ResultSet rst = stm.executeQuery(sql);
			List<Company> companies = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			
			for (int i = 0; (i < 10 && rst.next()); i++) // only print 10 names
			{	
				Company company = new Company();
				company.setId(rst.getString("Id"));
				company.setCompanyName(rst.getString("companyname"));
				company.setCompanyPhone(rst.getString("companyphone"));
				company.setCompanyAddress(rst.getString("companyaddress"));
				company.setStockValue(rst.getString("stockvalue"));
				companies.add(company);
				
			}
			String data = mapper.writeValueAsString(companies);
			Object json = mapper.readValue(data, Object.class);
			out.println(json);
			if (conn != null) {
				conn.close();
				System.out.println("Successfully closed");
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
