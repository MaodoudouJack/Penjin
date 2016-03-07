package com.penjin.android.domain;

/**
 * 用户所在公司的信息
 * Created by maotiancai on 2015/12/21.
 */
public class PenjinCompany {

    String name;//员工真实姓名
    String department;
    String position;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
