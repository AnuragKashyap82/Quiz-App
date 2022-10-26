package kashyap.anurag.quizx.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

        FirebaseFirestore.getInstance().collection("Users").orderBy("coins", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        for (DocumentSnapshot snapshot1 : snapshot){
                            ModelLeaderBoard modelLeaderBoard = snapshot1.toObject(ModelLeaderBoard.class);
                            leaderBoardArrayList.add(modelLeaderBoard);
                        }
                        adapterLeaderBoard = new AdapterLeaderBoard(getContext(), leaderBoardArrayList);
                        binding.leaderBoardsRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.leaderBoardsRv.setAdapter(adapterLeaderBoard);
                        adapterLeaderBoard.notifyDataSetChanged();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}