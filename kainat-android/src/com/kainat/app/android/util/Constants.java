package com.kainat.app.android.util;

import java.util.Hashtable;

import android.graphics.Bitmap.CompressFormat;

import com.kainat.app.android.R;
import com.kainat.app.android.core.BusinessProxy;

public final class Constants {
	public static final int STAT_COUNT = 15;
	public static final int DB_MSG_COUNT =20;
	// Constanats for UI state
	public static final byte TEXT_TXT = 1;
	public static final byte TEXT_RTF = 2;
	public static final int VOICE_PAYLOAD_BITMAP = 1 << 0;
	public static final int TEXT_PAYLOAD_BITMAP = 1 << 1;
	public static final int PICS_PAYLOAD_BITMAP = 1 << 2;
	public static final int VIDEO_PAYLOAD_BITMAP = 1 << 3;
	public static final int RTML_PAYLOAD_BITMAP = 1 << 9;
	final public static byte UI_STATE_INIT = 1;
	final public static byte UI_STATE_IDLE = 2;
	final public static byte UI_STATE_ALERT = 3;
	final public static byte UI_STATE_LOADING_CONTACT = 4;
	final public static byte UI_STATE_RECORDING = 5;
	final public static byte UI_STATE_RECORDING_FAILED = 6;
	final public static byte UI_STATE_RECORDING_OVERFLOWED = 7;
	final public static byte UI_STATE_SENDING = 8;
	final public static byte UI_STATE_SENDING_DONE = 9;
	final public static byte UI_STATE_SENDING_FAILED = 10;
	final public static byte UI_STATE_NETWORK_BUSY = 11;
	final public static byte UI_STATE_PLAYING = 12;
	final public static byte UI_STATE_PLAYING_FAILED = 13;
	final public static byte UI_STATE_VOICE_RETREIV = 14;
	final public static byte UI_STATE_VOICE_RETREIV_FAILED = 15;
	final public static byte UI_STATE_GET_MESSAGE = 16; // check new
	final public static byte UI_STATE_GET_MESSAGE_FAILED = 17;
	final public static byte UI_STATE_DEL_MESSAGE = 18;
	final public static byte UI_STATE_DEL_ALL_MESSAGE = 19;
	final public static byte UI_STATE_DEL_MESSAGE_FAILED = 20;
	final public static byte UI_STATE_CONFIRM_OP = 21;
	final public static byte UI_STATE_MORE = 22;
	final public static byte UI_STATE_MORE_FAILED = 23;
	final public static byte UI_STATE_DEL_CONTACT = 24;
	final public static byte UI_STATE_DELETED_CONTACT = 25;
	final public static byte UI_STATE_GET_PROFILE = 26;
	final public static byte UI_STATE_STOP_RECORDING = 27;
	final public static byte UI_STATE_STOP_PLAYING = 28;
	final public static byte UI_STATE_LOG_UPLOADING = 29;
	final public static byte UI_STATE_LOGGING_OFF = 30;
	final public static byte UI_STATE_EXITING = 31;
	final public static byte UI_STATE_REGISTERING = 32;
	final public static byte UI_STATE_SEARCHING = 33;
	final public static byte UI_STATE_PICTURE_PREVIEW = 35;
	final public static byte UI_STATE_CHECKING = 36;
	final public static byte UI_STATE_PLAYING_PAUSED = 37;
	final public static byte UI_STATE_BLOCK_USER = 38;
	final public static byte UI_STATE_SMS_CONFORMATION = 39;
	final public static byte UI_STATE_PHONE_VERIFIED = 38;
	final public static byte UI_STATE_VIDEO_RECORDING = 39;
	final public static byte UI_STATE_VIDEO_PLAYING = 40;
	final public static byte UI_STATE_REPORT_SENDER = 41;
	final public static byte UI_STATE_REPORT_MESSAGE = 42;
	final public static byte UI_STATE_PLAY_BUZZ = 43;
	final public static byte UI_STATE_QUICK_TALK = 44;
	final public static byte UI_STATE_CAMERA_OPEN = 45;

	// Confirmatio Message Box Types
	public static final byte CONFIRMATION_ALERT = 1;
	public static final byte CONFIRMATION_INFO = 2;
	public static final byte CONFIRMATION_ERROR = 3;
	public static final byte CONFIRMATION_QUESTION = 4;
	// Colors
	public static final int BORDER_COLOR = 0;// 0x939292;//0x0379A8;
	final public static int KEY_FIRE = -5;// -5
	
	public static final int RTML_BG_COLOR = 0xFFFFFF;
	public static final int LINK_SEL_COLOR = 0xFF9F00;
	public static final int RTML_LINK_SEL_TEXT_COLOR = 0x000000;
	public static final int RTML_TEXT_COLOR = 0x000000;
	public static final int RTML_LINK_UNSEL_TEXT_COLOR = 0x0000ff;
	public static final int SCROLL_PTR_COLOR = 0xffE3E1DF;
	public static final int SCROLL_PTR_FILL_COLOR = 0xff575656;

	public static final byte FRAME_TYPE_VTOV = 1; // outbox
	public static final byte FRAME_TYPE_LOGIN = 2;
	public static final byte FRAME_TYPE_UPDATE_PROFILE = 3;
	public static final byte FRAME_TYPE_GET_PROFILE = 4;
	public static final byte FRAME_TYPE_INBOX_PLAY = 5;
	public static final byte FRAME_TYPE_INBOX_MORE = 6;
	public static final byte FRAME_TYPE_INBOX_DEL = 7; // outbox
	public static final byte FRAME_TYPE_INBOX_DEL_ALL = 8; // outbox
	public static final byte FRAME_TYPE_INBOX_CHECK_NEW = 9;// check new msg
	public static final byte FRAME_TYPE_INBOX_REFRESH = 10; // outbox
	public static final byte FRAME_TYPE_SEARCH_OTS_BUDDY = 11; // outbox
	public static final byte FRAME_TYPE_INBOX_GET_PROFILE = 12;
	public static final byte FRAME_TYPE_VIEW_GROUP_PROFILE = 13;
	public static final byte FRAME_TYPE_COMM_MSG_STATUS = 14;
	public static final byte FRAME_TYPE_PHCONTACTS_INVITE = 15;
	public static final byte FRAME_TYPE_BOOKMARK = 16;

	// public static final byte FRAME_TYPE_DEL_ALL_OTS_BUDDY = 14;
	// public static final byte FRAME_TYPE_GROUP_CREATE_NEW = 15; //GROUP
	// public static final byte FRAME_TYPE_GROUP_DEL = 16; //GROUP
	// public static final byte FRAME_TYPE_GROUP_ADD_MEM = 17; //GROUP
	// public static final byte FRAME_TYPE_GROUP_DEL_MEM = 18; //GROUP
	// public static final byte FRAME_TYPE_GROUP_DETAILS = 19; //GROUP
	// public static final byte FRAME_TYPE_GROUP_UPDATE_MEM_STATUS = 20; //GROUP
	public static final byte FRAME_TYPE_LOGOFF = 21; // LOGOFF
	public static final byte FRAME_TYPE_SIGNUP = 22; // LOGOFF
	public static final byte FRAME_TYPE_CHECK_UPGRADE = 23;
	public static final byte FRAME_TYPE_FORGOT_PASSWORD = 24;
	public static final byte FRAME_TYPE_CHECK_AVAILABILITY = 25;
	public static final byte FRAME_TYPE_GET_IM_SETTING = 29;
	public static final byte FRAME_TYPE_GET_GROUP_DETAILS = 30;
	public static final byte FRAME_TYPE_GET_PREFERENCES = 31;
	public static final byte FRAME_TYPE_GET_ACC_SETTING = 32;
	public static final byte FRAME_TYPE_GET_EXTENDED_PROFILE = 33;
	public static final byte FRAME_TYPE_INBOX_SAVE_VOICE = 34;
	public static final byte FRAME_TYPE_ACTIVATE_CODE = 35;
	public static final byte FRAME_TYPE_CHECK_AVAIL_COMM = 36;
	public static final byte FRAME_TYPE_GET_IM_OFFLINE = 37;
	public static final byte FRAME_TYPE_IM_SIGN_OUT = 38;
	public static final byte FRAME_TYPE_BUDDY_GET_OFFLINE = 39;
	public static final byte FRAME_TYPE_PREVIEW_DEFAULT_BUZZ = 40;

	public static final byte ERROR_NONE = 0;
	public static final byte ERROR_NOT_FOUND = -1;
	public static final byte ERROR_CLIENT_NOT_LOGIN_YET = -2;
	public static final byte ERROR_INVALID_PARAM = -3;
	public static final byte ERROR_OUTQUEUE_FULL = -4;
	public static final byte ERROR_OUTQUEUE_EMPTY = -5;
	public static final byte ERROR_CREATE_FRAME = -6;
	public static final byte ERROR_INVALID_RESPONSE = -7;
	public static final byte ERROR_NET = -8;
	public static final byte ERROR_SYSTEM = -9;
	public static final byte ERROR_INVALID_CODEC = -10;
	public static final byte ERROR_DB = -11;
	public static final byte ERROR_OVERFLOW = -12;
	public static final byte ERROR_RECORD = -13;
	public static final byte ERROR_PLAY = -14;
	public static final byte ERROR_CODEC_NOT_SUPPORTED = -15;
	public static final byte ERROR_BUSY = -16;
	public static final byte ERROR_REQUEST_TOT = -17;
	public static final byte ERROR_DUPLICATE = -18;
	public static final byte ERROR_OUT_OF_MEMORY = -19;
	public static final byte ERROR_RECORD_FULL = -20;
	public static final byte ERROR_PARSE_ERROR = -21;
	public static final byte ERROR_DATABASE_FILE = -22;
	public static final byte ERROR_INIT = -23;
	public static final byte ERROR_NULL_POINTER = -24;
	public static final byte ERROR_SECURITY = -25;
	public static final int END_OF_MEDIA = -26;
	public static final int MEDIA_CLOSED_EVENT = -27;
	public static final int MEDIA_STOPPED_EVENT = -28;
	public static final byte RTML_CONFIRMATION_SCREEN = -111;

	public static final byte MSG_OP_GET = 1; // GET
	public static final byte MSG_OP_SET = 2; // SET
	public static final byte MSG_OP_DEL = 3; // DEL
	public static final byte MSG_OP_NEWMSG = 4; // CHKNEW
	public static final byte MSG_OP_INBOX_MORE = 5; // MORE
	public static final byte MSG_OP_FWD = 6; // FWD
	public static final byte MSG_OP_VOICE_NEW = 7; // Voice New Call
	public static final byte MSG_OP_REPLY = 8;
	public static final byte MSG_OP_DEL_ALL = 9; // DELETE ALL INBOX MESSAGES
	public static final byte MSG_OP_SEARCH_USER = 10;
	// public static final int MSG_OP_SEARCH_MORE_USER = 11;
	// public static final int MSG_OP_SEARCH_RECV_MSG = 12;
	// public static final int MSG_OP_SEARCH_MORE_RECV_MSG = 13;
	// public static final int MSG_OP_SEARCH_SENT_MSG = 14;
	// public static final int MSG_OP_SEARCH_MORE_SENT_MSG = 15;
	// public static final int MSG_OP_ADD_BUDDY = 16;
	// public static final int MSG_OP_MORE_BUDDY = 19;
	public static final byte MSG_OP_CHECK_UPGRADE = 20;
	// public static final int MSG_OP_GROUP_CREATE_NEW = 21; //GROUP
	// public static final int MSG_OP_GROUP_DEL = 22; //GROUP
	// public static final int MSG_OP_GROUP_ADD_MEM = 23; //GROUP
	// public static final int MSG_OP_GROUP_DEL_MEM = 24; //GROUP
	// public static final int MSG_OP_GROUP_UPDATE_MEM_STATUS = 26; //GROUP
	public static final byte MSG_OP_SEARCH_CONTACTS = 21;
	// --------------------------------------------------------------------------
	// Registration Ops
	// --------------------------------------------------------------------------
	public static final byte MSG_OP_REG_LOGIN = 1;
	public static final byte MSG_OP_REG_GET_BASIC_PROFILE = 2;
	public static final byte MSG_OP_REG_UPDATE_PROFILE = 3;
	public static final byte MSG_OP_REG_LOGOFF = 4;
	public static final byte MSG_OP_REG_SIGNUP = 7;
	public static final byte MSG_OP_REG_FORGOT_PASSWORD = 8;
	public static final byte MSG_OP_REG_USERID_AVAILABILITY = 9;
	public static final byte MSG_OP_REG_GET_EXTENDED_PROF = 10;
	public static final byte MSG_OP_REG_GET_ACC_SETTING = 11;
	public static final byte MSG_OP_REG_GET_PREF = 12;
	public static final byte MSG_OP_GUEST_LOGIN = 13;
	public static final byte MSG_OP_GROUP_DETAILS = 25; // GROUP
	public static final byte MSG_OP_GET_IM_SETTING = 29;
	public static final byte MSG_OP_GET_GROUP_DETAIL = 30; // for
	public static final byte MSG_OP_MULTIPLE_DELETE = 31;
	public static final byte MSG_OP_GET_PROFILE = 32; // GROUP
	public static final byte MSG_OP_GET_MEDIA = 34; // MEDIA
	public static final byte MSG_OP_GET_FRNDS_COMMUNITIES = 35; // GROUP
	public static final byte MSG_OP_GROUP_PROFILE = 39;

	public static final byte MES_OP_IM_GET_OFFLINE_BUDDIES = 45;
	public static final byte MES_OP_IM_SIGN_OUT = 47;
	public static final byte MSG_OP_REG_USER_ACTIVATION = 49;
	public static final byte MSG_OP_PREVIEW_DEFAULT_BUZZ = 52;
	public static final byte MSG_OP_GET_NOTIFICATIONS = 56;

	public static final byte RES_TYPE_SUCCESS = 1;
	public static final byte RES_TYPE_ERROR = 2;
	public static final byte RES_TYPE_CONGESTION = 3;
	public static final byte RES_TYPE_INVALIDUSER = 4;
	public static final byte RES_TYPE_DEST_NOT_SUPPORTED = 5;
	public static final byte RES_TYPE_NO_VOICE = 6;
	public static final byte RES_TYPE_SOFT_UPGRADE = 7;
	public static final byte RES_TYPE_HARD_UPGRADE = 8;
	public static final byte RES_TYPE_USER_ALREADY_EXISTS = 9;
	public static final byte RES_TYPE_USER_ID_AVAILABLE = 10;
	public static final byte RES_TYPE_ACTIVATION_NOT_DONE = 11;
	public static final byte RES_TYPE_USER_FAILED_FOR_PASSWORD = 13;
	public static final byte PHONE_VERFICATION_SUCCESS = 14;
	public static final byte PHONE_VERFICATION_FAIL = 15;
	public static final byte RES_TYPE_COMM_AVAILABLE = 16;
	public static final byte RES_TYPE_COMM_ALREADY_EXISTS = 17;
	public static final byte RES_TYPE_BAD_NAME = 19;
	public static final byte RES_TYPE_SOFT_UPGRADE_WITH_VERIFICATION = 21;
	public static final byte RES_TYPE_MULTIPLE_ACCOUNT = 22;

	public static final String ENC = "UTF-8";
	public static final int MAX_CONTACT_LIST_SIZE = 5000;
	public static final byte INBOX_MAX_REC_SIZE = 50;
	public static final byte INBOX_MAX_PAYLOAD_STORE = 50;
	public static final byte INBOX_MAX_PAYLOAD_COUNT = 5;
	public static final byte OUTBOX_MAX_REC_SIZE = 20;
	public static final byte SENT_MAX_REC_SIZE = 20;
	public static final byte HISTORY_MAX_REC_SIZE = 15;
	public static final byte LOGIN_HISTORY_MAX_REC_SIZE = 5;
	public static final byte MAX_PICTURE_FILE_ATTACHEMENT = 12;
	public static final byte MAX_PICTURE_FILE_ATTACH_PROFILE = 5;

	public static final String CAMERA_FILE_END = "---.jpg";
	public static final String VIDEO_FILE_END = ".3gp";
	public static final int MAX_IMAGE_SIZE = 5;
	final public static char EQUAL = '=';
	final public static char FIELD_SEPARATOR = ',';

	public static final byte EXECUTE_VOICE = 1;
	public static final int IM_REFRESH_TIME = 15;// 5;//check this for this
													// time.

	public static final byte MSG_STATUS_UNREAD = 1 << 0;
	public static final byte MSG_STATUS_REPLY = 1 << 1;
	public static final byte MSG_STATUS_FORWARD = 1 << 2;

	public static final int FEATURE_USER = 1 << 0;
	public static final int MYPAGE = 1 << 1;
	public static final int MEDIA = 1 << 2;
	public static final int FUNSTUFF = 1 << 3;
	public static final int INBOX = 1 << 4;
	public static final int MESSENGER = 1 << 5;
	public static final int CHAT = 1 << 6;

	public static int CHAT_SERVER_VAL = 1;
	public final static String[] AGE_RANGE_LIST = { "18-24", "25-34", "35-50",
			"Above 50" };
	
	public static final String LAST_VISITED_GROUPID = "lastVistedGroupId";

	// public final static String[] COUNTRY_LIST = { "United States",
	// "United Kingdom", "France", "Germany", "Italy", "India", "",
	// "Afghanistan", "Aland Islands", "Albania", "Algeria", "American Samoa",
	// "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and/or Barbuda",
	// "Argentina", "Armenia", "Aruba", "Ascension Island", "Australia",
	// "Austria", "Azerbaijan", "Bahamas", "Bahrain",
	// "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin",
	// "Bermuda", "Bhutan", "Bolivia", "Bonaire, Saint Eustatius And Saba",
	// "Bosnia and Herzegovina", "Botswana",
	// "Bouvet Island", "Brazil", "British lndian Ocean Territory",
	// "British Virgin Islands", "Brunei Darussalam", "Bulgaria",
	// "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada",
	// "Cape Verde", "Cayman Islands", "Central African Republic", "Chad",
	// "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands",
	// "Colombia", "Comoros", "Congo", "Cook Islands",
	// "Costa Rica", "Croatia (Hrvatska)", "Cuba", "Curacao", "Cyprus",
	// "Czech Republic", "Czechoslovakia (former)",
	// "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica",
	// "Dominican Republic", "East Timor", "Ecudaor", "Egypt", "El Salvador",
	// "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia",
	// "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji",
	// "Finland", "France, Metropolitan", "French Guiana", "French Polynesia",
	// "French Southern Territories", "Gabon", "Gambia", "Gaza Strip",
	// "Georgia", "Ghana", "Gibraltar",
	// "Great Britain (UK)", "Greece", "Greenland", "Grenada", "Guadeloupe",
	// "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea-Bissau", "Guyana",
	// "Haiti", "Heard and Mc Donald Islands",
	// "Honduras", "Hong Kong", "Hungary", "Iceland", "Indonesia",
	// "Iran (Islamic Republic of)", "Iraq", "Ireland", "Isle of Man", "Israel",
	// "Ivory Coast", "Jamaica", "Japan", "Jersey",
	// "Jordan", "Kazakhstan", "Kenya", "Kiribati",
	// "Korea, Democratic People's Republic of", "Korea, Republic of", "Kosovo",
	// "Kuwait", "Kyrgyzstan", "Lao People's Democratic Republic",
	// "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya",
	// "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia",
	// "Madagascar", "Malawi", "Malaysia", "Maldives",
	// "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania",
	// "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of",
	// "Moldova, Republic of", "Monaco", "Mongolia",
	// "Montenegro", "Montserrat", "Morocco", "Mozambique", "Myanmar",
	// "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles",
	// "New Caledonia", "New Zealand", "Nicaragua", "Niger",
	// "Nigeria", "Niue", "Norfork Island", "Northern Mariana Islands",
	// "Norway", "Oman", "Pakistan", "Palau", "Palestinian Territory, Occupied",
	// "Panama", "Papua New Guinea", "Paraguay",
	// "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico",
	// "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda",
	// "Saint Barthelemy", "Saint Kitts and Nevis",
	// "Saint Lucia", "Saint Martin (Dutch Part)", "Saint Martin (French Part)",
	// "Saint Vincent and the Grenadines", "Samoa", "San Marino",
	// "Sao Tome and Principe", "Saudi Arabia", "Senegal",
	// "Serbia and Montenegro (former)", "Serbia", "Seychelles", "Sierra Leone",
	// "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia",
	// "South Africa",
	// "South Georgia South Sandwich Islands", "South Sudan", "Spain",
	// "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan",
	// "Suriname", "Svalbarn and Jan Mayen Islands", "Swaziland",
	// "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan", "Tajikistan",
	// "Tanzania, United Republic of", "Thailand", "Timor-Leste", "Togo",
	// "Tokelau", "Tonga", "Trinidad and Tobago",
	// "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands",
	// "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "Uruguay",
	// "US Minor Outlying Island", "Uzbekistan", "Vanuatu",
	// "Vatican City State", "Venezuela", "Vietnam", "Virgin Islands (U.S.)",
	// "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Zaire",
	// "Zambia", "Zimbabwe" };

	public final static String[] COUNTRY_LIST = { "United States",
			"United Kingdom", "France", "Germany", "India", "Italy", "Canada",
			"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
			"Angola", "Anguilla", "Antarctica", "Antigua and/or Barbuda",
			"Argentina", "Armenia", "Aruba", "Australia", "Austria",
			"Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados",
			"Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan",
			"Bolivia", "Bosnia and Herzegovina", "Botswana", "Bouvet Island",
			"Brazil", "British lndian Ocean Territory",
			"British Virgin Islands", "Brunei Darussalam", "Bulgaria",
			"Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Cape Verde",
			"Cayman Islands", "Central African Republic", "Chad", "Chile",
			"China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia",
			"Comoros", "Congo", "Cook Islands", "Costa Rica",
			"Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic",
			"Democratic Republic of the Congo", "Denmark", "Djibouti",
			"Dominica", "Dominican Republic", "East Timor", "Ecudaor", "Egypt",
			"El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",
			"Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji",
			"Finland", "French Guiana", "French Polynesia",
			"French Southern Territories", "Gabon", "Gambia", "Gaza Strip",
			"Georgia", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada",
			"Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau",
			"Guyana", "Haiti", "Heard and Mc Donald Islands", "Honduras",
			"Hong Kong", "Hungary", "Iceland", "Indonesia",
			"Iran (Islamic Republic of)", "Iraq", "Ireland", "Isle of Man",
			"Israel", "Ivory Coast", "Jamaica", "Japan", "Jersey", "Jordan",
			"Kazakhstan", "Kenya", "Kiribati",
			"Korea, Democratic People's Republic of", "Korea, Republic of",
			"Kosovo", "Kuwait", "Kyrgyzstan",
			"Lao People's Democratic Republic", "Latvia", "Lebanon", "Lesotho",
			"Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania",
			"Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi",
			"Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
			"Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico",
			"Micronesia, Federated States of", "Moldova, Republic of",
			"Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco",
			"Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal",
			"Netherlands", "Netherlands Antilles", "New Caledonia",
			"New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue",
			"Norfork Island", "Northern Mariana Islands", "Norway", "Oman",
			"Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay",
			"Peru", "Philippines", "Pitcairn", "Poland", "Portugal",
			"Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation",
			"Rwanda", "Saint Barthelemy", "Saint Kitts and Nevis",
			"Saint Lucia", "Saint Martin", "Saint Vincent and the Grenadines",
			"Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia",
			"Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore",
			"Slovakia", "Slovenia", "Solomon Islands", "Somalia",
			"South Africa", "South Georgia South Sandwich Islands", "Spain",
			"Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan",
			"Suriname", "Svalbarn and Jan Mayen Islands", "Swaziland",
			"Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan",
			"Tajikistan", "Tanzania, United Republic of", "Thailand",
			"Timor-Leste", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago",
			"Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands",
			"Tuvalu", "Uganda", "Ukraine", "United Arab Emirates",
			"United States minor outlying islands", "Uruguay", "Uzbekistan",
			"Vanuatu", "Vatican City State", "Venezuela", "Vietnam",
			"Virigan Islands (British)", "Virgin Islands (U.S.)",
			"Wallis and Futuna Islands", "Western Sahara", "Yemen", "Zaire",
			"Zambia", "Zimbabwe" };

	// public final static String[] COUNTRY_LIST1 = { "United States",
	// "United Kingdom", "France", "Germany", "Italy", "India", "Afghanistan",
	// "Aland Islands", "Albania", "Algeria", "American Samoa",
	// "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and/or Barbuda",
	// "Argentina", "Armenia", "Aruba", "Ascension Island", "Australia",
	// "Austria", "Azerbaijan", "Bahamas", "Bahrain",
	// "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin",
	// "Bermuda", "Bhutan", "Bolivia", "Bonaire, Saint Eustatius And Saba",
	// "Bosnia and Herzegovina", "Botswana",
	// "Bouvet Island", "Brazil", "British lndian Ocean Territory",
	// "British Virgin Islands", "Brunei Darussalam", "Bulgaria",
	// "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada",
	// "Cape Verde", "Cayman Islands", "Central African Republic", "Chad",
	// "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands",
	// "Colombia", "Comoros", "Congo", "Cook Islands",
	// "Costa Rica", "Croatia (Hrvatska)", "Cuba", "Curacao", "Cyprus",
	// "Czech Republic", "Czechoslovakia (former)",
	// "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica",
	// "Dominican Republic", "East Timor", "Ecudaor", "Egypt", "El Salvador",
	// "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia",
	// "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji",
	// "Finland", "France, Metropolitan", "French Guiana", "French Polynesia",
	// "French Southern Territories", "Gabon", "Gambia", "Gaza Strip",
	// "Georgia", "Ghana", "Gibraltar",
	// "Great Britain (UK)", "Greece", "Greenland", "Grenada", "Guadeloupe",
	// "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea-Bissau", "Guyana",
	// "Haiti", "Heard and Mc Donald Islands",
	// "Honduras", "Hong Kong", "Hungary", "Iceland", "Indonesia",
	// "Iran (Islamic Republic of)", "Iraq", "Ireland", "Isle of Man", "Israel",
	// "Ivory Coast", "Jamaica", "Japan", "Jersey",
	// "Jordan", "Kazakhstan", "Kenya", "Kiribati",
	// "Korea, Democratic People's Republic of", "Korea, Republic of", "Kosovo",
	// "Kuwait", "Kyrgyzstan", "Lao People's Democratic Republic",
	// "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya",
	// "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia",
	// "Madagascar", "Malawi", "Malaysia", "Maldives",
	// "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania",
	// "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of",
	// "Moldova, Republic of", "Monaco", "Mongolia",
	// "Montenegro", "Montserrat", "Morocco", "Mozambique", "Myanmar",
	// "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles",
	// "New Caledonia", "New Zealand", "Nicaragua", "Niger",
	// "Nigeria", "Niue", "Norfork Island", "Northern Mariana Islands",
	// "Norway", "Oman", "Pakistan", "Palau", "Palestinian Territory, Occupied",
	// "Panama", "Papua New Guinea", "Paraguay",
	// "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico",
	// "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda",
	// "Saint Barthelemy", "Saint Kitts and Nevis",
	// "Saint Lucia", "Saint Martin (Dutch Part)", "Saint Martin (French Part)",
	// "Saint Vincent and the Grenadines", "Samoa", "San Marino",
	// "Sao Tome and Principe", "Saudi Arabia", "Senegal",
	// "Serbia and Montenegro (former)", "Serbia", "Seychelles", "Sierra Leone",
	// "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia",
	// "South Africa",
	// "South Georgia South Sandwich Islands", "South Sudan", "Spain",
	// "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan",
	// "Suriname", "Svalbarn and Jan Mayen Islands", "Swaziland",
	// "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan", "Tajikistan",
	// "Tanzania, United Republic of", "Thailand", "Timor-Leste", "Togo",
	// "Tokelau", "Tonga", "Trinidad and Tobago",
	// "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands",
	// "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "Uruguay",
	// "US Minor Outlying Island", "Uzbekistan", "Vanuatu",
	// "Vatican City State", "Venezuela", "Vietnam", "Virgin Islands (U.S.)",
	// "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Zaire",
	// "Zambia", "Zimbabwe" };

	public final static String[] COUNTRY_LIST1 = { "United States",
			"United Kingdom", "France", "Germany", "India", "Italy", "Canada",
			"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
			"Angola", "Anguilla", "Antarctica", "Antigua and/or Barbuda",
			"Argentina", "Armenia", "Aruba", "Australia", "Austria",
			"Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados",
			"Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan",
			"Bolivia", "Bosnia and Herzegovina", "Botswana", "Bouvet Island",
			"Brazil", "British lndian Ocean Territory",
			"British Virgin Islands", "Brunei Darussalam", "Bulgaria",
			"Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Cape Verde",
			"Cayman Islands", "Central African Republic", "Chad", "Chile",
			"China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia",
			"Comoros", "Congo", "Cook Islands", "Costa Rica",
			"Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic",
			"Democratic Republic of the Congo", "Denmark", "Djibouti",
			"Dominica", "Dominican Republic", "East Timor", "Ecudaor", "Egypt",
			"El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",
			"Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji",
			"Finland", "French Guiana", "French Polynesia",
			"French Southern Territories", "Gabon", "Gambia", "Gaza Strip",
			"Georgia", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada",
			"Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau",
			"Guyana", "Haiti", "Heard and Mc Donald Islands", "Honduras",
			"Hong Kong", "Hungary", "Iceland", "Indonesia",
			"Iran (Islamic Republic of)", "Iraq", "Ireland", "Isle of Man",
			"Israel", "Ivory Coast", "Jamaica", "Japan", "Jersey", "Jordan",
			"Kazakhstan", "Kenya", "Kiribati",
			"Korea, Democratic People's Republic of", "Korea, Republic of",
			"Kosovo", "Kuwait", "Kyrgyzstan",
			"Lao People's Democratic Republic", "Latvia", "Lebanon", "Lesotho",
			"Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania",
			"Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi",
			"Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
			"Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico",
			"Micronesia, Federated States of", "Moldova, Republic of",
			"Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco",
			"Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal",
			"Netherlands", "Netherlands Antilles", "New Caledonia",
			"New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue",
			"Norfork Island", "Northern Mariana Islands", "Norway", "Oman",
			"Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay",
			"Peru", "Philippines", "Pitcairn", "Poland", "Portugal",
			"Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation",
			"Rwanda", "Saint Barthelemy", "Saint Kitts and Nevis",
			"Saint Lucia", "Saint Martin", "Saint Vincent and the Grenadines",
			"Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia",
			"Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore",
			"Slovakia", "Slovenia", "Solomon Islands", "Somalia",
			"South Africa", "South Georgia South Sandwich Islands", "Spain",
			"Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan",
			"Suriname", "Svalbarn and Jan Mayen Islands", "Swaziland",
			"Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan",
			"Tajikistan", "Tanzania, United Republic of", "Thailand",
			"Timor-Leste", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago",
			"Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands",
			"Tuvalu", "Uganda", "Ukraine", "United Arab Emirates",
			"United States minor outlying islands", "Uruguay", "Uzbekistan",
			"Vanuatu", "Vatican City State", "Venezuela", "Vietnam",
			"Virigan Islands (British)", "Virgin Islands (U.S.)",
			"Wallis and Futuna Islands", "Western Sahara", "Yemen", "Zaire",
			"Zambia", "Zimbabwe" };

	// public final static String[] COUNTRY_CODE = { "+1", "+44", "+33", "+49",
	// "+39", "+91", "", "+93", "+358", "+355", "+213", "+1684", "+376", "+244",
	// "+1264", "+672", "+1268", "+54", "+374", "+297",
	// "+247", "+61", "+43", "+994", "+1242", "+973", "+880", "+1246", "+375",
	// "+32", "+501", "+229", "+1441", "+975", "+591", "+599", "+387", "+267",
	// "+55", "+55", "+246", "+1284", "+673",
	// "+359", "+226", "+257", "+855", "+237", "+1", "+238", "+1345", "+236",
	// "+235", "+56", "+86", "+61", "+61", "+57", "+269", "+242", "+682",
	// "+506", "+385", "+53", "+599", "+357", "+420",
	// "+42", "+243", "+45", "+253", "+1767", "+1809", "+670", "+593", "+20",
	// "+503", "+240", "+291", "+372", "+251", "+500", "+298", "+679", "+358",
	// "+33", "+594", "+689", "+33", "+241",
	// "+220", "+970", "+995", "+233", "+350", "+44", "+30", "+299", "+1473",
	// "+590", "+1671", "+502", "+44", "+224", "+245", "+592", "+509", "+61",
	// "+504", "+852", "+36", "+354", "+62", "+98",
	// "+964", "+353", "+44", "+972", "+225", "+1876", "+81", "+44", "+962",
	// "+7", "+254", "+686", "+850", "+82", "+381", "+965", "+996", "+856",
	// "+371", "+961", "+266", "+231", "+218", "+423",
	// "+370", "+352", "+853", "+389", "+261", "+265", "+60", "+960", "+223",
	// "+356", "+692", "+596", "+222", "+230", "+262", "+52", "+691", "+373",
	// "+377", "+976", "+382", "+1664", "+212",
	// "+258", "+95", "+264", "+674", "+977", "+31", "+599", "+687", "+64",
	// "+505", "+227", "+234", "+683", "+672", "+1670", "+47", "+968", "+92",
	// "+680", "+970", "+507", "+675", "+595", "+51",
	// "+63", "+870", "+48", "+351", "+1", "+974", "+262", "+40", "+7", "+250",
	// "+590", "+1869", "+1758", "+1599", "+1599", "+1784", "+685", "+378",
	// "+239", "+966", "+221", "+381", "+381",
	// "+248", "+232", "+65", "+421", "+386", "+677", "+252", "+27", "+500",
	// "+211", "+34", "+94", "+290", "+508", "+249", "+597", "+47", "+268",
	// "+46", "+41", "+963", "+886", "+992", "+255",
	// "+66", "+670", "+228", "+690", "+676", "+1868", "+216", "+90", "+993",
	// "+1649", "+688", "+256", "+380", "+971", "+598", "+1", "+998", "+678",
	// "+39", "+58", "+84", "+1340", "+681", "+212",
	// "+967", "+243", "+260", "+263" };

	public final static String[] COUNTRY_CODE = { "+1", "+44", "+33", "+49", "+91",
			"+39", "+1", "+93", "+355", "+213", "+1684", "+376", "+244", "+1264", "+672",
			"+1268", "+54", "+374", "+297", "+61", "+43", "+994", "+1242", "+973",
			"+880", "+1246", "+375", "+32", "+501", "+229", "+1441", "+975", "+591",
			"+387", "+267", "+47", "+55", "+246", "+1284", "+673", "+359", "+226",
			"+257", "+855", "+237", "+238", "+1345", "+236", "+235", "+56", "+86", "+61",
			"+61", "+57", "+269", "+242", "+682", "+506", "+385", "+53", "+357", "+420",
			"+243", "+45", "+253", "+1767", "+1809", "+670", "+593", "+20", "+503",
			"+240", "+291", "+372", "+251", "+500", "+298", "+679", "+358", "+594",
			"+689", "+33", "+241", "+220", "+970", "+995", "+233", "+350", "+30", "+299",
			"+1473", "+590", "+1671", "+502", "+224", "+245", "+592", "+509", "+11",
			"+504", "+852", "+36", "+354", "+62", "+98", "+964", "+353", "+44", "+972",
			"+225", "+1876", "+81", "+44", "+962", "+7", "+254", "+686", "+850", "+82",
			"+381", "+965", "+996", "+856", "+371", "+961", "+266", "+233", "+218",
			"+423", "+370", "+352", "+853", "+389", "+261", "+265", "+60", "+960",
			"+223", "+356", "+692", "+596", "+222", "+230", "+262", "+52", "+691",
			"+373", "+377", "+976", "+382", "+1664", "+212", "+258", "+95", "+264",
			"+674", "+977", "+31", "+599", "+687", "+64", "+505", "+227", "+234", "+683",
			"+672", "+1670", "+47", "+968", "+92", "+680", "+507", "+675", "+595", "+51",
			"+63", "+870", "+48", "+351", "+1", "+974", "+262", "+40", "+7", "+250",
			"+590", "+1869", "+1758", "+1599", "+1784", "+685", "+378", "+239", "+966",
			"+221", "+381", "+248", "+232", "+65", "+421", "+386", "+677", "+252", "+27",
			"+500", "+34", "+94", "+290", "+508", "+249", "+597", "+47", "+268", "+46",
			"+41", "+963", "+886", "+992", "+255", "+66", "+670", "+228", "+690", "+676",
			"+1868", "+216", "+90", "+993", "+1649", "+668", "+256", "+380", "+971",
			"+1", "+598", "+998", "+678", "+39", "+58", "+84", "+1284", "+1340", "+681",
			"+212", "+967", "+243", "+260", "+263" };

	// public final static String[] COUNTRY_SHORT_NAME = { "US", "UK", "FR",
	// "DE", "IT", "IN", "", "AF", "AX", "AL", "DZ", "AS", "AD", "AO", "AI",
	// "AQ", "AG", "AR", "AM", "AW", "AC", "AU", "AT", "AZ",
	// "BS", "BH", "BD", "BB", "BY", "BE", "BZ", "BJ", "BM", "BT", "BO", "BQ",
	// "BA", "BW", "BV", "BR", "IO", "VG", "BN", "BG", "BF", "BI", "KH", "CM",
	// "CA", "CV", "KY", "CF", "TD", "CL", "CN",
	// "CX", "CC", "CO", "KM", "CG", "CK", "CR", "HR", "CU", "CW", "CY", "CZ",
	// "CS", "CD", "DK", "DJ", "DM", "DO", "TP", "EC", "EG", "SV", "GQ", "ER",
	// "EE", "ET", "FK", "FO", "FJ", "FI", "FX",
	// "GF", "PF", "TF", "GA", "GM", "GZ", "GE", "GH", "GI", "GB", "GR", "GL",
	// "GD", "GP", "GU", "GT", "GG", "GN", "GW", "GY", "HT", "HM", "HN", "HK",
	// "HU", "IS", "ID", "IR", "IQ", "IE", "IM",
	// "IL", "CI", "JM", "JP", "JE", "JO", "KZ", "KE", "KI", "KP", "KR", "XK",
	// "KW", "KG", "LA", "LV", "LB", "LS", "LR", "LY", "LI", "LT", "LU", "MO",
	// "MK", "MG", "MW", "MY", "MV", "ML", "MT",
	// "MH", "MQ", "MR", "MU", "YT", "MX", "FM", "MD", "MC", "MN", "ME", "MS",
	// "MA", "MZ", "MM", "NA", "NR", "NP", "NL", "AN", "NC", "NZ", "NI", "NE",
	// "NG", "NU", "NF", "MP", "NO", "OM", "PK",
	// "PW", "PS", "PA", "PG", "PY", "PE", "PH", "PN", "PL", "PT", "PR", "QA",
	// "RE", "RO", "RU", "RW", "BL", "KN", "LC", "SX", "MF", "VC", "WS", "SM",
	// "ST", "SA", "SN", "YU", "RS", "SC", "SL",
	// "SG", "SK", "SI", "SB", "SO", "ZA", "GS", "SS", "ES", "LK", "SH", "PM",
	// "SD", "SR", "SJ", "SZ", "SE", "CH", "SY", "TW", "TJ", "TZ", "TH", "TL",
	// "TG", "TK", "TO", "TT", "TN", "TR", "TM",
	// "TC", "TV", "UG", "UA", "AE", "UY", "UM", "UZ", "VU", "VA", "VE", "VN",
	// "VI", "WF", "EH", "YE", "ZR", "ZM", "ZW" };

	public final static String[] COUNTRY_SHORT_NAME = { "US", "GB", "FR", "DE",
			"IN", "IT", "CA", "AF", "AL", "DZ", "DS", "AD", "AO", "AI", "AQ",
			"AG", "AR", "AM", "AW", "AU", "AT", "AZ", "BS", "BH", "BD", "BB",
			"BY", "BE", "BZ", "BJ", "BM", "BT", "BO", "BA", "BW", "BV", "BR",
			"IO", "VG", "BN", "BG", "BF", "BI", "KH", "CM", "CV", "KY", "CF",
			"TD", "CL", "CN", "CX", "CC", "CO", "KM", "CG", "CK", "CR", "HR",
			"CU", "CY", "CZ", "CD", "DK", "DJ", "DM", "DO", "TP", "EC", "EG",
			"SV", "GQ", "ER", "EE", "ET", "FK", "FO", "FJ", "FI", "GF", "PF",
			"TF", "GA", "GM", "PS", "GE", "GH", "GI", "GR", "GL", "GD", "GP",
			"GU", "GT", "GN", "GW", "GY", "HT", "HM", "HN", "HK", "HU", "IS",
			"ID", "IR", "IQ", "IE", "IM", "IL", "CI", "JM", "JP", "JE", "JO",
			"KZ", "KE", "KI", "KP", "KR", "XK", "KW", "KG", "LA", "LV", "LB",
			"LS", "LR", "LY", "LI", "LT", "LU", "MO", "MK", "MG", "MW", "MY",
			"MV", "ML", "MT", "MH", "MQ", "MR", "MU", "TY", "MX", "FM", "MD",
			"MC", "MN", "ME", "MS", "MA", "MZ", "MM", "NA", "NR", "NP", "NL",
			"AN", "NC", "NZ", "NI", "NE", "NG", "NU", "NF", "MP", "NO", "OM",
			"PK", "PW", "PA", "PG", "PY", "PE", "PH", "PN", "PL", "PT", "PR",
			"QA", "RE", "RO", "RU", "RW", "BL", "KN", "LC", "MF", "VC", "WS",
			"SM", "ST", "SA", "SN", "RS", "SC", "SL", "SG", "SK", "SI", "SB",
			"SO", "ZA", "GS", "ES", "LK", "SH", "PM", "SD", "SR", "SJ", "SZ",
			"SE", "CH", "SY", "TW", "TJ", "TZ", "TH", "TL", "TG", "TK", "TO",
			"TT", "TN", "TR", "TM", "TC", "TV", "UG", "UA", "AE", "UM", "UY",
			"UZ", "VU", "VA", "VE", "VN", "VG", "VI", "WF", "EH", "YE", "ZR",
			"ZM", "ZW" };

	public final static String[] STATE_NAME = { "Andhra Pradesh",
			"Andaman and Nicobar Islands", "Assam", "Arunachal Pradesh",
			"Bihar", "Chhattisgarh", "Chandigarh", "Dadra and Nagar Haveli",
			"Daman and Diu", "Delhi", "Goa", "Gujarat", "Haryana",
			"Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka",
			"Kerala", "Lakshadweep", "Madhya Pradesh", "Manipur", "Meghalaya",
			"Mizoram", "Maharastra", "Nagaland", "Orissa", "Punjab",
			"Pondicherry", "Rajasthan", "Sikkim", "Tripura", "Tamil Nadu",
			"Uttar Pradesh", "Uttaranchal", "West Bengal" };

	public final static String[] LANGUAGE = {

	"Spanish", "English", "Hindi", "Arabic", "Bengali", "Javanese", "French",
			"Abkhaz", "Afar", "Afrikaans", "Akan", "Albanian", "Amharic",
			"Arabic", "Aragonese", "Armenian", "Assamese", "Avaric", "Avestan",
			"Aymara", "Azerbaijani", "Bambara", "Bashkir", "Basque",
			"Belarusian", "Bengali; Bangla", "Bhojpuri", "Bihari", "Bislama",
			"Bosnian", "Breton", "Bulgarian", "Burmese", "Catalan; Valencian",
			"Chamorro", "Chechen", "Chichewa; Chewa; Nyanja", "Chinese",
			"Chuvash", "Cornish", "Corsican", "Cree", "Croatian", "Czech",
			"Danish", "Divehi; Dhivehi; Maldivian;", "Dutch", "Dzongkha",
			"English", "Esperanto", "Estonian", "Ewe", "Faroese", "Fijian",
			"Finnish", "French", "Fula; Fulah; Pulaar; Pular", "Galician",
			"Ganda", "Georgian", "German", "Greek, Modern", "Guarani",
			"Gujarati", "Haitian; Haitian Creole", "Hausa", "Hebrew (modern)",
			"Herero", "Hindi", "Hiri Motu", "Hungarian", "Icelandic", "Ido",
			"Igbo", "Indonesian", "Interlingua", "Interlingue", "Inuktitut",
			"Inupiaq", "Irish", "Italian", "Japanese", "Javanese",
			"Kalaallisut, Greenlandic", "Kannada", "Kanuri", "Kashmiri",
			"Kazakh", "Khmer", "Kikuyu, Gikuyu", "Kinyarwanda", "Kirundi",
			"Komi", "Kongo", "Korean", "Kurdish", "Kwanyama, Kuanyama",
			"Kyrgyz", "Lao", "Latin", "Latvian",
			"Limburgish, Limburgan, Limburger", "Lingala", "Lithuanian",
			"Luba-Katanga", "Luxembourgish, Letzeburgesch", "Macedonian",
			"Malagasy", "Malay", "Malayalam", "Maltese", "Manx", "Maori",
			"Marathi", "Marshallese", "Mongolian", "Nauru", "Navajo, Navaho",
			"Ndonga", "Nepali", "North Ndebele", "Northern Sami",
			"Norwegian Bokmal", "Norwegian Nynorsk", "Norwegian", "Nuosu",
			"Occitan", "Ojibwe, Ojibwa",
			"Old Church Slavonic, Church Slavic, Church Sl", "Oriya", "Oromo",
			"Ossetian, Ossetic", "Pali", "Panjabi, Punjabi", "Pashto, Pushto",
			"Persian", "Polish", "Portuguese", "Quechua",
			"Romanian, Moldavian(Romanian from Republic of", "Romansh",
			"Russian", "Samoan", "Sango", "Sanskrit", "Sardinian",
			"Scottish Gaelic; Gaelic", "Serbian", "Shona", "Sindhi",
			"Sinhala, Sinhalese", "Slovak", "Slovene", "Somali",
			"South Ndebele", "Southern Sotho", "Spanish; Castilian",
			"Sundanese", "Swahili", "Swati", "Swedish", "Tagalog", "Tahitian",
			"Tajik", "Tamil", "Tatar", "Telugu", "Thai",
			"Tibetan Standard, Tibetan, Central", "Tigrinya",
			"Tonga (Tonga Islands)", "Tsonga", "Tswana", "Turkish", "Turkmen",
			"Twi", "Uighur, Uyghur", "Ukrainian", "Urdu", "Uzbek", "Venda",
			"Vietnamese", "Volapuk", "Walloon", "Welsh", "Western Frisian",
			"Wolof", "Xhosa", "Yiddish", "Yoruba", "Zhuang, Chuang", "Zulu" };

	public static final int MAX_AUDIO_RECORD_TIME = 300;
	// public static final int MAX_AUDIO_RECORD_TIME_COMPOSE = 120;
	// public static final int MAX_AUDIO_RECORD_TIME_POST = 600;
	public static final int MAX_AUDIO_RECORD_TIME_BUZZ = 30;

	public static final int MAX_AUDIO_RECORD_TIME_POST = 300;
	public static final int MAX_AUDIO_RECORD_TIME_COMPOSE = 300;
	public static final int MAX_AUDIO_RECORD_TIME_REST = 300;
	// public static final int MAX_AUDIO_RECORD_TIME_INBOX = 600;

	public static final String defaultVideoPath = "/sdcard/f1.3gp"; // by n
	public static final String imagePath = "Yelatalk/stream/image/Yelatalk";// ".rocketalk/streem/images"
																				// ;
	public static final String imageTempPath = "Yelatalk/stream/tmp/Yelatalk";// ".rocketalk/streem/images"
																				// ;
	public static final String imagePathPost = "Yelatalk/stream/imagepost/Yelatalk";// ".rocketalk/streem/images"
																						// ;
	public static final String contentVideo = "Yelatalk/stream/video/Yelatalk";
	public static final String contentVoice = "Yelatalk/stream/voice/Yelatalk";
	public static final String contentPost = "Yelatalk/post";
	public static final String contentTemp = "Yelatalk/temp";
	public static final int MAX_PATH_SIZE_IN_SYSTEM = 250;

	public final static int INBOX_WINDOW_SIZE = 2 * Constants.INBOX_MAX_REC_SIZE;

	public static final int MAX_PICTURE_CACHE_SIZE = 100;

	public static final int MIN_PASSWORD_LENGTH = 6;
	public static final int MIN_PHONE_LENGTH = 7;
	public static final int MIN_NAME_LENGTH = 2;
	public static final int MAX_NAME_LENGTH = 15;
	public static final int MIN_AGE = 15;

	public static final String AGENT_USER_MANAGER = "User Manager<a:usermgr>";
	public static final String AGENT_MESSAGE_MANAGER = "Message Manager<a:messagemgr>";
	public static final String AGENT_YANK_MANAGER = "Report User<a:yankprofile>";
	public static final String AG_FLAGCONTENT = "Report Abuse<a:rtmoderator>";
	public static final String AGENT_INVITE = "a:invite";

	public static final byte ALERT_NO_SOUND = -1;
	public static final byte ALERT_BEEP = 1;
	public static final byte ALERT_CHIMEUP = ALERT_BEEP + 1;
	public static final byte ALERT_KNOCK = ALERT_BEEP + 2;
	public static final byte ALERT_MOO = ALERT_BEEP + 3;
	public static final byte ALERT_NEWALERT = ALERT_BEEP + 4;
	public static final byte ALERT_RINGING = ALERT_BEEP + 5;

	public static final long COMPOSE_MAX_IMAGE_ATTACH_SIZE = BusinessProxy.sSelf.MaxSizeData * 1024 * 1024;
	public static final byte COMPOSE_MAX_IMAGE_ATTACH_COUNT = 10;

	public static final int MAX_DB_DATA_STORE_SIZE = 1 * 1024 * 1024; // 1MB //
	public static final int MAX_IMAGE_DATA_STORE_SIZE = 2 * 1024 * 1024; // 1MB
																			// //

	public static final String ACTIVITY_FOR_RESULT = "ACTIVITY_FOR_RESULT";

	public static final String VIDEO_REC_PROB_ON_DEVICE = "X10i";
	public static final String VIDEO_REC_PROB_ON_DEVICE2 = "c2004";//avnish phone
	public static int EXTRA_VIDEO_QUALITY = 0;// Currently value 0 means low
												// quality, suitable for MMS
												// messages, and value 1 means
												// high quality
	
	public static final long MAX_IMAGE_ATTACH_SIZE = BusinessProxy.sSelf.MaxSizeData * 1024 * 1024;
	public static final String FACEBOOK_APP_ID = "444857532355614";//"438783839520835";//"540382436098247";//"418150184911683";//"683245738356799";// "418150184911683";//"167162466662454";//"540382436098247";//
	public static final String GOOGLE_APP_ID =  "com.rocketalk.mobile.gp";//"com.kainat.app.android";//"167162466662454";

	public static final int VIDEO_RECORDING_DUERATION = 60 * 5;
	public static final long VIDEO_RECORDING_SIZE_LIMITE = 10000000L;
	public static final String IB_APP_IN_BG = "IB_APP_IN_BG";
	public static String ERROR = null;

	public static final int FEED_INITIAL_LANDING_PAGE = 1;
	public static final int FEED_INITIAL_LEFT_MENU = 2;
	public static final int FEED_INITIAL_DISCOVERY = 3;
	public static final int FEED_ACTIVITY = 4;
	public static final int FEED_MEDIA_COMMENT = 5;
	public static final int FEED_MEDIA_DETAILS = 6;
	public static final int FEED_NOTIFICATION_COUNT = 7;
	public static final int FEED_CONVERSATION_LIST = 8;
	public static final int FEED_GET_MESSAGE = 9;
	public static final int FEED_GET_CONVERSATION_MESSAGES = 10;

	public static final int FEED_GET_BOOKMARK_MESSAGES = 11;
	public static final int FEED_GET_CONVERSATION_MESSAGES_REFRESH = 12;
	public static final int FEED_GET_BOOKMARK_MESSAGES_MORE = 13;
	public static final int FEED_GET_CONVERSATION_MESSAGES2 = 14;
	public static final int FEED_GET_LIKE_USE = 15;
	public static final int FEED_GET_CHANNEL_REFRESH = 16;
	

	public static final String REGISTRATION = "REGISTRATION";
	public static final String LEFT_MENU_URL = "LEFT_MENU_URL";
	public static final String ACTIVITY_FEED_URL = "ACTIVITY_FEED_URL";
	public static final String DISCOVERY_FEED_URL = "DISCOVERY_FEED_URL";
	public static final String NOTIFICATIO_COUNT = "NOTIFICATIO_COUNT";
	public static final String SELECTED_TAB = "SELECTED_TAB";
	public static final String CLICK_ON_ACTIVITY_NOTIFICATION = "CLICK_ON_ACTIVITY_NOTIFICATION";
	public static final String FIRST_LOGIN = "FIRST_LOGIN";
	public static final String SHOW_MEDIA_SEARCH = "SHOW_MEDIA_SEARCH";
	public static final String MENU_RFRESH_TIME = "MENU_RFRESH_TIME";
	public static final long MENU_RFRESH_TIME_INTERVAL = 1000 * 30;//1000 * 60 * 10;
	// public static final String VERSION = "VERSION";

	public static final String LEFT_MENU_URL_ITEM = "LEFT_MENU_URL_ITEM";
	public static final String DISCOVERY_MENU_URL_ITEM = "DISCOVERY_MENU_URL_ITEM";

	public static final String ROW_SEPRETOR = "#";
	public static final String COL_SEPRETOR = "^";

	public static final long NOTIFICATIO_REFRESH_INTERVAL = 1000 * 5;// 30
	public static final long ADD_REFRESH_INTERVAL = 1000 * 30;

	public final static String MIRGIN_GROUP_EVENT = "MIRGIN_GROUP_EVENT";
	public final static String MIRGIN_MEDIA = "MIRGIN_MEDIA";
	public final static String MIRGIN_FEATURE_USER = "MIRGIN_FEATURE_USER";
	public final static String MIRGIN_COMMUNITY = "MIRGIN_COMMUNITY";

	public final static int ACTIVITY_FOR_RESULT_INT = Constants.ResultCode;
	public final static String FEATURED_COMMUNITY = "FEATURED_COMMUNITY";
	public final static String FEATURED_COMMUNITY_URL = "FEATURED_COMMUNITY_URL";

	public static final String TAB_DISCOVERY = "TAB_DISCOVERY";
	public static final String TAB_ACTIVITY = "TAB_ACTIVITY";
	
	public static final String CH_TOP_ID = "chtopid";
	public static final String NO_CHANNLE_JOINED = "nochjoined";

	public static final boolean DISPLAY_RT_CANVAS = true;

	// //
	public static final String SCRTEEN_NAME_ACTIVITY = "USR_NTF";
	public static final String SCRTEEN_NAME_DISCOVERY = "DSC_PAG";
	public static final String SCRTEEN_NAME_FEATURED_USER = "FTR_USR";
	public static final String SCRTEEN_NAME_SPONSORED_USER = "SPO_USR";
	public static final String SCRTEEN_NAME_GROUP_EVENT = "GRP_EVT";

	public static final String SCRTEEN_NAME_MESSAGE_BOX = "THREAD_MSG_BOX";
	public static final String SCRTEEN_NAME_BOOKMARK = "THREAD_MSG_BOOK";
	public static final String SCRTEEN_NAME_SYSTEM = "THREAD_MSG_SYS";
	public static final String SCRTEEN_NAME_THREAD_CHAT = "THREAD_MSG_CHT";
	public static final String SCRTEEN_NAME_THREAD_CHAT_INFO = "THREAD_MSG_CHT_INFO";

	public static final String SCRTEEN_NAME_EDIT_PROFILE = "EDIT_PROFILE";
	public static final String SCRTEEN_NAME_VIEW_PROFILE = "VIEW_PROFILE";
	public static final String SCRTEEN_NAME_INTEREST = "INTEREST";
	public static final String SCRTEEN_NAME_ACCOUNT_SETTING = "ACCOUNT_SETTING";
	public static final String SCRTEEN_NAME_EMAIL_VERIFICATION = "MAIL_VERIFY";
	public static final String SCRTEEN_NAME_CONTACT_SYNC = "CONTACT_SYNCH";

	public static final String DELETE_TEMP = "DELETE_TEMP";

	public static final String REFRESH_CONVERSIOTION_LIST = "REFRESH_CONVERSIOTION_LIST";

	public static final String PROFILE_UPDATED = "PROFILE_UPDATED";

	public final static String CONVERSATION_ID = "CONVERSATION_ID";
	public final static String AUTO_SWITCH = "AUTO_SWITCH";
	public final static String TITLE = "TITLE";
	public final static String MUC_PIC = "MUC_PIC";

	public final static int MESSAGE_STATUS_SENDING = 1;
	public final static int MESSAGE_STATUS_SENT = 2;
	public final static int MESSAGE_STATUS_UNABLE_TO_SEND = 3;

	public final static int PAGE_DISPLAY_LIMITE = 1000;
	// Featured User - FTR_USR
	// Featured User more FTR_USR;;M
	// Sponsored User - SPO_USR
	// Sponsored User more - SPO_USR;;M
	// Group Event - GRP_EVT
	// Group Event - GRP_EVT;;M

	public final static byte SCREENLAYOUT_SIZE_SMALL = 1;
	public final static byte SCREENLAYOUT_SIZE_MEDIUM = 2;
	public final static byte SCREENLAYOUT_SIZE_LARGE = 3;
	public final static byte SCREENLAYOUT_SIZE_X_LARGE = 4;
	public final static byte SCREENLAYOUT_SIZE_UNKNOWN = 5;
	
	public static final byte SMALL_PHONE = 1;
	public static final byte MEDIUM_PHONE = 2;
	public static final byte LARGE_PHONE = 3;
	public static final byte XLARGE_PHONE = 4;
	public static final byte SIZE_UNKNOWN = 5;
	
	public final static String CHECKING_DONE = "CHECKING_DONE";
	public final static String CONVERSATION_LIST_MFUID_FOR_MORE = "CONVERSATION_LIST_MFUID_FOR_MORE";

	public final static String LAST_MFUID_FOR_CONVERSATION_LIST = "LAST_MFUID_FOR_CONVERSATION_LIST";

	public static final float dimamount = 0.4f;
	public static final String FORGOT_PASS_AUTO_FILL = "";

	public static final String CAN_UPLOAD_CONTACT = "CAN_UPLOAD_CONTACT";

	
//	[Settings sharedInstance].mediaCatIds = @"102|^103|^104|^105|^106|^107|^108|^109|^110|^111|^112|^113|^114|^115|^116";
//    
//    [Settings sharedInstance].mediaCat = @"Food & Drink|^Movies & TV|^Music|^Sports|^Nature & Outdoors|
	//^Lifestyle tools|^Travel & Places|^Fashion & Beauty|^Arts & Literature|^How to & Learning|^People & Relationships
	//|^Religion & Spirituality|^News & Citizen journalism|^Humour|^Others";
//    
	public static String[] values = new String[] { 
		"Food & Drink",
		"Movies & TV", 
		"Music", 
		"Sports", 
		"Nature & Outdoors",
		"Lifestyle tools", 
		"Travel & Places", 
		"Fashion & Beauty",
		"Arts & Literature", 
		"How to & Learning", 
		"People & Relationships",
		"Religion & Spirituality", 
		"News & Citizen journalism", 
		"Humour", 
		"Others" };
	
	public static String[] valuesId = new String[] { 
		"102", 
		"103", 
		"104", 
		"105", 
		"106", 
		"107",
		"108", 
		"109", 
		"110", 
		"111", 
		"112", 
		"113", 
		"114", 
		"115", 
		"116" };

	public static Hashtable<String, Integer> iconHash= new Hashtable<String, Integer>() ;
	
	static {
		iconHash.put("102", R.drawable.food_sport);
		iconHash.put("103", R.drawable.movies_tv);
		iconHash.put("104", R.drawable.music);
		iconHash.put("105", R.drawable.sports);
		iconHash.put("106", R.drawable.nature_outdoors);
		iconHash.put("107", R.drawable.lifestyle_tools);
		iconHash.put("108", R.drawable.travel_places);
		iconHash.put("109", R.drawable.fashion_beauty);
		iconHash.put("110", R.drawable.arts_lterature);
		iconHash.put("111", R.drawable.how_learning);
		iconHash.put("112", R.drawable.people_relationships);
		iconHash.put("113", R.drawable.religion_spirituality);
		iconHash.put("114", R.drawable.citizen_journalism);
		iconHash.put("115", R.drawable.humour);
		iconHash.put("116", R.drawable.others);
	}
//	public static Integer[] icon = new Integer[] { R.drawable.food_sport,
//			R.drawable.movies_tv, R.drawable.music, R.drawable.sports,
//			R.drawable.nature_outdoors, R.drawable.lifestyle_tools,
//			R.drawable.how_learning, R.drawable.travel_places,
//			R.drawable.fashion_beauty, R.drawable.arts_lterature,
//			R.drawable.people_relationships, R.drawable.religion_spirituality,
//			R.drawable.citizen_journalism, R.drawable.humour, R.drawable.others };

//	public static Integer[] icon_b = new Integer[] { R.drawable.food_sport_b,
//			R.drawable.movies_tv_b, R.drawable.music_b, R.drawable.sports_b,
//			R.drawable.nature_outdoors_b, R.drawable.lifestyle_tools_b,
//			R.drawable.how_learning_b, R.drawable.travel_places_b,
//			R.drawable.fashion_beauty_b, R.drawable.arts_lterature_b,
//			R.drawable.people_relationships_b,
//			R.drawable.religion_spirituality_b,
//			R.drawable.citizen_journalism_b, R.drawable.humour_b,
//			R.drawable.others_b };

	public static final String ID_FOR_UPDATE_PROFILE = "UPDATE_PROFILE";
	public static final String VISIBLE_COUNT = "VISIBLE_COUNT";
	
	
	public static final String FLURRY_API_KEY = "PFV6P8M3SSVK3VF3XZNY" ;
	
	public static final int CHUNK_LENGTH = 1024;
	public static final int IN_SAMPLE_SIZE = 1;//4
	public static final int COMPRESS = 80;//100
	public static final CompressFormat COMPRESS_TYPE = CompressFormat.JPEG;//100
	
	public static final String GOOGLE_PROJECT_ID_PROD = "1003192974761";
	public static final String GOOGLE_PROJECT_ID_DEV = "1001502560648";
	public static final String DATABASE_NAME_1 = "kainat123.db";
	public static final String DATABASE_NAME_2 = "kainat1234";
	
	public static final int CROP_DIMEN = 300;
	public static final int CROP_DIMEN_MIN = 300;
	
	public static final int THUMBNAIL_HEIGHT = 48;
	public static final int THUMBNAIL_WIDTH = 66;
	
	public static final int ResultCode = 9999;
	
	public static final String BLOBKED_CHARS= "~#^|$%&*@!<>?,.\\/\";:'[]{}_()+`";
	public static final String BLOBKED_CHARS_TAGS= "~^|$%&*@!<>?,.\\/\";:'[]{}_()+`";
	public static final String EMOJI_START_VALUE = "\uD83D\uDE01";
	
	public static final byte CHANNEL_NEW_COUNTER_FOR_EXISTING = 1;
	public static final byte CHANNEL_NEW_COUNTER_FOR_NEW = 2;
	public static final byte CHANNEL_YOU_BECOME_MEMBER = 3;
	public static final byte CHANNEL_REPORT_MESSAGE = 4;
	public static final int CROP_DIMENSION_X = 600;
	public static final int CROP_DIMENSION_Y = 600;
	
	public static int RECORDING_TIME = 6 * 60;
	
	public static final String MOBILE_NUMBER_TXT = "mobile_number";
	public static final String COUNTRY_CODE_TXT = "country_code";
	public static String countryCode = "+91";
	public static  String SELF_VARIFICATION_MSG = "SuperChat Self verification code is 7496";
	public static String SELF_VARIFICATION_NUM = null;
	
	public static boolean isBuildLive = true;
}
