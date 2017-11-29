package saim.com.contactsync.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.BoringLayout;

public class SharedPrefDatabase {

    public static final String KEY_LOGIN_STATUS = "KEY_LOGIN_STATUS";
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_MOBILE = "KEY_MOBILE";
    public static final String KEY_EMAIL = "KEY_EMAIL";


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    public SharedPrefDatabase(Context ctx) {
        this.context = ctx;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        editor = sharedPreferences.edit();
    }

    public void StoreLOGIN_STATUS(Boolean data){
        editor.putBoolean(KEY_LOGIN_STATUS, data);
        editor.commit();
    }
    public Boolean RetriveLOGIN_STATUS(){
        Boolean text = sharedPreferences.getBoolean(KEY_LOGIN_STATUS, false);
        return text;
    }

    public void StoreID(String data){
        editor.putString(KEY_ID, data);
        editor.commit();
    }
    public String RetriveID(){
        String text = sharedPreferences.getString(KEY_ID, null);
        return text;
    }

    public void StoreNAME(String data){
        editor.putString(KEY_NAME, data);
        editor.commit();
    }
    public String RetriveNAME(){
        String text = sharedPreferences.getString(KEY_NAME, null);
        return text;
    }

    public void StoreMOBILE(String data){
        editor.putString(KEY_MOBILE, data);
        editor.commit();
    }
    public String RetriveMOBILE(){
        String text = sharedPreferences.getString(KEY_MOBILE, null);
        return text;
    }

    public void StoreEMAIL(String data){
        editor.putString(KEY_EMAIL, data);
        editor.commit();
    }
    public String RetriveEMAIL(){
        String text = sharedPreferences.getString(KEY_EMAIL, null);
        return text;
    }


}
