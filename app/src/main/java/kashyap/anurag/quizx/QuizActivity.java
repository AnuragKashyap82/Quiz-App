package kashyap.anurag.quizx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.quizx.Models.ModelQuestions;
import kashyap.anurag.quizx.databinding.ActivityQuizBinding;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private ArrayList<ModelQuestions> questionsArrayList = new ArrayList<>();
    int index = 0;
    private ModelQuestions modelQuestions;
    private CountDownTimer timer;
    private String categoryId;
    private int correctAnswers = 0;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryId = getIntent().getStringExtra("categoryId");

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        Random random = new Random();
        int rand = random.nextInt(12);
        loadAllQuestions(categoryId, rand);

        resetTimer();

        binding.addQuestionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizActivity.this, AddQuestionsActivity.class);
                intent.putExtra("categoryId", ""+categoryId);
                startActivity(intent);
            }
        });
    }

    private void checkUser() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String userType = snapshot.get("userType").toString();
                if (userType.equals("admin")){
                    binding.addQuestionsBtn.setVisibility(View.VISIBLE);
                }else {
                    binding.addQuestionsBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadAllQuestions(String categoryId, int rand) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.mainLayout.setVisibility(View.GONE);
        FirebaseFirestore.getInstance().collection("Categories").document(categoryId).collection("Questions")
                .whereGreaterThanOrEqualTo("index", rand)
                .orderBy("index")
                .limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        if (snapshot.getDocuments().size() < 5 && snapshot.getDocuments().size() != 0) {
                            FirebaseFirestore.getInstance().collection("Categories").document(categoryId).collection("Questions")
                                    .whereLessThanOrEqualTo("index", rand)
                                    .orderBy("index")
                                    .limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot snapshot) {
                                            for (DocumentSnapshot snapshot1 : snapshot) {
                                                ModelQuestions modelQuestions = snapshot1.toObject(ModelQuestions.class);
                                                questionsArrayList.add(modelQuestions);
                                            }
                                            binding.progressBar.setVisibility(View.GONE);
                                            binding.mainLayout.setVisibility(View.VISIBLE);
                                            setNextQuestion();
                                        }
                                    });
                        }else if (snapshot.getDocuments().size() == 0){
                            binding.mainLayout.setVisibility(View.GONE);
                            binding.progressBar.setVisibility(View.GONE);
                            binding.noQuestionTv.setVisibility(View.VISIBLE);
                        } else {
                            for (DocumentSnapshot snapshot1 : snapshot) {
                                ModelQuestions modelQuestions = snapshot1.toObject(ModelQuestions.class);
                                questionsArrayList.add(modelQuestions);
                            }
                            binding.progressBar.setVisibility(View.GONE);
                            binding.mainLayout.setVisibility(View.VISIBLE);
                            setNextQuestion();
                        }
                    }
                });
    }

    void resetTimer() {
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                binding.timer.setText(String.valueOf(l / 1000));
            }

            @Override
            public void onFinish() {
                showTimeOutDialog();
            }
        };
    }
    private void showTimeOutDialog() {

        Dialog timeOutDialog = new Dialog(QuizActivity.this, R.style.instructionStyle);
        timeOutDialog.setContentView(R.layout.time_out_layout);
        timeOutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView quitBtn = timeOutDialog.findViewById(R.id.quitBtn);
        TextView continueBtn = timeOutDialog.findViewById(R.id.continueBtn);
        timeOutDialog.setCancelable(true);
        timeOutDialog.setCanceledOnTouchOutside(false);

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeOutDialog.dismiss();
                reset();
                if (index < questionsArrayList.size() - 1) {
                    index++;
                    setNextQuestion();
                } else {
                    Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                    intent.putExtra("correct", correctAnswers);
                    intent.putExtra("total", questionsArrayList.size());
                    startActivity(intent);
                    finish();
                    Toast.makeText(QuizActivity.this, "Finished!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        timeOutDialog.show();
    }


    void showAnswer() {
        if (modelQuestions.getAnswer().equals(binding.answerA.getText().toString())) {
            binding.answerA.setBackground(getResources().getDrawable(R.drawable.correct_bg));
        } else if (modelQuestions.getAnswer().equals(binding.answerB.getText().toString())) {
            binding.answerB.setBackground(getResources().getDrawable(R.drawable.correct_bg));
        } else if (modelQuestions.getAnswer().equals(binding.answerC.getText().toString())) {
            binding.answerC.setBackground(getResources().getDrawable(R.drawable.correct_bg));
        } else if (modelQuestions.getAnswer().equals(binding.answerD.getText().toString())) {
            binding.answerD.setBackground(getResources().getDrawable(R.drawable.correct_bg));
        }
    }

    void setNextQuestion() {
        if (timer != null) {
            timer.cancel();
        }
        timer.start();
        if (index < questionsArrayList.size()) {
            binding.questionCounter.setText(String.format("%d/%d", index + 1, questionsArrayList.size()));
            modelQuestions = questionsArrayList.get(index);
            binding.question.setText(modelQuestions.getQuestion());
            binding.answerA.setText(modelQuestions.getOption1());
            binding.answerB.setText(modelQuestions.getOption2());
            binding.answerC.setText(modelQuestions.getOption3());
            binding.answerD.setText(modelQuestions.getOption4());
        }
    }

    void checkAnswer(TextView selected) {
        String selectedAnswer = selected.getText().toString();
        if (selectedAnswer.equals(modelQuestions.getAnswer())) {
            correctAnswers++;
            selected.setBackground(getResources().getDrawable(R.drawable.correct_bg));
            binding.answerA.setEnabled(false);
            binding.answerB.setEnabled(false);
            binding.answerC.setEnabled(false);
            binding.answerD.setEnabled(false);
        } else {
            showAnswer();
            selected.setBackground(getResources().getDrawable(R.drawable.wrong_bg));
            binding.answerA.setEnabled(false);
            binding.answerB.setEnabled(false);
            binding.answerC.setEnabled(false);
            binding.answerD.setEnabled(false);
        }
    }

    void reset() {
        binding.answerA.setBackground(getResources().getDrawable(R.drawable.answers_bg));
        binding.answerA.setBackground(getResources().getDrawable(R.drawable.answers_bg));
        binding.answerB.setBackground(getResources().getDrawable(R.drawable.answers_bg));
        binding.answerC.setBackground(getResources().getDrawable(R.drawable.answers_bg));
        binding.answerD.setBackground(getResources().getDrawable(R.drawable.answers_bg));
        binding.answerA.setEnabled(true);
        binding.answerB.setEnabled(true);
        binding.answerC.setEnabled(true);
        binding.answerD.setEnabled(true);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.answerA:
            case R.id.answerB:
            case R.id.answerC:
            case R.id.answerD:
                if (timer != null) {
                    timer.cancel();
                }
                TextView selected = (TextView) view;
                checkAnswer(selected);
                break;
            case R.id.nextBtn:
                reset();
                if (index < questionsArrayList.size() - 1) {
                    index++;
                    setNextQuestion();
                } else {
                    Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
                    intent.putExtra("correct", correctAnswers);
                    intent.putExtra("total", questionsArrayList.size());
                    startActivity(intent);
                    finish();
                    Toast.makeText(QuizActivity.this, "Finished!!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(QuizActivity.this, MainActivity.class));
        finishAffinity();
    }


}