package util;

/**
 * Created by srinath on 14-07-2015.
 */
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GlobalClass extends Application{

    public static final String TAG = "Validations";

    /** SharedPreference and JSON TAGS*/
    public static final String CHECK_LOGIN = "login";
    public static final String COUNTRY_CODE = "CountryCode";
    public static final String MOBILE_COUNTRY_CODE = "MobileCountryCode";
    public static final String MOBILE_NUMBER = "MobileNumber";
    public static final String MOBILE_INFO = "MobileInfo";
    public static final String GCM_TOKEN = "Token";
    public static final String IP_ADDRESS = "IP";
    public static final String SENDER_NAME = "Name";
    public static final String END_POINT = "EndPoint";
    public static final String UDP_LISTEN_PORT = "UDPListenPort";
    public static final String INSTANCE_ID = "InstanceId";
    public static final String EMAIL = "Email";
    public static final String REG_ID = "reg_id";
    public static final String REG_CODE = "RegCode";
    public static final String WORK_NUMBER = "WorkNumber";
    public static final String HOME_NUMBER = "HomeNumber";
    public static final String CHECK_MESSAGE_RECEIVED = "checkmessagereceived";


    private int statuscode;
    private String response_from_server = null;


    /**GCM Tags*/
    public static final String NOTIFICATION_BODY_TAG = "gcm.notification.body";
    public static final String NOTIFICATION_TITLE_TAG = "gcm.notification.title";
    public static final String NOTIFICATION_MESSAGE = "notif_message";

    /** Base url to access the API*/
    public String getBase_url()
    {
        /* URL's to access the server*/
        String mBase_url = "http://www.csharpsolutions.co.uk/ValidateApp/api/v1/";
        return mBase_url;
    }

    /** Routes for Registration Request*/
    public String getRegistration_Request_Routes()
    {
        String mRegistration_request = "RegistrationRequest/";
        return mRegistration_request;
    }

    /** Routes for Completing Registration Request*/
    public String getComplete_Registration_Request_Routes()
    {
        String mComplete_registration_request = "Registrations/";
        return mComplete_registration_request;
    }

    /** Routes for GCM Registration Request*/
    public String getGcm_Registration_Request()
    {
        String mGcm_registration_request = "GCMRegistrationRequest/";
        return mGcm_registration_request;
    }

    /** Routes for Completing Registration Request*/
    public String getCheck_Login()
    {
        return CHECK_LOGIN;
    }

    /**Get methods*/
    public int getStatusCode()
    {
        return statuscode;
    }

    public String getServerResponse()
    {
        return response_from_server;
    }


    /**Set methods*/
    void setStatusCode(int statusCode)
    {
         this.statuscode = statusCode;
    }

    void setServerResponse(String response_from_server)
    {
        this.response_from_server = response_from_server;
    }



    /** Method to parse the response given by POST and GET method. Both POST and GET returns in the form of JSON with the StatusCode and Response from server.*/
    public void parseServerResponseJSON(String response_from_server_json)
    {
        try
        {
            JSONObject response_jsonobject = new JSONObject(response_from_server_json);
            setStatusCode(response_jsonobject.getInt("statuscode"));
            setServerResponse(response_jsonobject.getString("response"));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public boolean checkWifiConnectivity(Context context)
    {
        boolean wifi_availability = false;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Do whatever
            wifi_availability = true;
        }
        return wifi_availability;
    }


    public String sendPost(String url,String urlParameters) throws Exception {

        // String url = "https://selfsolve.apple.com/wcResults.do";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");



        // Send post request
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
        //print result
        /** Form a JSONObjec with StatusCode and Response from the server*/
        String responsejson = new JSONObject().put("statuscode",responseCode).put("response",response.toString()).toString();


        return responsejson;


    }

}