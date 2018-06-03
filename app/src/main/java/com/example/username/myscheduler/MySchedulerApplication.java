package com.example.username.myscheduler;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

//Realm使用時にデータベースの設定処理がアプリケーション起動時に実行される必要があるので下記クラスが作成された
public class MySchedulerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Realmの初期化
        Realm.init(this);
        //データベースの各種設定を行うRealmConfigurationクラスのインスタンス取得。
        //RealmConfiguration.Builderクラスのbuildメソッドを使用している
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        //使用される値の保存
        Realm.setDefaultConfiguration(realmConfig);
    }

}