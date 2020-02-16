package li.chuan.test.composecloth.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import li.chuan.test.composecloth.R;
import li.chuan.test.composecloth.entity.ImageInfo;

public class MyRVAdapter extends RecyclerView.Adapter<MyRVAdapter.MyTVHolder> {

    private final LayoutInflater mLayoutInflater;
    private final List<ImageInfo> mData;

    //定义接口 OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public MyRVAdapter(Context context, List<ImageInfo> data) {
        mLayoutInflater = LayoutInflater.from(context);
        mData = data;
    }

    @Override
    public MyRVAdapter.MyTVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyRVAdapter.MyTVHolder(mLayoutInflater.inflate(R.layout.rv_txt_item , parent, false));
    }

    @Override
    public void onBindViewHolder(final MyRVAdapter.MyTVHolder holder, int pos) {
        ImageInfo info = mData.get(pos);
        holder.mTextView.setText(info.getDisplayValue());

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class MyTVHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        MyTVHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_txt);
        }
    }
}
