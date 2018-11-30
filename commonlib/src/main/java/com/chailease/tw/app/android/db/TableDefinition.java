package com.chailease.tw.app.android.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static com.chailease.tw.app.android.db.TableDefinition.DATA_TYPE.STRING;

/**
 * 	定義資料庫的外觀：
 * 	 TableDefinition.TABLE_NAME;
 * 	 TableDefinition.COLUMN_NAME_ENTRY_ID
 * 	 TableDefinition.COLUMN_NAME_TITLE
 * 	 請以 static final String 來宣告 TABLE_NAME
 * 	 請以 static final String + TableColumn 來宣告 COLUMN_NAME, COLUMN NAME 變數名稱必須是 COLUMN_NAME_ 開頭
 */
public abstract class TableDefinition {

	public static enum DATA_TYPE {
		STRING, NUMBER, INTEGER
	}
	public static final String COLUMN_NAME_DECLARE_PREFIX = "COLUMN_NAME_";

	public abstract DataBean[] keepData(int oldVersion);
	public abstract String dropSql(int oldVersion);
	public String[] createSql(int oldVersion) {
		return null;
	}

	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface TableColumn {
		DATA_TYPE type() default STRING;
		boolean nullable() default false;
		boolean primaryKey() default false;
		String indexName() default "";
	}

}