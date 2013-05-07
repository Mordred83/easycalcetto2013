package edu.easycalcetto;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import com.google.android.gcm.GCMRegistrar;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;
import edu.easycalcetto.data.ECRegistrationData;
import edu.easycalcetto.data.ECUser;
import static edu.easycalcetto.CommonUtilities.PREFNAME_IMAGEDIR;

public class ECApplication extends Application {

	public static final String IMAGES_DIRECTORY_NAME = "myImages";
	public static final String PREFS_NAME = "APPLICATION_PREFERENCES";
	public static final String PREFKEY_REGSTATUS = "REGISTRATION_STATUS";;
	public static final String PREFKEY_OWNER_ID = "ID";
	public static final String PREFKEY_OWNER_NAME = "NAME";
	public static final String PREFKEY_OWNER_SURNAME = "SURNAME";
	public static final String PREFKEY_OWNER_YOB = "AGE";
	public static final String PREFKEY_OWNER_NUMBER = "NUMBER";
	public static final String PREFKEY_OWNER_PHOTO_FILE_NAME = "PHOTOPATH";

	private File imagesDir;
	private ECUser owner = null;

	@Override
	public void onCreate() {
		super.onCreate();

		boolean initialized = false;
		try {
			initialize();
			initialized = isInitialized();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					"Si è verificato un'errore", Toast.LENGTH_SHORT).show();
		}
		if (!initialized)
			setApplicationStatus(ApplicationStatus.UNREGISTERED);
	}

	public ECUser getOwner() {
		return owner;
	}

	public File getImagesDir() {
		return imagesDir;
	}

	private boolean isInitialized() {
		if (getImagesDir() == null || !getImagesDir().exists())
			try {
				initImagesDir();
			} catch (Exception e) {
				e.printStackTrace();
			}

		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		long id = pref.getLong(PREFKEY_OWNER_ID, -1);
		String name = pref.getString(PREFKEY_OWNER_NAME, null);
		String surname = pref.getString(PREFKEY_OWNER_SURNAME, null);
		String num_tel = pref.getString(PREFKEY_OWNER_NUMBER, null);
		String yob = pref.getString(PREFKEY_OWNER_YOB, null);
		String photo_path = pref.getString(PREFKEY_OWNER_PHOTO_FILE_NAME, null);
		if (id != -1 && name != null && surname != null && num_tel != null
				&& yob != null && photo_path != null) {
			owner = new ECUser(id, num_tel, name, surname, yob);
			owner.setPhotoName(photo_path);
		}
		return (owner != null) ? true : false;
	}

	private void initialize() throws StreamCorruptedException, IOException,
			ClassNotFoundException {
		initImagesDir();
	}

	private void initImagesDir() throws IOException {
		File externalRootDir = getExternalFilesDir(null);
		File imagesDir = new File(externalRootDir, IMAGES_DIRECTORY_NAME);
		if (!imagesDir.exists()) {
			imagesDir.mkdir();
			new File(imagesDir, ".nomedia").createNewFile();
		}
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(PREFNAME_IMAGEDIR, imagesDir.getAbsolutePath());
		editor.commit();
		this.imagesDir = imagesDir;
	}

	public boolean updateOwner(ECUser owner) {
		try {
			writeOwner(owner);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void writeOwner(ECUser owner) {
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		Editor e = pref.edit();
		e.putLong(PREFKEY_OWNER_ID, owner.get_id());
		e.putString(PREFKEY_OWNER_NAME, owner.getName());
		e.putString(PREFKEY_OWNER_SURNAME, owner.getSurname());
		e.putString(PREFKEY_OWNER_YOB, owner.getYob());
		e.putString(PREFKEY_OWNER_NUMBER, owner.getNum_tel());
		e.putString(PREFKEY_OWNER_PHOTO_FILE_NAME, owner.getPhotoName());
		e.commit();
	}

	public void setApplicationStatus(ApplicationStatus appStatus) {
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		Editor e = pref.edit();
		e.putString(PREFKEY_REGSTATUS, appStatus.toString());
		e.commit();
	}

	public boolean setOwner(long id, ECRegistrationData registration) {
		owner = new ECUser(id, registration.getMobileNumber(),
				registration.getName(), registration.getSurname(),
				String.valueOf(registration.getAge()));
		owner.setPhotoName(ECUser.IMAGE_FILE_NAME_DEFAULT);
		return updateOwner(owner);
	}

}
