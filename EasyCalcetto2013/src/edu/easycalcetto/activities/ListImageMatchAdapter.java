package edu.easycalcetto.activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.easycalcetto.R;

/**
 * Definition of the list adapter...uses the View Holder pattern to
 * optimize performance.
 */
public class ListImageMatchAdapter extends ArrayAdapter<ListImageMatch> {

	private static final int RESOURCE = R.layout.list_image_match_dialog;
	private LayoutInflater inflater;

    static class ViewHolder {
        TextView nameTxVw;
    }

	public ListImageMatchAdapter(Context context, ListImageMatch[] objects)
	{
		super(context, RESOURCE, objects);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder;

		if ( convertView == null ) {
			// inflate a new view and setup the view holder for future use
			convertView = inflater.inflate( RESOURCE, null );

			holder = new ViewHolder();
			holder.nameTxVw =
				(TextView) convertView.findViewById(R.id.descriptionImage);
			convertView.setTag( holder );
		}  else {
			// view already defined, retrieve view holder
			holder = (ViewHolder) convertView.getTag();
		}

		ListImageMatch cat = (ListImageMatch) getItem( position );
		if ( cat == null ) {
			Log.e( "errore", "Invalid category for position: " + position );
		}
		holder.nameTxVw.setText( cat.getName() );
		holder.nameTxVw.setCompoundDrawables( cat.getImg(), null, null, null );

		return convertView;
	}
}