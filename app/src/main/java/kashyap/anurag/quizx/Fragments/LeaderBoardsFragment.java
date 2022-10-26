package kashyap.anurag.quizx.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.recyclerview.widget.LinearLayoutManager;
import kashyap.anurag.quizx.Adapters.AdapterLeaderBoard;
import kashyap.anurag.quizx.Models.ModelLeaderBoard;
import kashyap.anurag.quizx.R;
import kashyap.anurag.quizx.databinding.FragmentLeaderBoardsBinding;


public class LeaderBoardsFragment extends Fragment {

    private FragmentLeaderBoardsBinding binding;
    private AdapterLeaderBoard adapterLeaderBoard;
    private ArrayList<ModelLeaderBoard> leaderBoardArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLeaderBoardsBinding.inflate(getLayoutInflater());

        loadLeaderBoard();

        return binding.getRoot();

    }

    private void loadLeaderBoard() {

        binding.progressBar.setVisibility(View.VISIBLE);
        leaderBoardArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        leaderBoardArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelLeaderBoard modelLeaderBoard = ds.getValue(ModelLeaderBoard.class);
                            leaderBoardArrayList.add(modelLeaderBoard);
                        }
//                        Collections.sort(leaderBoardArrayList, new Comparator<ModelLeaderBoard>() {
//                            @Override
//                            public int compare(ModelLeaderBoard t1, ModelLeaderBoard t2) {
//                                return t1.getCoins().compareToIgnoreCase(t2.getCoins());
//                            }
//                        });
//                        Collections.reverse(leaderBoardArrayList);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        binding.leaderBoardsRv.setLayoutManager(layoutManager);

                        binding.leaderBoardsRv.setLayoutManager(new LinearLayoutManager(getContext()));

                        adapterLeaderBoard = new AdapterLeaderBoard(getContext(), leaderBoardArrayList);
                        binding.leaderBoardsRv.setAdapter(adapterLeaderBoard);
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}