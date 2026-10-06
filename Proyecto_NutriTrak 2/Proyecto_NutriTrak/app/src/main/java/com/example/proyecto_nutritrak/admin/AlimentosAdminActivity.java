package com.example.proyecto_nutritrak.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto_nutritrak.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AlimentosAdminActivity extends AppCompatActivity {

    private ListView listaAlimentos;
    private Button btnVolver;

    private ArrayList<String> alimentos;
    private ArrayAdapter<String> adapter;

    private static final String URL_ALIMENTOS =
            "http://10.0.2.2:8001/admin_alimentos.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alimentos);

        listaAlimentos =
                findViewById(R.id.listaAlimentos);

        btnVolver =
                findViewById(R.id.btnVolver);

        alimentos = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                alimentos
        );

        listaAlimentos.setAdapter(adapter);

        btnVolver.setOnClickListener(v -> finish());

        cargarAlimentos();
    }

    private void cargarAlimentos() {

        StringRequest request = new StringRequest(
                Request.Method.GET,
                URL_ALIMENTOS,

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
                                            "No se pudieron cargar los alimentos."
                                    );

                            Toast.makeText(
                                    AlimentosAdminActivity.this,
                                    mensaje,
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        JSONArray lista =
                                json.optJSONArray("alimentos");

                        alimentos.clear();

                        if (lista != null) {

                            for (int i = 0;
                                 i < lista.length();
                                 i++) {

                                JSONObject alimento =
                                        lista.getJSONObject(i);

                                String id =
                                        alimento.optString(
                                                "id",
                                                ""
                                        );

                                String nombre =
                                        alimento.optString(
                                                "nombre",
                                                ""
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

                                String datos =
                                        "ID: " + id +
                                                "\n🥗 " + nombre +
                                                "\n🔥 Calorías: "
                                                + calorias + " kcal" +
                                                "\n💪 Proteínas: "
                                                + proteinas + " g" +
                                                "\n🍞 Carbohidratos: "
                                                + carbohidratos + " g" +
                                                "\n🥑 Grasas: "
                                                + grasas + " g";

                                alimentos.add(datos);
                            }
                        }

                        adapter.notifyDataSetChanged();

                        if (alimentos.isEmpty()) {

                            Toast.makeText(
                                    AlimentosAdminActivity.this,
                                    "No hay alimentos registrados.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (Exception e) {

                        Toast.makeText(
                                AlimentosAdminActivity.this,
                                "Error procesando alimentos: "
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
                            AlimentosAdminActivity.this,
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