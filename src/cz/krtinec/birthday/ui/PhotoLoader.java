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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cz.krtinec.birthday.R;
import cz.krtinec.birthday.data.BirthdayProvider;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ImageView;

public class PhotoLoader implements Runnable {
    private final Handler handler;
    private final Context ctx;
    private BlockingQueue<PhotoToLoad> photosToLoad = new LinkedBlockingQueue<PhotoToLoad>();
    private boolean shutdown = false;
    
    public PhotoLoader(Handler handler, Context ctx) {
            this.handler = handler;
            this.ctx = ctx;
    }

    public void addPhotoToLoad(ImageView icon, long contactId) {        	
    	PhotoToLoad p = new PhotoToLoad(icon, contactId);    	
    	photosToLoad.remove(p);
    	photosToLoad.add(p);    	
    }
    
    public void shutdown() {
    	this.shutdown = true;
    }
    
    @Override
    public void run() {
    		while (!shutdown) {
    			try {
    				final PhotoToLoad photo = photosToLoad.take();
    				final Drawable drawable;
		            InputStream photoStream;
					if ((photoStream = BirthdayProvider.openPhoto(ctx, photo.contactId)) != null) {
						drawable = Drawable.createFromStream(photoStream, "src");					
					} else {
						drawable = null;
					}
					handler.post(new Runnable() {
						public void run() {
							if (drawable != null) {
								photo.icon.setImageDrawable(drawable);
							} else {
								photo.icon.setImageResource(R.drawable.icon);
							}
						}
					});    			
    				
    			} catch (InterruptedException e) {
    				//
    			}
    			
    		}
    	
        	
    }       
    
    static class PhotoToLoad {
    	ImageView icon;
    	long contactId;
    	PhotoToLoad(ImageView icon, long contactId) {
    		this.icon = icon;
    		this.contactId = contactId;
    	}
		@Override
		public boolean equals(Object o) {
			PhotoToLoad p2 = (PhotoToLoad) o;
			return this.icon == p2.icon;
		}
    	
    	
    }
}
