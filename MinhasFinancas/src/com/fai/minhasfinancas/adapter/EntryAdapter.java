package com.fai.minhasfinancas.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fai.minhasfinancas.R;
import com.fai.minhasfinancas.entity.Entry;

public class EntryAdapter extends BaseAdapter {

	private List<Entry> lstPeople;
	private LayoutInflater mInflater;
	private ViewHolder holder;

	static class ViewHolder {
		private TextView tvDescription;
		private TextView tvDate;
		private TextView tvValue;
		private ImageView pic;
	}

	public EntryAdapter(Context context, List<Entry> people) {
		mInflater = LayoutInflater.from(context);
		this.lstPeople = people;
	}

	@Override
	public int getCount() {
		return lstPeople.size();
	}

	@Override
	public Object getItem(int index) {
		return lstPeople.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.person_adapter_item, null);
			holder = new ViewHolder();

			holder.tvDescription = (TextView) convertView
					.findViewById(R.id.etDescription);			
			holder.tvDate = (TextView) convertView
					.findViewById(R.id.etDate);
			holder.tvValue = (TextView) convertView
					.findViewById(R.id.etValue);
			holder.pic = (ImageView) convertView.findViewById(R.id.ivImage);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(lstPeople != null && position < lstPeople.size()) {
		
			Entry entry = lstPeople.get(position);
			holder.tvDescription.setText(entry.getDescription());
			holder.tvDate.setText(entry.getDate());
			holder.tvValue.setText("Valor: R$ " + entry.getValue());
			
			if(entry.getType() == 0){
				holder.pic.setImageResource(R.drawable.ic_credit);
				holder.tvValue.setTextColor(Color.BLUE);
			}else{
				holder.pic.setImageResource(R.drawable.ic_debit);
				holder.tvValue.setTextColor(Color.RED);
			}
		}
		return convertView;
	}

}
