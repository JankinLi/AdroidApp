package li.chuan.test.composecloth.entity;

public class ImageData {
    public static final int KIND_UNKNOWN = 0;
    public static final int KIND_UP = 1;
    public static final int KIND_DOWN = 2;
    public static final int KIND_TOTAL = 3;

    private String mName;
    private String mPath;
    private int mKind;

    public ImageData(String val, String path, int kind){
        mName = val;
        mPath = path;
        mKind = kind;
    }

    public String getDisplayValue(){
        return mName;
    }

    public String getPath(){
        return mPath;
    }

    public int getKind(){
        return mKind;
    }
}
