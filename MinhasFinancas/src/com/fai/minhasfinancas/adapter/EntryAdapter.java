package com.fai.minhasfinancas.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
		private TextView tvTitle;
		private TextView tvSubtitle;
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

			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.etTitle);
			holder.tvSubtitle = (TextView) convertView
					.findViewById(R.id.etSubtitle);
			holder.pic = (ImageView) convertView.findViewById(R.id.ivImage);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(lstPeople != null && position < lstPeople.size()) {
		
			Entry entry = lstPeople.get(position);
			holder.tvTitle.setText(entry.getName());
			holder.tvSubtitle.setText("Valor: " + entry.getValue());
		
//			if (entry.getPicture() != null) {
//				byte[] blob = entry.getPicture();
//				Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
//				ImageView image = new ImageView(convertView.getContext());
//				image.setImageBitmap(bmp);
//				holder.pic.setImageBitmap(bmp);
//			} else {
//				holder.pic.setImageResource(R.drawable.ic_launcher);
//			}
		}
		return convertView;
	}

}
