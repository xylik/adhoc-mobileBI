package hr.fer.poslovna.utility;

public final class QueryConstants {
	private QueryConstants(){}
	
	public static final String  QUERY_GET_FTABLES = "SELECT sifTablica, nazTablica, nazSQLTablica FROM tablica WHERE sifTipTablica = 1";
	
	public static String getMeasuresQuery(String factTableId) {
		return
				// @formatter:off
				"SELECT *" + 
				 " FROM tabAtribut, agrFun, tablica, tabAtributAgrFun" +                                          
				 " WHERE tabAtribut.sifTablica = " + factTableId +
				 " AND tabAtribut.sifTablica = tablica.sifTablica" + 
				 " AND tabAtribut.sifTablica  = tabAtributAgrFun.sifTablica" + 
				 " AND tabAtribut.rbrAtrib  = tabAtributAgrFun.rbrAtrib" + 
				 " AND tabAtributAgrFun.sifAgrFun = agrFun.sifAgrFun" + 
				 " AND tabAtribut.sifTipAtrib = 1" +
				 " ORDER BY tabAtribut.rbrAtrib";
				// @formatter:on
	}
	
	public static String getAttributesQuery(String factTableId) {
		// @formatter:off
		return
				"SELECT   dimTablica.nazTablica" +
			       ", dimTablica.nazSQLTablica  AS nazSqlDimTablica" +
			       ", cinjTablica.nazSQLTablica AS nazSqlCinjTablica" +
			       ", cinjTabAtribut.imeSQLAtrib" +
			       ", dimTabAtribut.imeSqlAtrib" +
			       ", tabAtribut.*"+
			  " FROM tabAtribut, dimCinj" +
			     ", tablica dimTablica, tablica cinjTablica" +
			     ", tabAtribut cinjTabAtribut, tabAtribut dimTabAtribut" +
			 " WHERE dimCinj.sifDimTablica  = dimTablica.sifTablica" +
			   " AND dimCinj.sifCinjTablica = cinjTablica.sifTablica" +
			   
			   " AND dimCinj.sifCinjTablica = cinjTabAtribut.sifTablica" +
			   " AND dimCinj.rbrCinj = cinjTabAtribut.rbrAtrib" +
			   
			   " AND dimCinj.sifDimTablica = dimTabAtribut.sifTablica" +
			   " AND dimCinj.rbrDim = dimTabAtribut.rbrAtrib" +

			   " AND tabAtribut.sifTablica  = dimCinj.sifDimTablica" +
			   " AND sifCinjTablica = " + factTableId +
			   " AND tabAtribut.sifTipAtrib = 2" +
			 " ORDER BY dimTablica.nazTablica, rbrAtrib";
		// @formatter:on
	}
}
