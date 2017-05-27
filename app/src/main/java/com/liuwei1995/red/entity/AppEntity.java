package com.liuwei1995.red.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dell on 2017/4/20
 */

public class AppEntity implements Parcelable {

    /**
     * name : WeChat
     * VersionName : 6.5.7
     * versionCode : 1041
     * url : https://pan.baidu.com/s/1pK7vlLx
     */

    private String name;
    private String VersionName;
    private int versionCode;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionName() {
        return VersionName;
    }

    public void setVersionName(String VersionName) {
        this.VersionName = VersionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.VersionName);
        dest.writeInt(this.versionCode);
        dest.writeString(this.url);
    }

    public AppEntity() {
    }

    protected AppEntity(Parcel in) {
        this.name = in.readString();
        this.VersionName = in.readString();
        this.versionCode = in.readInt();
        this.url = in.readString();
    }

    public static final Creator<AppEntity> CREATOR = new Creator<AppEntity>() {
        @Override
        public AppEntity createFromParcel(Parcel source) {
            return new AppEntity(source);
        }

        @Override
        public AppEntity[] newArray(int size) {
            return new AppEntity[size];
        }
    };

    @Override
    public String toString() {
        return "AppEntity{" +
                "name='" + name + '\'' +
                ", VersionName='" + VersionName + '\'' +
                ", versionCode=" + versionCode +
                ", url='" + url + '\'' +
                '}';
    }
}
