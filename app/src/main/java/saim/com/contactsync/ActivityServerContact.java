package saim.com.contactsync;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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

    ImageView imgSyncServerConatct;
    TextView txtMainTitle;

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

        imgSyncServerConatct = (ImageView) findViewById(R.id.imgSyncServerConatct);
        txtMainTitle = (TextView) findViewById(R.id.txtMainTitle);

        ContactRetrive(new SharedPrefDatabase(getApplicationContext()).RetriveID().toString().trim());

        imgSyncServerConatct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTaskAddAllContact().execute("Hello Saim");
            }
        });
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add_contact:

                new AsyncTaskAddAllContact().execute("Hello Saim");

                return true;
            case R.id.menu_delete:



                return true;
            case R.id.menu_exit:



                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class AsyncTaskAddAllContact extends AsyncTask<String, String, String> {

        private String resp;
        int i =0;

        @Override
        protected String doInBackground(String... params) {

            for (i=0; i<adapterlist.size(); i++){
                Log.d("SAIM HANDLER", adapterlist.get(i).getName());
                ContentValues values = new ContentValues();
                values.put(Contacts.People.NUMBER, adapterlist.get(i).getNumber());
                values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
                values.put(Contacts.People.NAME, adapterlist.get(i).getName());
                Uri dataUri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
                Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
                values.clear();
                values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
                values.put(Contacts.People.NUMBER, adapterlist.get(i).getNumber());
                updateUri = getContentResolver().insert(updateUri, values);

                publishProgress(adapterlist.get(i).getName());
            }

            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "All contact added to your conatact list.", Toast.LENGTH_LONG).show();
        }


        @Override
        protected void onPreExecute() {
            progressDialog.setTitle("Updateing Contacts");
            progressDialog.setMessage("Please wait. Contact is updateing...");
            progressDialog.show();
        }


        @Override
        protected void onProgressUpdate(String... text) {
            progressDialog.setMessage(text[0]);
        }

    }

}
