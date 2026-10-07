package com.example.proyecto_nutritrak;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etApellido;
    private EditText etCorreo;
    private EditText etPassword;
    private EditText etEdad;

    private AutoCompleteTextView etSexo;

    private EditText etPeso;
    private EditText etAltura;

    private Button btnRegistrar;
    private TextView txtLogin;

    private static final String URL_REGISTRO =
            "http://10.0.2.2/nutritrack_api/registro.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro);

        inicializarComponentes();

        configurarSelectorSexo();

        configurarEventos();
    }

    private void inicializarComponentes() {

        etNombre = findViewById(R.id.etNombre);

        etApellido = findViewById(R.id.etApellido);

        etCorreo = findViewById(R.id.etCorreo);

        etPassword = findViewById(R.id.etPassword);

        etEdad = findViewById(R.id.etEdad);

        etSexo = findViewById(R.id.etSexo);

        etPeso = findViewById(R.id.etPeso);

        etAltura = findViewById(R.id.etAltura);

        btnRegistrar = findViewById(R.id.btnRegistrar);

        txtLogin = findViewById(R.id.txtLogin);
    }

    private void configurarSelectorSexo() {

        String[] opcionesSexo = {
                "Hombre",
                "Mujer",
                "Otro",
                "Prefiero no decirlo"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        opcionesSexo
                );

        etSexo.setAdapter(adapter);

        etSexo.setOnClickListener(v -> {
            etSexo.showDropDown();
        });
    }

    private void configurarEventos() {

        btnRegistrar.setOnClickListener(v -> {
            registrarUsuario();
        });

        txtLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegistroActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);

            finish();
        });
    }

    private void registrarUsuario() {

        String nombre =
                etNombre.getText()
                        .toString()
                        .trim();

        String apellido =
                etApellido.getText()
                        .toString()
                        .trim();

        String correo =
                etCorreo.getText()
                        .toString()
                        .trim();

        String password =
                etPassword.getText()
                        .toString()
                        .trim();

        String edad =
                etEdad.getText()
                        .toString()
                        .trim();

        String sexo =
                etSexo.getText()
                        .toString()
                        .trim();

        String peso =
                etPeso.getText()
                        .toString()
                        .trim();

        String altura =
                etAltura.getText()
                        .toString()
                        .trim();

        if (nombre.isEmpty()
                || apellido.isEmpty()
                || correo.isEmpty()
                || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Completa los campos obligatorios",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(correo)
                .matches()) {

            etCorreo.setError(
                    "Ingrese un correo válido"
            );

            etCorreo.requestFocus();

            return;
        }

        btnRegistrar.setEnabled(false);

        Toast.makeText(
                this,
                "Registrando usuario...",
                Toast.LENGTH_SHORT
        ).show();

        enviarRegistro(
                nombre,
                apellido,
                correo,
                password,
                edad,
                sexo,
                peso,
                altura
        );
    }

    private void enviarRegistro(
            String nombre,
            String apellido,
            String correo,
            String password,
            String edad,
            String sexo,
            String peso,
            String altura
    ) {

        StringRequest request =
                new StringRequest(
                        Request.Method.POST,
                        URL_REGISTRO,

                        response -> {

                            btnRegistrar.setEnabled(true);

                            try {

                                JSONObject json =
                                        new JSONObject(response);

                                boolean success =
                                        json.getBoolean("success");

                                String message =
                                        json.getString("message");

                                if (success) {

                                    Toast.makeText(
                                            RegistroActivity.this,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();

                                    Intent intent =
                                            new Intent(
                                                    RegistroActivity.this,
                                                    LoginActivity.class
                                            );

                                    startActivity(intent);

                                    finish();

                                } else {

                                    Toast.makeText(
                                            RegistroActivity.this,
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }

                            } catch (JSONException e) {

                                Toast.makeText(
                                        RegistroActivity.this,
                                        "Respuesta inválida del servidor",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        },

                        error -> {

                            btnRegistrar.setEnabled(true);

                            Toast.makeText(
                                    RegistroActivity.this,
                                    "Error de conexión con el servidor",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                ) {

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params =
                                new HashMap<>();

                        params.put(
                                "nombre",
                                nombre
                        );

                        params.put(
                                "apellido",
                                apellido
                        );

                        params.put(
                                "correo",
                                correo
                        );

                        params.put(
                                "password",
                                password
                        );

                        params.put(
                                "edad",
                                edad
                        );

                        params.put(
                                "sexo",
                                sexo
                        );

                        params.put(
                                "peso",
                                peso
                        );

                        params.put(
                                "altura",
                                altura
                        );

                         return params;
                    }
                };

        RequestQueue queue =
                Volley.newRequestQueue(this);

        queue.add(request);
    }
}