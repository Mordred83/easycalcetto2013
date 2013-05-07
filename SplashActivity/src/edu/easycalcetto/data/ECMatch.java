package edu.easycalcetto.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ECMatch implements Parcelable, HTTPPostable {

	public static final String[] PARTECIPANT_STATUSES = { "CONFIRMED",
			"PENDING", "REFUSED" };

	public static final String PARTECIPANT_STATUS_CONFIRMED = PARTECIPANT_STATUSES[0];
	public static final String PARTECIPANT_STATUS_PENDING = PARTECIPANT_STATUSES[1];
	public static final String PARTECIPANT_STATUS_REFUSED = PARTECIPANT_STATUSES[2];

	private static final int DATES_SUPPORTED = 5;

	private long _id;
	private ECUser creator;
	private String name;
	private String status;
	private String[] userStatus;
	private String location;
	private ECUser[] partecipants;

	// TODO: aggiugere campo al database
	// getNumberMaxPlayer()

	private int number;
	private int confirmed;
	private long[] dates;

	public ECMatch(long _id, ECUser creator, String name, String status,
			String[] userStatus, String location, int number, long... dates) {
		super();
		this._id = _id;
		this.creator = creator;
		this.name = name;
		this.status = status;
		this.location = location;
		this.number = number;
		this.dates = dates;
		this.userStatus = new String[dates.length];
		for (int i = 0; i < dates.length; i++)
			if (userStatus != null && i < userStatus.length
					&& userStatus[i] != null)
				this.userStatus[i] = userStatus[i].toString().toLowerCase();
			else
				this.userStatus[i] = UserStatus.WAITING.toString()
						.toLowerCase();
		this.confirmed =0;
	}

	public ECMatch(Parcel source) {
		_id = source.readLong();
		creator = source.readParcelable(ECUser.class.getClassLoader());
		name = source.readString();
		status = source.readString();
		location = source.readString();
		number = source.readInt();

		dates = source.createLongArray();
		userStatus = source.createStringArray();
		partecipants = (ECUser[]) source.readParcelableArray(ECUser.class
				.getClassLoader());
		confirmed = source.readInt();
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id);
		dest.writeParcelable(creator, flags);
		dest.writeString(name);
		dest.writeString(status);
		dest.writeString(location);
		dest.writeInt(number);
		dest.writeLongArray(dates);
		dest.writeStringArray(userStatus);
		dest.writeParcelableArray(partecipants, flags);
		dest.writeInt(confirmed);
	}

	private ECMatch(JSONObject jO) {

		try {
			_id = jO.getLong("id");
			creator = new ECUser(jO.getJSONObject("creator"));
			name = jO.getString("name");
			status = jO.getString("status");
			location = jO.getString("location");
			number = jO.getInt("number");
			dates = getDatesFromJSONObject(jO);
			if (status.equals(Status.TO_PLAY.toString())) {
				userStatus = getStatusesFromJSONObject(jO);
			} else {
				userStatus = null;
			}
			confirmed = jO.optInt("confirmed", 0);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<ECMatch> CREATOR = new Parcelable.Creator<ECMatch>() {

		public ECMatch createFromParcel(Parcel source) {
			return new ECMatch(source);
		}

		public ECMatch[] newArray(int size) {
			return new ECMatch[size];
		}

	};

	public static final long NEW_MATCH_FLAG_ID = 0;

	private static final String NO_DATE_FLAG_ID = "" + 0;
	private static final String NO_STATUS_FALG_ID = null;

	public long getIdMatch() {
		return _id;
	}

	public void setIdMatch(long _id) {
		this._id = _id;
	}

	public ECUser getOwner() {
		return creator;
	}

	public int getNumberMaxPlayer() {
		return number;
	}

	public void setNumberMaxPlayer(int number) {
		this.number = number;
	}

	public void setOwner(ECUser creator) {
		this.creator = creator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return Status.getFromString(status);
	}

	public void setStatus(Status status) {
		this.status = status.toString();
	}

	public String getPlace() {
		return location;
	}

	public void setPlace(String location) {
		this.location = location;
	}

	public UserStatus[] getUserStatus() {
		UserStatus[] usArr = null;
		if (userStatus != null) {
			usArr = new UserStatus[userStatus.length];
			for (int i = 0; i < userStatus.length; i++)
				usArr[i] = UserStatus.getFromString(userStatus[i]);
		}
		return usArr;
	}

	public void setUserStatus(UserStatus[] status) {
		String[] sArr = new String[status.length];
		for (int i = 0; i < status.length; i++)
			sArr[i] = status[i].toString().toLowerCase();
		this.userStatus = sArr;
	}

	public Calendar getDate() {
		return getDates()[0];
	}

	public void setDate(Calendar cal) {
		dates[0] = cal.getTimeInMillis();
	}

	public Calendar[] getDates() {
		Calendar[] tmpArr = new Calendar[dates.length];
		Calendar c = GregorianCalendar.getInstance();
		for (int i = 0; i < dates.length; i++) {
			c.setTimeInMillis(dates[i]);
			tmpArr[i] = (Calendar) c.clone();
		}
		return tmpArr;
	}

	public void setDate(Date[] dates) {
		long[] tmpArr = new long[dates.length];
		int i = 0;
		for (Date d : dates)
			tmpArr[i++] = d.getTime();
		this.dates = tmpArr;
	}

	public List<NameValuePair> getObjectAsNameValuePairList() {
		ArrayList<NameValuePair> al = new ArrayList<NameValuePair>();
		al.add(new BasicNameValuePair("id", (_id == NEW_MATCH_FLAG_ID) ? ""
				: "" + _id));
		al.add(new BasicNameValuePair("creator", "" + creator.get_id()));
		al.add(new BasicNameValuePair("name", name));
		al.add(new BasicNameValuePair("location", location));
		al.add(new BasicNameValuePair("number", "" + number));
		
		for (int i = 0; i < DATES_SUPPORTED; i++) {
			if (i < dates.length)
				al.add(new BasicNameValuePair("dates[" + i + "]", "" + dates[i]));
			else
				al.add(new BasicNameValuePair("dates[" + i + "]", ""
						+ NO_DATE_FLAG_ID));
		}

		for (int i = 0; i < DATES_SUPPORTED; i++) {
			if (i < userStatus.length)
				al.add(new BasicNameValuePair("userstatus[" + i + "]", ""
						+ userStatus[i]));
			else
				al.add(new BasicNameValuePair("userstatus[" + i + "]", ""
						+ NO_STATUS_FALG_ID));
		}

		for (int i = 0; partecipants != null && i < partecipants.length; i++)
			al.add(new BasicNameValuePair("partecipants[" + i + "]", ""
					+ partecipants[i].get_id()));
		return al;
	}

	public static ECMatch[] createFromJSONArray(JSONArray jArr)
			throws JSONException {
		ECMatch[] result = new ECMatch[jArr.length()];
		for (int i = 0; i < jArr.length(); i++) {
			JSONObject jO = jArr.getJSONObject(i);
			ECMatch ecu = new ECMatch(jO);
			result[i] = ecu;
		}
		return result;
	}

	public enum Status {
		TO_PLAY, PLAYED;

		public static Status getFromString(String s) {
			for (Status st : Status.values())
				if (s.toLowerCase().equals(st.toString()))
					return st;
			return null;
		}

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public enum UserStatus {
		OWNER, CONFIRMED, REFUSED, WAITING;

		public static UserStatus getFromString(String s) {
			for (UserStatus st : UserStatus.values())
				if (s.toLowerCase().equals(st.toString()))
					return st;
			return null;
		}

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	private static long[] getDatesFromJSONObject(JSONObject jO)
			throws JSONException {
		ArrayList<String> tmpAl = new ArrayList<String>();
		long[] tmpArr;
		int i = 1;
		String l = jO.getString("date" + i);
		boolean stop = l.equalsIgnoreCase("" + 0);
		while (!stop) {
			l = jO.getString("date" + i);
			stop = l.equalsIgnoreCase("" + 0);
			tmpAl.add(l);
			i++;
		}
		tmpArr = new long[tmpAl.size()];
		for (i = 0; i < tmpArr.length; i++)
			tmpArr[i] = Long.valueOf(tmpAl.get(i));
		return tmpArr;
	}

	private static String[] getStatusesFromJSONObject(JSONObject jO)
			throws JSONException {
		JSONArray tmpArr = jO.getJSONArray("statuses");
		String[] us = new String[tmpArr.length()];
		for (int i = 0; i < tmpArr.length(); i++) {
			us[i] = tmpArr.getString(i);
		}
		return (us.length > 0) ? us : null;
	}

	protected long getDatesAsLong(int index) {
		return dates[index];
	}

	public void setPartecipants(ECUser[] partecipants) {
		this.partecipants = partecipants;
	}

	public int getConfirmed() {
		return confirmed;
	}
	
	public void setConfirmed(int confirmedUsers){
		confirmed = confirmedUsers;
	}

}
