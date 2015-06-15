package hr.fer.poslovna.model;

import java.util.List;

public class Row implements IRow{
	private List<String> rowValues;

	public Row() {}
	
	public Row(List<String> rowValues) {
		super();
		this.rowValues = rowValues;
	}

	@Override
	public List<String> getRowValues() {
		return rowValues;
	}

	@Override
	public int getAttributeNum() {
		return rowValues.size();
	}
}
