package edu.easycalcetto;

public enum ApplicationStatus {
	UNINITIALIZED, REGISTERED, UNREGISTERED, REGISTRATION_PENDING, ERROR;

	public static ApplicationStatus getByString(String string) {
		for(ApplicationStatus as : values()){
			if(as.toString().equals(string)) return as;
		}
		return null;
	}
}
