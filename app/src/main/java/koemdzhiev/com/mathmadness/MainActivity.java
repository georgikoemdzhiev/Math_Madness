package koemdzhiev.com.mathmadness;

import android.content.Intent;
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
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.Random;

import koemdzhiev.com.mathmadness.utils.Constants;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private int isMathProblemTrue;
    private TextView mDigitOne,mDigitTwo,mDigitThree,mLeftBracket,mRightBracket;
    private TextView mMathOperator,mMathOperator2;
    private TextView score;
    private TextView mSum;
    private ImageView mTrueBtn,mFalseBtn;
    private ProgressBar mProgressBar;
    private CountDownTimer mCountDownTimer;
    private long timer_length = 5 * 1000;
    private long timer_interval = 1;
    private int consecutiveGames = 0;
    private Random ifMathProblemTrueRandom;
    private int firstNumber = 0;
    private int secondNumber = 0;
    private int thirthNumber = 0;
    private int sum = 0;
    private boolean mIsAdvancedMode = false;
    private long totalMillisUntilFinished = 0;
    private boolean firstTime = true;
    private GoogleApiClient mGoogleApiClient;
    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;
    private View.OnClickListener trueButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Log.d(TAG,"true button pressed");
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
                    if(!mIsAdvancedMode) {
                        Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.normal_mode_leaderboard), consecutiveGames);
                    }else{
                        //advanced mode
                        Games.Leaderboards.submitScore(mGoogleApiClient,getString(R.string.advanced_mode_leaderboard),consecutiveGames);
                    }
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
    };
    private View.OnClickListener falseButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Log.d(TAG,"false button pressed");
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
                    if(!mIsAdvancedMode) {
                        Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.normal_mode_leaderboard), consecutiveGames);
                    }else{
                        //advanced mode
                        Games.Leaderboards.submitScore(mGoogleApiClient,getString(R.string.advanced_mode_leaderboard),consecutiveGames);
                    }
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
    };

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
        mDigitOne = (TextView) findViewById(R.id.digitOne);
        mDigitTwo = (TextView) findViewById(R.id.digitTwo);
        mDigitThree = (TextView)findViewById(R.id.digitThree);
        mLeftBracket = (TextView)findViewById(R.id.leftBracket);
        mRightBracket = (TextView)findViewById(R.id.rightBracket);
        mMathOperator = (TextView) findViewById(R.id.mathOperator);
        mMathOperator2 = (TextView)findViewById(R.id.mathOperator2);
        score = (TextView) findViewById(R.id.score);
        score.setText("Score: " + consecutiveGames);
        mSum = (TextView) findViewById(R.id.sum);
        mTrueBtn = (ImageView) findViewById(R.id.trueButton);
        mFalseBtn = (ImageView) findViewById(R.id.falseButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        createTimer();
        mCountDownTimer.start();

        ifMathProblemTrueRandom = new Random();

        mTrueBtn.setOnClickListener(trueButtonListener);

        mFalseBtn.setOnClickListener(falseButtonListener);
        //get the starting intend and the boolean extra, false is default
        mIsAdvancedMode = getIntent().getBooleanExtra(Constants.KEY_IS_ADVANCED_MODE,false);
        if(mIsAdvancedMode){
            showAdvancedModeFeatures();
        }else{
            hideAdvancedModeFeatures();
        }
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
        //setting the listeners again
        mTrueBtn.setOnClickListener(trueButtonListener);
        mFalseBtn.setOnClickListener(falseButtonListener);
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
        //does not include division
        //generate true math problem
        isMathProblemTrue = ifMathProblemTrueRandom.nextInt(2);
        Log.d(TAG, "Math problem true?: " + isMathProblemTrue);
        if (isMathProblemTrue == 1) {
            //generate true equation
            int generateRandomMathOperator = generateMathOperatorInteger();
            int total = 0;
            firstNumber = new Random().nextInt(10) + 1;
            secondNumber = new Random().nextInt(10) + 1;
            //Log.d(TAG, "randomMathNumber" + generateRandomMathOperator);
            total = generateMathOperator(generateRandomMathOperator, total);
            if(mIsAdvancedMode){
                thirthNumber = new Random().nextInt(10)+1;
                total = generateMathOperatorForAdvancedMode(generateMathOperatorInteger(),total);
                mDigitThree.setText(thirthNumber+"");
            }
            mDigitOne.setText(firstNumber + "");
            mDigitTwo.setText(secondNumber + "");
            mSum.setText(total + "");
        } else {
            // generate false equation
            int generateRandomMathOperator = generateMathOperatorInteger();
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
            int counter = 0;
            while (equasionIsTrue) {
                counter++;
                Log.d(TAG, "while loop..." + counter);
                firstNumber = new Random().nextInt(10) + 1;
                secondNumber = new Random().nextInt(10) + 1;

                //21 random number here...
                sum = new Random().nextInt(20) + 1;
                total = generateMathOperator(generateRandomMathOperator, total);
                if(mIsAdvancedMode){
                    thirthNumber = new Random().nextInt(10)+1;
                    total = generateMathOperatorForAdvancedMode(generateMathOperatorInteger(),total);
                    mDigitThree.setText(thirthNumber+"");
                }
                boolean isItTrue = total == sum;
                if (!isItTrue) {
                    equasionIsTrue = false;
                }
            }
            mDigitOne.setText(firstNumber + "");
            mDigitTwo.setText(secondNumber + "");
            mSum.setText(sum + "");
        }

    }

    private int generateMathOperatorInteger() {
        return new Random().nextInt(3);
    }

    private int generateMathOperator(int generateRandomMathOperator, int total) {
        switch (generateRandomMathOperator) {
            case 0:
                mMathOperator.setText("+");
                total = firstNumber + secondNumber;
                break;
            case 1:
                mMathOperator.setText("-");
                total = firstNumber - secondNumber;
                break;
            case 2:
                mMathOperator.setText("*");
                total = firstNumber * secondNumber;
                break;
        }
        return total;
    }
    //generate the second math operator that is used for advanced mode
    //it uses the previous value of the total
    private int generateMathOperatorForAdvancedMode(int generateRandomMathOperator, int total) {
        switch (generateRandomMathOperator) {
            case 0:
                mMathOperator2.setText("+");
                total += thirthNumber;
                break;
            case 1:
                mMathOperator2.setText("-");
                total -= thirthNumber;
                break;
            case 2:
                mMathOperator2.setText("*");
                total *= thirthNumber;
                break;
        }
        return total;
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

    private void transferUserToStartScreen(String str) {
        String toBeSend;
        if (!str.equals("Time's up!")) {
            if(!mIsAdvancedMode) {
                toBeSend = mDigitOne.getText().toString() + mMathOperator.getText().toString() +
                        mDigitTwo.getText().toString() + "=" + mSum.getText().toString();
            }else{
                toBeSend = "("+mDigitOne.getText().toString() + mMathOperator.getText().toString() +
                        mDigitTwo.getText().toString() + ")" + mMathOperator2.getText().toString() + mDigitThree.getText().toString() +
                        "=" + mSum.getText().toString();
            }
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
        if(!mIsAdvancedMode) {
            if (timer_length > 3000) {
                timer_length -= 500;
            }
            if (consecutiveGames == 10) {
                timer_length -= 500;
            }
            if (consecutiveGames == 15) {
                timer_length -= 500;
            }
            if (consecutiveGames == 20) {
                timer_length -= 300;
            }
            if (consecutiveGames == 30) {
                timer_length -= 300;
            }
        }else{
            if (timer_length > 3000) {
                timer_length -= 500;
            }
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
                //set the buttons to null listeners to prevent user of pressing the buttons when the timer is finished
                //that could cause resetting the timer at the wrong timer
                mTrueBtn.setOnClickListener(null);
                mFalseBtn.setOnClickListener(null);
                transferUserToStartScreen("Time's up!");
            }
        };
    }

    private void showAdvancedModeFeatures(){
        mLeftBracket.setVisibility(View.VISIBLE);
        mRightBracket.setVisibility(View.VISIBLE);
        mDigitThree.setVisibility(View.VISIBLE);
        mMathOperator2.setVisibility(View.VISIBLE);
    }
    private void hideAdvancedModeFeatures(){
        mLeftBracket.setVisibility(View.GONE);
        mRightBracket.setVisibility(View.GONE);
        mDigitThree.setVisibility(View.GONE);
        mMathOperator2.setVisibility(View.GONE);
    }
    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        //mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
    }
}
