package com.xrdev.musicastmaterial.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.interfaces.OnPlaylistSelectedListener;
import com.xrdev.musicastmaterial.fragments.PlaylistsFragment;
import com.xrdev.musicastmaterial.models.PlaylistModel;

public class BaseActivity extends Activity implements OnPlaylistSelectedListener {

    FragmentManager mFragmentManager;
    PlaylistsFragment mPlaylistsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initFragments();

    }

    public void initFragments(){
        mFragmentManager = getFragmentManager();

        if (mPlaylistsFragment == null) {
            mPlaylistsFragment = PlaylistsFragment.newInstance();
        }

        mFragmentManager.beginTransaction()
                .replace(R.id.frame_container, mPlaylistsFragment)
                .commit();
    }

    public void onPlaylistSelected(PlaylistModel playlist){
        Toast.makeText(this, "Playlist selecionada " + playlist.getName(), Toast.LENGTH_LONG).show();
    }
}
