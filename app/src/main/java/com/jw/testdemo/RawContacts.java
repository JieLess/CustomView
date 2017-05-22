package com.jw.testdemo;

import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */

public class RawContacts {
    private String contact_id;
    private String raw_contact_id;
    private List<Data> datas;

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public String getRaw_contact_id() {
        return raw_contact_id;
    }

    public void setRaw_contact_id(String raw_contact_id) {
        this.raw_contact_id = raw_contact_id;
    }

    public List<Data> getDatas() {
        return datas;
    }

    public void setDatas(List<Data> datas) {
        this.datas = datas;
    }
}
