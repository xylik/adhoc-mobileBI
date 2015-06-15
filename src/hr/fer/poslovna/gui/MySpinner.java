package hr.fer.poslovna.gui;


import hr.fer.poslovna.fragments.AttributeSelecter;
import hr.fer.poslovna.listeners.IMySpinnerListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class MySpinner extends AbstractSpinner implements OnMultiChoiceClickListener {
	private final int DEFAULT_SELECTION = 0;
	private Set<IMySpinnerListener> listeners = new HashSet<IMySpinnerListener>();
	private String[] _items;
	private List<Item> itemsWithValue;
	private int mSelection;
	private ArrayAdapter<String> simple_adapter;
	private String headerTitle = "";
	
	public MySpinner(Context context) {
		super(context);
		simple_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		simple_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		super.setAdapter(simple_adapter);
		mSelection = DEFAULT_SELECTION;
	}

	public MySpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		simple_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		simple_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		super.setAdapter(simple_adapter);
		mSelection = DEFAULT_SELECTION;
	}

	@Override
	public boolean performClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(AttributeSelecter.activity);
		builder.setSingleChoiceItems(_items, mSelection, this);
		builder.show();
		return true;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		if ( which < _items.length) {
			mSelection = which;

			simple_adapter.clear();
			simple_adapter.add(buildSelectedItemString(which));
			
			for(IMySpinnerListener l: listeners) {
				l.listenMySpinner(dialog, which, isChecked);
			}
		} else {
			throw new IllegalArgumentException("Argument 'which' is out of bounds.");
		}

	}

	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
	}

	public void register(IMySpinnerListener listener) {
		listeners.add(listener);
	}
	
	public void remove(IMySpinnerListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void setHeaderTitle(String headerTitle) {
		this.headerTitle = headerTitle;
		simple_adapter.clear();
		simple_adapter.add(headerTitle);
	}
	
	@Override
	public void setItemsWithValues(List<Item> items) {
		this.itemsWithValue = items;
		simple_adapter.clear();
		simple_adapter.add(headerTitle);
		_items = new String[items.size()];
		for(int i=0, end=items.size(); i<end; i++) {
			_items[i] = items.get(i).getTitle();
		}
	}
	
	@Override
	public void setItems(String[] items) {
		_items = items;
		simple_adapter.clear();
		simple_adapter.add(headerTitle);
	}

	@Override
	public void setItems(List<String> items) {
		_items = items.toArray(new String[items.size()]);
		simple_adapter.clear();
		simple_adapter.add(headerTitle);
	}

	public void setSelection(String selection) {
		for (int j = 0; j < _items.length; ++j) {
			if (_items[j].equals(selection)) {
				mSelection = j;
			}
		}
		simple_adapter.clear();
		simple_adapter.add(buildSelectedItemString(mSelection));
	}

	@Override
	public void setSelection(int index) {
		mSelection = index;
		simple_adapter.clear();
		simple_adapter.add(buildSelectedItemString(mSelection));
	}

	public Item getSelectedItemWithValue() {
		return itemsWithValue.get(mSelection);
	}
	
	public String getSelectedString() {
		return _items[mSelection];
	}

	public int getSelectedIndex() {
		return mSelection;
	}
	
	private String buildSelectedItemString(int position) {
		return headerTitle;
	}

}
