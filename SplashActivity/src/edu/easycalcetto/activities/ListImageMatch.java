package edu.easycalcetto.activities;


import android.graphics.drawable.Drawable;

/**
 * POJO for holding each list choice
 *
 */
public class ListImageMatch {
	private String   _name;
	private Drawable _img;
	private String   _val;

	public ListImageMatch( String name, Drawable img, String val ) {
		_name = name;
		_img = img;
		_val = val;
	}

	public String getName() {
		return _name;
	}

	public Drawable getImg() {
		return _img;
	}

	public String getVal() {
		return _val;
	}

}