package hr.fer.poslovna.dao;

public class DAOException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DAOException(String msg) {
		super(msg);
	}
	
	public DAOException(Throwable err) {
		super(err);
	}
	
	public DAOException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
