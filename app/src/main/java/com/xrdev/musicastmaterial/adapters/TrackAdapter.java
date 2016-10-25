package com.xrdev.musicastmaterial.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.interfaces.ITrack;
import com.xrdev.musicastmaterial.models.TrackItem;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Guilherme on 13/07/2016.
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackHolder> {

    private List<TrackItem> mTracks = new ArrayList<TrackItem>();
    private final static String TAG = "TrackAdapter";
    ITrack mListener; // Callback para Activity

    class TrackHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView artistsView;
        ProgressBar progressBar;
        ImageButton addButton;

        public TrackHolder(View itemView){
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.text_track_name);
            artistsView = (TextView) itemView.findViewById(R.id.text_track_artists);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbar_youtube_fetch);
            addButton = (ImageButton) itemView.findViewById(R.id.button_track_add);
        }

        /**
         * Vai construir os valores de cada Item, inclusive o listener de click.
         * @param item Item a popular as Views
         * @param listener Listener que receberá os eventos (na BaseActivity)
         */
        public void bind(final TrackItem item, final ITrack listener){
            /**
             * Configuração de texto:
             */
            this.titleView.setText(item.getName());
            this.artistsView.setText(item.getArtists() + " • " + item.getAlbum());


            /**
             * Configuração de ClickListeners:
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTrackSelected(item);
                }
            });
            addButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    listener.onTrackAdded(item);
                }
            });
        }
    }

    public TrackAdapter(ITrack listener) {
        mListener = listener;
    }

    /**
     * Cria uma nova View dentro do RecyclerView. Usado pelo LayoutManager.
     * Infla o layout conforme necessário e retorna um ViewHolder.
     */
    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);

        TrackHolder holder = new TrackHolder(v);
        return holder;
    }

    /**
     * Atualiza dados de um ViewHolder, reutilizando as Views já infladas e tornando o processo mais eficiente.
     * @param holder Holder criado pelo onCreateViewHolder referenciando uma View
     * @param position Posição do Holder a ser atualizada
     */
    @Override
    public void onBindViewHolder(TrackHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(mTracks.get(position), mListener);

    }


    // Adiciona um item a lista
    public void add(TrackItem item) {
        mTracks.add(item);
        notifyDataSetChanged();
    }

    // Limpa a lista
    public void clear(){
        mTracks.clear();
        notifyDataSetChanged();
    }

    // Retorna o número de itens na lista
    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    // Pega o ID de um item
    @Override
    public long getItemId(int pos) {
        return pos;
    }

    public TrackItem getItem(int pos) {
        return mTracks.get(pos);
    }
}
