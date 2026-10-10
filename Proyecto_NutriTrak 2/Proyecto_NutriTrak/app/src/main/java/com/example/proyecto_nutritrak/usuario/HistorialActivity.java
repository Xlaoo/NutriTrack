package com.example.proyecto_nutritrak.usuario;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_nutritrak.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class HistorialActivity extends AppCompatActivity {

    private static final String URL_API =
            "http://10.0.2.2/nutritrack_api/historial.php";

    private LinearLayout contenedorHistorial;
    private TextView txtTotalRegistros;
    private TextView txtEstadoHistorial;
    private ProgressBar progressHistorial;
    private TextView btnVolver;

    private RequestQueue requestQueue;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configurarVentana();
        setContentView(R.layout.activity_historial);

        inicializarComponentes();
        configurarBordesSeguros();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Obtener ID del usuario autenticado sin usar valores fijos
        usuarioId = getIntent().getIntExtra("idUsuario", -1);

        if (usuarioId == -1) {
            Toast.makeText(
                    this,
                    "Sesión no válida. Vuelva a iniciar sesión.",
                    Toast.LENGTH_LONG
            ).show();
            mostrarEstado("No se encontró una sesión activa.");
            return;
        }

        btnVolver.setOnClickListener(v -> finish());

        cargarHistorial();
    }

    private void configurarVentana() {
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#F2FBF7"));
        window.setNavigationBarColor(Color.WHITE);

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        WindowCompat.setDecorFitsSystemWindows(window, false);
    }

    private void inicializarComponentes() {
        contenedorHistorial = findViewById(R.id.contenedorHistorial);
        txtTotalRegistros = findViewById(R.id.txtTotalRegistros);
        txtEstadoHistorial = findViewById(R.id.txtEstadoHistorial);
        progressHistorial = findViewById(R.id.progressHistorial);
        btnVolver = findViewById(R.id.btnVolver);
    }

    private void configurarBordesSeguros() {
        View root = findViewById(R.id.rootHistorial);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                    root,
                    (view, windowInsets) -> {
                        Insets systemBars = windowInsets.getInsets(
                                WindowInsetsCompat.Type.systemBars()
                        );
                        view.setPadding(0, systemBars.top, 0, 0);
                        return windowInsets;
                    }
            );
            ViewCompat.requestApplyInsets(root);
        }
    }

    private void cargarHistorial() {
        progressHistorial.setVisibility(View.VISIBLE);
        txtEstadoHistorial.setVisibility(View.GONE);
        contenedorHistorial.removeAllViews();

        String url = URL_API + "?usuario_id=" + usuarioId;

        JsonObjectRequest solicitud = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressHistorial.setVisibility(View.GONE);

                    try {
                        boolean success = response.optBoolean("success", false);

                        if (!success) {
                            String errorMsg = response.optString("error", "No se pudo consultar el historial.");
                            mostrarEstado(errorMsg);
                            return;
                        }

                        JSONArray historial = response.optJSONArray("historial");

                        if (historial == null || historial.length() == 0) {
                            txtTotalRegistros.setText("0 alimentos registrados");
                            mostrarEstado("Todavía no tienes alimentos registrados.");
                            return;
                        }

                        int total = historial.length();
                        txtTotalRegistros.setText(
                                total + (total == 1
                                        ? " alimento registrado"
                                        : " alimentos registrados")
                        );

                        for (int i = 0; i < total; i++) {
                            JSONObject alimento = historial.getJSONObject(i);
                            agregarTarjetaAlimento(alimento);
                        }

                    } catch (Exception e) {
                        mostrarEstado("Error al procesar los datos recibidos.");
                    }
                },
                error -> {
                    progressHistorial.setVisibility(View.GONE);

                    String mensaje = "No se pudo conectar con el servidor.";
                    if (error.networkResponse != null) {
                        mensaje += " Código HTTP: " + error.networkResponse.statusCode;
                    } else if (error instanceof com.android.volley.TimeoutError) {
                        mensaje = "El servidor tardó demasiado en responder.";
                    }

                    mostrarEstado(mensaje);
                }
        );

        requestQueue.add(solicitud);
    }

    private void agregarTarjetaAlimento(JSONObject alimento) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(dp(16), dp(16), dp(16), dp(16));
        tarjeta.setBackgroundResource(R.drawable.bg_card);

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parametros.setMargins(0, 0, 0, dp(12));
        tarjeta.setLayoutParams(parametros);

        // Nombre del alimento
        String nombreAlimento = alimento.optString("nombre_alimento", "Alimento");
        tarjeta.addView(crearTexto(nombreAlimento, 18, "#17332E", true));

        // Tipo de comida
        String tipo = alimento.optString("tipo_comida", "General");
        tarjeta.addView(crearTexto("Momento: " + tipo, 14, "#16A085", true));

        // Cantidad y unidad
        String cantidad = alimento.optString("cantidad", "0");
        String unidad = alimento.optString("unidad", "gramos");
        tarjeta.addView(crearTexto("Porción: " + cantidad + " " + unidad, 14, "#4A5568", false));

        // Calorías
        String calorias = alimento.optString("calorias", "0");
        tarjeta.addView(crearTexto("🔥 Energía: " + calorias + " kcal", 15, "#17332E", true));

        // Macronutrientes
        String proteinas = alimento.optString("proteinas", "0");
        String carbohidratos = alimento.optString("carbohidratos", "0");
        String grasas = alimento.optString("grasas", "0");

        tarjeta.addView(crearTexto("🥩 Proteínas: " + proteinas + " g", 13, "#71847F", false));
        tarjeta.addView(crearTexto("🍚 Carbohidratos: " + carbohidratos + " g", 13, "#71847F", false));
        tarjeta.addView(crearTexto("🥑 Grasas: " + grasas + " g", 13, "#71847F", false));

        // Fecha de consumo
        String fecha = alimento.optString("fecha_consumo", "Sin fecha");
        tarjeta.addView(crearTexto("📅 Fecha: " + fecha, 12, "#A0AEC0", false));

        contenedorHistorial.addView(tarjeta);
    }

    private TextView crearTexto(String texto, int tamano, String colorHex, boolean negrita) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextSize(tamano);
        tv.setTextColor(Color.parseColor(colorHex));
        if (negrita) {
            tv.setTypeface(null, Typeface.BOLD);
        }
        tv.setPadding(0, dp(2), 0, dp(2));
        return tv;
    }

    private void mostrarEstado(String mensaje) {
        txtEstadoHistorial.setText(mensaje);
        txtEstadoHistorial.setVisibility(View.VISIBLE);
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(request -> true);
        }
    }
}
