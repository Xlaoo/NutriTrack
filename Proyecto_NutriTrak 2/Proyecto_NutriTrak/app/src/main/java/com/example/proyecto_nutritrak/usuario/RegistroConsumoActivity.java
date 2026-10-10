package com.example.proyecto_nutritrak.usuario;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private Button btnRegistrarConsumo;

    private LinearLayout navInicio;
    private LinearLayout navAlimentos;
    private LinearLayout navRegistro;
    private LinearLayout navProgreso;
    private LinearLayout navPerfil;

    private final ArrayList<String> nombresAlimentos = new ArrayList<>();
    private final ArrayList<Integer> idsAlimentos = new ArrayList<>();
    private final Map<String, Integer> mapaAlimentos = new HashMap<>();

    private int alimentoSeleccionadoId = -1;

    private final String URL_ALIMENTOS =
            "http://10.0.2.2/nutritrack_api/listar_alimentos.php";

    private final String URL_REGISTRAR =
            "http://10.0.2.2/nutritrack_api/registrar_consumo.php";

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
        etAlimento = findViewById(R.id.etAlimento);
        etCantidad = findViewById(R.id.etCantidad);
        etUnidad = findViewById(R.id.etUnidad);
        etMomento = findViewById(R.id.etMomento);
        btnRegistrarConsumo = findViewById(R.id.btnRegistrarConsumo);

        navInicio = findViewById(R.id.navInicio);
        navAlimentos = findViewById(R.id.navAlimentos);
        navRegistro = findViewById(R.id.navRegistro);
        navProgreso = findViewById(R.id.navProgreso);
        navPerfil = findViewById(R.id.navPerfil);
    }

    private void configurarBordesSeguros() {
        View root = findViewById(R.id.rootRegistro);
        if (root != null) {
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

        // Permitir desplegar al pulsar cualquier parte del campo
        etAlimento.setOnClickListener(v -> {
            if (nombresAlimentos.isEmpty()) {
                Toast.makeText(this, "No hay alimentos disponibles para seleccionar.", Toast.LENGTH_SHORT).show();
            } else {
                etAlimento.showDropDown();
            }
        });

        etAlimento.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !nombresAlimentos.isEmpty()) {
                etAlimento.showDropDown();
            }
        });

        etUnidad.setOnClickListener(v -> etUnidad.showDropDown());
        etMomento.setOnClickListener(v -> etMomento.showDropDown());

        // Asociación segura entre alimento y su ID, inmune a filtrado
        etAlimento.setOnItemClickListener(
                (parent, view, position, id) -> {
                    String nombreSeleccionado = (String) parent.getItemAtPosition(position);
                    if (mapaAlimentos.containsKey(nombreSeleccionado)) {
                        alimentoSeleccionadoId = mapaAlimentos.get(nombreSeleccionado);
                    } else {
                        alimentoSeleccionadoId = -1;
                    }
                    etAlimento.setError(null);
                }
        );
    }

    private void configurarNavegacion() {
        navInicio.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            pasarUsuario(intent);
            startActivity(intent);
            finish();
        });

        navAlimentos.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlimentosActivity.class);
            pasarUsuario(intent);
            startActivity(intent);
            finish();
        });

        navRegistro.setOnClickListener(v -> Toast.makeText(
                this,
                "Ya estás en Registro",
                Toast.LENGTH_SHORT
        ).show());

        navProgreso.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProgresoActivity.class);
            pasarUsuario(intent);
            startActivity(intent);
            finish();
        });

        navPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            pasarUsuario(intent);
            startActivity(intent);
            finish();
        });
    }

    private void pasarUsuario(Intent intent) {
        intent.putExtra(
                "idUsuario",
                getIntent().getIntExtra("idUsuario", -1)
        );

        intent.putExtra(
                "nombreUsuario",
                getIntent().getStringExtra("nombreUsuario")
        );

        intent.putExtra(
                "apellidoUsuario",
                getIntent().getStringExtra("apellidoUsuario")
        );

        intent.putExtra(
                "correoUsuario",
                getIntent().getStringExtra("correoUsuario")
        );
    }

    private void cargarAlimentos() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_ALIMENTOS,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        if (!json.getBoolean("success")) {
                            Toast.makeText(
                                    this,
                                    json.optString("message", "Error al cargar alimentos"),
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        JSONArray array = json.getJSONArray("alimentos");

                        nombresAlimentos.clear();
                        idsAlimentos.clear();
                        mapaAlimentos.clear();

                        if (array.length() == 0) {
                            Toast.makeText(
                                    this,
                                    "No hay alimentos registrados en el sistema.",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject alimento = array.getJSONObject(i);
                            String nombre = alimento.getString("nombre");
                            int idAlimento = alimento.getInt("id");

                            nombresAlimentos.add(nombre);
                            idsAlimentos.add(idAlimento);
                            mapaAlimentos.put(nombre, idAlimento);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                nombresAlimentos
                        );

                        etAlimento.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Toast.makeText(
                                this,
                                "Error al procesar la lista de alimentos",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },
                error -> Toast.makeText(
                        this,
                        "Error de conexión con el servidor al cargar alimentos",
                        Toast.LENGTH_LONG
                ).show()
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void registrarConsumo(View view) {
        int idUsuario = getIntent().getIntExtra("idUsuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(
                    this,
                    "No se encontró sesión activa de usuario",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        // Validación de alimento
        String nombreAlimentoTexto = etAlimento.getText() != null
                ? etAlimento.getText().toString().trim()
                : "";

        if (nombreAlimentoTexto.isEmpty()) {
            etAlimento.setError("Selecciona un alimento");
            Toast.makeText(this, "Por favor, selecciona un alimento", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reconfirmar correspondencia del alimento
        if (mapaAlimentos.containsKey(nombreAlimentoTexto)) {
            alimentoSeleccionadoId = mapaAlimentos.get(nombreAlimentoTexto);
        }

        if (alimentoSeleccionadoId == -1) {
            etAlimento.setError("Alimento no válido");
            Toast.makeText(this, "Selecciona un alimento válido del menú", Toast.LENGTH_SHORT).show();
            return;
        }
        etAlimento.setError(null);

        // Validación de cantidad: obligatoria, numérica, decimal permitida, > 0
        String cantidad = etCantidad.getText() != null
                ? etCantidad.getText().toString().trim()
                : "";

        if (cantidad.isEmpty()) {
            etCantidad.setError("Ingresa una cantidad");
            etCantidad.requestFocus();
            return;
        }

        double cantidadNumerica;
        try {
            cantidadNumerica = Double.parseDouble(cantidad);
            if (cantidadNumerica <= 0) {
                etCantidad.setError("La cantidad debe ser mayor a 0");
                etCantidad.requestFocus();
                return;
            }
            if (Double.isInfinite(cantidadNumerica) || Double.isNaN(cantidadNumerica)) {
                etCantidad.setError("Cantidad numérica no válida");
                etCantidad.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etCantidad.setError("Ingresa solo números válidos (ej. 150 o 1.5)");
            etCantidad.requestFocus();
            return;
        }
        etCantidad.setError(null);

        // Validación de unidad
        String unidad = etUnidad.getText() != null
                ? etUnidad.getText().toString().trim()
                : "";

        if (unidad.isEmpty()) {
            etUnidad.setError("Selecciona una unidad");
            Toast.makeText(this, "Selecciona una unidad de medida", Toast.LENGTH_SHORT).show();
            return;
        }
        etUnidad.setError(null);

        // Validación de momento
        String momento = etMomento.getText() != null
                ? etMomento.getText().toString().trim()
                : "";

        if (momento.isEmpty()) {
            etMomento.setError("Selecciona el momento");
            Toast.makeText(this, "Selecciona el momento del consumo", Toast.LENGTH_SHORT).show();
            return;
        }
        etMomento.setError(null);

        // Deshabilitar botón para evitar registros duplicados por pulsación múltiple
        if (btnRegistrarConsumo != null) {
            btnRegistrarConsumo.setEnabled(false);
        }

        String fecha = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_REGISTRAR,
                response -> {
                    // Rehabilitar botón
                    if (btnRegistrarConsumo != null) {
                        btnRegistrarConsumo.setEnabled(true);
                    }

                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.optBoolean("success", false);
                        String mensaje = json.optString("message", "Respuesta recibida");

                        Toast.makeText(
                                this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();

                        if (success) {
                            etCantidad.setText("");
                            etAlimento.setText("");
                            etUnidad.setText("");
                            etMomento.setText("");
                            alimentoSeleccionadoId = -1;
                        }

                    } catch (JSONException e) {
                        Toast.makeText(
                                this,
                                "Respuesta del servidor no válida",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },
                error -> {
                    // Rehabilitar botón en caso de error
                    if (btnRegistrarConsumo != null) {
                        btnRegistrarConsumo.setEnabled(true);
                    }

                    String mensajeError = "No se pudo registrar el consumo.";
                    if (error.networkResponse != null) {
                        mensajeError += " Código HTTP: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(
                            this,
                            mensajeError,
                            Toast.LENGTH_LONG
                    ).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuario_id", String.valueOf(idUsuario));
                params.put("alimento_id", String.valueOf(alimentoSeleccionadoId));
                params.put("cantidad", cantidad);
                params.put("unidad", unidad);
                params.put("momento", momento);
                params.put("fecha_consumo", fecha);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}