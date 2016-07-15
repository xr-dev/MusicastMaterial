package com.xrdev.musicastmaterial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;
import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.fragments.TracksFragment;
import com.xrdev.musicastmaterial.interfaces.IPlaylist;
import com.xrdev.musicastmaterial.fragments.PlaylistsFragment;
import com.xrdev.musicastmaterial.interfaces.ITrack;
import com.xrdev.musicastmaterial.models.PlaylistItem;
import com.xrdev.musicastmaterial.models.TrackItem;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class BaseActivity extends Activity implements IPlaylist, ITrack {

    int BLUR_RADIUS = 175;
    FragmentManager mFragmentManager;
    PlaylistsFragment mPlaylistsFragment;
    TracksFragment mTracksFragment;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    AppBarLayout mAppBarLayout;
    ImageView mToolbarBackground;
    ImageView mToolbarArt;
    CoordinatorLayout mCoordinatorLayout;
    FloatingActionMenu menuFab;
    FrameLayout mFrameContainer;
    FrameLayout mMiniPlayer;

    PlaylistItem mPlaylistSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        findViews();
        initFragments();
        setupMenuFabAnim();
        collapseToolbar();

    }

    /**
     * Inicialização das Views - bind dos elementos de layout com os objetos
     */
    public void findViews(){
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mToolbarBackground = (ImageView) findViewById(R.id.appbar_background);
        mToolbarArt = (ImageView) findViewById(R.id.appbar_art);
        menuFab = (FloatingActionMenu) findViewById(R.id.menu_fab);
        mFrameContainer = (FrameLayout) findViewById(R.id.frame_container);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
    }

    /**
     * Inicialização dos Fragments
     */
    public void initFragments(){
        mFragmentManager = getFragmentManager();

        if (mPlaylistsFragment == null) {
            mPlaylistsFragment = PlaylistsFragment.newInstance();
        }

        if (mTracksFragment == null) {
            mTracksFragment = TracksFragment.newInstance();
        }

        /**
         * TODO: alterar toda essa parte para inicializar o fragment correto, este código deve estar em outro método específico para o PlaylistsFragment.
         */
        loadPlaylists();

    }

    @Override
    public void onBackPressed(){
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
            menuFab.setVisibility(View.GONE);
            mCollapsingToolbarLayout.setTitle("Musicast");
            collapseToolbar();
        } else {
            //super.onBackPressed();
            this.moveTaskToBack(true);
        }
    }

    public void collapseToolbar() {
        mAppBarLayout.setExpanded(false, false);
    }

    /**
     * Configura a animação customizada do Floating Action Button.
     */
    public void setupMenuFabAnim() {

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(menuFab.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(menuFab.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(menuFab.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(menuFab.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                menuFab.getMenuIconView().setImageResource(menuFab.isOpened()
                        ? R.drawable.ic_action_toc : R.drawable.ic_close);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menuFab.setIconToggleAnimatorSet(set);

    }

    /**
     * Trata a seleção de uma Playlist no RecyclerView do PlaylistFragment.
     * @param playlist
     */
    public void onPlaylistSelected(PlaylistItem playlist){
        Snackbar.make(mCoordinatorLayout, "DEBUG: Playlist selecionada " + playlist.getName(), Snackbar.LENGTH_LONG).show();
        mPlaylistSelected = playlist;

        /**
         * ATUALIZAÇÃO DA INTERFACE
         */
        if (mTracksFragment == null) {
            mTracksFragment = TracksFragment.newInstance();
        }

        mFragmentManager.beginTransaction()
                .replace(R.id.frame_container, mTracksFragment)
                .addToBackStack(null)
                .commit();

        menuFab.setVisibility(View.VISIBLE);
        mCollapsingToolbarLayout.setTitle(mPlaylistSelected.getName());
        mAppBarLayout.setExpanded(true, false);

        Glide.with(this)
                .load("https://mosaic.scdn.co/640/134cd5ccaef9d411eba33df9542db9ba731aaf98c4b4399d9b7c6f61b6a6ee70c616bc1a985c7ab8e337f3661f68bc4d96a554de0ad7988d65edb25aec04f9acee17a7576f939eb5aa317d20c6322494")
                .into(mToolbarArt);

        Glide.with(this)
                .load("https://mosaic.scdn.co/640/134cd5ccaef9d411eba33df9542db9ba731aaf98c4b4399d9b7c6f61b6a6ee70c616bc1a985c7ab8e337f3661f68bc4d96a554de0ad7988d65edb25aec04f9acee17a7576f939eb5aa317d20c6322494")
                .bitmapTransform(new BlurTransformation(this, BLUR_RADIUS))
                .into(mToolbarBackground);
    }

    /**
     * Trata da seleção de uma Track no RecyclerView do TrackFragment.
     * Este método é executado quando o usuário toca na linha da música e iniciará a reprodução da
     * Playlist carregada na música selecionada. Funciona apenas no Modo Solo.
     * @param track
     */
    public void onTrackSelected(TrackItem track) {
        Snackbar.make(mCoordinatorLayout, "DEBUG: Track SELECIONADA: " + track.getName(), Snackbar.LENGTH_LONG).show();
    }

    /**
     * Trata da interação do usuário com o botão de "Adicionar Música". Funciona no Modo Festa.
     * @param track
     */
    public void onTrackAdded(TrackItem track) {
        Snackbar.make(mCoordinatorLayout, "DEBUG: Track ADICIONADA: " + track.getName(), Snackbar.LENGTH_LONG).show();
    }

    /**
     * Configura interface e toolbar (título, fundo e botões) para a View de tracks de uma playlist.
     */
    private void loadPlaylists(){

        /**
         * Configurar toolbar
         */
        mCollapsingToolbarLayout.setTitle("Musicast");

        mFragmentManager.beginTransaction()
                .replace(R.id.frame_container, mPlaylistsFragment)
                .commit();

        menuFab.setVisibility(View.GONE);


    }
}
