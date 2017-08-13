package com.tk4218.grocerylistr.Model;

import android.util.Log;
import android.net.Uri;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Tk4218 on 8/12/2017.
 */

public class JSONParser {

    public JSONObject makeHttpRequest(String link, ArrayList<ArrayList<String>> params) {
        String json = "";
        JSONObject jObj = null;


        try {
            /****************************************
             Set URL to connect to amazon AWS database
             ****************************************/
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //Set connection settings
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            /********************************
             * Build and set POST parameters
             ********************************/
            Uri.Builder builder = new Uri.Builder();
            for(int i = 0; i < params.size(); i++) {
                builder.appendQueryParameter(params.get(i).get(0), params.get(i).get(1));
            }

            String parameters = builder.build().getEncodedQuery();
            Log.d("POST Parameters", parameters);

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(parameters);
            writer.flush();
            writer.close();
            os.close();

            /**********************************
             * Read results and set JSONObject
             **********************************/
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line= reader.readLine()) != null){
                sb.append(line + "\n");
            }
            json = sb.toString();
            Log.d("JSON Text", json);

            jObj = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }
}
