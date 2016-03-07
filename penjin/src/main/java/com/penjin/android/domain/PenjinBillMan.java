package com.penjin.android.domain;

/**
 * Created by maotiancai on 2016/1/21.
 */
public class PenjinBillMan {
    public String name;
    public String phone;

    public PenjinBillMan() {
    }

    public PenjinBillMan(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public PenjinBillMan(PenjinBillMan man) {
        this.name = man.name;
        this.phone = man.phone;
    }
}
