package cz.krtinec.birthday.ui;

import java.text.MessageFormat;
import java.util.List;

import com.google.inject.Inject;

import android.content.Context;
import android.view.LayoutInflater;
import cz.krtinec.birthday.R;
import cz.krtinec.birthday.Utils;
import cz.krtinec.birthday.data.BirthdayService;
import cz.krtinec.birthday.dto.BirthdayEvent;
import cz.krtinec.birthday.dto.Event;


/**
 */
public class EventListAdapter extends AlternatingColorListAdapter<Event> {

    private Context ctx;
    private BirthdayService service;

    public EventListAdapter(LayoutInflater inflater, List<Event> items, boolean selectable, BirthdayService service) {
        super(R.layout.event_list_item, inflater, items, selectable);
        ctx = inflater.getContext();
        this.service = service;
    }

    public EventListAdapter(LayoutInflater inflater, List<Event> items, BirthdayService service) {
        super(R.layout.event_list_item, inflater, items);
        ctx = inflater.getContext();
        this.service = service;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] {R.id.iv_icon, R.id.tv_name, R.id.tv_event_type_and_date,  R.id.tv_age, R.id.tv_days};
    }

    @Override
    protected void update(int position, Event item) {
        super.update(position, item);
        service.loadPhoto(imageView(R.id.iv_icon), item.getContactId());
        setText(R.id.tv_name, item.getDisplayName());
        setText(R.id.tv_days, String.valueOf(item.getDaysToEvent()));

        if (item instanceof BirthdayEvent) {
            setText(R.id.tv_age, String.valueOf(((BirthdayEvent) item).getAge()));
        } else {
            setText(R.id.age, "");
        }

        String label = Utils.getEventLabel(ctx, item);
        setText(R.id.tv_event_type_and_date, label + ": " + item.getDisplayDate(ctx));
    }

}
