package com.xrdev.musicastmaterial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.xrdev.musicastmaterial.Application;
import com.xrdev.musicastmaterial.R;
import com.xrdev.musicastmaterial.adapters.PlaylistAdapter;
import com.xrdev.musicastmaterial.adapters.TrackAdapter;
import com.xrdev.musicastmaterial.apis.SpotifyManager;
import com.xrdev.musicastmaterial.apis.YouTubeManager;
import com.xrdev.musicastmaterial.fragments.LoginFragment;
import com.xrdev.musicastmaterial.fragments.TracksFragment;
import com.xrdev.musicastmaterial.interfaces.ILogin;
import com.xrdev.musicastmaterial.interfaces.IPlaylist;
import com.xrdev.musicastmaterial.fragments.PlaylistsFragment;
import com.xrdev.musicastmaterial.interfaces.ITrack;
import com.xrdev.musicastmaterial.models.LocalQueue;
import com.xrdev.musicastmaterial.models.PlaylistItem;
import com.xrdev.musicastmaterial.models.Token;
import com.xrdev.musicastmaterial.models.TrackItem;
import com.xrdev.musicastmaterial.utils.JsonConverter;
import com.xrdev.musicastmaterial.utils.PrefsManager;

import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class BaseActivity extends AppCompatActivity implements IPlaylist, ITrack, ILogin,
        Cast.MessageReceivedCallback, SessionManagerListener  {

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
    Toolbar mToolbar;

    // Layout e Action Buttons

    CoordinatorLayout mCoordinatorLayout;
    FloatingActionMenu menuFab;
    FrameLayout mFrameContainer;
    ProgressBar mProgressBar;
    ProgressDialog pd;
    FloatingActionButton mFabAddToQueue;
    FloatingActionButton mFabSwapPlaylist;
    FloatingActionButton mFabBecomeHost;
    FloatingActionButton mFabStopHosting;
    FloatingActionButton mFabSwitchMode;
    FloatingActionButton mFabLogout;
    FloatingActionButton mFabLogin;
    SlidingUpPanelLayout mSlidingUpLayout;
    TextView mAppbarInfo;

    // Google Cast
    CastContext mCastContext;
    SessionManager mSessionManager;
    CastSession mCastSession;
    String mCastNamespace;
    JsonConverter json = new JsonConverter(this);

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
    LocalQueue mLocalQueue;
    String mAdmin;

    // Dados
    PlaylistItem mPlaylistSelected;
    PlaylistAdapter mPlaylistAdapter;
    TrackAdapter mTrackAdapter;

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
        mCastContext = CastContext.getSharedInstance(this);
        mCastNamespace = getString(R.string.cast_channel_namespace);
        mSessionManager = mCastContext.getSessionManager();
        initViews();
        // setToolbarMenu();
        initFragments();
        setupMenuFabAnim();


        // TODO: Debugging, alterar o comportamento inicial do app
        showPlaylistsFragment();
        //showLoginFragment();

    }

    @Override
    protected void onResume() {
        try {
            mCastSession = mSessionManager.getCurrentCastSession();
            mSessionManager.addSessionManagerListener(this);
            if (mCastSession != null)
                mCastSession.setMessageReceivedCallbacks(mCastNamespace, this);
        } catch (IOException e) {
            Log.e(TAG, "Exception criando o Channel", e);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSessionManager.removeSessionManagerListener(this);
        mCastSession = null;
    }

    /**
     * --------------------------------------------------------------------------------------------
     * INICIALIZAÇÃO DAS VIEWS
     * Bind dos elementos de layout com os objetos
     * --------------------------------------------------------------------------------------------
     */
    private void initViews(){
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarBackground = (ImageView) findViewById(R.id.appbar_background);
        mToolbarArt = (ImageView) findViewById(R.id.appbar_art);
        menuFab = (FloatingActionMenu) findViewById(R.id.menu_fab);
        mFrameContainer = (FrameLayout) findViewById(R.id.frame_container);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mAppbarInfo = (TextView) findViewById(R.id.text_appbar_info);
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
        setSupportActionBar(mToolbar);

        // Fontes da Toolbar:
        final Typeface tfProximaSemi = Typeface.createFromAsset(this.getAssets(), "fonts/ProximaNova-SemiBold.otf");
        final Typeface tfProximaRegular = Typeface.createFromAsset(this.getAssets(), "fonts/ProximaNova-Regular.otf");
        mCollapsingToolbarLayout.setExpandedTitleTypeface(tfProximaSemi);
        mCollapsingToolbarLayout.setCollapsedTitleTypeface(tfProximaSemi);
        mAppbarInfo.setTypeface(tfProximaRegular);

        //mSlidingUpLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }


    /**
     * --------------------------------------------------------------------------------------------
     * INICIALIZAÇÃO DOS FRAGMENTS
     * Instancias de Fragments, Transitions e inicialização de FragmentManager
     * --------------------------------------------------------------------------------------------
     */
    private void initFragments(){
        mPlaylistAdapter = new PlaylistAdapter(this);
        mTrackAdapter = new TrackAdapter(this);

        mFragmentManager = getFragmentManager();

        if (mPlaylistsFragment == null) {
            mPlaylistsFragment = PlaylistsFragment.newInstance();
            mPlaylistsFragment.setAdapter(mPlaylistAdapter);
        }

        if (mTracksFragment == null) {
            mTracksFragment = TracksFragment.newInstance();
            mTracksFragment.setAdapter(mTrackAdapter);
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

        // Cancelar os Loaders caso estejam com algum load em andamento.
        if (mPlaylistsLoader != null)
            mPlaylistsLoader.cancel(true);
        if (mTracksLoader != null)
            mTracksLoader.cancel(true);
        mProgressBar.setVisibility(View.GONE);

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

    private void collapseToolbar() {
        mAppBarLayout.setExpanded(false, true);
    }

    /**
     * Configura a animação customizada do Floating Action Button.
     */
    private void setupMenuFabAnim() {

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
    protected void setupMenuFabButtons(){
        menuFab.close(true);
        menuFab.removeAllMenuButtons();
        // TODO: incluir a lógica para mostrar os botões corretos de acordo com Host/Guest e Mode.
        menuFab.addMenuButton(mFabBecomeHost);
    }

    protected static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressdialog);
        // dialog.setMessage(Message);
        return dialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                menu,
                R.id.media_route_menu_item);

        return true;
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
    public void showPlaylistsFragment(){
        mCollapsingToolbarLayout.setTitle(getString(R.string.title_activity_base));
        setupMenuFabButtons();
        switchFragment(mPlaylistsFragment);
        if (!hasLoadedPlaylists)
            mPlaylistsLoader = new PlaylistsLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
    }

    private void showTracksFragment(){
        mCollapsingToolbarLayout.setTitle(mPlaylistSelected.getName());
        setupMenuFabButtons();
        menuFab.addMenuButton(mFabAddToQueue);
        menuFab.addMenuButton(mFabSwapPlaylist);
        switchFragment(mTracksFragment);
        if (mPlaylistSelected != null) {

            Glide.with(this)
                    .load(mPlaylistSelected.getImageUrl())
                    .into(mToolbarArt);

            Glide.with(this)
                    .load(mPlaylistSelected.getImageUrl())
                    .bitmapTransform(new BlurTransformation(this, Application.GLIDE_BLUR_RADIUS))
                    .into(mToolbarBackground);
            mTracksLoader = new TracksLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        }

    }

    private void showLoginFragment(){
        mCollapsingToolbarLayout.setTitle(getString(R.string.title_spotify_login));
        menuFab.setVisibility(View.GONE);
        switchFragment(mLoginFragment);
    }

    private void switchFragment(Fragment fragment){
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        if (!(fragment instanceof TracksFragment))
            collapseToolbar();

        if (!(fragment instanceof PlaylistsFragment))
            transaction.addToBackStack(null);

        transaction.commit();
    }


    /**
     * --------------------------------------------------------------------------------------------
     * GOOGLE CAST
     * Inicialização do Google Cast e tratamento de eventos de sessão
     * --------------------------------------------------------------------------------------------
     */

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace,
                                  String message) {
        Log.d(TAG, "onMessageReceived: " + message);
        // TODO: desenvolver lógica.
    }

    private void sendMessage(String message){
        if (mCastSession != null){
            try {
                mCastSession.sendMessage(mCastNamespace, message);
            } catch (Exception e) {
                Log.e(TAG, "Exception enviando mensagem", e);
            }
        }
    }

    @Override
    public void onSessionStarted(Session session, String sessionId) {
        invalidateOptionsMenu();
        Log.d(TAG, "Cast Session Started");
        try {
            mCastSession = mSessionManager.getCurrentCastSession();
            mCastSession.setMessageReceivedCallbacks(mCastNamespace, this);
            sendMessage(json.makeGeneric(JsonConverter.TYPE_GET_STATUS));
        } catch (IOException e) {
            Log.e(TAG,"Erro no onSessionStarted " + e);
        }

    }

    @Override
    public void onSessionResumed(Session session, boolean wasSuspended) {
        invalidateOptionsMenu();
        Log.d(TAG, "Cast Session Resumed");
        try {
            mCastSession = mSessionManager.getCurrentCastSession();
            mCastSession.setMessageReceivedCallbacks(mCastNamespace, this);
            sendMessage(json.makeGeneric(JsonConverter.TYPE_GET_STATUS));
        } catch (IOException e) {
            Log.e(TAG,"Erro no onSessionStarted " + e);
        }
    }

    @Override
    public void onSessionEnded(Session session, int error) {
        Log.d(TAG, "Cast Session Ended");
        mCastSession = null;
    }

    @Override
    public void onSessionStarting(Session session) {
        Log.d(TAG, "Cast Session Starting");
    }

    @Override
    public void onSessionStartFailed(Session session, int i) {
        Log.d(TAG, "Cast Session Start Failed");
    }

    @Override
    public void onSessionEnding(Session session) {
        Log.d(TAG, "Cast Session Ending");
    }

    @Override
    public void onSessionResuming(Session session, String s) {
        Log.d(TAG, "Cast Session Resuming");
    }

    @Override
    public void onSessionResumeFailed(Session session, int i) {
        Log.d(TAG, "Cast Session Resume Failed");
    }

    @Override
    public void onSessionSuspended(Session session, int i) {
        Log.d(TAG, "Cast Session Suspended");
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

        mLocalQueue = Application.getQueue(playlist.getPlaylistId());
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
        Snackbar.make(mCoordinatorLayout, "DEBUG: " + track.getName() + "; video: " + track.getYoutubeId(), Snackbar.LENGTH_LONG).show();
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
     * LÓGICA
     * Lógica da aplicação
     * --------------------------------------------------------------------------------------------
     */
    private boolean isAdmin(){
        if (mAdmin == null)
            return false;
        return mAdmin.equals(PrefsManager.getUUID(this));
    }

    public String getAdmin(){
        return mAdmin;
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
    public class PlaylistsLoader extends AsyncTask<String, Integer, ArrayList<PlaylistItem>>{
        int playlistsCount;
        Token token;
        ArrayList<PlaylistItem> playlists;
        public PlaylistsLoader(){
            super();
        }
        @Override
        protected void onPreExecute(){
            token = PrefsManager.getTokenFromPrefs(getApplicationContext());
            mProgressBar.setVisibility(View.VISIBLE);
            pd = createProgressDialog(BaseActivity.this);
            pd.setMessage(getString((R.string.pd_loading)));
            pd.show();
            mPlaylistAdapter.clear();
        }
        @Override
        protected ArrayList<PlaylistItem> doInBackground(String... args){
            if (token == null || !token.isValid()) // Token inexistente ou inválido.
                return null;

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
        @Override
        protected void onProgressUpdate(Integer... progress){
            super.onProgressUpdate(progress);
            mProgressBar.setMax(playlistsCount);
            mProgressBar.setProgress(mRequestOffset);
            if (pd.isShowing())
                pd.dismiss();
            for (PlaylistItem item : playlists) {
                    mPlaylistAdapter.add(item);
            }
            hasLoadedPlaylists = true;
        }

        @Override
        protected void onPostExecute(ArrayList<PlaylistItem> items) {
            super.onPostExecute(items);
            mProgressBar.setVisibility(View.GONE);
            if (pd.isShowing())
                pd.dismiss();
            if (items == null) { // Resultado null por algum motivo - falha no Token.
                if (token == null && (!hasSkippedLogin || isAdmin()))
                    // Token inexistente, usuário não pulou login ou é o Admin (login obrigatório)
                    showLoginFragment();
                if (token != null && !token.isValid())
                    // Token existente, mas expirado. Tentar obter novo token automaticamente pela API.
                    onLoginButtonPressed();
            } else {
                hasLoadedPlaylists = true;
            }
        }
    }

    public class TracksLoader extends AsyncTask<String, Integer, ArrayList<TrackItem>>{
        int tracksCount;
        int tracksChecked;
        int foundCount;
        Token token;
        ArrayList<TrackItem> tracks;
        public TracksLoader(){
            super();
        }
        @Override
        protected void onPreExecute(){
            token = PrefsManager.getTokenFromPrefs(getApplicationContext());
            mProgressBar.setVisibility(View.VISIBLE);
            pd = createProgressDialog(BaseActivity.this);
            pd.setMessage(getString((R.string.pd_loading)));
            pd.show();
            mTrackAdapter.clear();
            mRequestOffset = 0;
            tracksCount = mPlaylistSelected.getNumTracksInt();
            mAppBarLayout.setExpanded(true);
            mAppbarInfo.setVisibility(View.INVISIBLE);
        }
        @Override
        protected ArrayList<TrackItem> doInBackground(String... args){
            if (token == null || !token.isValid() || mPlaylistSelected == null) // Token inválido, nenhuma playlist selecionada.
                return null;

            // Inicializar variáveis
            mSpotifyManager.setAccessToken(token);
            Log.d(TAG, "Total de Músicas na Playlist: " + tracksCount);

            // Recuperar músicas do Spotify
            while (mRequestOffset < tracksCount) {
                tracks = mSpotifyManager.getPlaylistTracks(mPlaylistSelected, REQUEST_LIMIT, mRequestOffset);
                if (isCancelled()){
                    mTrackAdapter.clear();
                    break;
                }
                mRequestOffset += REQUEST_LIMIT;
                publishProgress(0); // Indicará ao onProgressUpdate que a música deve ser adicionada ao Adapter.
            }

            // Procurar vídeos correspondentes
            for (int i = 0; i < mTrackAdapter.getItemCount(); i++) {
                if(isCancelled())
                    break;

                TrackItem currentItem = mTrackAdapter.getItem(i);
                if (!currentItem.wasCached || !currentItem.wasFound()) {
                    if (YouTubeManager.queryVideo(getApplicationContext(), currentItem, mLocalQueue))
                        foundCount++;
                } else
                    foundCount++;
                tracksChecked++;
                publishProgress(1); // Música já estava no Adapter e não deve ser adicionada novamente.
            }

            // Atualizar as correspondências antigas.
            for (int i = 0; i < mTrackAdapter.getItemCount(); i++) {
                if(isCancelled())
                    break;
                TrackItem currentItem = mTrackAdapter.getItem(i);
                if (currentItem.isRefreshNeeded())
                    YouTubeManager.queryVideo(getApplication(), currentItem, mLocalQueue);
            }
            return tracks;
        }
        @Override
        protected void onProgressUpdate(Integer... progress){
            super.onProgressUpdate(progress);
            mTrackAdapter.notifyDataSetChanged();
            mProgressBar.setMax(tracksCount);
            mProgressBar.setSecondaryProgress(mRequestOffset);
            mProgressBar.setProgress(tracksChecked);
            mAppbarInfo.setVisibility(View.VISIBLE);
            mAppbarInfo.setText(tracksCount + getString(R.string.tracks_caps) + " • " + foundCount + getString(R.string.videos_caps));
            if (pd.isShowing())
                pd.dismiss();
            if (progress[0] == 0) { // onProgressUpdate chamado quando as músicas foram recuperadas do Spotify.
                for (TrackItem item : tracks) {
                    mTrackAdapter.add(item);
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<TrackItem> items) {
            super.onPostExecute(items);
            mProgressBar.setVisibility(View.GONE);
            if (pd.isShowing())
                pd.dismiss();
            if (items == null) { // Resultado null por algum motivo - falha no Token.
                if (token == null && (!hasSkippedLogin || isAdmin()))
                    // Token inexistente, usuário não pulou login ou é o Admin (login obrigatório)
                    showLoginFragment();
                if (token != null && !token.isValid())
                    // Token existente, mas expirado. Tentar obter novo token automaticamente pela API.
                    //(O app vai retornar para a View de Playlists ao final do método)
                    onLoginButtonPressed();
            }
        }
    }


}
