package com.example.username.myscheduler;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class ScheduleEditActivity extends AppCompatActivity {
    private Realm mRealm;
    EditText mDateEdit;
    EditText mTitleEdit;
    EditText mDetailEdit;
    Button mDelete;
    Button mDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_edit);
        //MainActivity.javaと同様にRealmのインスタンス取得
        mRealm = Realm.getDefaultInstance();
        mDateEdit = (EditText) findViewById(R.id.dateEdit);
        mTitleEdit = (EditText) findViewById(R.id.titleEdit);
        mDetailEdit = (EditText) findViewById(R.id.detailEdit);
        mDelete = (Button) findViewById(R.id.delete);
        mDone = (Button) findViewById(R.id.done);

        //更新処理の実装
        //インテントのgetLongExtraメソッドを使い、インテントに格納した「schedule_id」の値を取得して変数scheduleに格納している
        long scheduleId = getIntent().getLongExtra("schedule_id", -1);
        //取得できなかった時にscheduleは「-1」となるため、「-1」の場合は新規登録、それ以外の場合は更新
        if (scheduleId != -1) {
            //更新の場合、Realmのインスタンスを生成した後、idフィールドがscheduleIdと同じレコードをfindAllメソッドで取得して、変数resultsに格納
            RealmResults<Schedule> results = mRealm.where(Schedule.class).equalTo("id", scheduleId).findAll();
            //Realmクラスのfirstメソッドで検索結果から最初のモデルを取得し、そのデータを画面上の日付、タイトル、詳細の各ビューに表示
            Schedule schedule = results.first();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String date = sdf.format(schedule.getDate());
            mDateEdit.setText(date);
            mTitleEdit.setText(schedule.getTitle());
            mDetailEdit.setText(schedule.getDetail());

            //setVisibilityメソッドはビューの表示を制御
            mDelete.setVisibility(View.VISIBLE);
            mDone.setVisibility(View.VISIBLE);

        } else {
            mDelete.setVisibility(View.INVISIBLE);
            mDone.setVisibility(View.INVISIBLE);
        }
    }
    //保存ボタンがタップされた時に呼ばれるメソッド
    public void onSaveTapped(View view) {
        //Dateに変換したい文字列の形式を指定してSimpleDateFormatクラスのインスタンス生成
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dateParse = new Date();
        try {
            //生成したインスタンスのparseメソッドに日付文字列を渡し、Dateのインスタンスを取得
            dateParse = sdf.parse(mDateEdit.getText().toString());
            //文字列が意図しない形式ではない時にParseException例外が発生
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Date date = dateParse;
        long scheduleId = getIntent().getLongExtra("schedule_id", -1);
        if (scheduleId != -1) {
            final RealmResults<Schedule> results = mRealm.where(Schedule.class).equalTo("id", scheduleId).findAll();
            //更新の場合もexecuteTransactionメソッドを使ってトランザクションの開始、終了、キャンセル処理を自動で行う
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //さっきと同じfirstメソッドを使用
                    Schedule schedule = results.first();
                    //モデルのデータを画面上の日付、タイトル、詳細の各ビューで更新して保存
                    schedule.setDate(date);
                    schedule.setTitle(mTitleEdit.getText().toString());
                    schedule.setDetail(mDetailEdit.getText().toString());
                }
            });
            //スナックバーをmakeメソッドで作成し、showメソッドで表示
            //Snakbar make(スナックバーを表示する時の親ビュー,スナックバーで表示したい文字列,表示する時間)
            //setAction(アクションのラベル,View.OnClickListenerを実装したクラスのインスタンス)
            Snackbar.make(findViewById(android.R.id.content), "アップデートしたよ！", Snackbar.LENGTH_LONG)
                    .setAction("戻る", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    })
                    .setActionTextColor(Color.YELLOW)
                    .show();
        } else {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //RealmQueryクラスのmaxメソッドで、Scheduleのidフィールドの最大値を取得し、idの最大値+1を、次に新規に登録するモデルのidとする
                    Number maxId = realm.where(Schedule.class).max("id");
                    long nextId = 0;
                    if (maxId != null) nextId = maxId.longValue() + 1;
                    //RealmインスタンスのcreateObjectメソッドを使い、データを1行追加する
                    Schedule schedule = realm.createObject(Schedule.class, new Long(nextId));
                    //各フィールドに値を設定するとデータの追加が完了する
                    schedule.setDate(date);
                    schedule.setTitle(mTitleEdit.getText().toString());
                    schedule.setDetail(mDetailEdit.getText().toString());
                }
            });
            //ウィンドウの前面に一定時間メッセージを表示するToast機能
            //makeText(アクティビティの指定,トーストで表示したい文字列,トーストを表示する時間)
            Toast.makeText(this, "追加したよ！", Toast.LENGTH_SHORT).show();
            //finishメソッドの実行でScheduleEditActivityが終了し、MainActivityに戻る
            finish();
        }

    }

    //削除処理
    public void onDeleteTapped(View view) {
        final long scheduleId = getIntent().getLongExtra("schedule_id", -1);
        if (scheduleId != -1) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //クエリ要件を満たす最初のオブジェクトを返す
                    Schedule schedule = realm.where(Schedule.class).equalTo("id", scheduleId).findFirst();
                    //DBからレコードを削除
                    schedule.deleteFromRealm();
                }
            });
            Toast.makeText(this, "削除したよ！", Toast.LENGTH_LONG).show();
        }
    }

    //削除処理
    public void onDoneTapped(View view) {
        final long scheduleId = getIntent().getLongExtra("schedule_id", -1);
        if (scheduleId != -1) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //クエリ要件を満たす最初のオブジェクトを返す
                    Schedule schedule = realm.where(Schedule.class).equalTo("id", scheduleId).findFirst();
                    //DBからレコーßドを削除
                    schedule.deleteFromRealm();
                }
            });
            int point = Point.getInstance().getPoint();
            Point.getInstance().setPoint(point + 1);
            Toast.makeText(this, "おめでとう！完了したよ！", Toast.LENGTH_LONG).show();
            startActivity(new Intent(ScheduleEditActivity.this, MainActivity.class));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
