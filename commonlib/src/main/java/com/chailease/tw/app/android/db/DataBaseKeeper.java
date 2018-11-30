package com.chailease.tw.app.android.db;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface DataBaseKeeper {

	String GO_BY_STANDARD_PROCESS   = "GO BY STANDARD PROCESS";
	String SKIP_THIS_PROCESS_FOR_CURRENT_TABLE  =   "SKIP THIS PROCESS FOR CURRENT TABLE";
	DecimalFormat INT_FORMAT = new DecimalFormat("######");
	DecimalFormat NUM_FORMAT = new DecimalFormat("######.######");

	String getDBName();
	int getDBVersion();

	String formatIntValue(int value);
	String formatNumValue(double value);

	/**
	 * versions
	 *  index 0 : old version
	 *  用於向 TableDefinition#createSql(old_version) 索取當創建新的版本 Table schema 時應提供哪樣的 SQL
	 * @param versions
	 * @return
	 */
	String[] createDB(int... versions);

	/**
	 * versions
	 *  index 0 : old version
	 *  用於向 TableDefinition#dropSql(old_version) 索取當創建新的版本 Table schema 時應提供哪樣的 SQL
	 * @param versions
	 * @return
	 */
	String[] dropDB(int... versions);

	/**
	 * versions
	 *  index 0 : old version
	 *  用於向 TableDefinition#keepData(old_version) 索取當創建新的版本 Table schema 時為了保留舊資料應提供哪樣的 SQL
	 * @param versions
	 * @return
	 */
	DataBean[] keepData(int... versions);

	/**
	 * 提供 TableDefinition 取得 Alter Table 的 SQL 以支援 createSql 時可用於直接透過 alter 變更,
	 * 當然也可以透過 drop table 後再 create table 完成
	 * @param table
	 * @param addCols
	 * @return
	 */
	String alterTable(Class<? extends TableDefinition> table, Field[] addCols);
	List<Map> queryDataByTable(Class<? extends TableDefinition> table);
	/**
	 * 如果指定的 Data Model 的欄位名稱與 DB 一致則可以自動對應, 否則應該是要透過 @DataModel 來對應的
	 * @param table
	 * @param <T>
	 * @return
	 */
	<T> List<T> queryDataByTable(Class<T> tClass, Class<? extends TableDefinition> table);
	/**
	 * 如果指定的 Data Model 的欄位名稱與 DB 一致則可以自動對應, 否則應該是要透過 @DataModel 來對應的
	 * @param table
	 * @param distinct_cols
	 * @param orders
	 * @param <T>
	 * @return
	 */
	<T> List<T> queryDataByTable(Class<T> tClass, String table, String[] distinct_cols, String[] orders);
	/**
	 * 如果指定的 Data Model 的欄位名稱與 DB 一致則可以自動對應, 否則應該是要透過 @DataModel 來對應的
	 * @param table
	 * @param whereStmt
	 * @param params
	 * @param <T>
	 * @return
	 */
	<T> List<T> queryDataByTable(Class<T> tClass, Class<? extends TableDefinition> table, String whereStmt, PARAM... params);

	/**
	 *  where statement will be following
	 *      cols[i] = values[i] and cols[i+1] = values[i+1]
	 * @param tClass
	 * @param table
	 * @param cols
	 * @param values
	 * @param <T>
	 * @return
	 */
	<T> List<T> queryDataByCols(Class<T> tClass, Class<? extends TableDefinition> table, String[] cols, String... values);

	/**
	 *  where statement will be following
	 *      cols[i] = parma[i] and cols[i+1] in (param[i+1])
	 * @param tClass
	 * @param table
	 * @param cols
	 * @param values
	 * @param <T>
	 * @return
	 */
	<T> List<T> queryDataByCols(Class<T> tClass, Class<? extends TableDefinition> table, String[] cols, PARAM... values);
	/**
	 * 如果指定的 Data Model 的欄位名稱與 DB 一致則可以自動對應, 否則應該是要透過 @DataModel 來對應的
	 * @param sqlStmt
	 * @param params
	 * @param <T>
	 * @return
	 */
	<T> List<T> queryDataByStmt(Class<T> tClass, String sqlStmt, PARAM... params);
	<T> List<T> queryDataByWhere(Class<T> tClass, ComplexDataModel cdm, String whereStmt, PARAM... params);

	int findMaxValueAsIntOfColumn(Class<? extends TableDefinition> table, String col);
	int findMaxValueAsIntOfColumn(String table, String col);
	long findMaxValueAsLongOfColumn(Class<? extends TableDefinition> table, String col);
	long findMaxValueAsLongOfColumn(String table, String col);
	String findMaxValueAsStringOfColumn(Class<? extends TableDefinition> table, String col);
	String findMaxValueAsStringOfColumn(String table, String col);
	double findMaxValueAsNumberOfColumn(Class<? extends TableDefinition> table, String col);
	double findMaxValueAsNumberOfColumn(String table, String col);

	/**
	 * 指定的 Data Model 應該要透過 @DataModel 來定義資料對應
	 * @param data
	 * @param <T>
	 * @return
	 */
	<T> boolean insertRow(T data);
	/**
	 * 指定的 Data Model 應該要透過 @DataModel 來定義資料對應
	 * @param data
	 * @param <T>
	 * @return
	 */
	<T> boolean updateRow(T data);
	/**
	 * 指定的 Data Model 應該要透過 @DataModel 來定義資料對應
	 * @param data
	 * @param <T>
	 * @return
	 */
	<T> boolean deleteRow(T data);

	<T> int insertRows(List<T> datas);
	<T> int insertRows(T... datas);
	<T> int updateRows(List<T> datas);
	<T> int updateRows(T... datas);
	<T> int deleteRows(List<T> datas);
	<T> int deleteRows(T... datas);
	int deleteRows(Class<? extends TableDefinition> table, List<Map<String, String>> conds);
	int deleteRows(Class<? extends TableDefinition> table, Map<String, String>... conds);
	int deleteRows(Class<? extends TableDefinition> table, String[] pkCols, String[]... values);
	int updateRows(Class<? extends TableDefinition> table, String[] updateCols, PARAM[] updateVals, String[] pkCols, String[]... values);

	int cleanData(Class<? extends TableDefinition>... tables);
	int cleanData(Class<? extends TableDefinition> table, String whereStmt, String[] whereArgs);
}