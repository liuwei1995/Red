package com.tgb.lk.ahibernate.util;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableHelper {
	private static final String TAG = "AHibernate";

	public static <T> void createTablesByClasses(SQLiteDatabase db,
			Class<?>[] clazzs) {
		for (Class<?> clazz : clazzs)
			createTable(db, clazz);
	}

	public static <T> void dropTablesByClasses(SQLiteDatabase db,
			Class<?>[] clazzs) {
		for (Class<?> clazz : clazzs)
			dropTable(db, clazz);
	}
	/**
		  CREATE TABLE COMPANY(
		   ID INT PRIMARY KEY     NOT NULL,
		   NAME           TEXT    NOT NULL,
		   AGE            INT     NOT NULL,
		   ADDRESS        CHAR(50),
		   SALARY         REAL
		);
	 * @param db
	 * @param clazz
	 */
	public static <T> void createTable(SQLiteDatabase db, Class<T> clazz) {
		if(clazz == null || db == null)return;
		String tableName = "";
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz.getAnnotation(Table.class);
			tableName = table.name();
		}else {
			tableName = clazz.getSimpleName();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(tableName).append(" (");

		List<Field> allFields = TableHelper
				.joinFields(clazz.getDeclaredFields(), clazz.getSuperclass()
						.getDeclaredFields());
		for (Field field : allFields) {
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}

			Column column = (Column) field.getAnnotation(Column.class);

			String columnType = "";
			if (column.type().equals(""))
				columnType = getColumnType(field.getType());
			else {
				columnType = column.type();
			}

			sb.append(column.name() + " " + columnType);

			if (column.length() != 0) {
				sb.append("(" + column.length() + ")");
			}

			if ((field.isAnnotationPresent(Id.class)) // update 2012-06-10 ʵ���ඨ��ΪInteger���ͺ������Id�쳣
					&& ((field.getType() == Integer.TYPE) || (field.getType() == Integer.class)))
				sb.append(" primary key autoincrement");
			else if (field.isAnnotationPresent(Id.class)) {
				sb.append(" primary key");
			}

			sb.append(", ");
		}

		sb.delete(sb.length() - 2, sb.length() - 1);
		sb.append(")");

		String sql = sb.toString();

		Log.d(TAG, "crate table [" + tableName + "]: " + sql);

		db.execSQL(sql);
	}

	public static <T> void dropTable(SQLiteDatabase db, Class<T> clazz) {
		String tableName = "";
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz.getAnnotation(Table.class);
			tableName = table.name();
		}
		String sql = "DROP TABLE IF EXISTS " + tableName;
		Log.d(TAG, "dropTable[" + tableName + "]:" + sql);
		db.execSQL(sql);
	}
	public static final String TEXT = "TEXT";
	public static final String INTEGER = "INTEGER";
	public static final String BIGINT = "BIGINT";
	public static final String FLOAT = "FLOAT";
	public static final String INT = "INT";
	public static final String DOUBLE = "DOUBLE";
	public static final String BLOB = "BLOB";
	private static String getColumnType(Class<?> fieldType) {
		if (String.class == fieldType) {
			return "TEXT";
		}
		if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
			return "INTEGER";
		}
		if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
			return "BIGINT";
		}
		if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
			return "FLOAT";
		}
		if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
			return "INT";
		}
		if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
			return "DOUBLE";
		}
		if (Blob.class == fieldType) {
			return "BLOB";
		}

		return "TEXT";
	}

	// �ϲ�Field���鲢ȥ��,��ʵ�ֹ��˵���Column�ֶ�,��ʵ��Id�������ֶ�λ�ù���
	public static List<Field> joinFields(Field[] fields1, Field[] fields2) {
		Map<String, Field> map = new LinkedHashMap<String, Field>();
		for (Field field : fields1) {
			// ���˵���Column������ֶ�
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}
			Column column = (Column) field.getAnnotation(Column.class);
			map.put(column.name(), field);
		}
		for (Field field : fields2) {
			// ���˵���Column������ֶ�
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}
			Column column = (Column) field.getAnnotation(Column.class);
			if (!map.containsKey(column.name())) {
				map.put(column.name(), field);
			}
		}
		List<Field> list = new ArrayList<Field>();
		for (String key : map.keySet()) {
			Field tempField = map.get(key);
			// �����Id�������λ��.
			if (tempField.isAnnotationPresent(Id.class)) {
				list.add(0, tempField);
			} else {
				list.add(tempField);
			}
		}
		return list;
	}
	/**
	 * 
	 * @param db
	 * @param tableName 表名
	 * @param columnName 列名
	 * @param columnType  列的类型  @see {@link #TEXT}{@link #BLOB}
	 * <li>alter table student add column name varchar;
	 */
	public static <T> void insertColumn(SQLiteDatabase db, String tableName,String columnName,String columnType) {
		String sql = "ALTER TABLE "+tableName+" add column "+columnName+" "+columnType;
		db.execSQL(sql);
	}
}