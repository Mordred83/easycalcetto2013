package edu.easycalcetto.activities;

import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.easycalcetto.R;
import edu.easycalcetto.data.ECMatch;
import edu.easycalcetto.data.ECMatch.UserStatus;

public class NewQAAdapterMatchs extends BaseAdapter {

	private LayoutInflater mInflater;
	private ECMatch[] matchs;

	public NewQAAdapterMatchs(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	public void setData(ECMatch[] matchs) {
		this.matchs = matchs;
	}

	@Override
	public int getCount() {
		return matchs.length;
	}

	@Override
	public Object getItem(int item) {
		return matchs[item];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_layout_match, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.dateText = (TextView) convertView
					.findViewById(R.id.matchDate);
			viewHolder.ownerText = (TextView) convertView
					.findViewById(R.id.matchOwner);
			viewHolder.numberOfPlayerText = (TextView) convertView
					.findViewById(R.id.matchPlayers);
			viewHolder.nameText = (TextView) convertView
					.findViewById(R.id.matchName);
			viewHolder.adesionImage = (ImageView) convertView
					.findViewById(R.id.matchRightImage);

			convertView.setTag(viewHolder);
			viewHolder.dateText.setTag(viewHolder.dateText);
			viewHolder.ownerText.setTag(viewHolder.ownerText);
			viewHolder.numberOfPlayerText.setTag(viewHolder.numberOfPlayerText);
			viewHolder.nameText.setTag(viewHolder.nameText);
			viewHolder.adesionImage.setTag(matchs[position]);

		} else {
			// Fabrizio per ora lascia qua poi ci penso io a questo else
		}
		Calendar c = matchs[position].getDates()[0];
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH)+1;
		int year = c.get(Calendar.YEAR);
		String data = String.format("%1$02d/%2$02d/%3$4d", day,month,year);
		String partecipanti = matchs[position].getConfirmed()+"/"
				+ matchs[position].getNumberMaxPlayer()*2;
		String creatore = matchs[position].getOwner().getName() + " "
				+ matchs[position].getOwner().getSurname();
		String nomePartita = matchs[position].getName();
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.dateText.setText(data);
		holder.ownerText.setText(creatore);
		holder.numberOfPlayerText.setText("Partecipanti: " + partecipanti);
		holder.nameText.setText(nomePartita);
		int imageResID = getRightImage(matchs[position]);
		//if(imageResID != -1)
		holder.adesionImage.setImageResource(imageResID);
		return convertView;
	}

	private int getRightImage(ECMatch ecMatch) {
		if(ecMatch.getUserStatus() == null)
			return -1;
		UserStatus generalStatus = UserStatus.REFUSED;
		for (int i = 0; i < ecMatch.getUserStatus().length
				&& generalStatus != UserStatus.CONFIRMED; i++) {
			UserStatus us = ecMatch.getUserStatus()[i];
			if (us.equals(UserStatus.CONFIRMED) || us.equals(UserStatus.OWNER))
				generalStatus = UserStatus.CONFIRMED;
			else if (us == UserStatus.WAITING)
				generalStatus = UserStatus.WAITING;

		}
		switch (generalStatus) {
		case CONFIRMED:
			return R.drawable.btn_check_buttonless_on;
		case REFUSED:
			return R.drawable.ic_delete;
		default:
			return -1;
		}
	}

	static class ViewHolder {
		TextView dateText;
		TextView ownerText;
		TextView numberOfPlayerText;
		TextView nameText;
		ImageView adesionImage;
	}
}