package com.liuwei1995.red.util;

import android.net.TrafficStats;

/**
 * Created by liuwei on 2017/4/14
 */

public class TrafficStatsUtils {
//    static long getMobileRxBytes()//获取通过Mobile连接收到的字节总数，但不包含WiFi
//    static long getMobileRxPackets()//获取Mobile连接收到的数据包总数
//    static long getMobileTxBytes()//Mobile发送的总字节数
//    static long getMobileTxPackets()//Mobile发送的总数据包数
//    static long getTotalRxBytes()//获取总的接受字节数，包含Mobile和WiFi等
//    static long getTotalRxPackets()//总的接受数据包数，包含Mobile和WiFi等
//    static long getTotalTxBytes()//总的发送字节数，包含Mobile和WiFi等
//    static long getTotalTxPackets()//发送的总数据包数，包含Mobile和WiFi等
//    static long getUidRxBytes(int uid)//获取某个网络UID的接受字节数
//    static long getUidTxBytes(int uid) //获取某个网络UID的发送字节数
    public static long get(){

        return TrafficStats.getMobileRxBytes();
    }
}
