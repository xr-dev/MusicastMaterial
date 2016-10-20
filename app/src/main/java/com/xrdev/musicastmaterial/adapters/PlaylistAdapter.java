package com.xrdev.musicastmaterial.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.interfaces.IPlaylist;
import com.xrdev.musicastmaterial.models.PlaylistItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guilherme on 12/04/2016.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {

    private List<PlaylistItem> mPlaylists = new ArrayList<PlaylistItem>();
    private final static String TAG = "PlaylistAdapter";
    IPlaylist mListener; // Callback para Activity
    Context mContext; // Necessário para o Glide. Obter no onCreateViewHolder

    class PlaylistHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView tracksView;
        ImageView imageView;

        public PlaylistHolder(View itemView){
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.text_playlist_name);
            tracksView = (TextView) itemView.findViewById(R.id.text_playlist_num_tracks);
            imageView = (ImageView) itemView.findViewById(R.id.image_playlist);
        }

        /**
         * Vai construir os valores de cada Item, inclusive o listener de click.
         * @param item Item a popular as Views
         * @param listener Listener que receberá os eventos (na BaseActivity)
         */
        public void bind(final PlaylistItem item, final IPlaylist listener){
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

    public PlaylistAdapter(IPlaylist listener) {
        mListener = listener;
    }

    /**
     * Cria uma nova View dentro do RecyclerView. Usado pelo LayoutManager.
     * Infla o layout conforme necessário e retorna um ViewHolder.
     */
    @Override
    public PlaylistAdapter.PlaylistHolder onCreateViewHolder(ViewGroup parent,int viewType){
        mContext = parent.getContext();
        View v = LayoutInflater.from(mContext)
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

        if (mPlaylists.get(position).getImageUrl() != null)
            Glide.with(mContext)
                    .load(mPlaylists.get(position).getImageUrl())
                    .into(holder.imageView);
        else {
            Glide.clear(holder.imageView);
            holder.imageView.setImageDrawable(mContext.getDrawable(R.drawable.bg_default_playlist_art));
        }
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
