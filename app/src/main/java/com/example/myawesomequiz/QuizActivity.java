package com.example.myawesomequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class QuizActivity extends AppCompatActivity {
    QuizDbHelper quizDbHelper;

    public static final String EXTRA_SCORE = "extraScore";
    private static final long COUNTDOWN_IN_MILLIS = 20000;
    private long backPressedTime;
    private TextView tv_score, tv_viewQuestionCount, tv_countDown, tv_viewQuestion;
    private RadioGroup rg_selection;
    private RadioButton rb_1, rb_2, rb_3;
    private Button btn_confirm;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private ArrayList<Integer> selectedQuestion;
    private int numberOfQuestion;
    private int questionCounter;
    private int questionCountTotalSize;
    private Question currentQuestion;
    private int score;
    private boolean answered;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        assign();
        confirmBtnClick();
    }


    private void assign() {
        quizDbHelper = new QuizDbHelper(this);
        questionList = quizDbHelper.getAllQuestions();
        questionCountTotalSize = questionList.size();
        Collections.shuffle(questionList);
        Bundle bundle = getIntent().getExtras();
        numberOfQuestion = bundle.getInt("NumberOfQuestion");
        tv_score = findViewById(R.id.tv_score);
        tv_viewQuestion = findViewById(R.id.tv_viewQuestion);
        tv_viewQuestionCount = findViewById(R.id.tv_viewQuestionCount);
        tv_countDown = findViewById(R.id.tv_countDown);
        rg_selection = findViewById(R.id.rg_selection);
        rb_1 = findViewById(R.id.rb_1);
        rb_2 = findViewById(R.id.rb_2);
        rb_3 = findViewById(R.id.rb_3);
        btn_confirm = findViewById(R.id.btn_confirm);

        textColorDefaultRb = rb_1.getTextColors();
        textColorDefaultCd = tv_countDown.getTextColors();
        selectedQuestion = new ArrayList<>();
        showNextQuestion();
    }

    private int createQuestionNumber(){
        Random random = new Random();
        int randNumber = random.nextInt(10);
        while(selectedQuestion.contains(randNumber)){
            randNumber = random.nextInt(10);
        }
        selectedQuestion.add(randNumber);
        return randNumber;
    }
    private void showNextQuestion() {
        rb_1.setTextColor(textColorDefaultRb);
        rb_2.setTextColor(textColorDefaultRb);
        rb_3.setTextColor(textColorDefaultRb);
        rg_selection.clearCheck();

        if (questionCounter < numberOfQuestion) {
            currentQuestion = questionList.get(createQuestionNumber());
            btn_confirm.setText("ONAYLA");
            tv_viewQuestion.setText(currentQuestion.getQuestion());
            rb_1.setText(currentQuestion.getOption1());
            rb_2.setText(currentQuestion.getOption2());
            rb_3.setText(currentQuestion.getOption3());

            questionCounter++;
            tv_viewQuestionCount.setText("Soru: " + questionCounter + "/" + numberOfQuestion);
            answered = false;
            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();
        } else {
            finishQuiz();
        }
    }

    private void startCountDown(){
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownTexT();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownTexT();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownTexT(){
        int minutes = (int)(timeLeftInMillis/1000)/60;
        int seconds = (int)(timeLeftInMillis/1000)%60;

        String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        tv_countDown.setText(timeFormatted);

        if(timeLeftInMillis < 10000){
            tv_countDown.setTextColor(Color.RED);
        }else{
            tv_countDown.setTextColor(textColorDefaultCd);
        }
    }

    private void finishQuiz() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE,score);
        setResult(RESULT_OK,resultIntent);
        finish();
    }

    private void confirmBtnClick() {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (rb_1.isChecked() || rb_2.isChecked() || rb_3.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, "Bir cevap seç!!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });
    }

    private void checkAnswer() {
        answered = true;
        countDownTimer.cancel();
        RadioButton rbSelected = findViewById(rg_selection.getCheckedRadioButtonId());

        int answer = rg_selection.indexOfChild(rbSelected) + 1;

        if (answer == currentQuestion.getAnswerNo()) {
            score++;
            tv_score.setText("Skor: " + score);
        }

        showSolutions();

    }

    private void showSolutions() {
        rb_1.setTextColor(Color.RED);
        rb_2.setTextColor(Color.RED);
        rb_3.setTextColor(Color.RED);

        RadioButton rbSelected = findViewById(rg_selection.getCheckedRadioButtonId());
        int answer = rg_selection.indexOfChild(rbSelected) + 1;

        switch (currentQuestion.getAnswerNo()) {
            case 1:
                rb_1.setTextColor(Color.GREEN);
                if(answer == currentQuestion.getAnswerNo()){
                    tv_viewQuestion.setText("Doğru :)");
                }
                else{
                    tv_viewQuestion.setText("Yanlış :( 1. cevap doğru");
                }

                break;
            case 2:
                rb_2.setTextColor(Color.GREEN);
                if(answer == currentQuestion.getAnswerNo()){
                    tv_viewQuestion.setText("Doğru :)");
                }
                else{
                    tv_viewQuestion.setText("Yanlış :) 2. cevap doğru");
                }

                break;
            case 3:
                rb_3.setTextColor(Color.GREEN);
                if(answer == currentQuestion.getAnswerNo()){
                    tv_viewQuestion.setText("Doğru :)");
                }
                else{
                    tv_viewQuestion.setText("Yanlış :) 3. cevap doğru");
                }

                break;
        }
        if (questionCounter < numberOfQuestion) {
            btn_confirm.setText("Sonraki Soru");
        } else {
            btn_confirm.setText("Bitir");
        }
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            finishQuiz();
        }
        else {
            Toast.makeText(getApplicationContext(),"Çıkmak için tekrar bas",Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}
