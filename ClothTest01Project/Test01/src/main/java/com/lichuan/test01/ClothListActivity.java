package com.lichuan.test01;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lichuan.test01.adapter.BoxGridAdapter;
import com.lichuan.test01.adapter.BoxImageGridAdapter;
import com.lichuan.test01.adapter.BoxModeGridAdapter;
import com.lichuan.test01.adapter.ClothGridAdapter;
import com.lichuan.test01.adapter.ClothMChooseGridAdapter;
import com.lichuan.test01.adapter.SuitModeGridAdapter;
import com.lichuan.test01.datamodel.BitmapOwner;
import com.lichuan.test01.datamodel.Box;
import com.lichuan.test01.datamodel.BoxImageType;
import com.lichuan.test01.datamodel.Cloth;
import com.lichuan.test01.datamodel.ClothType;
import com.lichuan.test01.datamodel.GridItem;
import com.lichuan.test01.datamodel.RootData;
import com.lichuan.test01.datamodel.Suit;
import com.lichuan.test01.msg.SourceObject;
import com.lichuan.test01.service.MyDB;
import com.lichuan.test01.service.db.BoxDb;
import com.lichuan.test01.service.db.BoxMappingClothDb;
import com.lichuan.test01.service.db.ClothDb;
import com.lichuan.test01.service.db.SuitDB;
import com.lichuan.test01.service.db.SuitMappingClothDB;
import com.lichuan.test01.utility.FileUtil;
import com.lichuan.test01.utility.LoadImageHelper;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class ClothListActivity extends AppCompatActivity {
    private final static String TAG = "ClothListActivity";

    private int mPreviewSize;
    private int mDetailPreviewSize;

    private boolean mFlag;

    private Vector<BitmapOwner> mImgData;

    private final static Object mLock = new Object();

    private final Handler mNotifyHandler = new Handler();

    private final static String PlaceholderFragmentTag = "PlaceholderFragmentTag";

    private final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateGridUI();
        }
    };

    private int mFrom;

    private int mCurrentMode;

    public final static int ShowClothOnlyMode = 0;
    public final static int ShowBoxAndClothMode = 1;
    public final static int ShowSuitOnlyMode = 2;

    private final static String ClothDetailFragmentName = "ClothDetail";
    private final static String BoxDetailFragmentName = "BoxDetail";
    private final static String ClothListInBoxDetailFragmentName = "ClothListInBox";
    private final static String SuitDetailFragmentName = "SuitDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_list);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(), PlaceholderFragmentTag)
                    .commit();
        }

        mFrom = SourceObject.SOURCE_START;
        SourceObject obj = getIntent().getParcelableExtra(SourceObject.SourceName);
        if (obj != null) {
            mFrom = obj.getSrcFrom();
        }

        mCurrentMode = ShowClothOnlyMode;
        if (mFrom == SourceObject.SOURCE_Add_BOX) {
            mCurrentMode = ShowBoxAndClothMode;
        } else if (mFrom == SourceObject.SOURCE_Add_SUIT) {
            mCurrentMode = ShowSuitOnlyMode;
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  //屏幕宽
        int height = dm.heightPixels;  //屏幕高

        int previewSize = LoadImageHelper.ComputePreviewSizeByScreenWH(width, height);
        Log.d(TAG, "onCreate width=" + width + ", height=" + height + ",preview size=" + previewSize);
        mPreviewSize = previewSize;

        mDetailPreviewSize = Math.min(width, height);

        mImgData = new Vector<BitmapOwner>();

        beginWorker();
    }

    public void onStart() {
        Log.d(TAG, "onStart begin");
        super.onStart();

        Log.d(TAG, "onStart end");
    }

    public void onPause() {
        Log.d(TAG, "onPause begin");
        super.onPause();
        Log.d(TAG, "onPause end");
    }

    public void onResume() {
        Log.d(TAG, "onResume begin");
        super.onResume();
        Log.d(TAG, "onResume end");
    }

    public void onStop() {
        Log.d(TAG, "onStop begin");
        super.onStop();

        mFlag = false;
        Log.d(TAG, "onStop end");
    }

    public void onRestart() {
        Log.d(TAG, "onRestart begin");
        super.onRestart();

        mImgData = new Vector<BitmapOwner>();

        beginWorker();
        Log.d(TAG, "onRestart end");
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy begin");
        Log.d(TAG, "onDestroy end");

        mFlag = false;
        super.onDestroy();
    }

    private void beginWorker() {
        mFlag = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                processImageData();
            }
        });
        t.start();
    }

    private void putImageData(Vector<BitmapOwner> list) {
        synchronized (mLock) {
            mImgData.addAll(list);
        }
    }

    private void processImageData() {
        while (mFlag) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Log.d(TAG, "processImageData sleep occur InterruptedException.");
            }

            synchronized (mLock) {
                if (mImgData.size() == 0) {
                    continue;
                }
                BitmapOwner owner = mImgData.get(0);
                mImgData.remove(0);
                loadBitmapForOwner(owner);
            }
        }
    }

    private void loadBitmapForOwner(BitmapOwner owner) {
        File dirFile = getFilesDir();
        String dirPath = dirFile.getPath();
        Log.d(TAG, "my app path =" + dirPath);

        String filePathOfImage = dirPath + File.separatorChar + owner.getImagePath();

        Log.d(TAG, "image full path =" + filePathOfImage);

        try {
            Bitmap img = FileUtil.getBitmapFromFile(filePathOfImage, mPreviewSize, mPreviewSize);
            owner.setBitmap(img);
            owner.setBadFlag(img == null);
        } catch (Exception e) {
            owner.setBitmap(null);
            owner.setBadFlag(true);
        }

        if (mFlag) {
            notifyUpdateImmediately();
        }
    }

    private void notifyUpdateImmediately() {
        mNotifyHandler.post(mUpdateResults);
    }

    private void updateGridUI() {
        //update UI
        Fragment f = getSupportFragmentManager().findFragmentByTag(PlaceholderFragmentTag);
        if (f == null) {
            return;
        }

        if (f instanceof PlaceholderFragment) {
            PlaceholderFragment gridFrag = (PlaceholderFragment) f;
            gridFrag.updateGrid();
        }
    }

    private void initializeGridAgain() {
        Fragment f = getSupportFragmentManager().findFragmentByTag(PlaceholderFragmentTag);
        if (f == null) {
            return;
        }

        if (f instanceof PlaceholderFragment) {
            PlaceholderFragment gridFrag = (PlaceholderFragment) f;
            gridFrag.initializeGridAgain();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cloth_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_addCloth:
                goToAddNewClothActivity();
                return true;
            case R.id.action_addBox:
                goToAddNewBoxActivity();
                return true;
            case R.id.action_addSuit:
                goToAddNewSuitActivity();
                return true;
            case R.id.action_changeMode:
                changeGridMode();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, " == onConfigurationChanged occur");
    }

    private void changeGridMode() {
        if (mCurrentMode == ShowClothOnlyMode) {
            mCurrentMode = ShowBoxAndClothMode;
        } else if (mCurrentMode == ShowBoxAndClothMode) {
            mCurrentMode = ShowSuitOnlyMode;
        } else if (mCurrentMode == ShowSuitOnlyMode) {
            mCurrentMode = ShowClothOnlyMode;
        }
        resetGridUI();
    }

    private void resetGridUI() {
        //update UI
        Fragment f = getSupportFragmentManager().findFragmentByTag(PlaceholderFragmentTag);
        if (f == null) {
            return;
        }

        if (f instanceof PlaceholderFragment) {
            PlaceholderFragment gridFrag = (PlaceholderFragment) f;
            gridFrag.initializeGrid();
        }
    }

    private void goToAddNewClothActivity() {
        Intent it = new Intent();
        it.putExtra(SourceObject.SourceName, new SourceObject(SourceObject.SOURCE_CLOTH_LIST));
        it.setClass(getBaseContext(), AddClothActivity.class);
        startActivity(it);

        finish();
    }

    private void goToAddNewBoxActivity() {
        Intent it = new Intent();
        it.putExtra(SourceObject.SourceName, new SourceObject(SourceObject.SOURCE_CLOTH_LIST));
        it.setClass(getBaseContext(), AddBoxActivity.class);
        startActivity(it);

        finish();
    }

    private void goToAddNewSuitActivity() {
        Intent it = new Intent();
        it.putExtra(SourceObject.SourceName, new SourceObject(SourceObject.SOURCE_CLOTH_LIST));
        it.setClass(getBaseContext(), AddSuitActivity.class);
        startActivity(it);

        finish();
    }

    public void showUnimplementDialog() {
        String titleStr = getString(R.string.title_warning);
        String contentStr = getString(R.string.unimplementFunction);
        String yesStr = getString(R.string.Close_Btn);
        new AlertDialog.Builder(this)
                .setTitle(titleStr)
                .setMessage(contentStr)
                .setPositiveButton(yesStr,null)
                .show();
    }

    public static class SuitPictureViewFragment extends Fragment {
        private View mContentView;
        private ImageView mPicViewUp;
        private ImageView mPicViewDown;

        private Suit mSuit;

        public static SuitPictureViewFragment newInstance( String name){
            SuitPictureViewFragment frag = new SuitPictureViewFragment();
            Bundle args = new Bundle();
            args.putString("name", name);
            frag.setArguments(args);
            return frag;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.fragment_show_picture_for_suit, container, false);
            assert (mContentView != null);

            if (getArguments() != null) {
                String nameVal = getArguments().getString("name");
                mSuit = RootData.getInstance().findSuit(nameVal);
            }

            mPicViewUp = (ImageView) mContentView.findViewById(R.id.picture_show_up);
            assert (mPicViewUp!= null);

            mPicViewDown = (ImageView) mContentView.findViewById(R.id.picture_show_down);
            assert (mPicViewDown!= null);

            loadBitmapForSuit();
            return mContentView;
        }

        private void loadBitmapForSuit(){
            Activity act = getActivity();
            DisplayMetrics dm = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;  //屏幕宽
            int height = dm.heightPixels/2;  //屏幕高

            int PreviewSize = width>height?height:width;

            ArrayList<Cloth> list = mSuit.getAllClothesBelongThisSuit();
            for(Cloth c : list) {
                if( c.getType() == ClothType.UpperBody) {
                    boolean ret = loadBitmapForCloth(c, PreviewSize, mPicViewUp);
                    if( !ret){
                        mPicViewUp.setImageDrawable(act.getResources().getDrawable(R.drawable.invalid));
                    }
                    break;
                }
            }
            for(Cloth c : list) {
                if( c.getType() == ClothType.LowerBody) {
                    boolean ret = loadBitmapForCloth(c, PreviewSize, mPicViewDown);
                    if( !ret){
                        mPicViewDown.setImageDrawable(act.getResources().getDrawable(R.drawable.invalid));
                    }
                    break;
                }
            }
        }

        private boolean loadBitmapForCloth(Cloth cloth,int PreviewSize, ImageView picView){
            Activity act = getActivity();
            File dirFile = act.getFilesDir();
            String dirPath = dirFile.getPath();

            String filePathOfImage = dirPath + File.separatorChar + cloth.getImagePath();

            try {
                Bitmap img = FileUtil.getBitmapFromFile(filePathOfImage, PreviewSize, PreviewSize);
                if( img != null)
                {
                    picView.setImageBitmap(img);
                    return true;
                }
            } catch (Exception e) {
                Log.d(TAG, "load image Exception =" + e);
            }
            return false;
        }
    }

    public static class ClothPictureViewFragment extends Fragment{
        private View mContentView;
        private Cloth mCloth;

        private ImageView mPicView;

        public static ClothPictureViewFragment newInstance( String name){
            ClothPictureViewFragment frag = new ClothPictureViewFragment();
            Bundle args = new Bundle();
            args.putString("name", name);
            frag.setArguments(args);
            return frag;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.fragment_show_picture, container, false);
            assert (mContentView != null);

            if (getArguments() != null) {
                String nameVal = getArguments().getString("name");
                mCloth = RootData.getInstance().findCloth(nameVal);
            }

            mPicView = (ImageView) mContentView.findViewById(R.id.picture_show);
            boolean ret = loadBitmapForCloth();
            if( !ret){
                mPicView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.invalid));
            }
            return mContentView;
        }

        private boolean loadBitmapForCloth() {
            Activity act = getActivity();
            File dirFile = act.getFilesDir();
            String dirPath = dirFile.getPath();
            Log.d(TAG, "my app path =" + dirPath);

            String filePathOfImage = dirPath + File.separatorChar + mCloth.getImagePath();

            Log.d(TAG, "image full path =" + filePathOfImage);

            DisplayMetrics dm = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;  //屏幕宽
            int height = dm.heightPixels;  //屏幕高

            int PreviewSize = width>height?height:width;
            try {
                Bitmap img = FileUtil.getBitmapFromFile(filePathOfImage, PreviewSize, PreviewSize);
                if( img != null)
                {
                    mPicView.setImageBitmap(img);
                    return true;
                }
            } catch (Exception e) {
                Log.d(TAG, "load image Exception =" + e);
            }
            return false;
        }
    }

    public static class ChangeClothListForSuitFragment extends  Fragment{
        private View mContentView;
        private Button mSave;
        private GridView mGrid;
        private ClothMChooseGridAdapter mAdapter;

        private Suit mSuit;

        private ProgressDialog m_pDialog;

        private final Handler mHandler;

        private final static String ChangeClothListForSuitFragmentTAG = "ChangeClothListForSuit";

        public static ChangeClothListForSuitFragment newInstance( String name){
            ChangeClothListForSuitFragment frag = new ChangeClothListForSuitFragment();
            Bundle args = new Bundle();
            args.putString("name", name);
            frag.setArguments(args);
            return frag;
        }

        public ChangeClothListForSuitFragment(){
            mHandler = new Handler();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.fragment_choose_cloth_for_suit, container, false);
            assert (mContentView != null);

            if (getArguments() != null) {
                String nameVal = getArguments().getString("name");
                mSuit = RootData.getInstance().findSuit(nameVal);
            }

            mGrid = (GridView) mContentView.findViewById(R.id.choose_cloth_grid);
            assert (mGrid != null);

            mSave = (Button) mContentView.findViewById(R.id.btn_save);
            assert (mSave != null);

            RootData root = RootData.getInstance();
            ArrayList<Cloth> clothes = root.getCloneClothes();

            for (Cloth c : clothes) {
                c.setSelected(false);
            }

            Activity act = getActivity();
            mAdapter = new ClothMChooseGridAdapter(act, clothes);
            mGrid.setAdapter(mAdapter);

            mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    RootData root = RootData.getInstance();
                    ArrayList<Cloth> clothes = root.getCloneClothes();

                    Cloth cloth = clothes.get(i);
                    cloth.setSelected(!cloth.isSelected());

                    mAdapter.notifyDataSetChanged();
                }
            });

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveClothForSuit();
                }
            });

            return mContentView;
        }

        private void saveClothForSuit(){
            RootData root = RootData.getInstance();
            ArrayList<Cloth> clothes = root.getCloneClothes();

            int count = 0;
            int upperCount = 0;
            int lowerCount = 0;
            for (Cloth c : clothes) {
                if (c.isSelected()) {
                    count++;
                    if (c.getType() == ClothType.UpperBody) {
                        upperCount++;
                    } else if (c.getType() == ClothType.LowerBody) {
                        lowerCount++;
                    }
                }
            }

            if (count == 0) {
                String msg = getString(R.string.choose_at_least_cloth);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                return;
            }

            if (upperCount == 0) {
                String msg = getString(R.string.choose_at_least_upper_cloth);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                return;
            }

            if (lowerCount == 0) {
                String msg = getString(R.string.choose_at_least_lower_cloth);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                return;
            }

            popProgressDialog();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    ChangeClothListForSuitFragment.this.updateSuitAndSaveIt();
                }
            });
            t.start();
        }


        private void updateSuitAndSaveIt(){
            ArrayList<Cloth> selectedCloth = new ArrayList<Cloth>();

            RootData root = RootData.getInstance();
            ArrayList<Cloth> clothes = root.getCloneClothes();

            for (Cloth c : clothes) {
                if (c.isSelected()) {
                    selectedCloth.add(c);
                }
            }

            boolean bSuccess = updateClothesBelongToSuitIntoDb(selectedCloth);
            if (!bSuccess) {
                Log.d(ChangeClothListForSuitFragmentTAG, "updateClothesBelongToSuitIntoDb return false.");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateSuitFail();
                    }
                });
                return;
            }

            mSuit.clearAllCloth();
            for (Cloth c : selectedCloth) {
                mSuit.addCloth(c);
                c.setSelected(false);
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateSuitFinish();
                }
            });

            Log.d(ChangeClothListForSuitFragmentTAG, "updateSuitAndSaveIt is end.");

        }

        private boolean updateClothesBelongToSuitIntoDb( ArrayList<Cloth> selectedCloth) {
            Activity act = getActivity();
            MyDB db = new MyDB(act.getBaseContext());
            SQLiteDatabase writableDB = db.getWritableDatabase();

            writableDB.delete(SuitMappingClothDB.TABLE_NAME,SuitMappingClothDB.createWhereStatementForDeleteCloth(), new String[]{mSuit.getName()});

            for (Cloth c : selectedCloth) {
                ContentValues values = SuitMappingClothDB.createValuesForInsert(Integer.parseInt(mSuit.getName()), Integer.parseInt(c.getName()));
                try {
                    writableDB.insertOrThrow(SuitMappingClothDB.TABLE_NAME, null, values);
                } catch (android.database.SQLException e) {
                    Log.d(ChangeClothListForSuitFragmentTAG, "write suit record fail. e=" + e.toString());
                    writableDB.close();
                    return false;
                }
            }

            writableDB.close();

            return true;
        }

        private void updateSuitFinish() {
            disappearProgressDialog();

            FragmentActivity act = getActivity();
            act.getSupportFragmentManager().popBackStack();
        }

        private void updateSuitFail() {
            disappearProgressDialog();

            Log.d(ChangeClothListForSuitFragmentTAG, "updateSuitFail, create suit fail.");

            Activity act = getActivity();
            String Message = act.getResources().getString(R.string.update_suit_fail);
            Toast.makeText(act, Message, Toast.LENGTH_LONG).show();
        }


        private void popProgressDialog() {
            Activity act = getActivity();
            m_pDialog = new ProgressDialog(act);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            String waitStr = getResources().getString(R.string.waiting_text);
            m_pDialog.setMessage(waitStr);
            m_pDialog.setIndeterminate(false);
            // 设置ProgressDialog 是否可以按退回按键取消
            m_pDialog.setCancelable(false);
            m_pDialog.show();
        }

        private void disappearProgressDialog() {
            m_pDialog.hide();
            m_pDialog.dismiss();
            m_pDialog = null;
        }
    }

    public static class ClothListInBoxFragment extends Fragment {
        private Box mBox;
        private View mContentView;

        private TextView mBoxDesc;
        private ImageView mBoxPreView;

        private GridView mClothGrid;

        private ClothGridAdapter mAdapter;

        private final static String ClothListInBoxFragmentTAG = "ClothListInBoxFragment";

        public static ClothListInBoxFragment newInstance(String name){
            ClothListInBoxFragment frag = new ClothListInBoxFragment();
            Bundle args = new Bundle();
            args.putString("name", name);
            frag.setArguments(args);
            return frag;
        }


        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mBox = null;
            mContentView = inflater.inflate(R.layout.fragment_cloth_list_in_box, container, false);
            assert (mContentView != null);

            if( getArguments() != null ){
                String name = getArguments().getString("name");
                mBox = RootData.getInstance().findBox(name);
            }

            mBoxDesc = (TextView) mContentView.findViewById(R.id.box_description);
            assert (mBoxDesc != null);

            assert (mBox != null);

            mBoxDesc.setText(mBox.getDescription());

            mBoxPreView = (ImageView) mContentView.findViewById(R.id.imageOfBox);
            assert (mBoxPreView != null);

            if (mBox.isBitmapImage()) {
                mBoxPreView.setImageBitmap(mBox.getBitmap());
            } else {
                Activity act = getActivity();
                mBoxPreView.setImageDrawable(act.getResources().getDrawable(mBox.getDrawableResId()));
            }

            mClothGrid = (GridView) mContentView.findViewById(R.id.cloth_grid);
            assert (mClothGrid != null);

            Activity act = getActivity();
            mAdapter = new ClothGridAdapter(act, mBox.getAllClothesBelongThisBox());
            mClothGrid.setAdapter(mAdapter);
            return mContentView;
        }
    }


    public static class SuitDetailFragment extends Fragment {
        private Suit mSuit;
        private View mContentView;
        private EditText mDescriptionView;

        private Button mSaveDesc;
        private Button mChangeCloth;
        private Button mDelete;
        private Button mTryIt;
        private Button mPublish;

        private GridView mClothGrid;

        private ClothGridAdapter mAdapter;

        private static final String SuitDetailFragment_TAG="SuitDetailFragment";

        public static SuitDetailFragment newInstance(String name){
            SuitDetailFragment frag = new SuitDetailFragment();
            Bundle args = new Bundle();
            args.putString("name", name);
            frag.setArguments(args);
            return frag;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mSuit = null;
            mContentView = inflater.inflate(R.layout.fragment_suit_detail, container, false);
            if( getArguments()!= null){
                String name = getArguments().getString("name");
                mSuit = RootData.getInstance().findSuit(name);
            }

            mDescriptionView = (EditText)mContentView.findViewById(R.id.edit_description);
            mDescriptionView.setText( mSuit.getDescription());


            mSaveDesc = (Button)mContentView.findViewById(R.id.btn_save);
            mSaveDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDescriptionToSuit();
                }
            });

            mChangeCloth = (Button)mContentView.findViewById(R.id.btn_change_cloth);
            mChangeCloth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoChangeClothForSuit();
                }
            });

            mDelete = (Button)mContentView.findViewById(R.id.btn_delete);
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteThisSuit();
                }
            });

            mPublish = (Button)mContentView.findViewById(R.id.btn_publish_suit);
            mPublish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO publish suit to server
                    ((ClothListActivity)getActivity()).showUnimplementDialog();
                }
            });

            mTryIt = (Button)mContentView.findViewById(R.id.btn_try_suit);
            mTryIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trySuit();
                }
            });

            mClothGrid = (GridView) mContentView.findViewById(R.id.cloth_grid);
            assert (mClothGrid != null);

            Activity act = getActivity();
            mAdapter = new ClothGridAdapter(act, mSuit.getAllClothesBelongThisSuit());
            mClothGrid.setAdapter(mAdapter);

            mClothGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(SuitDetailFragment_TAG, "mClothGrid only cloth mode, onItemClick i=" + i + ",l=" + l);

                    RootData root = RootData.getInstance();
                    ArrayList<Cloth> allCloth = mSuit.getAllClothesBelongThisSuit();
                    Cloth cloth = allCloth.get(i);
                    Log.d(SuitDetailFragment_TAG, "mClothGrid onItemClick cloth=" + cloth.getName());

                    //view detail info of cloth
                    gotoPictureFragment(cloth);
                }
            });

            return mContentView;
        }

        private void deleteThisSuit(){
            Activity act = getActivity();
            String titleStr = act.getString(R.string.title_warning);
            String contentStr = act.getString(R.string.are_you_sure_delete_this_suit);
            String yesStr = act.getString(R.string.Yes_Btn);
            String noStr = act.getString(R.string.No_Btn);
            new AlertDialog.Builder(act)
                    .setTitle(titleStr)
                    .setMessage(contentStr)
                    .setPositiveButton(yesStr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            doDeleteSuit();
                        }
                    })
                    .setNegativeButton(noStr, null)
                    .show();
        }

        private void doDeleteSuit(){
            String idStr = mSuit.getName();
            FragmentActivity act = getActivity();

            MyDB db = new MyDB(act.getBaseContext());
            SQLiteDatabase writableDB = db.getWritableDatabase();
            assert (writableDB != null);

            try {
                writableDB.delete(SuitDB.TABLE_NAME, SuitDB.getWhereConditionByID(), new String[]{idStr});
            } catch (Exception e) {
                writableDB.close();
                Log.d(SuitDetailFragment_TAG, "delete suit fail. suitId =" + idStr);
                return;
            }

            try {
                writableDB.delete(SuitMappingClothDB.TABLE_NAME, SuitMappingClothDB.createWhereStatementForDeleteCloth(), new String[]{idStr});
            } catch (Exception e) {
                writableDB.close();
                Log.d(SuitDetailFragment_TAG, "delete suit Mapping Cloth fail. suitId =" + idStr);
                return;

            }
            writableDB.close();

            mSuit.clearAllCloth();
            RootData root = RootData.getInstance();
            root.removeSuit(mSuit);

            mSuit = null;

            if (act instanceof ClothListActivity) {
                ClothListActivity clothListAct = (ClothListActivity) act;
                clothListAct.initializeGridAgain();
            }

            act.getSupportFragmentManager().popBackStack();
        }

        public void onHiddenChanged(boolean hidden){
            super.onHiddenChanged(hidden);
            if( !hidden ){
                if( mAdapter!= null ){
                    Activity act = getActivity();
                    mAdapter = new ClothGridAdapter(act, mSuit.getAllClothesBelongThisSuit());
                    mClothGrid.setAdapter(mAdapter);
                }
            }
        }

        private void gotoPictureFragment(Cloth cloth){
            ClothPictureViewFragment newFrag = ClothPictureViewFragment.newInstance(cloth.getName());
            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            if (!newFrag.isAdded()) {
                trans.hide(SuitDetailFragment.this).add(R.id.container, newFrag, "ShowPic");
            } else {
                trans.hide(SuitDetailFragment.this).show(newFrag);
            }
            trans.addToBackStack("ShowPic");
            trans.commit();
        }

        private void trySuit(){
            SuitPictureViewFragment newFrag = SuitPictureViewFragment.newInstance(mSuit.getName());
            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            if (!newFrag.isAdded()) {
                trans.hide(SuitDetailFragment.this).add(R.id.container, newFrag, "ShowSuit");
            } else {
                trans.hide(SuitDetailFragment.this).show(newFrag);
            }
            trans.addToBackStack("ShowSuit");
            trans.commit();
        }

        private void gotoChangeClothForSuit(){
            ChangeClothListForSuitFragment newFrag = ChangeClothListForSuitFragment.newInstance(mSuit.getName());

            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            if (!newFrag.isAdded()) {
                trans.hide(SuitDetailFragment.this).add(R.id.container, newFrag, "ChooseClothForSuit");
            } else {
                trans.hide(SuitDetailFragment.this).show(newFrag);
            }
            trans.addToBackStack("ChooseCloth");
            trans.commit();

        }
        private void saveDescriptionToSuit() {
            String desc = mDescriptionView.getText().toString();
            if( desc == null || desc.length() == 0){
                String msg = getString(R.string.suit_description_error);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                return;
            }

            Activity act = getActivity();
            MyDB db = new MyDB(act);

            SQLiteDatabase writeDb = db.getWritableDatabase();
            assert (writeDb != null);

            String idStr = mSuit.getName();

            ContentValues values = SuitDB.createValuesForUpdate(desc);
            String whereClause = SuitDB.getWhereConditionByID();

            try
            {
                writeDb.update(SuitDB.TABLE_NAME, values, whereClause, new String[]{idStr});
            }
            catch(Exception e){
                writeDb.close();
                return;
            }

            writeDb.close();

            mSuit.setDescription(desc);
        }
    }

    public static class BoxDetailFragment extends Fragment {
        private Box mBox;
        private View mContentView;

        private Button mSave;
        private Button mDelete;

        private Button mViewClothList;

        private GridView mGrid;
        private ImageView mPreView;

        private EditText mDescEditor;

        private final static String BoxDetailFragmentTAG = "BoxDetailFragment";

        private int mImageType;

        public static BoxDetailFragment newInstance(String name){
            BoxDetailFragment frag = new BoxDetailFragment();
            Bundle args = new Bundle();
            args.putString("name", name);
            frag.setArguments(args);
            return frag;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mBox = null;
            mContentView = inflater.inflate(R.layout.fragment_box_detail, container, false);
            if( getArguments()!= null){
                String name = getArguments().getString("name");
                mBox = RootData.getInstance().findBox(name);
            }

            mPreView = (ImageView) mContentView.findViewById(R.id.imageOfBox);
            assert (mPreView != null);

            Activity act = getActivity();
            int typeVal = mBox.getImageType();
            int ResourceID = BoxImageType.computeResourceIdByImageType(typeVal);
            mPreView.setImageDrawable(act.getResources().getDrawable(ResourceID));

            mDescEditor = (EditText) mContentView.findViewById(R.id.edit_description);
            assert (mDescEditor != null);

            mDescEditor.setText(mBox.getDescription());

            mGrid = (GridView) mContentView.findViewById(R.id.pic_grid);
            assert (mGrid != null);

            mGrid.setAdapter(new BoxImageGridAdapter(getActivity()));

            mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mImageType = BoxImageType.getImageTypeByIndex(i);

                    Activity act = getActivity();
                    int ResourceID = BoxImageType.computeResourceIdByIndex(i);
                    mPreView.setImageDrawable(act.getResources().getDrawable(ResourceID));
                }
            });

            mSave = (Button) mContentView.findViewById(R.id.btn_save);
            assert (mSave != null);

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveDataIntoBox();
                }
            });

            mDelete = (Button) mContentView.findViewById(R.id.btn_delete);
            assert (mDelete != null);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteThisBox();
                }
            });

            mViewClothList = (Button) mContentView.findViewById(R.id.btn_view_cloth);
            assert (mViewClothList != null);

            mViewClothList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoClothListInThisBox();
                }
            });

            return mContentView;
        }

        private void gotoClothListInThisBox() {
            if (mBox.getClothCount() <= 0) {
                String msg = getString(R.string.no_cloth_in_this_box);
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                return;
            }

            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            ClothListInBoxFragment newFragment = ClothListInBoxFragment.newInstance(mBox.getName());
            if (!newFragment.isAdded()) {
                trans.hide(BoxDetailFragment.this).add(R.id.container, newFragment, ClothListActivity.ClothListInBoxDetailFragmentName);
            } else {
                trans.hide(BoxDetailFragment.this).show(newFragment);
            }
            trans.addToBackStack("ClothListOfBox");
            trans.commit();
        }

        private void deleteThisBox() {
            Activity act = getActivity();
            String titleStr = act.getString(R.string.title_warning);
            String contentStr = act.getString(R.string.are_you_sure_delete_this_box);
            String yesStr = act.getString(R.string.Yes_Btn);
            String noStr = act.getString(R.string.No_Btn);
            new AlertDialog.Builder(act)
                    .setTitle(titleStr)
                    .setMessage(contentStr)
                    .setPositiveButton(yesStr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            doDeleteBox();
                        }
                    })
                    .setNegativeButton(noStr, null)
                    .show();
        }

        private void doDeleteBox() {
            String idStr = mBox.getName();
            FragmentActivity act = getActivity();

            MyDB db = new MyDB(act.getBaseContext());
            SQLiteDatabase writableDB = db.getWritableDatabase();
            assert (writableDB != null);

            try {
                writableDB.delete(BoxDb.TABLE_NAME, BoxDb.getWhereConditionByID(), new String[]{idStr});
            } catch (Exception e) {
                writableDB.close();
                Log.d(BoxDetailFragmentTAG, "delete box fail. boxId =" + idStr);
                return;
            }

            try {
                writableDB.delete(BoxMappingClothDb.TABLE_NAME, BoxMappingClothDb.createBoxWhereStatement(), new String[]{idStr});
            } catch (Exception e) {
                writableDB.close();
                Log.d(BoxDetailFragmentTAG, "delete box Mapping Cloth fail. boxId =" + idStr);
                return;

            }
            writableDB.close();

            mBox.clearCloth();
            RootData root = RootData.getInstance();
            root.removeBox(mBox);

            mBox = null;

            if (act instanceof ClothListActivity) {
                ClothListActivity clothListAct = (ClothListActivity) act;
                clothListAct.initializeGridAgain();
            }

            act.getSupportFragmentManager().popBackStack();
        }


        private void saveDataIntoBox() {
            String tmpDescription;
            tmpDescription = mDescEditor.getText().toString();
            if (tmpDescription.equals(mBox.getDescription())) {
                if (mImageType == mBox.getImageType()) {
                    String msg = getString(R.string.no_changed_for_box);
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    return;
                }
            }

            mSave.setEnabled(false);
            mSave.setActivated(false);

            boolean bSuccess = updateBoxIntoDB(mImageType, tmpDescription);
            if (!bSuccess) {
                Log.d(BoxDetailFragmentTAG, "update Box into DB failed.");

                mSave.setEnabled(true);
                mSave.setActivated(true);
                return;
            }

            if (mImageType != mBox.getImageType()) {
                mBox.setImageType(mImageType);
            }

            if (!tmpDescription.equals(mBox.getDescription())) {
                mBox.setDescription(tmpDescription);
            }

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSave.setEnabled(true);
                    mSave.setActivated(true);
                }
            }, 2000);

            Activity act = getActivity();
            String msg = act.getString(R.string.save_data_success);
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

            if (act instanceof ClothListActivity) {
                ((ClothListActivity) act).notifyUpdateImmediately();
            }
        }

        private boolean updateBoxIntoDB(int type, String desc) {
            Activity act = getActivity();

            MyDB db = new MyDB(act.getBaseContext());
            SQLiteDatabase writableDB = db.getWritableDatabase();
            assert (writableDB != null);

            String idStr = mBox.getName();

            ContentValues values = BoxDb.createValuesForUpdate(desc, type);
            String whereClause = BoxDb.getWhereConditionByID();

            try {
                writableDB.update(BoxDb.TABLE_NAME, values, whereClause, new String[]{idStr});
                writableDB.close();
            } catch (Exception e) {
                writableDB.close();
                Log.d(BoxDetailFragmentTAG, "update Box into DB failed.e=" + e.toString());
                return false;
            }

            return true;
        }
    }

    public static class ChooseBoxFragment extends Fragment {
        private Cloth mCloth;
        private View mContentView;
        private GridView mGrid;

        private BoxGridAdapter mBoxAdapter;

        private final static String ChooseBoxFragmentTag = "ChooseBoxFragment";

        public static ChooseBoxFragment newInstance(String name){
            ChooseBoxFragment frag = new ChooseBoxFragment();
            Bundle args = new Bundle();
            args.putString("name", name);
            frag.setArguments(args);
            return frag;
        }


        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mCloth = null;
            mContentView = inflater.inflate(R.layout.fragment_choose_box_for_cloth, container, false);

            if( getArguments()!= null){
                String nameVal = getArguments().getString("name");
                mCloth = RootData.getInstance().findCloth(nameVal);
            }

            initGrid();
            return mContentView;
        }

        private void initGrid() {
            mGrid = (GridView) mContentView.findViewById(R.id.box_grid);
            if (mGrid == null) {
                Log.d(ChooseBoxFragmentTag, "Not found grid.");
                return;
            }

            RootData root = RootData.getInstance();
            ArrayList<Box> allBox = root.getCloneBoxes();

            Activity act = getActivity();
            mBoxAdapter = new BoxGridAdapter(act, allBox);
            mGrid.setAdapter(mBoxAdapter);

            mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(ChooseBoxFragmentTag, "all box list, onItemClick i=" + i + ",l=" + l);

                    RootData root = RootData.getInstance();
                    ArrayList<Box> allBox = root.getCloneBoxes();

                    Box dumbBox = new Box(Box.dumbBoxString, Box.dumbBoxString);

                    allBox.add(dumbBox);

                    Box box = allBox.get(i);
                    boolean success = moveClothIntoThisBox(box);
                    if (!success) {
                        Activity act = getActivity();
                        String Message = act.getResources().getString(R.string.cloth_belong_to_this_box_fail);
                        Toast.makeText(act, Message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    FragmentActivity act = getActivity();
                    Fragment frag = act.getSupportFragmentManager().findFragmentByTag(ClothListActivity.ClothDetailFragmentName);
                    if (frag instanceof ClothDetailFragment) {
                        ClothDetailFragment detailFrag = (ClothDetailFragment) frag;
                        detailFrag.updateDetailUI();
                    }

                    if (act instanceof ClothListActivity) {
                        ClothListActivity clothListAct = (ClothListActivity) act;
                        clothListAct.initializeGridAgain();
                    }

                    act.getSupportFragmentManager().popBackStack();
                }
            });
        }

        private boolean moveClothIntoThisBox(Box newBox) {
            Activity act = getActivity();
            MyDB db = new MyDB(act);

            RootData root = RootData.getInstance();
            Box oldBox = root.findClothBelongBox(mCloth);
            if (oldBox != null) {
                SQLiteDatabase writeDb = db.getWritableDatabase();
                assert (writeDb != null);

                try {
                    writeDb.delete(BoxMappingClothDb.TABLE_NAME, BoxMappingClothDb.createWhereStatement(), new String[]{oldBox.getName(), mCloth.getName()});
                } catch (Exception e) {
                    Log.d(ChooseBoxFragmentTag, "delete relation of box and cloth fail. boxId =" + oldBox.getName() + ", clothId = " + mCloth.getName());
                    writeDb.close();
                    return false;
                }
                writeDb.close();
                oldBox.removeCloth(mCloth);
            }

            if (newBox.getName().equals(Box.dumbBoxString)) {
                return true;
            }

            SQLiteDatabase writeDb = db.getWritableDatabase();
            int boxId = Integer.parseInt(newBox.getName());
            int clothId = Integer.parseInt(mCloth.getName());
            ContentValues values = BoxMappingClothDb.createValuesForInsert(boxId, clothId);
            try {
                writeDb.insertOrThrow(BoxMappingClothDb.TABLE_NAME, null, values);
            } catch (Exception e) {
                Log.d(ChooseBoxFragmentTag, "add relation of box and cloth fail. boxId =" + newBox.getName() + ", clothId = " + mCloth.getName());
                return false;
            }
            writeDb.close();

            newBox.addCloth(mCloth);

            return true;
        }
    }


    public static class ClothDetailFragment extends Fragment {
        private static final String ClothDetailFragmentTag = "ClothDetailFragment";

        private EditText mDescription;
        private Cloth mCloth;
        private View mContentView;

        private RadioGroup mGroup;
        private RadioButton mUpper;
        private RadioButton mLower;

        private Button mSave;

        private Button mTryCloth;

        private Button mDeleteCloth;

        private ImageView mPic;

        private ImageView mLocationPic;

        private Button mModifyLocation;

        public static ClothDetailFragment newInstance(String nameVal){
            ClothDetailFragment newFragment = new ClothDetailFragment();
            Bundle args = new Bundle();
            args.putString("name", nameVal);
            newFragment.setArguments(args);
            return newFragment;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mCloth = null;

            mContentView = inflater.inflate(R.layout.fragment_cloth_detail, container, false);

            if (getArguments() != null) {
                String nameVal = getArguments().getString("name");
                mCloth = RootData.getInstance().findCloth(nameVal);
            }

            Log.d(ClothDetailFragmentTag, "onCreateView before initClothGrid.");

            mDescription = (EditText) mContentView.findViewById(R.id.your_description);
            mDescription.setText(mCloth.getDescription());


            mGroup = (RadioGroup) mContentView.findViewById(R.id.cloth_type_group);
            assert (mGroup != null);

            mUpper = (RadioButton) mContentView.findViewById(R.id.cloth_type_upper);
            assert (mUpper != null);

            mLower = (RadioButton) mContentView.findViewById(R.id.cloth_type_lower);
            assert (mLower != null);

            if (mCloth.getType() == ClothType.UpperBody) {
                mUpper.setChecked(true);
            } else if (mCloth.getType() == ClothType.LowerBody) {
                mLower.setChecked(true);
            }

            mPic = (ImageView) mContentView.findViewById(R.id.picture_show);
            assert (mPic != null);
            mPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoPictureFragment();
                }
            });

            mSave = (Button) mContentView.findViewById(R.id.save_data);
            assert (mSave != null);

            mSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSave.setEnabled(false);
                    mSave.setActivated(false);
                    updateClothTypeAndDescription();
                }
            });

            mTryCloth = (Button) mContentView.findViewById(R.id.tryIt);
            assert (mTryCloth != null);

            mTryCloth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoTryClothActivity();
                }
            });

            mDeleteCloth = (Button) mContentView.findViewById(R.id.deleteIt);
            assert (mDeleteCloth != null);

            mDeleteCloth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteThisCloth();
                }
            });

            mLocationPic = (ImageView) mContentView.findViewById(R.id.location_image);
            assert (mLocationPic != null);

            findBox();

            mModifyLocation = (Button) mContentView.findViewById(R.id.location_modify);
            assert (mModifyLocation != null);

            mModifyLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoChooseAnyBox();
                }
            });

            loadImage();

            Log.d(ClothDetailFragmentTag, "onCreateView end.");
            return mContentView;
        }

        private void gotoPictureFragment(){
            ClothPictureViewFragment newFrag = ClothPictureViewFragment.newInstance(mCloth.getName());
            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            if (!newFrag.isAdded()) {
                trans.hide(ClothDetailFragment.this).add(R.id.container, newFrag, "ShowPic");
            } else {
                trans.hide(ClothDetailFragment.this).show(newFrag);
            }
            trans.addToBackStack("ShowPic");
            trans.commit();
        }

        private void findBox() {
            RootData root = RootData.getInstance();
            Box box = root.findClothBelongBox(mCloth);

            if (box == null) {
                mLocationPic.setEnabled(false);
                Activity act = getActivity();
                mLocationPic.setImageDrawable(act.getResources().getDrawable(R.drawable.ic_launcher));
                return;
            }

            mLocationPic.setEnabled(true);
            if (box.isBitmapImage()) {
                mLocationPic.setImageBitmap(box.getBitmap());
            } else {
                Activity act = getActivity();
                mLocationPic.setImageDrawable(act.getResources().getDrawable(box.getDrawableResId()));
            }
        }

        private void gotoChooseAnyBox() {
            RootData root = RootData.getInstance();
            int count = root.getBoxesCount();
            if (count == 0) {
                Activity act = getActivity();
                String msg = act.getString(R.string.no_box_exist);
                Toast.makeText(act, msg, Toast.LENGTH_LONG).show();
                return;
            }

            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            ChooseBoxFragment newFrag = ChooseBoxFragment.newInstance(mCloth.getName());
            if (!newFrag.isAdded()) {
                trans.hide(ClothDetailFragment.this).add(R.id.container, newFrag, "ChooseBoxForCloth");
            } else {
                trans.hide(ClothDetailFragment.this).show(newFrag);
            }
            trans.addToBackStack("ChooseBox");
            trans.commit();
        }

        private void updateClothTypeAndDescription() {
            int type = ClothType.UpperBody;
            if (mUpper.isChecked()) {
                type = ClothType.UpperBody;
            }
            if (mLower.isChecked()) {
                type = ClothType.LowerBody;
            }

            String desc;
            desc = mDescription.getText().toString();

            boolean bSuccess = updateClothTypeAndDescriptionIntoDB(type, desc);
            if (!bSuccess) {
                Log.d(ClothDetailFragmentTag, "update Cloth into DB failed.");
                return;
            }

            mCloth.setType(type);

            mCloth.setDescription(desc);

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSave.setEnabled(true);
                    mSave.setActivated(true);
                }
            }, 2000);

            Activity act = getActivity();
            String msg = act.getString(R.string.save_data_success);
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();

            if (act instanceof ClothListActivity) {
                ((ClothListActivity) act).notifyUpdateImmediately();
            }
        }

        private boolean updateClothTypeAndDescriptionIntoDB(int type, String desc) {
            Activity act = getActivity();

            MyDB db = new MyDB(act.getBaseContext());
            SQLiteDatabase writableDB = db.getWritableDatabase();
            assert (writableDB != null);

            String idStr = mCloth.getName();

            ContentValues values = ClothDb.createValuesForUpdate(desc, type);
            String whereClause = ClothDb.getWhereConditionByID();

            try {
                writableDB.update(ClothDb.TABLE_NAME, values, whereClause, new String[]{idStr});
                writableDB.close();
            } catch (Exception e) {
                writableDB.close();
                Log.d(ClothDetailFragmentTag, "update Cloth into DB failed.e=" + e.toString());
                return false;
            }

            return true;
        }

        private void loadImage() {
            Activity act = getActivity();
            File dirFile = act.getFilesDir();

            assert (dirFile != null);

            String dirPath = dirFile.getPath();
            Log.d(ClothDetailFragmentTag, "loadImage my app path =" + dirPath);

            String filePathOfImage = dirPath + File.separatorChar + mCloth.getImagePath();

            Log.d(TAG, "image full path =" + filePathOfImage);

            int ImageSize = 300;
            ClothListActivity clothListAct;
            if (act instanceof ClothListActivity) {
                clothListAct = (ClothListActivity) act;
                ImageSize = clothListAct.mDetailPreviewSize;
            }

            try {
                Bitmap img = FileUtil.getBitmapFromFile(filePathOfImage, ImageSize, ImageSize);
                mPic.setImageBitmap(img);
            } catch (Exception e) {
                mPic.setImageDrawable(act.getResources().getDrawable(R.drawable.invalid));
            }
        }

        private void deleteThisCloth() {
            Activity act = getActivity();
            String titleStr = act.getString(R.string.title_warning);
            String contentStr = act.getString(R.string.are_you_sure_delete_this_cloth);
            String yesStr = act.getString(R.string.Yes_Btn);
            String noStr = act.getString(R.string.No_Btn);

            new AlertDialog.Builder(act)
                    .setTitle(titleStr)
                    .setMessage(contentStr)
                    .setPositiveButton(yesStr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            doDeleteCloth();
                        }
                    })
                    .setNegativeButton(noStr, null)
                    .show();
        }

        private void doDeleteCloth() {
            FragmentActivity act = getActivity();

            MyDB db = new MyDB(act.getBaseContext());
            SQLiteDatabase writableDB = db.getWritableDatabase();
            assert (writableDB != null);

            try {
                writableDB.delete(ClothDb.TABLE_NAME, ClothDb.getWhereConditionByID(), new String[]{mCloth.getName()});
            } catch (Exception e) {
                writableDB.close();
                Log.d(ClothDetailFragmentTag, "delete cloth fail. clothId =" + mCloth.getName());
                return;
            }

            try {
                writableDB.delete(BoxMappingClothDb.TABLE_NAME, BoxMappingClothDb.createClothWhereStatement(), new String[]{mCloth.getName()});
            } catch (Exception e) {
                writableDB.close();
                Log.d(ClothDetailFragmentTag, "delete box Mapping Cloth fail. clothId =" + mCloth.getName());
                return;

            }
            writableDB.close();

            RootData root = RootData.getInstance();
            Box box = root.findClothBelongBox(mCloth);
            if (box != null) {
                box.removeCloth(mCloth);
            }

            root.removeCloth(mCloth);
            mCloth = null;

            if (act instanceof ClothListActivity) {
                ClothListActivity clothListAct = (ClothListActivity) act;
                clothListAct.initializeGridAgain();
            }

            act.getSupportFragmentManager().popBackStack();
        }

        private void gotoTryClothActivity() {
            //TODO try cloth
            ((ClothListActivity)getActivity()).showUnimplementDialog();
        }

        public void updateDetailUI() {
            findBox();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String ClothGridTag = "PlaceholderFragment";
        private View mContentView;
        private GridView mClothGrid;
        private ClothGridAdapter mAdapter;
        private BoxModeGridAdapter mBoxAdapter;
        private SuitModeGridAdapter mSuitAdapter;
        private TextView mDesc;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.fragment_cloth_list, container, false);

            Log.d(ClothGridTag, "onCreateView before initClothGrid.");

            doAllClothImage();

            initializeGrid();

            Log.d(ClothGridTag, "onCreateView end.");
            return mContentView;
        }

        private void doAllClothImage() {
            Activity act = getActivity();
            if( !(act instanceof ClothListActivity)){
                return;
            }
            ClothListActivity clothAct = (ClothListActivity) act;

            RootData root = RootData.getInstance();
            ArrayList<Cloth> allCloth = root.getCloneClothes();
            Log.d(ClothGridTag, "allCloth.size() = " + allCloth.size());

            Vector<BitmapOwner> list = new Vector<BitmapOwner>();
            list.addAll(allCloth);
            clothAct.putImageData(list);

        }

        private void initializeGrid() throws AssertionError {
            Activity act = getActivity();

            if ((!(act instanceof ClothListActivity))) {
                throw new AssertionError();
            }

            ClothListActivity clothAct = (ClothListActivity) act;

            mSuitAdapter = null;
            mAdapter = null;
            mBoxAdapter = null;

            if (clothAct.mCurrentMode == ShowSuitOnlyMode) {
                initSuitModeGrid();
            } else if (clothAct.mCurrentMode == ShowBoxAndClothMode) {
                initBoxModeGrid();
            } else {
                initAllClothModeGrid();
            }
        }

        private void initAllClothModeGrid() {
            mDesc = (TextView) mContentView.findViewById(R.id.content_title);
            assert (mDesc != null);

            String msg = getString(R.string.display_content_for_cloth);
            mDesc.setText(msg);

            mClothGrid = (GridView) mContentView.findViewById(R.id.cloth_grid);
            if (mClothGrid == null) {
                Log.d(ClothGridTag, "Not found grid.");
                return;
            }

            RootData root = RootData.getInstance();
            ArrayList<Cloth> allCloth = root.getCloneClothes();

            Activity act = getActivity();
            mAdapter = new ClothGridAdapter(act, allCloth);
            mClothGrid.setAdapter(mAdapter);

            mClothGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(ClothGridTag, "mClothGrid only cloth mode, onItemClick i=" + i + ",l=" + l);

                    RootData root = RootData.getInstance();
                    ArrayList<Cloth> allCloth = root.getCloneClothes();
                    Cloth cloth = allCloth.get(i);
                    Log.d(ClothGridTag, "mClothGrid onItemClick cloth=" + cloth.getName());

                    //view detail info of cloth
                    gotoDetailOfCloth(cloth);
                }
            });

            mBoxAdapter = null;
            mSuitAdapter = null;
        }

        private void initBoxModeGrid() {

            mDesc = (TextView) mContentView.findViewById(R.id.content_title);
            assert (mDesc != null);

            String msg = getString(R.string.display_content_for_box);
            mDesc.setText(msg);

            mClothGrid = (GridView) mContentView.findViewById(R.id.cloth_grid);
            if (mClothGrid == null) {
                Log.d(ClothGridTag, "Not found grid.");
                return;
            }

            RootData root = RootData.getInstance();
            ArrayList<GridItem> allItem = root.getCloneBoxesAndSomeClothes();

            Activity act = getActivity();
            mBoxAdapter = new BoxModeGridAdapter(act, allItem);
            mClothGrid.setAdapter(mBoxAdapter);

            mClothGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(ClothGridTag, "mClothGrid box mode, onItemClick i=" + i + ",l=" + l);

                    RootData root = RootData.getInstance();
                    ArrayList<GridItem> allItem = root.getCloneBoxesAndSomeClothes();

                    GridItem item = allItem.get(i);
                    if (item.isSet()) {
                        if (item instanceof Box) {
                            Box box = (Box) item;
                            gotoDetailOfBox(box);
                        }
                    } else {
                        if (item instanceof Cloth) {
                            Cloth c = (Cloth) item;
                            gotoDetailOfCloth(c);
                        }
                    }
                }
            });

            mAdapter = null;
            mSuitAdapter = null;
        }

        private void gotoDetailOfCloth(Cloth cloth) {
            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            ClothDetailFragment newFrag = ClothDetailFragment.newInstance(cloth.getName());
            if (!newFrag.isAdded()) {
                trans.hide(PlaceholderFragment.this).add(R.id.container, newFrag, ClothListActivity.ClothDetailFragmentName);
            } else {
                trans.hide(PlaceholderFragment.this).show(newFrag);
            }
            trans.addToBackStack("Detail");
            trans.commit();
        }

        private void gotoDetailOfBox(Box box) {
            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            BoxDetailFragment newFrag = BoxDetailFragment.newInstance(box.getName());
            if (!newFrag.isAdded()) {
                trans.hide(PlaceholderFragment.this).add(R.id.container, newFrag, ClothListActivity.BoxDetailFragmentName);
            } else {
                trans.hide(PlaceholderFragment.this).show(newFrag);
            }
            trans.addToBackStack("BoxDetail");
            trans.commit();
        }

        private void gotoDetailOfSuit(Suit suit) {
            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            SuitDetailFragment newFrag = SuitDetailFragment.newInstance(suit.getName());
            if (!newFrag.isAdded()) {
                trans.hide(PlaceholderFragment.this).add(R.id.container, newFrag, ClothListActivity.SuitDetailFragmentName);
            } else {
                trans.hide(PlaceholderFragment.this).show(newFrag);
            }
            trans.addToBackStack("SuitDetail");
            trans.commit();
        }

        private void initSuitModeGrid() {
            mDesc = (TextView) mContentView.findViewById(R.id.content_title);
            assert (mDesc != null);

            String msg = getString(R.string.display_content_for_suit);
            mDesc.setText(msg);


            mClothGrid = (GridView) mContentView.findViewById(R.id.cloth_grid);
            if (mClothGrid == null) {
                Log.d(ClothGridTag, "Not found grid.");
                return;
            }

            RootData root = RootData.getInstance();
            ArrayList<Suit> allSuit = root.getCloneSuit();
            Activity act = getActivity();
            mSuitAdapter = new SuitModeGridAdapter(act, allSuit);
            mClothGrid.setAdapter(mSuitAdapter);

            mClothGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(ClothGridTag, "mClothGrid suit mode, onItemClick i=" + i + ",l=" + l);

                    RootData root = RootData.getInstance();
                    ArrayList<Suit> suitLst = root.getCloneSuit();
                    Suit suit = suitLst.get(i);
                    gotoDetailOfSuit(suit);
                }
            });

            mAdapter = null;
            mBoxAdapter = null;
        }

        private void updateGrid() {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }

            if (mBoxAdapter != null) {
                mBoxAdapter.notifyDataSetChanged();
            }

            if (mSuitAdapter != null) {
                mSuitAdapter.notifyDataSetChanged();
            }
        }

        private void initializeGridAgain() {
            initializeGrid();
        }

        public void onHiddenChanged(boolean hidden){
            super.onHiddenChanged(hidden);
            if( !hidden ){
                updateGrid();
            }
        }
    }

}
