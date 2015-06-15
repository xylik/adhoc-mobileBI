package hr.fer.poslovna.dao;

import hr.fer.poslovna.utility.App;


public class DAOProvider {
	private static IDao dao;
	
	public static synchronized IDao getDao() {
		if(dao == null) {
			dao = new SQLiteDaoImp(App.getContext());
		}
		return dao;
	}
	
	public static synchronized void setDao(IDao dao) {
		DAOProvider.dao = dao;
	}
}
