package com.example.proyecto_nutritrak;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_nutritrak.admin.AdminActivity;
import com.example.proyecto_nutritrak.usuario.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class  LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;

    private Button btnLogin;
    private Button btnGoogle;

    private CheckBox checkRemember;

    private TextView txtForgot;
    private TextView txtRegister;

    // URL de la API PHP
    private static final String URL_LOGIN =
            "http://10.0.2.2/nutritrack_api/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        inicializarComponentes();

        aplicarAnimacion();

        configurarEventos();
    }

    private void inicializarComponentes() {

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);

        checkRemember = findViewById(R.id.checkRemember);

        txtForgot = findViewById(R.id.txtForgot);
        txtRegister = findViewById(R.id.txtRegister);
    }

    private void aplicarAnimacion() {

        Animation slideUp =
                AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_up
                );

        findViewById(R.id.loginContent)
                .startAnimation(slideUp);
    }

    private void configurarEventos() {

        btnLogin.setOnClickListener(v -> iniciarSesion());

        btnGoogle.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Inicio con Google disponible próximamente",
                    Toast.LENGTH_SHORT
            ).show();

        });

        txtForgot.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RecuperarPasswordActivity.class
            );

            startActivity(intent);

        });

        txtRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegistroActivity.class
            );

            startActivity(intent);

        });
    }

    private void iniciarSesion() {

        String emailTexto = "";
        String passwordTexto = "";

        if (edtEmail.getText() != null) {

            emailTexto = edtEmail.getText()
                    .toString()
                    .trim();
        }

        if (edtPassword.getText() != null) {

            passwordTexto = edtPassword.getText()
                    .toString()
                    .trim();
        }

        // Validar correo
        if (emailTexto.isEmpty()) {

            edtEmail.setError(
                    "Ingrese su correo electrónico"
            );

            edtEmail.requestFocus();

            return;
        }

        // Validar contraseña
        if (passwordTexto.isEmpty()) {

            edtPassword.setError(
                    "Ingrese su contraseña"
            );

            edtPassword.requestFocus();

            return;
        }

        final String email = emailTexto;
        final String password = passwordTexto;

        // Desactivar botón mientras se realiza la petición
        btnLogin.setEnabled(false);

        Toast.makeText(
                this,
                "Verificando datos...",
                Toast.LENGTH_SHORT
        ).show();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_LOGIN,

                response -> {

                    btnLogin.setEnabled(true);

                    try {

                        JSONObject json =
                                new JSONObject(response);

                        boolean success =
                                json.getBoolean("success");

                        String message =
                                json.getString("message");

                        if (success) {

                            JSONObject usuario =
                                    json.getJSONObject("usuario");

                            int id =
                                    usuario.getInt("id");

                            String nombre =
                                    usuario.getString("nombre");

                            String apellido =
                                    usuario.getString("apellido");

                            String correo =
                                    usuario.getString("correo");

                            String rol =
                                    usuario.optString("rol", "usuario");

                            Toast.makeText(
                                    LoginActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();


                            // =================================================
                            // COMPROBAR SI ES ADMINISTRADOR POR ROL EN BD
                            // =================================================

                            if ("administrador".equalsIgnoreCase(rol)) {

                                // =============================================
                                // ENTRAR AL PANEL DE ADMINISTRADOR
                                // =============================================

                                Intent intent =
                                        new Intent(
                                                LoginActivity.this,
                                                AdminActivity.class
                                        );

                                // Enviar datos del administrador
                                intent.putExtra(
                                        "idUsuario",
                                        id
                                );

                                intent.putExtra(
                                        "nombreUsuario",
                                        nombre
                                );

                                intent.putExtra(
                                        "apellidoUsuario",
                                        apellido
                                );

                                intent.putExtra(
                                        "correoUsuario",
                                        correo
                                );

                                startActivity(intent);

                                overridePendingTransition(
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out
                                );

                                finish();

                            } else {

                                // =============================================
                                // ENTRAR COMO USUARIO NORMAL
                                // =============================================

                                Intent intent =
                                        new Intent(
                                                LoginActivity.this,
                                                MainActivity.class
                                        );

                                // Enviar datos del usuario
                                intent.putExtra(
                                        "idUsuario",
                                        id
                                );

                                intent.putExtra(
                                        "nombreUsuario",
                                        nombre
                                );

                                intent.putExtra(
                                        "apellidoUsuario",
                                        apellido
                                );

                                intent.putExtra(
                                        "correoUsuario",
                                        correo
                                );

                                startActivity(intent);

                                overridePendingTransition(
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out
                                );

                                finish();
                            }

                        } else {

                            Toast.makeText(
                                    LoginActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (JSONException e) {

                        btnLogin.setEnabled(true);

                        Toast.makeText(
                                LoginActivity.this,
                                "Respuesta inválida del servidor",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> {

                    btnLogin.setEnabled(true);

                    Toast.makeText(
                            LoginActivity.this,
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
                        "correo",
                        email
                );

                params.put(
                        "password",
                        password
                );

                return params;
            }
        };

        RequestQueue queue =
                Volley.newRequestQueue(this);

        queue.add(request);
    }
}