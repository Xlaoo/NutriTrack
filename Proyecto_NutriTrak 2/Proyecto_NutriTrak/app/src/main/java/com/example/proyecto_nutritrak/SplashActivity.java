package com.example.proyecto_nutritrak;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2500;

    private Handler handler;

    private boolean yaInicioLogin = false;

    private Runnable splashRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        LinearLayout splashContent =
                findViewById(R.id.splashContent);

        Button btnComenzar =
                findViewById(R.id.btnComenzar);

        Animation fadeAnimation =
                AnimationUtils.loadAnimation(
                        this,
                        R.anim.fade_in
                );
        Animation slideAnimation =
                AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_up
                );
        splashContent.startAnimation(fadeAnimation);

        btnComenzar.startAnimation(slideAnimation);
        handler = new Handler();
        splashRunnable = new Runnable() {

            @Override
            public void run() {

                abrirLogin();

            }

        };

        handler.postDelayed(
                splashRunnable,
                SPLASH_TIME
        );
        btnComenzar.setOnClickListener(v -> {

            // Cancelamos la transición automática
            // para evitar abrir el Login dos veces.
            handler.removeCallbacks(
                    splashRunnable
            );

            abrirLogin();
        });
    }
    private void abrirLogin() {

        // Si el Login ya fue abierto,
        // no hacemos nada.
        if (yaInicioLogin) {
            return;
        }

        yaInicioLogin = true;

        Intent intent = new Intent(
                SplashActivity.this,
                LoginActivity.class
        );

        startActivity(intent);

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        finish();

    }
    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (handler != null && splashRunnable != null) {

            handler.removeCallbacks(
                    splashRunnable
            );

        }
    }
}