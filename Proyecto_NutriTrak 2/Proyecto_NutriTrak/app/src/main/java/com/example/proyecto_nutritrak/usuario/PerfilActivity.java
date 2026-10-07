package com.example.proyecto_nutritrak.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_nutritrak.LoginActivity;
import com.example.proyecto_nutritrak.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private TextInputEditText etNombre;
    private TextInputEditText etApellido;
    private TextInputEditText etCorreo;
    private TextInputEditText etEdad;
    private TextInputEditText etPeso;
    private TextInputEditText etAltura;

    private MaterialAutoCompleteTextView etSexo;
    private MaterialAutoCompleteTextView etActividad;
    private MaterialAutoCompleteTextView etObjetivo;

    private Button btnGuardar;
    private Button btnCerrarSesion;

    private LinearLayout navInicio;
    private LinearLayout navAlimentos;
    private LinearLayout navRegistro;
    private LinearLayout navProgreso;
    private LinearLayout navPerfil;

    private int idUsuario;

    private static final String URL_PERFIL =
            "http://10.0.2.2/nutritrack_api/perfil.php";

    private static final String URL_ACTUALIZAR =
            "http://10.0.2.2/nutritrack_api/actualizar_perfil.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_perfil);

        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        if (idUsuario == -1) {

            Toast.makeText(
                    this,
                    "No se encontró el usuario",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        inicializarComponentes();
        configurarSelectores();
        configurarBotones();
        configurarNavegacion();

        cargarPerfil();
    }

    private void inicializarComponentes() {

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etCorreo);
        etEdad = findViewById(R.id.etEdad);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);

        etSexo = findViewById(R.id.etSexo);
        etActividad = findViewById(R.id.etActividad);
        etObjetivo = findViewById(R.id.etObjetivo);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        navInicio = findViewById(R.id.navInicio);
        navAlimentos = findViewById(R.id.navAlimentos);
        navRegistro = findViewById(R.id.navRegistro);
        navProgreso = findViewById(R.id.navProgreso);
        navPerfil = findViewById(R.id.navPerfil);
    }

    private void configurarSelectores() {

        String[] opcionesSexo = {
                "Hombre",
                "Mujer",
                "Otro",
                "Prefiero no decirlo"
        };

        String[] opcionesActividad = {
                "Sedentario",
                "Actividad física media",
                "Físicamente activo"
        };

        String[] opcionesObjetivo = {
                "Mejorar masa muscular",
                "Mantenerme en mi peso",
                "Perder peso"
        };

        ArrayAdapter<String> adapterSexo =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        opcionesSexo
                );

        ArrayAdapter<String> adapterActividad =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        opcionesActividad
                );

        ArrayAdapter<String> adapterObjetivo =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        opcionesObjetivo
                );

        etSexo.setAdapter(adapterSexo);
        etActividad.setAdapter(adapterActividad);
        etObjetivo.setAdapter(adapterObjetivo);

        etSexo.setOnClickListener(v ->
                etSexo.showDropDown()
        );

        etActividad.setOnClickListener(v ->
                etActividad.showDropDown()
        );

        etObjetivo.setOnClickListener(v ->
                etObjetivo.showDropDown()
        );
    }

    private void configurarBotones() {

        btnGuardar.setOnClickListener(v ->
                actualizarPerfil()
        );

        btnCerrarSesion.setOnClickListener(v ->
                cerrarSesion()
        );
    }

    private void configurarNavegacion() {

        navInicio.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    MainActivity.class
            );

            intent.putExtra("idUsuario", idUsuario);

            startActivity(intent);
            finish();
        });

        navAlimentos.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    AlimentosActivity.class
            );

            intent.putExtra("idUsuario", idUsuario);

            startActivity(intent);
        });

        navRegistro.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    RegistroConsumoActivity.class
            );

            intent.putExtra("idUsuario", idUsuario);

            startActivity(intent);
        });

        navProgreso.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PerfilActivity.this,
                    ProgresoActivity.class
            );

            intent.putExtra("idUsuario", idUsuario);

            startActivity(intent);
        });

        navPerfil.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Ya estás en Perfil",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void cargarPerfil() {

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_PERFIL,

                response -> {

                    try {

                        JSONObject json =
                                new JSONObject(response);

                        boolean success =
                                json.getBoolean("success");

                        if (success) {

                            JSONObject usuario =
                                    json.getJSONObject("usuario");

                            etNombre.setText(
                                    usuario.optString("nombre", "")
                            );

                            etApellido.setText(
                                    usuario.optString("apellido", "")
                            );

                            etCorreo.setText(
                                    usuario.optString("correo", "")
                            );

                            etEdad.setText(
                                    usuario.optString("edad", "")
                            );

                            etSexo.setText(
                                    usuario.optString("sexo", ""),
                                    false
                            );

                            etPeso.setText(
                                    usuario.optString("peso", "")
                            );

                            etAltura.setText(
                                    usuario.optString("altura", "")
                            );

                            etActividad.setText(
                                    usuario.optString(
                                            "nivel_actividad",
                                            ""
                                    ),
                                    false
                            );

                            etObjetivo.setText(
                                    usuario.optString(
                                            "objetivo_alimentacion",
                                            ""
                                    ),
                                    false
                            );

                        } else {

                            Toast.makeText(
                                    this,
                                    json.optString(
                                            "message",
                                            "No se pudo cargar el perfil"
                                    ),
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (JSONException e) {

                        Toast.makeText(
                                this,
                                "Error procesando el perfil",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> {

                    Toast.makeText(
                            this,
                            "No se pudo conectar con el servidor",
                            Toast.LENGTH_LONG
                    ).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params =
                        new HashMap<>();

                params.put(
                        "idUsuario",
                        String.valueOf(idUsuario)
                );

                return params;
            }
        };

        RequestQueue queue =
                Volley.newRequestQueue(this);

        queue.add(request);
    }

    private void actualizarPerfil() {

        String nombre =
                etNombre.getText().toString().trim();

        String apellido =
                etApellido.getText().toString().trim();

        String correo =
                etCorreo.getText().toString().trim();

        String edad =
                etEdad.getText().toString().trim();

        String sexo =
                etSexo.getText().toString().trim();

        String peso =
                etPeso.getText().toString().trim();

        String altura =
                etAltura.getText().toString().trim();

        String actividad =
                etActividad.getText().toString().trim();

        String objetivo =
                etObjetivo.getText().toString().trim();

        if (nombre.isEmpty()) {

            etNombre.setError("Ingrese su nombre");
            etNombre.requestFocus();
            return;
        }

        if (apellido.isEmpty()) {

            etApellido.setError("Ingrese su apellido");
            etApellido.requestFocus();
            return;
        }

        if (correo.isEmpty()) {

            etCorreo.setError("Ingrese su correo");
            etCorreo.requestFocus();
            return;
        }

        btnGuardar.setEnabled(false);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_ACTUALIZAR,

                response -> {

                    btnGuardar.setEnabled(true);

                    try {

                        JSONObject json =
                                new JSONObject(response);

                        boolean success =
                                json.getBoolean("success");

                        String message =
                                json.optString(
                                        "message",
                                        "Operación completada"
                                );

                        Toast.makeText(
                                this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                    } catch (JSONException e) {

                        Toast.makeText(
                                this,
                                "Respuesta inválida del servidor",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> {

                    btnGuardar.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Error al actualizar el perfil",
                            Toast.LENGTH_LONG
                    ).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params =
                        new HashMap<>();

                params.put(
                        "idUsuario",
                        String.valueOf(idUsuario)
                );

                params.put("nombre", nombre);
                params.put("apellido", apellido);
                params.put("correo", correo);
                params.put("edad", edad);
                params.put("sexo", sexo);
                params.put("peso", peso);
                params.put("altura", altura);

                params.put(
                        "nivel_actividad",
                        actividad
                );

                params.put(
                        "objetivo_alimentacion",
                        objetivo
                );

                return params;
            }
        };

        RequestQueue queue =
                Volley.newRequestQueue(this);

        queue.add(request);
    }

    private void cerrarSesion() {

        Intent intent = new Intent(
                PerfilActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}