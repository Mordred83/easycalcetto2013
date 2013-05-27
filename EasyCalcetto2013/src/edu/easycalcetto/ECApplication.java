package edu.easycalcetto;

import static edu.easycalcetto.ApplicationStatus.ERROR;
import static edu.easycalcetto.ApplicationStatus.UNINITIALIZED;
import static edu.easycalcetto.CommonUtilities.PREFKEY_IMAGEDIR_PATH;
import static edu.easycalcetto.Constants.IMAGES_DIRECTORY_NAME;
import static edu.easycalcetto.Constants.PREFKEY_OWNER_ID;
import static edu.easycalcetto.Constants.PREFKEY_OWNER_NAME;
import static edu.easycalcetto.Constants.PREFKEY_OWNER_NUMBER;
import static edu.easycalcetto.Constants.PREFKEY_OWNER_PHOTO_FILE_NAME;
import static edu.easycalcetto.Constants.PREFKEY_OWNER_SURNAME;
import static edu.easycalcetto.Constants.PREFKEY_OWNER_YOB;
import static edu.easycalcetto.Constants.PREFKEY_REGSTATUS;
import static edu.easycalcetto.Constants.PREFS_NAME;

import java.io.File;
import java.io.IOException;
import java.io.StreamCorruptedException;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;
import edu.easycalcetto.data.ECRegistrationData;
import edu.easycalcetto.data.ECUser;

public class ECApplication extends Application {
	private static final String LOGTAG = "ECApplication";
	private ECUser owner = null;
	private ApplicationStatus status = null;

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			getApplicationStatus();

			switch (status) {
			case UNINITIALIZED:
				try {
					initialize();
				} catch (Exception e) {
					setApplicationStatus(ERROR);
					Log.e(LOGTAG, "Error occurred during initialization", e);
				}
				setApplicationStatus(ApplicationStatus.UNREGISTERED);
				break;
			case UNREGISTERED:
			case REGISTERED:
			case REGISTRATION_PENDING:
			case ERROR:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					"Si è verificato un'errore", Toast.LENGTH_SHORT).show();
		}
	}

	public ECUser getOwner() {
		if (owner == null) {
			SharedPreferences pref = getSharedPreferences(PREFS_NAME,
					MODE_PRIVATE);
			long id = pref.getLong(PREFKEY_OWNER_ID, -1);
			String name = pref.getString(PREFKEY_OWNER_NAME, null);
			String surname = pref.getString(PREFKEY_OWNER_SURNAME, null);
			String num_tel = pref.getString(PREFKEY_OWNER_NUMBER, null);
			String yob = pref.getString(PREFKEY_OWNER_YOB, null);
			String photo_path = pref.getString(PREFKEY_OWNER_PHOTO_FILE_NAME,
					null);
			if (name != null && surname != null && num_tel != null
					&& yob != null && photo_path != null) {
				owner = new ECUser(id, num_tel, name, surname, yob);
				owner.setPhotoName(photo_path);
			}
		}
		return owner;
	}

	public File getImagesDir() {
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return new File(pref.getString(PREFKEY_IMAGEDIR_PATH, null));
	}

	private void initialize() throws StreamCorruptedException, IOException,
			ClassNotFoundException {
		initImagesDir();
	}

	private void initImagesDir() throws IOException {
		File imagesDir = new File(getFilesDir(), IMAGES_DIRECTORY_NAME);
		if (!imagesDir.exists()) {
			if (!imagesDir.mkdir())
				throw new IOException("Can't create images Directory");
			new File(imagesDir, ".nomedia").createNewFile();
		}
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(PREFKEY_IMAGEDIR_PATH, imagesDir.getAbsolutePath());
		editor.commit();
	}

	public boolean updateOwner(ECUser owner) {
		try {
			return saveOwner(owner);
		} catch (Exception e) {
			return false;
		}
	}

	private boolean saveOwner(ECUser owner) {
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		Editor e = pref.edit();
		e.putLong(PREFKEY_OWNER_ID, owner.get_id());
		e.putString(PREFKEY_OWNER_NAME, owner.getName());
		e.putString(PREFKEY_OWNER_SURNAME, owner.getSurname());
		e.putString(PREFKEY_OWNER_YOB, owner.getYob());
		e.putString(PREFKEY_OWNER_NUMBER, owner.getNum_tel());
		e.putString(PREFKEY_OWNER_PHOTO_FILE_NAME, owner.getPhotoName());
		return e.commit();
	}

	public boolean setApplicationStatus(ApplicationStatus appStatus) {
		boolean result = false;
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		Editor e = pref.edit();
		e.putString(PREFKEY_REGSTATUS, appStatus.toString());
		if (result = e.commit())
			status = appStatus;
		return result;
	}

	public ApplicationStatus getApplicationStatus() {
		if (status == null) {
			SharedPreferences pref = getSharedPreferences(PREFS_NAME,
					MODE_PRIVATE);
			status = ApplicationStatus.getByString(pref.getString(
					PREFKEY_REGSTATUS, UNINITIALIZED.toString()));
		}
		return status;
	}

	public boolean setOwner(long id, ECRegistrationData registration) {
		owner = new ECUser(id, registration.getMobileNumber(),
				registration.getName(), registration.getSurname(),
				String.valueOf(registration.getAge()));
		owner.setPhotoName(ECUser.IMAGE_FILE_NAME_DEFAULT);
		return updateOwner(owner);
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
