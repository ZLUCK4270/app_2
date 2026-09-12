package com.ecolim.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.ecolim.app.R;
import com.ecolim.app.util.SessionManager;

/**
 * Pantalla Splash de bienvenida con verificación de sesión del técnico de campo.
 * app/src/main/java/com/ecolim/app/ui/SplashActivity.java
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = new SessionManager(this);
            if (session.estaLogueado()) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1200);
    }
}
