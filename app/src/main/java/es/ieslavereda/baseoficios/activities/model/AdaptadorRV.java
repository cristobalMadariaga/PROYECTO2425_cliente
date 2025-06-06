package es.ieslavereda.baseoficios.activities.model;

import static es.ieslavereda.baseoficios.base.Parameters.URL_IMAGE_BASE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;
import es.ieslavereda.baseoficios.R;
import es.ieslavereda.baseoficios.base.ImageDownloader;

public class AdaptadorRV extends
        RecyclerView.Adapter<AdaptadorRV.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Usuario> usuarios;
    private View.OnClickListener onClickListener;
    private Map<Integer, Oficio> mapaOficios;
    public void setMapaOficios(Map<Integer, Oficio> mapaOficios) {
        this.mapaOficios = mapaOficios;
    }

    public AdaptadorRV(Context context, List<Usuario> usuarios, View.OnClickListener onClickListener){
        this.context = context;
        this.onClickListener = onClickListener;
        this.usuarios = usuarios;
        this.layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.viewholder_layout, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.nombreTV.setText(usuario.getNombre() + " " + usuario.getApellidos());
        Oficio oficio = mapaOficios.get(usuario.getOficio_idOficio());

        if (oficio != null) {
            holder.oficioTV.setText(oficio.getDescripcion());
            ImageDownloader.downloadImage(URL_IMAGE_BASE + oficio.getImage(), holder.oficioIV);
        }
    }


    @Override
    public int getItemCount() {
        return usuarios.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView oficioIV;
        public TextView nombreTV, oficioTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            oficioIV = itemView.findViewById(R.id.oficioIV);
            nombreTV = itemView.findViewById(R.id.nombreTV);
            oficioTV = itemView.findViewById(R.id.oficioTV);
        }
    }
}