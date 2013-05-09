package edu.easycalcetto.connection;


public final class ECConnectionMessageConstants{
	//*********	INTENT DATA KEYS
	public static final String INTKEY_MESSENGER = "MESSENGER";
	
	//*********	SERVICE MSG_WHAT
	public static final int MSGWHAT_POST = 1;
	public static final int MSGWHAT_GET = 2;
	
	//********* RETURN FLAGS
	public static final int RES_KIND_SUCCESS = 0;
	public static final int RES_KIND_FAILURE = 1;
	public static final int MSGWHAT_POST_IMAGE = 3;
	
	//********* ERROR FLAGS
	public static final int RES_KIND_UNKNOWN_ERROR = 256;
	public static final int RES_KIND_NETWORK_UNAVALIABLE = 257;
	
	//*********	MESSAGE DATA KEYS
	public static final String BNDKEY_MESSAGE = "MESSAGE";
	public static final String BNDKEY_POST_IMAGE_PHOTOPATH = "PHOTO_PATH";
	public static final String BNDKEY_POST_PARCELABLE = "POST_PARCELABLE";

	public static final int MSGTASKDESCRIPTOR_USER = 0;
	public static final int MSGTASKDESCRIPTOR_REGISTRATON = 1;
	public static final int MSGTASKDESCRIPTOR_GETFRIENDS = 2;
	public static final int MSGTASKDESCRIPTOR_GETMATCHES_OPEN = 3;
	public static final int MSGTASKDESCRIPTOR_CREATEMATCH = 4;
	public static final int MSGTASKDESCRIPTOR_GETMATCH_PARTECIPANTS = 5;
	public static final int MSGTASKDESCRIPTOR_GETMATCHES_CLOSED = 6;
	public static final int MSGTASKDESCRIPTOR_GETACQUAINTANCES = 7;
	public static final int MSGTASKDESCRIPTOR_UPLOAD_PHOTO = 8;
	public static final int MSGTASKDESCRIPTOR_DOWNLOAD_PHOTO = 9;
	public static final int MSGTASKDESCRIPTOR_CONFIRM_REGISTRATION = 10;
	public static final int MSGTASKDESCRIPTOR_CONFIRM_GAME = 11;
	public static final int MSGTASKDESCRIPTOR_DECLINE_GAME = 12;
	public static final int MSGTASKDESCRIPTOR_UPDATEMATCH = 13;
	public static final int MSGTASKDESCRIPTOR_UPDATEUSER = 14;
	public static final int MSGTASKDESCRIPTOR_ADD_FRIEND = 15;
	
	public static final String FUNC = "func";
	public static final String FUNCDESCRIPTOR_GETFRIENDS = "friend_list";
	public static final String FUNCDESCRIPTOR_REGISTRATION = "registration";
	public static final String FUNCDESCRIPTOR_GETMATCHES_OPEN = "open_match";
	public static final String FUNCDESCRIPTOR_GETMATCHES_CLOSED = "closed_match";
	public static final String FUNCDESCRIPTOR_CREATEMATCH = "create_match";
	public static final String FUNCDESCRIPTOR_GETMATCH_PARTECIPANTS = "invitation";
	public static final String FUNCDESCRIPTOR_GETACQUAINTANCES = "other_friends";
	public static final String FUNCDESCRIPTOR_UPLOAD_PHOTO = "add_photo";
	public static final String FUNCDESCRIPTOR_DOWNLOAD_PHOTO = "download_photo";
	public static final String FUNCDESCRIPTOR_CONFIRM_REGISTRATION = "add_user";
	public static final String FUNCDESCRIPTOR_CONFIRM_GAME = "confirm";
	public static final String FUNCDESCRIPTOR_DECLINE_GAME = "decline";
	public static final String FUNCDESCRIPTOR_UPDATE_FRIENDS = "tel_friend";
	public static final String FUNCDESCRIPTOR_UPDATEMATCH = "update_match";
	public static final String FUNCDESCRIPTOR_UPDATEUSER = "update_user";
	public static final String FUNCDESCRIPTOR_ADD_FRIEND = "add_friend";

	public static final String MSGRESDESCTIPTION_SUCCESS = "SUCCESS_FLAG";
	public static final String MSGRESDESCTIPTION_FAILURE = "FAILURE_FLAG";

	public static final String BNDKEY_PARAM_KEYS = "KEYS";

	public static final int RESIND_OPRESULT = 0;
	public static final int RESIND_DATA = 1;

	public static final String BNDKEY_DATATYPE = "DATA_TYPE";

	public static final String BNDKEY_RESULT_ARRAY = "RESULT_ARRAY";

	public static final String BNDKEY_RESULT = "RESULT";

	public static final String BNDKEY_ID = "id";

	

	

	

	

	

	

	
	
}
