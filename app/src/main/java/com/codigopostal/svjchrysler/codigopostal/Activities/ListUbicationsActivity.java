package com.codigopostal.svjchrysler.codigopostal.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codigopostal.svjchrysler.codigopostal.Adapters.ListAdapter;
import com.codigopostal.svjchrysler.codigopostal.Models.Ubication;
import com.codigopostal.svjchrysler.codigopostal.R;
import com.codigopostal.svjchrysler.codigopostal.Utilities.Urls;
import com.codigopostal.svjchrysler.codigopostal.Utilities.UserLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ListUbicationsActivity extends AppCompatActivity {

    private RecyclerView recyclerListUbications;
    private RecyclerView.Adapter adapterListUbications;
    private RecyclerView.LayoutManager layoutManagerListUbications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ubications);
        configInit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.register_ubication, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void configInit() {
        configTexts();
        configComponents();
    }

    private void configTexts() {
        this.setTitle(UserLogin.nombre);
    }

    private void configComponents() {
        recyclerListUbications = (RecyclerView)findViewById(R.id.reciclerListUbications);
        recyclerListUbications.setHasFixedSize(true);

        layoutManagerListUbications = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerListUbications.setLayoutManager(layoutManagerListUbications);

        cargarListUbications();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                redirectRegisterUbication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void redirectRegisterUbication() {
        Intent intent = new Intent(ListUbicationsActivity.this, RegisterUbicationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private void cargarListUbications() {
        final LinkedList<Ubication> listUbications = new LinkedList<>();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando Ubicaciones Registradas");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
            Urls.URL_LISTA_UBICACIONES + UserLogin.id,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i=0;i<array.length();i++){
                            JSONObject jsonObject = array.getJSONObject(i);
                            Ubication ubication = new Ubication();
                            ubication.id = jsonObject.getString("id");
                            ubication.codigoPostal = jsonObject.getString("codePostal");
                            ubication.calle = jsonObject.getString("streetName");
                            ubication.imagen = jsonObject.getString("nameImage");
                            listUbications.add(ubication);
                        }
                        cargarAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }

                private void cargarAdapter() {
                    adapterListUbications = new ListAdapter(listUbications);
                    recyclerListUbications.setAdapter(adapterListUbications);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(ListUbicationsActivity.this, "Error en el servidor Intentarlo mas tarde", Toast.LENGTH_SHORT).show();
                }
            }
        );

        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
}
