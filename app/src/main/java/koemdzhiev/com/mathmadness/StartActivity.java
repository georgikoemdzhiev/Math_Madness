package koemdzhiev.com.mathmadness;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

import koemdzhiev.com.mathmadness.utils.Constants;


public class StartActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = StartActivity.class.getSimpleName();
    private ImageView mPlay;
    private ImageView mPlayAdvancedMode;
    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mAchievements;
    private Button mLeaderboard;
//    private SharedPreferences mSharedPreferences;
//    private SharedPreferences.Editor mEditor;
    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;

    // Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Set to true to automatically start the sign in flow when the Activity starts.
    // Set to false to require the user to click the button in order to sign in.
    private boolean mAutoStartSignInFlow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
//        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        mEditor = mSharedPreferences.edit();
        mPlayAdvancedMode = (ImageView)findViewById(R.id.advanced_mode_button);
        mPlayAdvancedMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play animations
//                YoYo.with(Techniques.Pulse)
//                        .duration(200)
//                        .playOn(findViewById(R.id.advanced_mode_button));
                incrementAchievement();
                
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra(Constants.KEY_IS_ADVANCED_MODE, true);
                startActivity(intent);
            }
        });
        mSignInButton = (SignInButton)findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
        mSignOutButton = (Button)findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);
// Create the Google Api Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        mPlay = (ImageView)findViewById(R.id.startGameView);
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //play animations
//                YoYo.with(Techniques.Pulse)
//                        .duration(200)
//                        .playOn(findViewById(R.id.startGameView));
                incrementAchievement();

                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra(Constants.KEY_IS_ADVANCED_MODE,false);
                startActivity(intent);
            }
        });

        mAchievements = (Button) findViewById(R.id.show_achievements);
        mAchievements.setOnClickListener(this);
        mLeaderboard = (Button) findViewById(R.id.show_leaderboard);
        mLeaderboard.setOnClickListener(this);
    }

    private void incrementAchievement() {
        //increment the incremental achievement by 1 for each time the user play the game
        if (mGoogleApiClient.isConnected()) {
            Games.Achievements.increment(mGoogleApiClient, getString(R.string.addicted_200_times_play_achievement), 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(isNetworkAvailable()) {
            mGoogleApiClient.connect();
        }
    }
    protected void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    // Shows the "sign in" bar (explanation and button).
    private void showSignInBar() {
        Log.d(TAG, "Showing sign in bar");
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_button).setVisibility(View.INVISIBLE);
    }

    // Shows the "sign out" bar (explanation and button).
    private void showSignOutBar() {
        Log.d(TAG, "Showing sign out bar");
        findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // start the sign-in flow
            Log.d(TAG, "Sign-in button clicked");
            mSignInClicked = true;
            mGoogleApiClient.connect();

        }else if (view.getId() == R.id.sign_out_button) {
            // sign out.
            Log.d(TAG, "Sign-out button clicked");
            mSignInClicked = false;
            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            showSignInBar();

        }else if (view.getId() == R.id.show_achievements){
            if(mGoogleApiClient.isConnected()) {
                startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), 1);
            }else{
                alertUserForGoogleSignUp();
            }
        }else if(view.getId() == R.id.show_leaderboard){
            if(mGoogleApiClient.isConnected()) {
                startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(
                        mGoogleApiClient), 2);
            }else{
                alertUserForGoogleSignUp();
            }
        }
    }

    private void alertUserForGoogleSignUp() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.title)
                .content(R.string.content)
                .positiveText(R.string.agree)
                .show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected() called. Sign in successful!");
        showSignOutBar();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }
        showSignInBar();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
                    + responseCode + ", intent=" + intent);
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (responseCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this,requestCode,responseCode, R.string.signin_other_error);
            }
        }
    }
}
