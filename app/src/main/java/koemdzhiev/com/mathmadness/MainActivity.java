package koemdzhiev.com.mathmadness;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private int isMathProblemTrue;
    private TextView digitOne;
    private TextView digitTwo;
    private TextView mathOperator;
    private TextView equalsOperator;
    private TextView sum;
    private Button mTrueBtn;
    private Button mFalseBtn;
    Random ifTrue;
    Random digitGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        digitOne = (TextView)findViewById(R.id.digitOne);
        digitTwo = (TextView)findViewById(R.id.digitTwo);
        mathOperator = (TextView)findViewById(R.id.mathOperator);
        equalsOperator = (TextView)findViewById(R.id.equalsOperator);
        sum = (TextView)findViewById(R.id.sum);
        mTrueBtn = (Button)findViewById(R.id.trueButton);
        mFalseBtn = (Button)findViewById(R.id.falseButton);
        ifTrue = new Random();
        digitGenerator = new Random();

        mTrueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //is the user right? if pressing True button
                if(isMathProblemTrue == 1){
                    //user is correct
                    Toast.makeText(MainActivity.this,"Correct!",Toast.LENGTH_SHORT).show();
                    generateMathProblem();
                }else{
                    //user is incorrect
                    Toast.makeText(MainActivity.this,"Incorrect!",Toast.LENGTH_SHORT).show();
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
                }else{
                    //user is incorrect
                    Toast.makeText(MainActivity.this,"Incorrect!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        generateMathProblem();

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
        isMathProblemTrue = ifTrue.nextInt(2);
        Log.d(TAG, isMathProblemTrue + "");
        if(isMathProblemTrue == 1){
            //the equation is true
            int firstDigit = digitGenerator.nextInt(100);
            digitOne.setText(firstDigit+"");
            int secondDigit = digitGenerator.nextInt(100);
            digitTwo.setText(secondDigit+"");
            sum.setText(firstDigit+secondDigit+"");
        }else{
            //the equation is false
            int randomSum = digitGenerator.nextInt(100);
            sum.setText(randomSum+"");
        }
    }
}
