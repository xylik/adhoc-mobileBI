package hr.fer.poslovna.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import hr.fer.poslovna.activity.R;
import hr.fer.poslovna.adapters.SectionsPagerAdapter;
import hr.fer.poslovna.gui.AbstractSpinner.Item;
import hr.fer.poslovna.listeners.IFrgCommunicator;
import hr.fer.poslovna.listeners.IFrgDataReceiveListener;
import hr.fer.poslovna.model.Group;
import hr.fer.poslovna.model.IRow;
import hr.fer.poslovna.utility.App;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class QueryBuilder extends Fragment implements IFrgDataReceiveListener {
	public final static String CUSTOM_TAG = "queryBuilder";
	private IFrgCommunicator frgCommunicator;
	private Object receivedData;
	private EditText txtQuery;
	private Button btnContinue;
	private Button btnCancel;
	private CheckBox chkEditQuery;
	private String prettyQuery;
	private String query;

	public QueryBuilder() {
	}

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
		prettyQuery = txtQuery.getText().toString();
		query = prettyQuery.replace("\n", " ");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		frgCommunicator.unregister(this);
		frgCommunicator = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.ui_frg_querybuilder, container, false);

		setViews(rootView);
		setActions();
		return rootView;
	}

	private void setViews(View rootView) {
		txtQuery = (EditText) rootView.findViewById(R.id.query_txtQuery);
		btnContinue = (Button) rootView.findViewById(R.id.query_btnContinue);
		btnCancel = (Button) rootView.findViewById(R.id.query_btnCancel);
		chkEditQuery = (CheckBox) rootView.findViewById(R.id.query_chkEditQuery);
	}

	private void setActions() {
		chkEditQuery.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (!isChecked) {
					txtQuery.setFocusableInTouchMode(false);
					txtQuery.setFocusable(false);
				} else {
					txtQuery.setFocusableInTouchMode(true);
					txtQuery.setFocusable(true);
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				frgCommunicator.requestPageChange(SectionsPagerAdapter.ATTR_SELECTER_POS);
			}
		});

		btnContinue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String prettyQuery = txtQuery.getText().toString();
				String query = prettyQuery.replace("\n", " ");
				if (query.isEmpty()) {
					Toast.makeText(App.getContext(), "Molim unesite SQL upit!", Toast.LENGTH_SHORT).show();
				} else {
					frgCommunicator.dispatchData(CUSTOM_TAG, ResultFragment.CUSTOM_TAG, query);
					frgCommunicator.requestPageChange(SectionsPagerAdapter.RESULT_POS);
				}
			}
		});
	}

	@Override
	public void receiveData(String srcCustomTag, Object data) {
		if (srcCustomTag.equals(AttributeSelecter.CUSTOM_TAG)) {
			HashMap<String, Object> receivedData = (HashMap<String, Object>) data;
			Item selTable = (Item) receivedData.get("selTables");
			List<Item> selMeasures = (ArrayList<Item>) receivedData.get("selMeasures");
			List<Group> selAttributes = (List<Group>) receivedData.get("selAttributes");
			processData(selTable, selMeasures, selAttributes);
		}
	}

	private void processData(Item selTable, List<Item> selMeasures, List<Group> selAttributes) {
		String select = buildSelectPart(selAttributes, selMeasures);
		String from = buildFromPart(selTable, selAttributes);
		String where = buildWherePart(selTable, selAttributes);
		String groupBy = buildGroupByPart(selAttributes);
		prettyQuery = new StringBuilder(select + "\n\n").append(from + "\n\n").append(where + "\n\n").append(groupBy).toString();
		query = prettyQuery.replace("\n\n", " ");
		txtQuery.setText(prettyQuery);
	}

	private String buildSelectPart(List<Group> selAttributes, List<Item> selMeasures) {
		// Za dimenzijske atribute: tablica.nazSQLTablica + "." + tabAtribut.imeSQLAtrib + " AS '" + tabAtribut.imeAtrib + "'"
		// Za mjere: agrFun.nazAgrFun + "(" + tablica.nazSQLTablica + "." + tabAtribut.imeSQLAtrib + ") AS '" + tabAtribut.imeAtrib + "'"
		// [sifTablica, rbrAtrib, imeSQLAtrib, sifTipAtrib, imeAtrib, sifAgrFun, nazAgrFun, sifTablica, nazTablica, nazSQLTablica,
		// sifTipTablica, sifTablica, rbrAtrib, sifAgrFun, imeAtrib]
		// [100, 5, productPriceUSD, 1, productPriceUSD, 1, SUM, 100, fProductPrice, fProductPrice, 1, 100, 5, 1, Sum of productPriceUSD]
		int nazAgrFunPos = 6;
		int nazSQLTablicaPos = 9;
		int imeSQLAtribPos = 2;
		int imeAtribPos = 14;
		StringBuilder sbMeasures = new StringBuilder("SELECT ");
		for (Item i : selMeasures) {
			List<String> rv = (List<String>) i.getValue();
			String agrFun = rv.get(nazAgrFunPos) + "(" + rv.get(nazSQLTablicaPos) + "." + rv.get(imeSQLAtribPos) + ")";
			String agrFunPretty = agrFun + " AS '" + rv.get(imeAtribPos) + "', ";
			sbMeasures.append(agrFunPretty);
		}

		int nazSqlDimTablicaPos = 1;
		imeSQLAtribPos = 7;
		imeAtribPos = 9;
		StringBuilder sbAttributes = new StringBuilder(sbMeasures);
		for (Group g : selAttributes) {
			for (Item i : g.getChildren()) {
				List<String> rv = ((IRow) i.getValue()).getRowValues();
				String attrSql = rv.get(nazSqlDimTablicaPos) + "." + rv.get(imeSQLAtribPos);
				String attrName = rv.get(imeAtribPos);

				sbAttributes.append(attrSql + " AS '" + attrName + "', ");
			}
		}

		int startPos = 0;
		int charsToRemove = 2;
		return sbAttributes.substring(startPos, sbAttributes.length() - charsToRemove);
	}

	private String buildFromPart(Item selTable, List<Group> selAttributes) {
		// [sifTablica, nazTablica, nazSQLTablica]
		// [100, fProductPrice, fProductPrice]
		Set<String> distinctSqlTblNames = new LinkedHashSet<String>();
		List<String> fTableAttrs = (List<String>) selTable.getValue();
		int nazSQLTablicaPos = 2;
		distinctSqlTblNames.add(fTableAttrs.get(nazSQLTablicaPos));

		int nazSqlDimTablicaPos = 1;
		for (Group g : selAttributes) {
			for (Item i : g.getChildren()) {
				List<String> rv = ((IRow) i.getValue()).getRowValues();
				String dimTable = rv.get(nazSqlDimTablicaPos);
				distinctSqlTblNames.add(dimTable);
			}
		}

		StringBuilder sb = new StringBuilder("FROM ");
		for (String s : distinctSqlTblNames) {
			sb.append(s + ", ");
		}

		int startPos = 0;
		int charToRemove = 2;
		return sb.substring(startPos, sb.length() - charToRemove);
	}

	private String buildWherePart(Item selTable, List<Group> selAttributes) {
		int nazSqlCinjTablicaPos = 2;
		int cinjImeSQLAtribPos = 3;
		int nazSqlDimTablica = 1;
		int dimImeSQLAtrib = 4;
		Set<String> distinctJoins = new LinkedHashSet<String>();
		for (Group g : selAttributes) {
			for (Item i : g.getChildren()) {
				List<String> rv = ((IRow) i.getValue()).getRowValues();
				String leftSide = rv.get(nazSqlCinjTablicaPos) + "." + rv.get(cinjImeSQLAtribPos);
				String rightSide = rv.get(nazSqlDimTablica) + "." + rv.get(dimImeSQLAtrib);
				distinctJoins.add(leftSide + " = " + rightSide);
			}
		}

		StringBuilder sb = new StringBuilder("WHERE ");
		for (String s : distinctJoins) {
			sb.append(s + " AND ");
		}

		int startPos = 0;
		int charsToRemove = 5;
		return sb.substring(startPos, sb.length() - charsToRemove);
	}

	private String buildGroupByPart(List<Group> selAttributes) {
		// [nazTablica, nazSqlDimTablica, nazSqlCinjTablica, imeSQLAtrib, imeSQLAtrib, sifTablica, rbrAtrib, imeSQLAtrib, sifTipAtrib,
		// imeAtrib]
		// [dDate, dDate, fProductPrice, idDate, _idDate, 103, 1, _idDate, 2, _idDate]
		// [dDate, dDate, fProductPrice, idDate, _idDate, 103, 2, rowDescription, 2, rowDescription]
		// tablica.nazSQLTablica + "." + tabAtribut.imeSQLAtrib
		int nazSqlDimTablicaPos = 1;
		int imeSQLAtribPos = 7;
		StringBuilder sb = new StringBuilder("GROUP BY ");
		for (Group g : selAttributes) {
			for (Item i : g.getChildren()) {
				List<String> rv = ((IRow) i.getValue()).getRowValues();
				String attr = rv.get(nazSqlDimTablicaPos) + "." + rv.get(imeSQLAtribPos) + ", ";
				sb.append(attr);
			}
		}
		int charsToRemove = 2;
		return sb.substring(0, sb.length() - charsToRemove).concat(" LIMIT 100");
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
