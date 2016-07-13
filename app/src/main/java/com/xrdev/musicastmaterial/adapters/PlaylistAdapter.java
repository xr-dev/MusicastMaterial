package com.xrdev.musicastmaterial.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.interfaces.OnPlaylistSelectedListener;
import com.xrdev.musicastmaterial.models.PlaylistItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guilherme on 12/04/2016.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {

    private List<PlaylistItem> mPlaylists = new ArrayList<PlaylistItem>();
    private final static String TAG = "PlaylistAdapter";
    OnPlaylistSelectedListener mListener; // Callback para Activity

    class PlaylistHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView tracksView;

        public PlaylistHolder(View itemView){
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.text_playlist_name);
            tracksView = (TextView) itemView.findViewById(R.id.text_playlist_num_tracks);
        }

        /**
         * Vai construir os valores de cada Item, inclusive o listener de click.
         * @param item Item a popular as Views
         * @param listener Listener que receberá os eventos (na BaseActivity)
         */
        public void bind(final PlaylistItem item, final OnPlaylistSelectedListener listener){
            this.titleView.setText(item.getName());
            this.tracksView.setText(item.getNumTracks());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPlaylistSelected(item);
                }
            });
        }
    }

    public PlaylistAdapter(OnPlaylistSelectedListener listener) {
        mListener = listener;
    }

    /**
     * Cria uma nova View dentro do RecyclerView. Usado pelo LayoutManager.
     * Infla o layout conforme necessário e retorna um ViewHolder.
     */
    @Override
    public PlaylistAdapter.PlaylistHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);

        PlaylistHolder holder = new PlaylistHolder(v);
        return holder;
    }

    /**
     * Atualiza dados de um ViewHolder, reutilizando as Views já infladas e tornando o processo mais eficiente.
     * @param holder Holder criado pelo onCreateViewHolder referenciando uma View
     * @param position Posição do Holder a ser atualizada
     */
    @Override
    public void onBindViewHolder(PlaylistHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(mPlaylists.get(position), mListener);

    }


    // Adiciona um item a lista
    public void add(PlaylistItem item) {
        mPlaylists.add(item);
        notifyDataSetChanged();
    }

    // Limpa a lista
    public void clear(){
        mPlaylists.clear();
        notifyDataSetChanged();
    }

    // Retorna o n�mero de itens na lista
    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    // Pega o ID de um item
    @Override
    public long getItemId(int pos) {
        return pos;
    }
}
