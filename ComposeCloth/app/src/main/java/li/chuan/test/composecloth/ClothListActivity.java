package li.chuan.test.composecloth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import li.chuan.test.composecloth.ui.clothlist.ClothListFragment;

public class ClothListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloth_list_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ClothListFragment.newInstance())
                    .commitNow();
        }
    }
}
