package edu.easycalcetto.activities;

import static android.content.Context.MODE_PRIVATE;
import static edu.easycalcetto.Constants.PREFS_NAME;

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
import edu.easycalcetto.R;
import edu.easycalcetto.data.ECUser;

public class NewQAAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ECUser[] data;
	ViewHolder viewHolder;
	private Context context;

	public NewQAAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}

	public void setData(ECUser[] friends) {
		this.data = friends;
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

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_layout, null);
			viewHolder = new ViewHolder();
			viewHolder.nameText = (TextView) convertView
					.findViewById(R.id.personName);
			viewHolder.surnameText = (TextView) convertView
					.findViewById(R.id.personSurname);
			viewHolder.contactImage = (ImageView) convertView
					.findViewById(R.id.personImage);
			convertView.setTag(viewHolder);

			viewHolder.nameText.setTag(viewHolder.nameText);
			viewHolder.nameText.setTag(viewHolder.surnameText);
			viewHolder.contactImage.setTag(data[position]);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// ViewHolder holder = (ViewHolder) convertView.getTag();
		viewHolder.nameText.setText(data[position].getName());
		viewHolder.surnameText.setText(data[position].getSurname());
		
		SharedPreferences pref = context.getSharedPreferences(
				PREFS_NAME, MODE_PRIVATE);
		String imageDir = pref.getString(CommonUtilities.PREFNAME_IMAGEDIR,
				null);
		boolean imageExists = false;
		if (imageDir != null) {
			File imageFile = new File(imageDir, data[position].getPhotoName());
			if (imageFile != null && imageFile.exists()) {
				imageExists = true;
				viewHolder.contactImage.setImageBitmap(BitmapFactory
						.decodeFile(imageFile.getAbsolutePath()));
			}
		}
		if(!imageExists){
			viewHolder.contactImage.setImageResource(R.drawable.default_avatar);
		}
		
		viewHolder.contactImage.setScaleType(ScaleType.FIT_XY);

		return convertView;
	}

	static class ViewHolder {
		TextView nameText;
		TextView surnameText;
		ImageView contactImage;
	}
}