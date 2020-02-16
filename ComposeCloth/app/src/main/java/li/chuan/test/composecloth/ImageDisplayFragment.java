package li.chuan.test.composecloth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import li.chuan.test.composecloth.db.ImageDataQuery;
import li.chuan.test.composecloth.db.ImageInfoSaver;
import li.chuan.test.composecloth.entity.ImageData;

/**
 * A fragment with a button
 */
public class ImageDisplayFragment extends Fragment {
    private static final String TAG = "ImageDisplayF";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "param1";
    private static final String ARG_PATH = "param2";

    private String mName;
    private String mPATH;
    private Button mOkButton;

    private int mKindValue;

    public ImageDisplayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageDisplayFragment.
     */
    public static ImageDisplayFragment newInstance(String param1, String param2) {
        ImageDisplayFragment fragment = new ImageDisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, param1);
        args.putString(ARG_PATH, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ARG_NAME);
            mPATH = getArguments().getString(ARG_PATH);
            Log.d(TAG, "onCreate mName=" + mName + ",mPATH=" + mPATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_display, container, false);

        mKindValue = ImageData.KIND_UNKNOWN;

        //Find the button
        mOkButton = view.findViewById(R.id.ok_button);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonOkClicked();
            }
        });
        mOkButton.setEnabled(false);

        RadioGroup aGroup = view.findViewById(R.id.rg_part);
        aGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioGroupCheckedChanged(group,checkedId);
            }
        } );

        Button aBackButton = view.findViewById(R.id.back_button);
        aBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonBackClicked();
            }
        });

        Log.d(TAG, "onCreateView, mPATH=" + mPATH);

        ImageView v = view.findViewById(R.id.detail_image);
        Uri uri = Uri.fromFile(new File(mPATH));
        //通过setImageURI设置路径
        v.setImageURI(uri);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void buttonOkClicked(){
        Log.d(TAG, "buttonOkClicked");
        ImageDataQuery query = new ImageDataQuery(getActivity());
        boolean find = query.queryPath(mPATH);

        ImageInfoSaver saver = new ImageInfoSaver(getActivity());
        if (find){
            saver.delete(mPATH);
        }
        saver.save(mName, mPATH, mKindValue);

        backLastFragment();
    }

    private void buttonBackClicked(){
        Log.d(TAG, "buttonBackClicked");
        backLastFragment();
    }

    private void backLastFragment(){
        try {
            FragmentActivity act = getActivity();
            FragmentManager f = act.getSupportFragmentManager();
            f.popBackStack();
        }catch (NullPointerException e){
            Log.e(TAG, "catch Null Pointer Exception.");
            StackTraceElement[] elements = e.getStackTrace();
            for(StackTraceElement element:elements) {
                Log.e(TAG, element.toString());
            }
        }
    }

    private void radioGroupCheckedChanged(RadioGroup group, int checkedId){
        switch(checkedId){
            case R.id.rb_up:
                mKindValue = ImageData.KIND_UP;
                mOkButton.setEnabled(true);
                break;
            case R.id.rb_down:
                mKindValue = ImageData.KIND_DOWN;
                mOkButton.setEnabled(true);
                break;
            case R.id.rb_total:
                mKindValue = ImageData.KIND_TOTAL;
                mOkButton.setEnabled(true);
                break;
            default:
                break;
        }
    }
}
