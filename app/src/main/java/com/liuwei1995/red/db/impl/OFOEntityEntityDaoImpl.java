package com.liuwei1995.red.db.impl;

import android.content.Context;

import com.liuwei1995.red.db.DBHelper;
import com.liuwei1995.red.entity.OFOEntity;
import com.tgb.lk.ahibernate.dao.impl.BaseDaoImpl;

public class OFOEntityEntityDaoImpl extends BaseDaoImpl<OFOEntity>{

	public OFOEntityEntityDaoImpl(Context context) {
		super(new DBHelper(context),OFOEntity.class);
	}
	
	public int updateMessageState(String messageState, String messageType) {
		return update("UPDATE "+OFOEntity.class.getSimpleName()+" SET messageState = "+messageState+" WHERE messageType = '"+messageType+"'");
	}
}
