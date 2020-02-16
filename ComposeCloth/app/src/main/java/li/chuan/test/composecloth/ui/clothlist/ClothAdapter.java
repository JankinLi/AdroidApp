package li.chuan.test.composecloth.ui.clothlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import li.chuan.test.composecloth.R;
import li.chuan.test.composecloth.entity.ImageData;

public class ClothAdapter extends RecyclerView.Adapter<ClothAdapter.MyClothHolder> {

    private final LayoutInflater mLayoutInflater;
    private List<ImageData> mData;

    //定义接口 OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    private ClothAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(ClothAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public ClothAdapter(Context context, List<ImageData> data) {
        mLayoutInflater = LayoutInflater.from(context);
        mData = data;
    }

    @Override
    public ClothAdapter.MyClothHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ClothAdapter.MyClothHolder(mLayoutInflater.inflate(R.layout.cloth_message_item , parent, false));
    }

    @Override
    public void onBindViewHolder(final ClothAdapter.MyClothHolder holder, int pos) {
        ImageData data = mData.get(pos);
        String display_name  = data.getDisplayValue();
        if (display_name != null) {
            holder.mTextView.setText(display_name);
        }

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

    class MyClothHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        MyClothHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.cloth_txt);
        }
    }
}
