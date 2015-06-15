package hr.fer.poslovna.activity;

import java.util.HashMap;
import java.util.Map;

import hr.fer.poslovna.adapters.SectionsPagerAdapter;
import hr.fer.poslovna.listeners.IFrgCommunicator;
import hr.fer.poslovna.listeners.IFrgDataReceiveListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

@SuppressWarnings("deprecation")
public class AdHoc extends ActionBarActivity implements  ActionBar.TabListener, IFrgCommunicator {
	private static final int OFFSCREEN_FRG_LIMIT = 3;
	private Map<String,IFrgDataReceiveListener> frgDataObservers = new HashMap<String,IFrgDataReceiveListener>();
	//FragmentStatePagerAdapter za mem. zahtjevne fragmente
	private SectionsPagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_adhoc);
		
		setViewElements();
		initActivity();
	}
	
	private void setViewElements() {
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(false);
	    actionBar.setDisplayShowTitleEnabled(false);
	    actionBar.setDisplayShowHomeEnabled(false);
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	private void initActivity() {
		sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(sectionsPagerAdapter);
		viewPager.setOffscreenPageLimit(OFFSCREEN_FRG_LIMIT);
		for(int i=0, end=sectionsPagerAdapter.getCount(); i<end; i++) {
			actionBar.addTab(actionBar.newTab().setText(sectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction frag) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}
	
	@Override
	public void register(IFrgDataReceiveListener listener) {
		frgDataObservers.put(listener.getCustomTag(), listener);
	}
	
	@Override
	public void unregister(IFrgDataReceiveListener listener) {
		frgDataObservers.remove(listener.getCustomTag());
	}

	@Override
	public void dispatchData(String srcFrgCustomTag, String destFrgCustomTag, Object data) {
		IFrgDataReceiveListener listener = frgDataObservers.get(destFrgCustomTag);
		if(listener == null) {
			throw new RuntimeException("Invalid destFrgCustomTag specified!");
		}
		listener.receiveData(srcFrgCustomTag, data );
	}
	

	@Override
	public void requestPageChange(int position) {
		if(position < 0 || position >= viewPager.getChildCount()) {
			throw new RuntimeException("Invalid tab postion!");
		}
		viewPager.setCurrentItem(position);
	}
}

