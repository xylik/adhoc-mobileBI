package hr.fer.poslovna.fragments;

import java.util.List;

import hr.fer.poslovna.activity.R;
import hr.fer.poslovna.dao.DAOProvider;
import hr.fer.poslovna.dao.IDao;
import hr.fer.poslovna.listeners.IFrgCommunicator;
import hr.fer.poslovna.listeners.IFrgDataReceiveListener;
import hr.fer.poslovna.model.IResult;
import hr.fer.poslovna.model.IRow;
import hr.fer.poslovna.utility.App;
import hr.fer.poslovna.utility.AppSettings;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ResultFragment extends Fragment implements IFrgDataReceiveListener {
	public final static String CUSTOM_TAG = "result";
	private TextView lblMetaInfo;
	private LinearLayout lytTbl;
	private ProgressBar pBar;
	private Button btnPrevious;
	private Button btnNext;
	private IFrgCommunicator frgCommunicator;
	private int lastRowPos = 0;
	private IResult result;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		frgCommunicator = (IFrgCommunicator) activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		frgCommunicator.register(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		frgCommunicator.unregister(this);
		frgCommunicator = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.ui_frg_result, container, false);

		setViews(rootView);
		setActions();

		return rootView;
	}

	private void setViews(View rootView) {
		lblMetaInfo = (TextView) rootView.findViewById(R.id.result_lblMetaInfo);
		lytTbl = (LinearLayout) rootView.findViewById(R.id.result_lytTbl);
		pBar = (ProgressBar) rootView.findViewById(R.id.result_progressBar);
		btnPrevious = (Button) rootView.findViewById(R.id.result_btnPrevious);
		btnNext = (Button) rootView.findViewById(R.id.result_btnNext);
	}

	private void setActions() {
		btnPrevious.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnNext.setEnabled(true);
				if(lastRowPos - AppSettings.getRowsPerPage() <= 0) {
					btnPrevious.setEnabled(false);
				}
				lytTbl.removeAllViews();
				lastRowPos -= AppSettings.getRowsPerPage();
				View tableLayout = createTableLayout(result.getAttributeNames(), result.getRows(), AppSettings.getRowsPerPage(), lastRowPos);
				lytTbl.addView(tableLayout);
			}
		});
		
		btnNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnPrevious.setEnabled(true);
				if( lastRowPos + AppSettings.getRowsPerPage()  > result.getRowNum()) {
					btnNext.setEnabled(false);
				}
				lytTbl.removeAllViews();
				View tableLayout = createTableLayout(result.getAttributeNames(), result.getRows(), AppSettings.getRowsPerPage(), lastRowPos);
				lytTbl.addView(tableLayout);
				lastRowPos += AppSettings.getRowsPerPage();
			}
		});
	}

	private void processData(String data) {
		lastRowPos = AppSettings.getRowsPerPage();
		int initalStartPos = 0;
		new GetResultTask(initalStartPos).execute(data);
	}

	private View createTableLayout(List<String> attributeNames, List<IRow> rows, int rowsPerPage, int startPos) {
		Context ctx = App.getContext();
		TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
		TableLayout tableLayout = new TableLayout(ctx);
		tableLayout.setBackgroundColor(Color.BLACK);

		TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
		tableRowParams.setMargins(1, 1, 1, 1);
		tableRowParams.weight = 1;

		TableRow tblHeader = new TableRow(ctx);
		tblHeader.setBackgroundColor(Color.BLACK);
		for (String s : attributeNames) {
			TextView cell = new TextView(ctx);
			cell.setBackgroundColor(Color.WHITE);
			cell.setGravity(Gravity.CENTER);
			cell.setText(s);
			tblHeader.addView(cell, tableRowParams);
		}
		tableLayout.addView(tblHeader, tableLayoutParams);
		
		for(int i=startPos, resSize = rows.size(), end=i+rowsPerPage; i<resSize && i<end; i++) {
			IRow r = rows.get(i);
			TableRow tblRow = new TableRow(ctx);
			tblRow.setBackgroundColor(Color.BLACK);

			for(String s: r.getRowValues()) {
				TextView cell = new TextView(ctx);
				cell.setBackgroundColor(Color.WHITE);
				cell.setGravity(Gravity.CENTER);
				cell.setText(s);
				tblRow.addView(cell, tableRowParams);
			}
			tableLayout.addView(tblRow, tableLayoutParams);
		}
		
		ScrollView sv = new ScrollView(App.getContext());
		HorizontalScrollView hsv = new HorizontalScrollView(App.getContext());
		hsv.addView(tableLayout);
		sv.addView(hsv);
		return sv;
	}
	
	@Override
	public void receiveData(String srcFrgCustomTag, Object data) {
		if (srcFrgCustomTag.equals(QueryBuilder.CUSTOM_TAG)) {
			processData((String) data);
		}
	}

	@Override
	public String getSystemTag() {
		return getTag();
	}

	@Override
	public String getCustomTag() {
		return CUSTOM_TAG;
	}
	
	private class GetResultTask extends AsyncTask <String,Void,IResult>{
		private long startTime;
		private int startRowPos;
		
		public GetResultTask(int startRowPos) {
			this.startRowPos = startRowPos;
		}
		
	    @Override
	    protected void onPreExecute(){
	        pBar.setVisibility(View.VISIBLE);
	    }

	    @Override
	    protected IResult doInBackground(String... args) {
	    	startTime = System.currentTimeMillis();
			int queryParamPos = 0;
			IDao dao = DAOProvider.getDao();
			result = dao.executeQuery(args[queryParamPos]);
			return result;
	    }

	    @Override
	    protected void onPostExecute(IResult result) {
	    	long elapsedMs = System.currentTimeMillis() - startTime;
	    	String elapsedSec = String.format("%.2f", elapsedMs / 1000f);
	    	pBar.setVisibility(View.GONE);

			if(result != null) {
				lblMetaInfo.setText("Br. redaka: " + result.getRowNum() + " \nTrajanje: " + elapsedSec + "sec");
				View tableLayout = createTableLayout(result.getAttributeNames(), result.getRows(), AppSettings.getRowsPerPage(), startRowPos);
				lytTbl.removeAllViews();
				lytTbl.addView(tableLayout);
				btnNext.setEnabled(true);
			}
			else {
				lblMetaInfo.setText("Neispravan SQL upit!");
				Toast.makeText(App.getContext(), "Molim unesite ispravan SQL upit!", Toast.LENGTH_SHORT).show();
			}
	    }
	}
	
}
