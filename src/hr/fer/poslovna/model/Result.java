package hr.fer.poslovna.model;

import java.util.ArrayList;
import java.util.List;

public class Result implements IResult {
	private int attributNum;
	private List<String> attributeNames;
	private int rowNum;
	private List<IRow> rows;
	private List<List<String>> table;

	public Result(List<String> attributeNames, List<IRow> rows) {
		super();
		this.attributNum = attributeNames.size();
		this.attributeNames = attributeNames;
		this.rowNum = rows.size();
		this.rows = rows;
		
		table = new ArrayList<List<String>>();
		for(int i=0, end = rows.size(); i<end; i++) {
			table.add(rows.get(i).getRowValues());
		}
	}

	@Override
	public int getAttributNum() {
		return attributNum;
	}

	@Override
	public List<String> getAttributeNames() {
		return attributeNames;
	}

	@Override
	public int getRowNum() {
		return rowNum;
	}

	@Override
	public List<IRow> getRows() {
		return rows;
	}
}
