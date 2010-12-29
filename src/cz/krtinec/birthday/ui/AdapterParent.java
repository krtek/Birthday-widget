package cz.krtinec.birthday.ui;

import java.util.List;

import cz.krtinec.birthday.dto.BContact;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class AdapterParent<T extends BContact> extends BaseAdapter {
	protected List<T> list;
	protected Context ctx;
	protected PhotoLoader loader;
	
	public AdapterParent(List<T> list, Context ctx, PhotoLoader loader) {
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
		return list.get(position).getId();
	}
	
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
