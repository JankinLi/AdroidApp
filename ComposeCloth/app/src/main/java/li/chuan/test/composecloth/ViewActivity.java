package li.chuan.test.composecloth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

import li.chuan.test.composecloth.entity.ImageData;
import li.chuan.test.composecloth.ui.ImageLayerView;
import li.chuan.test.composecloth.ui.clothlist.ClothListFragment;
import li.chuan.test.composecloth.ui.clothlist.ClothListViewModel;

public class ViewActivity extends AppCompatActivity {
    private ImageLayerView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Button button = findViewById(R.id.button_refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_refresh_clicked();
            }
        });
        fillImageData();
    }

    private void fillImageData(){
        mImageView = findViewById(R.id.img_view);

        ViewModelProvider.Factory f = this.getDefaultViewModelProviderFactory();
        ClothListViewModel aViewModel = f.create(ClothListViewModel.class);

        LiveData<List<ImageData>> data = aViewModel.getImageInfo(this);
        data.observe(this, new Observer<List<ImageData>>() {
            @Override
            public void onChanged(List<ImageData> data) {
               liveDataChanged(data);
            }
        });
    }

    private void liveDataChanged(List<ImageData> data){
        mImageView.setImageData(data);
    }

    private void button_refresh_clicked(){
        mImageView.refreshImage();
    }
}
