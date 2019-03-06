package com.lichuan.test01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lichuan.test01.R;
import com.lichuan.test01.datamodel.Box;

import java.util.ArrayList;

/**
 * Created by guoym on 15-7-2.
 */
public class BoxGridAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<Box> mAllBox;

    public BoxGridAdapter(Context c, ArrayList<Box> allBox) {
        mAllBox = new ArrayList<Box>();
        mAllBox.addAll(allBox);

        Box dumbBox = new Box(Box.dumbBoxString, Box.dumbBoxString);

        mAllBox.add(dumbBox);

        mContext = c;
    }

    @Override
    public int getCount() {
        return mAllBox.size();
    }

    @Override
    public Object getItem(int i) {
        return mAllBox.get(i);
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


        Box box = mAllBox.get(i);

        if (box.getName().equals(Box.dumbBoxString)) {
            String message = mContext.getResources().getString(R.string.invalid_box_name);
            mainInfoView.setText(message);

            message = mContext.getResources().getString(R.string.invalid_box_desc);
            otherInfoView.setText(message);

            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.invalid));
            return contentView;
        }

        mainInfoView.setText(box.getDescription());

        box.setOtherInfo(mContext, otherInfoView);

        if (box.isBitmapImage()) {
            imageView.setImageBitmap(box.getBitmap());
        } else {
            imageView.setImageDrawable(mContext.getResources().getDrawable(box.getDrawableResId()));
        }
        return contentView;
    }
}
