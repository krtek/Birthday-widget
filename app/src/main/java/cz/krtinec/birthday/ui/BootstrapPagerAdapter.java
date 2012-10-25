package cz.krtinec.birthday.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cz.krtinec.birthday.R;

/**
 * Pager adapter
 */
public class BootstrapPagerAdapter extends FragmentPagerAdapter {

    private final Resources resources;

    /**
     * Create pager adapter
     *
     * @param resources
     * @param fragmentManager
     */
    public BootstrapPagerAdapter(Resources resources, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.resources = resources;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
      switch (position) {
          case 0:
            EventListFragment eventListFragment = new EventListFragment();
              eventListFragment.setArguments(bundle);
            return eventListFragment;
/*        case 1:
            OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
            orderDetailFragment.setArguments(bundle);
            return orderDetailFragment;

 */
            default:
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
           case 0:
            return resources.getString(R.string.birthday);
/*        case 1:
            return resources.getString(R.string.page_order);
*/
        default:
            return null;
        }
    }
}
