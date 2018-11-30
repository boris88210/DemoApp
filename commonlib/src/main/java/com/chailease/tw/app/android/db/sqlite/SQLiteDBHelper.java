package com.chailease.tw.app.android.db.sqlite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chailease.tw.app.android.db.ComplexDataModel;
import com.chailease.tw.app.android.db.DataBaseFactory;
import com.chailease.tw.app.android.db.DataBaseKeeper;
import com.chailease.tw.app.android.db.DataBean;
import com.chailease.tw.app.android.db.PARAM;
import com.chailease.tw.app.android.db.TableDefinition;
import com.chailease.tw.app.android.utils.LogUtility;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.chailease.tw.app.android.commonlib.BuildConfig.LIB_ID;
import static com.chailease.tw.app.android.commonlib.BuildConfig.isLibTrace;
import static com.chailease.tw.app.android.db.DataBaseFactory.IDX_LIST;
import static com.chailease.tw.app.android.db.DataBaseFactory.INTEGER_NOTNULL;
import static com.chailease.tw.app.android.db.DataBaseFactory.INTEGER_NULLABLE;
import static com.chailease.tw.app.android.db.DataBaseFactory.NUMBER_NOTNULL;
import static com.chailease.tw.app.android.db.DataBaseFactory.NUMBER_NULLABLE;
import static com.chailease.tw.app.android.db.DataBaseFactory.PRIMARY_KEY;
import static com.chailease.tw.app.android.db.DataBaseFactory.STRING_NOTNULL;
import static com.chailease.tw.app.android.db.DataBaseFactory.STRING_NULLABLE;
import static com.chailease.tw.app.android.db.TableDefinition.COLUMN_NAME_DECLARE_PREFIX;
import static com.chailease.tw.app.android.db.TableDefinition.DATA_TYPE.INTEGER;
import static com.chailease.tw.app.android.db.TableDefinition.DATA_TYPE.NUMBER;
import static com.chailease.tw.app.android.db.TableDefinition.DATA_TYPE.STRING;

/**
 *
 *  使用 SQL Helper 建立資料庫
 *  定義資料庫的外觀之後，您應實作多種方法以建立並維護資料庫與表格。 以下所示是可建立及刪除表格的某些典型陳述式：
 *  private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
 *      FeedEntry._ID + " INTEGER PRIMARY KEY," +
 *      FeedEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
 *      FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
 *      ... // Any other options for the CREATE command
 *      " )";
 *
 *  private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
 *  如同在裝置的內部儲存空間儲存檔案一樣，Android 會將您的資料庫儲存在與應用程式關聯的私用磁碟空間內。 依預設，其他應用程式無法存取此區域，因此您的資料安全無虞。
 *  SQLiteOpenHelper 類別中提供一組有用的 API。若使用此類別取得資料庫的參考，只有在需要執行且並非處於應用程式啟動期間時，系統才會執行資料庫的建立與更新操作 (執行時間可能很長)。
 *  您只需呼叫 getWritableDatabase() 或 getReadableDatabase() 即可。
 *  注意：由於這些操作的時間可能很長，因此請確保您在背景執行緒 (例如 AsyncTask 或 IntentService) 中呼叫 getWritableDatabase() 或 getReadableDatabase()。
 *  若要使用 SQLiteOpenHelper，請建立可覆寫 onCreate()、onUpgrade() 與 onOpen() 回呼方法的子類別。 您還可以實作 onDowngrade()，但這並非必需的操作。
 *  例如，以下展示了 SQLiteOpenHelper (使用上述某些命令) 的實作：
 *  public class FeedReaderDbHelper extends SQLiteOpenHelper {
 *      // If you change the database schema, you must increment the database version.
 *      public static final int DATABASE_VERSION = 1;
 *      public static final String DATABASE_NAME = "FeedReader.db";
 *
 *      public FeedReaderDbHelper(Context context) {
 *          super(context, DATABASE_NAME, null, DATABASE_VERSION);
 *      }
 *      public void onCreate(SQLiteDatabase db) {
 *          db.execSQL(SQL_CREATE_ENTRIES);
 *      }
 *      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 *          // This database is only a cache for online data, so its upgrade policy is
 *          // to simply to discard the data and start over
 *          db.execSQL(SQL_DELETE_ENTRIES);
 *          onCreate(db);
 *      }
 *      public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 *          onUpgrade(db, oldVersion, newVersion);
 *      }
 *  }
 *  若要存取您的資料庫，請啟動 SQLiteOpenHelper 的子類別：
 *  FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());
 *
 *  將資訊置入資料庫中
 *  透過將 ContentValues 物件傳遞至 insert() 方法，可將資料插入至資料庫：
 *
 *  // Gets the data repository in write mode
 *  SQLiteDatabase db = mDbHelper.getWritableDatabase();
 *
 * // Create a new map of values, where column names are the keys
 * ContentValues values = new ContentValues();
 * values.put(FeedEntry.COLUMN_NAME_ENTRY_ID, id);
 * values.put(FeedEntry.COLUMN_NAME_TITLE, title);
 * values.put(FeedEntry.COLUMN_NAME_CONTENT, content);
 *
 * // Insert the new row, returning the primary key value of the new row
 * long newRowId;
 * newRowId = db.insert(
 *      FeedEntry.TABLE_NAME,
 *      FeedEntry.COLUMN_NAME_NULLABLE,
 *      values);
 * insert() 的第一個引數即為表格名稱。 第二個引數將提供欄的名稱， 在 ContentValues 為空時，架構可在該欄中插入 NULL (若您將其設為 "null"，則在無值時，架構不會插入列)。
 *
 *
 */
public class SQLiteDBHelper extends SQLiteOpenHelper implements DataBaseKeeper {

	private static final String STRING_TYPE = " TEXT";
	private static final String NUMBER_TYPE = "REAL";
	private static final String INTEGER_TYPE = "INTEGER";
	private static final String COMMA_SEP = ", ";
	private static final String END_COMMAND = "; ";

	private final String NAME;
	private final int VERSION;
	SQLiteDatabase mDB;
	boolean upgrading = false;

	//public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
	public SQLiteDBHelper(Context context, String name, int version) {
		super(context, name, null, version);
		if (isLibTrace)
			LogUtility.debug(this, "Constructor", " --> check ");
		this.NAME = name;
		this.VERSION = version;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (isLibTrace)
			LogUtility.debug(this, "onCreate", " --> start ");
		mDB = db;
		try {
			String[] sqls = createDB();
			for (String sql : sqls)
				db.execSQL(sql);
			if (isLibTrace)
				LogUtility.debug(this, "onCreate", " --> done " + sqls.length);
		} finally {
			mDB = null;
		}
		if (isLibTrace)
			LogUtility.debug(this, "onCreate", " --> end ");
	}
	public void onCreate(SQLiteDatabase db, int oldVersion) {
		if (isLibTrace)
			LogUtility.debug(this, "onCreate", " --> start " + oldVersion);
		mDB = db;
		try {
			String[] sqls = createDB(oldVersion);
			if (null != sqls && sqls.length > 0)
				for (String sql : sqls)
					db.execSQL(sql);
			if (isLibTrace)
				LogUtility.debug(this, "onCreate", " --> done " + sqls.length);
		} finally {
			mDB = null;
		}
		if (isLibTrace)
			LogUtility.debug(this, "onCreate", " --> end " + oldVersion);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (isLibTrace)
			LogUtility.debug(this, "onUpgrade", " --> start " + oldVersion + ">>" + newVersion);
		if (newVersion>oldVersion)
			upgrading = true;
		else return;
		mDB = db;
		try {
			DataBean[] keepBeans = keepData(oldVersion);
			String[] sqls = dropDB(oldVersion);
			if (null != sqls && sqls.length>0) {
				for (String sql : sqls)
					db.execSQL(sql);
			}
			onCreate(db, oldVersion);
			if (null != keepBeans && keepBeans.length>0) {
				for (DataBean data : keepBeans) {
					data.migration(oldVersion, newVersion);
					insertRow(data);
				}
			}
		} finally {
			mDB = null;
		}
		if (isLibTrace)
			LogUtility.debug(this, "onUpgrade", " --> end ");
	}

	@Override
	public String getDBName() {
		return NAME;
	}
	@Override
	public int getDBVersion() {
		return VERSION;
	}

	@Override
	public String formatIntValue(int value) {
		return INT_FORMAT.format(value);
	}
	@Override
	public String formatNumValue(double value) {
		return NUM_FORMAT.format(value);
	}

	@Override
	public String[] createDB(int... oldVersion) {
		ArrayList<String> sqls = new ArrayList<String>();
		for (Class<? extends TableDefinition> table : DataBaseFactory.getTables()) {
			if (upgrading) {
				String[] ctSqls = null;
				try {
					ctSqls = table.newInstance().createSql(oldVersion[0]);
				} catch (InstantiationException|IllegalAccessException e) {
					throw new IllegalStateException("Bad TableDefinition " + table.getName());
				}
				if (ctSqls != null && ctSqls.length>0) {
					if (StringUtils.isBlank(ctSqls[0]) || SKIP_THIS_PROCESS_FOR_CURRENT_TABLE.equals(ctSqls[0]))
						continue;
					else if ( !GO_BY_STANDARD_PROCESS.equals(ctSqls[0])) {
						for (String ctsql : ctSqls)
							sqls.add(ctsql);
						continue;
					}
				}
			}
			try {
				//  GO_BY_STANDARD_PROCESS
				//  CREATE TABLE
				String TABLE_NAME = DataBaseFactory.getTableName(table);
				Map<String, String[]> cols = DataBaseFactory.getColumnDefinitions(table);
				if (null == cols || cols.size()==0) throw new IllegalStateException("Bad TableDefinition " + table.getClass().getName());

				String[] PRIMARY_KEYS = cols.get(PRIMARY_KEY);
				String[] COL_STR_NOTNULL = cols.get(STRING_NOTNULL);
				String[] COL_STR_NULL = cols.get(STRING_NULLABLE);
				String[] COL_NUM_NOTNULL = cols.get(NUMBER_NOTNULL);
				String[] COL_NUM_NULL = cols.get(NUMBER_NULLABLE);
				String[] COL_INT_NOTNULL = cols.get(INTEGER_NOTNULL);
				String[] COL_INT_NULL = cols.get(INTEGER_NULLABLE);

				StringBuilder sql = new StringBuilder("CREATE TABLE ").append(TABLE_NAME)
						.append(" (");
				if (null != COL_STR_NOTNULL)
					for (String col : COL_STR_NOTNULL)
						sql.append(col).append(" ").append(STRING_TYPE).append(" NOT NULL").append(COMMA_SEP);
				if (null != COL_STR_NULL)
					for (String col : COL_STR_NULL)
						sql.append(col).append(" ").append(STRING_TYPE).append(" ").append(COMMA_SEP);
				if (null != COL_NUM_NOTNULL)
					for (String col : COL_NUM_NOTNULL)
						sql.append(col).append(" ").append(NUMBER_TYPE).append(" NOT NULL").append(COMMA_SEP);
				if (null != COL_NUM_NULL)
					for (String col : COL_NUM_NULL)
						sql.append(col).append(" ").append(NUMBER_TYPE).append(" ").append(COMMA_SEP);
				if (null != COL_INT_NOTNULL)
					for (String col : COL_INT_NOTNULL)
						sql.append(col).append(" ").append(INTEGER_TYPE).append(" NOT NULL").append(COMMA_SEP);
				if (null != COL_INT_NULL)
					for (String col : COL_INT_NULL)
						sql.append(col).append(" ").append(INTEGER_TYPE).append(" ").append(COMMA_SEP);
				if (null != PRIMARY_KEYS) {
					boolean sec = false;
					sql.append(" PRIMARY KEY (");
					for (String col : PRIMARY_KEYS) {
						if (sec) sql.append(COMMA_SEP);
						sql.append(col);
						sec = true;
					}
					sql.append(")");
				} else {
					sql.deleteCharAt(sql.length()-1);   //  no declare primary key need to remove last COMMA_SEP
				}
				sql.append(" )");
				sqls.add(sql.toString());

				//  CREATE INDEX mytest_id_idx ON mytest(id);
				String[] IDXS = cols.get(IDX_LIST);
				if (null != IDXS) {
					for (String idx : IDXS) {
						String[] idx_cols = cols.get(idx);
						StringBuilder idxSql = new StringBuilder(" CREATE INDEX ").append(idx).append(" ON ").append(TABLE_NAME).append("(");
						boolean sec = false;
						for (String idx_col : idx_cols) {
							if (sec) idxSql.append(COMMA_SEP);
							idxSql.append(idx_col);
							sec = true;
						}
						idxSql.append(")");
						sqls.add(idxSql.toString());
					}
				}

			} catch (NoSuchFieldException|IllegalAccessException e) {
				throw new IllegalStateException("Bad TableDefinition " + table.getName());
			}
		}
		if (isLibTrace)
			LogUtility.debug(this, "createDB", " --> sqls " + sqls);
		return sqls.toArray(new String[sqls.size()]);
	}
	@Override
	public String[] dropDB(int... oldVersion) {
		if (isLibTrace)
			LogUtility.debug(this, "dropDB", " --> start ");
		ArrayList<String> sqls = new ArrayList<String>();
		for (Class<? extends TableDefinition> table : DataBaseFactory.getTables()) {
			try {
				String sql = table.newInstance().dropSql(oldVersion[0]);
				if (StringUtils.isBlank(sql) || SKIP_THIS_PROCESS_FOR_CURRENT_TABLE.equals(sql)) continue;
				else if (!GO_BY_STANDARD_PROCESS.equals(sql)) sqls.add(sql);
				else {
					sqls.add(" DROP TABLE IF EXISTS " + DataBaseFactory.getTableName(table));
				}
			} catch (InstantiationException|NoSuchFieldException|IllegalAccessException e) {
				throw new IllegalStateException("Bad TableDefinition " + table.getClass().getName());
			}
		}
		if (isLibTrace)
			LogUtility.debug(this, "dropDB", " --> end " + sqls);
		return sqls.toArray(new String[sqls.size()]);
	}
	@Override
	public DataBean[] keepData(int... oldVersion) {
		ArrayList<DataBean> keeps = new ArrayList<DataBean>();
		Class<? extends TableDefinition>[] tables = DataBaseFactory.getTables();
		for (Class<? extends TableDefinition> table : tables) {
			try {
				TableDefinition tObj = table.newInstance();
				DataBean[] ks = tObj.keepData(oldVersion[0]);
				if (ks!=null && ks.length>0) Collections.addAll(keeps, ks);
			} catch (InstantiationException|IllegalAccessException e) {
				LogUtility.warn(this, "keepData", " --> failure of " + table.getName(), e);
			}
		}
		return keeps.toArray(new DataBean[keeps.size()]);
	}

	public String alterTable(TableDefinition table, Field[] addCols) {
		return alterTable(table.getClass(), addCols);
	}
	@Override
	public String alterTable(Class<? extends TableDefinition> table, Field[] addCols) {
		//"ALTER TABLE foo ADD COLUMN new_column INTEGER DEFAULT 0"
		StringBuilder sql = new StringBuilder();
		try {
			String tableName = DataBaseFactory.getTableName(table);
			if (addCols!=null && addCols.length>0) {
				for (Field f : addCols) {
					String columnDeclare = columnDeclareType(f);
					String name = f.getName();
					if (null != columnDeclare) {
						sql.append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ")
								.append(name).append(columnDeclare).append(END_COMMAND);
					} else {
						throw new IllegalStateException("Bad Field Definition " + table.getName() + " Column " + name);
					}
				}
			}
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new IllegalStateException("Bad TableDefinition " + table.getName());
		}
		return sql.toString();
	}
	String columnDeclareType(Field field) {
		if (field.getName().startsWith(COLUMN_NAME_DECLARE_PREFIX)) {
			TableDefinition.TableColumn tc = DataBaseFactory.checkColumnDefinition(field);
			boolean notnull = !tc.nullable();
			if (tc != null) {
				switch (tc.type()) {
					case INTEGER:
						return " " + INTEGER_TYPE + (notnull ? " NOT NULL" : "");
					case NUMBER:
						return " " + NUMBER_TYPE + (notnull ? " NOT NULL" : "");
					default:
						return " " + STRING_TYPE + (notnull ? " NOT NULL" : "");
				}
			}
			return " " + STRING_TYPE;
		}
		return null;
	}

	/**
	 * 	// Gets the data repository in write mode
	 * 	SQLiteDatabase db = mDbHelper.getWritableDatabase();
	 * 	// Create a new map of values, where column names are the keys
	 * 	ContentValues values = new ContentValues();
	 * 	values.put(FeedEntry.COLUMN_NAME_ENTRY_ID, id);
	 * 	values.put(FeedEntry.COLUMN_NAME_TITLE, title);
	 * 	values.put(FeedEntry.COLUMN_NAME_CONTENT, content);
	 * 	// Insert the new row, returning the primary key value of the new row
	 * 	long newRowId;
	 * 	newRowId = db.insert(
	 *              FeedEntry.TABLE_NAME,
	 *              FeedEntry.COLUMN_NAME_NULLABLE,
	 *              values);
	 *
	 * @param data
	 * @param <T>
	 * @return
	 */
	@Override
	public <T> boolean insertRow(T data) {
		if (isLibTrace)
			LogUtility.debug(this, "insertRow", " --> start " + data);
		Class<? extends TableDefinition> tableDef = DataBaseFactory.findTableDefinition(data.getClass());
		if (null == tableDef) throw new IllegalArgumentException("Can not found target TableDefinition ");
		String tableName = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			return false;
		}
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		DataBaseFactory.TableSpy<T> dataSpy = new DataBaseFactory.TableSpy(data.getClass());
		String[] strCols = dataSpy.getStringColNames();
		for (String col : strCols) {
			values.put(col, dataSpy.getString(data, col));
		}
		String[] intCols = dataSpy.getIntColNames();
		for (String col : intCols) {
			values.put(col, dataSpy.getInt(data, col));
		}
		String[] numCols = dataSpy.getNumColNames();
		for (String col : numCols) {
			values.put(col, dataSpy.getNum(data, col));
		}
		 long newRowId = db.insert(tableName, null, values);

		// INSERT INTO TABLE (COL_1, COL_2) VALUES (VAL_1, VAL_2);
		if (isLibTrace)
			LogUtility.debug(this, "insertRow", " --> end ");
		return newRowId>0;
	}

	/**
	 * 更新資料庫
	 * 若您需要修改資料庫值的子集，請使用 update() 方法。
	 * 更新表格會合併 insert() 的內容值語法與 delete() 的 where 語法。
	 * SQLiteDatabase db = mDbHelper.getReadableDatabase();
	 * // New value for one column
	 * ContentValues values = new ContentValues();
	 * values.put(FeedEntry.COLUMN_NAME_TITLE, title);
	 * // Which row to update, based on the ID
	 * String selection = FeedEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
	 * String[] selectionArgs = { String.valueOf(rowId) };
	 * int count = db.update(
	 *      FeedReaderDbHelper.FeedEntry.TABLE_NAME,
	 *      values,
	 *      selection,
	 *      selectionArgs);
	 *
	 * @param data
	 * @param <T>
	 * @return
	 */
	@Override
	public <T> boolean updateRow(T data) {
		if (isLibTrace)
			LogUtility.debug(this, "updateRow", " --> start " + data);
		Class<? extends TableDefinition> tableDef = DataBaseFactory.findTableDefinition(data.getClass());
		if (null == tableDef) throw new IllegalArgumentException("Can not found target TableDefinition ");
		String tableName = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			return false;
		}
		DataBaseFactory.TableSpy<T> dataSpy = new DataBaseFactory.TableSpy(data.getClass());
		String[] pks = dataSpy.getPrimaryKeyColNames();
		if (pks == null || pks.length==0) throw new IllegalStateException("Table " + tableName + " without primary key can not update by this method ");
		SQLiteDatabase db = getWritableDatabase();
		// New value for one column
		ContentValues values = new ContentValues();
		String[] strCols = dataSpy.getStringColNames();
		for (String col : strCols) {
			values.put(col, dataSpy.getString(data, col));
		}
		String[] intCols = dataSpy.getIntColNames();
		for (String col : intCols) {
			values.put(col, dataSpy.getInt(data, col));
		}
		String[] numCols = dataSpy.getNumColNames();
		for (String col : numCols) {
			values.put(col, dataSpy.getNum(data, col));
		}
		// Which row to update, based on the ID
		StringBuilder selection = new StringBuilder();
		String[] selectionArgs = new String[pks.length];
		for (int i=0; i<pks.length; i++) {
			String pk = pks[i];
			if (i>0) selection.append(" AND ");
			selection.append(pk).append(" = ?");
			switch (dataSpy.checkColumnType(pk)) {
				case STRING:
					selectionArgs[i] = dataSpy.getString(data, pk); break;
				case INTEGER:
					selectionArgs[i] = formatIntValue(dataSpy.getInt(data, pk)); break;
				case NUMBER:
					selectionArgs[i] = formatNumValue(dataSpy.getNum(data, pk)); break;
				default:
					throw new IllegalStateException("Unknown Column " + pk + " for table " + tableName);
			}
		}
		int count = db.update(tableName, values, selection.toString(), selectionArgs);
		if (isLibTrace)
			LogUtility.debug(this, "updateRow", " --> end ");
		return count>0;
	}

	/**
	 * 刪除資料庫中的資訊
	 * 若要刪除表格中的列，您需要提供識別這些列的選取條件。 資料庫 API 可提供建立選取條件的機制 (能防止 SQL 插入)。
	 * 該機制會將選取規格分為選取子句與選取引數。 子句可定義要查看的欄，您也可以藉此合併欄測試。
	 * 引數是要測試的值，繫結在子句中。由於對結果的處理方式不同於規則 SQL 陳述式，因此結果不會遭受 SQL 插入。
	 * // Define 'where' part of query.
	 * String selection = FeedEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
	 * // Specify arguments in placeholder order.
	 * String[] selectionArgs = { String.valueOf(rowId) };
	 * // Issue SQL statement.
	 * db.delete(table_name, selection, selectionArgs);
	 *
	 * @param data
	 * @param <T>
	 * @return
	 */
	@Override
	public <T> boolean deleteRow(T data) {
		if (isLibTrace)
			LogUtility.debug(this, "deleteRow", " --> start " + data);
		Class<? extends TableDefinition> tableDef = DataBaseFactory.findTableDefinition(data.getClass());
		if (null == tableDef) throw new IllegalArgumentException("Can not found target TableDefinition ");
		String tableName = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			return false;
		}
		DataBaseFactory.TableSpy<T> dataSpy = new DataBaseFactory.TableSpy(data.getClass());
		String[] pks = dataSpy.getPrimaryKeyColNames();
		if (pks == null || pks.length==0) throw new IllegalStateException("Table " + tableName + " without primary key can not update by this method ");
		SQLiteDatabase db = getWritableDatabase();
		// Which row to update, based on the ID
		StringBuilder selection = new StringBuilder();
		String[] selectionArgs = new String[pks.length];
		for (int i=0; i<pks.length; i++) {
			String pk = pks[i];
			if (i>0) selection.append(" AND ");
			selection.append(pk).append(" = ?");
			switch (dataSpy.checkColumnType(pk)) {
				case STRING:
					selectionArgs[i] = dataSpy.getString(data, pk); break;
				case INTEGER:
					selectionArgs[i] = formatIntValue(dataSpy.getInt(data, pk)); break;
				case NUMBER:
					selectionArgs[i] = formatNumValue(dataSpy.getNum(data, pk)); break;
				default:
					throw new IllegalStateException("Unknown Column " + pk + " for table " + tableName);
			}
		}
		if (isLibTrace)
			LogUtility.debug(this, "deleteRow", " --> trace >> table:" + tableName
				+ "\n selection:" + selection.toString()
				+ "\n selectionArgs:" + (selectionArgs!=null?selectionArgs.length:0));
		int count = db.delete(tableName, selection.toString(), selectionArgs);
		if (isLibTrace)
			LogUtility.debug(this, "deleteRow", " --> end ");
		return count>0;
	}

	@Override
	public <T> int insertRows(List<T> datas) {
		int count = 0;
		int i=0;
		try {
			for (; i<datas.size(); i++) {
				T data = datas.get(i);
				if (insertRow(data)) count++;
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("processing at item " + i + " and already finishing " + count + " rows", e);
		}
		return count;
	}
	@Override
	public <T> int insertRows(T... datas) {
		int count = 0;
		int i=0;
		try {
			for (; i<datas.length; i++) {
				if (insertRow(datas[i])) count++;
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("processing at item " + i + " and already finishing " + count + " rows", e);
		}
		return count;
	}

	@Override
	public <T> int updateRows(List<T> datas) {
		int count = 0;
		int i=0;
		try {
			for (; i<datas.size(); i++) {
				T data = datas.get(i);
				if (updateRow(data)) count++;
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("processing at item " + i + " and already finishing " + count + " rows", e);
		}
		return count;
	}
	@Override
	public <T> int updateRows(T... datas) {
		int count = 0;
		int i=0;
		try {
			for (; i<datas.length; i++) {
				if (updateRow(datas[i])) count++;
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("processing at item " + i + " and already finishing " + count + " rows", e);
		}
		return count;
	}
	@Override
	public int updateRows(Class<? extends TableDefinition> tableDef, String[] updateCols, PARAM[] updateVals, String[] pkCols, String[]... values) {
		String tableName = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			return -1;
		}
		SQLiteDatabase db = getWritableDatabase();
		// New value for one column
		ContentValues updValues = new ContentValues();
		for (int i=0; i<updateCols.length; i++) {
			PARAM updVal = updateVals[i];
			switch (updVal.TYPE) {
				case INTEGER:
					updValues.put(updateCols[i], updVal.INT_VALUE); break;
				case NUMBER:
					updValues.put(updateCols[i], updVal.NUM_VALUE); break;
				case STRING:
				default:
					updValues.put(updateCols[i], updVal.VALUE); break;
			}
		}
		// Which row to update, based on the ID
		StringBuilder selection = new StringBuilder();
		ArrayList<String> selectionArgs = new ArrayList<String>();
		for (int i=0; i<pkCols.length; i++) {
			String pk = pkCols[i];
			if (i>0) selection.append(" AND ");
			if (values[i].length==1) {
				selection.append(pk).append(" = ?");
				selectionArgs.add(values[i][0]);
			} else {
				selection.append(pk).append(" IN (");
				boolean notFirst = false;
				for (String pkv : values[i]) {
					if (notFirst) selection.append(", ");
					selection.append("?");
					selectionArgs.add(pkv);
					notFirst = true;
				}
				selection.append(")");
			}
		}
		int count = db.update(tableName, updValues, selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]));
		if (isLibTrace)
			LogUtility.debug(this, "updateRow", " --> end ");
		return count;
	}

	@Override
	public <T> int deleteRows(List<T> datas) {
		if (null == datas) return 0;
		return deleteRows(datas.toArray(new Object[datas.size()]));
	}
	@Override
	public <T> int deleteRows(T... datas) {
		if (isLibTrace)
			LogUtility.debug(this, "deleteRows", " --> start ");
		if (null == datas || datas.length==0) return 0;

		Class<? extends TableDefinition> tableDef = DataBaseFactory.findTableDefinition(datas[0].getClass());
		if (null == tableDef) throw new IllegalArgumentException("Can not found target TableDefinition ");
		String tableName = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to get TableDefinition ", e);
		}
		DataBaseFactory.TableSpy<T> dataSpy = new DataBaseFactory.TableSpy(datas[0].getClass());
		String[] pks = dataSpy.getPrimaryKeyColNames();
		if (pks == null || pks.length==0) throw new IllegalStateException("Table " + tableName + " without primary key can not update by this method ");

		SQLiteDatabase db = getWritableDatabase();
		// Which row to update, based on the ID
		StringBuilder selection = new StringBuilder();
		String[] selectionArgs = new String[pks.length*datas.length];
		for (int j=0; j<datas.length; j++) {
			if (j>0) selection.append(" OR ");
			selection.append("(");
			T data = datas[j];
			for (int i=0; i<pks.length; i++) {
				String pk = pks[i];
				if (i>0) selection.append(" AND ");
				selection.append(pk).append(" = ?");
				switch (dataSpy.checkColumnType(pk)) {
					case STRING:
						selectionArgs[i] = dataSpy.getString(data, pk); break;
					case INTEGER:
						selectionArgs[i] = formatIntValue(dataSpy.getInt(data, pk)); break;
					case NUMBER:
						selectionArgs[i] = formatNumValue(dataSpy.getNum(data, pk)); break;
					default:
						throw new IllegalStateException("Unknown Column " + pk + " for table " + tableName);
				}
			}
			selection.append(")");
		}
		if (isLibTrace)
			LogUtility.debug(this, "deleteRows", " --> trace >> table:" + tableName
					+ "\n select:" + selection.toString()
					+ "\n selectArgs:" + (selectionArgs!=null?selectionArgs.length:0));
		int count = db.delete(tableName, selection.toString(), selectionArgs);
		if (isLibTrace)
			LogUtility.debug(this, "deleteRows", " --> end ");
		return count;
	}

	@Override
	public int deleteRows(Class<? extends TableDefinition> tableDef, List<Map<String, String>> conds) {
		if (null == conds || conds.size()==0) return 0;
		return deleteRows(tableDef, conds.toArray(new Map[conds.size()]));
	}

	@Override
	public int deleteRows(Class<? extends TableDefinition> tableDef, Map<String, String>... conds) {
		if (null == conds || conds.length==0) return 0;
		if (isLibTrace)
			LogUtility.debug(this, "deleteRows", " --> start " + conds);

		if (null == tableDef) throw new IllegalArgumentException("Can not found target TableDefinition ");
		String tableName = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to get TableDefinition ", e);
		}

		SQLiteDatabase db = getWritableDatabase();
		// Which row to update, based on the ID
		StringBuilder selection = new StringBuilder();
		ArrayList<String> args = new ArrayList<String>();
		for (int j=0; j<conds.length; j++) {
			if (j>0) selection.append(" OR ");
			selection.append("(");
			Map cond = conds[j];
			Iterator cols = cond.keySet().iterator();
			boolean sec = false;
			while(cols.hasNext()) {
				String col = cols.next().toString();
				if (sec) selection.append(" AND ");
				sec = true;
				selection.append(col).append(" = ?");
				args.add(cond.get(col).toString());
			}
			selection.append(")");
		}
		if (isLibTrace)
			LogUtility.debug(this, "deleteRows", " --> trace >> table:" + tableName
					+ "\n select:" + selection.toString()
					+ "\n selectArgs:" + (args!=null?args.size():0));
		int count = db.delete(tableName, selection.toString(), args.toArray(new String[args.size()]));
		if (isLibTrace)
			LogUtility.debug(this, "deleteRows", " --> end ");
		return count;
	}
	@Override
	public int deleteRows(Class<? extends TableDefinition> tableDef, String[] pkCols, String[]... values) {
		if (null == pkCols || pkCols.length==0 || values == null || values.length==0) return 0;
		if (isLibTrace)
			LogUtility.debug(this, "deleteRows", " --> start " + values.length);

		if (null == tableDef) throw new IllegalArgumentException("Can not found target TableDefinition ");
		String tableName = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to get TableDefinition ", e);
		}

		SQLiteDatabase db = getWritableDatabase();
		// Which row to update, based on the ID
		StringBuilder selection = new StringBuilder();
		ArrayList<String> args = new ArrayList<String>();
		if (pkCols.length == 1) {
			if (values.length == 1) {
				selection.append(pkCols[0]).append("=?");
				args.add(values[0][0]);
			} else {
				selection.append(pkCols[0]).append(" IN (");
				boolean sec = false;
				for (String[] val : values) {
					if (sec) selection.append(", ? ");
					else selection.append("?");
					sec = true;
					args.add(val[0]);
				}
				selection.append(")");
			}
		} else {
			for (int j=0; j<values.length; j++) {
				if (j>0) selection.append(" OR ");
				selection.append("(");
				String[] cond = values[j];
				boolean sec = false;
				for (int i=0; i<pkCols.length; i++) {
					String pk = pkCols[i];
					if (sec) selection.append(" AND ");
					sec = true;
					selection.append(pk).append(" = ?");
					args.add(values[j][i]);
				}
				selection.append(")");
			}
		}
		if (isLibTrace)
			LogUtility.debug(this, "deleteRows", " --> trace >> table:" + tableName
				+ "\n select:" + selection.toString()
				+ "\n selectArgs:" + (args!=null?args.size():0));
		int count = db.delete(tableName, selection.toString(), args.toArray(new String[args.size()]));
		if (isLibTrace)
			LogUtility.debug(this, "deleteRows", " --> end ");
		return count;
	}

	public List<Map> queryDataByTable(Class<? extends TableDefinition> tableDef) {
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> start ");
		// Define a projection that specifies which columns from the database
		if (null==tableDef) throw new IllegalArgumentException("Can not found target TableDefinition ");

		String tableName = null;
		String[] cols = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
			cols = DataBaseFactory.getColumns(tableDef);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to get TableDefinition ", e);
		}
		// you will actually use after this query.
		String[] projection = cols;
		String selection = null;
		String[] selectionArgs = null;
		// How you want the results sorted in the resulting Cursor
		String sortOrder = null;
		SQLiteDatabase db = getReadableDatabase();
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> trace >> table:" + tableName + " all in list of map");
		Cursor c = db.query(tableName,  // The table to query
				projection, // The columns to return
				selection,                                // The columns for the WHERE clause
				selectionArgs,                            // The values for the WHERE clause
				null,                                     // don't group the rows
				null,                                     // don't filter by row groups
				sortOrder                                 // The sort order
		);
		List<Map> rs = null;
		try {
			rs = loadResult(c);
		} catch (InstantiationException|IllegalAccessException e) {
			throw new IllegalStateException("Can not new instance of " + tableName);
		} finally {
			c.close();
		}
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> end ");
		return rs;
	}
	/**
	 *  讀取資料庫中的資訊
	 *  若要從資料庫進行讀取，請使用 query() 方法，然後向其傳遞您的選取條件與所需的欄。
	 *  該方法會合併 insert() 與 update() 的元素，對您希望擷取的資料 (而非要插入的資料) 進行定義的欄清單除外。 將在 Cursor 物件中，為您傳回查詢結果。
	 *  SQLiteDatabase db = mDbHelper.getReadableDatabase();
	 *  // Define a projection that specifies which columns from the database
	 *  // you will actually use after this query.
	 *  String[] projection = {
	 *      FeedEntry._ID,
	 *      FeedEntry.COLUMN_NAME_TITLE,
	 *      FeedEntry.COLUMN_NAME_UPDATED,
	 *      ...
	 *      };
	 *  // How you want the results sorted in the resulting Cursor
	 *  String sortOrder =
	 *      FeedEntry.COLUMN_NAME_UPDATED + " DESC";
	 *  Cursor c = db.query(
	 *          FeedEntry.TABLE_NAME,  // The table to query
	 *          projection,                               // The columns to return
	 *          selection,                                // The columns for the WHERE clause
	 *          selectionArgs,                            // The values for the WHERE clause
	 *          null,                                     // don't group the rows
	 *          null,                                     // don't filter by row groups
	 *          sortOrder                                 // The sort order
	 *  );
	 *  若要查看游標指示的列，請使用其中一種 Cursor move 方法，您必須始終先呼叫該方法，然後再開始讀取值。
	 *  一般而言，您應呼叫 moveToFirst() 來執行啟動，如此會將「讀取位置」置於結果中的第一個項目。
	 *  對於每列，您可以呼叫其中一種 Cursor get 方法 (例如 getString() 或 getLong())，以讀取欄的值。
	 *  對於每種 get 方法，您必須傳遞所需欄的索引位置，可以呼叫 getColumnIndex() 或 getColumnIndexOrThrow() 取得該位置。
	 *  例如：
	 *  cursor.moveToFirst();
	 *  long itemId = cursor.getLong(
	 *      cursor.getColumnIndexOrThrow(FeedEntry._ID)
	 *  );
	 * @param table
	 * @param <T>
	 * @return
	 */
	@Override
	public <T> List<T> queryDataByTable(Class<T> tClass, Class<? extends TableDefinition> table) {
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> start ");
		// Define a projection that specifies which columns from the database
		Class<? extends TableDefinition> tableDef = DataBaseFactory.findTableDefinition(tClass);
		if (null==tableDef && table==null) throw new IllegalArgumentException("Can not found target TableDefinition ");
		if (table!=null && !table.equals(tableDef)) throw new IllegalArgumentException("No matched target TableDefinition " + tableDef);
		String tableName = null;
		DataBaseFactory.TableSpy spy = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
			spy = new DataBaseFactory.TableSpy(tClass);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to get TableDefinition ", e);
		}
		// you will actually use after this query.
		String[] projection = spy.getColNames();
		String selection = null;
		String[] selectionArgs = null;
		// How you want the results sorted in the resulting Cursor
		String sortOrder = null;
		SQLiteDatabase db = getReadableDatabase();
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> trace >> table:" + tableName
				+ "\n tClass:" + tClass.getName());
		Cursor c = db.query(tableName,  // The table to query
							projection, // The columns to return
							selection,                                // The columns for the WHERE clause
							selectionArgs,                            // The values for the WHERE clause
							null,                                     // don't group the rows
							null,                                     // don't filter by row groups
							sortOrder                                 // The sort order
			);
		List<T> rs = null;
		try {
			rs = loadResult(tClass, c, spy);
		} catch (InstantiationException|IllegalAccessException e) {
			throw new IllegalStateException("Can not new instance of " + tClass.getName());
		} finally {
			c.close();
		}
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> end ");
		return rs;
	}
	private <T> List<T> loadResult(Class<T> tClass, Cursor c, DataBaseFactory.TableSpy<T> spy) throws IllegalAccessException, InstantiationException {
		ArrayList<T> rs = new ArrayList<T>();
		if (	c.getCount()>0) {
			c.moveToFirst();
			String[] cols = c.getColumnNames();
			do {
				T row = tClass.newInstance();
				for (int cIdx=0; cIdx<cols.length; cIdx++) {
					TableDefinition.DATA_TYPE dt = spy.checkColumnType(cols[cIdx]);
					String str = "";
//					if (isLibTrace)
//						LogUtility.debug(this, "[loadResult] " + cols[cIdx] + " of " + dt);
					if (null == dt) {
						//  skip value because can not find the field mapping
					} else {
						switch(dt) {
							case INTEGER:
								spy.setColValue(row, cols[cIdx], c.getInt(cIdx)); str = String.valueOf(c.getInt(cIdx)); break;
							case NUMBER:
								spy.setColValue(row, cols[cIdx], c.getDouble(cIdx)); str = String.valueOf(c.getDouble(cIdx)); break;
							case STRING:
								spy.setColValue(row, cols[cIdx], c.getString(cIdx)); str = c.getString(cIdx); break;
						}
					}
//					if (isLibTrace)
//						LogUtility.debug(this, "[loadResult] " + cols[cIdx] + " of " + dt + "=" + str);
				}
				rs.add(row);
			} while (c.moveToNext());
		}
		return rs;
	}
	private <T> List<T> loadResult(Class<T> tClass, Cursor c) throws IllegalAccessException, InstantiationException {
		ArrayList<T> rs = new ArrayList<T>();
		if (	c.getCount()>0) {
			c.moveToFirst();
			String[] cols = c.getColumnNames();
			Field[] fields = tClass.getFields();
			HashMap<String, Field> cMap = new HashMap<String, Field>();
			HashMap<String, TableDefinition.DATA_TYPE> tMap = new HashMap<String, TableDefinition.DATA_TYPE>();
			do {
				T row = tClass.newInstance();
				for (int cIdx=0; cIdx<cols.length; cIdx++) {
					TableDefinition.DATA_TYPE dt = tMap.get(cols[cIdx]);
					if (dt==null) {
						Field cf = null;
						for (Field f : fields)
							if (f.getName().equals(cols[cIdx]))
								cf = f;
						Type type = cf.getType();

						if (Integer.TYPE.equals(type) || Short.TYPE.equals(type)) {
							dt = INTEGER;
						} else if (Double.TYPE.equals(type) || Float.TYPE.equals(type) || Long.TYPE.equals(type)) {
							dt = NUMBER;
						} else {
							dt = STRING;
						}
						tMap.put(cols[cIdx], dt);
						cMap.put(cols[cIdx], cf);
					}
					Field f = cMap.get(cols[cIdx]);
					switch(dt) {
						case INTEGER:
							f.setInt(row, c.getInt(cIdx)); break;
						case NUMBER:
							f.setDouble(row, c.getDouble(cIdx)); break;
						case STRING:
							f.set(row, c.getString(cIdx)); break;
					}
				}
				rs.add(row);
			} while (c.moveToNext());
		}
		return rs;
	}
	private List<Map> loadResult(Cursor c) throws IllegalAccessException, InstantiationException {
		ArrayList<Map> rs = new ArrayList<Map>();
		if (	c.getCount()>0) {
			c.moveToFirst();
			String[] cols = c.getColumnNames();
			HashMap<String, Field> cMap = new HashMap<String, Field>();
			HashMap<String, TableDefinition.DATA_TYPE> tMap = new HashMap<String, TableDefinition.DATA_TYPE>();
			do {
				Map row = new HashMap();
				for (int cIdx=0; cIdx<cols.length; cIdx++) {
					String colName = cols[cIdx];
					switch(c.getType(cIdx)) {
						case Cursor.FIELD_TYPE_INTEGER:
							row.put(colName, c.getInt(cIdx)); break;
						case Cursor.FIELD_TYPE_FLOAT:
							row.put(colName, c.getDouble(cIdx)); break;
						case Cursor.FIELD_TYPE_STRING:
							row.put(colName, c.getString(cIdx)); break;
						case Cursor.FIELD_TYPE_NULL:
							row.put(colName, null); break;
					}
				}
				rs.add(row);
			} while (c.moveToNext());
		}
		return rs;
	}
	enum SQL_OPS {
		GROUP_BY, ORDER_BY
	}
	private String sqlStmtBuilder(SQL_OPS ops, String[] cols) {
		StringBuilder sql = new StringBuilder();
		boolean sec = false;
		for (String col : cols) {
			if (sec) sql.append(COMMA_SEP);
			sec = true;
			sql.append(col);
		}
		return sql.toString();
	}

	@Override
	public <T> List<T> queryDataByTable(Class<T> tClass, String table, String[] distinct_cols, String[] orders) {
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> start ");
		// Define a projection that specifies which columns from the database
		Class<? extends TableDefinition> tableDef = DataBaseFactory.findTableDefinition(tClass);
		if (null==tableDef && StringUtils.isBlank(table)) throw new IllegalArgumentException("Can not found target TableDefinition ");
		String tableName = null;
		DataBaseFactory.TableSpy spy = null;
		try {
			if (tableDef!=null) {
				tableName = DataBaseFactory.getTableName(tableDef);
				spy = new DataBaseFactory.TableSpy(tClass);
			} else {
				tableName = table;
			}
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to get TableDefinition ", e);
		}
		if (!StringUtils.isBlank(table) && !table.equals(tableName)) throw new IllegalArgumentException("No matched target Table " + tableName);
		// you will actually use after this query.
		String[] projection = distinct_cols;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		if (distinct_cols!=null && distinct_cols.length>0) groupBy = sqlStmtBuilder(SQL_OPS.GROUP_BY, distinct_cols);
		String having = null;
		// How you want the results sorted in the resulting Cursor
		String sortOrder = null;
		if (orders!=null && orders.length>0) sortOrder = sqlStmtBuilder(SQL_OPS.ORDER_BY, orders);
		String limit = null;    //  as no limit
		SQLiteDatabase db = getReadableDatabase();
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> trace >> table:" + tableName
				+ "\n selection:" + selection
				+ "\n selectionArgs:" + (selectionArgs!=null ? selectionArgs : 0)
				+ "\n groupBy:" + groupBy
				+ "\n sortOrder:" + sortOrder);
		Cursor c = db.query(true, tableName,  // The table to query
				projection, // The columns to return
				selection,                                // The columns for the WHERE clause
				selectionArgs,                            // The values for the WHERE clause
				groupBy,                                     // don't group the rows
				having,                                     // don't filter by row groups
				sortOrder                                 // The sort order
				, limit
		);
		List<T> rs = null;
		try {
			if (spy != null) {
				rs = loadResult(tClass, c, spy);
			} else {
				rs = loadResult(tClass, c);
			}
		} catch (InstantiationException|IllegalAccessException e) {
			throw new IllegalStateException("Can not new instance of " + tClass.getName());
		} finally {
			c.close();
		}
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> end ");
		return rs;
	}

	@Override
	public <T> List<T> queryDataByTable(Class<T> tClass, Class<? extends TableDefinition> table, String whereStmt, PARAM... params) {
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> start ");
		// Define a projection that specifies which columns from the database
		Class<? extends TableDefinition> tableDef = DataBaseFactory.findTableDefinition(tClass);
		if (null==tableDef && table==null) throw new IllegalArgumentException("Can not found target TableDefinition ");
		if (table!=null && !table.equals(tableDef)) throw new IllegalArgumentException("No matched target TableDefinition " + tableDef);
		String tableName = null;
		DataBaseFactory.TableSpy spy = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
			spy = new DataBaseFactory.TableSpy(tClass);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to get TableDefinition ", e);
		}
		// you will actually use after this query.
		String[] projection = spy.getColNames();
		String selection = null;
		String[] selectionArgs = null;
		if (!StringUtils.isBlank(whereStmt)) {
			selection = whereStmt;
			selectionArgs = new String[params.length];
			for (int i=0; i<params.length; i++) {
				if (StringUtils.isBlank(params[i].VALUE)) {
					switch(params[i].TYPE) {
						case INTEGER:
							selectionArgs[i] = INT_FORMAT.format(params[i].INT_VALUE); break;
						case NUMBER:
							selectionArgs[i] = NUM_FORMAT.format(params[i].NUM_VALUE); break;
						case STRING:
							selectionArgs[i] = params[i].VALUE;
					}
				} else {
					selectionArgs[i] = params[i].VALUE;
				}
			}
		}
		String groupBy = null;
		String having = null;
		// How you want the results sorted in the resulting Cursor
		String sortOrder = null;
		SQLiteDatabase db = getReadableDatabase();
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> trace >> table:" + tableName
				+ "\n selection:" + selection
				+ "\n selectionArgs" + (selectionArgs!=null?selectionArgs.length:0));
		Cursor c = db.query(tableName,  // The table to query
				projection, // The columns to return
				selection,                                // The columns for the WHERE clause
				selectionArgs,                            // The values for the WHERE clause
				groupBy,                                     // don't group the rows
				having,                                     // don't filter by row groups
				sortOrder                                 // The sort order
		);
		List<T> rs = null;
		try {
			rs = loadResult(tClass, c, spy);
		} catch (InstantiationException|IllegalAccessException e) {
			throw new IllegalStateException("Can not new instance of " + tClass.getName());
		} finally {
			c.close();
		}
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByTable", " --> end ");
		return rs;
	}
	@Override
	public <T> List<T> queryDataByCols(Class<T> tClass, Class<? extends TableDefinition> table, String[] cols, String... values) {
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByCols", " --> start ");
		// Define a projection that specifies which columns from the database
		Class<? extends TableDefinition> tableDef = DataBaseFactory.findTableDefinition(tClass);
		if (null==tableDef && table==null) throw new IllegalArgumentException("Can not found target TableDefinition ");
		if (table!=null && !table.equals(tableDef)) throw new IllegalArgumentException("No matched target TableDefinition " + tableDef);
		String tableName = null;
		DataBaseFactory.TableSpy spy = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
			spy = new DataBaseFactory.TableSpy(tClass);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to get TableDefinition ", e);
		}
		// you will actually use after this query.
		String[] projection = spy.getColNames();
		String selection = null;
		String[] selectionArgs = null;
		if (cols!=null && cols.length>0) {
			StringBuilder whereStmt = new StringBuilder();
			selectionArgs = values;
			for (String col : cols) {
				if (whereStmt.length()>0) whereStmt.append(" AND ");
				whereStmt.append(col).append("= ?");
			}
			selection = whereStmt.toString();
		}
		String groupBy = null;
		String having = null;
		// How you want the results sorted in the resulting Cursor
		String sortOrder = null;
		SQLiteDatabase db = getReadableDatabase();
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByCols", " --> trace >> table:" + tableName
				+ "\n selection:" + selection
				+ "\n selectionArgs:" + (selectionArgs!=null?selectionArgs.length:0));
		Cursor c = db.query(tableName,  // The table to query
				projection, // The columns to return
				selection,                                // The columns for the WHERE clause
				selectionArgs,                            // The values for the WHERE clause
				groupBy,                                     // don't group the rows
				having,                                     // don't filter by row groups
				sortOrder                                 // The sort order
		);
		List<T> rs = null;
		try {
			rs = loadResult(tClass, c, spy);
		} catch (InstantiationException|IllegalAccessException e) {
			throw new IllegalStateException("Can not new instance of " + tClass.getName());
		} finally {
			c.close();
		}
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByCols", " --> end ");
		return rs;
	}
	public <T> List<T> queryDataByCols(Class<T> tClass, Class<? extends TableDefinition> table, String[] cols, PARAM... values) {
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByCols", " --> start ");
		// Define a projection that specifies which columns from the database
		Class<? extends TableDefinition> tableDef = DataBaseFactory.findTableDefinition(tClass);
		if (null==tableDef && table==null) throw new IllegalArgumentException("Can not found target TableDefinition ");
		if (table!=null && !table.equals(tableDef)) throw new IllegalArgumentException("No matched target TableDefinition " + tableDef);
		String tableName = null;
		DataBaseFactory.TableSpy spy = null;
		try {
			tableName = DataBaseFactory.getTableName(tableDef);
			spy = new DataBaseFactory.TableSpy(tClass);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to get TableDefinition ", e);
		}
		// you will actually use after this query.
		String[] projection = spy.getColNames();
		String selection = null;
		String[] selectionArgs = null;
		ArrayList<String> args = new ArrayList<String>();
		if (cols!=null && cols.length>0) {
			StringBuilder whereStmt = new StringBuilder();
			for (int i=0; i<cols.length; i++) {
				String col = cols[i];
				if (whereStmt.length()>0) whereStmt.append(" AND ");
				whereStmt.append(col);
				if (values[i].isSingleValue()) {
					whereStmt.append("= ?");
					switch (values[i].TYPE) {
						case INTEGER:
							args.add(formatIntValue(values[i].INT_VALUE)); break;
						case NUMBER:
							args.add(formatNumValue(values[i].NUM_VALUE)); break;
						default:
							args.add(values[i].VALUE);
					}
				} else {
					whereStmt.append(" IN (");
					for (int x=0; x<values[i].valueCount(); x++) {
						if (x>0) whereStmt.append(", ");
						whereStmt.append("?");
						String xStr = "";
						switch(values[i].TYPE) {
							case INTEGER:
								xStr = formatIntValue(values[i].INT_VALUES[x]); break;
							case NUMBER:
								xStr = formatNumValue(values[i].NUM_VALUES[x]); break;
							default:
								xStr = values[i].VALUES[x];
						}
						args.add(xStr);
					}
					whereStmt.append(") ");
				}
			}
			selection = whereStmt.toString();
			selectionArgs = args.toArray(new String[args.size()]);
		}
		String groupBy = null;
		String having = null;
		// How you want the results sorted in the resulting Cursor
		String sortOrder = null;
		SQLiteDatabase db = getReadableDatabase();
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByCols", " --> trace >> table:" + tableName
				+ "\n selection:" + selection
				+ "\n selectionArgs:" + (selectionArgs!=null?selectionArgs.length:0));
		Cursor c = db.query(tableName,  // The table to query
				projection, // The columns to return
				selection,                                // The columns for the WHERE clause
				selectionArgs,                            // The values for the WHERE clause
				groupBy,                                     // don't group the rows
				having,                                     // don't filter by row groups
				sortOrder                                 // The sort order
		);
		List<T> rs = null;
		try {
			rs = loadResult(tClass, c, spy);
		} catch (InstantiationException|IllegalAccessException e) {
			throw new IllegalStateException("Can not new instance of " + tClass.getName());
		} finally {
			c.close();
		}
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByCols", " --> end ");
		return rs;
	}

	/**
	 * Normally when you run a query, you want to get a cursor back with lots of rows. That's not what SQLiteStatement is for, though.
	 * You don't run a query with it unless you only need a simple result, like the number of rows in the database, which you can do with simpleQueryForLong()
	 * String sql = "SELECT COUNT(*) FROM table_name";
	 * SQLiteStatement statement = db.compileStatement(sql);
	 * long result = statement.simpleQueryForLong();
	 * Usually you will run the query() method of SQLiteDatabase to get a cursor.
	 * SQLiteDatabase db = dbHelper.getReadableDatabase();
	 * String table = "table_name";
	 * String[] columnsToReturn = { "column_1", "column_2" };
	 * String selection = "column_1 =?";
	 * String[] selectionArgs = { someValue }; // matched to "?" in selection
	 * Cursor dbCursor = db.query(table, columnsToReturn, selection, selectionArgs, null, null, null);
	 * See this answer for better details about queries.
	 * @param tClass
	 * @param sqlStmt
	 * @param params
	 * @param <T>
	 * @return
	 */
	@Override
	public <T> List<T> queryDataByStmt(Class<T> tClass, String sqlStmt, PARAM... params) {
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByStmt", " --> start ");
		// Define a projection that specifies which columns from the database
		Class<? extends TableDefinition> tableDef = DataBaseFactory.findTableDefinition(tClass);
		DataBaseFactory.TableSpy spy = null;
		if (tableDef != null) {
			spy = new DataBaseFactory.TableSpy(tClass);
		}
		// you will actually use after this query.
		String tableName = spy.getTableName();
		ArrayList<String> args = new ArrayList<String>();
		for (int i=0; i<params.length; i++) {
			if (StringUtils.isBlank(params[i].VALUE)) {
				if (params[i].isSingleValue()) {
					switch(params[i].TYPE) {
						case INTEGER:
							args.add(formatIntValue(params[i].INT_VALUE)); break;
						case NUMBER:
							args.add(formatNumValue(params[i].NUM_VALUE)); break;
						case STRING:
							args.add(params[i].VALUE);
					}
				} else {
					for (int x=0; x<params[i].valueCount(); x++) {
						String xStr = "";
						switch(params[i].TYPE) {
							case INTEGER:
								xStr = formatIntValue(params[i].INT_VALUES[x]); break;
							case NUMBER:
								xStr = formatNumValue(params[i].NUM_VALUES[x]); break;
							default:
								xStr = params[i].VALUES[x];
						}
						args.add(xStr);
					}
				}
			} else {
				args.add(params[i].VALUE);
			}
		}
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(sqlStmt, args.toArray(new String[args.size()]));
		List<T> rs = null;
		try {
			if (spy != null) {
				rs = loadResult(tClass, c, spy);
			} else {
				rs = loadResult(tClass, c);
			}
		} catch (InstantiationException|IllegalAccessException e) {
			throw new IllegalStateException("Can not new instance of " + tClass.getName());
		} finally {
			c.close();
		}
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByStmt", " --> end ");
		return rs;
	}

	@Override
	public <T> List<T> queryDataByWhere(Class<T> tClass, ComplexDataModel cdm, String whereStmt, PARAM... params) {
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByWhere", " --> start ");
		ComplexDataModel _cdm = DataBaseFactory.getComplexDataModelDefinition(tClass);
		if (_cdm!=null && cdm!=null && !_cdm.equals(cdm)) throw new IllegalStateException("not matched ComplexDataModel " + _cdm + " / " + cdm);
		if (_cdm == null && cdm == null) {
			try {
				Class<? extends TableDefinition> td = DataBaseFactory.findTableDefinition(tClass);
				String sqlStmt = "SELECT * FROM " + DataBaseFactory.getTableName(td);
				return queryDataByStmt(tClass, sqlStmt, params);
			} catch (NoSuchFieldException|IllegalAccessException|NullPointerException e) {
				throw new IllegalStateException("unable to define target table of " + tClass.getName(), e);
			}
		}
		DataBaseFactory.TableSpy spy = new DataBaseFactory.TableSpy(tClass);
		// you will actually use after this query.
		//String tableName = spy.getTableName();
		StringBuilder sqlStmt = new StringBuilder();
		sqlStmt.append(spy.getCDMSqlSelector());
		sqlStmt.append(" WHERE ");
		sqlStmt.append(whereStmt);
		String[] args = new String[params.length];
		for (int i=0; i<params.length; i++) {
			if (StringUtils.isBlank(params[i].VALUE)) {
				switch(params[i].TYPE) {
					case INTEGER:
						args[i] = INT_FORMAT.format(params[i].INT_VALUE); break;
					case NUMBER:
						args[i] = NUM_FORMAT.format(params[i].NUM_VALUE); break;
					case STRING:
						args[i] = params[i].VALUE;
				}
			} else {
				args[i] = params[i].VALUE;
			}
		}
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery(sqlStmt.toString(), args);
		List<T> rs = null;
		try {
			rs = loadResult(tClass, c, spy);
		} catch (InstantiationException|IllegalAccessException e) {
			throw new IllegalStateException("Can not new instance of " + tClass.getName());
		} finally {
			c.close();
		}
		if (isLibTrace)
			LogUtility.debug(this, "queryDataByWhere", " --> end ");
		return rs;
	}

	@Override
	public int cleanData(Class<? extends TableDefinition>... tables) {
		int rs = 0;
		SQLiteDatabase db = getReadableDatabase();
		db.beginTransaction();
		try {
			for (Class<? extends TableDefinition> table : tables)
				rs += db.delete(getTableName(table), null, null);
			db.setTransactionSuccessful();
		} finally {
			if (db!=null && db.inTransaction())
				db.endTransaction();
		}
		return rs;
	}
	@Override
	public int cleanData(Class<? extends TableDefinition> table, String whereStmt, String[] whereArgs) {
		int rs = 0;
		SQLiteDatabase db = getReadableDatabase();
		rs = db.delete(getTableName(table), whereStmt, whereArgs);
		return rs;
	}

	private String getTableName(Class<? extends TableDefinition> table) {
		try {
			return DataBaseFactory.getTableName(table);
		} catch (NoSuchFieldException|IllegalAccessException e) {
			LogUtility.warn(this, "getTableName", " failure ", e);
		}
		return null;
	}
	private long getMaxLong(String table, String col) {
		SQLiteDatabase db = getReadableDatabase();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT MAX(").append(col).append(") FROM ").append(table);
		return db.compileStatement(sql.toString()).simpleQueryForLong();
	}
	private String getMaxString(String table, String col) {
		SQLiteDatabase db = getReadableDatabase();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT MAX(").append(col).append(") FROM ").append(table);
		return db.compileStatement(sql.toString()).simpleQueryForString();
	}
	@Override
	public int findMaxValueAsIntOfColumn(Class<? extends TableDefinition> table, String col) {
		String tableName = getTableName(table);
		if (tableName!=null)
			return findMaxValueAsIntOfColumn(tableName, col);
		return -2;
	}
	@Override
	public int findMaxValueAsIntOfColumn(String table, String col) {
		return (int) getMaxLong(table, col);
	}
	@Override
	public long findMaxValueAsLongOfColumn(Class<? extends TableDefinition> table, String col) {
		String tableName = getTableName(table);
		if (tableName!=null)
			return findMaxValueAsLongOfColumn(tableName, col);
		return -2;
	}
	@Override
	public long findMaxValueAsLongOfColumn(String table, String col) {
		return getMaxLong(table, col);
	}
	@Override
	public double findMaxValueAsNumberOfColumn(Class<? extends TableDefinition> table, String col) {
		String tableName = getTableName(table);
		if (tableName!=null)
			return findMaxValueAsNumberOfColumn(tableName, col);
		return -2;
	}
	@Override
	public double findMaxValueAsNumberOfColumn(String table, String col) {
		String max = getMaxString(table, col);
		if (StringUtils.isBlank(max)) return -2;
		return Double.parseDouble(max);
	}
	@Override
	public String findMaxValueAsStringOfColumn(Class<? extends TableDefinition> table, String col) {
		String tableName = getTableName(table);
		if (tableName!=null)
			return findMaxValueAsStringOfColumn(tableName, col);
		return null;
	}
	@Override
	public String findMaxValueAsStringOfColumn(String table, String col) {
		return getMaxString(table, col);
	}

}