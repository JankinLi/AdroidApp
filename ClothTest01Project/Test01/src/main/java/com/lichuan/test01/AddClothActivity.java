package com.lichuan.test01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lichuan.test01.datamodel.Cloth;
import com.lichuan.test01.datamodel.ClothType;
import com.lichuan.test01.datamodel.RootData;
import com.lichuan.test01.image.ImageData;
import com.lichuan.test01.image.ImageLoadingFinish;
import com.lichuan.test01.image.ImagePicker;
import com.lichuan.test01.image.PicGridAdapter;
import com.lichuan.test01.msg.SourceObject;
import com.lichuan.test01.service.MyDB;
import com.lichuan.test01.service.db.ClothDb;
import com.lichuan.test01.service.db.DBHelper;
import com.lichuan.test01.utility.FileUtil;
import com.lichuan.test01.utility.LoadImageHelper;
import com.lichuan.test01.utility.RandomUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AddClothActivity extends AppCompatActivity {
    private static final String TAG = "AddClothActivity";

    private ImagePicker mImagePick;
    private LoadImageHelper mImageWorker;
    private int mPreviewSize;

    private final static String TAG_PlaceholderFragment = "PlaceholderFragment";
    private final static String TAG_FragmentGrid = "PictureGrid";
    private final static String TAG_FragmentPreview = "PicturePreview";
    private final static String TAG_FragmentInputDesc = "InputDesc";

    private final static int CAMERA_RESULT = 8888;


    private final static String CaptureImageTempFolder = "/picFolder/";
    private static final String CaptureImageFileName = "CapturePicture.jpg";

    private int mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate begin");

        setContentView(R.layout.activity_add_cloth);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(),TAG_PlaceholderFragment)
                    .commit();
        }

        mFrom = SourceObject.SOURCE_START;
        SourceObject obj = getIntent().getParcelableExtra(SourceObject.SourceName);
        if (obj != null) {
            mFrom = obj.getSrcFrom();
        }

        mImagePick = new ImagePicker(this, savedInstanceState);

        mImageWorker = new LoadImageHelper(this);
        mImageWorker.beginWorker();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  //屏幕宽
        int height = dm.heightPixels;  //屏幕高
        Log.d(TAG, "onCreate width=" + width + ", height=" + height);

        int previewSize = 200;

        int value = Math.min(width, height);
        if (value > previewSize + 80) {
            previewSize = value - 80;
        }

        mPreviewSize = previewSize;
        mImageWorker.setScreenInfo(width, height);
        Log.d(TAG, "onCreate end");
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

    public void onStop() {
        Log.d(TAG, "onStop begin");
        super.onStop();
        mImageWorker.stopWorker();
        Log.d(TAG, "onStop end");
    }

    public void onResume() {
        Log.d(TAG, "onResume begin");
        super.onResume();
        Log.d(TAG, "onResume end");
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy begin");
        mImageWorker.clear();
        mImageWorker.stopWorker();
        Log.d(TAG, "onDestroy end");
        super.onDestroy();
    }

    public void onRestart() {
        Log.d(TAG, "onRestart begin");
        super.onRestart();
        mImageWorker.beginWorker();
        Log.d(TAG, "onRestart end");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu begin");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_cloth, menu);
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

    @Override
    public void onBackPressed() {
        //Fragment f = getSupportFragmentManager().findFragmentByTag(TAG_FragmentGrid);
        //if( f!= null ){

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (mFrom == SourceObject.SOURCE_CLOTH_LIST) {
                gotoClothListActivity();
                finish();
                return;
            }
        }

        super.onBackPressed();
    }

    private void gotoClothListActivity() {
        Intent it = new Intent();
        it.setClass(getBaseContext(), ClothListActivity.class);
        startActivity(it);
    }

    public static class DescriptionInputFragment extends Fragment {
        private long mPictureUID;
        private EditText mDescriptionEdit;
        private View mContentView;
        private Button mNextStep;
        private RadioGroup mGroup;
        private RadioButton mUpper;
        private RadioButton mLower;
        private int mClothType;

        private Handler mHandler;

        private ProgressDialog m_pDialog;

        private static final String TAG_DescriptionInputFragment = "DescriptionInputFrag";

        public static DescriptionInputFragment newInstance(long uid){
            DescriptionInputFragment frag = new DescriptionInputFragment();
            Bundle arg = new Bundle();
            arg.putLong("uid",uid);
            frag.setArguments(arg);
            return frag;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Log.d(TAG_DescriptionInputFragment, "onCreateView begin.");

            if( getArguments()!= null){
                mPictureUID = getArguments().getLong("uid");
            }
            mContentView = inflater.inflate(R.layout.fragment_add_cloth_description, container, false);
            assert (mContentView != null);

            mDescriptionEdit = (EditText) mContentView.findViewById(R.id.edit_description);
            assert (mDescriptionEdit != null);

            mNextStep = (Button) mContentView.findViewById(R.id.btn_next_step);
            assert (mNextStep != null);

            mGroup = (RadioGroup) mContentView.findViewById(R.id.cloth_type_group);
            assert (mGroup != null);

            mUpper = (RadioButton) mContentView.findViewById(R.id.cloth_type_upper);
            assert (mUpper != null);

            mLower = (RadioButton) mContentView.findViewById(R.id.cloth_type_lower);
            assert (mLower != null);

            mHandler = new Handler();

            mClothType = ClothType.UpperBody;
            mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (mUpper.isChecked()) {
                        mClothType = ClothType.UpperBody;
                    } else if (mLower.isChecked()) {
                        mClothType = ClothType.LowerBody;
                    }
                }
            });

            mNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity act = DescriptionInputFragment.this.getActivity();
                    if (!mUpper.isChecked() && !mLower.isChecked()) {
                        String Message = act.getResources().getString(R.string.cloth_type_error);
                        Toast.makeText(act, Message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    String text;
                    text = mDescriptionEdit.getText().toString();
                    if (text.length() == 0) {
                        String Message = act.getResources().getString(R.string.description_error);
                        Toast.makeText(act, Message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.d(TAG_DescriptionInputFragment, "do next step.");

                    popProgressDialog();

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DescriptionInputFragment.this.createClothObjectAndSaveIt();
                        }
                    });
                    t.start();
                }
            });

            Log.d(TAG_DescriptionInputFragment, "onCreateView end.");
            return mContentView;
        }

        private void createClothObjectAndSaveIt() {
            String filePath ;
            if( mPictureUID== -1){
                filePath = copyCapturePictureIntoMyFolder();
            }
            else {
                filePath = copyPictureIntoMyFolder();
            }

            assert (filePath != null);
            if (filePath == null) {
                Log.d(TAG_DescriptionInputFragment, "Could not copy Picture into my app folder.");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        createFail(0);
                    }
                });
                return;
            }

            String desc;
            desc = mDescriptionEdit.getText().toString();

            String clothName = saveClothIntoDatabase(filePath, desc);

            assert (clothName != null);
            if (clothName == null) {
                Log.d(TAG_DescriptionInputFragment, "Could not save data into database.");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        createFail(1);
                    }
                });
                return;
            }

            Cloth cloth = new Cloth(clothName, desc);
            assert (cloth != null);

            cloth.setType(mClothType);
            cloth.setImageFilePath(filePath);

            RootData root = RootData.getInstance();
            root.addCloth(cloth);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    createFinish();
                }
            });
            Log.d(TAG_DescriptionInputFragment, "createClothObjectAndSaveIt is end.");
        }

        private void createFail(int src) {
            disappearProgressDialog();

            Log.d(TAG_DescriptionInputFragment, "createFail, create cloth fail.");

            Activity act = getActivity();
            String Message;
            Message = act.getResources().getString(R.string.create_cloth_fail);
            if (src == 0) {
                Message = act.getResources().getString(R.string.create_cloth_fail_copy_file);
            } else if (src == 1) {
                Message = act.getResources().getString(R.string.create_cloth_fail_save_db);
            }
            Toast.makeText(act, Message, Toast.LENGTH_LONG).show();
        }

        private void createFinish() {
            disappearProgressDialog();
            //go to another activity.

            Log.d(TAG_DescriptionInputFragment, "createFinish will leave this activity. go to another activity ClothListActivity.");

            Activity act = getActivity();

            Intent it = new Intent();
            it.putExtra(SourceObject.SourceName, new SourceObject(SourceObject.SOURCE_Add_CLOTH));
            it.setClass(act.getBaseContext(), ClothListActivity.class);
            startActivity(it);

            act.finish();
        }

        private String copyCapturePictureIntoMyFolder(){
            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + CaptureImageTempFolder;
            String inFileName = dir + CaptureImageFileName;

            try{
                InputStream is = new FileInputStream(inFileName);

                //create cloth image name;
                StringBuilder sb = new StringBuilder("Cloth_Capture_");
                sb.append(RandomUtil.RandomStr(6));
                sb.append(".data");

                String outFilePath = sb.toString();
                FileOutputStream os = getActivity().openFileOutput(outFilePath, Context.MODE_PRIVATE);
                FileUtil.writeInputStreamIntoOutputStream(is, os);

                is.close();
                os.close();

                Log.d(TAG_DescriptionInputFragment, "copyCapturePictureIntoMyFolder success. file path is " + sb.toString());

                File sourceFile = new File(inFileName);
                if( sourceFile.exists() ){
                    sourceFile.delete();
                }
                return outFilePath;
            }
            catch (Exception e) {
                Log.d(TAG_DescriptionInputFragment, "copyCapturePictureIntoMyFolder does not find picture. UID = " + mPictureUID + ", e=" + e.toString());
                return null;
            }
        }

        private String copyPictureIntoMyFolder() {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Long.toString(mPictureUID)).build();
            Log.d(TAG_DescriptionInputFragment, "copyPictureIntoMyFolder Image UID: " + mPictureUID + ", uri : " + (uri != null ? uri.getPath() : ""));

            Activity act = getActivity();
            ContentResolver resolver = act.getContentResolver();
            assert (resolver != null);

            if (uri == null) {
                Log.d(TAG_DescriptionInputFragment, "copyPictureIntoMyFolder does not find picture. UID = " + mPictureUID);
                return null;
            }
            try {
                //create cloth image name;
                StringBuilder sb = new StringBuilder("Cloth_");
                sb.append(Long.toString(mPictureUID));
                sb.append("_");
                sb.append(RandomUtil.RandomStr(6));
                sb.append(".data");

                String outFilePath = sb.toString();
                InputStream is = resolver.openInputStream(Uri.parse(uri.toString()));

                FileOutputStream os = act.openFileOutput(outFilePath, Context.MODE_PRIVATE);
                FileUtil.writeInputStreamIntoOutputStream(is, os);

                is.close();
                os.close();

                Log.d(TAG_DescriptionInputFragment, "copyPictureIntoMyFolder success. file path is " + sb.toString());
                return outFilePath;
            } catch (Exception e) {
                Log.d(TAG_DescriptionInputFragment, "copyPictureIntoMyFolder does not find picture. UID = " + mPictureUID + ", e=" + e.toString());
                return null;
            }
        }

        private String saveClothIntoDatabase(String filePath, String desc) {
            Activity act = getActivity();

            MyDB db = new MyDB(act.getBaseContext());
            SQLiteDatabase writableDB = db.getWritableDatabase();

            ContentValues values = ClothDb.createValuesForInsert(filePath, desc, mClothType);
            try {
                writableDB.insertOrThrow(ClothDb.TABLE_NAME, null, values);
            } catch (android.database.SQLException e) {
                writableDB.close();
                Log.d(TAG_DescriptionInputFragment, "write cloth record fail. e=" + e.toString());
                return null;
            }

            writableDB.close();

            Log.d(TAG_DescriptionInputFragment, "saveClothIntoDatabase, insert success. ");

            SQLiteDatabase readableDB = db.getReadableDatabase();

            try {
                String querySQL = DBHelper.createQuerySQLForFindMaxID(ClothDb.TABLE_NAME, ClothDb.Field_id);
                Cursor cursor = readableDB.rawQuery(querySQL, null);
                if (cursor == null) {
                    Log.d(TAG_DescriptionInputFragment, "select max value is fail. cursor is null");
                    readableDB.close();
                    return null;
                }

                if (cursor.getCount() == 0) {
                    readableDB.close();
                    Log.d(TAG_DescriptionInputFragment, "select max value is fail. count of cursor is zero.");
                    return null;
                }

                cursor.moveToFirst();
                long id = DBHelper.getIdFromCursor(cursor);
                cursor.close();

                readableDB.close();

                String ret = Long.toString(id);

                Log.d(TAG_DescriptionInputFragment, "saveClothIntoDatabase, receive id success. id=" + id);
                return ret;
            } catch (Exception e) {
                readableDB.close();
                Log.d(TAG_DescriptionInputFragment, "saveClothIntoDatabase, receive id fail. e=" + e.toString());
                return null;
            }
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

    public static class PicturePreviewFragment extends Fragment {
        private final static String TAG_Pic_Prev = "PicturePreviewFragment";
        private View mContentView;
        private ImageView mPicView;
        private Bitmap mImage;
        private Handler mHandler;
        private long mUID;
        private Button mNextStep;

        private ProgressDialog m_pDialog;

        public static PicturePreviewFragment newInstance(long uid) {
            PicturePreviewFragment frag = new PicturePreviewFragment();
            Bundle arg = new Bundle();
            arg.putLong("uid", uid);
            frag.setArguments(arg);
            return frag;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Log.d(TAG_Pic_Prev, "onCreateView begin");

            if(getArguments()!= null){
                mUID = getArguments().getLong("uid");
            }

            mContentView = inflater.inflate(R.layout.fragment_add_cloth_preview, container, false);
            assert (mContentView != null);

            mPicView = (ImageView) mContentView.findViewById(R.id.viewPicture);
            assert (mPicView != null);

            mNextStep = (Button) mContentView.findViewById(R.id.btn_next_step);
            assert (mNextStep != null);

            mNextStep.setEnabled(false);
            mNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoDescriptionFragment();
                }
            });

            mHandler = new Handler();

            popProgressDialog();
            updatePicView();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    loadPicture(mUID);
                }
            });
            t.start();

            Log.d(TAG_Pic_Prev, "onCreateView end.");
            return mContentView;
        }

        private void gotoDescriptionFragment(){
            FragmentTransaction trans = PicturePreviewFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
            DescriptionInputFragment newFrag = DescriptionInputFragment.newInstance(mUID);
            if (!newFrag.isAdded()) {
                trans.hide(PicturePreviewFragment.this).add(R.id.container, newFrag, TAG_FragmentInputDesc);
            } else {
                trans.hide(PicturePreviewFragment.this).show(newFrag);
            }
            trans.addToBackStack(TAG_FragmentInputDesc);
            trans.commit();
            Log.d(TAG_Pic_Prev, "go to another fragment: DescriptionInputFragment.");
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
        }

        private void updatePicView() {
            Activity act = getActivity();
            if (mImage != null) {
                mPicView.setImageBitmap(mImage);
            } else {
                mPicView.setImageDrawable(act.getResources().getDrawable(R.drawable.invalid));
            }
        }

        private void enableNextStepBtn() {
            mNextStep.setEnabled(true);
        }

        private void loadPicture(long uid) {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Long.toString(uid)).build();
            Log.d(TAG_Pic_Prev, "loadPicture Image UID: " + uid + ", uri : " + (uri != null ? uri.getPath() : ""));

            Activity act = getActivity();
            AddClothActivity addAct = (AddClothActivity) act;
            Log.d(TAG_Pic_Prev, "loadPicture Image previewSize=" + addAct.mPreviewSize);

            ContentResolver resolver = act.getContentResolver();
            if (uri == null) {
                return;
            }

            try {
                mImage = FileUtil.getBitmapFromStream(resolver.openInputStream(Uri.parse(uri.toString())), addAct.mPreviewSize, addAct.mPreviewSize);
            } catch (Exception e) {
                mImage = null;
                Log.d(TAG_Pic_Prev, "loadPicture Read Image resource fail.e=" + e.toString());
                return;
            }

            if (mImage == null) {
                Log.d(TAG_Pic_Prev, "loadPicture Read Image resource fail. mImage == null");
                return;
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updatePicView();
                    enableNextStepBtn();
                    disappearProgressDialog();
                }
            });
            Log.d(TAG_Pic_Prev, "loadPicture end : " + uid);
        }
    }

    public static class PictureGridFragment extends Fragment {
        private View mContentView;

        private GridView mPicGrid;
        private PicGridAdapter mAdapter;
        private static final String PicGrid_TAG = "PictureGridFragment";

        public PictureGridFragment() {
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.fragment_add_cloth_grid, container, false);
            Log.d(PicGrid_TAG, "onCreateView before iniPictureGrid.");
            initPictureGrid();

            Log.d(PicGrid_TAG, "onCreateView end.");
            return mContentView;
        }

        private void initPictureGrid() {
            mPicGrid = (GridView) mContentView.findViewById(R.id.pic_grid);
            if (mPicGrid == null) {
                Log.d(PicGrid_TAG, "Not found grid.");
                return;
            }

            final Activity act = getActivity();
            final AddClothActivity clothActivity = (AddClothActivity) act;
            ArrayList<ImageData> allPic = clothActivity.mImagePick.getAllImage();
            mAdapter = new PicGridAdapter(act, allPic, R.layout.picture_item, R.id.itemDescription, R.id.itemPicture, clothActivity.mImageWorker, R.drawable.ic_launcher, R.drawable.invalid);
            clothActivity.mImageWorker.setBaseAdapter(mAdapter);
            clothActivity.mImageWorker.setData(allPic);
            mPicGrid.setAdapter(mAdapter);
            mPicGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(PicGrid_TAG, "mPicGrid onItemClick i=" + i + ",l=" + l);
                    //Intent it = new Intent();
                    //it.setClass(act.getBaseContext(),PicViewActivity.class);

                    ArrayList<ImageData> data = clothActivity.mImagePick.getAllImage();
                    ImageData value = data.get(i);
                    //it.putExtra("fromObj", new PicViewObject(value.getUID()));
                    //startActivity(it);

                    if (value.isImgBad()) {
                        String message = act.getResources().getString(R.string.bad_picture_for_cloth);
                        Toast.makeText(act, message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    FragmentTransaction trans = clothActivity.getSupportFragmentManager().beginTransaction();
                    PicturePreviewFragment newFrag = PicturePreviewFragment.newInstance(value.getUID());
                    if (!newFrag.isAdded()) {
                        trans.hide(PictureGridFragment.this).add(R.id.container, newFrag, TAG_FragmentPreview);
                    } else {
                        trans.hide(PictureGridFragment.this).show(newFrag);
                    }
                    trans.addToBackStack(TAG_FragmentPreview);
                    trans.commit();

                    Log.d(PicGrid_TAG, "go to another fragment: PicturePreviewFragment.");
                }
            });
        }
    }

//    public static class PictureListFragment extends Fragment{
//        private ListView mPicList;
//        private View contentView;
//        private ArrayList<HashMap<String, String>> mDataSource;
//        public PictureListFragment(){
//
//        }
//
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState){
//            contentView = inflater.inflate(R.layout.fragment_add_cloth_choose_picture, container, false);
//            initPictureList();
//            return contentView;
//        }
//
//        private void initPictureList(){
//            mPicList = (ListView) contentView.findViewById(R.id.PictureList);
//            if (mPicList == null) {
//                return;
//            }
//
//            mDataSource = loadData();
//            SimpleAdapter listItemAdapter = new SimpleAdapter(contentView.getContext(), mDataSource,
//                    R.layout.picture_item,
//                    new String[] {"ItemName"},
//                    new int[] {R.id.itemDescription});
//            mPicList.setAdapter(listItemAdapter);
//
//            mPicList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    HashMap<String, String> tmp = mDataSource.get(i);
//                    String value = tmp.get("ItemName");
//                    String formatStr = getResources().getString( R.string.toast_format_listItem);
//                    String Message = String.format(formatStr,value);
//                    Context c = contentView.getContext();
//                    if (c != null) {
//                        Toast.makeText(c, Message, Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//        }
//
//        private ArrayList<HashMap<String, String>> loadData() {
//            Activity act = getActivity();
//            AddClothActivity clothActivity = (AddClothActivity)act;
//            ArrayList<ImageData> allPic = clothActivity.mImagePick.getAllImage();
//
//            ArrayList<HashMap<String, String>> tmp = new ArrayList<HashMap<String, String>>();
//            for(ImageData data : allPic)
//            {
//                HashMap<String,String> map = new HashMap<String, String>();
//                map.put("ItemName", data.getTitle());
//                tmp.add(map);
//            }
//
//            return tmp;
//        }
//    }

    public static class CapturePictureViewFragment extends Fragment {
        private View mContentView;
        private String mFileName;

        private ImageView mPicView;

        private Button mNextStep;

        public static CapturePictureViewFragment newInstance(String fileName) {
            CapturePictureViewFragment frag = new CapturePictureViewFragment();
            Bundle args = new Bundle();
            args.putString("name", fileName);
            frag.setArguments(args);
            return frag;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.fragment_add_new_cloth_show_capture_image, container, false);
            assert (mContentView != null);

            if (getArguments() != null) {
                mFileName = getArguments().getString("name");
            }

            mPicView = (ImageView) mContentView.findViewById(R.id.picture_show);
            boolean ret = loadBitmapFromFile();
            if( !ret){
                mPicView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.invalid));
            }


            mNextStep = (Button)mContentView.findViewById(R.id.btn_next_step);
            assert (mNextStep != null);

            mNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoDescriptionFragment();
                }
            });

            return mContentView;
        }

        private void gotoDescriptionFragment(){
            FragmentTransaction trans = CapturePictureViewFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
            DescriptionInputFragment newFrag = DescriptionInputFragment.newInstance(-1);
            if (!newFrag.isAdded()) {
                trans.hide(CapturePictureViewFragment.this).add(R.id.container, newFrag, TAG_FragmentInputDesc);
            } else {
                trans.hide(CapturePictureViewFragment.this).show(newFrag);
            }
            trans.addToBackStack(TAG_FragmentInputDesc);
            trans.commit();
        }

        private boolean loadBitmapFromFile() {
            Activity act = getActivity();
            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + CaptureImageTempFolder;
            String filePathOfImage = dir + mFileName;

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements ImageLoadingFinish {
        private static final String FragmentTag = "PlaceholderFrag";
        private Button mNextStep;
        private Button mCaptureImage;
        private ProgressDialog m_pDialog;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.d(FragmentTag, "onCreateView begin");

            View rootView = inflater.inflate(R.layout.fragment_add_cloth, container, false);
            assert rootView != null;

            AddClothActivity clothActivity = (AddClothActivity) getActivity();
            clothActivity.mImagePick.setFinishHandler(this);

            mNextStep = (Button) rootView.findViewById(R.id.btn_next_step);
            assert(mNextStep!=null);
            mNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseImageFromPhoto();
                }
            });

            mCaptureImage = (Button)rootView.findViewById(R.id.capture_image);
            assert(mCaptureImage!=null);

            mCaptureImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doCaptureImageForCloth();
                }
            });
            return rootView;
        }

        private void doCaptureImageForCloth(){
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + CaptureImageTempFolder;
            File newDir = new File(dir);
            newDir.mkdirs();

            String file = dir + CaptureImageFileName;
            File newFile = new File(file);
            try {
                if( !newFile.exists()) {
                    newFile.createNewFile();
                }
            } catch (IOException e) {}

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.e(TAG, "doCaptureImageForCloth todo");
            }
            else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
                startActivityForResult(intent, CAMERA_RESULT);
            }
        }

        private void chooseImageFromPhoto(){
            Log.d(FragmentTag, "mNextStep OnClickListener.");

            Activity act = getActivity();

            m_pDialog = new ProgressDialog(act);
            m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            String waitStr = getResources().getString(R.string.waiting_text);
            m_pDialog.setMessage(waitStr);
            m_pDialog.setIndeterminate(false);
            // 设置ProgressDialog 是否可以按退回按键取消
            m_pDialog.setCancelable(false);
            m_pDialog.show();

            if (act instanceof AddClothActivity) {
                AddClothActivity abActivity = (AddClothActivity) act;
                abActivity.mImagePick.pullAllImage();
            } else {
                Log.d(FragmentTag, "activity is wrong");
                m_pDialog.hide();
            }
        }

        private void switchNextUI() {
            Activity act = getActivity();
            if (!(act instanceof AddClothActivity)) {
                return;
            }

            AddClothActivity clothActivity = (AddClothActivity) act;
            Log.d(FragmentTag, "switchNextUI, getAllImage = " + clothActivity.mImagePick.getAllImage().size());

            m_pDialog.hide();
            m_pDialog.dismiss();

            //FragmentTransaction trans = clothActivity.getSupportFragmentManager().beginTransaction();
            //trans.replace(R.id.container, new PictureListFragment(),"PictureList");
            //trans.commitAllowingStateLoss();

            FragmentTransaction trans = clothActivity.getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.container, new PictureGridFragment(), TAG_FragmentGrid);
            trans.commitAllowingStateLoss();

            Log.d(FragmentTag, "switchNextUI ,go to another fragment:PictureGridFragment. ");
        }

        @Override
        public void loadFinish() {
            switchNextUI();
        }

        public void gotoCaptureImageView(){
            CapturePictureViewFragment newFrag = CapturePictureViewFragment.newInstance(CaptureImageFileName);

            FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
            if (!newFrag.isAdded()) {
                trans.hide(PlaceholderFragment.this).add(R.id.container, newFrag, "CaptureImageView");
            } else {
                trans.hide(PlaceholderFragment.this).show(newFrag);
            }
            trans.addToBackStack("CaptureImageView");
            trans.commit();
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA_RESULT) {
                gotoCaptureImageView();
            }
        }
    }


}
