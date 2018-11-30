package com.chailease.tw.app.android.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Example:
 *  @ComplexDataModel(main_table = TABLE_A.class,
 *          main_alias = "a",
 *          cdmJoins = {TABLE_B.class, TABLE_C.class},
 *          cdmJoin_alias = {"b", "c"},
 *          cdmJoin_rules = {" a.COL_A=b.COL_B ", "b.COL_B=c.COL_C"} )
 *  class ComplexDataBean {
 *      @DataAttribute(column = "a.COL_A")
 *      public String COL_1;
 *      @DataAttribute(column = "b.COL_B")
 *      public String COL_2;
 *      @DataAttribute(column = "c.COL_C")
 *      public String COL_3;
 *  }
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComplexDataModel {
	Class<? extends TableDefinition> main_table();
	String main_alias() default "";
	Class<? extends TableDefinition>[] joins();
	String[] join_alias();
	String[] join_rules();
}