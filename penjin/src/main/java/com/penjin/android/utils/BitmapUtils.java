package com.penjin.android.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.spec.EncodedKeySpec;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.penjin.android.constants.PenjinConstants;

public class BitmapUtils {

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static final Bitmap create2DCoderBitmap(String url, int QR_WIDTH, int QR_HEIGHT) {
        if (url == null || url.equals("")) {
            return null;
        }
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_WIDTH, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            // 下面这里按照二维码的算法，逐个生成二维码的图片
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
            return null;
        }
    }

    public static void saveImageBitmap(File output, Bitmap bitmap) throws Exception {
        if (output.exists()) {
            output.delete();
        }
        output.createNewFile();
        System.out.println(output.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(output);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    }

    public static Bitmap readImageBitmap(File input) throws Exception {
        if (!input.exists())
            throw new FileNotFoundException();
        else {
            Bitmap imageBitmap = BitmapFactory.decodeFile(input.getAbsolutePath());
            return imageBitmap;
        }
    }

    public static void copyImage2Data(String fileRoot, String fileName, Integer PicID, Context mContext) {
        Log.d("BitMap TOOL", "mythou copyImage2Data----->Enter PicID=" + PicID);

        try {
            //计算图片存放全路径
            String LogoFilePath = fileRoot + fileName;
            File dir = new File(fileRoot);
            //如果文件夹不存在，创建一个（只能在应用包下面的目录，其他目录需要申请权限 OWL）
            if (!dir.exists()) {
                Log.d("BitMap TOOL", "mythou copyImage2Data----->dir not exist");
            }

            boolean result = dir.mkdirs();
            Log.d("BitMap TOOL", "dir.mkdirs()----->result = " + result);

            // 获得封装  文件的InputStream对象
            InputStream is = mContext.getResources().openRawResource(PicID);

            Log.d("BitMap TOOL", "copyImage2Data----->InputStream open");

            FileOutputStream fos = new FileOutputStream(LogoFilePath);

            byte[] buffer = new byte[8192];
            System.out.println("3");
            int count = 0;

            // 开始复制Logo图片文件
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
                System.out.println("4");
            }
            fos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据原图尺寸和目标区域的尺寸计算出合适的Bitmap尺寸
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原始图片的宽高
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 根据计算出的inSampleSize生成Bitmap
     *
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(String path,
                                                         int reqWidth, int reqHeight) {

        // 首先设置 inJustDecodeBounds=true 来获取图片尺寸
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // 计算 inSampleSize 的值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 根据计算出的 inSampleSize 来解码图片生成Bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
}
