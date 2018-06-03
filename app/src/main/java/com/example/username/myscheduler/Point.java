package com.example.username.myscheduler;

/**
 * Created by kisamasaki on 2018/03/30.
 */

public class Point {

    private int point = 0;

    private static Point instance = new Point();

    public static Point getInstance(){
        return instance;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

}
