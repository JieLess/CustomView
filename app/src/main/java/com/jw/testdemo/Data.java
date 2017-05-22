package com.jw.testdemo;

/**
 * Created by Administrator on 2017/1/13.
 */

public class Data {
    private String data_id;
    /** MimeType
     * 姓名 ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
     * 电话 ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
     * 邮箱 ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
     * 地址 ontactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE
     * 公司 ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE
     * 生日 ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
     * 备注 ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE
     */
    private String mimeType;
    /**
     * DataType
     */
    private String data1;
    /**
     * DataType
     * 电话（手机）ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
     * 电话（住宅）ContactsContract.CommonDataKinds.Phone.TYPE_HOME
     * 电话（工作）ContactsContract.CommonDataKinds.Phone.TYPE_WORK
     * 电话（工作传真）ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK
     * 电话（其他）null
     * 邮箱（个人）ContactsContract.CommonDataKinds.Email.TYPE_HOME
     * 邮箱（工作）ContactsContract.CommonDataKinds.Email.TYPE_WORK
     * 地址（家庭）ContactsContract.CommonDataKinds.SipAddress.TYPE_HOME
     * 地址（工作）ContactsContract.CommonDataKinds.SipAddress.TYPE_WORK
     * 地址（其他）ContactsContract.CommonDataKinds.SipAddress.TYPE_OTHER
     * 公司 ContactsContract.CommonDataKinds.Organization.TYPE_WORK
     * 生日 ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY
     * 备注 null
     */
    private String data2;
    private String data3;
    private String data4;
    /**
     * 生日（公历） 3
     * 生日（农历） 2
     */
    private String data5;
}
