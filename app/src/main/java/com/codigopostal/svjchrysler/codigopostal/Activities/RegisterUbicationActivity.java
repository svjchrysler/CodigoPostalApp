package com.codigopostal.svjchrysler.codigopostal.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codigopostal.svjchrysler.codigopostal.Models.Province;
import com.codigopostal.svjchrysler.codigopostal.R;
import com.codigopostal.svjchrysler.codigopostal.Utilities.Urls;
import com.codigopostal.svjchrysler.codigopostal.Utilities.UserLogin;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegisterUbicationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtCalle;
    TextView tvCoordenada;
    private ImageButton imgBtnOption;
    private Spinner spMunicipalities;
    private RadioButton rbGaleria, rbCamara;
    private ImageView imgFoto;

    private CameraPhoto cameraPhoto;
    private GalleryPhoto galleryPhoto;

    private String userLatitude;
    private String userLength;

    private String pathPhoto;
    private Bitmap bitmapPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ubication);
        configInit();
    }

    private void configInit() {
        configText();
        configComponents();
        configEvents();
        configLocation();
        cargarMunicipios();
    }

    private void configEvents() {
        imgBtnOption.setOnClickListener(this);
    }

    private void configComponents() {
        edtCalle = (EditText)findViewById(R.id.edtCalle);
        tvCoordenada = (TextView)findViewById(R.id.tvLocation);
        spMunicipalities = (Spinner)findViewById(R.id.spMunicipalities);
        rbCamara = (RadioButton)findViewById(R.id.rbCamara);
        rbGaleria = (RadioButton)findViewById(R.id.rbGaleria);
        imgBtnOption = (ImageButton)findViewById(R.id.btnOption);
        imgFoto = (ImageView)findViewById(R.id.imgFoto);
    }

    private void configText() {
        this.setTitle(R.string.regiterLocation);
    }

    private void configLocation() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location Local = new Location();
        Local.setRegisterUbicationActivity(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
    }

    public void setLocation(android.location.Location loc) {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    userLatitude = String.valueOf(loc.getLatitude());
                    userLength = String.valueOf(loc.getLongitude());
                    tvCoordenada.setText("lat: "+loc.getLatitude()+", lon: "+loc.getLongitude());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarMunicipios() {
        final LinkedList<Province> listProvinces = new LinkedList<>();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET,
                Urls.URL_LISTA_MUNICIPIOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i=0;i<array.length();i++){
                                JSONObject jsonObject = array.getJSONObject(i);
                                listProvinces.add(new Province(jsonObject.getString("id"), jsonObject.getString("name")));
                            }
                            ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, listProvinces);
                            spMunicipalities.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterUbicationActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(jsonObjectRequest);
    }

    private void registarUbication() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Guardando Informacion");
        progressDialog.show();

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST,
            Urls.URL_REGISTRAR_UBICACION,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    redirectListUbications();
                }

                private void redirectListUbications() {
                    Intent intent = new Intent(RegisterUbicationActivity.this, ListUbicationsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    finish();
                }
            },

            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegisterUbicationActivity.this, "Error con el servidor", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("person_id", UserLogin.id);
                params.put("municipality_id", ((Province)spMunicipalities.getSelectedItem()).id.toString());
                params.put("streetName", edtCalle.getText().toString());
                params.put("latitude", userLatitude);
                params.put("length", userLength);
                params.put("nameImage", obtenerImagenBase64());
                params.put("state", "0");
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(jsonObjectRequest);

    }

    private String obtenerImagenBase64() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapPhoto.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_ubication, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                registarUbication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOption:
                obtenerImagen();
                break;
        }
    }

    private void obtenerImagen() {
        if (rbCamara.isChecked())
            imagenCamera();
        if (rbGaleria.isChecked())
            imagenGaleria();
    }

    private void imagenGaleria() {
        galleryPhoto = new GalleryPhoto(RegisterUbicationActivity.this);
        startActivityForResult(galleryPhoto.openGalleryIntent(), 1);
    }

    private void imagenCamera() {
        cameraPhoto = new CameraPhoto(RegisterUbicationActivity.this);
        try {
            startActivityForResult(cameraPhoto.takePhotoIntent(), 2);
            cameraPhoto.addToGallery();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {
            Uri uri = data.getData();
            galleryPhoto.setPhotoUri(uri);
            String photoPath = galleryPhoto.getPath();
            cargarImagen(photoPath);
        }
        else {
            String photoPath = cameraPhoto.getPhotoPath();
            cargarImagen(photoPath);
        }
    }

    protected void cargarImagen(String photoPath) {
        pathPhoto = photoPath;
        try {
            Bitmap imageBitmap = ImageLoader.init().from(photoPath).getBitmap();
            bitmapPhoto = imageBitmap;
            imgFoto.setImageBitmap(imageBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
