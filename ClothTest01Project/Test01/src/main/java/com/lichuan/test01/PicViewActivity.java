package com.lichuan.test01;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lichuan.test01.msg.PicViewObject;
import com.lichuan.test01.utility.FileUtil;

public class PicViewActivity extends AppCompatActivity {
    private static final String TAG = "PicViewActivity";

    private final static String Fragment_Name = "PlaceholderFragment";
    private Bitmap mImage;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate begin");

        setContentView(R.layout.activity_picview);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(), Fragment_Name)
                    .commit();
        }


        PicViewObject obj = getIntent().getParcelableExtra("fromObj");
        if (obj == null) {
            Log.d(TAG, "do not receive object from Intent");
            return;
        }
        Log.d(TAG, "obj ,getSource=" + obj.getSource() + " ,getUID=" + obj.getUID() + ", getPath=" + obj.getPath());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int width = dm.widthPixels;  //屏幕宽
        final int height = dm.heightPixels;  //屏幕高
        Log.d(TAG, "onCreate width=" + width + ", height=" + height);

        mHandler = new Handler();

        if (obj.getSource() == PicViewObject.FROM_PHOTO) {
            mImage = null;

            final long picUID = obj.getUID();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    loadPicture(picUID, width, height);
                }
            });
            t.start();

        }
        Log.d(TAG, "onCreate end");
    }

    public void onStop() {
        super.onStop();

        mImage = null;
        Log.d(TAG, "onStop");
    }

    public void onDestroy() {
        mImage = null;

        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void loadPicture(long uid, int width, int height) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Long.toString(uid)).build();
        Log.d(TAG, "loadPicture Image UID: " + uid + ", uri : " + uri.getPath());
        int previewSize = 200;

        int value = Math.min(width, height);
        if (value > previewSize + 30) {
            previewSize = value - 30;
        }

        Log.d(TAG, "loadPicture Image previewSize=" + previewSize);

        ContentResolver resolver = this.getContentResolver();

        try {
            mImage = FileUtil.getBitmapFromStream(resolver.openInputStream(Uri.parse(uri.toString())), previewSize, previewSize);
        } catch (Exception e) {
            mImage = null;
            Log.d(TAG, "loadPicture Read Image resource fail.e=" + e.toString());
            return;
        }

        if (mImage == null) {
            Log.d(TAG, "loadPicture Read Image resource fail. mImage == null");
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                PicViewActivity.this.notifyUpdateUI();
            }
        });
        Log.d(TAG, "loadPicture end : " + uid);
    }

    private void notifyUpdateUI() {
        Fragment frag = this.getSupportFragmentManager().findFragmentByTag(Fragment_Name);
        if (frag == null) {
            Log.d(TAG, "notifyUpdateUI  frag is null.");
            return;
        }

        if (!(frag instanceof PlaceholderFragment)) {
            Log.d(TAG, "notifyUpdateUI  frag is not instanceof PlaceholderFragment.");
            return;
        }

        PlaceholderFragment f = (PlaceholderFragment) frag;
        f.updatePicView();
    }

    private void loadClothImage(String path) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pic_view, menu);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, " == onConfigurationChanged occur");
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private ImageView mPicView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_picview, container, false);
            mPicView = (ImageView) rootView.findViewById(R.id.viewPicture);

            updatePicView();
            return rootView;
        }

        private void updatePicView() {
            Activity act = getActivity();
            if( !(act instanceof PicViewActivity )){
                return;
            }
            PicViewActivity picAct = (PicViewActivity) act;
            if (picAct.mImage != null) {
                mPicView.setImageBitmap(picAct.mImage);
            } else {
                mPicView.setImageDrawable(act.getResources().getDrawable(R.drawable.invalid));
            }
        }
    }

}
