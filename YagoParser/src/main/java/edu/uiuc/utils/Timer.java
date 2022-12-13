package edu.uiuc.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Timer {
    private final int mPrintInterval;
    private int mCount = 0;
    private final NumberFormat mFormatter = new DecimalFormat("#0.00000");
    private long mStartTime;

    public Timer(int printInterval) {
        this.mPrintInterval = printInterval;
        this.start();
    }

    public void start() {
        this.mStartTime = System.currentTimeMillis();
    }

    public void tik() {
        this.mCount++;
        if (this.mCount % this.mPrintInterval == 0) {
            this.printTime();
        }
    }

    public void printTime(){
        System.out.println("Count: " + this.mCount + "; " + this.mFormatter.format(
                (System.currentTimeMillis() - this.mStartTime) / 1000d) + " seconds");
    }

    public int getCount() {
        return this.mCount;
    }
}
