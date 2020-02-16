package li.chuan.test.composecloth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button next = findViewById(R.id.buttonNext);
        mNextButton = next;
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonNextClicked();
            }
        });

        boolean ret = checkPermissionREAD_EXTERNAL_STORAGE(this);
        if(ret){
            Log.d(TAG, "Permission is pass.");
            enableNextButton();
        }
        else{
            Log.e(TAG, "No Permission");
            disableNextButton();
        }
    }

    private void buttonNextClicked(){
        Log.d(MainActivity.TAG, "buttonNextClicked");
        startActivity(new Intent(this, ListActivity.class));
        this.finish();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Activity act) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(act,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(act,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog(act, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions(act,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void showDialog(final Context context,
                           final String permission) {
        final String title = getResources().getString(R.string.permission_title);
        final String content = getResources().getString(R.string.permission_content);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(content);
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"user granted");
                    enableNextButton();
                } else {
                    Log.w(TAG,"user denied.");
                    final String msg = getResources().getString(R.string.toast_tip_1);
                    Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
                    disableNextButton();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    private void disableNextButton(){
        if (mNextButton == null){
            return;
        }

        mNextButton.setEnabled(false);
    }

    private void enableNextButton(){
        if (mNextButton == null){
            return;
        }

        mNextButton.setEnabled(true);
    }
}
