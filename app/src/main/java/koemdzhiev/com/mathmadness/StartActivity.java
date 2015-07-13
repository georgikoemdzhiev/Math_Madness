package koemdzhiev.com.mathmadness;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import koemdzhiev.com.mathmadness.utils.Constants;


public class StartActivity extends BaseGameActivity implements View.OnClickListener{
    private ImageView mPlay;
    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mAchievements;
    private Button mLeaderboard;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        mSignInButton = (SignInButton)findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
        mSignOutButton = (Button)findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);

        mPlay = (ImageView)findViewById(R.id.startGameView);
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play animations
                YoYo.with(Techniques.Pulse)
                        .duration(200)
                        .playOn(findViewById(R.id.startGameView));
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mAchievements = (Button) findViewById(R.id.show_achievements);
        mAchievements.setOnClickListener(this);
        mLeaderboard = (Button) findViewById(R.id.show_leaderboard);
        mLeaderboard.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnected = mSharedPreferences.getBoolean(Constants.KEY_IS_Google_Api_Client_connected,false);
        if(isConnected){
            if(mSignInButton.getVisibility() == View.VISIBLE){
                mSignInButton.setVisibility(View.INVISIBLE);
            }
            if(mSignOutButton.getVisibility() == View.INVISIBLE){
                mSignOutButton.setVisibility(View.VISIBLE);
            }
        }
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
    public void onSignInFailed() {
        mSignInButton.setVisibility(View.VISIBLE);
        mSignOutButton.setVisibility(View.GONE);
        //save to shared preferences
        mEditor.putBoolean(Constants.KEY_IS_Google_Api_Client_connected, false);
        mEditor.apply();
    }

    @Override
    public void onSignInSucceeded() {
        mSignInButton.setVisibility(View.GONE);
        mSignOutButton.setVisibility(View.VISIBLE);
        //save to shared preferences
        mEditor.putBoolean(Constants.KEY_IS_Google_Api_Client_connected, true);
        mEditor.apply();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            beginUserInitiatedSignIn();
        }else if (view.getId() == R.id.sign_out_button) {
            signOut();
           mSignInButton.setVisibility(View.VISIBLE);
            mSignOutButton.setVisibility(View.GONE);
        }else if (view.getId() == R.id.show_achievements){
            if(getApiClient().isConnected()) {
                startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 1);
            }else{
                alertUserForGoogleSignUp();
            }
        }else if(view.getId() == R.id.show_leaderboard){
            if(getApiClient().isConnected()) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
                        getApiClient(), getString(R.string.number_of_solved_math_problems_leaderboard)), 2);
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

}
