package com.nikzzzn.mapbox;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.MapboxAccountManager;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
private MapView mapView;
    Dialog addMarkerDialog;
    static final int GALLERY_REQUEST = 1;
    //Bitmap bitmap = null;
    private MapboxMap map;
    FloatingActionButton floatingActionButton;
    LocationServices locationServices;

    private static final int PERMISSIONS_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.accessToken));
        setContentView(R.layout.activity_main);
        locationServices = LocationServices.getLocationServices(MainActivity.this);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                map = mapboxMap;

                mapboxMap.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull final LatLng point) {


                        new MaterialDialog.Builder(MainActivity.this)
                                .title(R.string.input)
                                .content(R.string.input_content)
                                .inputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                                .input(R.string.input_hint1, 0, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog, final CharSequence input1) {
                                        new MaterialDialog.Builder(MainActivity.this)
                                                .title(R.string.input)
                                                .content(R.string.input_content)
                                                .inputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                                                .input(R.string.input_hint2, 0, new MaterialDialog.InputCallback() {

                                                    @Override
                                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input2) {
                                                        /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                                        photoPickerIntent.setType("image/*");
                                                        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                                                        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                                                        if(bitmap!=null) {
                                                            Icon icon = iconFactory.fromBitmap(bitmap);
                                                            MarkerViewOptions marker = new MarkerViewOptions().icon(icon)
                                                                    .position(new LatLng(point))
                                                                    .title(input1.toString())
                                                                    .snippet(input2.toString());

                                                            mapboxMap.addMarker(marker);
                                                        }*/
                                                        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                                                        Drawable iconDrawable = ContextCompat.getDrawable(MainActivity.this, R.mipmap.egor);
                                                        Icon icon = iconFactory.fromDrawable(iconDrawable);
                                                        MarkerViewOptions marker = new MarkerViewOptions().icon(icon)
                                                                .position(new LatLng(point))
                                                                .title(input1.toString())
                                                                .snippet(input2.toString());
                                                        mapboxMap.addMarker(marker);

                                                    }

                                                }).show();


                                    }
                                }).show();
                    }

                });
                mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {

                        return false;
                    }
                });
            }});
        floatingActionButton = (FloatingActionButton) findViewById(R.id.myFAB);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
                }
            }
        });
    }
    public void toggleGps(boolean enableGps) {
        if (enableGps){
            if (!locationServices.areLocationPermissionsGranted()) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
            }
            else {
                enableLocation(true);
            }
        }
        else{
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            locationServices.addLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        // Move the map camera to where the user location is
                        map.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(location))
                                .zoom(16)
                                .build());
                    }
                }
            });
//            floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
        } else {
  //          floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_LOCATION: {
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableLocation(true);
                }
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    /*try {
                        Bitmap bitmap_ = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        bitmap = bitmap_.copy(Bitmap.Config.ARGB_8888,true);
                        try{ bitmap.setWidth((int)bitmap_.getWidth()/2);
                        bitmap.setHeight((int)bitmap_.getHeight()/2);}

                        catch(NullPointerException npe){}
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


}
