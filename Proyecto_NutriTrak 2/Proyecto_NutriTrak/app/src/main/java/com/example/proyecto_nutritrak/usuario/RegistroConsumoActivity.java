package com.example.proyecto_nutritrak.usuario;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegistroConsumoActivity extends AppCompatActivity {

    private MaterialAutoCompleteTextView etAlimento;
    private MaterialAutoCompleteTextView etUnidad;
    private MaterialAutoCompleteTextView etMomento;

    private TextInputEditText etCantidad;

    private LinearLayout navInicio;
    private LinearLayout navAlimentos;
    private LinearLayout navRegistro;
    private LinearLayout navProgreso;
    private LinearLayout navPerfil;

    private final ArrayList<String> nombresAlimentos =
            new ArrayList<>();

    private final ArrayList<Integer> idsAlimentos =
            new ArrayList<>();

    private int alimentoSeleccionadoId = -1;

    private final String URL_ALIMENTOS =
            "http://10.0.2.2:8001/listar_alimentos.php";

    private final String URL_REGISTRAR =
            "http://10.0.2.2:8001/registrar_consumo.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(
                getWindow(),
                false
        );

        getWindow().setStatusBarColor(
                Color.parseColor("#F2FBF7")
        );

        getWindow().setNavigationBarColor(
                Color.WHITE
        );

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        setContentView(
                R.layout.activity_registro_consumo
        );

        inicializarComponentes();
        configurarBordesSeguros();
        configurarSelectores();
        configurarNavegacion();
        cargarAlimentos();
    }

    private void inicializarComponentes() {

        etAlimento =
                findViewById(R.id.etAlimento);

        etCantidad =
                findViewById(R.id.etCantidad);

        etUnidad =
                findViewById(R.id.etUnidad);

        etMomento =
                findViewById(R.id.etMomento);

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

        View root =
                findViewById(R.id.rootRegistro);

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

    private void configurarSelectores() {

        String[] unidades = {
                "gramos",
                "mililitros",
                "unidad",
                "porción",
                "taza"
        };

        String[] momentos = {
                "Desayuno",
                "Almuerzo",
                "Cena",
                "Merienda",
                "Snack"
        };

        ArrayAdapter<String> adapterUnidad =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        unidades
                );

        ArrayAdapter<String> adapterMomento =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        momentos
                );

        etUnidad.setAdapter(adapterUnidad);
        etMomento.setAdapter(adapterMomento);

        etUnidad.setOnClickListener(
                v -> etUnidad.showDropDown()
        );

        etMomento.setOnClickListener(
                v -> etMomento.showDropDown()
        );

        etAlimento.setOnItemClickListener(
                (parent, view, position, id) -> {

                    alimentoSeleccionadoId =
                            idsAlimentos.get(position);
                }
        );
    }

    private void configurarNavegacion() {

        navInicio.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            this,
                            MainActivity.class
                    );

            pasarUsuario(intent);

            startActivity(intent);
            finish();
        });

        navAlimentos.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            this,
                            AlimentosActivity.class
                    );

            pasarUsuario(intent);

            startActivity(intent);
            finish();
        });

        navRegistro.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Ya estás en Registro",
                    Toast.LENGTH_SHORT
            ).show();
        });

        navProgreso.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            this,
                            ProgresoActivity.class
                    );

            pasarUsuario(intent);

            startActivity(intent);
            finish();
        });

        navPerfil.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            this,
                            PerfilActivity.class
                    );

            pasarUsuario(intent);

            startActivity(intent);
            finish();
        });
    }

    private void pasarUsuario(Intent intent) {

        intent.putExtra(
                "idUsuario",
                getIntent().getIntExtra(
                        "idUsuario",
                        -1
                )
        );

        intent.putExtra(
                "nombreUsuario",
                getIntent().getStringExtra(
                        "nombreUsuario"
                )
        );

        intent.putExtra(
                "apellidoUsuario",
                getIntent().getStringExtra(
                        "apellidoUsuario"
                )
        );

        intent.putExtra(
                "correoUsuario",
                getIntent().getStringExtra(
                        "correoUsuario"
                )
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

                                if (!json.getBoolean(
                                        "success"
                                )) {

                                    Toast.makeText(
                                            this,
                                            json.optString(
                                                    "message"
                                            ),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                JSONArray array =
                                        json.getJSONArray(
                                                "alimentos"
                                        );

                                nombresAlimentos.clear();
                                idsAlimentos.clear();

                                for (
                                        int i = 0;
                                        i < array.length();
                                        i++
                                ) {

                                    JSONObject alimento =
                                            array.getJSONObject(i);

                                    nombresAlimentos.add(
                                            alimento.getString(
                                                    "nombre"
                                            )
                                    );

                                    idsAlimentos.add(
                                            alimento.getInt(
                                                    "id"
                                            )
                                    );
                                }

                                ArrayAdapter<String> adapter =
                                        new ArrayAdapter<>(
                                                this,
                                                android.R.layout.simple_dropdown_item_1line,
                                                nombresAlimentos
                                        );

                                etAlimento.setAdapter(
                                        adapter
                                );

                            } catch (JSONException e) {

                                Toast.makeText(
                                        this,
                                        "Error leyendo alimentos",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        },

                        error -> Toast.makeText(
                                this,
                                "Error de conexión",
                                Toast.LENGTH_LONG
                        ).show()
                );

        RequestQueue queue =
                Volley.newRequestQueue(this);

        queue.add(request);
    }

    public void registrarConsumo(View view) {

        int idUsuario =
                getIntent().getIntExtra(
                        "idUsuario",
                        -1
                );

        String cantidad =
                etCantidad.getText()
                        .toString()
                        .trim();

        String unidad =
                etUnidad.getText()
                        .toString()
                        .trim();

        String momento =
                etMomento.getText()
                        .toString()
                        .trim();

        if (idUsuario == -1) {

            Toast.makeText(
                    this,
                    "No se encontró el usuario",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (alimentoSeleccionadoId == -1) {

            Toast.makeText(
                    this,
                    "Selecciona un alimento",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (cantidad.isEmpty()) {

            etCantidad.setError(
                    "Ingresa una cantidad"
            );

            return;
        }

        if (unidad.isEmpty()) {

            Toast.makeText(
                    this,
                    "Selecciona una unidad",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (momento.isEmpty()) {

            Toast.makeText(
                    this,
                    "Selecciona el momento",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String fecha =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        StringRequest request =
                new StringRequest(
                        Request.Method.POST,
                        URL_REGISTRAR,

                        response -> {

                            try {

                                JSONObject json =
                                        new JSONObject(response);

                                Toast.makeText(
                                        this,
                                        json.optString(
                                                "message"
                                        ),
                                        Toast.LENGTH_LONG
                                ).show();

                                if (json.getBoolean(
                                        "success"
                                )) {

                                    etCantidad.setText("");
                                    etAlimento.setText("");
                                    etUnidad.setText("");
                                    etMomento.setText("");

                                    alimentoSeleccionadoId =
                                            -1;
                                }

                            } catch (JSONException e) {

                                Toast.makeText(
                                        this,
                                        "Respuesta inválida",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        },

                        error -> Toast.makeText(
                                this,
                                "No se pudo registrar el consumo",
                                Toast.LENGTH_LONG
                        ).show()
                ) {

                    @Override
                    protected Map<String, String>
                    getParams() {

                        Map<String, String> params =
                                new HashMap<>();

                        params.put(
                                "usuario_id",
                                String.valueOf(
                                        idUsuario
                                )
                        );

                        params.put(
                                "alimento_id",
                                String.valueOf(
                                        alimentoSeleccionadoId
                                )
                        );

                        params.put(
                                "cantidad",
                                cantidad
                        );

                        params.put(
                                "unidad",
                                unidad
                        );

                        params.put(
                                "momento",
                                momento
                        );

                        params.put(
                                "fecha_consumo",
                                fecha
                        );

                        return params;
                    }
                };

        Volley.newRequestQueue(this)
                .add(request);
    }
}