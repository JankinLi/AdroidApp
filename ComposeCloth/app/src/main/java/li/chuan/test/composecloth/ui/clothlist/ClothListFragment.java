package li.chuan.test.composecloth.ui.clothlist;

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

import li.chuan.test.composecloth.ClothFragment;
import li.chuan.test.composecloth.R;
import li.chuan.test.composecloth.ViewActivity;
import li.chuan.test.composecloth.entity.ImageData;

public class ClothListFragment extends Fragment {
    private static final String TAG = "ClothFragment";

    private ClothListViewModel mViewModel;

    private RecyclerView mClothList;
    private TextView mClothLoading;

    public static ClothListFragment newInstance() {
        return new ClothListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.cloth_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "onActivityCreated");

        mClothList = getActivity().findViewById(R.id.cloth_list);
        // 设置布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mClothList.setLayoutManager(linearLayoutManager);

        //设置Item增加、移除动画
        mClothList.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        mClothList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        mClothLoading = getActivity().findViewById(R.id.cloth_message);

        ViewModelProvider.Factory f = this.getDefaultViewModelProviderFactory();
        mViewModel = f.create(ClothListViewModel.class);

        Button viewButton = getActivity().findViewById(R.id.cloth_display);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_view_clicked();
            }
        });

        hideWhenLoading();

        LiveData<List<ImageData>> data = mViewModel.getImageInfo(this.getActivity());
        data.observe(getViewLifecycleOwner(), new Observer<List<ImageData>>() {
            @Override
            public void onChanged(List<ImageData> data) {
                ClothListFragment.this.setList(data);
                ClothListFragment.this.displayListAfterLoad();
            }
        });
    }

    private void hideWhenLoading(){
        mClothList.setVisibility(View.GONE);
    }

    private void displayListAfterLoad(){
        mClothList.setVisibility(View.VISIBLE);
        mClothLoading.setVisibility(View.GONE);
    }

    private void setList(List<ImageData> data){
        ClothAdapter adapter = new ClothAdapter(getActivity(), data);
        mClothList.setAdapter(adapter);

        adapter.setOnItemClickListener(new ClothAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick="+ position );
                clothListItemClicked(view, position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.d(TAG, "onItemLongClick="+ position );
            }
        });
    }

    private void clothListItemClicked(View view, int position){
        LiveData<List<ImageData>> all_data = mViewModel.getImageInfo(getActivity());
        if (all_data != null) {
            List<ImageData> data_list = all_data.getValue();
            if (data_list != null) {
                if (position < data_list.size()) {
                    ImageData data = data_list.get(position);
                    Log.d(TAG, "onItemClick data.getPath()="+ data.getPath() );
                    imageDisplay(data);
                }
                else{
                    Log.e(TAG, "onItemClick : position is wrong. data_list.size()=" + data_list.size());
                }
            }
            else{
                Log.e(TAG, "onItemClick : data_list is null.");
            }
        }
        else{
            Log.e(TAG, "onItemClick : all_data is null.");
        }
    }

    private void imageDisplay(ImageData data){
        FragmentActivity act = getActivity();
        ClothFragment fragment = ClothFragment.newInstance(data.getDisplayValue(), data.getPath(), data.getKind());
        FragmentTransaction transaction = act.getSupportFragmentManager().beginTransaction();
        transaction
                .addToBackStack(null)  //将当前fragment加入到返回栈中
                .replace(R.id.container,fragment)
                .show(fragment)
                .commit();
    }

    private void button_view_clicked(){
        startActivity(new Intent(getActivity(), ViewActivity.class));
        getActivity().finish();
    }
}
