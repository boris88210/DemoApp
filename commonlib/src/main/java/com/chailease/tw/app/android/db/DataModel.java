package com.chailease.tw.app.android.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.chailease.tw.app.android.db.TableDefinition.DATA_TYPE;

/**
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataModel {
	Class<? extends TableDefinition> table();

	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface DataAttribute {
		String column();
		String target_col() default "";
		boolean extendDef() default false;
		DATA_TYPE extendType() default DATA_TYPE.STRING;
	}

}