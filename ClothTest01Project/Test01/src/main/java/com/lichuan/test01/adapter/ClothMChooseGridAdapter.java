package com.lichuan.test01.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
 * Created by guoym on 15-7-16.
 */
public class ClothMChooseGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Cloth> m_list;

    public ClothMChooseGridAdapter(Context c, ArrayList<Cloth> list) {
        mContext = c;
        m_list = new ArrayList<Cloth>();
        if (list != null) {
            m_list.addAll(list);
        }
    }

    @Override
    public int getCount() {
        return m_list.size();
    }

    @Override
    public Object getItem(int i) {
        return m_list.get(i);
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
            contentView = inflater.inflate(R.layout.cloth_multi_choose_item, null, false);
            assert (contentView != null);
        } else {
            contentView = view;
        }

        TextView selectView = (TextView) contentView.findViewById(R.id.select_flag);
        assert (selectView != null);

        TextView mainInfoView = (TextView) contentView.findViewById(R.id.clothDescription);
        assert (mainInfoView != null);

        ImageView picView = (ImageView) contentView.findViewById(R.id.clothPicture);
        assert (picView != null);

        TextView typeView = (TextView) contentView.findViewById(R.id.clothType);
        assert (typeView != null);

        Cloth cloth = m_list.get(i);

        selectView.setText(mContext.getString(R.string.no_selected));
        if (cloth.isSelected()) {
            selectView.setText(mContext.getString(R.string.is_selected));
        }

        mainInfoView.setText(cloth.getDescription());

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


        return contentView;
    }
}
