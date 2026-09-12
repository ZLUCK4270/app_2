package com.ecolim.app.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ecolim.app.R;
import com.ecolim.app.data.db.dao.UsuarioDao;
import com.ecolim.app.data.model.Usuario;
import com.ecolim.app.util.HashUtil;
import com.ecolim.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

public class PerfilFragment extends Fragment {

    private EditText etNombre, etDni, etPassActual, etPassNueva;
    private Spinner spinnerSede;
    private ShapeableImageView ivFoto;
    
    private SessionManager session;
    private UsuarioDao usuarioDao;
    
    private String fotoUriStr = "";
    private Usuario usuarioActual;

    private ActivityResultLauncher<Intent> photoPickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        
        session = new SessionManager(requireContext());
        usuarioDao = new UsuarioDao(requireContext());

        etNombre = view.findViewById(R.id.et_perfil_nombre);
        etDni = view.findViewById(R.id.et_perfil_dni);
        etPassActual = view.findViewById(R.id.et_perfil_password_actual);
        etPassNueva = view.findViewById(R.id.et_perfil_password_nueva);
        spinnerSede = view.findViewById(R.id.spinner_perfil_sede);
        ivFoto = view.findViewById(R.id.iv_perfil_foto);
        MaterialButton btnGuardar = view.findViewById(R.id.btn_perfil_guardar);

        String[] sedes = {"Sede Norte", "Sede Sur", "Sede Central", "Sede Este"};
        ArrayAdapter<String> sedeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sedes);
        sedeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSede.setAdapter(sedeAdapter);

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

        view.findViewById(R.id.fab_perfil_foto).setOnClickListener(v -> pickImage());
        btnGuardar.setOnClickListener(v -> guardarPerfil());

        cargarDatos();

        return view;
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerLauncher.launch(intent);
    }

    private void cargarDatos() {
        String id = session.getOperadorId();
        usuarioActual = usuarioDao.obtenerPorId(id);

        if (usuarioActual != null) {
            etNombre.setText(usuarioActual.getNombre());
            etDni.setText(usuarioActual.getDni() != null ? usuarioActual.getDni() : "");
            
            String sede = usuarioActual.getSede();
            if (sede != null) {
                for (int i = 0; i < spinnerSede.getCount(); i++) {
                    if (spinnerSede.getItemAtPosition(i).toString().equals(sede)) {
                        spinnerSede.setSelection(i);
                        break;
                    }
                }
            }

            fotoUriStr = usuarioActual.getFotoPerfil();
            if (fotoUriStr != null && !fotoUriStr.isEmpty()) {
                ivFoto.setImageURI(Uri.parse(fotoUriStr));
            }
        }
    }

    private void guardarPerfil() {
        if (usuarioActual == null) return;

        String nombre = etNombre.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String sede = spinnerSede.getSelectedItem().toString();
        
        String passActual = etPassActual.getText().toString().trim();
        String passNueva = etPassNueva.getText().toString().trim();
        
        String nuevaContrasenaHash = null;

        if (nombre.isEmpty()) {
            Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si intenta cambiar la contraseña
        if (!passNueva.isEmpty()) {
            if (passActual.isEmpty()) {
                Toast.makeText(getContext(), "Debes ingresar tu contraseña actual para cambiarla", Toast.LENGTH_SHORT).show();
                return;
            }
            // Verificar contraseña actual
            String hashActual = HashUtil.md5(passActual);
            Usuario verificacion = usuarioDao.autenticar(usuarioActual.getEmail(), hashActual);
            if (verificacion == null) {
                Toast.makeText(getContext(), "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                return;
            }
            nuevaContrasenaHash = HashUtil.md5(passNueva);
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setDni(dni);
        usuarioActual.setSede(sede);
        usuarioActual.setFotoPerfil(fotoUriStr);

        boolean exito = usuarioDao.actualizarPerfil(usuarioActual, nuevaContrasenaHash);

        if (exito) {
            session.guardarSesion(usuarioActual);
            Toast.makeText(getContext(), "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
            etPassActual.setText("");
            etPassNueva.setText("");
        } else {
            Toast.makeText(getContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
        }
    }
}
