package com.example.tienbi.filemanager.model;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by TienBi on 29/09/2016.
 */
public class MemorySize {
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static double getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        double blockSize = stat.getBlockSize();
        double availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static double getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        double blockSize = stat.getBlockSize();
        double totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static double getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            double blockSize = stat.getBlockSize();
            double availableBlocks = stat.getAvailableBlocks();
            return blockSize * availableBlocks ;
        } else {
            return 0;
        }
    }

    public static double getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            double blockSize = stat.getBlockSize();
            double totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return 0;
        }
    }
}
