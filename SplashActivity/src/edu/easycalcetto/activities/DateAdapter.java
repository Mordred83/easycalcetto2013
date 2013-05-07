package edu.easycalcetto.activities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import edu.easycalcetto.R;

public class DateAdapter extends BaseAdapter {
	
	private final Context context;
	private final ArrayList<Calendar> list;
	private final String dateFormatString;
	private final String timeFormatString;
	//final Method method;
	
	
	
	public DateAdapter(Context context, ArrayList<Calendar> dates) {
		if(dates == null) throw new IllegalArgumentException("The dates list can't be null");
		this.context = context;
		this.list = dates;
		this.dateFormatString = context.getResources().getString(R.string.rowView_date_format);
		this.timeFormatString = context.getResources().getString(R.string.rowView_time_format);
		//this.method = method;
		//this.removeListner = removeButtonListener;
	}

	// public DateAdapter(Context context, Collection<? extends Calendar> data){
	// this.context = context;
	// if(data == null){
	// this.data = new ArrayList<Calendar>();
	// }
	// else{
	// this.data = data;
	// }
	// }

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = new ViewHolder();
		Calendar c = list.get(position);
		
		if(convertView == null){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    convertView = inflater.inflate(R.layout.rowlayout_date, parent, false);
		    
		    holder.dateButton = (Button) convertView.findViewById(R.id.date_Button);
		    holder.timeButton = (Button) convertView.findViewById(R.id.time_Button);
		    holder.removeButton = (ImageButton) convertView.findViewById(R.id.remove_Button);
		    
		    holder.dateButton.setTag(holder.dateButton);
		    holder.timeButton.setTag(holder.timeButton);
		    holder.removeButton.setTag(holder.removeButton);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		holder.dateButton.setText(String.format(dateFormatString, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR)));
		holder.timeButton.setText(String.format(timeFormatString, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));
		holder.removeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				list.remove(position);
				notifyDataSetChanged();
			}
		});
		holder.removeButton.setEnabled(false);
		return null;
	}

	static class ViewHolder {
		Button dateButton;
		Button timeButton;
		ImageButton removeButton;
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
		return position;
	}

}
