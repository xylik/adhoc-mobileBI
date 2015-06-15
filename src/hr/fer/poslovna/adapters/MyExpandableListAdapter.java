package hr.fer.poslovna.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.poslovna.activity.R;
import hr.fer.poslovna.gui.AbstractSpinner.Item;
import hr.fer.poslovna.gui.MultiSelectionSpinner;
import hr.fer.poslovna.gui.MultiSelectionSpinner.IMultiSelectListener;
import hr.fer.poslovna.model.Group;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

public class MyExpandableListAdapter extends BaseExpandableListAdapter implements IMultiSelectListener {
	private SparseArray<Group> data;
	private Map<Integer, RestoreState> restoreStates = new HashMap<Integer, RestoreState>();
	private String headerTitle;
	private int grpSize = 1;
	public LayoutInflater inflater;
	public Context ctx;

	public MyExpandableListAdapter(Context ctx, SparseArray<Group> data, String headerTitle) {
		this.ctx = ctx;
		this.data = data;
		this.headerTitle = headerTitle;
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		final Group child = (Group) getChild(groupPosition, childPosition);
		MultiSelectionSpinner spinner;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.comp_listrow_details, null);
		}
		spinner = (MultiSelectionSpinner)convertView.findViewById(R.id.multiSpinner);
		spinner.setHeaderTitle(child.getGrpTitle());
		spinner.setItems(child.getChildrenTitles());
		spinner.setExpandableListPosition(groupPosition, childPosition);
		spinner.register(this);
		RestoreState rs = restoreStates.get(getChildKey(groupPosition, childPosition));
		if(rs != null) {
			spinner.setSelection(rs.checkedItems);
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return data.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return data;
	}

	@Override
	public int getGroupCount() {
		return grpSize;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.comp_listrow_group, null);
		}
		LinearLayout rootView = (LinearLayout)convertView;
		CheckedTextView chkTxt = (CheckedTextView)rootView.findViewById(R.id.textView1);
		chkTxt.setText(headerTitle);
		chkTxt.setChecked(isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public void multiSelectListen(int groupPos, int childPos, boolean[] checkedItems) {
		int key = getChildKey(groupPos, childPos);
		restoreStates.put(key, new RestoreState(groupPos, childPos, checkedItems));
	}
	
	private int getChildKey(int grpPos, int childPos) {
		String keyString = "1" + Integer.toString(grpPos) + Integer.toString(childPos);
		return Integer.parseInt(keyString);
	}
	
	public List<Group> getSelectedGroups() {
		List<Group> result = new ArrayList<Group>();
		for(RestoreState rs: restoreStates.values() ) {
			Group restoreGrp = data.get(rs.childPos);
			Group selGrp = new Group(restoreGrp.getGrpTitle());
			List<Item> children = restoreGrp.getChildren();
			
			for(int i=0, end=restoreGrp.getChildren().size(); i<end; i++) {
				if(rs.checkedItems[i]) {
					String childTitle = children.get(i).getTitle();
					Object childValue = children.get(i).getValue();
					selGrp.addChild(new Item(childTitle, childValue));
				}
			}
			result.add(selGrp);
		}
		return result;
	}
	
	private static class RestoreState {
		public int groupPos;
		public int childPos;
		public boolean[] checkedItems;
		
		public RestoreState(int groupPos, int childPos, boolean[] checkedItems) {
			super();
			this.groupPos = groupPos;
			this.childPos = childPos;
			this.checkedItems = checkedItems;
		}
	}
	
}