package saim.com.contactsync;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.markushi.ui.CircleButton;
import saim.com.contactsync.ContactList.ContactAdapter;
import saim.com.contactsync.ContactList.ContactModel;
import saim.com.contactsync.Utility.ApiURL;
import saim.com.contactsync.Utility.MySingleton;
import saim.com.contactsync.Utility.SharedPrefDatabase;

public class MainActivity extends AppCompatActivity {

    ListView listView ;
    ArrayList<String> StoreContacts ;
    ArrayAdapter<String> arrayAdapter ;
    Cursor cursor ;
    String name, phonenumber ;
    public  static final int RequestPermissionCode  = 1 ;
    Button button, button2, btnUpload;

    RecyclerView recyclerViewAllContact;
    RecyclerView.LayoutManager layoutManagerAllContact;
    RecyclerView.Adapter allContactAdapter;

    public static ArrayList<ContactModel> adapterlist = new ArrayList<>();
    public static JSONArray ja = new JSONArray();
    public static ArrayList<ContactModel> retriveList = new ArrayList<>();

    ProgressDialog progressDialog;

    CircleButton btnContactUpload, btnContactSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }


    public void init(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Contact Upload");
        progressDialog.setMessage("Your contact is uploading. \nPlease wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        recyclerViewAllContact = (RecyclerView) findViewById(R.id.recyclerViewAllContact);
        layoutManagerAllContact = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewAllContact.setLayoutManager(layoutManagerAllContact);
        recyclerViewAllContact.setHasFixedSize(true);

        listView = (ListView)findViewById(R.id.listview1);
        button = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        btnUpload = (Button) findViewById(R.id.btnUpload);

        btnContactUpload = (CircleButton) findViewById(R.id.btnContactUpload);
        btnContactSync = (CircleButton) findViewById(R.id.btnContactSync);

        StoreContacts = new ArrayList<String>();

        EnableRuntimePermission();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetContactsIntoArrayList();
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, StoreContacts);
                listView.setAdapter(arrayAdapter);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressDialog.show();
                //ContactRetrive(new SharedPrefDatabase(getApplicationContext()).RetriveID().toString().trim());
                startActivity(new Intent(getApplicationContext(), ActivityServerContact.class));
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                Log.d("SAIM JA", ja.toString());
                ContactUpload(new SharedPrefDatabase(getApplicationContext()).RetriveID().toString().trim(), ja.toString());
            }
        });

        btnContactSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityServerContact.class));
            }
        });

        btnContactUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                ContactUpload(new SharedPrefDatabase(getApplicationContext()).RetriveID().toString().trim(), ja.toString());
            }
        });
    }


    public void GetContactsIntoArrayList(){
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            StoreContacts.add(name + " "  + ":" + " " + phonenumber);

            ContactModel contactModel = new ContactModel(name, phonenumber);
            adapterlist.add(contactModel);
        }
        cursor.close();

        ArrayList<ContactModel> result = new ArrayList<ContactModel>();
        Set<String> titles = new HashSet<String>();

        int count = 0;

        for( ContactModel item : adapterlist ) {
            if( titles.add( item.getName() )) {
                result.add( item );
                JSONObject jo = new JSONObject();
                try {
                    jo.put("n", item.getName());
                    jo.put("p", item.getNumber());
                    ja.put(jo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("SAIM COUNT", count++ + "");
            }
        }
        count=0;
        adapterlist.clear();
        adapterlist = result;

        allContactAdapter = new ContactAdapter(adapterlist);
        recyclerViewAllContact.setAdapter(allContactAdapter);

        Log.d("SAIM", ja.toString());

    }

    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)
                && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CALL_PHONE)
                && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_CONTACTS}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                    GetContactsIntoArrayList();
                    arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, StoreContacts);
                    listView.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(MainActivity.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    public void ContactUpload(final String user_id, final String contact_list) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiURL.getContactUpload,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("success")){
                                String message = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Log.d("HDHD ", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user_id", user_id.trim());
                params.put("contact_list", contact_list.trim());

                return params;
            }
        };
        stringRequest.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void ContactRetrive(final String user_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiURL.getContactList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length()>1){
                                for (int i=0; i<jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String name = jsonObject.getString("n");
                                    String phone = jsonObject.getString("p");
                                    ContactModel cm = new ContactModel(name, phone);
                                    retriveList.add(cm);
                                    Log.d("SAIM LIST", name + " : " + phone);
                                }
                            } else {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String code = jsonObject.getString("code");
                                String message = jsonObject.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Log.d("HDHD ", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("user_id", user_id.trim());

                return params;
            }
        };
        stringRequest.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
