package com.liuwei1995.red.fragment.presenter;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.liuwei1995.red.BaseApplication;
import com.liuwei1995.red.R;
import com.liuwei1995.red.activity.LoginActivity;
import com.liuwei1995.red.adapter.RedAdapter;
import com.liuwei1995.red.adapter.RedViewHolder;
import com.liuwei1995.red.db.impl.OFOEntityEntityDaoImpl;
import com.liuwei1995.red.entity.OFOEntity;
import com.liuwei1995.red.fragment.FragmentHandler;
import com.liuwei1995.red.fragment.FragmentHandlerInterface;
import com.liuwei1995.red.http.HttpCallback;
import com.liuwei1995.red.http.HttpUtils;
import com.liuwei1995.red.http.OkHttpClientUtils;
import com.liuwei1995.red.service.OFOEntitySaveIntentService;
import com.liuwei1995.red.util.UserJSON;
import com.liuwei1995.red.view.RedSnackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuwei on 2017/5/11
 */


public class EyeFragmentPresenter extends FragmentPresenter implements TextWatcher,FragmentHandlerInterface{

    private static final String TAG = EyeFragmentPresenter.class.getSimpleName();

    private FragmentHandler handler = null;


    private static final int H_DISMISS_RED_SNACKBAR = 10;//DISMISS_RED_SNACKBAR
    private static final int H_SHOW_RED_SNACKBAR = 11;
    private static final int H_TYPE_ZERO = 0;
    private static final int H_TYPE_ONE = 1;
    private static final int H_TYPE_TWO = 2;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ACCOUNT = "account";
    private static final String TYPE = "type";
    private int type = 0;
    private String acc = null;
    private boolean isPrepared = false;

    public void onAttach(Context context,android.support.v4.app.Fragment fragment) {
        super.onAttach(context);
        handler = new FragmentHandler(this);
        Bundle arguments = fragment.getArguments();
        if(arguments != null){
            isPrepared = arguments.getBoolean("isPrepared", false);
            type = arguments.getInt(TYPE, 0);
            acc = arguments.getString(ACCOUNT);
        }
    }

    private List<OFOEntity> list = null;
    private List<OFOEntity> list_quanbu = null;
    private RecyclerView rv_content;
    private AutoCompleteTextView actv_license_plate_number;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_eye, container, false);
        list = new ArrayList<>();
        list_quanbu = new ArrayList<>();
        rv_content = $(rootView,R.id.rv_frg_eye_content);
        actv_license_plate_number = $(rootView,R.id.actv_license_plate_number);
        actv_license_plate_number.addTextChangedListener(this);
        watchSearch();
        $(rootView,R.id.btn_Search).setOnClickListener(this);
        rv_content.setLayoutManager(new LinearLayoutManager(mContext));
//        页码 页数 角标  pageNumber pageIndex  搜索
        return rootView;
    }


    public void initData() {
        if (type == 0 || type == 2){
            getNativeData(acc);
        }else if(type == 1){
            if (acc != null && !TextUtils.isEmpty(acc)){
                actv_license_plate_number.setText(acc);
                actv_license_plate_number.setSelection(acc.length());//将光标移至文字末尾
                search();
            }else {
                ofoGetAccountPassword();
            }
        }
    }
    public void onVisible() {
    }
    public void onInvisible(){
        handler.removeMessages(H_SHOW_RED_SNACKBAR);
        dismissRedSnackbar();
    }

    private int pageNumber = 30;
    private int pageIndex = 1;

    private OFOEntityEntityDaoImpl impl;
    public void getNativeData(String acc){
        if(impl == null){
            synchronized (this){
                if(impl == null){
                    impl = new OFOEntityEntityDaoImpl(mContext);
                }
            }
        }
        if (type == 0 || type == 2){
            synchronized (this){
                List<OFOEntity> ofoEntities = null;
                if (type == 0) {
                    if(acc == null || TextUtils.isEmpty(acc)){
                        ofoEntities = impl.rawQuery("SELECT *FROM "+OFOEntity.class.getSimpleName()+" ORDER BY createTime DESC LIMIT 0,30");
                    }else {
                        ofoEntities = impl.rawQuery("SELECT *FROM "+OFOEntity.class.getSimpleName()+" WHERE account LIKE '%"+acc+"%' ORDER BY createTime DESC LIMIT 0,30");
                    }
                }else if (type == 2){
                    if(acc == null || TextUtils.isEmpty(acc)){
                        ofoEntities = impl.rawQuery("SELECT *FROM "+OFOEntity.class.getSimpleName()+" WHERE submitState = 0 ORDER BY createTime DESC");
                    }else {
                        ofoEntities = impl.rawQuery("SELECT *FROM "+OFOEntity.class.getSimpleName()+" WHERE submitState = 0 AND account LIKE '%"+acc+"%' ORDER BY createTime DESC");
                    }
                }
                if(ofoEntities != null){
                    if (pageIndex == 1){
                        list.clear();
                    }
                    for (int i = 0; i < ofoEntities.size(); i++) {
                        list.add(ofoEntities.get(i));
                    }
                    setAdapter();
                }
            }
        }
    }

    private void ofoGetAccountPassword() {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNumber",pageNumber);
        map.put("pageIndex",pageIndex);
        HttpUtils.ofoGetAccountPassword(map, new HttpCallback<JSONObject>() {
            @Override
            public void onResponse(Boolean isSuccess, JSONObject result) {
                if (isSuccess){
                    int code_time = UserJSON.getInt(result, "code_time");
                    if (code_time == 1){
                        int code_success = UserJSON.getInt(result, "code_success");
                        if(code_success == 1){
                            JSONArray content = UserJSON.getJSONArray(result, "content");
                            List<OFOEntity> ofoEntities = UserJSON.parsUser(OFOEntity.class, content);
                            if (pageIndex == 1){
                                list.clear();
                            }
                            if(ofoEntities != null){
                                for (int i = 0; i < ofoEntities.size(); i++) {
                                    list.add(ofoEntities.get(i));
                                }
                                list_quanbu.clear();
                                list_quanbu.addAll(list);
                            }
                            setAdapter();
                        }else {
                            Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                        LoginActivity.newStartActivity(mContext);
                    }
                }else Toast.makeText(mContext, "网络开小差啦", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private RedAdapter<OFOEntity> adapter;
    private void setAdapter(){
        if(adapter == null){
            adapter = new RedAdapter<OFOEntity>(list,R.layout.item_fragment_eye) {
                @Override
                public void convert(RedViewHolder holder, final OFOEntity item, int position) {
                    holder.setText(R.id.tv_account,item.getAccount());
                    holder.setText(R.id.tv_accountPassword,item.getAccountPassword());
                    Button btn_submit = holder.getView(R.id.btn_submit);
                    btn_submit.setVisibility(View.VISIBLE);
                    if(type == 0 || type == 2){
                        Integer submitState = item.getSubmitState();
                        if(submitState == 1){
                            btn_submit.setVisibility(View.GONE);
                        }else {
                            btn_submit.setText("提交");
                            btn_submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    submit(item);
                                }
                            });
                        }
                    }else if (type == 1){
                        Integer submitState = item.getSubmitState();
                        if(submitState == 1){
                            btn_submit.setVisibility(View.GONE);
                        }else {
                            btn_submit.setVisibility(View.VISIBLE);
                            btn_submit.setText("保存");
                            btn_submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    save(item);
                                }
                            });
                        }
                    }
                }
            };
            rv_content.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    private void save(final OFOEntity item) {
        if(item == null)return;
        synchronized (this){
            OFOEntityEntityDaoImpl impl = new OFOEntityEntityDaoImpl(mContext);
            List<OFOEntity> ofoEntities = impl.rawQuery("SELECT *FROM "+OFOEntity.class.getSimpleName()+" WHERE account = '" + item.getAccount()+"'");
            if(ofoEntities != null && !ofoEntities.isEmpty()){
                item.setSubmitState(1);
                Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            }else {
                item.setSubmitState(1);
                long insert = impl.insert(item);
                if(insert > 0){
                    OFOEntitySaveIntentService.startActionFoo(mContext,item);
                    Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                }
            }
            setAdapter();
        }
    }
    private void submit(final OFOEntity item){
        Map<String, Object> map = new HashMap<>();
        String account = item.getAccount();
        map.put("account",account);//提交密码 submitPassword

//        String phoneNumber = item.getPhoneNumber();
        map.put("phoneNumber", PhoneUtils.getPhoneNumber(mContext));

        String androidID = DeviceUtils.getAndroidID();
        item.setAndroidID(androidID);
        map.put("androidID",item.getAndroidID());

        item.setIMEI(PhoneUtils.getIMEI());
        map.put("IMEI",item.getIMEI());

        String accountPassword = item.getAccountPassword();
        map.put("accountPassword",accountPassword);

        int androidSDK = item.getAndroidSDK();
        map.put("androidSDK",androidSDK);

//        String versionName = item.getVersionName();
        map.put("versionName", BaseApplication.versionName);

//        int versionCode = item.getVersionCode();
        map.put("versionCode",BaseApplication.versionCode);

        String androidVersion = item.getAndroidVersion();
        map.put("androidVersion",androidVersion);

        String buildManufacturer = item.getBuildManufacturer();
        map.put("buildManufacturer",buildManufacturer);

        String buildModel = item.getBuildModel();
        map.put("buildModel",buildModel);
        map.put("deviceType","Android");
        map.put("submitPassword","");

        HttpUtils.saveAccountPassword(map, new HttpCallback<JSONObject>() {
            @Override
            public void onResponse(Boolean isSuccess, JSONObject result) {
                if (isSuccess){
                    int code_time = UserJSON.getInt(result, "code_time");
                    if (code_time == 1) {
                        int code_success = UserJSON.getInt(result, "code_success");
                        if (code_success == 1) {
                            if (item.getSubmitState() != 1){
                                OFOEntityEntityDaoImpl impl = new OFOEntityEntityDaoImpl(mContext);
                                item.setSubmitState(1);
                                impl.update(item);
                            }
                            setAdapter();
                            Toast.makeText(mContext, "提交成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                        LoginActivity.newStartActivity(mContext);
                    }
                }else ToastUtils.showShortToast("网络开小差啦");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_Search){
            if(type == 1){
                search();
            }
        }
    }
    private RedSnackbar redSnackbar;

    private void dismissRedSnackbar(){
        if(redSnackbar != null){
            redSnackbar.dismiss();
        }
    }
    private void showRedSnackbar(){
        if(redSnackbar == null){
            synchronized (this){
                if (redSnackbar == null)
                    redSnackbar = new RedSnackbar(rv_content.getContext(), R.layout.snackbar_eye_activity_submit) {
                        @Override
                        public void setCustomView(Context mContext,View customView) {
                            setText(R.id.actv_license_plate_number,actv_license_plate_number.getText().toString());
                            setOnClickListener(R.id.btn_submit);
                        }
                        @Override
                        public void onClick(View v) {
                            AutoCompleteTextView tv_number = getView(R.id.actv_license_plate_number);
                            String account = tv_number.getText().toString().trim();
                            if(TextUtils.isEmpty(account) || account.length() < 4){
                                tv_number.setError("号码不正确");return;
                            }
                            AutoCompleteTextView tv_number_password = getView(R.id.actv_license_plate_numbed_password);
                            String accountPassword = tv_number_password.getText().toString().trim();
                            if(TextUtils.isEmpty(accountPassword) || accountPassword.length() != 4){
                                tv_number_password.setError("密码格式不正确");return;
                            }
                            dismiss();
                            OFOEntity item = new OFOEntity();
                            item.setAccount(account);
                            item.setPhoneNumber(PhoneUtils.getPhoneNumber(mContext));
                            String androidID = DeviceUtils.getAndroidID();
                            item.setAndroidID(androidID);
                            item.setIMEI(PhoneUtils.getIMEI());
                            item.setAccountPassword(accountPassword);
                            item.setVersionName(BaseApplication.versionName);
                            item.setVersionCode(BaseApplication.versionCode);
                            submit(item);
                        }
                    };
            }
        }else {
            TextView view = redSnackbar.getView(R.id.actv_license_plate_number);
            view.setError(null);
            view.setText(actv_license_plate_number.getText());
        }
        redSnackbar.make(rv_content, 60*60*1000);
    }

    private void search() {
        OkHttpClientUtils.cancel(mContext);
        String trim = actv_license_plate_number.getText().toString().trim();
        if(TextUtils.isEmpty(trim)){
            actv_license_plate_number.setError("空");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        if(TextUtils.isEmpty(trim))return;
        map.put("account",trim);
        map.put("pageNumber",pageNumber);
        map.put("pageIndex",pageIndex);
        HttpUtils.ofoSearchAccountPassword(map,mContext,new HttpCallback<JSONObject>() {
            @Override
            public void onResponse(Boolean isSuccess, JSONObject result) {
                if (isSuccess){
                    int code_time = UserJSON.getInt(result, "code_time");
                    if (code_time == 1){
                        int code_success = UserJSON.getInt(result, "code_success");
                        if(code_success == 1){
                            JSONArray content = UserJSON.getJSONArray(result, "content");
                            List<OFOEntity> ofoEntities = UserJSON.parsUser(OFOEntity.class, content);
                            if (pageIndex == 1){
                                list.clear();
                            }
                            if(ofoEntities != null && !ofoEntities.isEmpty()){
                                for (int i = 0; i < ofoEntities.size(); i++) {
                                    list.add(ofoEntities.get(i));
                                }
                            }else {
                                Toast.makeText(mContext, "该车牌号暂时没有密码", Toast.LENGTH_SHORT).show();
                                handler.removeMessages(H_SHOW_RED_SNACKBAR);
                                Message message = handler.obtainMessage();
                                message.what = H_SHOW_RED_SNACKBAR;
                                handler.sendMessageDelayed(message,2000);
                            }
                            setAdapter();
                        }else {
                            Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(mContext, UserJSON.getString(result,"message"), Toast.LENGTH_SHORT).show();
                        LoginActivity.newStartActivity(mContext);
                    }
                }else Toast.makeText(mContext, "网络开小差啦", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(type == H_TYPE_ZERO || type == H_TYPE_TWO){
            getNativeData(s.toString());
        }else if (type == H_TYPE_ONE){
            handler.removeMessages(H_SHOW_RED_SNACKBAR);
            handler.removeMessages(H_DISMISS_RED_SNACKBAR);
            handler.sendEmptyMessage(H_DISMISS_RED_SNACKBAR);
            synchronized (this){
                handler.removeMessages(H_TYPE_ONE);
                Message message = handler.obtainMessage();
                message.obj = s;
                message.what = type;
                handler.sendMessageDelayed(message,500);
            }
        }
    }
    /**
     * 方法说明:监控软键盘的的搜索按钮
     */
    private void watchSearch() {
        if (type == H_TYPE_ONE)
            actv_license_plate_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        // 先隐藏键盘
                        ((InputMethodManager) actv_license_plate_number.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(((Activity)mContext).getCurrentFocus().getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                        // 搜索，进行自己要的操作...
                        search();
                        return true;
                    }
                    return false;
                }
            });
    }

    @Override
    public void onDetach() {
        list = null;
        list_quanbu = null;
        adapter = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        list = null;
        list_quanbu = null;
        adapter = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if(redSnackbar != null){
            redSnackbar.dismiss();
            redSnackbar = null;
        }
        list = null;
        list_quanbu = null;
        adapter = null;
        if(handler != null){
            handler.removeMessages(H_DISMISS_RED_SNACKBAR);
            handler.removeMessages(H_SHOW_RED_SNACKBAR);
            handler.removeMessages(H_TYPE_TWO);
            handler.removeMessages(H_TYPE_ONE);
            handler.removeMessages(H_TYPE_ZERO);
            handler = null;
        }
        super.onDestroy();
    }

    @Override
    public void handleMessage(Message msg) {
        if(msg.what == H_TYPE_ONE){
            if(msg.obj != null && !TextUtils.isEmpty(msg.obj.toString())){
                Toast.makeText(mContext, "开始搜索", Toast.LENGTH_SHORT).show();
                search();
            }else {
                Toast.makeText(mContext, "为空", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < list_quanbu.size(); i++) {
                    list.add(list_quanbu.get(i));
                }
                setAdapter();
            }
        }else if (msg.what == H_DISMISS_RED_SNACKBAR){
            dismissRedSnackbar();
        }else if (msg.what == H_SHOW_RED_SNACKBAR){
            showRedSnackbar();
        }
    }
}
