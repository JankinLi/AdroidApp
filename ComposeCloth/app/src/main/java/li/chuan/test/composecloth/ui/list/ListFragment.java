package li.chuan.test.composecloth.ui.list;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import li.chuan.test.composecloth.ClothListActivity;
import li.chuan.test.composecloth.R;
import li.chuan.test.composecloth.entity.ImageInfo;
import li.chuan.test.composecloth.ImageDisplayFragment;

public class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";

    private ListViewModel mViewModel;
    private RecyclerView mRvList;
    private TextView mLoading;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated");

        Button nextButton = getActivity().findViewById(R.id.list_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonListNextClicked();
            }
        });

        mRvList = getActivity().findViewById(R.id.rv_list);
        // 设置布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRvList.setLayoutManager(linearLayoutManager);

        //设置Item增加、移除动画
        mRvList.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mRvList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        //DividerItemDecoration decoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        //decoration.setDrawable(getResources().getDrawable(R.drawable.divider,null));
        //mRvList.addItemDecoration(decoration);

        mLoading = getActivity().findViewById(R.id.loading_tip);

        ViewModelProvider.Factory f = this.getDefaultViewModelProviderFactory();
        mViewModel =  f.create(ListViewModel.class);
        LiveData<List<ImageInfo>> data = mViewModel.getImageInfo(this.getActivity());
        data.observe(getViewLifecycleOwner(), new Observer<List<ImageInfo>>() {
            @Override
            public void onChanged(List<ImageInfo> imageInfo) {
                ListFragment.this.setList(imageInfo);
                ListFragment.this.displayListAfterLoad();
            }
        });

        List<ImageInfo> all_data = data.getValue();
        if (all_data != null) {
            for (ImageInfo info : all_data) {
                Log.d(TAG, "value =" + info.getDisplayValue() + ",path=" + info.getPath());
            }
        }
        else{
            hideWhenLoading();
        }
    }

    private void setList(List<ImageInfo> allData){
        MyRVAdapter adapter = new MyRVAdapter(getActivity(), allData);
        mRvList.setAdapter(adapter);
        adapter.setOnItemClickListener(new MyRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick="+ position );
                LiveData<List<ImageInfo>> data = mViewModel.getImageInfo(ListFragment.this.getActivity());
                if (data != null) {
                    List<ImageInfo> all_data = data.getValue();
                    if (all_data != null) {
                        if (position < all_data.size()) {
                            ImageInfo info = all_data.get(position);
                            Log.d(TAG, "onItemClick info.getPath()="+ info.getPath() );
                            popImageDisplay(info);
                        }
                        else{
                            Log.e(TAG, "onItemClick : position is wrong. all_data.size()=" + all_data.size());
                        }
                    }
                    else{
                        Log.e(TAG, "onItemClick : all_data is null.");
                    }
                }
                else{
                    Log.e(TAG, "onItemClick : data is null.");
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, "onItemLongClick="+ position );
            }
        });
    }

    private void hideWhenLoading(){
        mRvList.setVisibility(View.GONE);
    }

    private void displayListAfterLoad(){
        mRvList.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
    }

    private void popImageDisplay(ImageInfo info){
        FragmentActivity act = getActivity();
        ImageDisplayFragment fragment = ImageDisplayFragment.newInstance(info.getDisplayValue(), info.getPath());
        FragmentTransaction transaction = act.getSupportFragmentManager().beginTransaction();
        transaction
                .addToBackStack(null)  //将当前fragment加入到返回栈中
                .replace(R.id.container,fragment)
                .show(fragment)
                .commit();
    }

    private void buttonListNextClicked(){
        Log.d(TAG, "buttonListNextClicked");
        startActivity(new Intent(getActivity(), ClothListActivity.class));
        getActivity().finish();
    }
}
