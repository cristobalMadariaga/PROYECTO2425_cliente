package es.ieslavereda.baseoficios.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ieslavereda.baseoficios.API.Connector;
import es.ieslavereda.baseoficios.R;
import es.ieslavereda.baseoficios.activities.model.AdaptadorRV;
import es.ieslavereda.baseoficios.activities.model.Oficio;
import es.ieslavereda.baseoficios.activities.model.Usuario;
import es.ieslavereda.baseoficios.base.BaseActivity;
import es.ieslavereda.baseoficios.base.CallInterface;


public class MainActivity extends BaseActivity implements CallInterface<List<Usuario>>,
        View.OnClickListener {

    private List<Usuario> usuarios;
    private RecyclerView recyclerView;
    private FloatingActionButton buttonAnyadir;
    private Usuario usuarioInsertar;
    private int posicionInsertar;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private List<Oficio> listaOficios;
    private Map<Integer, Oficio> mapaOficios;

    private CallInterface<Usuario> INSERTAR_USUARIO = new CallInterface<Usuario>() {
        @Override
        public Usuario doInBackground() throws Exception {
            return Connector.getConector().post(Usuario.class, usuarioInsertar,"usuarios/");
        }

        @Override
        public void doInUI(Usuario data) {
            if(data!=null|| data.equals(usuarioInsertar)){
                usuarios.add(posicionInsertar, usuarioInsertar);
                recyclerView.getAdapter().notifyItemInserted(posicionInsertar);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleView);
        buttonAnyadir = findViewById(R.id.anyadirBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                Intent data = result.getData();
                                if (data.hasExtra("usuarioActualizado")) {
                                    Usuario usuarioActualizado = (Usuario) data.getSerializableExtra("usuarioActualizado");
                                    int index = -1;
                                    for (int i = 0; i < usuarios.size(); i++) {
                                        if (usuarios.get(i).getIdUsuario() == usuarioActualizado.getIdUsuario()) {
                                            index = i;
                                        }
                                    }
                                    if (index != -1) {
                                        usuarios.set(index, usuarioActualizado);
                                        recyclerView.getAdapter().notifyItemChanged(index);
                                    }
                                } else if (data.hasExtra("usuarioInsertado")) {
                                    Usuario usuarioInsertado = (Usuario) data.getSerializableExtra("usuarioInsertado");
                                    usuarios.add(usuarioInsertado);
                                    recyclerView.getAdapter().notifyItemInserted(usuarios.size() - 1);
                                }
                            }
                        });


        buttonAnyadir.setOnClickListener( view -> {
            Intent intent = new Intent(this, DetailActivity.class);
            activityResultLauncher.launch(intent);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        int posIni = viewHolder.getAdapterPosition();
                        int posFin = target.getAdapterPosition();
                        Usuario usuario = usuarios.remove(posIni);
                        usuarios.add(posFin,usuario);
                        recyclerView.getAdapter().notifyItemMoved(posIni,posFin);
                        return true;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        Usuario usuario = usuarios.get(viewHolder.getAdapterPosition());
                        eliminarUsuario(usuario);
                    }
                }
        );

        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Mostramos la barra de progreso
        showProgress();
        //ejecutamos la llamada a la API
        executeCall(this);
        cargarOficios();

    }

    private void cargarOficios() {
        executeCall(new CallInterface<List<Oficio>>() {
            @Override
            public List<Oficio> doInBackground() throws Exception {
                return Connector.getConector().getAsList(Oficio.class, "oficios/");
            }

            @Override
            public void doInUI(List<Oficio> data) {
                listaOficios = data;
                mapaOficios = new HashMap<>();
                for (Oficio oficio : listaOficios) {
                    mapaOficios.put(oficio.getIdOficio(), oficio);
                }
                if (usuarios != null) {
                    configurarAdaptador();
                }
            }
        });
    }
    private void configurarAdaptador() {
        AdaptadorRV adaptador = new AdaptadorRV(this, usuarios, this);
        adaptador.setMapaOficios(mapaOficios);
        recyclerView.setAdapter(adaptador);
        hideProgress();
    }



    @Override
    public List<Usuario> doInBackground() throws Exception {
        return Connector.getConector().getAsList(Usuario.class,"usuarios/");
    }

    @Override
    public void doInUI(List<Usuario> data) {
        usuarios = data;
        if (mapaOficios != null) {
            configurarAdaptador();
        }

    }

    @Override
    public void onClick(View view) {
        int posicion = recyclerView.getChildAdapterPosition(view);
        Usuario usuario = usuarios.get(posicion);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("usuario", usuario);
        activityResultLauncher.launch(intent);
    }

    private void eliminarUsuario(Usuario usuario){
        executeCall(new CallInterface<Usuario>() {
            @Override
            public Usuario doInBackground() throws Exception {
                return Connector.getConector().delete(Usuario.class,"usuarios/"+usuario.getIdUsuario());
            }

            @Override
            public void doInUI(Usuario data) {
                if (data != null || data.equals(data)){
                    int posicion = usuarios.indexOf(usuario);
                    usuarios.remove(posicion);
                    recyclerView.getAdapter().notifyItemRemoved(posicion);
                    usuarioInsertar = data;
                    posicionInsertar=posicion;
                    Snackbar.make(recyclerView,"Borrado usuario: " + usuario.getNombre()+" "+usuario.getApellidos(), Snackbar.LENGTH_LONG).
                            setAction("Deshacer", view -> executeCall(INSERTAR_USUARIO)).
                            show();
                }
            }
        });
    }
}