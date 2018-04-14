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

	private static final boolean TCP = false;
	private static final String CONNECTION_STR = "jdbc:h2:~/results";

	private static Database database = null;

	private Connection connection;

	private Database() throws SQLException {
		if(TCP)
			this.getTcpConnection(0);
		else
			this.getEmbeddedConnection();

		System.out.println("DB Opened");
	}

	private void getTcpConnection(final int retry) {
		if(retry == 5) {
			System.err.println("Could not obtain DB connection");
			return;
		}

		try {
			this.connection = DriverManager.getConnection(CONNECTION_STR);
		} catch(final SQLException e) {
			try {
				Server.createTcpServer("-tcpAllowOthers", "-tcpDaemon", "-trace").start();
			} catch (final SQLException e1) {
				e1.printStackTrace();
				System.err.println("Could not start H2 TCP server");
				return;
			}

			this.getTcpConnection(retry + 1);
		}
	}

	private void getEmbeddedConnection() throws SQLException {
		this.connection = DriverManager.getConnection(CONNECTION_STR + System.currentTimeMillis());
	}

	public static Database getInstance() {
		if(database == null) {
			try {
				database = new Database();
				database.init();

			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}

		return database;
	}

	private void init() throws SQLException {

		this.connection.setAutoCommit(true);

		final Statement s = this.connection.createStatement();
		s.executeUpdate("CREATE TABLE IF NOT EXISTS TBL01_COEFFICIENT_LOG (id BIGINT auto_increment, a DOUBLE, b DOUBLE, c DOUBLE, d DOUBLE, e DOUBLE, success_rate DOUBLE, game_guid VARCHAR(100))");
		s.close();

		System.out.println("DB initialized");
	}

	public void close() {
		System.out.println("Closing DB");
		try {
			this.connection.close();
			if(TCP) {
				Server.shutdownTcpServer("tcp://localhost", "", true, false);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}

		database = null;
	}

	public void logCoefficientResult(final ResultDE result) {
		try {
			final double[] coeffs = result.getCoefficients();

			final PreparedStatement s = this.connection.prepareStatement("INSERT INTO TBL01_COEFFICIENT_LOG (a, b, c, d, e, success_rate, game_guid) VALUES (?,?,?,?,?,?,?)");
			s.setDouble(1, coeffs[0]);
			s.setDouble(2, coeffs[1]);
			s.setDouble(3, coeffs[2]);
			s.setDouble(4, coeffs[3]);
			s.setDouble(5, coeffs[4]);
			s.setDouble(6, result.getSuccessRate());
			s.setString(7, result.getGameGuid());

			final int cnt = s.executeUpdate();
			if(cnt != 0) {
				System.out.println("Wrote result to DB");
			} else {
				System.out.println("Result save to DB failed");
			}
			s.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public List<ResultDE> getAllResults(){
		final ArrayList<ResultDE> results = new ArrayList<>();

		try {
			final Statement s = this.connection.createStatement();
			final ResultSet rs = s.executeQuery("SELECT * FROM TBL01_COEFFICIENT_LOG");

			ResultDE result;
			double[] coeffs;

			while(rs.next()) {
				coeffs = new double[] {
						rs.getDouble("a"),
						rs.getDouble("b"),
						rs.getDouble("c"),
						rs.getDouble("d"),
						rs.getDouble("e")
				};

				result = new ResultDE();
				result.setCoefficients(coeffs);
				result.setSuccessRate(rs.getDouble("success_rate"));

				results.add(result);
			}

		} catch (final SQLException e) {
			e.printStackTrace();
		}

		return results;
	}

}
