package kashyap.anurag.quizx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.quizx.databinding.ActivityResultBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

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

        updateCoins(overAllPoints);

        binding.reStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, MainActivity.class));
                finishAffinity();
            }
        });
    }

    private void updateCoins(int overAllPoints) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String coins = "" + snapshot.child("coins").getValue();

                        if (coins.equals("") || coins.equals("null")) {
                            coins = "0";
                        }

                        long updatedCoins = Long.parseLong(coins) + overAllPoints;


                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("coins", updatedCoins);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(firebaseAuth.getUid())
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ResultActivity.this, "" + updatedCoins, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ResultActivity.this, MainActivity.class));
        finishAffinity();
    }
}