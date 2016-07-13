package com.xrdev.musicastmaterial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.BottomSheetBehavior;
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
import com.xrdev.musicastmaterial.interfaces.OnPlaylistSelectedListener;
import com.xrdev.musicastmaterial.fragments.PlaylistsFragment;
import com.xrdev.musicastmaterial.models.PlaylistItem;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class BaseActivity extends Activity implements OnPlaylistSelectedListener {

    int BLUR_RADIUS = 175;
    FragmentManager mFragmentManager;
    PlaylistsFragment mPlaylistsFragment;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    ImageView mToolbarBackground;
    ImageView mToolbarArt;
    CoordinatorLayout mCoordinatorLayout;
    FloatingActionMenu menuFab;
    BottomSheetBehavior<FrameLayout> mBottomSheetBehavior;
    FrameLayout mMiniPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        findViews();
        initFragments();
        setupMenuFabAnim();

    }

    public void findViews(){
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mToolbarBackground = (ImageView) findViewById(R.id.appbar_background);
        mToolbarArt = (ImageView) findViewById(R.id.appbar_art);
        menuFab = (FloatingActionMenu) findViewById(R.id.menu_fab);
        mMiniPlayer = (FrameLayout) findViewById(R.id.mini_player);



    }

    public void initFragments(){
        mFragmentManager = getFragmentManager();

        if (mPlaylistsFragment == null) {
            mPlaylistsFragment = PlaylistsFragment.newInstance();
        }

        /**
         * TODO: alterar toda essa parte para inicializar o fragment correto, este código deve estar em outro método específico para o PlaylistsFragment.
         */
        onPlaylistSelected();

    }

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

    public void onPlaylistSelected(PlaylistItem playlist){
        Snackbar.make(mCoordinatorLayout, "Playlist selecionada " + playlist.getName(), Snackbar.LENGTH_LONG).show();
    }

    /**
     * Configura interface e toolbar (título, fundo e botões) para a View de tracks de uma playlist.
     */
    private void onPlaylistSelected(){

        /**
         * Configurar toolbar
         */
        mCollapsingToolbarLayout.setTitle("Playlist Name");

        mFragmentManager.beginTransaction()
                .replace(R.id.frame_container, mPlaylistsFragment)
                .commit();


        Glide.with(this)
                .load("https://mosaic.scdn.co/640/134cd5ccaef9d411eba33df9542db9ba731aaf98c4b4399d9b7c6f61b6a6ee70c616bc1a985c7ab8e337f3661f68bc4d96a554de0ad7988d65edb25aec04f9acee17a7576f939eb5aa317d20c6322494")
                .into(mToolbarArt);

        Glide.with(this)
                .load("https://mosaic.scdn.co/640/134cd5ccaef9d411eba33df9542db9ba731aaf98c4b4399d9b7c6f61b6a6ee70c616bc1a985c7ab8e337f3661f68bc4d96a554de0ad7988d65edb25aec04f9acee17a7576f939eb5aa317d20c6322494")
                .bitmapTransform(new BlurTransformation(this, BLUR_RADIUS))
                .into(mToolbarBackground);

    }
}
