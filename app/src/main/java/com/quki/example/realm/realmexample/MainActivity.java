package com.quki.example.realm.realmexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private int count = 1;
    private Realm realm;
    private TextView status;
    private RealmChangeListener mListener;
    private RealmResults<Data> realmResultsAsync;
    private Button insert,init,change,startService,stopService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get Realm instance at this tread
        realm = Realm.getInstance(getApplicationContext());
        //final RealmResults<Data> mDataResult = realm.where(Data.class).findAll();
        realmResultsAsync  = realm.where(Data.class).findAllAsync(); // find data asynchronous

        insert = (Button) findViewById(R.id.insert);
        init = (Button) findViewById(R.id.init);
        change = (Button) findViewById(R.id.change);
        status = (TextView) findViewById(R.id.status);
        startService = (Button) findViewById(R.id.startService);
        stopService = (Button) findViewById(R.id.stopService);

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

                Toast.makeText(getApplicationContext(), realmResultsAsync.size() + "", Toast.LENGTH_SHORT).show();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        Data mChangedData = bgRealm.where(Data.class).findFirst();
                        mChangedData.setName("data changed");
                    }
                }, new Realm.Transaction.Callback() {
                    @Override
                    public void onSuccess() {

                        status.setText("=====================\n");
                        for (int i = 0; i < realmResultsAsync.size(); i++) {
                            status.append(realmResultsAsync.get(i).getName());
                            status.append("\n");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                realmResultsAsync.clear();

                realm.commitTransaction();
                Toast.makeText(getApplicationContext(), realmResultsAsync.size() + "", Toast.LENGTH_SHORT).show();
            }
        });

        startService.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startService();
            }
        });
        stopService.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                stopService();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // register callback
        mListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                status.setText("=====================\n");
                for (int i = 0; i < realmResultsAsync.size(); i++) {
                    status.append(realmResultsAsync.get(i).getName());
                    status.append("\n");
                }
            }
        };
        realmResultsAsync.addChangeListener(mListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // to protect memory leak removing listener
        realm.removeChangeListener(mListener);
    }

    protected void startService(){
        Intent intentService = new Intent(this, MainService.class);
        startService(intentService);
    }
    protected void stopService(){
        Intent intentService = new Intent(this, MainService.class);
        stopService(intentService);
    }
}
