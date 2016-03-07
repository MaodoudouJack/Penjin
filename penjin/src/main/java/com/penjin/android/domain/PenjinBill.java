package com.penjin.android.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 盆景有关的申请单
 * Created by maotiancai on 2016/1/20.
 */
public class PenjinBill {

    public String billNumber;

    public int billType;
    public int billSubType;
    public String typeName;
    public String subTypeName;
    public String reason;
    public String startTime;//订单开始时间
    public String endTime;//订单结束时间
    public String billTime;//提交订单时间

    public List<PenjinFlowNode> flowNodes = new ArrayList<>();//审批流节点
    public List<String> imageList = new ArrayList<>();//附件图片
}
