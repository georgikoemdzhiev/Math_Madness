package koemdzhiev.com.mathmadness;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private int isMathProblemTrue;
    private TextView digitOne;
    private TextView digitTwo;
    private TextView mMathOperator;
    private TextView equalsOperator;
    private TextView sum;
    private Button mTrueBtn;
    private Button mFalseBtn;
    private ProgressBar mProgressBar;
    private CountDownTimer mCountDownTimer;
    private long timer_length = 10*1000;
    private long timer_interval = 1;
    long totalMillisUntilFinished = 0;
    boolean firstTime = true;
    Random ifTrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        digitOne = (TextView)findViewById(R.id.digitOne);
        digitTwo = (TextView)findViewById(R.id.digitTwo);
        mMathOperator = (TextView)findViewById(R.id.mathOperator);
        equalsOperator = (TextView)findViewById(R.id.equalsOperator);
        sum = (TextView)findViewById(R.id.sum);
        mTrueBtn = (Button)findViewById(R.id.trueButton);
        mFalseBtn = (Button)findViewById(R.id.falseButton);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        createNStartTimer();

        ifTrue = new Random();

        mTrueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is the user right? if pressing True button
                if(isMathProblemTrue == 1){
                    //user is correct
                    Toast.makeText(MainActivity.this,"Correct!",Toast.LENGTH_SHORT).show();
                    generateMathProblem();
                    if(timer_length > 5000){
                        timer_length -=1000;
                    }
                    mCountDownTimer.cancel();
                    createNStartTimer();
                }else{
                    //user is incorrect
                    transferUserToStartScreen();
                    mCountDownTimer.cancel(); // cancel
                }
            }
        });

        mFalseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is the user right? if pressing True button
                if(isMathProblemTrue == 0){
                    //user is correct
                    Toast.makeText(MainActivity.this,"Correct!",Toast.LENGTH_SHORT).show();
                    generateMathProblem();
                    if(timer_length > 5000){
                        timer_length -=1000;
                    }
                    mCountDownTimer.cancel();
                    createNStartTimer();
                }else{
                    //user is incorrect
                    transferUserToStartScreen();
                    mCountDownTimer.cancel();
                }
            }
        });

        generateMathProblem();

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
        int mathOperator = mathOperatorGenerator.nextInt(3);
        //generate math problem
        isMathProblemTrue = ifTrue.nextInt(2);
        Log.d(TAG,"Math problem true?: "+isMathProblemTrue);
        if(isMathProblemTrue == 1){
            //the equation is true
            Random firstGenerator = new Random();
            int firstDigit = firstGenerator.nextInt(10)+1;
            digitOne.setText(firstDigit+"");
            //attemp to fix same digits
            Random secondGenerator = new Random();
            int secondDigit = secondGenerator.nextInt(10)+1;
            digitTwo.setText(secondDigit+"");
            if(mathOperator == 0){
                //+
                mMathOperator.setText("+");
                sum.setText(firstDigit+secondDigit+"");
            }else if(mathOperator == 1){
                //-
                mMathOperator.setText("-");
                sum.setText(firstDigit-secondDigit+"");
            }else if(mathOperator == 2){
                //*
                mMathOperator.setText("*");
                sum.setText(firstDigit*secondDigit+"");
            }else {
                // /
                mMathOperator.setText("/");
                sum.setText(firstDigit/secondDigit+"");
            }

        }else{
            //the equation is false
            if(mathOperator == 0){
                //+
                mMathOperator.setText("+");
                //sum.setText(firstDigit+secondDigit+"");
            }else if(mathOperator == 1){
                //-
                mMathOperator.setText("-");
                //sum.setText(firstDigit-secondDigit+"");
            }else if(mathOperator == 2){
                //*
                mMathOperator.setText("*");
                //sum.setText(firstDigit*secondDigit+"");
            }else {
                // /
                mMathOperator.setText("/");
                //sum.setText(firstDigit/secondDigit+"");
            }
            Random sumGenerator = new Random();
            int randomSum = sumGenerator.nextInt(10)+1;
            sum.setText(randomSum+"");
        }

    }
    private void transferUserToStartScreen() {
        Toast.makeText(MainActivity.this,"Incorrect!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(intent);
    }

    private void restartTimer() {
        //reset the timer
        mCountDownTimer.cancel(); // cancel
        mCountDownTimer.start();  // then restart
    }

    private void createNStartTimer() {
        Log.d(TAG,"timer length:"+timer_length);
        mCountDownTimer = new CountDownTimer(timer_length,timer_interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "Mil until finish:" + millisUntilFinished);
                if(firstTime){totalMillisUntilFinished = millisUntilFinished; firstTime = false;}
                int progress = (int) ((millisUntilFinished*100)/totalMillisUntilFinished);
                Log.d(TAG, "progressBar:" + progress);
                mProgressBar.setProgress(progress);
            }
            @Override
            public void onFinish() {
                mProgressBar.setProgress(0);
                transferUserToStartScreen();
            }
        }.start();
    }
}
