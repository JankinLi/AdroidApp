package li.chuan.test.composecloth.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import li.chuan.test.composecloth.R;
import li.chuan.test.composecloth.entity.ImageData;

public class ImageLayerView extends View {
    private static final String TAG = "ImageLayerView";

    private Paint mPaint = new Paint();

    private List<ImageData> mData;
    private Map<Integer , ArrayList<ImageData> > mClothMap;

    private int mViewWidth;
    private int mViewHeight;

    private int mCurrentDownPartIndex;
    private int mCurrentUpPartIndex;
    private int mCurrentTotalPartIndex;

    private Rect mDownPartRect;
    private Rect mUpPartRect;
    private Rect mTotalPartRect;

    private Bitmap mDownPartBitmap;
    private Bitmap mUpPartBitmap;
    private Bitmap mTotalPartBitmap;

    private Rect mDownPartBitmapRect;
    private Rect mUpPartBitmapRect;
    private Rect mTotalBitmapRect;

    public ImageLayerView(Context context) {
        super(context);
        init();
    }

    public ImageLayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init();
    }

    public ImageLayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ImageLayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.image_layer);
        String text = ta.getString(R.styleable.image_layer_text);
        int kind = ta.getInteger(R.styleable.image_layer_kind, -1);
        Log.d(TAG, "text = " + text + " , kind = " + kind);
        ta.recycle();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(){
        mClothMap = new HashMap<>();

        mPaint.setColor(Color.BLUE);

        // set touch listener
        this.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return viewTouchOccurred(v, event);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        //获取绘制的View的宽度
        int width = getWidth() - paddingLeft - paddingRight;
        //获取绘制的View的高度
        int height = getHeight() - paddingTop - paddingBottom;

        Log.d(TAG, "getWidth()=" + getWidth() + ",width=" + width);
        Log.d(TAG, "getHeight()=" + getHeight() + ",height=" + height);

        //绘制View，左上角坐标（0+paddingLeft,0+paddingTop），右下角坐标（width+paddingLeft,height+paddingTop）
        //canvas.drawRect(0 + paddingLeft, 0 + paddingTop, width + paddingLeft, height + paddingTop, mPaint);

        int half_height = mViewHeight / 2 ;

        if (mDownPartBitmap!= null && mDownPartBitmapRect != null) {
            int down_part_left_x = 0 + paddingLeft;
            int down_part_top_y = half_height + paddingTop;
            int down_part_right_x = width + paddingLeft;
            int down_part_right_y = mViewHeight + paddingTop;
            mDownPartRect = computePartRect(down_part_left_x, down_part_top_y, down_part_right_x, down_part_right_y, mDownPartBitmap.getWidth() , mDownPartBitmap.getHeight());
            if (mDownPartRect != null) {
                canvas.drawBitmap(mDownPartBitmap, mDownPartBitmapRect, mDownPartRect, mPaint);
            }
        }

        if (mUpPartBitmap != null && mUpPartBitmapRect != null ) {
            int up_part_left_x = 0 + paddingLeft;
            int up_part_top_y = 0 + paddingTop;
            int up_part_right_x = width + paddingLeft;
            int up_part_right_y = half_height + paddingTop;

            mUpPartRect = computePartRect(up_part_left_x, up_part_top_y, up_part_right_x, up_part_right_y, mUpPartBitmap.getWidth(), mUpPartBitmap.getHeight());
            if( mUpPartRect!= null) {
                canvas.drawBitmap(mUpPartBitmap, mUpPartBitmapRect, mUpPartRect, mPaint);
            }
        }
    }

    private Rect computePartRect(int part_left_x, int part_top_y, int part_right_x, int part_right_y, int bitmap_width, int bitmap_height){
        Log.d(TAG, "computePartRect bitmap_height=" + bitmap_height + ",bitmap_width=" + bitmap_width);

        if (bitmap_height == 0){
            Log.e(TAG, "bitmap_height is 0");
            return null;
        }
        if (bitmap_width == 0){
            Log.e(TAG, "bitmap_width is 0");
            return null;
        }

        if (bitmap_height > bitmap_width ) {
            float rate = ((float) bitmap_height / (float) bitmap_width);
            if (rate == 0.0) {
                Log.e(TAG, "computePartRect: rate is 0.0. bitmap_height=" + bitmap_height + ",bitmap_width=" + bitmap_width);
                return null;
            }

            int height = part_right_y - part_top_y;
            int width = (int) (height / rate);

            int center_x = part_left_x + (int) ((part_right_x - part_left_x) / 2);
            int left_x = center_x - (width / 2);
            int right_x = center_x + (width / 2);
            Log.d(TAG, "center_x=" + center_x + ",width=" + width + ",rate=" + rate);
            return new Rect(left_x, part_top_y, right_x, part_right_y);
        }
        if (bitmap_height < bitmap_width ) {
            float rate = ((float) bitmap_width / (float) bitmap_height);
            if (rate == 0.0) {
                Log.e(TAG, "computePartRect: rate is 0.0. bitmap_height=" + bitmap_height + ",bitmap_width=" + bitmap_width);
                return null;
            }

            int width = part_right_x - part_left_x;
            int height = (int) (width / rate);

            int center_y = part_top_y + (int) ((part_right_y - part_top_y) / 2);
            int top_x = center_y - (height / 2);
            int bottom_x = center_y + (height / 2);
            return new Rect(part_left_x, top_x, part_right_x, bottom_x);
        }

        int center_x = part_left_x + (int) ((part_right_x - part_left_x) / 2);
        int center_y = part_top_y + (int) ((part_right_y - part_top_y) / 2);
        int left_x = center_x - (bitmap_width / 2);
        int right_x = center_x + (bitmap_width / 2);
        int top_x = center_y - (bitmap_height / 2);
        int bottom_x = center_y + (bitmap_height / 2);
        return new Rect(left_x, top_x, right_x, bottom_x);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        Log.d(TAG, "widthSize=" + widthSize + ",heightSize=" + heightSize);

        mViewWidth = widthSize;
        mViewHeight = heightSize;
    }

    private boolean viewTouchOccurred(View v, MotionEvent event){
        //Log.d(TAG, "onTouch");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "onTouch Down");
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "onTouch Up");
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.d(TAG, "onTouch Move");
        }
        return true;
    }

    public void setImageData(List<ImageData> data){
        Log.d(TAG, "setImageData data.size()=" + data.size());
        mData = data;

        fillImageData();
    }

    private void fillImageData(){
        Log.d(TAG, "fillImageData");
        for (ImageData data:mData){
            int value = data.getKind();
            if ( mClothMap.containsKey(value) ){
                ArrayList<ImageData> lst = mClothMap.get(value);
                lst.add(data);
            }
            else{
                ArrayList<ImageData> lst = new ArrayList<>();
                lst.add(data);
                mClothMap.put(value, lst);
            }
        }
        int up_count = computeSizeOfSpecifyImageData(ImageData.KIND_UP);
        Log.d(TAG, "up_count=" + up_count);
        mCurrentUpPartIndex = 0;

        int down_count = computeSizeOfSpecifyImageData(ImageData.KIND_DOWN);
        Log.d(TAG, "down_count=" + down_count);
        mCurrentDownPartIndex = 0;

        int total_count = computeSizeOfSpecifyImageData(ImageData.KIND_TOTAL);
        mCurrentTotalPartIndex = -1;
        Log.d(TAG, "total_count=" + total_count);

        computeDownPartBitmap();
        computeUpPartBitmap();
        computeTotalPartBitmap();
    }

    private void computeDownPartBitmap(){
        int down_count = computeSizeOfSpecifyImageData(ImageData.KIND_DOWN);
        if (down_count<= 0){
            Log.e(TAG, "down_count<= 0");
            return;
        }

        Random r = new Random();
        int index = r.nextInt(down_count);
        if (index>= down_count){
            index = down_count -1;
        }

        ArrayList<ImageData> kind_list = mClothMap.get(ImageData.KIND_DOWN);
        ImageData imgData = kind_list.get(index);
        if (imgData == null){
            return;
        }
        String path = imgData.getPath();

        try {
            FileInputStream in = new FileInputStream(path);
            mDownPartBitmap = BitmapFactory.decodeStream(in);
            int bitmap_width = mDownPartBitmap.getWidth();
            int bitmap_height = mDownPartBitmap.getHeight();
            Log.d(TAG, "computeDownPartBitmap bitmap_width=" + bitmap_width + ",bitmap_height=" + bitmap_height);
            mDownPartBitmapRect = new Rect(0,0,bitmap_width,bitmap_height);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "e=" + e.toString());
            mDownPartBitmap = null;
        }
    }

    private void computeUpPartBitmap(){
        int up_count = computeSizeOfSpecifyImageData(ImageData.KIND_UP);
        if (up_count <= 0){
            Log.e(TAG, "up_count<= 0");
            return;
        }

        Random r = new Random();
        int index = r.nextInt(up_count);
        if (index>= up_count){
            index = up_count -1;
        }

        ArrayList<ImageData> kind_list = mClothMap.get(ImageData.KIND_UP);
        ImageData imgData = kind_list.get(index);
        if (imgData == null){
            return;
        }

        String path = imgData.getPath();

        try {
            FileInputStream in = new FileInputStream(path);
            mUpPartBitmap = BitmapFactory.decodeStream(in);
            mUpPartBitmapRect = new Rect(0,0,mUpPartBitmap.getWidth(),mUpPartBitmap.getHeight());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "e=" + e.toString());
            mUpPartBitmap = null;
        }
    }

    private void computeTotalPartBitmap(){
        int total_count = computeSizeOfSpecifyImageData(ImageData.KIND_TOTAL);
        if (total_count <= 0){
            Log.e(TAG, "total_count <= 0");
            return;
        }

        Random r = new Random();
        int index = r.nextInt(total_count);
        if (index>= total_count){
            index = total_count -1;
        }

        ArrayList<ImageData> kind_list = mClothMap.get(ImageData.KIND_TOTAL);
        ImageData imgData = kind_list.get(index);
        if (imgData == null){
            Log.e(TAG, "imgData is null.");
            return;
        }

        String path = imgData.getPath();

        try {
            FileInputStream in = new FileInputStream(path);
            mTotalPartBitmap = BitmapFactory.decodeStream(in);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "e=" + e.toString());
            mTotalPartBitmap = null;
        }
    }

    private int computeSizeOfSpecifyImageData(int kindValue){
        ArrayList<ImageData> kind_list = mClothMap.get(kindValue);
        return kind_list!=null? kind_list.size():0;
    }

    public void refreshImage(){
        computeDownPartBitmap();
        computeUpPartBitmap();
        computeTotalPartBitmap();

        invalidate();
    }
}
