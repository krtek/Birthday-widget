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

import java.util.List;

import cz.krtinec.birthday.data.StockPhotoLoader;
import cz.krtinec.birthday.dto.Event;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class AdapterParent<T extends Event> extends BaseAdapter {
	protected List<T> list;
	protected Context ctx;
	protected StockPhotoLoader loader;
	
	public AdapterParent(List<T> list, Context ctx, StockPhotoLoader loader) {
		this.list = list;
		this.ctx = ctx;
		this.loader = loader;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position).getContactId();
	}
	
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
