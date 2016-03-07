package com.penjin.android.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 审批流节点
 * Created by maotiancai on 2016/1/21.
 */
public class PenjinFlowNode {

    public String department;
    public String nodeName;
    public List<PenjinBillMan> billManList;

    public PenjinFlowNode() {
    }

    public PenjinFlowNode(PenjinFlowNode node) {
        this.department = node.department;
        this.nodeName = node.nodeName;
        this.billManList = new ArrayList<>();
        for (int i = 0; i < node.billManList.size(); i++) {
            PenjinBillMan billMan = new PenjinBillMan(node.billManList.get(i));
            this.billManList.add(billMan);
        }
    }
}
