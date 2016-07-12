/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bbb.dao;

/**
 *
 * @author Chandrabhan
 */
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class CreateTimer {

    public static Timer timer = new Timer();

    public static Calendar getFirstTime() {
        Calendar cal = Calendar.getInstance();

        int currentMinute = cal.get(Calendar.MINUTE);

        if (currentMinute < 15) {
            System.out.println("in 15 min");
            cal.set(Calendar.MINUTE, 15);
        }
        if (currentMinute < 10) {
            System.out.println("in 10 min");
            cal.set(Calendar.MINUTE, 10);
        }
        if (currentMinute < 5) {
            System.out.println("in 5 min");
            cal.set(Calendar.MINUTE, 5);
        }
        if (currentMinute >= 15) {
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
            cal.set(Calendar.MINUTE, 0);
            System.out.println("in 0 min");
        }

        cal.set(Calendar.SECOND, 0);
        System.out.println("in actual min"+cal);
        return cal;
    }

//    public static void main(String... args) {
//        Calendar firstTaskTime = getFirstTime();
//        System.out.println("Task will start at: " + firstTaskTime.getTime());
//        timer.schedule(new MyTask(), firstTaskTime.getTime(), 1000 * 60 * 15);
//    }
}

//class MyTask extends TimerTask {
//    public void run() {
//        System.out.println("running task");
//    }
//}