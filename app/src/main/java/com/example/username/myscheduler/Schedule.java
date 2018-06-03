package com.example.username.myscheduler;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

//データを格納するモデルの作成
//「モデル」とはデータを格納するためのクラスで1レコード分のデータを保持する

public class Schedule extends RealmObject {
    //@PrimaryKeyでidを主キーとして指定
    @PrimaryKey
    private long id;
    private Date date;
    private String title;
    private String detail;

    //ゲッターとセッターの作成
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
