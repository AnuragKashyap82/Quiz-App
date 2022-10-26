package kashyap.anurag.quizx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.quizx.databinding.ActivityAddQuestionsBinding;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class AddQuestionsActivity extends AppCompatActivity {
    private ActivityAddQuestionsBinding binding;
    private String categoryId;
    private String question, option1, option2, option3, option4, answer;
    private int questionNo;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryId = getIntent().getStringExtra("categoryId");
        loadIndex(categoryId);

        binding.uploadQuedtionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData(categoryId);
            }
        });

        binding.answerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateOptions();
            }
        });

    }

    private void loadIndex(String categoryId) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Categories").document(categoryId);
        documentReference.collection("Questions")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        if (snapshot.isEmpty()){
                            index++;
                            binding.indexTv.setText(String.valueOf(index));
                        }else {
                            index = snapshot.size();
                            index++;
                            binding.indexTv.setText(String.valueOf(index));

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddQuestionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validateOptions() {
        question = binding.questionEt.getText().toString().trim();
        option1 = binding.option1.getText().toString().trim();
        option2 = binding.option2.getText().toString().trim();
        option3 = binding.option3.getText().toString().trim();
        option4 = binding.option4.getText().toString().trim();

        if (question.isEmpty()){
            Toast.makeText(this, "Enter Question!!!", Toast.LENGTH_SHORT).show();
        }else if (option1.isEmpty()){
            Toast.makeText(this, "Enter Option!!!", Toast.LENGTH_SHORT).show();
        }else if (option2.isEmpty()){
            Toast.makeText(this, "Enter Option!!!", Toast.LENGTH_SHORT).show();
        }else if (option3.isEmpty()){
            Toast.makeText(this, "Enter Option!!!", Toast.LENGTH_SHORT).show();
        }else if (option4.isEmpty()){
            Toast.makeText(this, "Enter Option!!!", Toast.LENGTH_SHORT).show();
        }else  {
            showOptionsDialog(option1, option2, option3, option4);
        }
    }

    private void validateData(String categoryId) {
        question = binding.questionEt.getText().toString().trim();
        option1 = binding.option1.getText().toString().trim();
        option2 = binding.option2.getText().toString().trim();
        option3 = binding.option3.getText().toString().trim();
        option4 = binding.option4.getText().toString().trim();
        answer = binding.answerTv.getText().toString().trim();


        if (question.isEmpty()){
            Toast.makeText(this, "Enter Question!!!", Toast.LENGTH_SHORT).show();
        }else if (option1.isEmpty()){
            Toast.makeText(this, "Enter Option!!!", Toast.LENGTH_SHORT).show();
        }else if (option2.isEmpty()){
            Toast.makeText(this, "Enter Option!!!", Toast.LENGTH_SHORT).show();
        }else if (option3.isEmpty()){
            Toast.makeText(this, "Enter Option!!!", Toast.LENGTH_SHORT).show();
        }else if (option4.isEmpty()){
            Toast.makeText(this, "Enter Option!!!", Toast.LENGTH_SHORT).show();
        }else if (answer.isEmpty()){
            Toast.makeText(this, "Enter answer!!!", Toast.LENGTH_SHORT).show();
        }else {
            uploadQuestion(categoryId);
        }
    }

    private void uploadQuestion(String categoryId) {

        long timestamp = System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("question", ""+question);
        hashMap.put("option1", ""+option1);
        hashMap.put("option2", ""+option2);
        hashMap.put("option3", ""+option3);
        hashMap.put("option4", ""+option4);
        hashMap.put("answer", ""+answer);
        hashMap.put("index", index);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Categories").document(categoryId);
        documentReference.collection("Questions").document(""+timestamp)
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                     if (task.isSuccessful()){
                         Toast.makeText(AddQuestionsActivity.this, "Question successfully!!", Toast.LENGTH_SHORT).show();
                         startActivity(new Intent(AddQuestionsActivity.this, MainActivity.class));
                         finishAffinity();
                     }else {
                         Toast.makeText(AddQuestionsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                     }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddQuestionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showOptionsDialog(String option1, String option2, String option3, String option4) {

        Dialog optionDialog = new Dialog(AddQuestionsActivity.this, R.style.instructionStyle);
        optionDialog.setContentView(R.layout.options_layout);
        optionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RelativeLayout option1Rl = optionDialog.findViewById(R.id.option1Rl);
        RelativeLayout option2Rl = optionDialog.findViewById(R.id.option2Rl);
        RelativeLayout option3Rl = optionDialog.findViewById(R.id.option3Rl);
        RelativeLayout option4Rl = optionDialog.findViewById(R.id.option4Rl);

        TextView option1Tv = optionDialog.findViewById(R.id.option1Tv);
        TextView option2Tv = optionDialog.findViewById(R.id.option2Tv);
        TextView option3Tv = optionDialog.findViewById(R.id.option3Tv);
        TextView option4Tv = optionDialog.findViewById(R.id.option4Tv);

        option1Tv.setText(option1);
        option2Tv.setText(option2);
        option3Tv.setText(option3);
        option4Tv.setText(option4);

        option1Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.answerTv.setText(option1);
                optionDialog.dismiss();
            }
        });
        option2Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.answerTv.setText(option2);
                optionDialog.dismiss();
            }
        });
        option3Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.answerTv.setText(option3);
                optionDialog.dismiss();
            }
        });
        option4Rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.answerTv.setText(option4);
                optionDialog.dismiss();
            }
        });

        optionDialog.setCancelable(true);


        optionDialog.show();
    }

}