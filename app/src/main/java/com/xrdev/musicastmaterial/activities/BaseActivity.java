package com.xrdev.musicastmaterial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.apis.SpotifyManager;
import com.xrdev.musicastmaterial.fragments.LoginFragment;
import com.xrdev.musicastmaterial.fragments.TracksFragment;
import com.xrdev.musicastmaterial.interfaces.ILogin;
import com.xrdev.musicastmaterial.interfaces.IPlaylist;
import com.xrdev.musicastmaterial.fragments.PlaylistsFragment;
import com.xrdev.musicastmaterial.interfaces.ITrack;
import com.xrdev.musicastmaterial.models.PlaylistItem;
import com.xrdev.musicastmaterial.models.TrackItem;
import com.xrdev.musicastmaterial.utils.PrefsManager;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class BaseActivity extends Activity implements IPlaylist, ITrack, ILogin {

    final static String TAG = "MusicastMaterial";
    // Glide -- imagens
    int GLIDE_BLUR_RADIUS = 175;

    // Fragments
    FragmentManager mFragmentManager;
    PlaylistsFragment mPlaylistsFragment;
    TracksFragment mTracksFragment;
    LoginFragment mLoginFragment;

    // Toolbar
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    AppBarLayout mAppBarLayout;
    ImageView mToolbarBackground;
    ImageView mToolbarArt;

    // Layout
    CoordinatorLayout mCoordinatorLayout;
    FloatingActionMenu menuFab;
    FrameLayout mFrameContainer;

    // Dados
    PlaylistItem mPlaylistSelected;

    // Spotify Auth
    int REQUEST_CODE = SpotifyManager.REQUEST_CODE;
    String REDIRECT_URI = SpotifyManager.REDIRECT_URI;
    boolean wasLoginPrompted = false;
    String authCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initViews();
        initFragments();
        setupMenuFabAnim();
        mAppBarLayout.setExpanded(false, false);

        // TODO: Debugging, alterar o comportamento inicial do app

        //showPlaylistsFragment();
        showLoginFragment();

    }

    /**
     * --------------------------------------------------------------------------------------------
     * INICIALIZAÇÃO DAS VIEWS
     * Bind dos elementos de layout com os objetos
     * --------------------------------------------------------------------------------------------
     */
    public void initViews(){
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mToolbarBackground = (ImageView) findViewById(R.id.appbar_background);
        mToolbarArt = (ImageView) findViewById(R.id.appbar_art);
        menuFab = (FloatingActionMenu) findViewById(R.id.menu_fab);
        mFrameContainer = (FrameLayout) findViewById(R.id.frame_container);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
    }

    /**
     * --------------------------------------------------------------------------------------------
     * INICIALIZAÇÃO DOS FRAGMENTS
     * Instancias de Fragments e inicialização de FragmentManager
     * --------------------------------------------------------------------------------------------
     */
    public void initFragments(){
        mFragmentManager = getFragmentManager();

        if (mPlaylistsFragment == null) {
            mPlaylistsFragment = PlaylistsFragment.newInstance();
        }

        if (mTracksFragment == null) {
            mTracksFragment = TracksFragment.newInstance();
        }

        if (mLoginFragment == null) {
            mLoginFragment = LoginFragment.newInstance();
        }
    }

    /**
     * --------------------------------------------------------------------------------------------
     * ANIMAÇÕES E FUNCIONALIDADES DA UI
     * --------------------------------------------------------------------------------------------
     */

    /**
     * Override no método onBackPressed para esconder o FAB, alterar o título e recolher a Toolbar
     * caso o usuário esteja voltando ao primeiro nível dos Fragments (fragment inicial)
     */
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
        mAppBarLayout.setExpanded(false, true);
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
     * --------------------------------------------------------------------------------------------
     * IMPLEMENTAÇÃO DAS INTERAÇÕES COM FRAGMENTS
     * --------------------------------------------------------------------------------------------
     */

    /**
     * --------------------------------------------------------------------------------------------
     * PLAYLISTSFRAGMENT - Fragment com as Playlists do usuário
     * --------------------------------------------------------------------------------------------
     */

    /**
     * Trata a seleção de uma Playlist no RecyclerView do PlaylistFragment.
     * @param playlist playlist selecionada
     */
    public void onPlaylistSelected(PlaylistItem playlist){
        Snackbar.make(mCoordinatorLayout, "DEBUG: Playlist selecionada " + playlist.getName(), Snackbar.LENGTH_LONG).show();
        mPlaylistSelected = playlist;
        showTracksFragment();
    }

    /**
     * --------------------------------------------------------------------------------------------
     * TRACKSFRAGMENT - Fragment com as músicas do usuário
     * --------------------------------------------------------------------------------------------
     */

    /**
     * Trata da seleção de uma Track no RecyclerView do TrackFragment.
     * Este método é executado quando o usuário toca na linha da música e iniciará a reprodução da
     * Playlist carregada na música selecionada. Funciona apenas no Modo Solo.
     * @param track Música selecionada
     */
    public void onTrackSelected(TrackItem track) {
        Snackbar.make(mCoordinatorLayout, "DEBUG: Track SELECIONADA: " + track.getName(), Snackbar.LENGTH_LONG).show();
    }

    /**
     * Trata da interação do usuário com o botão de "Adicionar Música". Funciona no Modo Festa.
     * @param track Música selecionada
     */
    public void onTrackAdded(TrackItem track) {
        Snackbar.make(mCoordinatorLayout, "DEBUG: Track ADICIONADA: " + track.getName(), Snackbar.LENGTH_LONG).show();
    }


    /**
     * --------------------------------------------------------------------------------------------
     * LOGINFRAGMENT - Fragment para login no Spotify
     * --------------------------------------------------------------------------------------------
     */

    public void onLoginButtonPressed(){
        Snackbar.make(mCoordinatorLayout, "DEBUG: Botão de login pressionado", Snackbar.LENGTH_LONG).show();
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(SpotifyManager.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "playlist-read-private"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    public void onLoginSkipButtonPressed(){
        Snackbar.make(mCoordinatorLayout, "DEBUG: Botão para pular login pressionado", Snackbar.LENGTH_LONG).show();
    }

    /**
     * --------------------------------------------------------------------------------------------
     * IMPLEMENTAÇÕES GERAIS E DEBUGGING
     * Métodos de debug, possivelmente serão movidos a outras classes ou removidos...
     * TODO: Refatorar os "loads" dos Fragments usando generalização
     * --------------------------------------------------------------------------------------------
     */

    /**
     * Configura interface e toolbar (título, fundo e botões) para a View de tracks de uma playlist.
     */
    private void showPlaylistsFragment(){
        mCollapsingToolbarLayout.setTitle(getString(R.string.title_activity_base));
        switchFragment(mPlaylistsFragment);
    }

    private void showTracksFragment(){
        mCollapsingToolbarLayout.setTitle(mPlaylistSelected.getName());
        switchFragment(mTracksFragment);
        Glide.with(this)
                .load("https://mosaic.scdn.co/640/134cd5ccaef9d411eba33df9542db9ba731aaf98c4b4399d9b7c6f61b6a6ee70c616bc1a985c7ab8e337f3661f68bc4d96a554de0ad7988d65edb25aec04f9acee17a7576f939eb5aa317d20c6322494")
                .into(mToolbarArt);

        Glide.with(this)
                .load("https://mosaic.scdn.co/640/134cd5ccaef9d411eba33df9542db9ba731aaf98c4b4399d9b7c6f61b6a6ee70c616bc1a985c7ab8e337f3661f68bc4d96a554de0ad7988d65edb25aec04f9acee17a7576f939eb5aa317d20c6322494")
                .bitmapTransform(new BlurTransformation(this, GLIDE_BLUR_RADIUS))
                .into(mToolbarBackground);
    }

    private void showLoginFragment(){
        mCollapsingToolbarLayout.setTitle(getString(R.string.title_spotify_login));
        switchFragment(mLoginFragment);
    }

    private void switchFragment(Fragment fragment){
        mFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .addToBackStack(null)
                .commit();

        if (fragment instanceof TracksFragment) {
            menuFab.setVisibility(View.VISIBLE);
            mAppBarLayout.setExpanded(true);
        } else {
            menuFab.setVisibility(View.GONE);
            collapseToolbar();
        }
    }

    /**
     * Método executado quando o Authorization Flow da API do Spotify é encerrado.
     * Deve retornar um código de autenticação, necessário para todas as funcionalidades do Spotify.
     * @param requestCode Código para verificar se o resultado é deste app
     * @param resultCode Código de autenticação retornado
     * @param intent Necessário para herança.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case CODE:
                    String accessToken = response.getAccessToken();
                    authCode = response.getCode();

                    PrefsManager.setCodeToPrefs(this, authCode);

                    new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... params) {
                            SpotifyManager.setAuthCredentials(getApplication());
                            return null;
                        }
                    }.execute();

                    wasLoginPrompted = true;

                    Log.i(TAG, "Access token obtido: " + accessToken);
                    Log.i(TAG, "Code obtido: " + authCode);
                    break;
                case TOKEN:
                    Log.i(TAG, "Recebido resultado via Token");
                    Log.i(TAG, "Token recebido: " + response.getAccessToken());
                    break;
                // Auth flow returned an error
                case ERROR:
                    Log.i(TAG, "Erro na Autenticação: " + response.getError());
                    break;
                default:

            }
        }
    }


    /**
     * --------------------------------------------------------------------------------------------
     * ASYNCTASKS
     * AsyncTasks necessários para carregamento de informações fora da UI Thread.
     * --------------------------------------------------------------------------------------------
     */

//    public class AsyncLogin extends AsyncTask<Void, Void, Void>{
//        ProgressDialog pd;
//        public AsyncLogin(){
//            super();
//        }
//        @Override
//        protected void onPreExecute(){
//            pd = new ProgressDialog(BaseActivity.this);
//            pd.show();
//        }
//        @Override
//        protected Void doInBackground(Void... params){
//            SpotifyManager.setAuthCredentials(getApplication());
//            return null;
//        }
//        @Override
//        protected void onPostExecute(){
//            super.onPostExecute(null);
//            if (pd.isShowing()) {
//                pd.dismiss();
//            }
//        }
//
//    }

}
