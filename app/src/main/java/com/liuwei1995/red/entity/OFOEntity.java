package com.liuwei1995.red.entity;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

import java.util.Date;

/**
 * Created by liuwei on 2017/5/4
 */
@Table(name = "OFOEntity")
public class OFOEntity implements Parcelable {
    @Id
    @Column(name = "Id")
    private int Id;//账号id

    @Column(name = "accountId")
    private int accountId;//账号id

    @Column(name = "accountPassword")
    private String accountPassword;//密码

    @Column(name = "account")
    private String account;

    @Column(name = "author")
    private String author = "liuwei";//作者

    @Column(name = "androidVersion")
    private  int androidSDK  = Build.VERSION.SDK_INT;

    @Column(name = "androidVersion")
    private   String androidVersion = Build.VERSION.RELEASE;//作者

    @Column(name = "buildModel")
    private  String buildModel = Build.MODEL;//获取设备型号

    @Column(name = "buildManufacturer")
    private  String buildManufacturer =  Build.MANUFACTURER;//设备厂商  如Xiaomi

    @Column(name = "androidID")
    private  String androidID =  "";//获取设备AndroidID

    @Column(name = "versionName")
    private String versionName;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "userName")
    private String userName;

    @Column(name = "userPassword")
    private String userPassword;//密码

    @Column(name = "IMEI")
    private String IMEI;//


    @Column(name = "versionName")
    private int  versionCode;

    @Column(name = "createTime")
    private Date createTime = new Date();

    @Column(name = "submitState")
    private Integer submitState = 0;

    public Integer getSubmitState() {
        return submitState;
    }

    public void setSubmitState(Integer submitState) {
        this.submitState = submitState;
    }

    public String getBuildModel() {
        return buildModel;
    }

    public void setBuildModel(String buildModel) {
        this.buildModel = buildModel;
    }

    public String getBuildManufacturer() {
        return buildManufacturer;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public void setBuildManufacturer(String buildManufacturer) {
        this.buildManufacturer = buildManufacturer;
    }

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public  String getAndroidVersion() {
        return androidVersion;
    }

    public  int getAndroidSDK() {
        return androidSDK;
    }

    public void setAndroidSDK(int androidSDK) {
        this.androidSDK = androidSDK;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public OFOEntity() {
    }

    @Override
    public String toString() {
        return "OFOEntity{" +
                "accountId=" + accountId +
                ", accountPassword='" + accountPassword + '\'' +
                ", account='" + account + '\'' +
                ", author='" + author + '\'' +
                ", androidSDK=" + androidSDK +
                ", androidVersion='" + androidVersion + '\'' +
                ", buildModel='" + buildModel + '\'' +
                ", buildManufacturer='" + buildManufacturer + '\'' +
                ", androidID='" + androidID + '\'' +
                ", versionName='" + versionName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", versionCode=" + versionCode +
                ", createTime=" + createTime +
                ", submitState=" + submitState +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.accountId);
        dest.writeString(this.accountPassword);
        dest.writeString(this.account);
        dest.writeString(this.author);
        dest.writeInt(this.androidSDK);
        dest.writeString(this.androidVersion);
        dest.writeString(this.buildModel);
        dest.writeString(this.buildManufacturer);
        dest.writeString(this.androidID);
        dest.writeString(this.versionName);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.userName);
        dest.writeString(this.userPassword);
        dest.writeInt(this.versionCode);
        dest.writeLong(this.createTime != null ? this.createTime.getTime() : -1);
        dest.writeValue(this.submitState);
    }

    protected OFOEntity(Parcel in) {
        this.accountId = in.readInt();
        this.accountPassword = in.readString();
        this.account = in.readString();
        this.author = in.readString();
        this.androidSDK = in.readInt();
        this.androidVersion = in.readString();
        this.buildModel = in.readString();
        this.buildManufacturer = in.readString();
        this.androidID = in.readString();
        this.versionName = in.readString();
        this.phoneNumber = in.readString();
        this.userName = in.readString();
        this.userPassword = in.readString();
        this.versionCode = in.readInt();
        long tmpCreateTime = in.readLong();
        this.createTime = tmpCreateTime == -1 ? null : new Date(tmpCreateTime);
        this.submitState = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<OFOEntity> CREATOR = new Creator<OFOEntity>() {
        @Override
        public OFOEntity createFromParcel(Parcel source) {
            return new OFOEntity(source);
        }

        @Override
        public OFOEntity[] newArray(int size) {
            return new OFOEntity[size];
        }
    };
}
