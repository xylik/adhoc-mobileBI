package hr.fer.poslovna.model;

import java.util.List;

public interface IResult{
	public int getAttributNum();
	public List<String> getAttributeNames();
	public int getRowNum();
	public List<IRow> getRows();
}
