package com.example.proyecto_nutritrak.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_nutritrak.R;

import org.json.JSONObject;

public class ReportesAdminActivity extends AppCompatActivity {

    private TextView txtTotalUsuarios;
    private TextView txtTotalAlimentos;
    private TextView txtTotalConsumos;

    private Button btnVolver;

    private static final String URL_REPORTES =
            "http://10.0.2.2:8001/admin_reportes.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reportes);

        txtTotalUsuarios =
                findViewById(R.id.txtTotalUsuarios);

        txtTotalAlimentos =
                findViewById(R.id.txtTotalAlimentos);

        txtTotalConsumos =
                findViewById(R.id.txtTotalConsumos);

        btnVolver =
                findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(
                v -> finish()
        );

        cargarReportes();
    }

    private void cargarReportes() {

        StringRequest request = new StringRequest(
                Request.Method.GET,
                URL_REPORTES,

                response -> {

                    try {

                        JSONObject json =
                                new JSONObject(response);

                        boolean success =
                                json.optBoolean(
                                        "success",
                                        false
                                );

                        if (!success) {

                            String mensaje =
                                    json.optString(
                                            "message",
                                            "No se pudieron cargar los reportes."
                                    );

                            Toast.makeText(
                                    ReportesAdminActivity.this,
                                    mensaje,
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        int usuarios =
                                json.optInt(
                                        "usuarios",
                                        0
                                );

                        int alimentos =
                                json.optInt(
                                        "alimentos",
                                        0
                                );

                        int consumos =
                                json.optInt(
                                        "consumos",
                                        0
                                );

                        txtTotalUsuarios.setText(
                                "👥 Usuarios registrados\n"
                                        + usuarios
                        );

                        txtTotalAlimentos.setText(
                                "🥗 Alimentos registrados\n"
                                        + alimentos
                        );

                        txtTotalConsumos.setText(
                                "🍽 Consumos registrados\n"
                                        + consumos
                        );

                    } catch (Exception e) {

                        Toast.makeText(
                                ReportesAdminActivity.this,
                                "Error procesando reportes: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> {

                    String mensaje =
                            "No se pudo conectar con la API.";

                    if (error.networkResponse != null) {

                        mensaje +=
                                "\nCódigo HTTP: "
                                        + error.networkResponse.statusCode;
                    }

                    Toast.makeText(
                            ReportesAdminActivity.this,
                            mensaje,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        RequestQueue queue =
                Volley.newRequestQueue(this);

        queue.add(request);
    }
}