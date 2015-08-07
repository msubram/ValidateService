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

    /** URL's to access the server*/
    String base_url = "http://www.csharpsolutions.co.uk/ValidateApp/api/v1/";
    String registration_request = "RegistrationRequest/";
    String complete_registration_request = "Registrations/";
    String gcm_registration_request = "GCMRegistrationRequest/";

    String port = "38798";

    /** SharedPreference and JSON TAGS*/
    public static String check_login = "login";
    public static String country_code = "CountryCode";
    public static String mobile_country_code = "MobileCountryCode";
    public static String mobile_number = "MobileNumber";
    public static String mobile_info = "MobileInfo";
    public static String gcm_token = "Token";
    public static String ip_address = "IP";
    public static String sender_name = "Name";
    public static String endPoint = "EndPoint";
    public static String udpListenPort = "UDPListenPort";
    public static String instance_id = "InstanceId";
    public static String email = "Email";
    public static String reg_id = "reg_id";
    public static String reg_code = "RegCode";
    public static String work_number = "WorkNumber";
    public static String home_number = "HomeNumber";
    public static String check_gcmisregistered = "isgcmregistered";



    int statuscode;
    String response_from_server = null;


    /**GCM Tags*/
    public static String notification_bodytag = "gcm.notification.body";
    public static String notification_titletag = "gcm.notification.title";


    /** Base url to access the API*/
    public String getBase_url()
    {
        return base_url;
    }

    /** Routes for Registration Request*/
    public String getRegistration_request_routes()
    {
        return registration_request;
    }

    /** Routes for Completing Registration Request*/
    public String get_Complete_Registration_request_routes()
    {
        return complete_registration_request;
    }

    /** Routes for GCM Registration Request*/
    public String get_gcm_registration_request()
    {
        return gcm_registration_request;
    }

    /** Routes for Completing Registration Request*/
    public String get_check_login()
    {
        return check_login;
    }

    /**Port to listen and send messages*/
    public String get_Udp_port()
    {
        return port;
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
    public void setStatusCode(int statusCode)
    {
         this.statuscode = statusCode;
    }

    public void setServerResponse(String response_from_server)
    {
        this.response_from_server = response_from_server;
    }



    /** Method to parse the response given by POST and GET method. Both POST and GET returns in the form of JSON with the StatusCode and Response from server.*/
    public String parseServerResponseJSON(String response_from_server_json)
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

        return response_from_server;
    }


    public boolean checkWifiConnectivity()
    {
        boolean wifi_availability = false;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Do whatever
            wifi_availability = true;
        }
        return wifi_availability;
    }



    public String sendGet(String url, int connection_timeout) throws Exception {


        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setConnectTimeout(connection_timeout);
        //add request header

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        /** Form a JSONObjec with StatusCode and Response from the server*/
        String responsejson = new JSONObject().put("statuscode",responseCode).put("response",response.toString()).toString();


        return responsejson;


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