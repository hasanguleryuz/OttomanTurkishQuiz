package com.example.myawesomequiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    QuizDbHelper quizDbHelper;
    private SharedPreferences sharedPreferences;
    private Button btn_startQuiz;
    private TextView tv_highScore;
    private NumberPicker np_numberOfQuestion;
    private int highscore;
    private List<Question> questionList;
    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighscore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assign();
        loadHighScore();
        startQuizBtnClick();
    }

    private void assign(){
        quizDbHelper = new QuizDbHelper(this);
        questionList = quizDbHelper.getAllQuestions();
        sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        btn_startQuiz = findViewById(R.id.btn_startQuiz);
        tv_highScore = findViewById(R.id.tv_highScore);
        np_numberOfQuestion = findViewById(R.id.np_numberOfQuestion);
    }

    private void startQuizBtnClick(){
        np_numberOfQuestion.setMinValue(1);
        if(questionList.size() < 20 ){
            np_numberOfQuestion.setMaxValue(questionList.size());
        }
        else{
            np_numberOfQuestion.setMaxValue(20);
        }
        btn_startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }

    private void startQuiz(){
        Intent intent = new Intent(MainActivity.this,QuizActivity.class);
        intent.putExtra("NumberOfQuestion",np_numberOfQuestion.getValue());
        startActivityForResult(intent,REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_QUIZ){
            if(resultCode == RESULT_OK){
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE,0);
                if(score > highscore){
                    updateHighscore(score);
                }
            }
        }
    }
    private void loadHighScore(){
        highscore = sharedPreferences.getInt(KEY_HIGHSCORE,0);
        tv_highScore.setText("En Yüksek Skor: "+ highscore);
    }

    private void updateHighscore(int highscoreNew){
        Toast.makeText(getApplicationContext(),"TEBRİKLER Yeni Yüksek Skor",Toast.LENGTH_SHORT).show();
        highscore = highscoreNew;
        tv_highScore.setText("Highscore: "+ highscore);
        SharedPreferences  prefs =  getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE,highscore);
        editor.apply();
    }
}
