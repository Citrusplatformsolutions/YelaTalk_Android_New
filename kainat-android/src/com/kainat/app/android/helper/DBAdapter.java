package com.kainat.app.android.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kainat.app.android.util.Logger;

public class DBAdapter extends ContextWrapper {
	private SQLiteDatabase db;

	/**
	 * Constructor creates the database with version and database name specified After creating the instance of this class open method should be called to open the database in Writable mode other wise
	 * all queries will fail. After finishing the work close must be called to close the database.
	 * 
	 * @param _context
	 *            Context of the application. This can be get from activity.getApplicationContext()
	 * @param onCreateQueries
	 *            The array of the queries to be executed when first Database is created
	 * @param databaseName
	 *            Name of the database file. This must end with <b>.db</b>
	 * @param version
	 *            The version of the database. Usually 1
	 * @throws IllegalArgumentException
	 *             Thrown when either _context is null or Database is null or version is less than 1 Also when DatabaseName does not ends with <b>.db</b>
	 */
	public DBAdapter(Context _context, String databaseName, int version, String[] onCreateQueries) throws IllegalArgumentException {
		super(_context);
		if (null == _context || null == databaseName || 1 > version)
			throw new IllegalArgumentException("Either parameter is null or version is less than 1");
		if (!databaseName.endsWith(".db"))
			throw new IllegalArgumentException("Database name must end with .db");
		open(databaseName);
		if (null != onCreateQueries) {
			for (int i = 0; i < onCreateQueries.length; i++) {
				db.execSQL(onCreateQueries[i]);
			}
		}
	}

	/**
	 * This method opens the Database in Write mode.
	 * 
	 * @throws SQLException
	 *             Any SQL exception thrown while opening the database in Write mode
	 */
	public void open(String name) throws SQLException {
		if (null == db || !db.isOpen())
			db = this.openOrCreateDatabase(name, Context.MODE_PRIVATE, null);
	}

	/**
	 * This method can be used to close the database
	 */
	public void close() {
		if (null != db)
			db.close();
		db = null;
	}

	/**
	 * This method can be used to insert a new entry in the selected table
	 * 
	 * @param val
	 *            ContentValues with already filled values.
	 * @param tableName
	 *            Table name in which the entry should be made
	 * @return The index found after insertion(In case of auto increment ON).
	 * @throws IllegalArgumentException
	 *             Thrown when passed values to insert is null.
	 * @throws IllegalStateException
	 *             Thrown when database is not open.
	 */
	public long insertEntry(String tableName, ContentValues values) throws IllegalStateException, IllegalArgumentException {
		if (null == values)
			throw new IllegalArgumentException("Values must not be null");
		if (null == db || !db.isOpen())
			throw new IllegalStateException("Database not open");
		if(Logger.ENABLE)
			Logger.debug("DB ENgin", "tableName: "+tableName+"  ; "+values.toString()); 
		return db.insert(tableName, null, values);
	}
	

	/**
	 * This method can be used to update the entry available in database
	 * 
	 * @param tableName
	 *            Name of the table in which entry is to be updated
	 * @param values
	 *            ConstentValues with already filled data according to the table row
	 * @param whereClause
	 *            where clause (Excluding WHERE itself) ex. <b>name = 'XXXXXX'</b>
	 * @param whereArgs
	 *            pass the argument if you have values later for ex. (whereClause as <b>name = ? and age = ? </b> and whereArgs = <b> new String[]{"XXXXXX", "27"} </b>
	 * @return Number of rows affected
	 * @throws IllegalArgumentException
	 *             Thrown when passed values to update is null.
	 * @throws IllegalStateException
	 *             Thrown when database is not open.
	 */

	public long updateEntry(String tableName, ContentValues values, String whereClause, String[] whereArgs) throws IllegalStateException, IllegalArgumentException {
		if (null == values)
			throw new IllegalArgumentException("Values must not be null");
		if (null == db || !db.isOpen())
			throw new IllegalStateException("Database not open");
		return db.update(tableName, values, whereClause, whereArgs);
	}

	/**
	 * This method is used to remove any particular entry with specific condition
	 * 
	 * @param tableName
	 *            Name of the table.
	 * @param whereClause
	 *            where clause (Excluding WHERE itself) ex. <b>name = 'XXXXXX'</b>
	 * @param whereArgs
	 *            pass the argument if you have values later for ex. (whereClause as <b>name = ? and age = ? </b> and whereArgs = <b> new String[]{"XXXXXX", "27"} </b>
	 * @return The could rows affected in table
	 * @throws IllegalArgumentException
	 *             Thrown when tableName passed is null.
	 * @throws IllegalStateException
	 *             Thrown when database is not open.
	 */
	public int delte(String tableName, String whereClause, String[] whereArgs) throws IllegalStateException, IllegalArgumentException {
		if (null == tableName)
			throw new IllegalArgumentException("Table name must not be null");
		if (null == db || !db.isOpen())
			throw new IllegalStateException("Database not open");
		return db.delete(tableName, whereClause, whereArgs);
	}

	/**
	 * This method can be used to delete the whole table
	 * 
	 * @param tableName
	 *            Name of the table
	 * @throws IllegalArgumentException
	 *             Thrown when tableName passed is null.
	 * @throws IllegalStateException
	 *             Thrown when database is not open.
	 */
	public void deleteTable(String tableName) throws IllegalStateException, IllegalArgumentException {
		if (null == tableName)
			throw new IllegalArgumentException("Table name must not be null");
		if (null == db || !db.isOpen())
			throw new IllegalStateException("Database not open");
		db.execSQL("DROP TABLE IF EXISTS " + tableName);
	}

	/**
	 * This method is used to create a new table.
	 * 
	 * @param tableName
	 *            - name of the table
	 * @param otherParams
	 *            - all other parameters For ex. (<b>id integer primary key auto increment, name text not null, age integer</b>)
	 * @throws IllegalArgumentException
	 *             Thrown when either tableName or its structure parameter is null.
	 * @throws IllegalStateException
	 *             Thrown when database is not open.
	 */
	public void createTable(String tableName, String otherParams) throws IllegalStateException, IllegalArgumentException {
		if (null == tableName || null == otherParams)
			throw new IllegalArgumentException("Either content is null");
		if (null == db || !db.isOpen())
			throw new IllegalStateException("Database not open");
		StringBuffer buff = new StringBuffer("CREATE TABLE ");
		buff.append(tableName);
		buff.append('(');
		buff.append(otherParams);
		buff.append(')');
		db.execSQL(buff.toString());
		buff = null;
	}

	/**
	 * Fires a query to get all the data from specified table
	 * 
	 * @param tableName
	 *            Name of the table to get all entries
	 * @param fieldNames
	 *            all the names of field to get the entries. Passing null will return all columns
	 * @return Cursor is returned after executing the query
	 * @throws IllegalArgumentException
	 *             Thrown when tableName passed is null.
	 * @throws IllegalStateException
	 *             Thrown when database is not open.
	 */
	public Cursor getAllEntries(String tableName, String[] fieldNames) throws IllegalStateException, IllegalArgumentException {
		if (null == tableName)
			throw new IllegalArgumentException("Table name must not be null");
		if (null == db || !db.isOpen())
			throw new IllegalStateException("Database not open");
		return db.query(tableName, fieldNames, null, null, null, null, null);
	}

	/**
	 * This method is used to Fire query and get the result set
	 * 
	 * @param tableName
	 *            Name of the table to query from
	 * @param fieldNames
	 *            Name of the fields wanted. Null will return all the fields
	 * @param whereClause
	 *            Where clause specifying the condition excluding WHERE itself
	 * @param whereArgs
	 *            pass this argument when there are one or more values unknown For ex. (in whereClause <b> "NAME = ? and AGE = ? "</b> and in whereArgs <b>new String[] {"XXXXXX", "26"}</b> )
	 * @return The Cursor after querying from specified table
	 * @throws IllegalArgumentException
	 *             Thrown when tableName passed is null.
	 * @throws IllegalStateException
	 *             Thrown when database is not open.
	 */
	public Cursor query(String tableName, String[] fieldNames, String whereClause, String[] whereArgs, String groupBy, String having, String orderBy, String limit) throws IllegalStateException,
			IllegalArgumentException {
		if (null == tableName)
			throw new IllegalArgumentException("Table name must not be null");
		if (null == db || !db.isOpen())
			throw new IllegalStateException("Database not open");
		return db.query(tableName, fieldNames, whereClause, whereArgs, groupBy, having, orderBy, limit);
	}

	/**
	 * This method executes the raw Query and returns the result.
	 * 
	 * @param sql
	 *            The query to be executed
	 * @param selectionArgs
	 *            Arguments for the ?s in sql
	 * @return Cursor for the executed query
	 * @throws IllegalArgumentException
	 *             When sql argument is null
	 * @throws IllegalStateException
	 *             Thrown when database is not open.
	 */
	public Cursor executeRawQuery(String sql, String[] selectionArgs) throws IllegalArgumentException, IllegalStateException {
		if (null == sql)
			throw new IllegalArgumentException("Query must not be null");
		if (null == db || !db.isOpen())
			throw new IllegalStateException("Database not open");
		return db.rawQuery(sql, selectionArgs);
	}

	public void executeSQL(String sql, String[] selectionArgs) throws IllegalArgumentException, IllegalStateException {
		if (null == sql)
			throw new IllegalArgumentException("Query must not be null");
		if (null == db || !db.isOpen())
			throw new IllegalStateException("Database not open");
		db.execSQL(sql);
	}
}