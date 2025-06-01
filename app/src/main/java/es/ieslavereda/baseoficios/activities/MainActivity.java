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


import java.util.List;

import es.ieslavereda.baseoficios.API.Connector;
import es.ieslavereda.baseoficios.R;
import es.ieslavereda.baseoficios.activities.model.AdaptadorRV;
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

        ActivityResultLauncher<Intent> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if(result.getResultCode() == RESULT_OK){
                                Usuario pais = (Usuario) result.getData().getExtras().getSerializable("usuario");
                                usuarios.add(pais);
                                recyclerView.getAdapter().notifyItemInserted(usuarios.size()-1);
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
    }


    @Override
    public List<Usuario> doInBackground() throws Exception {
        return Connector.getConector().getAsList(Usuario.class,"usuarios/");
    }

    @Override
    public void doInUI(List<Usuario> data) {
        usuarios = data;
        AdaptadorRV adaptador = new AdaptadorRV(this, usuarios,this);
        recyclerView.setAdapter(adaptador);

    }

    @Override
    public void onClick(View view) {
        int posicion = recyclerView.getChildAdapterPosition(view);
        Usuario usuario = usuarios.get(posicion);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
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