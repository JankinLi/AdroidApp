package com.lichuan.test01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lichuan.test01.R;
import com.lichuan.test01.datamodel.GridItem;

import java.util.ArrayList;

/**
 * Created by guoym on 15-6-28.
 */
public class BoxModeGridAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<GridItem> mAllItem;

    public BoxModeGridAdapter(Context c, ArrayList<GridItem> allItem) {

        mContext = c;

        mAllItem = new ArrayList<GridItem>();
        mAllItem.addAll(allItem);
    }

    @Override
    public int getCount() {
        return mAllItem.size();
    }

    @Override
    public Object getItem(int i) {
        return mAllItem.get(i);
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

        TextView mainInfoView = (TextView) contentView.findViewById(R.id.clothDescription);
        assert (mainInfoView != null);

        ImageView imageView = (ImageView) contentView.findViewById(R.id.clothPicture);
        assert (imageView != null);

        TextView otherInfoView = (TextView) contentView.findViewById(R.id.clothType);
        assert (otherInfoView != null);


        GridItem item = mAllItem.get(i);

        mainInfoView.setText(item.getMainInfo());

        item.setOtherInfo(mContext, otherInfoView);

        if (item.isBitmapImage()) {
            imageView.setImageBitmap(item.getBitmap());
        } else {
            imageView.setImageDrawable(mContext.getResources().getDrawable(item.getDrawableResId()));
        }
        return contentView;
    }
}
