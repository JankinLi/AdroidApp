package com.lichuan.test01.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lichuan.test01.R;
import com.lichuan.test01.datamodel.BoxImageType;

/**
 * Created by guoym on 15-6-28.
 */
public class BoxImageGridAdapter extends BaseAdapter {
    private final Context mContext;

    public BoxImageGridAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return BoxImageType.getImageLibCount();
    }

    @Override
    public Object getItem(int i) {
        return BoxImageType.getImageTypeByIndex(i);
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
            contentView = inflater.inflate(R.layout.box_image_item, null, false);
            assert (contentView != null);
        } else {
            contentView = view;
        }

        ImageView image = (ImageView) contentView.findViewById(R.id.box_image);
        assert (image != null);

        TextView desc = (TextView) contentView.findViewById(R.id.box_image_description);
        assert (desc != null);

        //int imageType = BoxImageType.getImageTypeByIndex(i);
        //int rId = BoxImageType.computeResourceIdByImageType(imageType);
        image.setImageDrawable(mContext.getResources().getDrawable(BoxImageType.computeResourceIdByIndex(i)));

        desc.setText(mContext.getResources().getString(R.string.box_image_desc));
        return contentView;
    }
}
