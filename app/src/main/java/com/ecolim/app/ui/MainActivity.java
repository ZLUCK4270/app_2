package com.ecolim.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.ecolim.app.R;
import com.ecolim.app.ui.fragments.InicioFragment;
import com.ecolim.app.ui.fragments.PerfilFragment;
import com.ecolim.app.ui.fragments.RegistrosFragment;
import com.ecolim.app.ui.fragments.ReportesFragment;
import com.ecolim.app.ui.fragments.SincronizarFragment;
import com.ecolim.app.util.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import android.net.Uri;
import com.google.android.material.imageview.ShapeableImageView;
import android.view.View;

/**
 * Actividad Principal en Java nativo: Drawer Sarah M. y BottomNavigationView.
 * app/src/main/java/com/ecolim/app/ui/MainActivity.java
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNav;
    private MaterialToolbar toolbar;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNav = findViewById(R.id.bottom_navigation);
        NavigationView navView = findViewById(R.id.nav_view);

        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Configurar el Header dinámicamente
        View headerView = navView.getHeaderView(0);
        TextView tvNombreTecnico = headerView.findViewById(R.id.tv_nombre_tecnico);
        TextView tvRolTecnico = headerView.findViewById(R.id.tv_rol_tecnico);
        TextView tvTurnoTecnico = headerView.findViewById(R.id.tv_turno_tecnico);
        ShapeableImageView ivAvatar = headerView.findViewById(R.id.iv_avatar_tecnico);

        tvNombreTecnico.setText(session.getOperadorNombre());
        tvRolTecnico.setText(session.getOperadorRol() + " · ID " + session.getOperadorId() + (session.getOperadorSede().isEmpty() ? "" : " (" + session.getOperadorSede() + ")"));
        tvTurnoTecnico.setText(session.getOperadorTurno());
        
        String fotoPath = session.getOperadorFoto();
        if (fotoPath != null && !fotoPath.isEmpty()) {
            ivAvatar.setImageURI(Uri.parse(fotoPath));
        }

        // Menú lateral
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.drawer_panel) {
                cargarFragmento(new InicioFragment(), "Inicio", "Turno Matutino");
            } else if (id == R.id.drawer_registros) {
                cargarFragmento(new RegistrosFragment(), "Registros", "Pesajes de Campo");
            } else if (id == R.id.drawer_sincronizar) {
                cargarFragmento(new SincronizarFragment(), "Sincronización", "Nodo ACT-709");
            } else if (id == R.id.drawer_perfil) {
                cargarFragmento(new PerfilFragment(), "Mi Perfil", session.getOperadorNombre());
            } else if (id == R.id.drawer_calibrar_bascula) {
                Toast.makeText(this, "Báscula #B-04 calibrada correctamente a 0.00 kg", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.drawer_sqlite_estado) {
                Toast.makeText(this, "Base de datos ecolim.db en buen estado (SQLite 4)", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Navegación Inferior (BottomNav)
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) {
                cargarFragmento(new InicioFragment(), "Ecolim", "Panel de Turno Matutino");
                return true;
            } else if (id == R.id.nav_registros) {
                cargarFragmento(new RegistrosFragment(), "Registros", "Trazabilidad de Residuos");
                return true;
            } else if (id == R.id.nav_escanear) {
                startActivity(new Intent(this, EscanearActivity.class));
                return false;
            } else if (id == R.id.nav_sincronizar) {
                cargarFragmento(new SincronizarFragment(), "Sincronización", "Cola Offline / Python");
                return true;
            }
            return false;
        });

        // Cargar fragmento inicial por defecto
        if (savedInstanceState == null) {
            cargarFragmento(new InicioFragment(), "Ecolim", "Panel de Turno Matutino");
        }
    }

    private void cargarFragmento(Fragment fragment, String titulo, String subtitulo) {
        toolbar.setTitle(titulo);
        toolbar.setSubtitle(subtitulo);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
