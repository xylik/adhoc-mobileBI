package hr.fer.poslovna.adapters;

import hr.fer.poslovna.activity.R;
import hr.fer.poslovna.fragments.AttributeSelecter;
import hr.fer.poslovna.fragments.QueryBuilder;
import hr.fer.poslovna.fragments.ResultFragment;
import hr.fer.poslovna.fragments.SettingsFragment;
import hr.fer.poslovna.utility.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	public static final int ATTR_SELECTER_POS = 0;
	public static final int QUERY_BUILDER_POS = 1;
	public static final int RESULT_POS = 2;
	private final int TAB_CNT = 4;
	private String[] navTitles;

	public SectionsPagerAdapter(FragmentManager fm) {
		super(fm);
		navTitles = Resources.getStringArray(R.array.adHoc_navTitles);
	}

	@Override
	public Fragment getItem(int position) {
		switch(position) {
		case ATTR_SELECTER_POS:
			return new AttributeSelecter();
		case QUERY_BUILDER_POS:
			return new QueryBuilder();
		case RESULT_POS:
			return new ResultFragment();
		default:
			return new SettingsFragment();
		}
	}

	@Override
	public int getCount() {
		return TAB_CNT;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return navTitles[position];
	}
}