package kashyap.anurag.quizx;

import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.quizx.databinding.ActivityResultBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResultActivity extends AppCompatActivity {
    private ActivityResultBinding binding;
    private int correct, total;
    private int CORRECT_POINTS = 10;
    private int WRONG_POINTS = 5;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        correct = getIntent().getIntExtra("correct", 0);
        total = getIntent().getIntExtra("total", 0);

        binding.scoreTv.setText(String.format("%d/%d", correct, total));

        int lostPoints = (total - correct) * WRONG_POINTS;
        binding.lostCoinTv.setText(String.valueOf(lostPoints));

        int earnedPoints = correct * CORRECT_POINTS;
        binding.earnedCoinsTv.setText(String.valueOf(earnedPoints));

        int overAllPoints = earnedPoints - lostPoints;
        binding.overAllCoinsTv.setText(String.valueOf(overAllPoints));

        updateCoins(earnedPoints);
        updateLostCoins(lostPoints);

        binding.reStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, MainActivity.class));
                finishAffinity();
            }
        });
    }

    private void updateCoins(int earnedPoints) {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.update("coins", FieldValue.increment(earnedPoints));
    }
    private void updateLostCoins(int lostPoints) {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.update("coins", FieldValue.increment(-lostPoints));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ResultActivity.this, MainActivity.class));
        finishAffinity();
    }
}