package hr.fer.poslovna.dao;

import hr.fer.poslovna.model.IResult;


public interface IDao {
	public IResult executeQuery(String query);
}
