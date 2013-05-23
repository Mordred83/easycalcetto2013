package edu.easycalcetto.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import edu.easycalcetto.R;

public class NoNetworkDialogFragment extends DialogFragment{
	
	TextView message_tv;
	Button exitB, openPB;
	OnClickListener buttonListner;
	
	public NoNetworkDialogFragment(){};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		buttonListner = (OnClickListener) activity;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_no_network, container);
        message_tv = (EditText) view.findViewById(R.id.message_textview);
        message_tv.setText(R.string.msg_no_network);
        exitB = (Button) view.findViewById(R.id.exit_button);
        exitB.setOnClickListener(buttonListner);
        openPB = (Button) view.findViewById(R.id.open_network_button);
        openPB.setOnClickListener(buttonListner);
        return view;
    }
}
