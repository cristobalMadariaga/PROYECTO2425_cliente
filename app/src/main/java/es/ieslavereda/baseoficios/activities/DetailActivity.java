package es.ieslavereda.baseoficios.activities;

import static es.ieslavereda.baseoficios.base.Parameters.URL_IMAGE_BASE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import es.ieslavereda.baseoficios.API.Connector;
import es.ieslavereda.baseoficios.R;
import es.ieslavereda.baseoficios.activities.model.Oficio;
import es.ieslavereda.baseoficios.activities.model.Usuario;
import es.ieslavereda.baseoficios.base.BaseActivity;
import es.ieslavereda.baseoficios.base.CallInterface;
import es.ieslavereda.baseoficios.base.ImageDownloader;

public class DetailActivity extends BaseActivity implements CallInterface<List<Oficio>> {
    private List<Oficio> oficios = new ArrayList<>();
    private Button guardar_btn, cancelar_btn;
    private EditText nombre_ET, apellidos_ET;
    private Spinner oficios_spinner;
    private ImageView detail_IV;
    private Usuario usuario = null;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("nombre", nombre_ET.getText().toString());
        outState.putString("apellidos", apellidos_ET.getText().toString());
        outState.putInt("spinnerSelectedIndex", oficios_spinner.getSelectedItemPosition());
    }
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
           nombre_ET.setText(usuario.getNombre());
           apellidos_ET.setText(usuario.getApellidos());
            guardar_btn.setOnClickListener((View view)->{
                executeCall(new CallInterface<Usuario>() {
                    @Override
                    public Usuario doInBackground() throws Exception {
                        Oficio oficioSeleccionado = (Oficio) oficios_spinner.getSelectedItem();

                        usuario = new Usuario(
                                usuario.getIdUsuario(),
                                nombre_ET.getText().toString(),
                                apellidos_ET.getText().toString(),
                                oficioSeleccionado.getIdOficio()
                        );
                        return Connector.getConector().put(Usuario.class, usuario, "usuarios/");
                    }

                    @Override
                    public void doInUI(Usuario data) {
                        Intent intent = new Intent();
                        intent.putExtra("usuarioActualizado", data);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            });

        } else {
            nombre_ET.setText("");
            apellidos_ET.setText("");
            guardar_btn.setOnClickListener((View view)->{
                        executeCall(new CallInterface<Usuario>() {
                            @Override
                            public Usuario doInBackground() throws Exception {
                                Oficio oficioSeleccionado = (Oficio) oficios_spinner.getSelectedItem();

                                usuario = new Usuario(
                                        nombre_ET.getText().toString(),
                                        apellidos_ET.getText().toString(),
                                        oficioSeleccionado.getIdOficio()
                                );
                                return Connector.getConector().post(Usuario.class, usuario, "usuarios/");
                            }

                            @Override
                            public void doInUI(Usuario data) {
                                Intent intent = new Intent();
                                intent.putExtra("usuarioInsertado", data);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
            });
        }
        cancelar_btn.setOnClickListener((View view)->{
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        });


        // Ejecutamos una llamada para obtener datos de la API
        executeCall(this);
        showProgress();
    }
    //metodo para cargar la imagen con el ImageDownloader
    private void cargarImagen(int idOficio) {
        executeCall(new CallInterface<String>() {
            @Override
            public String doInBackground() throws Exception {
                return Connector.getConector().get(String.class, "oficios/imagen/" + idOficio);
            }

            @Override
            public void doInUI(String data) {
                ImageDownloader.downloadImage(URL_IMAGE_BASE + data, detail_IV);
            }
        });
    }


    @Override
    public List<Oficio> doInBackground() throws Exception {
        return Connector.getConector().getAsList(Oficio.class, "oficios/");
    }

    @Override
    public void doInUI(List<Oficio> data) {
        oficios.addAll(data);
        ArrayAdapter<Oficio> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, oficios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        oficios_spinner.setAdapter(adapter);
        // llama al metodo para cambiar la imagen al cambiar el oficio en el spinner
        oficios_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Oficio oficioSeleccionado = (Oficio) oficios_spinner.getSelectedItem();
                cargarImagen(oficioSeleccionado.getIdOficio());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //para seleccionar el oficio que tiene el usuario ya establecido para hacer PUT
        if (usuario != null) {
            for (int i = 0; i < oficios.size(); i++) {
                if (oficios.get(i).getIdOficio() == usuario.getOficio_idOficio()) {
                    oficios_spinner.setSelection(i);
                }
            }
            cargarImagen(usuario.getOficio_idOficio());
        }
    }


}
