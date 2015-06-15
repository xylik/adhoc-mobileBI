package hr.fer.poslovna.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hr.fer.poslovna.activity.R;
import hr.fer.poslovna.adapters.MyExpandableListAdapter;
import hr.fer.poslovna.adapters.SectionsPagerAdapter;
import hr.fer.poslovna.dao.DAOProvider;
import hr.fer.poslovna.dao.IDao;
import hr.fer.poslovna.dao.JDBCDaoImp;
import hr.fer.poslovna.dao.SQLiteDaoImp;
import hr.fer.poslovna.gui.AbstractSpinner.Item;
import hr.fer.poslovna.gui.MultiSelectionSpinner;
import hr.fer.poslovna.gui.MySpinner;
import hr.fer.poslovna.listeners.IFrgCommunicator;
import hr.fer.poslovna.listeners.IFrgDataReceiveListener;
import hr.fer.poslovna.listeners.IMySpinnerListener;
import hr.fer.poslovna.model.AsyncValueWrapper;
import hr.fer.poslovna.model.Group;
import hr.fer.poslovna.model.IResult;
import hr.fer.poslovna.model.IRow;
import hr.fer.poslovna.utility.App;
import hr.fer.poslovna.utility.QueryConstants;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AttributeSelecter extends Fragment implements IMySpinnerListener, IFrgDataReceiveListener{
	public static final String CUSTOM_TAG = "attrSelecter";
	private final String SPN_TABLES_TITLE = "Tablice: ";
	private final String SPN_MEASURE_TITLE = "Mjere: ";
	private final String LST_ATTRIBUTES_TITLE = "Atributi: ";
	private final int DEFAULT_SELECT_TBL_INDX = 0;
	private RadioGroup rdbGrpConnType;
	private MySpinner spnTables;
	private MultiSelectionSpinner spnMeasures;
	private ExpandableListView lstAttributes;
	public Button btnExecute;
	private IFrgCommunicator frgCommunicator;
	public static Activity activity;

	public AttributeSelecter() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		frgCommunicator = (IFrgCommunicator)activity;
		AttributeSelecter.activity = activity;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		frgCommunicator.register(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		frgCommunicator.unregister(this);
		frgCommunicator = null;
		AttributeSelecter.activity = null;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.ui_frg_attrselecter, container, false);

		setViews(rootView);
		setActions();
		initSpnTables(spnTables, Integer.toString(DEFAULT_SELECT_TBL_INDX) );
		
		return rootView;
	}
	
	private void setViews(View rootView) {
		rdbGrpConnType = (RadioGroup) rootView.findViewById(R.id.queryBuilder_rdbGrpConnType);
		spnTables = (MySpinner) rootView.findViewById(R.id.queryBuilder_spnTables);
		spnMeasures = (MultiSelectionSpinner) rootView.findViewById(R.id.queryBuilder_spnMeasures);
	    lstAttributes = (ExpandableListView) rootView.findViewById(R.id.queryBuilder_lstAttributes);
		btnExecute = (Button) rootView.findViewById(R.id.queryBuilder_btnExecute);
	}
	
	private void setActions() {
		spnTables.register(this);
		
		btnExecute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Item selTable = spnTables.getSelectedItemWithValue();
				List<Item> selMeasures = spnMeasures.getSelectedItemsWithValues();
				List<Group> selAttributes = ((MyExpandableListAdapter)lstAttributes.getExpandableListAdapter()).getSelectedGroups();
				HashMap<String, Object> data = new HashMap<String, Object>();
				data.put("selTables", selTable);
				data.put("selMeasures", selMeasures);
				data.put("selAttributes", selAttributes);
				frgCommunicator.dispatchData(CUSTOM_TAG, QueryBuilder.CUSTOM_TAG, data);
				frgCommunicator.requestPageChange(SectionsPagerAdapter.QUERY_BUILDER_POS);
			}
		});
		
		rdbGrpConnType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int rdbJdbcPos = 1;
				int radioButtonID = group.getCheckedRadioButtonId();
				View radioButton = group.findViewById(radioButtonID);
				int idx = group.indexOfChild(radioButton);
				if(idx == rdbJdbcPos) {
					DAOProvider.setDao(new JDBCDaoImp());
					initSpnTables(spnTables, Integer.toString(DEFAULT_SELECT_TBL_INDX) );
				}
				else {
					DAOProvider.setDao(new SQLiteDaoImp(App.getContext()));
					initSpnTables(spnTables, Integer.toString(DEFAULT_SELECT_TBL_INDX) );
				}
			}
		});
	}
	
//	Toast.makeText(App.getContext(), "Mreža nedostupna!", Toast.LENGTH_SHORT).show();
	private boolean isNetworkAvailable() {
		boolean isAvailable = false;
		ConnectivityManager connMgr = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}
		return isAvailable;
	}
	
	private void initSpnTables(MySpinner spnTables, String selectIndx) {
		btnExecute.setEnabled(false);
		new FTablesQueryTask().execute(QueryConstants.QUERY_GET_FTABLES, selectIndx);
	}
	
	private void initSpnMeasures(MultiSelectionSpinner spnMeasures, MySpinner spnTables) {
		IResult tblValues = (IResult)spnTables.getTag();
		int selectedTblPos = spnTables.getSelectedIndex();
		int tblIdAttributePos = 0;
		String selFactTblId = tblValues.getRows().get(selectedTblPos).getRowValues().get(tblIdAttributePos);
		String query = QueryConstants.getMeasuresQuery(selFactTblId);
		new MeasuresQueryTask().execute(query, selFactTblId);
	}
	
	private void initLstAttributes(ExpandableListView lstAttributes, String factTblId) {
		String query = QueryConstants.getAttributesQuery(factTblId);
		new AttributesQueryTask().execute(query);
	}
	
	private class FTablesQueryTask extends AsyncTask<String, Void, AsyncValueWrapper> {
		@Override
		protected AsyncValueWrapper doInBackground(String... params) {
			String query = params[0];
			String selTblIndx = params[1];
			
			IDao dao = DAOProvider.getDao();
			IResult resultGetFTables = dao.executeQuery(query);
			
			return new AsyncValueWrapper(resultGetFTables, selTblIndx);
		}

		@Override
		protected void onPostExecute(AsyncValueWrapper value) {
			IResult result = value.result;
			int selFactTblPos= Integer.parseInt(value.stringVal);

			if(result == null || result.getRowNum() == 0) {
				return;
			}
			int tableNameIndx = 1;
			List<Item> tables = new ArrayList<Item>();
			for (IRow r : result.getRows()) {
				List<String> rv = r.getRowValues();
				tables.add(new Item( rv.get(tableNameIndx), rv) );
			}
			
			spnTables.setHeaderTitle(SPN_TABLES_TITLE);
			spnTables.setItemsWithValues(tables);
			spnTables.setSelection(selFactTblPos);
			spnTables.setTag(result);
			
			initSpnMeasures(spnMeasures, spnTables);
		}
	}
	
	private class MeasuresQueryTask extends AsyncTask<String, Void, AsyncValueWrapper> {
		@Override
		protected AsyncValueWrapper doInBackground(String... params) {
			String query = params[0];
			String selFactTblId = params[1];
			
			IDao dao = DAOProvider.getDao();
			IResult resultGetMeasures = dao.executeQuery(query);
			
			return new AsyncValueWrapper(resultGetMeasures, selFactTblId);
		}

		@Override
		protected void onPostExecute(AsyncValueWrapper value) {
			IResult result = value.result;
			String selFactTblId = value.stringVal;
			
			int agrFunAndAttrNamePos = 14;
			List<Item> measures = new ArrayList<Item>();
			for (IRow r : result.getRows()) {
				List<String> rv = r.getRowValues();
				measures.add(new Item(rv.get(agrFunAndAttrNamePos), rv));
			}
			
			spnMeasures.setHeaderTitle(SPN_MEASURE_TITLE);
			spnMeasures.setItemsWithValues(measures);
			initLstAttributes(lstAttributes, selFactTblId);
		}
	}
	
	private class AttributesQueryTask extends AsyncTask<String, Void, IResult> {
		@Override
		protected IResult doInBackground(String... params) {
			String query = params[0];
			
			IDao dao = DAOProvider.getDao();
			IResult resultGetAttributes = dao.executeQuery(query);
			
			return resultGetAttributes;
		}

		@Override
		protected void onPostExecute(IResult result) {
			//[nazTablica, nazSqlDimTablica, nazSqlCinjTablica, imeSQLAtrib, imeSQLAtrib, sifTablica, rbrAtrib, imeSQLAtrib, sifTipAtrib, imeAtrib]
			//[dDate, dDate, fProductPrice, idDate, _idDate, 103, 1, _idDate, 2, _idDate]
			SparseArray<Group> groups = new SparseArray<Group>();
			List<IRow> rows = result.getRows();

			int tableIdPos = 5;
			int tableNamePos = 0;
			int attrNamePos = 7;
			int currGrpIndx = 0;
			int firstGroupPos= 0;
			String currGrpMarker = rows.get(firstGroupPos).getRowValues().get(tableIdPos);
			Group currGrp = new Group(rows.get(firstGroupPos).getRowValues().get(tableNamePos));
			for (IRow r : rows) {
				List<String> rv = r.getRowValues();

				String tableId = rv.get(tableIdPos);
				if(! tableId.equals(currGrpMarker)) {
					groups.append(currGrpIndx++, currGrp);
					currGrpMarker = tableId;
					currGrp = new Group(rv.get(tableNamePos));
				}
				String attrName = rv.get(attrNamePos);
				currGrp.addChild(new Item(attrName, r));
			}
			groups.append(currGrpIndx, currGrp);
		
			
			lstAttributes.setAdapter(new MyExpandableListAdapter(App.getContext(), groups, LST_ATTRIBUTES_TITLE));
			btnExecute.setEnabled(true);
		}
	}
	
	@Override
	public void listenMySpinner(DialogInterface dialog, int which, boolean isChecked) {
		initSpnTables(spnTables, Integer.toString(which));
	}
	
	//radioBtn listener
//	@Override
//	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//		String selectedVal = parent.getItemAtPosition(pos).toString();
//		Toast.makeText(App.getContext(), "From QueryBuilder: " + selectedVal, Toast.LENGTH_SHORT).show();
//	}
//	
//	@Override
//	public void onNothingSelected(AdapterView<?> arg0) {
		// Ne radi ništa
//	}

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
