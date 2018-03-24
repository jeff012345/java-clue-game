package cis579.ai;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.h2.tools.Server;

import cis579.ai.de.ResultDE;

public class Database {
	
	private static final String CONNECTION_STR = "jdbc:h2:tcp://localhost/~/results";
	
	private static Database database = null;
	
	private Connection connection;
	
	private Database() throws SQLException {
		getConnection(0);
		
		System.out.println("DB Opened");
	}
	
	private void getConnection(int retry) {
		if(retry == 5) {
			System.err.println("Could not obtain DB connection");
			return;
		}
		
		try {
			this.connection = DriverManager.getConnection(CONNECTION_STR);
		} catch(SQLException e) {
			try {
				Server.createTcpServer("-tcpAllowOthers", "-tcpDaemon", "-trace").start();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.err.println("Could not start H2 TCP server");
				return;
			}
			
			getConnection(retry + 1);
		}
	}
	
	
	public static Database getInstance() {
		if(database == null) {
			try {
				database = new Database();
				database.init();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return database;
	}
	
	private void init() throws SQLException {
		
		this.connection.setAutoCommit(true);
		
		Statement s = this.connection.createStatement();
		s.executeUpdate("CREATE TABLE IF NOT EXISTS TBL01_COEFFICIENT_LOG (id BIGINT auto_increment, a DOUBLE, b DOUBLE, c DOUBLE, success_rate DOUBLE)");
		s.close();
		
		System.out.println("DB initialized");
	}
	
	public void close() {
		System.out.println("Closing DB");
		try {
			connection.close();
			Server.shutdownTcpServer(CONNECTION_STR, "", true, true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		database = null;
	}
	
	public void logCoefficientResult(double a, double b, double c, double successRate) {
		try {
			PreparedStatement s = this.connection.prepareStatement("INSERT INTO TBL01_COEFFICIENT_LOG (a, b, c, success_rate) VALUES (?,?,?,?)");
			s.setDouble(1, a);
			s.setDouble(2, b);
			s.setDouble(3, c);
			s.setDouble(4, successRate);
			int cnt = s.executeUpdate();
			if(cnt != 0) {
				System.out.println("Wrote result to DB");
			} else {
				System.out.println("Result save to DB failed");
			}
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<ResultDE> getAllResults(){
		ArrayList<ResultDE> results = new ArrayList<>(); 
		
		try {
			Statement s = this.connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM TBL01_COEFFICIENT_LOG");
			
			ResultDE result;
			double[] coeffs;
			
			while(rs.next()) {
				 coeffs = new double[] {
					 rs.getDouble("a"),
					 rs.getDouble("b"),
					 rs.getDouble("c") 
				 };
				 
				 result = new ResultDE();
				 result.setCoefficients(coeffs);
				 result.setSuccessRate(rs.getDouble("success_rate"));
				 
				 results.add(result);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return results;
	}
	
}
