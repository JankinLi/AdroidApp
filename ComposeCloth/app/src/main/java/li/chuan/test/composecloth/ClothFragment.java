package li.chuan.test.composecloth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import li.chuan.test.composecloth.entity.ImageData;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClothFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClothFragment extends Fragment {
    private static final String TAG = "ClothF";

    private static final String ARG_NAME = "display_name";
    private static final String ARG_PATH = "path";
    private static final String ARG_KIND = "kind";

    private String mDisplayName;
    private String mPath;
    private int mKindValue;

    public ClothFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Display Name
     * @param path Path of Image
     * @param kindValue value of kind
     * @return A new instance of fragment ClothFragment.
     */
    public static ClothFragment newInstance(String name, String path, int kindValue) {
        ClothFragment fragment = new ClothFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_PATH, path);
        args.putInt(ARG_KIND, kindValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDisplayName = getArguments().getString(ARG_NAME);
            mPath = getArguments().getString(ARG_PATH);
            mKindValue = getArguments().getInt(ARG_KIND);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cloth, container, false);

        Button aBackButton = view.findViewById(R.id.button_back);
        aBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonBackClicked();
            }
        });

        ImageView v = view.findViewById(R.id.cloth_display_detail);
        Uri uri = Uri.fromFile(new File(mPath));
        //通过setImageURI设置路径
        v.setImageURI(uri);

        TextView t = view.findViewById(R.id.cloth_kind_value);
        int resId = computeKindDisplayValue(mKindValue);
        String kindDisplayValue = getString(resId);
        t.setText(kindDisplayValue);

        TextView tt = view.findViewById(R.id.cloth_display_name);
        tt.setText(mDisplayName);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void buttonBackClicked(){
        Log.d(TAG, "buttonBackClicked");
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private int computeKindDisplayValue(int value){
        switch(value){
            case ImageData.KIND_UP:
                return R.string.radio_up_text;
            case ImageData.KIND_DOWN:
                return R.string.radio_down_text;
            case ImageData.KIND_TOTAL:
                return R.string.radio_total_text;
            default:
                return R.string.radio_unknown_text;
        }
    }
}
