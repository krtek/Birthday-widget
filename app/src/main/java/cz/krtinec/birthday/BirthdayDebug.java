/*
 * This file is part of Birthday Widget.
 *
 * Birthday Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Birthday Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Birthday Widget.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) Lukas Marek, 2011.
 */

package cz.krtinec.birthday;

import java.text.MessageFormat;
import java.util.List;

import com.google.inject.Inject;

import cz.krtinec.birthday.data.BirthdayService;
import cz.krtinec.birthday.dto.EventDebug;
import cz.krtinec.birthday.ui.AdapterParent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cz.krtinec.birthday.data.StockPhotoLoader;

public class BirthdayDebug extends Activity {
    @Inject
    private BirthdayService service;

    private StockPhotoLoader loader;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView list = (ListView) findViewById(R.id.debug_list);
        loader = new StockPhotoLoader(service, this, R.drawable.icon);
        List<EventDebug> listOfContacts = service.allBirthday();
        list.setAdapter(new BirthdayDebugAdapter(listOfContacts, this, loader));
        loader.resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loader.stop();
//		Debug.stopMethodTracing();		
    }

    static class BirthdayDebugAdapter extends AdapterParent<EventDebug> {
        public BirthdayDebugAdapter(List<EventDebug> list, Context ctx, StockPhotoLoader loader) {
            super(list, ctx, loader);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EventDebug contact = list.get(position);
            View v;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.debug_row, null);
            } else {
                v = convertView;
            }

            ((TextView) v.findViewById(R.id.name)).setText(contact.getDisplayName());
            ((TextView) v.findViewById(R.id.dateParsed)).setText(MessageFormat.format(ctx.getString(R.string.debug_parsed), contact.getDisplayDate(ctx)));

            ((TextView) v.findViewById(R.id.dateAsString)).setText(MessageFormat.format(ctx.getString(R.string.debug_raw), contact.getbDayString()));
            ImageView check = (ImageView) v.findViewById(R.id.check);

            switch (contact.getIntegrity()) {
                case FULL: {
                    check.setImageResource(R.drawable.accept);
                    break;
                }
                case WITHOUT_YEAR: {
                    check.setImageResource(R.drawable.exclamation);
                    break;
                }
                case NONE: {
                    check.setImageResource(R.drawable.cancel);
                    break;
                }
            }
            final ImageView imageView = (ImageView) v.findViewById(R.id.bicon);
            imageView.setImageResource(R.drawable.icon);
            loader.loadPhoto((ImageView) v.findViewById(R.id.bicon), contact.getContactId());


            return v;

        }
    }

}
