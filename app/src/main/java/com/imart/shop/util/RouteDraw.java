package com.imart.shop.util;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.imart.shop.app.myapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteDraw {

    double from_lat = 24.905954;
    double from_log = 67.0803505;
    double to_lat = 24.9053485;
    double to_log = 67.079119;
    String google_key = "";
    Polyline line;

    GoogleMap g_map;
    Context context;
    float zoom_level = 11.0f;
    String color_hash = "#FF4081";
    public onDrawRoute callInterface;
    boolean show_loader = true;
    String loader_msg = "Please wait...";

    public RouteDraw(onDrawRoute dlg, Context ctx) {
        this.context = ctx;
        this.callInterface = dlg;
    }

    public static RouteDraw getInstance(onDrawRoute dlg, Context ctx) {
        return new RouteDraw(dlg, ctx);
    }

    public interface onDrawRoute {
        public void afterDraw(String result);
    }

    public RouteDraw setFromLatLong(double fromlat, double fromlog) {
        this.from_lat = fromlat;
        this.from_log = fromlog;
        return this;
    }

    public RouteDraw setToLatLong(double tolat, double tolog) {
        this.to_lat = tolat;
        this.to_log = tolog;
        return this;
    }

    public RouteDraw setGmapAndKey(String googlekey, GoogleMap gmap) {
        this.google_key = googlekey;
        this.g_map = gmap;
        return this;
    }

    public RouteDraw setZoomLevel(float zoomlevel) {
        this.zoom_level = zoomlevel;
        return this;
    }

    public RouteDraw setColorHash(String colorhash) {
        this.color_hash = colorhash;
        return this;
    }

    public RouteDraw setLoader(boolean showloader){
        this.show_loader = showloader;
        return this;
    }

    public RouteDraw setLoaderMsg(String loadermsg){
        this.loader_msg = loadermsg;
        return this;
    }

    public void run() {
        if (google_key.equals("")) {
            Toast.makeText(context, "Please set google map key", Toast.LENGTH_SHORT).show();
            return;
        }
        final String dUrl = generateURL(from_lat, from_log, to_lat, to_log, google_key);
        //Showing a dialog till we get the route
        //final ProgressDialog loading = ProgressDialog.show(context, "Getting Route", "Please wait...", false, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(dUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //loading.dismiss();
                        //Calling the method drawPath to draw the path
                        drawPath(response);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(from_lat, from_log)).zoom(zoom_level).build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        g_map.animateCamera(cameraUpdate);
                        callInterface.afterDraw(response);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading.dismiss();
                    }
                });

        //Adding the request to request queue
        myapp.getInstance().addToRequestQueue(stringRequest);
    }

    public String generateURL(double sourcelat, double sourcelog, double destlat, double destlog, String google_key) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=" + google_key);
        return urlString.toString();
    }

    public void drawPath(String result) {

        try {
            //Convert string to jsona and parse
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);

            //parse total jarak
            JSONArray arr = routes.getJSONArray("legs");
            JSONObject a = arr.getJSONObject(0);
            JSONObject b = a.getJSONObject("distance");
           // Constant.DurasiJarak = b.getString("text");

            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            if (line != null) {
                line.remove();
            }
            line = g_map.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor(color_hash))
                    .geodesic(true)
            );

        } catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
