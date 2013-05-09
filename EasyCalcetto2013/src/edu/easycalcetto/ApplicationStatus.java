package edu.easycalcetto;

public enum ApplicationStatus {
	REGISTERED, UNREGISTERED, REGISTRATION_PENDING;

	public static ApplicationStatus getByString(String string) {
		for(ApplicationStatus as : values()){
			if(as.toString().equals(string)) return as;
		}
		return null;
	}
}
