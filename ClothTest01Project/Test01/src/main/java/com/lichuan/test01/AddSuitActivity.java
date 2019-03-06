package com.lichuan.test01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.lichuan.test01.adapter.ClothMChooseGridAdapter;
import com.lichuan.test01.datamodel.Cloth;
import com.lichuan.test01.datamodel.ClothType;
import com.lichuan.test01.datamodel.RootData;
import com.lichuan.test01.datamodel.Suit;
import com.lichuan.test01.msg.SourceObject;
import com.lichuan.test01.service.MyDB;
import com.lichuan.test01.service.db.DBHelper;
import com.lichuan.test01.service.db.SuitDB;
import com.lichuan.test01.service.db.SuitMappingClothDB;

import java.util.ArrayList;

public class AddSuitActivity extends AppCompatActivity {
    private static final String TAG = "AddSuitActivity";

    private static final String AddDescriptionForSuitFragmentFlag = "AddDescriptionForSuitFragment";
    private static final String ChooseClothForSuitFragmentFlag = "ChooseClothForSuitFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_suit);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_suit, menu);
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


    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, " == onConfigurationChanged occur");
    }

    public static class ChooseClothForSuitFragment extends Fragment {
        private String mDescription;
        private View mContentView;
        private Button mNext;
        private GridView mGrid;
        private ClothMChooseGridAdapter mAdapter;

        private ProgressDialog m_pDialog;
        private final Handler mHandler;
        private static final String ChooseClothForSuitFragmentTAG = "ChooseClothForSuitFrag";


        public static ChooseClothForSuitFragment newInstance(String desc){
            ChooseClothForSuitFragment frag = new ChooseClothForSuitFragment();
            Bundle arg = new Bundle();
            arg.putString("desc", desc);
            frag.setArguments(arg);
            return frag;
        }
        public ChooseClothForSuitFragment() {
            mHandler = new Handler();
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            if( getArguments()!=null){
                mDescription = getArguments().getString("desc");
            }

            mContentView = inflater.inflate(R.layout.fragment_add_suit_choose_cloth, container, false);
            assert (mContentView != null);

            mGrid = (GridView) mContentView.findViewById(R.id.choose_cloth_grid);
            assert (mGrid != null);

            mNext = (Button) mContentView.findViewById(R.id.btn_next_step);
            assert (mNext != null);

            RootData root = RootData.getInstance();
            ArrayList<Cloth> clothes = root.getCloneClothes();


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

            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tryToAddSuit();
                }
            });
            return mContentView;
        }

        private void tryToAddSuit() {
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
                    ChooseClothForSuitFragment.this.createSuitAndSaveIt();
                }
            });
            t.start();
        }

        private void createSuitAndSaveIt() {
            String suitName = saveSuitIntoDatabase();

            if (suitName == null) {
                Log.d(ChooseClothForSuitFragmentTAG, "Could not save suit into database.");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        createSuitFail();
                    }
                });
                return;
            }

            RootData root = RootData.getInstance();

            Suit suit = new Suit(suitName, mDescription);
            root.addSuit(suit);

            ArrayList<Cloth> selectedCloth = new ArrayList<Cloth>();
            ArrayList<Cloth> clothes = root.getCloneClothes();
            for (Cloth c : clothes) {
                if (c.isSelected()) {
                    selectedCloth.add(c);
                }
            }

            boolean bSuccess = saveSuitAndClothesRelationIntoDb(suit, selectedCloth);
            if (!bSuccess) {
                Log.d(ChooseClothForSuitFragmentTAG, "saveSuitAndClothesRelationIntoDb return false.");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        createSuitFail();
                    }
                });
                return;
            }

            for (Cloth c : selectedCloth) {
                suit.addCloth(c);
                c.setSelected(false);
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    createSuitFinish();
                }
            });
            Log.d(ChooseClothForSuitFragmentTAG, "createSuitObjectAndSaveIt is end.");
        }

        private boolean saveSuitAndClothesRelationIntoDb(Suit suit, ArrayList<Cloth> selectedCloth) {
            Activity act = getActivity();
            MyDB db = new MyDB(act.getBaseContext());
            SQLiteDatabase writableDB = db.getWritableDatabase();

            for (Cloth c : selectedCloth) {
                ContentValues values = SuitMappingClothDB.createValuesForInsert(Integer.parseInt(suit.getName()), Integer.parseInt(c.getName()));
                try {
                    writableDB.insertOrThrow(SuitMappingClothDB.TABLE_NAME, null, values);
                } catch (android.database.SQLException e) {
                    Log.d(ChooseClothForSuitFragmentTAG, "write suit record fail. e=" + e.toString());
                    writableDB.close();
                    return false;
                }
            }

            writableDB.close();

            return true;
        }

        private void createSuitFinish() {
            disappearProgressDialog();

            Activity act = getActivity();

            Intent it = new Intent();
            it.putExtra(SourceObject.SourceName, new SourceObject(SourceObject.SOURCE_Add_SUIT));
            it.setClass(act.getBaseContext(), ClothListActivity.class);
            startActivity(it);

            act.finish();
        }

        private void createSuitFail() {
            disappearProgressDialog();

            Log.d(ChooseClothForSuitFragmentTAG, "createSuitFail, create suit fail.");

            Activity act = getActivity();
            String Message = act.getResources().getString(R.string.create_suit_fail);
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

        private String saveSuitIntoDatabase() {
            Activity act = getActivity();
            MyDB db = new MyDB(act.getBaseContext());
            SQLiteDatabase writableDB = db.getWritableDatabase();

            ContentValues values = SuitDB.createValuesForInsert(mDescription, Suit.UNKNOWN_PUBLISH);
            try {
                writableDB.insertOrThrow(SuitDB.TABLE_NAME, null, values);
            } catch (android.database.SQLException e) {
                Log.d(ChooseClothForSuitFragmentTAG, "write suit record fail. e=" + e.toString());
                writableDB.close();
                return null;
            }


            writableDB.close();

            Log.d(ChooseClothForSuitFragmentTAG, "saveSuitIntoDatabase, insert success. ");

            SQLiteDatabase readableDB = db.getReadableDatabase();

            try {
                String querySQL = DBHelper.createQuerySQLForFindMaxID(SuitDB.TABLE_NAME, SuitDB.Field_id);
                Cursor cursor = readableDB.rawQuery(querySQL, null);
                if (cursor == null) {
                    readableDB.close();
                    Log.d(ChooseClothForSuitFragmentTAG, "select max value is fail. cursor is null");
                    return null;
                }

                if (cursor.getCount() == 0) {
                    readableDB.close();
                    Log.d(ChooseClothForSuitFragmentTAG, "select max value is fail. count of cursor is zero.");
                    return null;
                }

                cursor.moveToFirst();
                long id = DBHelper.getIdFromCursor(cursor);
                cursor.close();

                readableDB.close();

                String ret = Long.toString(id);

                Log.d(ChooseClothForSuitFragmentTAG, "saveSuitIntoDatabase, receive id success. id=" + id);
                return ret;
            } catch (Exception e) {
                Log.d(ChooseClothForSuitFragmentTAG, "saveSuitIntoDatabase, receive id fail. e=" + e.toString());
                readableDB.close();
                return null;
            }
        }
    }

    public static class AddDescriptionForSuitFragment extends Fragment {
        private Button mNext;
        private EditText mDesc;

        public AddDescriptionForSuitFragment() {

        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_suit_add_description, container, false);
            assert (rootView != null);


            mDesc = (EditText) rootView.findViewById(R.id.edit_description);
            assert (mDesc != null);

            mNext = (Button) rootView.findViewById(R.id.btn_next_step);
            assert (mNext != null);

            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String msg = mDesc.getText().toString();
                    if (msg.length() <= 0) {
                        String message = getString(R.string.suit_description_error);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    ChooseClothForSuitFragment newFrag = ChooseClothForSuitFragment.newInstance(msg);
                    FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                    if (!newFrag.isAdded()) {
                        trans.hide(AddDescriptionForSuitFragment.this).add(R.id.container, newFrag, ChooseClothForSuitFragmentFlag);
                    } else {
                        trans.hide(AddDescriptionForSuitFragment.this).show(newFrag);
                    }
                    trans.addToBackStack(ChooseClothForSuitFragmentFlag);
                    trans.commit();

                }
            });
            return rootView;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private Button mNext;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_suit, container, false);
            assert (rootView != null);

            mNext = (Button) rootView.findViewById(R.id.btn_next_step);
            assert (mNext != null);

            mNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddDescriptionForSuitFragment newFrag = new AddDescriptionForSuitFragment();

                    FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                    if (!newFrag.isAdded()) {
                        trans.hide(PlaceholderFragment.this).add(R.id.container, newFrag, AddDescriptionForSuitFragmentFlag);
                    } else {
                        trans.hide(PlaceholderFragment.this).show(newFrag);
                    }
                    trans.addToBackStack(AddDescriptionForSuitFragmentFlag);
                    trans.commit();
                }
            });

            return rootView;
        }
    }

}
