package com.lichuan.test01;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lichuan.test01.datamodel.RootData;
import com.lichuan.test01.msg.SourceObject;
import com.lichuan.test01.service.Loading;
import com.lichuan.test01.service.MyDB;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ServiceConnection mConnection;
    private Loading mLoadingService;
    private MyDB mMyDB;

    private MyReceiver mReceiver;

    private static final String TAG_MainActivity = "MainActivity";

    public final static String FLAG_MY_CLOTH_MAIN_RECEIVER = "android.intent.action.MyCloth_Main_RECEIVER";
    public final static String FLAG_EXTRA_PARAM = "progress";

    private static final String TAG_WaitFragment = "Wait";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_MainActivity, "onCreate begin");

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction()
            //        .add(R.id.container, new PlaceholderFragment())
            //        .commit();
            loadWaitFragment();
        }

        Log.d(TAG_MainActivity, "onCreate after loadWaitFragment.");

        mMyDB = new MyDB(getBaseContext());

        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(FLAG_MY_CLOTH_MAIN_RECEIVER);
        //注册
        registerReceiver(mReceiver, filter);

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mLoadingService = ((Loading.MyBinder) iBinder).getService();
                Log.d(TAG_MainActivity, "onServiceConnected");
                mLoadingService.setMyDB(mMyDB);
                mLoadingService.pullDataFromDB();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(TAG_MainActivity, "onServiceDisconnected");
                mLoadingService.setMyDB(null);
                mLoadingService = null;
            }
        };

        Intent intent = new Intent(MainActivity.this, Loading.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  //屏幕宽
        int height = dm.heightPixels;  //屏幕高
        Log.d(TAG_MainActivity, "onCreate width=" + width + ", height=" + height);

        Log.d(TAG_MainActivity, "onCreate finish");
    }

    private void loadWaitFragment() {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.container, new WaitFragment(), TAG_WaitFragment);
        trans.commit();
    }

//

    //public RootData getRootData(){

    //return mRoot;

    //}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        Log.d(TAG_MainActivity, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onStop() {
        Log.d(TAG_MainActivity, "onStop");
        super.onStop();
    }

    public void onResume() {
        Log.d(TAG_MainActivity, "onResume");
        super.onResume();
    }

    private void RemoveSomeComponent() {
        Log.d(TAG_MainActivity, "RemoveSomeComponent begin");
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
            Log.d(TAG_MainActivity, "RemoveSomeComponent after unregisterReceiver");
        }

        if (mConnection != null) {
            unbindService(mConnection);
            Intent intent = new Intent(MainActivity.this, Loading.class);
            stopService(intent);

            Log.d(TAG_MainActivity, "RemoveSomeComponent after stopService");
            mConnection = null;
        }
        Log.d(TAG_MainActivity, "RemoveSomeComponent End");
    }

    protected void onDestroy() {
        Log.d(TAG_MainActivity, "onDestroy");

        RemoveSomeComponent();
        Log.d(TAG_MainActivity, "onDestroyFinish");
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG_MainActivity, " == onConfigurationChanged occur");
    }

    public static class WaitFragment extends Fragment {
        private View contentView;

        public WaitFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            contentView = inflater.inflate(R.layout.wait_fragment, container, false);
            return contentView;
        }

        public void updateTip(int progress) {
            TextView tv = (TextView) contentView.findViewById(R.id.specialId);
            String waitStr = getResources().getString(R.string.waiting_text);
            StringBuilder sb;
            sb = new StringBuilder(waitStr);
            sb.append("...");
            sb.append("  ");
            sb.append(progress);
            tv.setText(sb.toString());
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private ListView mGoodList;
        private ArrayList<HashMap<String, String>> mDataSource;
        private View contentView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            contentView = inflater.inflate(R.layout.fragment_main, container, false);
            initGoodList();
            return contentView;
        }

        private void initGoodList() {
            mGoodList = (ListView) contentView.findViewById(R.id.GoodList1);
            if (mGoodList == null) {
                return;
            }

            mDataSource = loadData();
            SimpleAdapter listItemAdapter = new SimpleAdapter(contentView.getContext(), mDataSource,
                    R.layout.good_item,
                    new String[]{"ItemName"},
                    new int[]{R.id.itemDescription});
            mGoodList.setAdapter(listItemAdapter);
            mGoodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    HashMap<String, String> tmp = PlaceholderFragment.this.mDataSource.get(i);
                    String value = tmp.get("ItemName");
                    String formatStr = getResources().getString(R.string.toast_format_listItem);
                    String Message = String.format(formatStr, value);
                    Context c = PlaceholderFragment.this.contentView.getContext();
                    if (c != null) {
                        Toast.makeText(c, Message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private ArrayList<HashMap<String, String>> loadData() {
            String[] names = new String[]{"suit1", "suit2"};

            ArrayList<HashMap<String, String>> tmp = new ArrayList<HashMap<String, String>>();
            for (String s : names) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemName", s);
                tmp.add(map);
            }
            return tmp;
        }
    }

    public static class ErrorFragment extends Fragment {
        private View contentView;

        public ErrorFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            contentView = inflater.inflate(R.layout.loading_error_fragment, container, false);
            return contentView;
        }
    }

    public static class DescriptionFragment extends Fragment {
        private View contentView;
        private Button mNextStep;

        public DescriptionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            contentView = inflater.inflate(R.layout.loading_finish_description_fragment, container, false);
            assert contentView != null;
            mNextStep = (Button) contentView.findViewById(R.id.btn_next_step);
            if (mNextStep != null) {
                mNextStep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Activity act = getActivity();
                        if (act instanceof MainActivity) {
                            MainActivity main = (MainActivity) act;
                            main.RemoveSomeComponent();
                        }

                        RootData root = RootData.getInstance();
                        if (root.getBoxesCount() == 0 && root.getClothesCount() == 0) {
                            Intent it = new Intent();
                            it.putExtra(SourceObject.SourceName, new SourceObject(SourceObject.SOURCE_START));
                            it.setClass(act.getBaseContext(), AddClothActivity.class);
                            startActivity(it);
                        } else {
                            Intent it = new Intent();
                            it.putExtra(SourceObject.SourceName, new SourceObject(SourceObject.SOURCE_START));
                            it.setClass(act.getBaseContext(), ClothListActivity.class);
                            startActivity(it);
                        }
                        act.finish();
                    }
                });
            }
            return contentView;
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        private static final String TAG = "MyReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = Loading.FAIL;

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                progress = bundle.getInt("progress");
            }

            if (progress == Loading.FAIL) {
                Log.d(TAG, "Go to ErrorFragment");
                //go to error fragment;
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.container, new ErrorFragment(), "Error");
                trans.commitAllowingStateLoss();
                return;
            }

            if (progress == Loading.SUCCESS) {
                //go to next fragment;
                Log.d(TAG, "Go to DescriptionFragment");
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.container, new DescriptionFragment(), "Description");
                trans.commitAllowingStateLoss();
                return;
            }

            //Update UI
            Log.d(TAG, "Update UI progress=" + progress);
            Fragment frag = getSupportFragmentManager().findFragmentByTag(TAG_WaitFragment);
            if (frag == null) {
                Log.d(TAG, "Update UI frag is null.");
                return;
            }

            if (!(frag instanceof WaitFragment)) {
                Log.d(TAG, "Update UI frag is not WaitFragment.");
                return;
            }

            WaitFragment wait = (WaitFragment) frag;
            wait.updateTip(progress);
        }
    }

}
