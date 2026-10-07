package com.example.proyecto_nutritrak;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RecuperarPasswordActivity extends AppCompatActivity {

    // Secciones de la pantalla
    private LinearLayout layoutVerificarCorreo;
    private LinearLayout layoutNuevaPassword;

    // Componentes para verificar correo
    private TextInputEditText edtEmailRecuperar;
    private Button btnVerificarCorreo;
    private ProgressBar progressVerificar;
    private TextView txtVolverLogin;

    // Componentes para nueva contraseña
    private TextView txtCuentaConfirmada;
    private TextInputEditText edtPasswordNuevo;
    private TextInputEditText edtPasswordConfirmar;
    private Button btnActualizarPassword;
    private ProgressBar progressActualizar;
    private TextView txtCambiarCorreo;

    // Variable de estado
    private String correoVerificado = "";

    // URL del endpoint de recuperación directa para entorno local
    private static final String URL_RECUPERAR_DIRECTO =
            "http://10.0.2.2/nutritrack_api/recuperar_directo.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_password);

        inicializarVistas();
        configurarEventos();
    }

    private void inicializarVistas() {
        // Secciones
        layoutVerificarCorreo = findViewById(R.id.layoutVerificarCorreo);
        layoutNuevaPassword = findViewById(R.id.layoutNuevaPassword);

        // Paso 1: Correo
        edtEmailRecuperar = findViewById(R.id.edtEmailRecuperar);
        btnVerificarCorreo = findViewById(R.id.btnVerificarCorreo);
        progressVerificar = findViewById(R.id.progressVerificar);
        txtVolverLogin = findViewById(R.id.txtVolverLogin);

        // Paso 2: Contraseña
        txtCuentaConfirmada = findViewById(R.id.txtCuentaConfirmada);
        edtPasswordNuevo = findViewById(R.id.edtPasswordNuevo);
        edtPasswordConfirmar = findViewById(R.id.edtPasswordConfirmar);
        btnActualizarPassword = findViewById(R.id.btnActualizarPassword);
        progressActualizar = findViewById(R.id.progressActualizar);
        txtCambiarCorreo = findViewById(R.id.txtCambiarCorreo);
    }

    private void configurarEventos() {
        // Evento 1: Verificar existencia del correo
        btnVerificarCorreo.setOnClickListener(v -> verificarCorreoEnServidor());
        txtVolverLogin.setOnClickListener(v -> finish());

        // Evento 2: Actualizar contraseña
        btnActualizarPassword.setOnClickListener(v -> actualizarPasswordEnServidor());
        txtCambiarCorreo.setOnClickListener(v -> volverAIngresoCorreo());
    }

    /**
     * Paso 1: Consultar mediante PHP si el correo existe en la tabla usuarios.
     * Si no existe, muestra el mensaje de error y no permite continuar.
     */
    private void verificarCorreoEnServidor() {
        String email = "";
        if (edtEmailRecuperar.getText() != null) {
            email = edtEmailRecuperar.getText().toString().trim();
        }

        if (email.isEmpty()) {
            edtEmailRecuperar.setError("Ingrese su correo electrónico");
            edtEmailRecuperar.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmailRecuperar.setError("Formato de correo no válido");
            edtEmailRecuperar.requestFocus();
            return;
        }

        final String finalEmail = email;
        btnVerificarCorreo.setEnabled(false);
        progressVerificar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_RECUPERAR_DIRECTO,
                response -> {
                    btnVerificarCorreo.setEnabled(true);
                    progressVerificar.setVisibility(View.GONE);

                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.optBoolean("success", false);
                        String message = json.optString("message", "");

                        if (success) {
                            correoVerificado = finalEmail;
                            String nombre = json.optString("nombre", "");
                            mostrarFormularioNuevaPassword(nombre);
                        } else {
                            // Mostrar mensaje solicitado y bloquear avance
                            edtEmailRecuperar.setError(message);
                            edtEmailRecuperar.requestFocus();
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnVerificarCorreo.setEnabled(true);
                    progressVerificar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accion", "verificar");
                params.put("correo", finalEmail);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * Muestra la sección de nueva contraseña tras verificar la existencia del correo.
     */
    private void mostrarFormularioNuevaPassword(String nombre) {
        layoutVerificarCorreo.setVisibility(View.GONE);
        layoutNuevaPassword.setVisibility(View.VISIBLE);

        if (nombre != null && !nombre.isEmpty()) {
            txtCuentaConfirmada.setText("Cuenta verificada para " + nombre + " (" + correoVerificado + "). Ingresa tu nueva contraseña.");
        } else {
            txtCuentaConfirmada.setText("Cuenta verificada (" + correoVerificado + "). Ingresa tu nueva contraseña.");
        }

        edtPasswordNuevo.setText("");
        edtPasswordConfirmar.setText("");
        edtPasswordNuevo.requestFocus();
    }

    /**
     * Permite al usuario regresar al ingreso de correo.
     */
    private void volverAIngresoCorreo() {
        layoutVerificarCorreo.setVisibility(View.VISIBLE);
        layoutNuevaPassword.setVisibility(View.GONE);
        edtEmailRecuperar.requestFocus();
    }

    /**
     * Paso 2: Validar que ambas contraseñas coincidan y tengan al menos 8 caracteres,
     * y actualizar la clave en MySQL utilizando password_hash().
     */
    private void actualizarPasswordEnServidor() {
        String passNuevo = "";
        String passConfirmar = "";

        if (edtPasswordNuevo.getText() != null) {
            passNuevo = edtPasswordNuevo.getText().toString();
        }
        if (edtPasswordConfirmar.getText() != null) {
            passConfirmar = edtPasswordConfirmar.getText().toString();
        }

        if (passNuevo.length() < 8) {
            edtPasswordNuevo.setError("La contraseña debe tener al menos 8 caracteres");
            edtPasswordNuevo.requestFocus();
            return;
        }

        if (!passNuevo.equals(passConfirmar)) {
            edtPasswordConfirmar.setError("Las contraseñas no coinciden");
            edtPasswordConfirmar.requestFocus();
            return;
        }

        final String finalPassword = passNuevo;
        btnActualizarPassword.setEnabled(false);
        progressActualizar.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_RECUPERAR_DIRECTO,
                response -> {
                    btnActualizarPassword.setEnabled(true);
                    progressActualizar.setVisibility(View.GONE);

                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.optBoolean("success", false);
                        String message = json.optString("message", "");

                        if (success) {
                            Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_LONG).show();
                            // Regresar al inicio de sesión
                            finish();
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnActualizarPassword.setEnabled(true);
                    progressActualizar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("accion", "actualizar");
                params.put("correo", correoVerificado);
                params.put("nuevo_password", finalPassword);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
