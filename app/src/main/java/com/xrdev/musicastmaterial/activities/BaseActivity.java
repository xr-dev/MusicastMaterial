package com.xrdev.musicastmaterial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.xrdev.musicastmaterial.Application;
import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.adapters.PlaylistAdapter;
import com.xrdev.musicastmaterial.apis.SpotifyManager;
import com.xrdev.musicastmaterial.fragments.LoginFragment;
import com.xrdev.musicastmaterial.fragments.TracksFragment;
import com.xrdev.musicastmaterial.interfaces.ILogin;
import com.xrdev.musicastmaterial.interfaces.IPlaylist;
import com.xrdev.musicastmaterial.fragments.PlaylistsFragment;
import com.xrdev.musicastmaterial.interfaces.ITrack;
import com.xrdev.musicastmaterial.models.PlaylistItem;
import com.xrdev.musicastmaterial.models.Token;
import com.xrdev.musicastmaterial.models.TrackItem;
import com.xrdev.musicastmaterial.utils.PrefsManager;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class BaseActivity extends Activity implements IPlaylist, ITrack, ILogin {

    final static String TAG = "MusicastMaterial";

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

    // Layout e Action Buttons
    CoordinatorLayout mCoordinatorLayout;
    FloatingActionMenu menuFab;
    FrameLayout mFrameContainer;
    ProgressBar mProgressBar;
    FloatingActionButton mFabAddToQueue;
    FloatingActionButton mFabSwapPlaylist;
    FloatingActionButton mFabBecomeHost;
    FloatingActionButton mFabStopHosting;
    FloatingActionButton mFabSwitchMode;
    FloatingActionButton mFabLogout;
    FloatingActionButton mFabLogin;
    SlidingUpPanelLayout mSlidingUpLayout;

    //AsyncTasks
    AsyncTask mPlaylistsLoader;
    AsyncTask mTracksLoader;

    // Controle
    boolean isChromecastConnected;
    boolean hasSkippedLogin = false;
    boolean hasRefusedAdmin = false;
    boolean isAskingForAdmin;
    boolean hasLoadedPlaylists = false;
    private static int REQUEST_LIMIT = 20;
    int mRequestOffset;

    // Dados
    PlaylistItem mPlaylistSelected;
    PlaylistAdapter mPlaylistAdapter;

    // Spotify Auth
    SpotifyManager mSpotifyManager;
    int REQUEST_CODE = SpotifyManager.REQUEST_CODE;
    String REDIRECT_URI = SpotifyManager.REDIRECT_URI;
    boolean wasLoginPrompted = false;
    String authCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mSpotifyManager = new SpotifyManager(getApplicationContext());
        initViews();
        initFragments();
        setupMenuFabAnim();


        // TODO: Debugging, alterar o comportamento inicial do app
        showPlaylistsFragment();
        //showLoginFragment();

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
        mProgressBar = (ProgressBar) findViewById(R.id.pbar_linear);
        mSlidingUpLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mFabAddToQueue = (FloatingActionButton) findViewById(R.id.fab_add_pl_to_queue);
        mFabSwapPlaylist = (FloatingActionButton) findViewById(R.id.fab_swap_pl);
        mFabBecomeHost = (FloatingActionButton) findViewById(R.id.fab_become_host);
        mFabStopHosting = (FloatingActionButton) findViewById(R.id.fab_stop_hosting);
        mFabSwitchMode = (FloatingActionButton) findViewById(R.id.fab_switch_mode);
        mFabLogout = (FloatingActionButton) findViewById(R.id.fab_logout);
        mFabLogin = (FloatingActionButton) findViewById(R.id.fab_login);
        mAppBarLayout.setExpanded(false, false);
        mProgressBar.setVisibility(View.GONE);
        //mSlidingUpLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    /**
     * --------------------------------------------------------------------------------------------
     * INICIALIZAÇÃO DOS FRAGMENTS
     * Instancias de Fragments e inicialização de FragmentManager
     * --------------------------------------------------------------------------------------------
     */
    public void initFragments(){
        mPlaylistAdapter = new PlaylistAdapter(this);

        mFragmentManager = getFragmentManager();

        if (mPlaylistsFragment == null) {
            mPlaylistsFragment = PlaylistsFragment.newInstance();
            mPlaylistsFragment.setAdapter(mPlaylistAdapter);
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
     * ANIMAÇÕES, FUNCIONALIDADES DA UI E ACTION BUTTONS
     * --------------------------------------------------------------------------------------------
     */

    /**
     * Override no método onBackPressed para esconder o FAB, alterar o título e recolher a Toolbar
     * caso o usuário esteja voltando ao primeiro nível dos Fragments (fragment inicial)
     */
    @Override
    public void onBackPressed(){
        if(mSlidingUpLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingUpLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            if (mFragmentManager.getBackStackEntryCount() > 0) {
                mFragmentManager.popBackStack();
                mCollapsingToolbarLayout.setTitle(getString(R.string.title_activity_base));
                collapseToolbar();
                setupMenuFabButtons();
            } else {
                //super.onBackPressed();
                this.moveTaskToBack(true);
            }
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
     * Configura as opções padrão do Floating Action Button
     */
    public void setupMenuFabButtons(){
        menuFab.close(true);
        menuFab.removeAllMenuButtons();
        // TODO: incluir a lógica para mostrar os botões corretos de acordo com Host/Guest e Mode.
        menuFab.addMenuButton(mFabBecomeHost);
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
        hasSkippedLogin = true;
    }

    /**
     * --------------------------------------------------------------------------------------------
     * GERENCIA DE FRAGMENTS
     * Configuração e exibição de fragments
     * --------------------------------------------------------------------------------------------
     */

    /**
     * Configura interface e toolbar (título, fundo e botões) para a View de tracks de uma playlist.
     */
    private void showPlaylistsFragment(){
        mCollapsingToolbarLayout.setTitle(getString(R.string.title_activity_base));
        setupMenuFabButtons();
        switchFragment(mPlaylistsFragment);
        if (!hasLoadedPlaylists)
            mPlaylistsLoader = new PlaylistLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

    }

    private void showTracksFragment(){
        mCollapsingToolbarLayout.setTitle(mPlaylistSelected.getName());
        setupMenuFabButtons();
        menuFab.addMenuButton(mFabAddToQueue);
        menuFab.addMenuButton(mFabSwapPlaylist);
        switchFragment(mTracksFragment);
        Glide.with(this)
                .load("https://mosaic.scdn.co/640/134cd5ccaef9d411eba33df9542db9ba731aaf98c4b4399d9b7c6f61b6a6ee70c616bc1a985c7ab8e337f3661f68bc4d96a554de0ad7988d65edb25aec04f9acee17a7576f939eb5aa317d20c6322494")
                .into(mToolbarArt);

        Glide.with(this)
                .load("https://mosaic.scdn.co/640/134cd5ccaef9d411eba33df9542db9ba731aaf98c4b4399d9b7c6f61b6a6ee70c616bc1a985c7ab8e337f3661f68bc4d96a554de0ad7988d65edb25aec04f9acee17a7576f939eb5aa317d20c6322494")
                .bitmapTransform(new BlurTransformation(this, Application.GLIDE_BLUR_RADIUS))
                .into(mToolbarBackground);
    }

    private void showLoginFragment(){
        mCollapsingToolbarLayout.setTitle(getString(R.string.title_spotify_login));
        switchFragment(mLoginFragment);
    }

    private void switchFragment(Fragment fragment){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        if (fragment instanceof TracksFragment)
            mAppBarLayout.setExpanded(true);
        else
            collapseToolbar();

        if (!(fragment instanceof PlaylistsFragment))
            transaction.addToBackStack(null);

        transaction.commit();
    }

    /**
     * --------------------------------------------------------------------------------------------
     * LÓGICA
     * Lógica e controles da aplicação
     * --------------------------------------------------------------------------------------------
     */
    private boolean isAdmin(){
        // TODO: construir lógica
        return false;
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
                case CODE:
                    Log.e(TAG,"[Spotify] Erro na Autenticação: resposta de tipo incorreto (CODE)");
                    break;
                case TOKEN:
                    Log.d(TAG, "Recebido resultado via Token");
                    Log.d(TAG, "Token recebido: " + response.getAccessToken());
                    Log.d(TAG, "Expira em: " + response.getExpiresIn());
                    PrefsManager.setTokenToPrefs(this, new Token(response.getAccessToken(),response.getExpiresIn()));
                    showPlaylistsFragment();
                    break;
                // Auth flow returned an error
                case ERROR:
                    Log.e(TAG, "[Spotify] Erro na Autenticação: " + response.getError());
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
    public class PlaylistLoader extends AsyncTask<String, Integer, ArrayList<PlaylistItem>>{
        int playlistsCount;
        Token token;
        ArrayList<PlaylistItem> playlists;
        public PlaylistLoader(){
            super();
        }
        @Override
        protected void onPreExecute(){
            token = PrefsManager.getTokenFromPrefs(getApplicationContext());
            mProgressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected ArrayList<PlaylistItem> doInBackground(String... args){
            if (token == null || !token.isValid()) // Token inexistente ou inválido.
                return null;
            else { // Token válido
                mSpotifyManager.setAccessToken(token);
                mRequestOffset = 0;
                playlistsCount = mSpotifyManager.getUserPlaylistsCount();
                Log.d(TAG, "Total de Playlists: " + playlistsCount);

                while (mRequestOffset < playlistsCount) {
                    playlists = mSpotifyManager.getUserPlaylists(REQUEST_LIMIT, mRequestOffset);
                    if (isCancelled())
                        break;
                    mRequestOffset += REQUEST_LIMIT;
                    publishProgress(mRequestOffset);
                }
                return playlists;
            }
        }
        @Override
        protected void onProgressUpdate(Integer... progress){
            super.onProgressUpdate(progress);
            mProgressBar.setMax(playlistsCount);
            mProgressBar.setProgress(mRequestOffset);
            for (PlaylistItem item : playlists) {
                    mPlaylistAdapter.add(item);
            }

        }

        @Override
        protected void onPostExecute(ArrayList<PlaylistItem> items) {
            super.onPostExecute(items);
            mProgressBar.setVisibility(View.GONE);
            if (items == null) { // Resultado null por algum motivo - falha no Token.
                if (token == null && (!hasSkippedLogin || isAdmin()))
                    // Token inexistente, usuário não pulou login ou é o Admin (login obrigatório)
                    showLoginFragment();
                if (token != null && !token.isValid())
                    // Token existente, mas expirado. Tentar obter novo token automaticamente pela API.
                    onLoginButtonPressed();
            }
            hasLoadedPlaylists = true;

        }
    }

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
