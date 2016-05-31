package com.kainat.app.android.util;

import java.util.HashMap;

/**
 * This class is used to store the model state/data model for the application
 * 
 * @version $Revision: 1.3 $
 */
public class DataModel {

	/**
	 * object to store the state/data model
	 */
	private HashMap<String, Object> model;

	/**
	 * used to provide synchronization
	 */
	private static Object mutex = new Object();

	/**
	 * singleton object of the class
	 */
	public static DataModel sSelf;

	private DataModel() {
	}

	/**
	 * factory method to get singleton object of the class
	 */
	public static void createInstance() {
		synchronized (mutex) {
			if (sSelf == null) {
				sSelf = new DataModel();
			}
		}
	}

	/**
	 * method to store object in the data model
	 * 
	 * @param key
	 *            key for the object, that can be used to retrieve the object later
	 * @param object
	 *            object to be stored
	 */
	public void storeObject(String key, Object object) {
		synchronized (mutex) {

			if (model == null) {
				model = new HashMap<String, Object>();
			}

			model.put(key, object);
		}
	}

	/**
	 * Method to get data from the data model
	 * 
	 * @param key
	 *            key of the object user wants to get
	 * @return object previously stored with the given key or null if there was no object stored with the given key
	 */
	public Object getObject(String key) {
		synchronized (mutex) {
			if (model == null) {
				return null;
			}
			return model.get(key);
		}
	}

	public String getString(String key) {
		synchronized (mutex) {

			if (model == null) {
				return null;
			}
			Object obj = model.get(key);
			return null == obj ? "" : obj.toString();
		}
	}

	public boolean getBoolean(String key) {
		synchronized (mutex) {
			if (model == null) {
				return false;
			}
			Object obj = model.get(key);
			if (null != obj && obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			} else {
				return false;
			}
		}

	}
	public boolean getRemoveBoolean(String key) {
		synchronized (mutex) {
			if (model == null) {
				return false;
			}
			Object obj = model.remove(key);
			if (null != obj && obj instanceof Boolean) {
				return ((Boolean) obj).booleanValue();
			} else {
				return false;
			}
		}

	}
	public int getByte(String key) {
		synchronized (mutex) {
			if (model == null) {
				return 0;
			}
			Object obj = model.get(key);
			if (null != obj && obj instanceof Byte) {
				return (Byte) obj;
			} else {
				return 0;
			}
		}
	}
	public int getInt(String key) {
		synchronized (mutex) {
			if (model == null) {
				return 0;
			}
			Object obj = model.get(key);
			if (null != obj && obj instanceof Integer) {
				return ((Integer) obj).intValue();
			} else {
				return 0;
			}
		}
	}

	public int removeInt(String key) {
		synchronized (mutex) {
			if (model == null) {
				return 0;
			}
			Object obj = model.remove(key);
			if (null != obj && obj instanceof Integer) {
				return (Integer) obj;
			} else {
				return -1;
			}
		}
	}

	public byte removeByte(String key) {
		synchronized (mutex) {
			if (model == null) {
				return 0;
			}
			Object obj = model.remove(key);
			if (null != obj && obj instanceof Byte) {
				return (Byte) obj;
			} else {
				return -1;
			}
		}
	}

	/**
	 * Method to remove object from the data model
	 * 
	 * @param key
	 *            key of the object to be removed from the data model
	 * @return true if the object was removed successfully or false
	 */
	public Object removeObject(String key) {
		synchronized (mutex) {
			if (model == null) {
				return null;
			}
			return model.remove(key);
		}
	}

	public String removeString(String key) {
		return (String) removeObject(key);
	}

	/**
	 * method to remove all the data from the data model
	 * 
	 * @return true id the operation is successful, false otherwise
	 */
	public boolean resetDataModel() {
		synchronized (mutex) {
			model.clear();
		}
		return true;
	}

}
