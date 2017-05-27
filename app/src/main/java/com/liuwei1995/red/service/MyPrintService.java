package com.liuwei1995.red.service;

import android.annotation.TargetApi;
import android.os.Build;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.support.annotation.Nullable;

/**
 * Created by linxins on 17-4-15.
 */

@TargetApi(Build.VERSION_CODES.KITKAT)
public class MyPrintService extends PrintService {

    @Nullable
    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        return null;
    }

    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {

    }

    @Override
    protected void onPrintJobQueued(PrintJob printJob) {

    }
}
