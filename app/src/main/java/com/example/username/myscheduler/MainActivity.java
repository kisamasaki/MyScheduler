package com.example.username.myscheduler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

//AppCompatActivityは、サポートライブラリを利用するための基本となるアクティビティクラス
// サポートライブラリとは新しいAPIレベルで追加された機能を古いAPIレベルでも使えるようにしたり、その他便利な機能を利用できるようにするプログラムをひとまとまりにしたファイル
public class MainActivity extends AppCompatActivity {

    //Realmクラスのメンバ変数の用意
    private Realm mRealm;
    //ListViewクラスのメンバ変数の用意
    private ListView mListView;

    ImageView image1;
    ImageView image2;
    ImageView image3;

    //あるクラスを継承した時、その親クラスのメソッドを子クラスで定義し直すことをオーバーライドと呼ぶ
    //親クラスのメソッドの上書きで、コンパイラに「このメソッドは上書きです」ということを伝えるために@Overrideアノテーションをつける
    //上書きなので、メソッド名、引数の型、戻り値の型すべてが親クラスと同じでないといけない
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //親クラスのonCreate()メソッドを呼び出している
        super.onCreate(savedInstanceState);
        //アプリで表示する画面の設定
        //「R.layout.activity_main」という記述は「res/layout/activity_main.xml」ファイルを指す定数
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ボタン押下時にMainActivityからScheduleEditActivityへの画面遷移
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScheduleEditActivity.class));
            }
        });

        //setDefaultConfigurationメソッドに設定したデーターベースを取得し、Realmインスタンスを返す。これでRealmデータベースを使用する準備が完了
        mRealm = Realm.getDefaultInstance();
        //findViewByIdメソッドでListViewのインスタンスを取得
        //リストビューは、情報を一覧表示することができるビューで、ListViewクラスを使って生成
        mListView = (ListView) findViewById(R.id.listView);
        //Realmインスタンスからデータを取得するクエリの発行。クエリの発行にはwhereメソッドで対象となるclassファイル名を指定、その後findAllメソッドで全てのスケジュールを取得し、RealmResultsクラスの変数に格納
        //「クエリ」とはデータの抽出条件という意味
        RealmResults<Schedule> schedules = mRealm.where(Schedule.class).findAll();
        //用意したScheduleAdapterクラスのインスタンスを生成
        ScheduleAdapter adapter = new ScheduleAdapter(schedules);
        //setAdapterメソッドでアダプターのインスタンスを渡して設定
        //setAdapterメソッドの機能：必要に応じて子ビューを提供するアダプターを設定する
        //これによりデーターベースから取得した全てのスケジュールをリストビューに表示する準備が整った
        mListView.setAdapter(adapter);

        //MainActivityのリストビュータップ時にScheduleEditActivityに画面遷移するコード
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //void onItemClick(AdapterView<?> タップされた項目を含むリストビューのインスタンス, View タップされた項目, int タップされた項目のリスト上の位置,long タップされた項目のid)
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                //getItemAtPositionメソッドはリスト内の指定された位置に関連するデータを取得する
                Schedule schedule = (Schedule) parent.getItemAtPosition(position);
                //取得したScheduleインスタンスからidを取得し、インテントに「schedule_id」として格納することで、idをScheduleEditActivityに渡している
                startActivity(new Intent(MainActivity.this, ScheduleEditActivity.class).putExtra("schedule_id", schedule.getId()));
            }
        });

        image1 = (ImageView)findViewById(R.id.image1);
        image2 = (ImageView)findViewById(R.id.image2);
        image3 = (ImageView)findViewById(R.id.image3);

        if(Point.getInstance().getPoint()>=1){
            image1.setVisibility(View.VISIBLE);
        } else {
            image1.setVisibility(View.GONE);
        }
        if(Point.getInstance().getPoint()>=3){
            image2.setVisibility(View.VISIBLE);
        } else {
            image2.setVisibility(View.GONE);
        }
        if(Point.getInstance().getPoint()>=5){
            image3.setVisibility(View.VISIBLE);
        } else {
            image3.setVisibility(View.GONE);
        }

        ((TextView)findViewById(R.id.totalPoint)).setText(String.format("%d",Point.getInstance().getPoint()));


    }

    public void onShopButtonTapped(View view){
        Intent intent = new Intent(this,ShopActivity.class);
        startActivity(intent);
    }

    //アクティビティの終了処理、closeメソッドでRealmのインスタンスを破棄する
    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}