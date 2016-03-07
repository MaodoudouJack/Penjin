package com.penjin.android.domain;

import android.os.Parcel;
import android.os.Parcelable;


import java.io.Serializable;

/**
 * Created by maotiancai on 2015/12/15.
 */
public class PenjinUser implements Parcelable {

    String userId;
    String username; //平台用户的昵称
    String password;//用户注册的手机号，是唯一的用户的ID
    String email;
    String phone;
    String address;
    String region;
    String touxiang;
    String chatId;
    String companyId;
    String companyName;
    String staffNum;//员工工号
    String sex;
    String qianming;

    public String getQianming() {
        return qianming;
    }

    public void setQianming(String qianming) {
        this.qianming = qianming;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStaffNum() {
        return staffNum;
    }

    public void setStaffNum(String staffNum) {
        this.staffNum = staffNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTouxiang(String touxiang) {
        this.touxiang = touxiang;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChatId() {
        return chatId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getTouxiang() {
        return touxiang;
    }

    public String getUsername() {
        return username;
    }

    public PenjinUser() {
        super();
    }

    protected PenjinUser(Parcel in) {
        userId = in.readString();
        username = in.readString();
        password = in.readString();
        email = in.readString();
        phone = in.readString();
        address = in.readString();
        region = in.readString();
        touxiang = in.readString();
        chatId = in.readString();
        companyId = in.readString();
        companyName = in.readString();
        staffNum = in.readString();
    }

    public static final Creator<PenjinUser> CREATOR = new Creator<PenjinUser>() {
        @Override
        public PenjinUser createFromParcel(Parcel in) {
            return new PenjinUser(in);
        }

        @Override
        public PenjinUser[] newArray(int size) {
            return new PenjinUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(region);
        dest.writeString(touxiang);
        dest.writeString(chatId);
        dest.writeString(companyId);
        dest.writeString(companyName);
        dest.writeString(staffNum);
    }

    /**
     * 判断员工是否已经绑定了公司
     *
     * @return
     */
    public boolean isInCompany() {
        return this.staffNum == null ? false : true;
    }
}
