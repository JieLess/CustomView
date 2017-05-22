package com.jw.testdemo;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private TextView tv_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_log = (TextView) findViewById(R.id.tv_log);
        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_log.setText("");
                init();
            }
        });
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();
            }
        });
        findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delContactByName("XYZ");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int state = this.checkSelfPermission(Manifest.permission.READ_CONTACTS);
            if (state != PackageManager.PERMISSION_GRANTED) {
                if (this.shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                    init();
                } else {
                    this.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                init();
            }
        }else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    init();
                } else {
                    // permission denied, boo! Disable the
                }
                return;
            }
        }
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getContacts();
            }
        }).start();
    }

    private void getContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                // 姓名
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                // 电话（手机、住宅、工作、工作传真、其他）
                getPhone(cr, id, name);

                // 邮箱（个人，工作）
                getEMail(cr, id, name);

                // 地址（家庭、工作、其它）
                getAddress(cr, id, name);

                // 公司
                getCompany(cr, id, name);

                // 生日
                getBirthday(cr, id, name);

                // 备注
                getNote(cr, id, name);
            }
            cur.close();
        }
    }

    private void getPhone(ContentResolver cr, String contactId, String name) {
        Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.CONTACT_ID + "=?" + " and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}, null);
        while (cur.moveToNext()) {
            int type = cur.getInt(cur.getColumnIndex(ContactsContract.Data.DATA2));
            String data1 = cur.getString(cur.getColumnIndex(ContactsContract.Data.DATA1));
            switch (type) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    showLog(name + "(电话-手机)", data1);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    showLog(name + "(电话-住宅)", data1);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    showLog(name + "(电话-工作)", data1);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                    showLog(name + "(电话-工作传真)", data1);
                    break;
                default:
                    showLog(name + "(电话-其他)", data1);
                    break;
            }
        }
        cur.close();
    }

    private void getEMail(ContentResolver cr, String contactId, String name) {
        Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.CONTACT_ID + "=?" + " and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{contactId, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE}, null);
        while (cur.moveToNext()) {
            int type = cur.getInt(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.TYPE));
            String data1 = cur.getString(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            switch (type) {
                case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                    showLog(name + "(邮箱-个人)", data1);
                    break;
                case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                    showLog(name + "(邮箱-工作)", data1);
                    break;
            }
        }
        cur.close();
    }

    private void getAddress(ContentResolver cr, String contactId, String name) {
        Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.CONTACT_ID + "=?" + " and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{contactId, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}, null);
        while (cur.moveToNext()) {
            int type = cur.getInt(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.TYPE));
            String data1 = cur.getString(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            switch (type) {
                case ContactsContract.CommonDataKinds.SipAddress.TYPE_HOME:
                    showLog(name + "(地址-家庭)", data1);
                    break;
                case ContactsContract.CommonDataKinds.SipAddress.TYPE_WORK:
                    showLog(name + "(地址-工作)", data1);
                    break;
                case ContactsContract.CommonDataKinds.SipAddress.TYPE_OTHER:
                    showLog(name + "(地址-其他)", data1);
                    break;
            }
        }
        cur.close();
    }

    private void getCompany(ContentResolver cr, String contactId, String name) {
        Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.CONTACT_ID + "=?" + " and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{contactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}, null);
        while (cur.moveToNext()) {
            String data1 = cur.getString(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            showLog(name + "(公司)", data1);
        }
        cur.close();
    }

    private void getBirthday(ContentResolver cr, String contactId, String name) {
        Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.CONTACT_ID + "=?" + " and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{contactId, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE}, null);
        while (cur.moveToNext()) {
            int type = cur.getInt(cur.getColumnIndex("data5"));
            String data1 = cur.getString(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            switch (type) {
                case 3:
                    showLog(name + "(生日-公历)", data1);
                    break;
                case 2:
                    showLog(name + "(生日-农历)", data1);
                    break;
            }
        }
        cur.close();
    }

    private void getNote(ContentResolver cr, String contactId, String name) {
        Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, null,
                ContactsContract.Data.CONTACT_ID + "=?" + " and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{contactId, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE}, null);
        while (cur.moveToNext()) {
            String data1 = cur.getString(cur.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            showLog(name + "(备注)", data1);
        }
        cur.close();
    }

    public void addContact() {
        String DisplayName = "XYZ";
        String MobileNumber = "18565769817";
        String HomeNumber = "18565769816";
        String WorkNumber = "18565769815";
        String pEmail = "personal@nomail.com";
        String wEmail = "work@nomail.com";
        String hAddress = "家庭地址";
        String wAddress = "工作地址";
        String oAddress = "其他地址";
        String company = "公司";
        String birthday = "2017-1-12";
        String note = "备注备注备注";

        String id = getContactID(DisplayName);
        if(!id.equals("0")) {
            Toast.makeText(this,"contact already exist", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(DisplayName)){
            Toast.makeText(this,"contact name is empty", Toast.LENGTH_SHORT).show();
        } else {

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            //------------------------------------------------------ Names
            if (DisplayName != null) {
                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, DisplayName).build());
            }

            //------------------------------------------------------ Number
            if (MobileNumber != null) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                        .build());
            }

            //------------------------------------------------------ Email
            if (pEmail != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, pEmail)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, wEmail)
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .build());
            }

            //------------------------------------------------------ Address
            if (hAddress != null) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.SipAddress.DATA, hAddress)
                        .withValue(ContactsContract.CommonDataKinds.SipAddress.TYPE, ContactsContract.CommonDataKinds.SipAddress.TYPE_HOME)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.SipAddress.DATA, wAddress)
                        .withValue(ContactsContract.CommonDataKinds.SipAddress.TYPE, ContactsContract.CommonDataKinds.SipAddress.TYPE_WORK)
                        .build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.SipAddress.DATA, oAddress)
                        .withValue(ContactsContract.CommonDataKinds.SipAddress.TYPE, ContactsContract.CommonDataKinds.SipAddress.TYPE_OTHER)
                        .build());
            }

            //------------------------------------------------------ Organization
            if (!TextUtils.isEmpty(company)) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                        .build());
            }

            //------------------------------------------------------ Birthday
            if (!TextUtils.isEmpty(birthday)) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Event.DATA, birthday)
                        .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                        .withValue(ContactsContract.CommonDataKinds.Event.DATA5, "3") //公历
                        .build());

            /*ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Event.DATA, birthday)
                    .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                    .withValue(ContactsContract.CommonDataKinds.Event.DATA5, "2") //农历
                    .build());*/
            }

            //------------------------------------------------------ Note
            if (!TextUtils.isEmpty(note)) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE, note)
                        .build());
            }

            // Asking the Contact provider to create a new contact
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void delContactByName(String name) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String id = getContactID(name);
        if(id.equals("0")) {
            Toast.makeText(this, name + " not exist", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"contact name is empty", Toast.LENGTH_SHORT).show();
        }else {
            //delete contact
            ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", new String[]{id})
                    .build());
            //delete contact information such as phone number,email
            ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=?", new String[]{id})
                    .build());

            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateContact(String id){
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        // update
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data._ID + "=?", new String[]{id})
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, "somebody@android.com")
                .build());

        // insert
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "12345678912")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "free")
                .build());

        // delete
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data._ID + "=?", new String[]{String.valueOf(id)})
                .build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public String getContactID(String name) {
        String id = "0";
        Cursor cur = getContentResolver().query(
                android.provider.ContactsContract.Contacts.CONTENT_URI,
                new String[]{android.provider.ContactsContract.Contacts._ID},
                android.provider.ContactsContract.Contacts.DISPLAY_NAME + "=?",
                new String[]{name}, null);
        if (cur.moveToNext()) {
            id = cur.getString(cur.getColumnIndex(
                    android.provider.ContactsContract.Contacts._ID));
            cur.close();
        }
        return id;
    }

    private void showLog(String tag, String msg) {
        final String log = tag + msg + "\n";
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_log.append(log);
            }
        });
    }

}
