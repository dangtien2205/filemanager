package com.example.tienbi.filemanager;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    static ArrayList<TFile> selectionCopy;
    static int varCopyCut;
    private ExecutorService es = Executors.newFixedThreadPool(3);

    public void execRunnable(Runnable r) {
        if (!es.isShutdown()) {
            es.execute(r);
        }
    }

    public static void setSelectionCopy(List<TFile> selectionCopy1) {
        selectionCopy = new ArrayList<>();
        for (int i = 0; i < selectionCopy1.size(); i++) {
            selectionCopy.add(selectionCopy1.get(i));
        }
    }

    public static ArrayList<TFile> getSelectionCopy() {
        return selectionCopy;
    }

    public static void onClearList() {
        selectionCopy = null;
    }

    public static int getVarCopyCut() {
        return varCopyCut;
    }

    public static void setVarCopyCut(int varCopyCut) {
        App.varCopyCut = varCopyCut;
    }
}
