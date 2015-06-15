package hr.fer.poslovna.gui;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public abstract class AbstractSpinner extends Spinner {

	public AbstractSpinner(Context context) {
		super(context);
	}
	
	public AbstractSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public abstract boolean performClick();
	
	public abstract void onClick(DialogInterface dialog, int which, boolean isChecked);
	
	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		super.setAdapter(adapter);
	}
	
	public abstract void setHeaderTitle(String headerTitle);
	
	public abstract void setItemsWithValues(List<Item> items);
	
	public abstract void setItems(String[] items);
	
	public abstract void setItems(List<String> items);
	
	public abstract void setSelection(int index);
	
	public static class Item {
		private String title;
		private Object value;
		
		public Item(String title, Object value) {
			super();
			this.title = title;
			this.value = value;
		}

		public String getTitle() {
			return title;
		}

		public Object getValue() {
			return value;
		}
	}
}
