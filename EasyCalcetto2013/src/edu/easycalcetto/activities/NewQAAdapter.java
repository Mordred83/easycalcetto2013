package edu.easycalcetto.activities;

import static android.widget.ImageView.ScaleType.FIT_XY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.easycalcetto.ECApplication;
import edu.easycalcetto.R;
import edu.easycalcetto.data.ECUser;

public class NewQAAdapter extends BaseAdapter {
	private static final boolean DEBUG = true;
	private LayoutInflater mInflater;
	private ECUser[] data;
	ViewHolder viewHolder;
	private Context context;
	private ECApplication app;

	public NewQAAdapter(Context context, ECApplication app) {
		mInflater = LayoutInflater.from(context);
		this.app = app;
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
		// BitmapDrawable bdrw = new BitmapDrawable(context.getResources(), new
		// File(app.getImagesDir(),
		// data[position].getPhotoName()).getAbsolutePath());
		File imageFile = new File(app.getImagesDir(),
				data[position].getPhotoName());
		if (DEBUG) {
			File destFile = new File(app.getExternalFilesDir(null),
					imageFile.getName());
			try {
				Log.d("copyFile", "path: " + destFile.getPath() + " exists: "
						+ destFile.exists() + " create: "
						+ destFile.getParentFile().canWrite());
				FileInputStream fis = new FileInputStream(imageFile);
				FileOutputStream fos = new FileOutputStream(destFile);
				byte[] buffer = new byte[1024];
				int i = 0;
				while ((i = fis.read(buffer)) > 0)
					fos.write(buffer, 0, i);
				fis.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("Adapter", "path: " + imageFile.getPath() + " exists: "
					+ imageFile.exists());
		}
		Bitmap bdrw = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

		viewHolder.contactImage.setImageBitmap(bdrw);
		// viewHolder.contactImage.setImageResource(R.drawable.default_avatar);
		viewHolder.contactImage.setScaleType(FIT_XY);

		return convertView;
	}

	static class ViewHolder {
		TextView nameText;
		TextView surnameText;
		ImageView contactImage;
	}
}