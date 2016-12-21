import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by white on 2016-12-19.
 */
public class UploadHandler {
    private static UploadHandler mUploadHandler;
    private static Cloudinary cloudinary;
    private static Thread t;
    private static Runnable r;

    private UploadHandler(){
        cloudinary=new Cloudinary(ObjectUtils.asMap(
                "cloud_name", Constant.CLOUDINARY_CLOUDNAME,
                "api_key", Constant.CLOUDINARY_APIKEY,
                "api_secret", Constant.CLOUDINARY_SECRETKEY));
    }

    public static UploadHandler getInstance(){
        if(mUploadHandler==null)
            mUploadHandler=new UploadHandler();
        return mUploadHandler;
    }

    public void asyncSingleUpload(final File file , final ResponseHandler responseHandler){
        r=new Runnable() {
            public void run() {
                Map map=upload(file);
                responseHandler.onUploadResponse(file.getName(),(String)map.get("url"));
            }
        };
        t=new Thread(r);
        t.start();

    }

    public Map upload(File file){
        Map uploadResult=null;
        try {
            uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uploadResult;
    }

    public void asyncBatchUpload(final File dir,final ResponseHandler responseHandler) throws Exception {
        if(dir.isDirectory()) {
            for (File f:dir.listFiles()){
                    if(f.exists() && !f.isDirectory() && f.canRead()){
                        asyncSingleUpload(f,responseHandler);
                    }
            }
        }else {
            throw new Exception("Direcotry?Directory NAMANA!");
        }
    }
}
