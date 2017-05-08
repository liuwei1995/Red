package com.liuwei1995.red.db;

import android.content.Context;

import com.liuwei1995.red.entity.OFOEntity;
import com.tgb.lk.ahibernate.util.MyDBHelper;

public class DBHelper extends MyDBHelper{
	
	private static final String DBNAME = "red_ofo.db";// 数据库名
	private static final int DBVERSION = 3;
	
	private static final Class<?>[] clazz = {OFOEntity.class};// 要初始化的表

	public DBHelper(Context context) {
		super(context, DBNAME, null, DBVERSION, clazz);
	}

}