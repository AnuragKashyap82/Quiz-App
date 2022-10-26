package kashyap.anurag.quizx.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.quizx.Models.ModelCategory;
import kashyap.anurag.quizx.QuizActivity;
import kashyap.anurag.quizx.R;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> {

    private Context context;
    private ArrayList<ModelCategory> categoryArrayList;
    private FirebaseAuth firebaseAuth;

    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_categories_items, parent, false);
        return new HolderCategory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        ModelCategory modelCategory = categoryArrayList.get(position);
        String categoryName = modelCategory.getCategoryName();
        String categoryImage = modelCategory.getCategoryImage();
        String categoryId = modelCategory.getCategoryId();

        holder.categoryTv.setText(categoryName);
        try {
            Picasso.get().load(categoryImage).placeholder(R.drawable.ic_profile_white).into(holder.categoryIv);
        } catch (Exception e) {
            holder.categoryIv.setImageResource(R.drawable.ic_profile_white);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInstructions(categoryId);
            }
        });
    }

    private void showInstructions(String categoryId) {

        Dialog instructionsDialog = new Dialog(context, R.style.instructionStyle);
        instructionsDialog.setContentView(R.layout.instructions_layout);
        instructionsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView closeDialogBtn = instructionsDialog.findViewById(R.id.closeDialogBtn);
        TextView continueBtn = instructionsDialog.findViewById(R.id.continueBtn);
        TextView availableCoinsTv = instructionsDialog.findViewById(R.id.availableCoinsTv);

        loadAvailableCoins(availableCoinsTv);

        instructionsDialog.setCancelable(true);

        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instructionsDialog.dismiss();
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAvailableCoins(categoryId);
                instructionsDialog.dismiss();
            }
        });
        instructionsDialog.show();
    }

    private void loadAvailableCoins(TextView availableCoinsTv) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String coin = ""+snapshot.child("coins").getValue();
                    availableCoinsTv.setText("Available Coins: "+coin);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkAvailableCoins(String categoryId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String coins = ""+snapshot.child("coins").getValue();
                int availableCoins = Integer.parseInt(coins);
                if (availableCoins < 200) {
                    Toast.makeText(context, "You need atLeast 200 coins to play quiz", Toast.LENGTH_SHORT).show();
                    showCoinNotAvailable();
                } else {
                    Intent intent = new Intent(context, QuizActivity.class);
                    intent.putExtra("categoryId", "" + categoryId);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showCoinNotAvailable() {

        Dialog timeOutDialog = new Dialog(context, R.style.instructionStyle);
        timeOutDialog.setContentView(R.layout.coin_not_available_dialog);
        timeOutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView quitBtn = timeOutDialog.findViewById(R.id.quitBtn);
        timeOutDialog.setCancelable(true);
        timeOutDialog.setCanceledOnTouchOutside(false);

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeOutDialog.dismiss();
            }
        });
        timeOutDialog.show();
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public class HolderCategory extends RecyclerView.ViewHolder {

        private ImageView categoryIv;
        private TextView categoryTv;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);

            categoryIv = itemView.findViewById(R.id.categoryIv);
            categoryTv = itemView.findViewById(R.id.categoryTv);
        }
    }
}
