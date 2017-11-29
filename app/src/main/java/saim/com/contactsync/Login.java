package saim.com.contactsync;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import saim.com.contactsync.Utility.ApiURL;
import saim.com.contactsync.Utility.MySingleton;
import saim.com.contactsync.Utility.SharedPrefDatabase;

public class Login extends AppCompatActivity {

    ProgressDialog progressDialog;

    EditText inputEmail, inputPassword;
    Button btnLogin;
    TextView txtTarmsCondition, txtForgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    public void init(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please wait checking your information.");
        progressDialog.setCanceledOnTouchOutside(false);

        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);

        btnLogin = (Button) findViewById(R.id.btnLogin);

        txtTarmsCondition = (TextView) findViewById(R.id.txtTarmsCondition);
        txtForgetPassword = (TextView) findViewById(R.id.txtForgetPassword);

        ButtonClicked();
    }


    public void ButtonClicked(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (inputEmail.getText().toString().isEmpty() || inputPassword.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Email or Password can not be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    SaveUserLogin(inputEmail.getText().toString(), inputPassword.getText().toString());
                }
            }
        });
    }


    public void SaveUserLogin(final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiURL.getLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("success")){

                                String id = jsonObject.getString("id");
                                String name = jsonObject.getString("name");
                                String mobile = jsonObject.getString("mobile");
                                String email = jsonObject.getString("email");

                                Log.d("SAIM LOG",id + " " + name + " " + mobile + " " + email);

                                new SharedPrefDatabase(getApplicationContext()).StoreLOGIN_STATUS(true);
                                new SharedPrefDatabase(getApplicationContext()).StoreID(id);
                                new SharedPrefDatabase(getApplicationContext()).StoreNAME(name);
                                new SharedPrefDatabase(getApplicationContext()).StoreMOBILE(mobile);
                                new SharedPrefDatabase(getApplicationContext()).StoreEMAIL(email);

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();

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
                params.put("user_email", email);
                params.put("user_pass", password);

                return params;
            }
        };
        stringRequest.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
