package hr.fer.poslovna.utility;

public final class Resources {
	private Resources(){}
	
	public static String getString(int id) {
		return App.getContext().getString(id);
	}
	
	public static String[] getStringArray(int id) {
		return App.getContext().getResources().getStringArray(id);
	}
}
