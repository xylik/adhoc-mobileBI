package hr.fer.poslovna.gui;


import hr.fer.poslovna.fragments.AttributeSelecter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class MultiSelectionSpinner extends AbstractSpinner implements OnMultiChoiceClickListener {
	private Set<IMultiSelectListener> multiSelListeners = new HashSet<IMultiSelectListener>();
	private String[] _items = null;
	private List<Item> itemsWithValue;
	private boolean[] mSelection = null;
	private ArrayAdapter<String> simple_adapter;
	private String headerTitle = "";
	private int grpPos;
	private int childPos;
	
	public MultiSelectionSpinner(Context context) {
		super(context);
		simple_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		simple_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		super.setAdapter(simple_adapter);
	}

	public MultiSelectionSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		simple_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		simple_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		super.setAdapter(simple_adapter);
	}

	@Override
	public boolean performClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(AttributeSelecter.activity);
		builder.setMultiChoiceItems(_items, mSelection, this);
		builder.show();
		return true;
	}
	
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		if (mSelection != null && which < mSelection.length) {
			mSelection[which] = isChecked;
			
			notifyMultiSelObservers();

			simple_adapter.clear();
			simple_adapter.add(buildSelectedItemString(headerTitle));
		} else {
			throw new IllegalArgumentException("Argument 'which' is out of bounds.");
		}
	}

	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
	}

	public void register(IMultiSelectListener listener) {
		multiSelListeners.add(listener);
	}
	
	public void remove(IMultiSelectListener listener) {
		multiSelListeners.remove(listener);
	}

	private void notifyMultiSelObservers() {
		for(IMultiSelectListener l: multiSelListeners) {
			l.multiSelectListen(grpPos, childPos, mSelection);
		}
	}
	
	public void setHeaderTitle(String headerTitle) {
		this.headerTitle = headerTitle;
	}
	
	public void setExpandableListPosition(int grpPosition, int childPosition) {
		this.grpPos = grpPosition;
		this.childPos = childPosition;
	}
	
	public void setItemsWithValues(List<Item> items) {
		this.itemsWithValue = items;
		mSelection = new boolean[items.size()];
		simple_adapter.clear();
		simple_adapter.add(buildSelectedItemString(headerTitle));
		Arrays.fill(mSelection, false);
		_items = new String[items.size()];
		for(int i=0, end=items.size(); i<end; i++) {
			_items[i] = items.get(i).getTitle();
		}
	}
	
	public void setItems(String[] items) {
		_items = items;
		mSelection = new boolean[_items.length];
		simple_adapter.clear();
		simple_adapter.add(buildSelectedItemString(headerTitle));
		Arrays.fill(mSelection, false);
	}

	public void setItems(List<String> items) {
		_items = items.toArray(new String[items.size()]);
		mSelection = new boolean[_items.length];
		simple_adapter.clear();
		simple_adapter.add(headerTitle);
		Arrays.fill(mSelection, false);
	}

	public void setSelection(List<String> selection) {
		for (int i = 0; i < mSelection.length; i++) {
			mSelection[i] = false;
		}
		for (String sel : selection) {
			for (int j = 0; j < _items.length; ++j) {
				if (_items[j].equals(sel)) {
					mSelection[j] = true;
				}
			}
		}
		simple_adapter.clear();
		simple_adapter.add(buildSelectedItemString(headerTitle));
	}

	public void setSelection(int index) {
		for (int i = 0; i < mSelection.length; i++) {
			mSelection[i] = false;
		}
		if (index >= 0 && index < mSelection.length) {
			mSelection[index] = true;
		} else {
			throw new IllegalArgumentException("Index " + index + " is out of bounds.");
		}
		simple_adapter.clear();
		simple_adapter.add(buildSelectedItemString(headerTitle));
	}

	public void setSelection(int[] selectedIndicies) {
		for (int i = 0; i < mSelection.length; i++) {
			mSelection[i] = false;
		}
		for (int index : selectedIndicies) {
			if (index >= 0 && index < mSelection.length) {
				mSelection[index] = true;
			} else {
				throw new IllegalArgumentException("Index " + index + " is out of bounds.");
			}
		}
		simple_adapter.clear();
		simple_adapter.add(buildSelectedItemString(headerTitle));
	}
	
	public void setSelection(boolean[] selectedIndicies) {
		if(_items.length == selectedIndicies.length) {
			mSelection = selectedIndicies;
		}
		else {
			throw new RuntimeException("selectedIndicies length mismatch!");
		}
		
		simple_adapter.clear();
		simple_adapter.add(buildSelectedItemString(headerTitle));
	}
	
	public List<Item> getSelectedItemsWithValues() {
		List<Item> selection = new ArrayList<Item>();
		for (int i = 0; i < _items.length; ++i) {
			if (mSelection[i]) {
				Object value = itemsWithValue.get(i).getValue();
				selection.add(new Item( _items[i], value));
			}
		}
		return selection;
	}
	
	public List<String> getSelectedStrings() {
		List<String> selection = new LinkedList<String>();
		for (int i = 0; i < _items.length; ++i) {
			if (mSelection[i]) {
				selection.add(_items[i]);
			}
		}
		return selection;
	}

	public List<Integer> getSelectedIndicies() {
		List<Integer> selection = new LinkedList<Integer>();
		for (int i = 0; i < _items.length; ++i) {
			if (mSelection[i]) {
				selection.add(i);
			}
		}
		return selection;
	}

	private String buildSelectedItemString(String headerTitle) {
//		StringBuilder sb = new StringBuilder(headerTitle);
//		boolean foundOne = false;
//
//		for (int i = 0; i < _items.length; ++i) {
//			if (mSelection[i]) {
//				if (foundOne) {
//					sb.append(", ");
//				}
//				foundOne = true;
//
//				sb.append(_items[i]);
//			}
//		}
//		return sb.toString();
		return headerTitle;
	}

	public String getSelectedItemsAsString() {
		StringBuilder sb = new StringBuilder();
		boolean foundOne = false;

		for (int i = 0; i < _items.length; ++i) {
			if (mSelection[i]) {
				if (foundOne) {
					sb.append(", ");
				}
				foundOne = true;
				sb.append(_items[i]);
			}
		}
		return sb.toString();
	}
	
	public static interface IMultiSelectListener {
		public void multiSelectListen(int groupPos, int childPos, boolean[] checkedItems);
	}
}
