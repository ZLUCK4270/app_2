package com.ecolim.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ecolim.app.R;
import com.ecolim.app.data.db.dao.UsuarioDao;
import com.ecolim.app.data.model.Usuario;
import com.ecolim.app.util.HashUtil;
import com.ecolim.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;

/**
 * Pantalla de inicio de sesión con validación de credenciales en SQLite local.
 * app/src/main/java/com/ecolim/app/ui/LoginActivity.java
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private UsuarioDao usuarioDao;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioDao = new UsuarioDao(this);
        session = new SessionManager(this);

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        MaterialButton btnLogin = findViewById(R.id.btn_login_entrar);

        btnLogin.setOnClickListener(v -> intentarLogin());
        
        findViewById(R.id.tv_login_registrarse).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void intentarLogin() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String hash = HashUtil.md5(pass);
        Usuario u = usuarioDao.autenticar(email, hash);

        if (u != null) {
            session.guardarSesion(u);
            Toast.makeText(this, "Bienvenido " + u.getNombre(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}
