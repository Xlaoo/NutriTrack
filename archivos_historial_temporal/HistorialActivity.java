package com.example.proyecto_nutritrak;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class HistorialActivity extends Activity {

    private static final String URL_API =
            "http://10.0.2.2:8001/historial.php";

    private LinearLayout contenedorHistorial;
    private TextView txtTotalRegistros;
    private TextView txtEstadoHistorial;
    private ProgressBar progressHistorial;

    private RequestQueue requestQueue;

    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        contenedorHistorial =
                findViewById(R.id.contenedorHistorial);

        txtTotalRegistros =
                findViewById(R.id.txtTotalRegistros);

        txtEstadoHistorial =
                findViewById(R.id.txtEstadoHistorial);

        progressHistorial =
                findViewById(R.id.progressHistorial);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // El ID debe venir de la sesión o del inicio de sesión.
        // El valor 1 es solo para probar la API.
        usuarioId = getIntent().getIntExtra("usuario_id", 1);

        cargarHistorial();
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
                        boolean success =
                                response.optBoolean("success", false);

                        if (!success) {
                            mostrarEstado(
                                    "No se pudo consultar el historial."
                            );
                            return;
                        }

                        JSONArray historial =
                                response.optJSONArray("historial");

                        if (historial == null || historial.length() == 0) {
                            txtTotalRegistros.setText(
                                    "0 alimentos registrados"
                            );

                            mostrarEstado(
                                    "Todavía no tienes alimentos registrados."
                            );
                            return;
                        }

                        int total = historial.length();

                        txtTotalRegistros.setText(
                                total + (total == 1
                                        ? " alimento registrado"
                                        : " alimentos registrados")
                        );

                        for (int i = 0; i < total; i++) {
                            JSONObject alimento =
                                    historial.getJSONObject(i);

                            agregarTarjetaAlimento(alimento);
                        }

                    } catch (Exception e) {
                        mostrarEstado(
                                "Error al procesar los datos recibidos."
                        );
                    }
                },

                error -> {
                    progressHistorial.setVisibility(View.GONE);

                    String mensaje = "No se pudo conectar con el servidor.";

                    if (error.networkResponse != null) {
                        mensaje += " Código HTTP: "
                                + error.networkResponse.statusCode;
                    } else if (error instanceof
                            com.android.volley.TimeoutError) {
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
        tarjeta.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametros.setMargins(0, 0, 0, dp(12));
        tarjeta.setLayoutParams(parametros);

        // Nombre del alimento
        TextView nombre = crearTexto(
                alimento.optString("nombre_alimento", "Alimento"),
                18,
                "#287D55",
                true
        );

        tarjeta.addView(nombre);

        // Tipo de comida
        String tipo = alimento.optString(
                "tipo_comida", "Sin categoría"
        );

        tarjeta.addView(crearTexto(
                "Comida: " + tipo,
                14,
                "#555555",
                false
        ));

        // Cantidad
        String cantidad = alimento.optString("cantidad", "0");
        String unidad = alimento.optString("unidad", "gramos");

        tarjeta.addView(crearTexto(
                "Cantidad: " + cantidad + " " + unidad,
                14,
                "#333333",
                false
        ));

        // Calorías
        tarjeta.addView(crearTexto(
                "Energía: " + alimento.optString("calorias", "0")
                        + " kcal",
                16,
                "#333333",
                true
        ));

        // Macronutrientes
        tarjeta.addView(crearTexto(
                "Proteínas: "
                        + alimento.optString("proteinas", "0") + " g",
                14,
                "#555555",
                false
        ));

        tarjeta.addView(crearTexto(
                "Carbohidratos: "
                        + alimento.optString("carbohidratos", "0") + " g",
                14,
                "#555555",
                false
        ));

        tarjeta.addView(crearTexto(
                "Grasas: "
                        + alimento.optString("grasas", "0") + " g",
                14,
                "#555555",
                false
        ));

        // Fecha del consumo
        tarjeta.addView(crearTexto(
                "Fecha: "
                        + alimento.optString(
                                "fecha_consumo", "Sin fecha"
                        ),
                12,
                "#777777",
                false
        ));

        contenedorHistorial.addView(tarjeta);
    }

    private TextView crearTexto(
            String texto,
            int tamano,
            String color,
            boolean negrita) {

        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextSize(tamano);
        tv.setTextColor(Color.parseColor(color));

        if (negrita) {
            tv.setTypeface(null, Typeface.BOLD);
        }

        tv.setPadding(0, dp(4), 0, dp(4));

        return tv;
    }

    private void mostrarEstado(String mensaje) {
        txtEstadoHistorial.setText(mensaje);
        txtEstadoHistorial.setVisibility(View.VISIBLE);
    }

    private int dp(int valor) {
        return (int) (valor * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (requestQueue != null) {
            requestQueue.cancelAll(request -> true);
        }
    }
}