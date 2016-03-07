package com.penjin.android.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;

import com.penjin.android.constants.PenjinConstants;
import com.penjin.android.domain.PenjinCompany;
import com.penjin.android.domain.PenjinUser;
import com.penjin.android.utils.BitmapUtils;

import java.io.File;

/**
 * Created by maotiancai on 2015/12/19.
 */
public class UserService {

    public static String avatarPath = "";//用户头像缓存地址
    public static String qrPath = "";//用户二维码名片缓存地址


    private PenjinUser user = null;
    private Context context;
    private SharedPreferences sharedPreferences;
    private static UserService instance;

    private UserService(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("PenjinUser", 1);
    }

    public static UserService getInstance(Context context) {
        synchronized (UserService.class) {
            if (instance == null) {
                instance = new UserService(context);
            }
        }
        return instance;
    }

    public File getUserAvatarFile() throws Exception {
        return new File(Environment.getExternalStorageDirectory()
                + File.separator + PenjinConstants.ROOT +
                File.separator + PenjinConstants.USER_DIR +
                File.separator + PenjinConstants.AVATAR_FILE_NAME);
    }

    /**
     * 获取用户头像图片
     *
     * @return
     */
    public Bitmap getUserAvatar() throws Exception {
        String userDir=this.getCurrentUser().getPhone();
        return BitmapUtils.readImageBitmap(new File(Environment.getExternalStorageDirectory()
                + File.separator + PenjinConstants.ROOT +
                File.separator + userDir +
                File.separator + PenjinConstants.AVATAR_FILE_NAME));
    }

    /**
     * 保存用户头像
     *
     * @param
     */
    public void saveUserAvatar(Bitmap avatar) throws Exception {
        BitmapUtils.saveImageBitmap(new File(Environment.getExternalStorageDirectory()
                + File.separator + PenjinConstants.ROOT +
                File.separator + PenjinConstants.USER_DIR +
                File.separator + PenjinConstants.AVATAR_FILE_NAME), avatar);
    }

    /**
     * 获得当前用户信息
     *
     * @return
     */
    public PenjinUser getCurrentUser() {
        String userPhone = sharedPreferences.getString("phone", null);
        if (userPhone == null) {
            return null;
        }
        user = new PenjinUser();
        user.setUsername(sharedPreferences.getString("username", null));
        user.setPassword(sharedPreferences.getString("password", null));
        user.setEmail(sharedPreferences.getString("email", null));
        user.setPhone(sharedPreferences.getString("phone", null));
        user.setAddress(sharedPreferences.getString("address", null));
        user.setRegion(sharedPreferences.getString("region", null));
        user.setChatId(sharedPreferences.getString("chatId", null));
        user.setCompanyId(sharedPreferences.getString("companyId", null));
        user.setCompanyName(sharedPreferences.getString("companyName", null));
        user.setStaffNum(sharedPreferences.getString("staffNum", null));
        user.setSex(sharedPreferences.getString("sex", null));
        user.setQianming(sharedPreferences.getString("qianming", null));
        return user;
    }

    /**
     * 获得当前用户公司信息
     *
     * @return
     */
    public PenjinCompany getCurrentCompany() {
        PenjinCompany penjinCompany = new PenjinCompany();
        penjinCompany.setName(sharedPreferences.getString("realName", null));
        penjinCompany.setDepartment(sharedPreferences.getString("department", null));
        penjinCompany.setPosition(sharedPreferences.getString("position", null));
        return penjinCompany;
    }

    /**
     * 缓存当前用户信息
     *
     * @param user
     */
    public void saveUser(PenjinUser user) {
        this.user = null;
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("userId", user.getUserId());
        editor.putString("username", user.getUsername());
        editor.putString("password", user.getPassword());
        editor.putString("email", user.getEmail());
        editor.putString("phone", user.getPhone());
        editor.putString("address", user.getAddress());
        editor.putString("region", user.getRegion());
        editor.putString("chatId", user.getChatId());
        editor.putString("companyId", user.getCompanyId());
        editor.putString("comanyName", user.getCompanyName());
        editor.putString("staffNum", user.getStaffNum());
        editor.putString("sex", user.getSex());
        editor.putString("qianming", user.getQianming());
        editor.commit();
    }

    public void saveCompany(PenjinCompany company) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("realName", company.getName());
        editor.putString("department", company.getDepartment());
        editor.putString("position", company.getPosition());
        editor.commit();
    }

    public void clearUser() {
        this.user = null;
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString("phone", null);
        editor.commit();
    }
}
