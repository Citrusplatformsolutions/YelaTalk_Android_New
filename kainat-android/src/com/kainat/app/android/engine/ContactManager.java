package com.kainat.app.android.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Contacts.People;
import android.provider.ContactsContract;

import com.kainat.app.android.model.PhoneContact;
import com.kainat.app.android.util.Logger;

@SuppressWarnings("deprecation")
public class ContactManager {

	private static final String TAG = ContactManager.class.getSimpleName();
	public static final boolean log = false ;
	private ContactHandler parent;
	private Activity activity;
	private boolean mCancelLoading;

	public interface ContactHandler {

		public void newContactAdded(PhoneContact phoneContact);

		public void contactLoadingDone();
	}

	public ContactManager(Activity activity, ContactHandler parent) {
		this.activity = activity;
		this.parent = parent;
	}

	public void addContact(PhoneContact contact) {
		ContentValues values = new ContentValues();
		values.put(People.NAME, contact.name);
		Uri uri = activity.getContentResolver().insert(People.CONTENT_URI, values);
		Uri phoneUri = Uri.withAppendedPath(uri, People.Phones.CONTENT_DIRECTORY);
		values.clear();
		values.put(People.Phones.TYPE, People.Phones.TYPE_MOBILE);
		values.put(People.Phones.NUMBER, contact.phoneNumber.get(0));
		activity.getContentResolver().insert(phoneUri, values);
	}

	public PhoneContact findContact(String phoneNumber) {
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

		ContentResolver contentResolver = activity.getContentResolver();
		Cursor contactLookup = contentResolver.query(uri, new String[] { BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.NUMBER }, null, null, null);

		try {
			if (contactLookup != null && contactLookup.getCount() > 0) {
				contactLookup.moveToNext();
				PhoneContact newContact = new PhoneContact();
				newContact.phoneNumber.add(phoneNumber);
				newContact.name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
				//newContact.id = contactLookup.getLong(contactLookup.getColumnIndex(BaseColumns._ID));
				return newContact;
			}
		} finally {
			if (contactLookup != null) {
				contactLookup.close();
			}
		}

		return null;
	}

	public List<PhoneContact> getAllContact(boolean onlyEmail, boolean onlyContact) {
		List<PhoneContact> contacts = new ArrayList<PhoneContact>();

		ContentResolver cr = activity.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,ContactsContract.Contacts.PHOTO_ID }, null, null,
				ContactsContract.Contacts.DISPLAY_NAME + " ASC");
		PhoneContact contact = null;
		if (Logger.ENABLE)
			Logger.debug(TAG, "--------------------------------------getAllContact");
		if (cur==null || cur.isClosed()) {
			return contacts;
		}
		if (cur.getCount() > 0 && !mCancelLoading) {
			while (cur.moveToNext() && !mCancelLoading) {try{
				contact = getNewContact(activity, cur, onlyEmail);
			}catch (Exception e) {

			}
			catch (OutOfMemoryError e) {

			}
			boolean shouldReturn = true;
			if(log){
				if(contact!=null)
					System.out.println("-----contact name : "+contact.name);
				else
					System.out.println("-----contact is null ");}
			if(contact != null && contact.phoneNumber!=null && contact.phoneNumber.size()>0){
				if(log)
					System.out.println("-----contact phoneNumber len : "+contact.phoneNumber.size() +" : "+contact.phoneNumber.toString());
				for (String text : contact.phoneNumber) {
					if(text != null && text.trim().length()>0){
						shouldReturn = false;
					}
				}
			}
			if(contact != null && contact.email!=null && contact.email.size()>0){
				if(log)
					System.out.println("-----contact email len : "+contact.email.size() +" : "+contact.email.toString());
				for (String text : contact.email) {
					if(text != null && text.trim().length()>0){
						shouldReturn = false;
					}
				}
			}
			if(shouldReturn)
				continue ;
			if (null != contact)
			{	
				if(contact.getMobileno()!=null ){
					if(contact.getMobileno().length()>2)
						contacts.add(contact);
				}
			}
			if (!mCancelLoading && null != this.parent && null != contact) {
				this.parent.newContactAdded(contact);
			}
			}
		}
		cur.close();
		if(onlyEmail)
		{
			if (!mCancelLoading && null != this.parent) {
				this.parent.contactLoadingDone();
			}
			return contacts;
		}
		try{
			Uri simUri = Uri.parse("content://icc/adn");
			Cursor cursorSim = cr.query(simUri, null, null, null, null);
			while (cursorSim.moveToNext()) {
				String displayName = cursorSim.getString(cursorSim.getColumnIndex("name"));
				String phoneNo;
				PhoneContact newContact = new PhoneContact();
				if (null == displayName) {
					displayName = "Unknown";
				}
				displayName = displayName.replaceAll("|", "");
				newContact.name = displayName;
				if (!onlyEmail) {
					phoneNo = cursorSim.getString(cursorSim.getColumnIndex("number"));
					if(phoneNo != null)
					{
						phoneNo = phoneNo.replaceAll("\\D", "");
						phoneNo = phoneNo.replaceAll("&", "");
						newContact.phoneNumber.add(phoneNo);
					}
				}
				if (null != newContact)
					contacts.add(newContact);
				if (!mCancelLoading && null != this.parent && null != newContact) {
					this.parent.newContactAdded(newContact);
				}
			}

			cursorSim.close();
		}
		catch (Exception e) {
			e.printStackTrace() ;
		}
		catch (OutOfMemoryError e) {
			e.printStackTrace() ;
		}
		if (!mCancelLoading && null != this.parent) {
			this.parent.contactLoadingDone();
		}
		return contacts;
	}

	public void cancelLoading() {
		mCancelLoading = true;
		if (Logger.ENABLE)
			Logger.debug(TAG, "Canceled--------");
	}

	private PhoneContact getNewContact(Activity activity, Cursor idCursor, boolean onlyEmail) {
		if (mCancelLoading) {
			return null;
		}
		ContentResolver cr = activity.getContentResolver();
		String id = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts._ID));
		String displayName = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		//String displaynumber = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts.));
		PhoneContact newContact = new PhoneContact();

		newContact.thumbnailUri = getPhotoUriFromID(idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts._ID))) ;
		//		System.out.println("----thumbnailUri : "+newContact.thumbnailUri);
		if (null == displayName) {
			displayName = "Unknown";
		}


		boolean found = false;
		if (!onlyEmail) {
			//Query phone here.  Covered next
			Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);
			while (pCur!=null && pCur.moveToNext() && !mCancelLoading) {
				// Do something with phones
				String mobileNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if(mobileNo!=null){
					newContact.setmobileno(mobileNo);
					newContact.name = displayName;
				}
				/*List<String> a = new ArrayList<String>();
				a.add(mobileNo);
				newContact.phoneNumber = a;*/
				//String mobileNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.));
				if(!newContact.phoneNumber.contains(mobileNo) && pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)).equalsIgnoreCase("2")){
					newContact.phoneNumber.add(mobileNo);//+":"+pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
				}
				found = true;
			}
			if(pCur!=null)
				pCur.close();
		}
		Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);
		while (emailCur!=null && emailCur.moveToNext() && !mCancelLoading) {
			String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
			newContact.email.add(email);
			found = true;
		}
		if(emailCur!=null)
			emailCur.close();
		if (!found) {
			newContact = null;
		}
		displayName = null;
		id = null;
		return newContact;
	}
	private Uri getPhotoUriFromID(String id) {
		try {
			//	    	System.out.println("----getPhotoUriFromID : "+id);
			//	        Cursor cur =activity. getContentResolver()
			//	                .query(ContactsContract.Data.CONTENT_URI,
			//	                        null,
			//	                        ContactsContract.Data.CONTACT_ID
			//	                                + "="
			//	                                + id
			//	                                + " AND "
			//	                                + ContactsContract.Data.MIMETYPE
			//	                                + "='"
			//	                                + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
			//	                                + "'", null, null);
			//	        if (cur != null) {
			//	            if (!cur.moveToFirst()) {
			//	                return null; // no photo
			//	            }
			//	        } else {
			//	            return null; // error in cursor process
			//	        }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Uri person = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
		return Uri.withAppendedPath(person,
				ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	}
}