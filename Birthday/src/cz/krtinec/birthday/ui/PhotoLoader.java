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

package cz.krtinec.birthday.ui;

import java.io.InputStream;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import cz.krtinec.birthday.R;
import cz.krtinec.birthday.data.BirthdayProvider;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ImageView;

public class PhotoLoader implements Runnable {
    private final Handler handler;
    private final Context ctx;
    private Map<ImageView, Long> photosToLoad = new ConcurrentHashMap<ImageView, Long>();

    private boolean shutdown = false;
    private Map<Long, Drawable> cache = new WeakHashMap<Long, Drawable>();
    
    public PhotoLoader(Handler handler, Context ctx) {
            this.handler = handler;
            this.ctx = ctx;
    }

    public void addPhotoToLoad(ImageView icon, long contactId) {        	
        photosToLoad.put(icon, contactId);
    }
    
    public void shutdown() {
    	this.shutdown = true;
    }
    
    @Override
    public void run() {
    		while (!shutdown) {
                if (!photosToLoad.isEmpty()) {
                    for (final ImageView icon: photosToLoad.keySet()) {
                        Long contactId = photosToLoad.get(icon);
                        Drawable drawable = null;
                        drawable = cache.get(contactId);
                        if (drawable == null) {
                            InputStream photoStream;
                            if ((photoStream = BirthdayProvider.openPhoto(ctx, contactId)) != null) {
                                drawable = Drawable.createFromStream(photoStream, "src");
                                cache.put(contactId, drawable);
                            }
                        }
                        final Drawable drawable1 = drawable;
                        handler.post(new Runnable() {
                            public void run() {
                                if (drawable1 != null) {
                                    icon.setImageDrawable(drawable1);
                                } else {
                                    icon.setImageResource(R.drawable.icon);
                                }
                            }
                        });
                    }
                }
    		}
    }
}
