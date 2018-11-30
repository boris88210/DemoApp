package com.chailease.tw.app.android.db;

import android.content.Context;

import com.chailease.tw.app.android.db.sqlite.SQLiteDBHelper;
import com.chailease.tw.app.android.utils.LogUtility;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.chailease.tw.app.android.commonlib.BuildConfig.LIB_ID;
import static com.chailease.tw.app.android.commonlib.BuildConfig.isLibTrace;
import static com.chailease.tw.app.android.db.TableDefinition.COLUMN_NAME_DECLARE_PREFIX;

/**
 *
 */
public class DataBaseFactory {

	public static enum DBMS {
		SQLITE
	}

	private static DataBaseKeeper dbKeeper;
	private static DBMS dbms;
	private static Class<? extends TableDefinition>[] tables;
	private static boolean doCacheDef = false;
	private static Map<Class<? extends TableDefinition>, Map> colDef;


	public static void init(DBMS vDbms, Class<? extends TableDefinition>... vTables) {
		init(vDbms, false, vTables);
	}
	public static void init(DBMS vDbms, boolean doCache, Class<? extends TableDefinition>... vTables) {
		if (null == dbms) {
			synchronized (DataBaseFactory.class) {
				if (null == dbms) {
					if (isLibTrace)
						LogUtility.debug(DataBaseFactory.class, "init", " --> start ");
					if (null == vTables || vTables.length==0)
						throw new IllegalArgumentException("Table did not be defined");
					dbms = vDbms;
					tables = vTables;
					doCacheDef = doCache;
					if (doCacheDef) {
						colDef = new HashMap<Class<? extends TableDefinition>, Map>();
						for (Class<? extends TableDefinition> t : tables)
							try {
								getColumnDefinitions(t);
							} catch (IllegalAccessException e) {
								if (isLibTrace)
									LogUtility.warn(DataBaseFactory.class, "init", " --> get column definitions failure " + t.getName(), e);
							}
					}
					if (isLibTrace)
						LogUtility.debug(DataBaseFactory.class, "init", " --> end ");
					return;
				}
			}
		}
		if (vDbms != dbms) {
			throw new IllegalArgumentException("DBMS already setting as " + dbms);
		}
	}

	public static DataBaseKeeper getDbKeeper(Context context, String dbName, int dbVersion) {
		if (null == dbms) throw new IllegalStateException("need to init DataBaseFactory first");
		if (null == dbKeeper) {
			synchronized (DataBaseFactory.class) {
				if (null == dbKeeper) {
					if (isLibTrace)
						LogUtility.debug(DataBaseFactory.class, "getDbKeeper", " --> start ");
					dbKeeper = new SQLiteDBHelper(context, dbName, dbVersion);
					if (isLibTrace)
						LogUtility.debug(DataBaseFactory.class, "getDbKeeper", " --> end " + dbKeeper);
				}
			}
		}
		return dbKeeper;
	}

	public static void touch(Context context, String dbName, int dbVersion) {
		if (isLibTrace)
			LogUtility.debug(DataBaseFactory.class, "touch", " --> start ");
		DataBaseKeeper keeper = getDbKeeper(context, dbName, dbVersion);
		List<Map> rs = keeper.queryDataByTable(tables[0]);
		if (isLibTrace)
			LogUtility.debug(DataBaseFactory.class, "touch", " --> end with " + rs);
	}

	public static int getTableCount() {
		return tables.length;
	}
	public static Class<? extends TableDefinition> getTable(int index) {
		return tables[index];
	}
	public static Class<? extends TableDefinition>[] getTables() {
		if (null == tables || tables.length==0) return new Class[0];
		Class<? extends TableDefinition>[] clone = new Class[tables.length];
		for (int i=0; i<tables.length; i++)
			clone[i] = tables[i];
		return clone;
	}
	public static final String TABLE_NAME = "TABLE_NAME";
	public static String getTableName(Class<? extends TableDefinition> table) throws NoSuchFieldException, IllegalAccessException {
		Class tClass = table;
		Field tableName = tClass.getField(TABLE_NAME);
		return (String) tableName.get(null);
	}
	public static final String PRIMARY_KEY = "PRIMARY_KEYS";
	public static final String IDX_LIST = "IDX_LIST";
	public static final String STRING_NULLABLE = "STR_NULL";
	public static final String STRING_NOTNULL = "STR_NOTNULL";
	public static final String NUMBER_NULLABLE = "NUM_NULL";
	public static final String NUMBER_NOTNULL = "NUM_NOTNULL";
	public static final String INTEGER_NULLABLE = "INT_NULL";
	public static final String INTEGER_NOTNULL = "INT_NOTNULL";
	public static String[] getColumns(Class<? extends TableDefinition> table) throws IllegalAccessException {

		Map<String, String[]> cols = getColumnDefinitions(table);
		if (cols.size()>0) {
			String[] strings_nullable = cols.get(STRING_NULLABLE);
			String[] strings_notnull = cols.get(STRING_NOTNULL);
			String[] numbers_nullable = cols.get(NUMBER_NULLABLE);
			String[] numbers_notnull = cols.get(NUMBER_NOTNULL);
			String[] ints_nullable = cols.get(INTEGER_NULLABLE);
			String[] ints_notnull = cols.get(INTEGER_NOTNULL);
			int size = strings_notnull!=null ? strings_notnull.length : 0;
			size += strings_nullable!=null ? strings_nullable.length : 0;
			size += numbers_nullable!=null ? numbers_nullable.length : 0;
			size += numbers_notnull!=null ? numbers_notnull.length : 0;
			size += ints_nullable!=null ? ints_nullable.length : 0;
			size += ints_notnull!=null ? ints_notnull.length : 0;
			String[] rs = new String[size];
			int i=0;
			if (strings_nullable!=null) {
				for (String col : strings_nullable) {
					rs[i++] = col;
				}
			}
			if (strings_notnull!=null) {
				for (String col : strings_notnull) {
					rs[i++] = col;
				}
			}
			if (numbers_nullable!=null) {
				for (String col : numbers_nullable) {
					rs[i++] = col;
				}
			}
			if (numbers_notnull!=null) {
				for (String col : numbers_notnull) {
					rs[i++] = col;
				}
			}
			if (ints_nullable!=null) {
				for (String col : ints_nullable) {
					rs[i++] = col;
				}
			}
			if (ints_notnull!=null) {
				for (String col : ints_notnull) {
					rs[i++] = col;
				}
			}
			return rs;
		}
		return null;
	}
	public static Map<String, String[]> getColumnDefinitions(Class<? extends TableDefinition> table) throws IllegalAccessException {

		if (doCacheDef) {
			if (colDef.containsKey(table)) {
				return colDef.get(table);
			} else if (isLibTrace) LogUtility.debug(DataBaseFactory.class, "getColumnDefinitions", "can not found table definition from cache " + table);
		}

		Class tClass = table;
		Field[] fields = tClass.getDeclaredFields();

		HashMap<String, String[]> cols = new HashMap<String, String[]>();
		ArrayList<String> primary_keys = new ArrayList<String>();
		HashMap<String, ArrayList<String>> idx_map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> strings_nullable = new ArrayList<String>();
		ArrayList<String> strings_notnull = new ArrayList<String>();
		ArrayList<String> numbers_nullable = new ArrayList<String>();
		ArrayList<String> numbers_notnull = new ArrayList<String>();
		ArrayList<String> ints_nullable = new ArrayList<String>();
		ArrayList<String> ints_notnull = new ArrayList<String>();

		if (isLibTrace)
			LogUtility.debug(DataBaseFactory.class, "getColumnDefinitions", " --> checking table-definition " + tClass);

		for (Field field : fields) {
			if (isLibTrace)
				LogUtility.debug(DataBaseFactory.class, "getColumnDefinitions", " --> checking table-definition field " + field.getName() + " >> " + field.toGenericString() + " >> " + field.getType());

			field.setAccessible(true);
			TableDefinition.TableColumn tc = checkColumnDefinition(field);
			Object ckf = field.get(null);
			if (ckf == null) continue;
			String name = ckf.toString();

			if (isLibTrace)
				LogUtility.debug(DataBaseFactory.class, "getColumnDefinitions", " --> checking table-definition field " + field.getName() + " as column " + name + " with TC>>" + tc);

			if (null != tc) {
				if (tc.primaryKey()) primary_keys.add(name);
				if (!StringUtils.isBlank(tc.indexName())) {
					String idx_name = tc.indexName();
					if (!idx_map.containsKey(idx_name)) idx_map.put(idx_name, new ArrayList<String>());
					idx_map.get(idx_name).add(name);
				}
				if (isDataType(tc, TableDefinition.DATA_TYPE.STRING, false)) {
					strings_notnull.add(name);
				} else if (isDataType(tc, TableDefinition.DATA_TYPE.STRING, true)) {
					strings_nullable.add(name);
				} else if (isDataType(tc, TableDefinition.DATA_TYPE.NUMBER, false)) {
					numbers_notnull.add(name);
				} else if (isDataType(tc, TableDefinition.DATA_TYPE.NUMBER, true)) {
					numbers_nullable.add(name);
				} else if (isDataType(tc, TableDefinition.DATA_TYPE.INTEGER, false)) {
					ints_notnull.add(name);
				} else if (isDataType(tc, TableDefinition.DATA_TYPE.INTEGER, true)) {
					ints_nullable.add(name);
				}
			} else if (field.getName().startsWith(COLUMN_NAME_DECLARE_PREFIX)) {
				strings_notnull.add(name);
			}
		}
		if (strings_notnull.size()>0) cols.put(STRING_NOTNULL, strings_notnull.toArray(new String[strings_notnull.size()]));
		if (strings_nullable.size()>0) cols.put(STRING_NULLABLE, strings_nullable.toArray(new String[strings_nullable.size()]));
		if (numbers_notnull.size()>0) cols.put(NUMBER_NOTNULL, numbers_notnull.toArray(new String[numbers_notnull.size()]));
		if (numbers_nullable.size()>0) cols.put(NUMBER_NULLABLE, numbers_nullable.toArray(new String[numbers_nullable.size()]));
		if (ints_notnull.size()>0) cols.put(INTEGER_NOTNULL, ints_notnull.toArray(new String[ints_notnull.size()]));
		if (ints_nullable.size()>0) cols.put(INTEGER_NULLABLE, ints_nullable.toArray(new String[ints_nullable.size()]));

		if (cols.size() == 0) throw new IllegalStateException("Bad TableDefinition " + table.getClass().getName());

		if (primary_keys.size()>0) cols.put(PRIMARY_KEY, primary_keys.toArray(new String[primary_keys.size()]));
		if (idx_map.size()>0) {
			String[] idx_list = idx_map.keySet().toArray(new String[idx_map.size()]);
			cols.put(IDX_LIST, idx_list);
			for (String idx : idx_list)
				cols.put(idx, idx_map.get(idx).toArray(new String[idx_map.get(idx).size()]));
		}
		if (doCacheDef) {
			colDef.put(table, cols);
		}
		return cols;
	}

	public static boolean isDataType(TableDefinition.TableColumn ann, TableDefinition.DATA_TYPE type, boolean nullable) {
		return ann.type() == type && nullable == ann.nullable();
	}
	public static TableDefinition.DATA_TYPE getDataType(Field field) {
		field.setAccessible(true);
		TableDefinition.TableColumn tc = checkColumnDefinition(field);
		if (null != tc) {
			return tc.type();
		} else if (field.getName().startsWith(COLUMN_NAME_DECLARE_PREFIX)) {
			return TableDefinition.DATA_TYPE.STRING;
		}
		return null;
	}
	public static TableDefinition.TableColumn checkColumnDefinition(Field field) {
		if (field.getName().startsWith(COLUMN_NAME_DECLARE_PREFIX)) {
			Annotation[] anns = field.getDeclaredAnnotations();
			if (anns != null && anns.length > 0) {
				for (Annotation ann : anns)
					if (ann instanceof TableDefinition.TableColumn) {
						TableDefinition.TableColumn tc = (TableDefinition.TableColumn) ann;
						return tc;
					}
			}
		}
		return null;
	}

	public static Class<? extends TableDefinition> findTableDefinition(Class dataModel) {
		Annotation[] typeAnns = dataModel.getAnnotations();
		if (typeAnns != null && typeAnns.length>0) {
			for (Annotation dm : typeAnns)
				if (dm instanceof DataModel)
					return ((DataModel)dm).table();
				else if (dm instanceof ComplexDataModel)
					return ((ComplexDataModel)dm).main_table();
		}
		return null;
	}
	public static DataModel.DataAttribute findMatchedTableColumn(Field field) {
		Annotation[] fieldAnns = field.getAnnotations();
		if (fieldAnns != null && fieldAnns.length > 0) {
			for (Annotation da : fieldAnns)
				if (da instanceof DataModel.DataAttribute)
					return (DataModel.DataAttribute) da;
		}
		return null;
	}
	public static TableDefinition.DATA_TYPE findExtendDefColumnType(Field field) {
		Annotation[] fieldAnns = field.getAnnotations();
		if (fieldAnns != null && fieldAnns.length > 0) {
			for (Annotation da : fieldAnns)
				if (da instanceof DataModel.DataAttribute) {
					DataModel.DataAttribute ada = (DataModel.DataAttribute) da;
					if (ada.extendDef())
						return ada.extendType();
				}
		}
		return null;
	}
	public static boolean isComplexDataModel(Class dataModel) {
		Annotation[] typeAnns = dataModel.getAnnotations();
		if (typeAnns != null && typeAnns.length>0) {
			for (Annotation dm : typeAnns)
				if (dm instanceof ComplexDataModel)
					return true;
		}
		return false;
	}
	public static ComplexDataModel getComplexDataModelDefinition(Class dataModel) {
		Annotation[] typeAnns = dataModel.getAnnotations();
		if (typeAnns != null && typeAnns.length>0) {
			for (Annotation dm : typeAnns)
				if (dm instanceof ComplexDataModel)
					return (ComplexDataModel) dm;
		}
		return null;
	}

	public static class TableSpy<T> {
		Class<T> dataModel;
		String tableName;
		ArrayList<String> colNames;
		HashSet<String> intCols;
		HashSet<String> numCols;
		HashSet<String> strCols;
		HashMap<String, Field> attrs;
		HashSet<String> intExtCols;
		HashSet<String> numExtCols;
		HashSet<String> strExtCols;
		String[] pkCols;
		boolean isComplexDataModel = false;
		Class<? extends TableDefinition>[] cdmJoins = null;
		String[] cdmJoin_alias = null;
		String[] cdmJoin_rules = null;
		String cdmSqlSelector = null;

		public TableSpy(Class<T> tClass) {
			super();
			this.dataModel = tClass;
			init();
		}
		void init() {
			try {
				Class<? extends TableDefinition> td = findTableDefinition(dataModel);
				if (null == td) throw new IllegalArgumentException("Undefined Data target Table");
				tableName = (String) td.getField(TABLE_NAME).get(null);
				Map<String, String[]> colsDef = getColumnDefinitions(td);
				pkCols = colsDef.get(PRIMARY_KEY);
				intCols = new HashSet<String>();
				String[] intcol = colsDef.get(INTEGER_NOTNULL);
				if (null != intcol && intcol.length>0) {
					for (String col : intcol)
						intCols.add(col);
				}
				intcol = colsDef.get(INTEGER_NULLABLE);
				if (null != intcol && intcol.length>0) {
					for (String col : intcol)
						intCols.add(col);
				}
				intcol = null;
				numCols = new HashSet<String>();
				String[] numcol = colsDef.get(NUMBER_NOTNULL);
				if (null != numcol && numcol.length>0) {
					for (String col : numcol)
						numCols.add(col);
				}
				numcol = colsDef.get(NUMBER_NULLABLE);
				if (null != numcol && numcol.length>0) {
					for (String col : numcol)
						numCols.add(col);
				}
				numcol = null;
				strCols = new HashSet<String>();
				String[] strcol = colsDef.get(STRING_NOTNULL);
				if (null != strcol && strcol.length>0) {
					for (String col : strcol)
						strCols.add(col);
				}
				strcol = colsDef.get(STRING_NULLABLE);
				if (null != strcol && strcol.length>0) {
					for (String col : strcol)
						strCols.add(col);
				}
				strcol = null;

				ComplexDataModel cdm = getComplexDataModelDefinition(dataModel);
				isComplexDataModel = cdm != null;
				if (isComplexDataModel && isLibTrace) {
					LogUtility.debug(this, "init", "running with ComplexDataModel >> main_table.TableDefinition=" + cdm.main_table().getName()
									+ ";\n main_alias=" + cdm.main_alias()
									+ ";\n joins=" + DataBaseFactory.toString(cdm.joins())
									+ ";\n join_alias=" + DataBaseFactory.toString(cdm.join_alias())
									+ ";\n join_rules=" + DataBaseFactory.toString(cdm.join_rules())
					);
				}
				String[][] join_table_cols = null;
				StringBuilder _cdmSqlSelector = null;
				String _cdmSqlFrom = null;
				if (isComplexDataModel) {
					cdmJoins = cdm.joins();
					cdmJoin_alias = cdm.join_alias();
					cdmJoin_rules = cdm.join_rules();
					join_table_cols = new String[cdmJoins.length][];
					_cdmSqlSelector = new StringBuilder();
					_cdmSqlSelector.append("SELECT 1 dummy_col ");
					StringBuilder fromTemp = new StringBuilder();
					fromTemp.append(" FROM ").append(tableName).append(" ").append(cdm.main_alias());
					for (int i=0; i<cdmJoins.length; i++) {
						fromTemp.append(" JOIN ").append(DataBaseFactory.getTableName(cdmJoins[i])).append(" ").append(cdmJoin_alias[i])
								.append(" ON ").append(cdmJoin_rules[i]);
					}
					_cdmSqlFrom = fromTemp.toString();
				}

				Field[] fields = dataModel.getDeclaredFields();
				attrs = new HashMap<String, Field>();
				colNames = new ArrayList<String>();
				intExtCols = new HashSet<String>();
				numExtCols = new HashSet<String>();
				strExtCols = new HashSet<String>();
				CHK_FIELD:
				for (Field field : fields) {
					DataModel.DataAttribute colDef = findMatchedTableColumn(field);
					String columnName = colDef!=null ? colDef.column() : null;
					if (StringUtils.isBlank(columnName) && !isComplexDataModel) {
						columnName = field.getName();
					}
					if (StringUtils.isBlank(columnName)) {
						if (isLibTrace)
							LogUtility.debug(this, "init", "wow column >>" + DataBaseFactory.toString(field));
						continue ;
					}
					String tAlias = colDef!=null ? colDef.target_col() : null;
					if (isComplexDataModel) {
						_cdmSqlSelector.append(", ");
						if (!StringUtils.isBlank(tAlias))
							_cdmSqlSelector.append(tAlias).append(" as ");   //  target_col as column
						_cdmSqlSelector.append(columnName).append(" "); //  as field name
						// in complex-data-model if the value of column matching main table's columns that mapping into main table
						//  if not that mean going to checking with isComplexDataModel mapping rules
						if (columnName.startsWith(cdm.main_alias()+".")) {
							columnName = columnName.substring(cdm.main_alias().length()+1); //  assume the result of query without table alias
						}
					}
					if (strCols.contains(columnName) || intCols.contains(columnName) || numCols.contains(columnName)) {
						attrs.put(columnName, field);
						colNames.add(columnName);
					} else {
						if (isComplexDataModel) {
							Class<? extends TableDefinition> join_table = null;
							String[] _colNames = null;
							if (StringUtils.isBlank(tAlias)) {
								//  tAlias is not empty that mean field has target column and with its own name maybe as field
								tAlias = columnName;
								columnName = columnName.substring(columnName.indexOf(".")+1);
							}
							for (int i=0; i<cdmJoin_alias.length; i++) {
								String a = cdmJoin_alias[i] + ".";
								if (tAlias.startsWith(a)) {
									join_table = cdmJoins[i];
									if (join_table_cols[i] == null) join_table_cols[i] = getColumns(join_table);
									_colNames = join_table_cols[i];
									for (String _col : _colNames) {
										String ck_col = a + _col;
										if (_col.equalsIgnoreCase(columnName) || ck_col.equalsIgnoreCase(columnName)) {
											//  start mapping join column
											boolean matchType = false;
											Map<String, String[]> _colsDef = getColumnDefinitions(join_table);
											intcol = colsDef.get(INTEGER_NOTNULL);
											if (null != intcol && intcol.length > 0) {
												for (String col : intcol)
													if (matchType = columnName.equalsIgnoreCase(col)) {
														intCols.add(col);
														break;
													}
											}
											if (!matchType) {
												intcol = colsDef.get(INTEGER_NULLABLE);
												if (null != intcol && intcol.length > 0) {
													for (String col : intcol)
														if (matchType = columnName.equalsIgnoreCase(col)) {
															intCols.add(col);
															break;
														}
												}
											}
											intcol = null;
											if (!matchType) {
												numcol = colsDef.get(NUMBER_NOTNULL);
												if (null != numcol && numcol.length > 0) {
													for (String col : numcol)
														if (columnName.equalsIgnoreCase(col))
															if (matchType = columnName.equalsIgnoreCase(col)) {
																numCols.add(col);
																break;
															}
												}
											}
											if (!matchType) {
												numcol = colsDef.get(NUMBER_NULLABLE);
												if (null != numcol && numcol.length > 0) {
													for (String col : numcol)
														if (columnName.equalsIgnoreCase(col))
															if (matchType = columnName.equalsIgnoreCase(col)) {
																numCols.add(col);
																break;
															}
												}
											}
											numcol = null;
											if (!matchType) {
												strcol = colsDef.get(STRING_NOTNULL);
												if (null != strcol && strcol.length > 0) {
													for (String col : strcol)
														if (columnName.equalsIgnoreCase(col))
															if (matchType = columnName.equalsIgnoreCase(col)) {
																strCols.add(col);
																break;
															}
												}
											}
											if (!matchType) {
												strcol = colsDef.get(STRING_NULLABLE);
												if (null != strcol && strcol.length > 0) {
													for (String col : strcol)
														if (columnName.equalsIgnoreCase(col))
															if (matchType = columnName.equalsIgnoreCase(col)) {
																strCols.add(col);
																break;
															}
												}
											}
											strcol = null;
											if (matchType) {
												attrs.put(columnName, field);
												colNames.add(columnName);
												continue CHK_FIELD;
//											} else if (isLibTrace) {
//												Log.d(LIB_ID, "field no match-type " + field.getName() + " >>" + columnName + " >> " + _col);
												//attrs.put(columnName, field);
											}
//										} else if (isLibTrace) {
//											Log.d(LIB_ID, "field not allow as a column " + field.getName() + " >>" + columnName + " >> " + _col);
										}
									}
								}
							}
						} else if (isLibTrace) {
							LogUtility.debug(this, "init", "field not allow as a column " + field.getName() + " >>" + columnName + " >> check as extend");
						}
						TableDefinition.DATA_TYPE dt = findExtendDefColumnType(field);
						if (dt != null) {
							switch (dt) {
								case INTEGER:
									attrs.put(columnName, field);
									intExtCols.add(columnName);
									break;
								case NUMBER:
									attrs.put(columnName, field);
									numExtCols.add(columnName);
									break;
								case STRING:
									attrs.put(columnName, field);
									strExtCols.add(columnName);
									break;
							}
						}
					}
				}
				if (_cdmSqlSelector!=null) {
					cdmSqlSelector = _cdmSqlSelector.toString() + _cdmSqlFrom;
				}
			} catch (IllegalAccessException|NoSuchFieldException e) {
				throw new IllegalStateException("Bad Data Model for transfer to table mapping.", e);
			}
		}
		public String getCDMSqlSelector() {
			return cdmSqlSelector;
		}

		public String getString(T data, String colName) {
			if (strCols.contains(colName)) {
				try {
					return (String) attrs.get(colName).get(data);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException("access " + colName + " failure by " + data, e);
				}
			}
			return null;
		}
		public int getInt(T data, String colName) {
			if (intCols.contains(colName)) {
				try {
					return attrs.get(colName).getInt(data);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException("access " + colName + " failure by " + data, e);
				}
			}
			throw new IllegalArgumentException(colName + " is not a integer attribute ");
		}
		public double getNum(T data, String colName) {
			if (numCols.contains(colName)) {
				try {
					return attrs.get(colName).getDouble(data);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException("access " + colName + " failure by " + data, e);
				}
			}
			throw new IllegalArgumentException(colName + " is not a integer attribute ");
		}
		public String getTableName() {
			return tableName;
		}
		public String[] getColNames() {
			return colNames.toArray(new String[colNames.size()]);
		}
		public String[] getStringColNames() {
			return strCols.toArray(new String[strCols.size()]);
		}
		public String[] getIntColNames() {
			return intCols.toArray(new String[intCols.size()]);
		}
		public String[] getNumColNames() {
			return numCols.toArray(new String[numCols.size()]);
		}
		public String[] getPrimaryKeyColNames() {
			if (pkCols!=null) {
				String[] rts = new String[pkCols.length];
				for (int i=0; i<rts.length; i++)
					rts[i] = pkCols[i];
				return rts;
			}
			return null;
		}
		public TableDefinition.DATA_TYPE checkColumnType(String col) {
			if (attrs.containsKey(col)) {
				if (strCols.contains(col)) return TableDefinition.DATA_TYPE.STRING;
				if (intCols.contains(col)) return TableDefinition.DATA_TYPE.INTEGER;
				if (numCols.contains(col)) return TableDefinition.DATA_TYPE.NUMBER;
				if (strExtCols.contains(col)) return TableDefinition.DATA_TYPE.STRING;
				if (intExtCols.contains(col)) return TableDefinition.DATA_TYPE.INTEGER;
				if (numExtCols.contains(col)) return TableDefinition.DATA_TYPE.NUMBER;
				return TableDefinition.DATA_TYPE.STRING;
			}
			return null;
		}
		public void setColValue(T data, String col, int value) throws IllegalAccessException {
			Field field = attrs.get(col);
			field.setAccessible(true);
			field.setInt(data, value);
		}
		public void setColValue(T data, String col, double value) throws IllegalAccessException {
			Field field = attrs.get(col);
			field.setAccessible(true);
			if (field.getGenericType().equals(long.class))
				field.setLong(data, new Double(value).longValue());
			else if (field.getType().equals(double.class))
				field.setDouble(data, value);
		}
		public void setColValue(T data, String col, String value) throws IllegalAccessException {
			Field field = attrs.get(col);
			field.setAccessible(true);
			field.set(data, value);
		}
	}

	private static String toString(Class<? extends TableDefinition>... tables) {
		StringBuilder str = new StringBuilder();
		if (tables!=null) {
			str.append("[");
			if (tables.length>0) {
				boolean multi = false;
				for (Class<? extends TableDefinition> t : tables) {
					if (multi) str.append(",");
					str.append("{");
					try {
						str.append("Class:").append(t.getCanonicalName());
						str.append(", table-name=").append(t.getField("TABLE_NAME").get(null));
					} catch (IllegalAccessException|NoSuchFieldException e) {
						LogUtility.info(DataBaseFactory.class, "toString", t + ">>" + e.getMessage());
					}
					str.append("}");
					multi = true;
				}
			}
			str.append("]");
		}
		return str.toString();
	}
	private static String toString(String... strs) {
		StringBuilder str = new StringBuilder();
		if (strs!=null) {
			str.append("[");
			boolean multi = false;
			for (String s : strs) {
				if (multi) str.append(", ");
				str.append(s);
			}
			str.append("]");
		}
		return str.toString();
	}
	private static String toString(Object... objs) {
		StringBuilder str = new StringBuilder();
		if (objs!=null) {
			str.append("[");
			boolean multi = false;
			for (Object o : objs) {
				if (multi) str.append(", ");
				str.append("{");
				if (o instanceof DataModel) {
					DataModel dm = (DataModel) o;
					str.append("TYPE:").append(dm.getClass()).append(", ");
					str.append("table=").append(toString(dm.table()));
				} else if (o instanceof DataModel.DataAttribute) {
					DataModel.DataAttribute da = (DataModel.DataAttribute) o;
					str.append("TYPE:").append(da.getClass());
					str.append(", column=").append(da.column());
					str.append(", target_col=").append(da.target_col());
					str.append(", extendType=").append(da.extendType());
					str.append(", extendDef=").append(da.extendDef());
				} else if (o instanceof Field) {
					Field f = (Field) o;
					str.append("TYPE:").append(f.getClass());
					str.append(", GenericType=").append(f.getGenericType());
					str.append(", Name=").append(f.getName());
				} else {
					str.append(o);
				}
				str.append("}");
			}
			str.append("]");
		}
		return str.toString();
	}
}