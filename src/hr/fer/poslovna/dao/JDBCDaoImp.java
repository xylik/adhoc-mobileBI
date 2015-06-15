package hr.fer.poslovna.dao;

import hr.fer.poslovna.model.IResult;
import hr.fer.poslovna.model.IRow;
import hr.fer.poslovna.model.Result;
import hr.fer.poslovna.model.Row;
import hr.fer.poslovna.utility.AppSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBCDaoImp implements IDao {

	@Override
	public IResult executeQuery(String query) {
		IResult result = null;
		String connectionUrl = AppSettings.getConnString();

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {

			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			con = DriverManager.getConnection(connectionUrl);

			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			result = generateResult(rs);
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (Exception e) {
				}
			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
				}
		}
		return result;
	}

	private IResult generateResult(ResultSet rs) {
//punjenje liste sa imenima atributa
		int attrNum = 0;
		List<String> attrNames = new ArrayList<String>();
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			attrNum = rsmd.getColumnCount();
			for(int i=1; i<=attrNum; i++) {
				attrNames.add(rsmd.getColumnName(i).trim());
			}
		} catch (SQLException e1) {
			throw new DAOException(e1.getMessage());
		}
		
//punjenje liste sa redcima
		int rowNum = 0;
		List<IRow> rows = new ArrayList<IRow>();
		try {
			while (rs.next()) {
				rowNum++;
				List<String> rowValues = new ArrayList<String>();
				for(int i=1; i<=attrNum; i++) {
					rowValues.add(rs.getString(i).trim());
				}
				rows.add(new Row(rowValues));
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage());
		}
		return new Result(attrNames, rows);
	}

}
