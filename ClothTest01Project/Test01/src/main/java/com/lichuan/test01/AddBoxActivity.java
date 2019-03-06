package com.lichuan.test01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.lichuan.test01.adapter.BoxImageGridAdapter;
import com.lichuan.test01.datamodel.Box;
import com.lichuan.test01.datamodel.BoxImageType;
import com.lichuan.test01.datamodel.RootData;
import com.lichuan.test01.msg.SourceObject;
import com.lichuan.test01.service.MyDB;
import com.lichuan.test01.service.db.BoxDb;
import com.lichuan.test01.service.db.DBHelper;

public class AddBoxActivity extends AppCompatActivity {
    private int mFrom;

    private static final String TAG = "AddBoxActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_box);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DescriptionFragment())
                    .commit();
        }

        mFrom = SourceObject.SOURCE_START;
        SourceObject obj = getIntent().getParcelableExtra(SourceObject.SourceName);
        if (obj != null) {
            mFrom = obj.getSrcFrom();
        }
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
        Log.d(TAG, "onStop end");
    }

    public void onResume() {
        Log.d(TAG, "onResume begin");
        super.onResume();
        Log.d(TAG, "onResume end");
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy begin");
        Log.d(TAG, "onDestroy end");
        super.onDestroy();
    }

    public void onRestart() {
        Log.d(TAG, "onRestart begin");
        super.onRestart();
        Log.d(TAG, "onRestart end");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_box, menu);
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
    public void onBackPressed() {
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

    public static class ChooseImageFragment extends Fragment {
        private String mDescription;
        private View mContentView;

        private Button mNextStep;
        private GridView mGrid;
        private ImageView mPreView;

        private int mImageTypeOfBox;

        private ProgressDialog m_pDialog;
        private Handler mHandler;

        private final static String ChooseImageFragment_TAG = "ChooseImageFragment";

        public static ChooseImageFragment newInstance(String description){
            ChooseImageFragment frag = new ChooseImageFragment();
            Bundle args = new Bundle();
            args.putString("param", description);
            frag.setArguments(args);
            return frag;
        }
        public ChooseImageFragment() {
            mImageTypeOfBox = BoxImageType.Use_ImagePath;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.fragment_add_box_choose_image, container, false);
            if(getArguments()!= null){
                mDescription = getArguments().getString("param");
            }

            initOtherWeight();

            mHandler = new Handler();
            return mContentView;
        }

        private void initOtherWeight() {
            mNextStep = (Button) mContentView.findViewById(R.id.btn_next_step);
            assert (mNextStep != null);

            mPreView = (ImageView) mContentView.findViewById(R.id.imageOfBox);
            assert (mPreView != null);

            mGrid = (GridView) mContentView.findViewById(R.id.pic_grid);
            assert (mGrid != null);

            mGrid.setAdapter(new BoxImageGridAdapter(getActivity()));
            mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l2) {
                    Log.d(ChooseImageFragment_TAG, "onItemClick i=" + i + ",l2=" + l2);
                    mImageTypeOfBox = BoxImageType.getImageTypeByIndex(i);

                    Activity act = ChooseImageFragment.this.getActivity();
                    int ResourceID = BoxImageType.computeResourceIdByIndex(i);
                    mPreView.setImageDrawable(act.getResources().getDrawable(ResourceID));
                }
            });

            mNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity act = ChooseImageFragment.this.getActivity();

                    if (mImageTypeOfBox == BoxImageType.Use_ImagePath) {
                        String Message = act.getResources().getString(R.string.box_image_error);
                        Toast.makeText(act, Message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.d(ChooseImageFragment_TAG, "mImageTypeOfBox=" + mImageTypeOfBox);
                    Log.d(ChooseImageFragment_TAG, "do next step.");

                    popProgressDialog();

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ChooseImageFragment.this.createBoxObjectAndSaveIt();
                        }
                    });
                    t.start();
                }
            });
        }

        private void createBoxObjectAndSaveIt() {
            String boxName = saveBoxIntoDatabase();

            if (boxName == null) {
                Log.d(ChooseImageFragment_TAG, "Could not save box into database.");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        createFail();
                    }
                });
                return;
            }

            Box box = new Box(boxName, mDescription);
            box.setImageType(mImageTypeOfBox);

            RootData root = RootData.getInstance();
            root.addBox(box);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    createFinish();
                }
            });
            Log.d(ChooseImageFragment_TAG, "createBoxObjectAndSaveIt is end.");
        }

        private String saveBoxIntoDatabase() {
            Activity act = getActivity();

            MyDB db = new MyDB(act.getBaseContext());
            SQLiteDatabase writableDB = db.getWritableDatabase();

            ContentValues values = BoxDb.createValuesForInsert(mDescription, mImageTypeOfBox, Box.InvalidPath);
            try {
                writableDB.insertOrThrow(BoxDb.TABLE_NAME, null, values);
            } catch (android.database.SQLException e) {
                Log.d(ChooseImageFragment_TAG, "write box record fail. e=" + e.toString());
                writableDB.close();
                return null;
            }

            writableDB.close();

            Log.d(ChooseImageFragment_TAG, "saveBoxIntoDatabase, insert success. ");

            SQLiteDatabase readableDB = db.getReadableDatabase();

            try {
                String querySQL = DBHelper.createQuerySQLForFindMaxID(BoxDb.TABLE_NAME, BoxDb.Field_id);
                Cursor cursor = readableDB.rawQuery(querySQL, null);
                if (cursor == null) {
                    Log.d(ChooseImageFragment_TAG, "select max value is fail. cursor is null");
                    readableDB.close();
                    return null;
                }

                if (cursor.getCount() == 0) {
                    Log.d(ChooseImageFragment_TAG, "select max value is fail. count of cursor is zero.");
                    readableDB.close();
                    return null;
                }

                cursor.moveToFirst();
                long id = DBHelper.getIdFromCursor(cursor);
                cursor.close();

                readableDB.close();

                String ret = Long.toString(id);

                Log.d(ChooseImageFragment_TAG, "saveBoxIntoDatabase, receive id success. id=" + id);
                return ret;
            } catch (Exception e) {
                Log.d(ChooseImageFragment_TAG, "saveBoxIntoDatabase, receive id fail. e=" + e.toString());
                readableDB.close();
                return null;
            }
        }

        private void createFinish() {
            disappearProgressDialog();

            Activity act = getActivity();

            Intent it = new Intent();
            it.putExtra(SourceObject.SourceName, new SourceObject(SourceObject.SOURCE_Add_BOX));
            it.setClass(act.getBaseContext(), ClothListActivity.class);
            startActivity(it);

            act.finish();
        }

        private void createFail() {
            disappearProgressDialog();

            Log.d(ChooseImageFragment_TAG, "createFail, create box fail.");

            Activity act = getActivity();
            String Message = act.getResources().getString(R.string.create_box_fail);
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

    public static class AddDescriptionFragment extends Fragment {
        private View mContentView;
        private Button mNextStep;
        private EditText mDescriptionEdit;

        private final static String AddDescTag = "AddDescTag";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mContentView = inflater.inflate(R.layout.fragment_add_box_add_desc, container, false);

            initOtherWeight();
            return mContentView;
        }

        private void initOtherWeight() {
            mNextStep = (Button) mContentView.findViewById(R.id.btn_next_step);
            assert (mNextStep != null);

            mDescriptionEdit = (EditText) mContentView.findViewById(R.id.edit_description);
            assert (mDescriptionEdit != null);

            mNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity act = AddDescriptionFragment.this.getActivity();

                    String text;
                    text = mDescriptionEdit.getText().toString();
                    if (text.length() == 0) {
                        String Message = act.getResources().getString(R.string.box_description_error);
                        Toast.makeText(act, Message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Log.d(AddDescTag, "do next step.");

                    FragmentTransaction trans = AddDescriptionFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
                    ChooseImageFragment newFrag;
                    newFrag = ChooseImageFragment.newInstance(text);
                    if (!newFrag.isAdded()) {
                        trans.hide(AddDescriptionFragment.this).add(R.id.container, newFrag, "ChooseImageForBox");
                    } else {
                        trans.hide(AddDescriptionFragment.this).show(newFrag);
                    }
                    trans.addToBackStack(AddDescTag);
                    trans.commit();
                    Log.d(AddDescTag, "go to another fragment: ChooseImageFragment.");
                }
            });

        }


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DescriptionFragment extends Fragment {
        private final static String BOX_DESC_TAG = "DescriptionFragment";
        private Button mNextStep;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_box_description, container, false);

            mNextStep = (Button) rootView.findViewById(R.id.btn_next_step);
            assert (mNextStep != null);

            mNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction trans = DescriptionFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
                    AddDescriptionFragment newFrag = new AddDescriptionFragment();
                    trans.replace(R.id.container, newFrag, BOX_DESC_TAG);
                    trans.commit();
                    Log.d(BOX_DESC_TAG, "go to another fragment: Add Description for box.");
                }
            });
            return rootView;
        }
    }

}
