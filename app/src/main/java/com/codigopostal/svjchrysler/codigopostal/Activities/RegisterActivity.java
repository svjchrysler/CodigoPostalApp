package com.codigopostal.svjchrysler.codigopostal.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codigopostal.svjchrysler.codigopostal.R;
import com.codigopostal.svjchrysler.codigopostal.Utilities.Urls;
import com.codigopostal.svjchrysler.codigopostal.Utilities.UserLogin;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

    @NotEmpty(message = "Escriba su Nombre")
    private EditText edtNombre;
    @NotEmpty(message = "Escriba su Profesion")
    private EditText edtProfesion;
    @NotEmpty(message = "Escriba su Correo Electronico")
    private EditText edtEmail;
    @NotEmpty(message = "Escriba su Contrasena")
    private EditText edtPassword;
    @NotEmpty(message = "Verificar Contrasena")
    private EditText edtVerificarPassword;

    private TextView tvIniciarSesion;

    private Button btnRegistrar;

    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        configInit();
    }

    private void configInit() {
        configTexts();
        configComponents();
        configEvents();
    }

    private void configTexts() {
        this.setTitle(R.string.titleRegistrarse);
    }

    private void configComponents() {
        edtNombre = (EditText) findViewById(R.id.edtNombre);
        edtProfesion = (EditText) findViewById(R.id.edtProfesion);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtVerificarPassword = (EditText) findViewById(R.id.edtVerificarPassword);
        tvIniciarSesion = (TextView) findViewById(R.id.tvIniciarSesion);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        validator = new Validator(this);
    }

    private void configEvents() {
        tvIniciarSesion.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);
        validator.setValidationListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegistrar:
                registrarPersona();
                break;
            case R.id.tvIniciarSesion:
                redirectIniciarSesion();
                break;
        }
    }

    private void redirectIniciarSesion() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    private void registrarPersona() {
        validator.validate();
    }

    private void peticionRegistrarPersona() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando su informacion");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Urls.URL_REGISTRAR_USUARIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UserLogin.id = response;
                        UserLogin.nombre = edtNombre.getText().toString();
                        redirectListUbications();
                        Toast.makeText(RegisterActivity.this, "Datos Registrados Correctamente", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    private void redirectListUbications() {
                        Intent intent = new Intent(RegisterActivity.this, ListUbicationsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error en el servidor Intentarlo mas tarde", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("social_id", "1");
                params.put("name", edtNombre.getText().toString());
                params.put("email", edtEmail.getText().toString());
                params.put("password", edtPassword.getText().toString());
                params.put("provider", "Email");
                params.put("profession", edtProfesion.getText().toString());
                params.put("tipo", "1");
                return params;
            }
        };

        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }

    @Override
    public void onValidationSucceeded() {
        peticionRegistrarPersona();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
