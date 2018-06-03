package com.example.username.myscheduler;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;


//データベースから取得した結果をリストビューに表示するための専用アダプターRealmBaseAdapterを継承
public class ScheduleAdapter extends RealmBaseAdapter<Schedule> {

    //ViewHolderクラスはViewオブジェクトを保持するためのもの
    private static class ViewHolder {
        TextView date;
        TextView title;
    }
    //コンストラクタ。データのnullを許可する？参考書には言及されてなかった
    public ScheduleAdapter(@Nullable OrderedRealmCollection<Schedule> data) {
        super(data);
    }

    @Override
    //getViewでテーブルのセルを生成し、値をセットする処理を記述
    //getViewはリストビューのセルのデータが必要になるたびに呼び出し、表示するビューを戻り値として返す
    //getView(リストビューのセルの位置を受け取る,既に作成済みのセルを表すビューを受け取る。nullの場合もある,親のリストビューを受け取る)
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //ビューが入っているかnullか
        if(convertView == null) {
            //LayoutInflaterクラスはXMLファイルからビューを生成
            //LayoutInflaterのクラスメソッドfromを使用してインスタンスを作成
            //fromメソッドの引数は、getViewに第3引数として渡される親のリストビューを使用して取得している
            //inflateメソッドはXMLファイルからビューを生成
            //inflate(ビューを作成したいレイアウトXMLのリソースID,第三引数がfalseの場合作成するビューをアタッチするビューを指定,XMLファイルから作成したいビューを返したい場合falseを返す
            convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            //オブジェクトを作る
            viewHolder = new ViewHolder();
            viewHolder.date = (TextView) convertView.findViewById(android.R.id.text1);
            viewHolder.title = (TextView) convertView.findViewById(android.R.id.text2);
            //setTagメソッドでビューにタグをつける
            convertView.setTag(viewHolder);
        } else {
            //タグ付けすることで、タグとしてつけておいたViewHolderからgetTagメソッドで取り出すことで効率的に処理できる(ListViewの表示を高速化するためのテクニック)
            viewHolder = (ViewHolder)convertView.getTag();
        }
        //ここからはデータを取り出して表示するための処理
        //RealmBaseAdapterクラスのadapterDataフィールドにはデータのリストが入っている
        //getメソッドで何番目のメソッドを取得するのか指定すれば、そのデータを取得できる。getViewメソッドの第1引数positionを使ってデータを取り出す
        Schedule schedule = adapterData.get(position);
        //どのような形式で文字列に変換したいのかを指定してSimpleDateFormatクラスのインスタンス生成
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        //フォーマットどおりに変換した文字列の取得
        String formatDate = sdf.format(schedule.getDate());
        viewHolder.date.setText(formatDate);
        viewHolder.title.setText(schedule.getTitle());
        //設定が終わったセル用ビューをgetViewの戻り値として戻せば、リストビューの行として表示される
        return convertView;
    }
}
