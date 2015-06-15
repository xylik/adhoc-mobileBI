package hr.fer.poslovna.model;

import hr.fer.poslovna.gui.AbstractSpinner.Item;

import java.util.ArrayList;
import java.util.List;

public class Group {
	private String grpTitle;
	private List<Item> children;
	private List<String> childrenTitles = new ArrayList<String>();

	public Group(String grpTitle, List<Item> children) {
		super();
		this.grpTitle = grpTitle;
		this.children = children;
		
		for(Item i: children) {
			childrenTitles.add( i.getTitle() );
		}
	}

	public Group(String grpTitle) {
		super();
		this.grpTitle = grpTitle;
		this.children = new ArrayList<Item>();
	}

	public String getGrpTitle() {
		return grpTitle;
	}

	public void setGrpTitle(String grpTitle) {
		this.grpTitle = grpTitle;
	}

	public List<Item> getChildren() {
		return children;
	}
	
	public void addChild(Item child) {
		children.add(child);
		childrenTitles.add(child.getTitle());
	}
	
	public List<String> getChildrenTitles() {
		return childrenTitles;
	}
	
}