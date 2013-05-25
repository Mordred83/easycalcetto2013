package edu.easycalcetto.activities;

import static edu.easycalcetto.Constants.PREFS_NAME;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import edu.easycalcetto.CommonUtilities;
import edu.easycalcetto.Constants;
import edu.easycalcetto.ECApplication;
import edu.easycalcetto.R;
import edu.easycalcetto.data.ECUser;

public class NewQAAdapterSelectFriends extends BaseAdapter {
	private LayoutInflater mInflater;
	private CheckWrapper<ECUser>[] data;
	private Context context;
	// boolean[] checkBoxState;
	ViewHolder viewHolder;

	public NewQAAdapterSelectFriends(Context context, CheckWrapper<ECUser>[] p) {
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.data = p; // set class level variable.
	}

	public void setData(CheckWrapper<ECUser>[] data) {
		this.data = data;
		// checkBoxState=new boolean[data.length];
		for (int i = 0; i < data.length; i++) {
			// checkBoxState[i]=data[i].isCheck();
		}
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_select_friends, null);
			viewHolder = new ViewHolder();

			viewHolder.nameText = (TextView) convertView
					.findViewById(R.id.personName);
			viewHolder.surnameText = (TextView) convertView
					.findViewById(R.id.personSurname);
			viewHolder.contactImage = (ImageView) convertView
					.findViewById(R.id.personImage);
			viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ECUser user = data[position].getData();
		viewHolder.nameText.setText(user.getName());
		viewHolder.surnameText.setText(user.getSurname());
		SharedPreferences pref = context.getSharedPreferences(
				PREFS_NAME, ECApplication.MODE_PRIVATE);
		String imageDir = pref.getString(CommonUtilities.PREFKEY_IMAGEDIR_PATH,
				null);
		boolean imageExists = false;
		if (imageDir != null) {
			File imageFile = new File(imageDir, user.getPhotoName());
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
		// viewHolder.checkBox.setChecked(checkBoxState[position]);
		viewHolder.checkBox.setChecked(data[position].isChecked());
		viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					// checkBoxState[position]=true;
					data[position].setChecked(true);
				} else {
					// checkBoxState[position]=false;
					data[position].setChecked(false);
				}
			}
		});
		// viewHolder.checkBox.setChecked(checkBoxState[position]);
		return convertView;
	}

	static class ViewHolder {
		TextView nameText;
		TextView surnameText;
		ImageView contactImage;
		CheckBox checkBox;
		CheckBox checkAll;
	}

}