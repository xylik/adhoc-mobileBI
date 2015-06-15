package hr.fer.poslovna.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import hr.fer.poslovna.model.IResult;
import hr.fer.poslovna.model.IRow;
import hr.fer.poslovna.model.Result;
import hr.fer.poslovna.model.Row;

public class SQLiteDaoImp implements IDao {
	private DB db;
	
	public SQLiteDaoImp(Context ctx) {
		db = new DB(ctx);
		
		//samo jednom se pokrece, tj importa dump baze
		db.importDBDump();
	}

	@Override
	public IResult executeQuery(String query) {
		if(! db.open()) {
			throw new DAOException("Database open err!");
		}

		IResult result = new Result(new ArrayList<String>(), new ArrayList<IRow>());
		Cursor cs;
		try {
			cs = db.executeQuery(query);
		} catch (Exception e) {
			return null;
		}
		
		//punjenje liste sa imenima atributa
		int collNum = 0;
		List<String> collNames = new ArrayList<String>();
		collNum = cs.getColumnCount();
		for(int i=0; i<collNum; i++) {
			collNames.add(cs.getColumnName(i).trim()); //trimana imena kolumni
		}
		
		//punjenje liste sa redcima
		int rowNum = 0;
		List<IRow> rows = new ArrayList<IRow>();
		if (cs.moveToFirst()) {
			do {
				rowNum++;
				List<String> rowValues = new ArrayList<String>();
				for(int i=0; i<collNum; i++) {
					rowValues.add(cs.getString(i).trim()); //trimani podaci
				}
				rows.add(new Row(rowValues));
			} while (cs.moveToNext());
			result = new Result(collNames, rows);
		}
		return result;
	}
}
