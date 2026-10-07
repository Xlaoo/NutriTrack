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

public class UsuariosAdminActivity extends AppCompatActivity {

    private ListView listaUsuarios;
    private Button btnVolver;

    private ArrayList<String> usuarios;
    private ArrayAdapter<String> adapter;

    private static final String URL_USUARIOS =
            "http://10.0.2.2/nutritrack_api/admin_usuarios.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_usuarios_admin);

        listaUsuarios = findViewById(R.id.listaUsuarios);
        btnVolver = findViewById(R.id.btnVolver);

        usuarios = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                usuarios
        );

        listaUsuarios.setAdapter(adapter);

        btnVolver.setOnClickListener(v -> finish());

        cargarUsuarios();
    }

    private void cargarUsuarios() {

        StringRequest request = new StringRequest(
                Request.Method.GET,
                URL_USUARIOS,

                response -> {

                    try {

                        JSONObject json =
                                new JSONObject(response);

                        boolean success =
                                json.optBoolean("success", false);

                        if (!success) {

                            String mensaje =
                                    json.optString(
                                            "message",
                                            "No se pudieron cargar los usuarios."
                                    );

                            Toast.makeText(
                                    UsuariosAdminActivity.this,
                                    mensaje,
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        JSONArray lista =
                                json.optJSONArray("usuarios");

                        usuarios.clear();

                        if (lista != null) {

                            for (int i = 0; i < lista.length(); i++) {

                                JSONObject usuario =
                                        lista.getJSONObject(i);

                                String id =
                                        usuario.optString("id", "");

                                String nombre =
                                        usuario.optString("nombre", "");

                                String apellido =
                                        usuario.optString("apellido", "");

                                String correo =
                                        usuario.optString("correo", "");

                                String edad =
                                        usuario.optString("edad", "");

                                String sexo =
                                        usuario.optString("sexo", "");

                                String peso =
                                        usuario.optString("peso", "");

                                String altura =
                                        usuario.optString("altura", "");

                                String rol =
                                        usuario.optString(
                                                "rol",
                                                "usuario"
                                        );

                                String fecha =
                                        usuario.optString(
                                                "fecha_registro",
                                                ""
                                        );

                                String datos =
                                        "ID: " + id +
                                                "\n👤 " + nombre + " " + apellido +
                                                "\n📧 " + correo +
                                                "\n🎂 Edad: " + edad +
                                                "\n⚥ Sexo: " + sexo +
                                                "\n⚖ Peso: " + peso + " kg" +
                                                "\n📏 Altura: " + altura + " cm" +
                                                "\n🔐 Rol: " + rol +
                                                "\n📅 Registro: " + fecha;

                                usuarios.add(datos);
                            }
                        }

                        adapter.notifyDataSetChanged();

                        if (usuarios.isEmpty()) {

                            Toast.makeText(
                                    UsuariosAdminActivity.this,
                                    "No hay usuarios registrados.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (Exception e) {

                        Toast.makeText(
                                UsuariosAdminActivity.this,
                                "Error procesando la respuesta: "
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
                            UsuariosAdminActivity.this,
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