package kashyap.anurag.quizx;

import androidx.appcompat.app.AppCompatActivity;
import kashyap.anurag.quizx.databinding.ActivitySpinnerResultBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SpinnerResultActivity extends AppCompatActivity {
    private ActivitySpinnerResultBinding binding;
    private long cash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinnerResultBinding.inflate(getLayoutInflater());;
        setContentView(binding.getRoot());

        cash = getIntent().getLongExtra("cash", 0);

        binding.earnedCoinsTv.setText(String.valueOf(cash));

        binding.reSpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SpinnerResultActivity.this, SpinnerActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SpinnerResultActivity.this, MainActivity.class));
        finishAffinity();
    }
}