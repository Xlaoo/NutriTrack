package com.example.proyecto_nutritrak.usuario;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_nutritrak.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlimentosActivity extends AppCompatActivity {

    private LinearLayout contenedorAlimentos;

    private LinearLayout navInicio;
    private LinearLayout navAlimentos;
    private LinearLayout navRegistro;
    private LinearLayout navProgreso;
    private LinearLayout navPerfil;

    private final String URL_ALIMENTOS =
            "http://10.0.2.2/nutritrack_api/listar_alimentos.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        getWindow().setStatusBarColor(Color.parseColor("#F2FBF7"));
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        setContentView(R.layout.activity_alimentos);

        inicializarComponentes();
        configurarBordesSeguros();
        configurarNavegacion();

        cargarAlimentos();
    }

    private void inicializarComponentes() {

        contenedorAlimentos =
                findViewById(R.id.contenedorAlimentos);

        navInicio =
                findViewById(R.id.navInicio);

        navAlimentos =
                findViewById(R.id.navAlimentos);

        navRegistro =
                findViewById(R.id.navRegistro);

        navProgreso =
                findViewById(R.id.navProgreso);

        navPerfil =
                findViewById(R.id.navPerfil);
    }

    private void configurarBordesSeguros() {

        View root = findViewById(R.id.rootAlimentos);

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, windowInsets) -> {

                    Insets systemBars =
                            windowInsets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    view.setPadding(
                            0,
                            systemBars.top,
                            0,
                            0
                    );

                    return windowInsets;
                }
        );

        ViewCompat.requestApplyInsets(root);
    }

    private void configurarNavegacion() {

        navInicio.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AlimentosActivity.this,
                            MainActivity.class
                    );

            pasarUsuario(intent);

            startActivity(intent);
            finish();
        });

        navAlimentos.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Ya estás en Alimentos",
                    Toast.LENGTH_SHORT
            ).show();
        });

        navRegistro.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AlimentosActivity.this,
                            RegistroConsumoActivity.class
                    );

            pasarUsuario(intent);

            startActivity(intent);
            finish();
        });

        navProgreso.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AlimentosActivity.this,
                            ProgresoActivity.class
                    );

            pasarUsuario(intent);

            startActivity(intent);
            finish();
        });

        navPerfil.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AlimentosActivity.this,
                            PerfilActivity.class
                    );

            pasarUsuario(intent);

            startActivity(intent);
            finish();
        });
    }

    private void pasarUsuario(Intent intent) {

        int idUsuario =
                getIntent().getIntExtra(
                        "idUsuario",
                        -1
                );

        String nombre =
                getIntent().getStringExtra(
                        "nombreUsuario"
                );

        String apellido =
                getIntent().getStringExtra(
                        "apellidoUsuario"
                );

        String correo =
                getIntent().getStringExtra(
                        "correoUsuario"
                );

        intent.putExtra(
                "idUsuario",
                idUsuario
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
    }

    private void cargarAlimentos() {

        StringRequest request =
                new StringRequest(
                        Request.Method.POST,
                        URL_ALIMENTOS,

                        response -> {

                            try {

                                JSONObject json =
                                        new JSONObject(response);

                                boolean success =
                                        json.getBoolean("success");

                                if (!success) {

                                    Toast.makeText(
                                            this,
                                            json.optString(
                                                    "message",
                                                    "No se pudieron cargar los alimentos"
                                            ),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                JSONArray alimentos =
                                        json.getJSONArray(
                                                "alimentos"
                                        );

                                contenedorAlimentos.removeAllViews();

                                for (int i = 0;
                                     i < alimentos.length();
                                     i++) {

                                    JSONObject alimento =
                                            alimentos.getJSONObject(i);

                                    agregarTarjetaAlimento(
                                            alimento
                                    );
                                }

                            } catch (JSONException e) {

                                Toast.makeText(
                                        this,
                                        "Error al procesar alimentos",
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
                );

        RequestQueue queue =
                Volley.newRequestQueue(this);

        queue.add(request);
    }

    private void agregarTarjetaAlimento(
            JSONObject alimento
    ) {

        try {

            String nombre =
                    alimento.optString(
                            "nombre",
                            "Alimento"
                    );

            String categoria =
                    alimento.optString(
                            "categoria",
                            "Sin categoría"
                    );

            String descripcion =
                    alimento.optString(
                            "descripcion",
                            "Sin descripción"
                    );

            String calorias =
                    alimento.optString(
                            "calorias",
                            "0"
                    );

            String proteinas =
                    alimento.optString(
                            "proteinas",
                            "0"
                    );

            String carbohidratos =
                    alimento.optString(
                            "carbohidratos",
                            "0"
                    );

            String grasas =
                    alimento.optString(
                            "grasas",
                            "0"
                    );

            String azucares =
                    alimento.optString(
                            "azucares",
                            "0"
                    );

            String sodio =
                    alimento.optString(
                            "sodio",
                            "0"
                    );

            LinearLayout tarjeta =
                    new LinearLayout(this);

            tarjeta.setOrientation(
                    LinearLayout.VERTICAL
            );

            tarjeta.setPadding(
                    35,
                    30,
                    35,
                    30
            );

            tarjeta.setBackgroundColor(
                    Color.WHITE
            );

            LinearLayout.LayoutParams parametros =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            parametros.setMargins(
                    0,
                    0,
                    0,
                    25
            );

            tarjeta.setLayoutParams(parametros);

            TextView titulo =
                    crearTexto(
                            nombre,
                            22,
                            Color.parseColor("#173B2D")
                    );

            tarjeta.addView(titulo);

            TextView txtCategoria =
                    crearTexto(
                            categoria,
                            14,
                            Color.parseColor("#4CAF50")
                    );

            tarjeta.addView(txtCategoria);

            TextView txtDescripcion =
                    crearTexto(
                            descripcion,
                            14,
                            Color.DKGRAY
                    );

            tarjeta.addView(txtDescripcion);

            tarjeta.addView(
                    crearTexto(
                            "🔥 Calorías: " + calorias + " kcal",
                            15,
                            Color.DKGRAY
                    )
            );

            tarjeta.addView(
                    crearTexto(
                            "🥩 Proteínas: " + proteinas + " g",
                            15,
                            Color.DKGRAY
                    )
            );

            tarjeta.addView(
                    crearTexto(
                            "🍚 Carbohidratos: " + carbohidratos + " g",
                            15,
                            Color.DKGRAY
                    )
            );

            tarjeta.addView(
                    crearTexto(
                            "🥑 Grasas: " + grasas + " g",
                            15,
                            Color.DKGRAY
                    )
            );

            tarjeta.addView(
                    crearTexto(
                            "🍬 Azúcares: " + azucares + " g",
                            15,
                            Color.DKGRAY
                    )
            );

            tarjeta.addView(
                    crearTexto(
                            "🧂 Sodio: " + sodio + " mg",
                            15,
                            Color.DKGRAY
                    )
            );

            contenedorAlimentos.addView(
                    tarjeta
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private TextView crearTexto(
            String texto,
            int tamaño,
            int color
    ) {

        TextView textView =
                new TextView(this);

        textView.setText(texto);
        textView.setTextSize(tamaño);
        textView.setTextColor(color);

        textView.setPadding(
                0,
                8,
                0,
                8
        );

        return textView;
    }
}