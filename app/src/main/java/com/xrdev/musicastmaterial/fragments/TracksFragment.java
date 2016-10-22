package com.xrdev.musicastmaterial.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.adapters.TrackAdapter;
import com.xrdev.musicastmaterial.interfaces.ITrack;
import com.xrdev.musicastmaterial.models.TrackItem;
import com.xrdev.musicastmaterial.utils.DividerItemDecoration;

public class TracksFragment extends Fragment {


    private RecyclerView mTracksRecyclerView; // View que será populada com a lista
    private RecyclerView.LayoutManager mLayoutManager; // LayoutManager da RV.
    private TrackAdapter mTracksAdapter; // Adapter que distribuirá os dados do modelo na lista

    ITrack mListener; // Callback para Activity


    public TracksFragment() {
        // Required empty public constructor
    }

    public static TracksFragment newInstance() {
        return new TracksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tracks, container, false);
        setupViews(v);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ITrack) {
            mListener = (ITrack) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ITrack");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setupViews(View inflaterView){
        // Configurar o RecyclerView
        mTracksRecyclerView = (RecyclerView) inflaterView.findViewById(R.id.rv_tracks);
        mTracksRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTracksRecyclerView.setLayoutManager(mLayoutManager);
        mTracksRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mTracksRecyclerView.setAdapter(mTracksAdapter);
    }

    public void setAdapter(TrackAdapter adapter) {
        mTracksAdapter = adapter;
    }

}
