package edu.easycalcetto.data;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class SpaceTimeInterval implements Parcelable{
	private final String location;
	private final Date startDate;
	private final Date endDate;
	
	SpaceTimeInterval(String location, Date startDate, Date endDate){
		this.location = location;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	
}
