package com.lichuan.test01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lichuan.test01.R;
import com.lichuan.test01.datamodel.Suit;

import java.util.ArrayList;

/**
 * Created by guoym on 15-6-29.
 */
public class SuitModeGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Suit> mSuit;

    public SuitModeGridAdapter(Context c, ArrayList<Suit> allSuit) {
        mSuit = new ArrayList<Suit>();
        if (allSuit != null) {
            mSuit.addAll(allSuit);
        }

        mContext = c;
    }

    @Override
    public int getCount() {
        return mSuit.size();
    }

    @Override
    public Object getItem(int i) {
        return mSuit.get(i);
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


        Suit suit = mSuit.get(i);

        mainInfoView.setText(suit.getMainInfo());

        suit.setOtherInfo(mContext, otherInfoView);

        if (suit.isBitmapImage()) {
            imageView.setImageBitmap(suit.getBitmap());
        } else {
            imageView.setImageDrawable(mContext.getResources().getDrawable(suit.getDrawableResId()));
        }
        return contentView;
    }
}
