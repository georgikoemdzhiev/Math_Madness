package koemdzhiev.com.mathmadness;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.util.Random;

import koemdzhiev.com.mathmadness.utils.Constants;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private int isMathProblemTrue;
    private TextView digitOne;
    private TextView digitTwo;
    private TextView mMathOperator;
    private TextView score;
    private TextView mSum;
    private ImageView mTrueBtn;
    private ImageView mFalseBtn;
    private ProgressBar mProgressBar;
    private CountDownTimer mCountDownTimer;
    private long timer_length = 5 * 1000;
    private long timer_interval = 1;
    private int consecutiveGames = 0;
    private Random ifTrue;
    private int firstNumber = 0;
    private int secondNumber = 0;
    private int sum = 0;
    private long totalMillisUntilFinished = 0;
    private boolean firstTime = true;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        // add other APIs and scopes here as needed
                .build();
        digitOne = (TextView) findViewById(R.id.digitOne);
        digitTwo = (TextView) findViewById(R.id.digitTwo);
        mMathOperator = (TextView) findViewById(R.id.mathOperator);
        score = (TextView) findViewById(R.id.score);
        score.setText("Score: " + consecutiveGames);
        mSum = (TextView) findViewById(R.id.sum);
        mTrueBtn = (ImageView) findViewById(R.id.trueButton);
        mFalseBtn = (ImageView) findViewById(R.id.falseButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        createTimer();
        mCountDownTimer.start();

        ifTrue = new Random();

        mTrueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play animations
                YoYo.with(Techniques.Pulse)
                        .duration(100)
                        .playOn(findViewById(R.id.trueButton));
                //is the user right? if pressing True button
                if (isMathProblemTrue == 1) {
                    //user is correct
                    //update consecutive variable
                    consecutiveGames++;
                    score.setText("Score: " + consecutiveGames);
                    if (mGoogleApiClient.isConnected()) {
                        unlockAchievement();
                        Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.number_of_solved_math_problems_leaderboard), consecutiveGames);
                    }

                    //Toast.makeText(MainActivity.this,"Correct!",Toast.LENGTH_SHORT).show();
                    generateMathProblem();
                    mCountDownTimer.cancel();
                    createTimer();
                    speedUpTimer();
                    mCountDownTimer.start();
                } else {
                    //user is incorrect
                    //consecutiveGames = 0;
                    score.setText("Score: " + consecutiveGames);
                    transferUserToStartScreen("");
                    mCountDownTimer.cancel(); // cancel
                }
            }
        });

        mFalseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play animations
                YoYo.with(Techniques.Pulse)
                        .duration(100)
                        .playOn(findViewById(R.id.falseButton));
                //is the user right? if pressing True button
                if (isMathProblemTrue == 0) {
                    //user is correct
                    //update consecutive variable
                    consecutiveGames++;
                    score.setText("Score: " + consecutiveGames);
                    if (mGoogleApiClient.isConnected()) {
                        unlockAchievement();
                        Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.number_of_solved_math_problems_leaderboard), consecutiveGames);
                    }
                    //Toast.makeText(MainActivity.this,"Correct!",Toast.LENGTH_SHORT).show();
                    generateMathProblem();
                    mCountDownTimer.cancel();
                    createTimer();
                    speedUpTimer();
                    mCountDownTimer.start();
                } else {
                    //user is incorrect
                    //update consecutive variable
                    //consecutiveGames = 0;
                    score.setText("Score: " + consecutiveGames);
                    transferUserToStartScreen("");
                    mCountDownTimer.cancel();
                }
            }
        });

        generateMathProblem();

    }

    private void unlockAchievement() {
        if (consecutiveGames == 10) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.score_10_achievement));
        }
        if (consecutiveGames == 25) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.score_25_achievement));
        }
        if (consecutiveGames == 50) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.score_50_achievement));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCountDownTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        generateMathProblem();
        restartTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void generateMathProblem() {
        //generate math operator
        Random mathOperatorGenerator = new Random();
        //does not include division
        int mathOperator = mathOperatorGenerator.nextInt(3);
        //generate math problem
        isMathProblemTrue = ifTrue.nextInt(2);
        Log.d(TAG, "Math problem true?: " + isMathProblemTrue);
        if (isMathProblemTrue == 1) {
            //generate true equation
            int generateRandomMathOperator = new Random().nextInt(3);
            //Log.d(TAG, "randomMathNumber" + generateRandomMathOperator);
            switch (generateRandomMathOperator) {
                case 0:
                    mMathOperator.setText("+");
                    break;
                case 1:
                    mMathOperator.setText("-");
                    break;
                case 2:
                    mMathOperator.setText("*");
                    break;
            }
            int total = 0;
            boolean equasionIsTrue = false;
            while (!equasionIsTrue) {
                firstNumber = new Random().nextInt(10) + 1;
                secondNumber = new Random().nextInt(10) + 1;
                sum = new Random().nextInt(10) + 1;

                switch (generateRandomMathOperator) {
                    case 0:
                        total = firstNumber + secondNumber;
                        break;
                    case 1:
                        total = firstNumber - secondNumber;
                        break;
                    case 2:
                        total = firstNumber * secondNumber;
                        break;
                }
                boolean isItTrue = total == sum;
                if (isItTrue == true) {
                    equasionIsTrue = true;
                }
            }
            digitOne.setText(firstNumber + "");
            digitTwo.setText(secondNumber + "");
            mSum.setText(sum + "");
        } else {
            // generate false equation
            int generateRandomMathOperator = new Random().nextInt(3);
            //Log.d(TAG, "randomMathNumber" + generateRandomMathOperator);
            switch (generateRandomMathOperator) {
                case 0:
                    mMathOperator.setText("+");
                    break;
                case 1:
                    mMathOperator.setText("-");
                    break;
                case 2:
                    mMathOperator.setText("*");
                    break;
            }
            int total = 0;
            boolean equasionIsTrue = true;
            while (equasionIsTrue) {
                firstNumber = new Random().nextInt(10) + 1;
                secondNumber = new Random().nextInt(10) + 1;
                sum = new Random().nextInt(10) + 1;

                switch (generateRandomMathOperator) {
                    case 0:
                        total = firstNumber + secondNumber;
                        break;
                    case 1:
                        total = firstNumber - secondNumber;
                        break;
                    case 2:
                        total = firstNumber * secondNumber;
                        break;
                }
                boolean isItTrue = total == sum;
                if (isItTrue == false) {
                    equasionIsTrue = false;
                }
            }
            digitOne.setText(firstNumber + "");
            digitTwo.setText(secondNumber + "");
            mSum.setText(sum + "");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void transferUserToStartScreen(String str) {
        String toBeSend = "";
        if (!str.equals("Time's up!")) {
            //Toast.makeText(MainActivity.this,"Incorrect!",Toast.LENGTH_SHORT).show();
            toBeSend = digitOne.getText().toString() + mMathOperator.getText().toString() +
                    digitTwo.getText().toString() + "=" + mSum.getText().toString();
        } else {
            toBeSend = str;
        }
        Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
        intent.putExtra(Constants.KEY_MATH_EQ, toBeSend);
        intent.putExtra(Constants.KEY_SCORE, consecutiveGames);
        startActivity(intent);
    }

    private void restartTimer() {
        //reset the timer
        mCountDownTimer.cancel(); // cancel
        mCountDownTimer.start();  // then restart
    }

    private void speedUpTimer() {
        if (timer_length > 3000) {
            timer_length -= 500;
        }
        if (consecutiveGames == 10) {
            timer_length -= 500;
        }
        if (consecutiveGames == 15) {
            timer_length -= 500;
        }
        if(consecutiveGames == 20){
            timer_length -= 300;
        }
        if(consecutiveGames == 25){
            timer_length -= 300;
        }
        if(consecutiveGames == 30){
            timer_length -= 100;
        }
    }

    private void createTimer() {
        Log.d(TAG, "timer length:" + timer_length);
        mCountDownTimer = new CountDownTimer(timer_length, timer_interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (firstTime) {
                    totalMillisUntilFinished = millisUntilFinished;
                    firstTime = false;
                }
                int progress = (int) (millisUntilFinished * 100 / totalMillisUntilFinished);
                //Log.d(TAG, "progressBar:" + progress);
                Resources res = getResources();
                Rect bounds = mProgressBar.getProgressDrawable().getBounds();
                if (progress < 18) {
                    mProgressBar.setProgressDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.progress_bar_red));
                } else {
                    mProgressBar.setProgressDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.progress_bar_green));
                }

                mProgressBar.getProgressDrawable().setBounds(bounds);
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                mProgressBar.setProgress(0);
                transferUserToStartScreen("Time's up!");
            }
        };
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
