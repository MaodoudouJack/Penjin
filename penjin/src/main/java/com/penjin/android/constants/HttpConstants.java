package com.penjin.android.constants;

public class HttpConstants {

    /**
     * billSort：
     * 出差申请单据：31
     * 请假申请单据：32
     * 加班申请单据：33
     * 签卡申请单据：34
     * 调休申请单据：35
     * 异地申请单据：36
     * type：
     * 出差类型：46
     * 请假类型：47
     * 加班类型：48
     * 调休类型：49
     */
    public static int chuchaiBillSort = 31;
    public static int qingjiaBillSort = 32;
    public static int jiabanBillSort = 33;
    public static int qiankaBillSort = 34;
    public static int tiaoxiuBillSort = 35;
    public static int yidiBillSort = 36;

    public static int chuchaiType = 46;
    public static int qingjiaType = 47;
    public static int jiabanType = 48;
    public static int tiaoxiuType = 49;


    public static String HOST = "http://211.149.199.42:88/App/";

    /**
     * companyId：公司ID
     * billSort:单据类别的编号（某一类单据。例：属于出差申请一类的单据）
     * type:申请类型
     * sn：访问数据识别码
     */
    public static String BillProperty = "BillProperty";

    public static String GetStaff = "GetStaff";

    public static String ChuchaiApply = "ChuChai";

    /**
     * 请假申请接口
     * staffNumber
     * companyId
     * billType
     * qingjiaType
     * startDate
     * endDate
     * remark
     * sn
     */
    public static String QingjiaApply = "QingJia";

    /**
     * 加班申请接口
     * staffNumber
     * companyId
     * billType
     * jiaBanType
     * startDate
     * endDate
     * remark
     * sn
     */
    public static String JiabanApply = "JiaBan";

    /**
     * 调休申请接口
     * staffNumber
     * companyId
     * billType
     * tiaoXiuType
     * startDate
     * endDate
     * remark
     * sn
     */
    public static String TiaoxiuApply = "TiaoXiu";

    /**
     * staffNumber
     * companyId
     * billType
     * date
     * remark
     * sn
     */
    public static String BuqianApply = "BuQian";

    /**
     * userName:用户登录账号。
     * photo：个人头像
     * nickname：个人昵称
     * address：个人详细地址
     * strict：地区
     * gender：性别
     * personal：个性签名
     * sn：访问数据识别码
     */
    public static String PesonInfoSet = "PersonalInfoSet";

    /**
     * staffNumber:员工工号。
     * companyId：公司ID
     * sn：访问数据识别码
     */
    public static String ApplicationDetail = "ApplicationDetail";

    public static String KaoQinInfo = "KaoQinInfo";

    public static String LoginInterface="LoginInterface";

    /**
     * staffNumber:员工工号。
     * companyId：公司ID
     * billSort：单据类型的编号（具体单据的编号.例：出差申请单：3101）
     * date：日期（读取考勤申请时，需要）
     * sn：访问数据识别码
     */
    public static String SpecificDetail = "SpecificDetail";
}
