package hr.fer.poslovna.activity;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import hr.fer.poslovna.dao.DAOProvider;
import hr.fer.poslovna.dao.IDao;
import hr.fer.poslovna.model.IResult;
import hr.fer.poslovna.utility.App;

public class Main extends ActionBarActivity {
	private Button btnAdHoc;
	private Button btnBenchmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        
        setViewElements();
        initActivity();
    }
	
	private void setViewElements() {
		btnAdHoc = (Button)findViewById(R.id.main_btnAdHoc);
		btnBenchmark = (Button) findViewById(R.id.main_btnBenchmark);
	}
	
	private void initActivity() {
		btnAdHoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Main.this, AdHoc.class));
			}
		});
		
		btnBenchmark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(Main.this, R.string.main_tstBenchmarkInfo, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	//TODO izbrisati u deploymentu
	private void testJDBCDaoImp() {
		String query_getFTables = "SELECT sifTablica, nazTablica FROM tablica WHERE sifTipTablica = 1";
		ConnectivityManager connMgr = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new FTablesQueryTask().execute(query_getFTables);
		} else {
			Toast.makeText(App.getContext(), "Mreža nedostupna!", Toast.LENGTH_SHORT).show();
		}
	}
	
	//TODO izbrisati u deploymentu
	private class FTablesQueryTask extends AsyncTask<String, Void, IResult> {
		@Override
		protected IResult doInBackground(String... params) {
			IDao dao = DAOProvider.getDao();
			IResult result_getFTables = dao.executeQuery(params[0]);
			return result_getFTables;
		}

		@Override
		protected void onPostExecute(IResult result) {
			Toast.makeText(Main.this, "Uspjelo", Toast.LENGTH_SHORT).show();
		}
	}
}
