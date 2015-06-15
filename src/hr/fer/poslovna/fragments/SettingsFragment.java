package hr.fer.poslovna.fragments;

import hr.fer.poslovna.activity.R;
import hr.fer.poslovna.adapters.SectionsPagerAdapter;
import hr.fer.poslovna.listeners.IFrgCommunicator;
import hr.fer.poslovna.listeners.IFrgDataReceiveListener;
import hr.fer.poslovna.utility.App;
import hr.fer.poslovna.utility.AppSettings;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsFragment extends Fragment implements IFrgDataReceiveListener {
	public final static String  CUSTOM_TAG = "settings";
	private IFrgCommunicator frgCommunicator;
	private EditText txtConnString;
	private EditText txtMaxRowsPerPage;
	private Button btnSaveChanges;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		frgCommunicator = (IFrgCommunicator)activity;
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
		View rootView = inflater.inflate(R.layout.ui_frg_settings, container, false);
		
		setViews(rootView);
		setActions();
		
		txtConnString.setText(AppSettings.getConnString());
		txtMaxRowsPerPage.setText(Integer.toString(AppSettings.getRowsPerPage()));
		return rootView;
	}
	
	private void setViews(View rootView) {
		txtConnString = (EditText)rootView.findViewById(R.id.settings_txtConnString);
		txtMaxRowsPerPage = (EditText)rootView.findViewById(R.id.settings_txtRowsPerPage);
		btnSaveChanges = (Button)rootView.findViewById(R.id.settings_btnSave);
	}
	
	private void setActions() {
		btnSaveChanges.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int maxRowsPerPage;
				String err = "Greška, broj redaka mora biti pozitivan broj !";
				try {
					maxRowsPerPage = Integer.parseInt(txtMaxRowsPerPage.getText().toString());
				} catch(NumberFormatException ex) {
					Toast.makeText(App.getContext(), err, Toast.LENGTH_SHORT).show();
					return;
				}
				if(maxRowsPerPage <= 0) {
					Toast.makeText(App.getContext(), err, Toast.LENGTH_SHORT).show();
				}


				String connStr = txtConnString.getText().toString();
				if(connStr.isEmpty()) {
					Toast.makeText(App.getContext(), "Greška, konekcijski string je prazan !", Toast.LENGTH_SHORT).show();
				}
				AppSettings.setRowsPerPage(maxRowsPerPage);
				AppSettings.setJdbcConnString(connStr);
				frgCommunicator.requestPageChange(SectionsPagerAdapter.ATTR_SELECTER_POS);
			}
		});
	}
	
	private void processData(String data) {
	}

	@Override
	public void receiveData(String srcFrgCustomTag, Object data) {
	}

	@Override
	public String getSystemTag() {
		return getTag();
	}

	@Override
	public String getCustomTag() {
		return CUSTOM_TAG;
	}

}
