package saim.com.contactsync;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import saim.com.contactsync.ContactList.ContactAdapter;
import saim.com.contactsync.ContactList.ContactModel;
import saim.com.contactsync.Utility.ApiURL;
import saim.com.contactsync.Utility.MySingleton;
import saim.com.contactsync.Utility.SharedPrefDatabase;

public class ActivityServerContact extends AppCompatActivity {

    ProgressDialog progressDialog;

    public static ArrayList<ContactModel> adapterlist = new ArrayList<>();
    RecyclerView recyclerViewServerContact;
    RecyclerView.LayoutManager layoutManagerServerContact;
    RecyclerView.Adapter serverContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_contact);

        init();
    }

    private void init() {

        this.setTitle("Server Contact");

        adapterlist.clear();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Your request is processing.\nPlease wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        recyclerViewServerContact = (RecyclerView) findViewById(R.id.recyclerViewServerContact);
        layoutManagerServerContact = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewServerContact.setLayoutManager(layoutManagerServerContact);
        recyclerViewServerContact.setHasFixedSize(true);

        ContactRetrive(new SharedPrefDatabase(getApplicationContext()).RetriveID().toString().trim());
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
                                    adapterlist.add(cm);
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

                        Collections.sort(adapterlist, new Comparator<ContactModel>() {
                            @Override
                            public int compare(ContactModel o1, ContactModel o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });

                        ArrayList<ContactModel> result = new ArrayList<ContactModel>();
                        Set<String> titles = new HashSet<String>();
                        for( ContactModel item : adapterlist ) {
                            if( titles.add( item.getName() )) {
                                result.add( item );
                                JSONObject jo = new JSONObject();
                            }
                        }

                        adapterlist.clear();
                        adapterlist = result;

                        serverContactAdapter = new ContactAdapter(adapterlist);
                        recyclerViewServerContact.setAdapter(serverContactAdapter);
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
