package kashyap.anurag.quizx.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import kashyap.anurag.quizx.Adapters.AdapterCategory;
import kashyap.anurag.quizx.AddCategoryActivity;
import kashyap.anurag.quizx.MainActivity;
import kashyap.anurag.quizx.Models.ModelCategory;
import kashyap.anurag.quizx.R;
import kashyap.anurag.quizx.SpinnerActivity;
import kashyap.anurag.quizx.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private AdapterCategory adapterCategory;
    private ArrayList<ModelCategory> categoryArrayList;
    private FirebaseAuth firebaseAuth;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        loadAllCategories();
        binding.spinWheelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SpinnerActivity.class));
            }
        });
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddCategoryActivity.class));
            }
        });

        return binding.getRoot();
    }
    private void checkUser() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                String userType = snapshot.get("userType").toString();
                if (userType.equals("admin")){
                    binding.addCategoryBtn.setVisibility(View.VISIBLE);
                }else {
                    binding.addCategoryBtn.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadAllCategories() {

        binding.progressBar.setVisibility(View.VISIBLE);

        categoryArrayList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Categories")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categoryArrayList.clear();

                        for (DocumentSnapshot snapshot : value.getDocuments()){
                            ModelCategory modelCategory = snapshot.toObject(ModelCategory.class);
                            modelCategory.setCategoryId(snapshot.getId());
                            categoryArrayList.add(modelCategory);
                        }

                        adapterCategory = new AdapterCategory(getContext(), categoryArrayList);
                        binding.categoriesRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        binding.categoriesRv.setAdapter(adapterCategory);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });


    }
}