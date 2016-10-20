package com.xrdev.musicastmaterial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.adapters.PlaylistAdapter;
import com.xrdev.musicastmaterial.interfaces.IPlaylist;
import com.xrdev.musicastmaterial.models.PlaylistItem;
import com.xrdev.musicastmaterial.utils.DividerItemDecoration;

public class PlaylistsFragment extends Fragment {


    private RecyclerView mPlaylistsRecyclerView; // View que será populada com a lista
    private RecyclerView.LayoutManager mLayoutManager; // LayoutManager da RV.
    private PlaylistAdapter mPlaylistAdapter; // Adapter que distribuirá os dados do modelo na lista

    IPlaylist mListener; // Callback para Activity


    public PlaylistsFragment() {
        // Required empty public constructor
    }

    public static PlaylistsFragment newInstance() {
        return new PlaylistsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_playlists, container, false);
        setupViews(v);
        // debug_loadPlaylists();
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IPlaylist) {
            mListener = (IPlaylist) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ILogin");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setAdapter(PlaylistAdapter adapter){
        mPlaylistAdapter = adapter;
    }

    private void setupViews(View inflaterView){
        // Configurar o RecyclerView
        mPlaylistsRecyclerView = (RecyclerView) inflaterView.findViewById(R.id.rv_playlists);
        mPlaylistsRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPlaylistsRecyclerView.setLayoutManager(mLayoutManager);
        mPlaylistsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mPlaylistsRecyclerView.setAdapter(mPlaylistAdapter);
        mPlaylistsRecyclerView.setNestedScrollingEnabled(false);
    }


}
