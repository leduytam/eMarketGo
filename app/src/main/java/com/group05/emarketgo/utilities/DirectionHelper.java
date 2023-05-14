package com.group05.emarketgo.utilities;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

public class DirectionHelper {
    private static final String TAG = DirectionHelper.class.getSimpleName();
    private static final String API_KEY = "AIzaSyBpbbzd_gEfynO4AatkbOuk4DjqGxIa4Vg";

    public static void getDirection(Context context, double originLat, double originLng, double destinationLat, double destinationLng, final MapView mapView, DirectionListener listener) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                new DirectionTask(context, googleMap, listener).execute(originLat, originLng, destinationLat, destinationLng);
            }
        });
    }

    public interface DirectionListener {
        void onDirectionReceived(DirectionsResult directionsResult, Duration duration);

        void onDirectionError();
    }

    private static class DirectionTask extends AsyncTask<Double, Void, DirectionsResult> {
        private Context context;
        private GoogleMap googleMap;
        private DirectionListener listener;

        public DirectionTask(Context context, GoogleMap googleMap, DirectionListener listener) {
            this.context = context;
            this.googleMap = googleMap;
            this.listener = listener;
        }

        @Override
        protected DirectionsResult doInBackground(Double... params) {
            GeoApiContext geoApiContext = new GeoApiContext.Builder()
                    .apiKey(API_KEY)
                    .build();

            try {
                double originLat = params[0];
                double originLng = params[1];
                double destinationLat = params[2];
                double destinationLng = params[3];

                return DirectionsApi.newRequest(geoApiContext)
                        .mode(TravelMode.DRIVING)
                        .origin(new com.google.maps.model.LatLng(originLat, originLng))
                        .destination(new com.google.maps.model.LatLng(destinationLat, destinationLng))
                        .await();
            } catch (Exception e) {
                Log.e(TAG, "Error retrieving directions", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(DirectionsResult directionsResult) {
            if (directionsResult != null) {
                displayDirections(directionsResult);
            } else {
                listener.onDirectionError();
            }
        }

        private void displayDirections(DirectionsResult directionsResult) {
            if (googleMap == null) {
                return;
            }

            googleMap.clear();

            DirectionsRoute route = directionsResult.routes[0];
            DirectionsLeg leg = route.legs[0];

            // Set origin marker
            LatLng originLatLng = new LatLng(leg.startLocation.lat, leg.startLocation.lng);
            googleMap.addMarker(new MarkerOptions()
                    .position(originLatLng)
                    .title("Origin"));

            // Set destination marker
            LatLng destinationLatLng = new LatLng(leg.endLocation.lat, leg.endLocation.lng);
            googleMap.addMarker(new MarkerOptions()
                    .position(destinationLatLng)
                    .title("Destination"));
            // Draw polyline for the route
            List<LatLng> polylinePoints = decodePolyline(route.overviewPolyline.getEncodedPath());
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(polylinePoints)
                    .width(10)
                    .color(Color.BLUE);
            Polyline polyline = googleMap.addPolyline(polylineOptions);

            // Zoom to fit the route
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(originLatLng);
            builder.include(destinationLatLng);
            LatLngBounds bounds = builder.build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

            // Get duration
            Duration duration = leg.duration;

            // Notify listener
            listener.onDirectionReceived(directionsResult, duration);
        }

        private List<LatLng> decodePolyline(String encodedPolyline) {
            List<LatLng> points = new ArrayList<>();
            int index = 0, len = encodedPolyline.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encodedPolyline.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encodedPolyline.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                double latDouble = lat * 1e-5;
                double lngDouble = lng * 1e-5;
                LatLng point = new LatLng(latDouble, lngDouble);
                points.add(point);
            }

            return points;
        }
    }
}


