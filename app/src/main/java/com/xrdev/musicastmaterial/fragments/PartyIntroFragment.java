package com.xrdev.musicastmaterial.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xrdev.musicastmaterial.Application;
import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.activities.BaseActivity;
import com.xrdev.musicastmaterial.interfaces.ILogin;
import com.xrdev.musicastmaterial.utils.PrefsManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ILogin} interface
 * to handle interaction events.
 * Use the {@link PartyIntroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartyIntroFragment extends Fragment {

    private BaseActivity mListener;

    public PartyIntroFragment() {
        // Required empty public constructor
    }

    Button mButtonGotIt;
    TextView mPartyIntroText;

    public static PartyIntroFragment newInstance() {
        PartyIntroFragment fragment = new PartyIntroFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_party_intro, container, false);
        setupViews(v);
        return v;
    }

    private void setupViews(View v){
        mPartyIntroText = (TextView) v.findViewById(R.id.party_intro_text);
        mButtonGotIt = (Button) v.findViewById(R.id.party_intro_button_got_it);

        String thisUuid = PrefsManager.getUUID(getActivity().getApplicationContext());
        String adminUuid = mListener.getAdmin();

        if (thisUuid.equals(adminUuid))
            mPartyIntroText.setText(R.string.party_admin_connected);
        else
            mPartyIntroText.setText(R.string.party_guest_connected);

        mButtonGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.showPlaylistsFragment();
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ILogin) {
            mListener = (BaseActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must be BaseActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
