package com.ecolim.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.ecolim.app.R;
import com.ecolim.app.data.db.dao.UsuarioDao;
import com.ecolim.app.data.model.Usuario;
import com.ecolim.app.util.HashUtil;
import com.ecolim.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

/**
 * Pantalla de registro de nuevos usuarios en SQLite local.
 * app/src/main/java/com/ecolim/app/ui/RegisterActivity.java
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword, etPasswordConfirm, etDni;
    private Spinner spinnerRol, spinnerTurno, spinnerSede;
    private ShapeableImageView ivFoto;
    private String fotoUriStr = "";
    private UsuarioDao usuarioDao;
    private SessionManager session;
    private ActivityResultLauncher<Intent> photoPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usuarioDao = new UsuarioDao(this);
        session = new SessionManager(this);

        etNombre = findViewById(R.id.et_register_nombre);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        etPasswordConfirm = findViewById(R.id.et_register_password_confirm);
        etDni = findViewById(R.id.et_register_dni);
        spinnerRol = findViewById(R.id.spinner_register_rol);
        spinnerTurno = findViewById(R.id.spinner_register_turno);
        spinnerSede = findViewById(R.id.spinner_register_sede);
        ivFoto = findViewById(R.id.iv_register_foto);
        findViewById(R.id.fab_register_foto).setOnClickListener(v -> pickImage());
        
        MaterialButton btnCrear = findViewById(R.id.btn_register_crear);
        TextView tvYaTengoCuenta = findViewById(R.id.tv_register_ya_tengo_cuenta);

        // Configurar Spinners
        String[] roles = {"Técnico Ambiental", "Supervisor", "Administrador"};
        ArrayAdapter<String> rolAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        rolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(rolAdapter);

        String[] turnos = {"Turno Matutino", "Turno Vespertino", "Turno Nocturno"};
        ArrayAdapter<String> turnoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, turnos);
        turnoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurno.setAdapter(turnoAdapter);

        String[] sedes = {"Sede Norte", "Sede Sur", "Sede Central", "Sede Este"};
        ArrayAdapter<String> sedeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sedes);
        sedeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSede.setAdapter(sedeAdapter);

        btnCrear.setOnClickListener(v -> intentarRegistro());
        
        tvYaTengoCuenta.setOnClickListener(v -> finish());

        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            fotoUriStr = imageUri.toString();
                            ivFoto.setImageURI(imageUri);
                        }
                    }
                }
        );
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerLauncher.launch(intent);
    }

    private void intentarRegistro() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String passConfirm = etPasswordConfirm.getText().toString().trim();
        String rol = spinnerRol.getSelectedItem().toString();
        String turno = spinnerTurno.getSelectedItem().toString();
        String dni = etDni.getText().toString().trim();
        String sede = spinnerSede.getSelectedItem().toString();

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(passConfirm)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        String hash = HashUtil.md5(pass);
        Usuario u = usuarioDao.registrar(nombre, email, hash, rol, turno, dni, sede, fotoUriStr);

        if (u != null) {
            session.guardarSesion(u);
            Toast.makeText(this, "Cuenta creada. Bienvenido " + u.getNombre(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        } else {
            Toast.makeText(this, "El correo ya está registrado", Toast.LENGTH_SHORT).show();
        }
    }
}
