package koemdzhiev.com.mathmadness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import koemdzhiev.com.mathmadness.utils.Constants;


public class GameOverActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private TextView mMathEquasion;
    private ImageView mGoHome;
    private TextView mScore;
    private ImageView mPlayAgain;
    private boolean mIsAdvancedMode;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        mMathEquasion = (TextView)findViewById(R.id.mathEquasion);
        Intent intent = getIntent();
        mMathEquasion.setText(intent.getStringExtra(Constants.KEY_MATH_EQ));
        mIsAdvancedMode = getIntent().getBooleanExtra(Constants.KEY_IS_ADVANCED_MODE,false);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        // add other APIs and scopes here as needed
                .build();

        mScore = (TextView)findViewById(R.id.score);
        mScore.setText("Score: "+intent.getIntExtra(Constants.KEY_SCORE,0));
        mGoHome = (ImageView)findViewById(R.id.goToHome);
        mGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //play animations
//                YoYo.with(Techniques.Pulse)
//                        .duration(100)
//                        .playOn(findViewById(R.id.goToHome));
                Intent intent = new Intent(GameOverActivity.this,StartActivity.class);
                startActivity(intent);
            }
        });
        mPlayAgain = (ImageView)findViewById(R.id.play_again);
        mPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsAdvancedMode){
                    Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                    intent.putExtra(Constants.KEY_IS_ADVANCED_MODE, true);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
                    intent.putExtra(Constants.KEY_IS_ADVANCED_MODE,false);
                    startActivity(intent);
                }
                //increment the incremental achievement by 1 for each time the user play the game
                if (mGoogleApiClient.isConnected()) {
                    Games.Achievements.increment(mGoogleApiClient, getString(R.string.addicted_200_times_play_achievement), 1);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_over, menu);
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
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
