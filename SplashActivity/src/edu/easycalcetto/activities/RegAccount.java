package edu.easycalcetto.activities;

import android.os.Parcel;
import android.os.Parcelable;

public class RegAccount implements Parcelable {
	private String name, surname,number;
	private int age;
	
	
	public RegAccount(String name, String surname, int age, String number) {
		this.name = name;
		this.surname = surname;
		this.age=age;
		this.number=number;
	}
	
	public RegAccount(Parcel p) {
		this.name = p.readString();
		this.surname = p.readString();
		this.age = p.readInt();
		this.number=p.readString();
	}
	

	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel p, int flags) {
		// TODO Auto-generated method stub
		p.writeString(name);
		p.writeString(surname);
		p.writeInt(age);
		p.writeString(number);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR =
			new Parcelable.Creator() {
		
				public RegAccount createFromParcel(Parcel in) {
					return new RegAccount(in);
				}
			
				public RegAccount[] newArray(int size) {
					return new RegAccount[size];
				}
			};


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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
