package cz.krtinec.birthday.ui;

import java.util.List;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.widget.ListView;
import cz.krtinec.birthday.R;
import cz.krtinec.birthday.data.BirthdayService;
import cz.krtinec.birthday.dto.Event;

/**
 */
public class EventListFragment extends ItemListFragment<Event> {
    @Inject
    protected BirthdayService service;

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error;
    }

    @Override
    protected void configureList(Activity activity, ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);
    }

    @Override
    protected SingleTypeAdapter<Event> createAdapter(List<Event> items) {
        return new EventListAdapter(getActivity().getLayoutInflater(), items);
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        final List<Event> initialItems = items;

        return new ThrowableLoader<List<Event>>(getActivity(), items) {

            @Override
            public List<Event> loadData() throws Exception {
                return service.upcomingBirthday();
            }
        };
    }
}
