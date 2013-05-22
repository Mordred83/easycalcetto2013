package edu.easycalcetto.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ECUser implements Parcelable, HTTPPostable{

	public static final long serialVersionUID = 1014215480574712987L;
	public static final String IMAGE_FILE_NAME_DEFAULT = "default.png";
	
	private long _id;
	private String num_tel;
	private String name;
	private String surname;
	private String yob;
	private String photoName;

	/**
	 * @param _id
	 * @param num_tel
	 *            max 20 chars
	 * @param name
	 *            max 30 chars
	 * @param surname
	 *            max 30 chars
	 * @param yob
	 * @param role
	 * @param rating
	 * @param votes
	 * @param _id_comment
	 */
	public ECUser(long _id, String num_tel, String name, String surname,
			String yob) {

		if (num_tel.length() > 20 || name.length() > 30
				|| surname.length() > 30)
			throw new IllegalArgumentException(
					"String Argument/s exceeds database constrains");

		this._id = _id;
		this.num_tel = num_tel;
		this.name = name;
		this.surname = surname;
		this.yob = yob;
	}

	public ECUser(Parcel source) {
		_id = source.readLong();
		num_tel = source.readString();
		name = source.readString();
		surname = source.readString();
		yob = source.readString();
		photoName = source.readString();
	}

	public ECUser(JSONObject jO) {
		try {
			_id = jO.getLong("id");
			num_tel = jO.getString("num_tel");
			name = jO.getString("name");
			surname = jO.getString("surname");
			yob = jO.getString("yob");
			String tmp = jO.getString("photo");
			tmp = tmp.substring(tmp.lastIndexOf("/")+1).trim();
			photoName = tmp.replace("\\", "");
			Log.d("photoName: ", photoName);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}

	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id);
		dest.writeString(num_tel);
		dest.writeString(name);
		dest.writeString(surname);
		dest.writeString(yob);
		dest.writeString(photoName);
		// dest.writeString(role);
		// dest.writeDouble(rating);
		// dest.writeInt(votes);
		// dest.writeLong(_id_comment);
	}

	public static final Parcelable.Creator<ECUser> CREATOR = new Parcelable.Creator<ECUser>() {

		public ECUser createFromParcel(Parcel source) {

			return new ECUser(source);
		}

		public ECUser[] newArray(int size) {
			return new ECUser[size];
		}

	};

	public static final ECComunicable.Creator<ECUser> ECCREATOR = new ECComunicable.Creator<ECUser>() {

		@Override
		public ECUser createFromJSONObject(JSONObject jo) {
			return new ECUser(jo);
		}

		@Override
		public ECUser[] newArray(int size) {
			return new ECUser[size];
		}
	};

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getNum_tel() {
		return num_tel;
	}

	public void setNum_tel(String num_tel) {
		this.num_tel = num_tel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getYob() {
		return yob;
	}

	public void setYob(String yob) {
		this.yob = yob;
	}

	// public String getRole() {
	// return role;
	// }
	//
	// public void setRole(String role) {
	// this.role = role;
	// }
	//
	// public double getRating() {
	// return rating;
	// }
	//
	// public void setRating(double rating) {
	// this.rating = rating;
	// }
	//
	// public int getVotes() {
	// return votes;
	// }
	//
	// public void setVotes(int votes) {
	// this.votes = votes;
	// }
	//
	// public long get_id_comment() {
	// return _id_comment;
	// }
	//
	// public void set_id_comment(long _id_comment) {
	// this._id_comment = _id_comment;
	// }

	public List<NameValuePair> getObjectAsNameValuePairList() {
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("id", ""+_id));
		al.add(new BasicNameValuePair("name", name));
		al.add(new BasicNameValuePair("surname", surname));
		al.add(new BasicNameValuePair("yob", yob));
		return al;
	}

	public static String[] getTags() {
		Field[] fArr = ECUser.class.getDeclaredFields();
		String[] sArr = new String[fArr.length];
		for (int i = 0; i < fArr.length; i++)
			sArr[i] = fArr[i].getName();
		return sArr;
	}

	public static ECUser[] createFromJSONArray(JSONArray jArr)
			throws JSONException {
		ECUser[] result = new ECUser[jArr.length()];
		for (int i = 0; i < jArr.length(); i++) {
			JSONObject jO = jArr.getJSONObject(i);
			ECUser ecu = new ECUser(jO);
			result[i] = ecu;
		}
		return result;
	}
	
	public int getAge(){
		long yobInt = Long.parseLong(yob);
		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		c.setTimeInMillis(yobInt);
		int yearOfBirth = c.get(Calendar.YEAR);
		int age = currentYear-yearOfBirth;
		return age;
	}
	
	public String generatePhotoFileName() {
		StringBuffer sb = new StringBuffer();
		Calendar c = Calendar.getInstance();
		sb.append(this._id + "_");
		sb.append(c.get(Calendar.YEAR) + "_");
		sb.append(c.get(Calendar.MONTH) + "_");
		sb.append(c.get(Calendar.DAY_OF_MONTH) + "_");
		sb.append(c.get(Calendar.HOUR_OF_DAY) + "_");
		sb.append(c.get(Calendar.MINUTE) + "_");
		sb.append(c.get(Calendar.SECOND));
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof ECUser){
			ECUser other = (ECUser)o;
			return this.get_id() == other.get_id();
		} 
		return false;
	}
	
	public static Bitmap getBitmapFromUrl(String photoPath){
		Bitmap photo = null;
		photo = BitmapFactory.decodeFile(photoPath);
		return photo;
	}
	
	public static String generateNewPhotoName(String oldName){
		StringBuffer newName = new StringBuffer();
		return newName.toString();
	}

	public void setPhotoName(String imageFileName) {
		this.photoName = imageFileName;
	}

	public String getPhotoName() {
		return photoName;
	}

}