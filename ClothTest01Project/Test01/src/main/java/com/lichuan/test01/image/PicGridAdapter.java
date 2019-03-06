package com.lichuan.test01.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lichuan.test01.utility.LoadImageHelper;
import com.lichuan.test01.utility.TimeHelper;

import java.util.ArrayList;

/**
 * Created by guoym on 15-6-18.
 */
public class PicGridAdapter extends BaseAdapter {
    private static final String TAG = "PicGridAdapter";
    private ArrayList<ImageData> mAllPic;
    private Context mContext;

    private int mContentViewId;
    private int mDescriptionId;
    private int mPictureId;

    //private LoadImageHelper mImageWorker;

    private int mDefaultImageId;
    private int mBadImageId;

    public PicGridAdapter(Context c, ArrayList<ImageData> list, int contentViewId, int descriptionTextId, int pictureId, LoadImageHelper worker, int defaultImageId, int badImageId) {
        mAllPic = new ArrayList<ImageData>();
        if (list != null && list.size() > 0) {
            mAllPic.addAll(list);
        }

        mContext = c;
        mContentViewId = contentViewId;
        mDescriptionId = descriptionTextId;
        mPictureId = pictureId;

        //mImageWorker = worker;

        mDefaultImageId = defaultImageId;

        mBadImageId = badImageId;
    }


    @Override
    public int getCount() {
        return mAllPic.size();
    }

    @Override
    public Object getItem(int i) {
        return mAllPic.get(i);
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
            contentView = inflater.inflate(mContentViewId, null, false);
            assert (contentView != null);
        } else {
            contentView = view;
        }

        TextView descriptionView = (TextView) contentView.findViewById(mDescriptionId);
        assert (descriptionView != null);
        ImageView picView = (ImageView) contentView.findViewById(mPictureId);
        assert (picView != null);

        //int dw = picView.getDrawable().getBounds().width();
        //int dh = picView.getDrawable().getBounds().height();

        ImageData data = mAllPic.get(i);
        long modifiedDateOfPic = data.getModifiedDate();

        String imageTimeStr;
        imageTimeStr = TimeHelper.ComputeTimeDisplayStringByModifiedDate(mContext, modifiedDateOfPic);
        descriptionView.setText(imageTimeStr);
        //descriptionView.setText(data.getTitle());

        Bitmap bitImg = data.getPicture();
        if (bitImg != null) {
            picView.setImageBitmap(bitImg);
        } else {
            if (data.isImgBad()) {
                picView.setImageDrawable(mContext.getResources().getDrawable(mBadImageId));
            } else {
                picView.setImageDrawable(mContext.getResources().getDrawable(mDefaultImageId));
            }
        }
        return contentView;
    }
}


//if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
//                MaxHeight = picView.getMeasuredHeight();
//                MaxWidth = picView.getMeasuredWidth();
//}