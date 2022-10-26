package kashyap.anurag.quizx.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import kashyap.anurag.quizx.R;
import kashyap.anurag.quizx.databinding.FragmentWalletBinding;

public class WalletFragment extends Fragment {

    public WalletFragment() {
        // Required empty public constructor
    }

    private FragmentWalletBinding binding;
    private FirebaseAuth firebaseAuth;
    private int coins;
    private String email, name, currentDate, currentTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();
        loadCoins();

        binding.sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        return  binding.getRoot();
    }

    private void validateData() {
       email = binding.emailEt.getText().toString().trim();
       coins = Integer.parseInt(binding.coinsTv.getText().toString());

       if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
           Toast.makeText(getContext(), "Invalid Email!!!!!", Toast.LENGTH_SHORT).show();
       }else if (coins > 300000 ){
            sendWithdrawRequest();
        }else {
            Toast.makeText(getContext(), "You need more coins to withdraw!!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCoins() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot.exists()){
                    String coin = snapshot.get("coins").toString();
                    name = snapshot.get("name").toString();

                    binding.coinsTv.setText(coin);
                }
            }
        });
    }
    private void sendWithdrawRequest() {

        binding.progressBar.setVisibility(View.VISIBLE);

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+firebaseAuth.getUid());
        hashMap.put("requestedBy", ""+name);
        hashMap.put("emailAddress", ""+email);
        hashMap.put("requestedAt", ""+currentDate+"  "+currentTime);

        DocumentReference documentReference  = FirebaseFirestore.getInstance().collection("WithdrawRequest").document(firebaseAuth.getUid());
        documentReference.set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Requested sent Successfully!!!!", Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),   e.getMessage(), Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}