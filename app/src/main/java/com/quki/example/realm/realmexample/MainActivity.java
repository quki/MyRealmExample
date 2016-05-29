package com.quki.example.realm.realmexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 이 스레드에서 Realm 인스턴스 얻기
        final Realm realm = Realm.getInstance(getApplicationContext());
        final RealmResults<Data> mDataResult = realm.where(Data.class).findAll(); //자동갱신
        final Button insert = (Button) findViewById(R.id.insert);
        final Button find = (Button) findViewById(R.id.find);
        final Button init = (Button) findViewById(R.id.init);
        final Button change = (Button) findViewById(R.id.change);
        final TextView status = (TextView) findViewById(R.id.status);

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Data mData = new Data();
                mData.setName("my data" + count);
                mData.setNumber(count);
                realm.beginTransaction();
                realm.copyToRealm(mData);
                realm.commitTransaction();
                count++;

                Toast.makeText(getApplicationContext(),mDataResult.size()+"",Toast.LENGTH_SHORT).show();
            }
        });
        change.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        Data mChangedData = bgRealm.where(Data.class).findFirst();
                        mChangedData.setName("바꼈어용");
                    }
                }, new Realm.Transaction.Callback() {
                    @Override
                    public void onSuccess() {

                        status.setText("=====================\n");
                        for (int i = 0; i < mDataResult.size(); i++) {
                            status.append(mDataResult.get(i).getName());
                            status.append("\n");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // All changes to data must happen in a transaction
                realm.beginTransaction();

                // remove single match
                /*results.remove(0);
                results.removeLast();*/

                /*// remove a single object
                Data d = results.get(5);
                d.removeFromRealm();*/

                // Delete all matches
                mDataResult.clear();

                realm.commitTransaction();
                Toast.makeText(getApplicationContext(),mDataResult.size()+"",Toast.LENGTH_SHORT).show();
            }
        });
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                status.setText("=====================\n");
                for (int i = 0; i < mDataResult.size(); i++) {
                    status.append(mDataResult.get(i).getName());
                    status.append("\n");
                }


            }
        });




    }
}
