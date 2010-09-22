package cz.krtinec.birthday.ui;

import java.util.List;

import cz.krtinec.birthday.dto.BContactParent;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class AdapterParent<T extends BContactParent> extends BaseAdapter {
	protected List<T> list;
	protected Context ctx;
	
	public AdapterParent(List<T> list, Context ctx) {
		this.list = list;
		this.ctx = ctx;
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
		return list.get(position).getId();
	}
	
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
