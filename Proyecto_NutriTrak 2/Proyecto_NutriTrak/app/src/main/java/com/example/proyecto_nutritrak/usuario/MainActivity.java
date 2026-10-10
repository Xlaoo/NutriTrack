package com.example.proyecto_nutritrak.usuario;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_nutritrak.R;

public class MainActivity extends AppCompatActivity {

    private TextView txtHola;

    private LinearLayout navInicio;
    private LinearLayout navAlimentos;
    private LinearLayout navRegistro;
    private LinearLayout navProgreso;
    private LinearLayout navPerfil;
    private LinearLayout btnHistorialAlimentacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Window window = getWindow();

        window.setStatusBarColor(
                Color.parseColor("#F2FBF7")
        );

        window.setNavigationBarColor(
                Color.parseColor("#FFFFFF")
        );

        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        WindowCompat.setDecorFitsSystemWindows(
                window,
                false
        );


        setContentView(R.layout.activity_main);

        inicializarComponentes();

        configurarBordesSeguros();

        mostrarNombreUsuario();

        configurarNavegacion();
    }

    private void configurarBordesSeguros() {

        View root =
                findViewById(R.id.mainRoot);

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

    private void inicializarComponentes() {

        txtHola =
                findViewById(R.id.txtHola);

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

        btnHistorialAlimentacion =
                findViewById(R.id.btnHistorialAlimentacion);
    }


    private void mostrarNombreUsuario() {

        String nombre =
                getIntent().getStringExtra(
                        "nombre"
                );

        if (nombre != null
                && !nombre.isEmpty()) {

            txtHola.setText(
                    "Hola, " + nombre + " 👋"
            );

        } else {

            txtHola.setText(
                    "Hola 👋"
            );
        }
    }

    private void configurarNavegacion() {


        navInicio.setOnClickListener(v -> {


        });

        if (btnHistorialAlimentacion != null) {
            btnHistorialAlimentacion.setOnClickListener(v -> {
                Intent intent =
                        new Intent(
                                MainActivity.this,
                                HistorialActivity.class
                        );
                enviarDatosUsuario(intent);
                startActivity(intent);
            });
        }

        navAlimentos.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            AlimentosActivity.class
                    );

            enviarDatosUsuario(intent);

            startActivity(intent);
        });

        navRegistro.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            RegistroConsumoActivity.class
                    );

            enviarDatosUsuario(intent);

            startActivity(intent);
        });

        navProgreso.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            ProgresoActivity.class
                    );

            enviarDatosUsuario(intent);

            startActivity(intent);
        });


        navPerfil.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            PerfilActivity.class
                    );

            enviarDatosUsuario(intent);

            startActivity(intent);
        });
    }

    private void enviarDatosUsuario(Intent intent) {

        // ID

        int idUsuario =
                getIntent().getIntExtra(
                        "idUsuario",
                        -1
                );
        String nombreUsuario =
                getIntent().getStringExtra(
                        "nombreUsuario"
                );

        String apellidoUsuario =
                getIntent().getStringExtra(
                        "apellidoUsuario"
                );

        // Correo

        String correoUsuario =
                getIntent().getStringExtra(
                        "correoUsuario"
                );


        intent.putExtra(
                "idUsuario",
                idUsuario
        );

        intent.putExtra(
                "nombreUsuario",
                nombreUsuario
        );

        intent.putExtra(
                "apellidoUsuario",
                apellidoUsuario
        );

        intent.putExtra(
                "correoUsuario",
                correoUsuario
        );
    }
}