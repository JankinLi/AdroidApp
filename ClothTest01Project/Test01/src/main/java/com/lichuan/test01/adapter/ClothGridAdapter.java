package com.lichuan.test01.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lichuan.test01.R;
import com.lichuan.test01.datamodel.Cloth;
import com.lichuan.test01.datamodel.ClothType;

import java.util.ArrayList;

/**
 * Created by guoym on 15-6-26.
 */
public class ClothGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Cloth> mAllClothes;

    private static final String TAG = "ClothGridAdapter";

    public ClothGridAdapter(Context c, ArrayList<Cloth> allClothes) {
        mContext = c;
        mAllClothes = new ArrayList<Cloth>();
        mAllClothes.addAll(allClothes);
    }

    @Override
    public int getCount() {
        return mAllClothes.size();
    }

    @Override
    public Object getItem(int i) {
        return mAllClothes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View contentView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            contentView = inflater.inflate(R.layout.cloth_item, null, false);
            assert (contentView != null);
        } else {
            contentView = view;
        }

        TextView descriptionView = (TextView) contentView.findViewById(R.id.clothDescription);
        assert (descriptionView != null);

        ImageView picView = (ImageView) contentView.findViewById(R.id.clothPicture);
        assert (picView != null);

        TextView typeView = (TextView) contentView.findViewById(R.id.clothType);
        assert (typeView != null);

        Cloth cloth = mAllClothes.get(i);

        descriptionView.setText(cloth.getDescription());

        typeView.setText(ClothType.convertString(mContext, cloth.getType()));

        picView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));
        if (cloth.isBad()) {
            picView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.invalid));
        } else {
            Bitmap img = cloth.getBitmap();
            if (img != null) {
                picView.setImageBitmap(img);
            }
        }

        Log.d(TAG, "cloth refresh view . cloth=" + cloth.getName());
        return contentView;
    }
}
