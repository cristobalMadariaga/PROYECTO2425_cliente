package es.ieslavereda.baseoficios.activities;

import static es.ieslavereda.baseoficios.base.Parameters.URL_IMAGE_BASE;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.activity.EdgeToEdge;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import es.ieslavereda.baseoficios.API.Connector;
import es.ieslavereda.baseoficios.R;
import es.ieslavereda.baseoficios.activities.model.Oficio;
import es.ieslavereda.baseoficios.activities.model.Usuario;
import es.ieslavereda.baseoficios.base.BaseActivity;
import es.ieslavereda.baseoficios.base.CallInterface;

public class DetailActivity extends BaseActivity implements CallInterface<Collection<Oficio>> {
    private List<Oficio> oficios = new ArrayList<>();
    private Usuario usuario;
    private Button guardar_btn, cancelar_btn;
    private EditText nombre_ET, apellidos_ET;
    private Spinner oficios_spinner;
    private ImageView detail_IV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        guardar_btn = findViewById(R.id.guardar_btn);
        cancelar_btn = findViewById(R.id.cancelar_btn);
        nombre_ET = findViewById(R.id.nombre_ET);
        apellidos_ET = findViewById(R.id.apellidos_ET);
        oficios_spinner = findViewById(R.id.oficios_spinner);
        detail_IV = findViewById(R.id.detail_IV);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
           usuario = (Usuario) extras.getSerializable("usuario");
        } else {

        }
        nombre_ET.setText(usuario.getNombre());
        apellidos_ET.setText(usuario.getApellidos());

        guardar_btn.setOnClickListener((View view)->{
            //TO DO
        });
        cancelar_btn.setOnClickListener((View view)->{
            //falta un activity result?
            finish();
        });


        // Ejecutamos una llamada para obtener datos de la API
        executeCall(this);
        showProgress();
    }

    @Override
    public Collection<Oficio> doInBackground() throws Exception {
        return Connector.getConector().getAsList(Oficio.class, "oficios/");
    }

    @Override
    public void doInUI(Collection<Oficio> data) {
//        oficios_spinner.setAdapter(); // TO DO y también poner el nombre del oficio que tiene ahora
//        ImageDownloader.downloadImage(URL_IMAGE_BASE+oficio.getImage(), detail_IV);
    }

}
