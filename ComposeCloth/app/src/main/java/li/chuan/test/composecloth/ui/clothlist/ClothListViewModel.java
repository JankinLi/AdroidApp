package li.chuan.test.composecloth.ui.clothlist;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import li.chuan.test.composecloth.db.ImageDataQuery;
import li.chuan.test.composecloth.entity.ImageData;

public class ClothListViewModel extends ViewModel {
    private final static String TAG = "ClothListViewModel";

    private MutableLiveData<List<ImageData>> mData;

    public LiveData<List<ImageData>> getImageInfo(Context c) {
        if (mData == null) {
            mData = new MutableLiveData<List<ImageData>>();
            loadImageData(c);
        }
        return mData;
    }

    private void loadImageData(Context c){
        ImageDataQuery query = new ImageDataQuery(c);
        List<ImageData> result = query.queryAll();
        mData.postValue(result);
    }
}
