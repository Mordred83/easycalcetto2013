package edu.easycalcetto.data;

import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_REGISTRATION;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import edu.easycalcetto.connection.ECConnectionMessageConstants;

public class MessagesCreator {
	
//	public static Message getRegistrationMessage(Messenger msnger, ECRegistrationData registration){
//		Message m = Message.obtain();
//		Bundle b = createBundle(FUNCDESCRIPTOR_REGISTRATION, "OK_BUDDY", "SORRY_MY_FRIEND");
//		b.putParcelable(ECConnectionMessageConstants.BNDKEY_POST_PARCELABLE, registration);
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_REGISTRATON;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}
	
//	public static Message getConfirmRegistrationMessage(Messenger msnger, ECRegistrationData registration){
//		Message m = Message.obtain();
//		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_CONFIRM_REGISTRATION, "OK_BUDDY", "SORRY_MY_FRIEND");
//		b.putParcelable(ECConnectionMessageConstants.BNDKEY_POST_PARCELABLE, registration);
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CONFIRM_REGISTRATION;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}
//	public static Message getGetFriendsMessage(Messenger msnger, Long id){
//		Message m = Message.obtain();
//		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_GETFRIENDS, "OK_BUDDY", "SORRY_MY_FRIEND");
//		Map<String,String> map = new HashMap<String, String>();
//		map.put("id", String.valueOf(id));
//		b = addParametersToBundle(b, map);
//		b.putString(ECConnectionMessageConstants.BNDKEY_DATATYPE, long.class.getCanonicalName());
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETFRIENDS;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}	
//	public static Message getGetAcquaintanceMessage(Messenger msnger, Long id){
//		Message m = Message.obtain();
//		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_GETACQUAINTANCES, "OK_BUDDY", "SORRY_MY_FRIEND");
//		Map<String,String> map = new HashMap<String, String>();
//		map.put("id", String.valueOf(id));
//		b = addParametersToBundle(b, map);
//		b.putString(ECConnectionMessageConstants.BNDKEY_DATATYPE, long.class.getCanonicalName());
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETACQUAINTANCES;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}
	
//	public static Message getGamePartecipantsMessage(Messenger msnger, Long matchID){
//		Message m = Message.obtain();
//		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_GETMATCH_PARTECIPANTS, "OK_BUDDY", "SORRY_MY_FRIEND");
//		Map<String,String> map = new HashMap<String, String>();
//		map.put("id", String.valueOf(matchID));
//		b = addParametersToBundle(b, map);
//		b.putString(ECConnectionMessageConstants.BNDKEY_DATATYPE, long.class.getCanonicalName());
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETMATCH_PARTECIPANTS;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}
	
//	public static Message getGetOpenMatchesMessage(Messenger msnger, Long id){
//		Message m = Message.obtain();
//		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_GETMATCHES_OPEN, "OK_BUDDY", "SORRY_MY_FRIEND");
//		Map<String,String> map = new HashMap<String, String>();
//		map.put("id", String.valueOf(id));
//		b = addParametersToBundle(b, map);
//		b.putString(ECConnectionMessageConstants.BNDKEY_DATATYPE, long.class.getCanonicalName());
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETMATCHES_OPEN;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}
//	public static Message getGetClosedMatchesMessage(Messenger msnger, Long id){
//		Message m = Message.obtain();
//		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_GETMATCHES_CLOSED, "OK_BUDDY", "SORRY_MY_FRIEND");
//		Map<String,String> map = new HashMap<String, String>();
//		map.put("id", String.valueOf(id));
//		b = addParametersToBundle(b, map);
//		b.putString(ECConnectionMessageConstants.BNDKEY_DATATYPE, long.class.getCanonicalName());
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETMATCHES_CLOSED;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}
	
//	public static Message getCreateMatchMessage(Messenger msnger, ECMatch ecm){
//		Message m = Message.obtain();
//		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_CREATEMATCH, "OK_BUDDY", "SORRY_MY_FRIEND");
//		b.putParcelable(ECConnectionMessageConstants.BNDKEY_POST_PARCELABLE, ecm);
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CREATEMATCH;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}
	
	public static Message getDowloadPhotoMessage(Messenger msnger, long id) {
		//TODO:
		Message m = Message.obtain();
		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_CREATEMATCH, "OK_BUDDY", "SORRY_MY_FRIEND");
		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CREATEMATCH;
		m.replyTo = msnger;
		m.setData(b);
		return m;
	}
	
	public static Message getUploadPhotoMessage(Messenger msnger, long id,String photoPath){
		Message m = Message.obtain();
		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_UPLOAD_PHOTO, "OK_BUDDY", "SORRY_MY_FRIEND");
		b.putLong(ECConnectionMessageConstants.BNDKEY_ID, id);
		b.putString(ECConnectionMessageConstants.BNDKEY_POST_IMAGE_PHOTOPATH, photoPath);
		m.what = ECConnectionMessageConstants.MSGWHAT_POST_IMAGE;
		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_UPLOAD_PHOTO;
		m.replyTo = msnger;
		m.setData(b);
		return m;
	}
	
	private static Bundle createBundle(String funcDescriptor, String resSeccessDescriptor, String resFailureDescriptor){
		Bundle b = new Bundle();
		b.putString(ECConnectionMessageConstants.FUNC, funcDescriptor);
		b.putString(ECConnectionMessageConstants.MSGRESDESCTIPTION_SUCCESS, resSeccessDescriptor);
		b.putString(ECConnectionMessageConstants.MSGRESDESCTIPTION_FAILURE, resFailureDescriptor);
		return b;
	}
	
	private static Bundle addParametersToBundle(Bundle b, Map<String, String> keyParamsMap){
		b.putStringArray(ECConnectionMessageConstants.BNDKEY_PARAM_KEYS, keyParamsMap.keySet().toArray(new String[0]));
		for(String key : keyParamsMap.keySet())
			b.putString(key, keyParamsMap.get(key));
		return b;
	}

	public static Message getConfirmGameMessage(Messenger msnger, long playerID,
			long matchID, int dataID) {
		Message m = Message.obtain();
		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_CONFIRM_GAME, "OK_BUDDY", "SORRY_MY_FRIEND");
		Map<String,String> map = new HashMap<String, String>();
		map.put("player_id", String.valueOf(playerID));
		map.put("match_id", String.valueOf(matchID));
		map.put("data_id", String.valueOf(dataID));
		b = addParametersToBundle(b, map);
		b.putString(ECConnectionMessageConstants.BNDKEY_DATATYPE, long.class.getCanonicalName());
		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CONFIRM_GAME;
		m.replyTo = msnger;
		m.setData(b);
		return m;
	}
	
	public static Message getAddFriendMessage(Messenger msnger, long userID,
			String friendPhone) {
		Message m = Message.obtain();
		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_ADD_FRIEND, "OK_BUDDY", "SORRY_MY_FRIEND");
		Map<String,String> map = new HashMap<String, String>();
		map.put("user_id", String.valueOf(userID));
		map.put("num_tel", friendPhone);
		b = addParametersToBundle(b, map);
		b.putString(ECConnectionMessageConstants.BNDKEY_DATATYPE, long.class.getCanonicalName());
		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_ADD_FRIEND;
		m.replyTo = msnger;
		m.setData(b);
		return m;
	}
	
//	public static Message getDeclineGameMessage(Messenger msnger, long playerID,
//			long matchID, int dataID) {
//		Message m = Message.obtain();
//		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_DECLINE_GAME, "OK_BUDDY", "SORRY_MY_FRIEND");
//		Map<String,String> map = new HashMap<String, String>();
//		map.put("player_id", String.valueOf(playerID));
//		map.put("match_id", String.valueOf(matchID));
//		map.put("data_id", String.valueOf(dataID));
//		b = addParametersToBundle(b, map);
//		b.putString(ECConnectionMessageConstants.BNDKEY_DATATYPE, long.class.getCanonicalName());
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_DECLINE_GAME;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}

//	public static Message getUpdateMatchMessage(Messenger msnger, ECMatch match) {
//		Message m = Message.obtain();
//		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_UPDATEMATCH, "OK_BUDDY", "SORRY_MY_FRIEND");
//		b.putParcelable(ECConnectionMessageConstants.BNDKEY_POST_PARCELABLE, match);
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_UPDATEMATCH;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}
	
//	public static Message getUpdateUserMessage(Messenger msnger, long id, String name, String surname, long dob) {
//		Message m = Message.obtain();
//		Bundle b = createBundle(ECConnectionMessageConstants.FUNCDESCRIPTOR_UPDATEUSER, "OK_BUDDY", "SORRY_MY_FRIEND");
//		Map<String,String> map = new HashMap<String, String>();
//		map.put("id", String.valueOf(id));
//		map.put("name", name);
//		map.put("surname", surname);
//		map.put("yob", String.valueOf(dob));
//		b = addParametersToBundle(b, map);
//		b.putString(ECConnectionMessageConstants.BNDKEY_DATATYPE, long.class.getCanonicalName());
//		m.what = ECConnectionMessageConstants.MSGWHAT_POST;
//		m.arg1 = ECConnectionMessageConstants.MSGTASKDESCRIPTOR_UPDATEUSER;
//		m.replyTo = msnger;
//		m.setData(b);
//		return m;
//	}

	
}
