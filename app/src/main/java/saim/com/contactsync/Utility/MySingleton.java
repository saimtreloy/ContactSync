package saim.com.contactsync.Utility;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Android on 1/2/2017.
 */
public class MySingleton {

    private static MySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context context;

    private MySingleton(Context cntx){
        this.context = cntx;
        this.requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized  MySingleton getInstance(Context cntx){
        if (mInstance == null){
            mInstance = new MySingleton(cntx);
        }
        return mInstance;
    }

    public <T>void addToRequestQueue(Request<T> tRequest){
        requestQueue.add(tRequest);
    }
}
