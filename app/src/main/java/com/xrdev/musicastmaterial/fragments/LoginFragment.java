package com.xrdev.musicastmaterial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.interfaces.ILogin;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ILogin} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private ILogin mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    Button mButtonLogin;
    Button mButtonSkipLogin;
    TextView mLoginTextAux;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        setupViews(v);
        return v;
    }

    private void setupViews(View v){
        mLoginTextAux = (TextView) v.findViewById(R.id.login_text_aux);
        mButtonLogin = (Button) v.findViewById(R.id.login_button_login);
        mButtonSkipLogin = (Button) v.findViewById(R.id.login_button_skip);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLoginButtonPressed();
            }
        });

        mButtonSkipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLoginSkipButtonPressed();
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ILogin) {
            mListener = (ILogin) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
