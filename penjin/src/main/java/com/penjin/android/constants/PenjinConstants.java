package com.penjin.android.constants;

import android.os.Environment;

import com.penjin.android.activity.photo.util.Bimp;
import com.penjin.android.activity.photo.util.FileUtils;

import java.io.File;

public class PenjinConstants {


    public static Bimp currentBitmapPath; //用于保存上一次手机截图的图片地址

    public static Bimp currentBitmaps = null;

    //App外部存储根路径
    public static final String ROOT = "penjin";

    public static final String IMAGE_DIR = "PenjinPhoto";

    //用户路径，在登陆成功设置该路径
    public static String USER_DIR;

    //头像截取时，用户缓存手机的临时图片
    public static String CAPTURE_TEMP = "penjin/faceImage.jpg";

    //个人头像文件后缀
    public static String AVATAR_FILE_NAME = "avatar.jpg";


    public static final int REQ_IMAGE = 1433;
    public static final int REQ_IMAGE_CROP = 1435;


    public static final int IMAGE_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int RESULT_REQUEST_CODE = 2;

    public static final int TAKE_PICTURE = 0x000001;
    public static final int PENJIN_CAPTURE_PICTURE = 0x000002;

    public static void createAppDir() {
        File APP_ROOT = new File(Environment.getExternalStorageDirectory() + File.separator + ROOT);
        if (!APP_ROOT.exists()) {
            APP_ROOT.mkdir();
        }
        File IMAGE_ROOT = new File(Environment.getExternalStorageDirectory() + File.separator + IMAGE_DIR);
        if (!IMAGE_ROOT.exists()) {
            IMAGE_ROOT.mkdir();
        }
    }

    public static void createUserDir(String username) {
        USER_DIR = username;
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + ROOT + File.separator + USER_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static boolean isCurrentUserDirInited() {
        if (USER_DIR == null) {
            return false;
        }
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + ROOT + File.separator + USER_DIR);
        if (!file.exists()) {
            return false;
        } else return true;
    }
}
