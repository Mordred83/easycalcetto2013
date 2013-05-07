package edu.easycalcetto.activities;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import edu.easycalcetto.CommonUtilities;
import edu.easycalcetto.ECApplication;
import edu.easycalcetto.R;
import edu.easycalcetto.activities.NewQAAdapter.ViewHolder;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.MyCheckable;

public class NewQAAdapterInvitedPlayers extends BaseAdapter {
	private LayoutInflater mInflater;
	private MyCheckable<ECUser>[] data;
	private Context context;

	public NewQAAdapterInvitedPlayers(Context context) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	public void setData(MyCheckable<ECUser>[] data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.length;
	}

	@Override
	public Object getItem(int item) {
		return data[item];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ECUser ecu = data[position].getData();
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.list_layout_invitati, null);
			
			viewHolder.nameText = (TextView) convertView
					.findViewById(R.id.personName);
			viewHolder.surnameText = (TextView) convertView
					.findViewById(R.id.personSurname);
			viewHolder.contactImage = (ImageView) convertView
					.findViewById(R.id.personImage);
			viewHolder.answerImage = (ImageView) convertView
					.findViewById(R.id.answer);

			convertView.setTag(viewHolder);
			viewHolder.nameText.setTag(viewHolder.nameText);
			viewHolder.nameText.setTag(viewHolder.surnameText);
			viewHolder.contactImage.setTag(data[position]);
			viewHolder.answerImage.setTag(data[position]);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.nameText.setText(ecu.getName());
		holder.surnameText.setText(ecu.getSurname());
		SharedPreferences pref = context.getSharedPreferences(
				ECApplication.PREFS_NAME, ECApplication.MODE_PRIVATE);
		String imageDir = pref.getString(CommonUtilities.PREFNAME_IMAGEDIR,
				null);
		boolean imageExists = false;
		if (imageDir != null) {
			File imageFile = new File(imageDir, ecu.getPhotoName());
			if (imageFile != null && imageFile.exists()) {
				imageExists = true;
				holder.contactImage.setImageBitmap(BitmapFactory
						.decodeFile(imageFile.getAbsolutePath()));
			}
		}
		if (!imageExists) {
			holder.contactImage.setImageResource(R.drawable.default_avatar);
		}
		holder.contactImage.setScaleType(ScaleType.FIT_XY);
		int imageResID = (data[position].isChecked()) ? R.drawable.ic_delete:-1;
		holder.answerImage.setImageResource(imageResID);
		return convertView;
	}

	static class ViewHolder {
		TextView nameText;
		TextView surnameText;
		ImageView contactImage;
		ImageView answerImage;
	}
}