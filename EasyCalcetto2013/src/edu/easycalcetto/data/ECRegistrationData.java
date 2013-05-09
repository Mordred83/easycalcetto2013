package edu.easycalcetto.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.YuvImage;
import android.os.Parcel;
import android.os.Parcelable;

public class ECRegistrationData implements Parcelable, HTTPPostable{
	/**
	 * 
	 */
	private String name;
	private String surname;
	private long yob;
	private String mobileNumber;
	private String code;
	
	public ECRegistrationData(String name, String surname, String age,
			String mobileNumber) {
		super();
		this.name = name;
		this.surname = surname;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, c.get(Calendar.YEAR)-Integer.parseInt(age));
		this.yob = c.getTimeInMillis();
		this.mobileNumber = mobileNumber;
		this.code = "";
	}

	public ECRegistrationData(Parcel in) {
		readFromParcel(in);
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int arg1) {
		out.writeInt(this.describeContents());
		out.writeString(name);
		out.writeString(surname);
		out.writeLong(yob);
		out.writeString(mobileNumber);
		out.writeString(code);
	}

	private void readFromParcel(Parcel in) {
		in.readInt(); // Excluding class description from creation
		this.name = in.readString();
		this.surname = in.readString();
		this.yob = in.readLong();
		this.mobileNumber = in.readString();
		this.code = in.readString();
	}

	public static final Parcelable.Creator<ECRegistrationData> CREATOR = new Parcelable.Creator<ECRegistrationData>() {

		public ECRegistrationData createFromParcel(Parcel source) {
			return new ECRegistrationData(source);
		}

		public ECRegistrationData[] newArray(int size) {
			return new ECRegistrationData[size];
		}
	};
	
	public List<NameValuePair> getObjectAsNameValuePairList() {
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("name", name));
		list.add(new BasicNameValuePair("surname", surname));
		list.add(new BasicNameValuePair("age", String.valueOf(yob)));
		list.add(new BasicNameValuePair("mobile", mobileNumber));
		list.add(new BasicNameValuePair("code", code));
		return list;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public long getAge() {
		return yob;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}
	
	public String getCode(){
		return code;
	}
	
	public boolean setCode(String code){
		String pattern = "\\d{5}";
		if(code.matches(pattern)){
			this.code = code;
			return true;
		}
		return false;
	}
}
