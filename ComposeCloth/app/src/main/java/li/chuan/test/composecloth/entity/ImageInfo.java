package li.chuan.test.composecloth.entity;

public class ImageInfo {
    private String mName;
    private String mPath;
    private int mSize;
    private String mParentPath;

    public ImageInfo(String val, String path, int size){
        mName = val;
        mPath = path;
        mSize = size;
    }

    public void setParentPath(String parentPath){
        mParentPath = parentPath;
    }

    public String getDisplayValue(){
        return mName;
    }

    public String getPath(){
        return mPath;
    }
}
