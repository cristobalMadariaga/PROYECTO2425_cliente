package es.ieslavereda.baseoficios.activities;

import static es.ieslavereda.baseoficios.base.Parameters.URL_IMAGE_BASE;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import es.ieslavereda.baseoficios.API.Connector;
import es.ieslavereda.baseoficios.R;
import es.ieslavereda.baseoficios.activities.model.Usuario;
import es.ieslavereda.baseoficios.base.BaseActivity;
import es.ieslavereda.baseoficios.base.CallInterface;
import es.ieslavereda.baseoficios.base.ImageDownloader;

public class MainActivity extends BaseActivity implements CallInterface<Usuario> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ejecutamos una llamada para obtener datos de la API
        executeCall(this);

    }

    // Realizamos la llamada en segundo plano y devolvemos el objeto obtenido
    @Override
    public Usuario doInBackground() throws Exception {

        return Connector.getConector().get(Usuario.class,"");

    }

    // Una vez finalizada la tarea en segundo plano, ejecutamos en primer plano (UI).
    // Recibimos por parametro lo obtenido en segundo plano.
    @Override
    public void doInUI(Usuario usuario) {

//        ImageDownloader.downloadImage(URL_IMAGE_BASE + "albañil.png", findViewById(R.id.imageView));

    }

//    Si queremos variar que hacer si se produce un error descomentar
//    @Override
//    public void doInError(Context context, Exception e) {
//
//    }

}