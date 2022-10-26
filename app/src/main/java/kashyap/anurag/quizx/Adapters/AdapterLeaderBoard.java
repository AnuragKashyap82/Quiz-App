package kashyap.anurag.quizx.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kashyap.anurag.quizx.Models.ModelLeaderBoard;
import kashyap.anurag.quizx.R;

public class AdapterLeaderBoard extends RecyclerView.Adapter<AdapterLeaderBoard.HolderLeaderBoard> {

    private Context context;
    private ArrayList<ModelLeaderBoard> leaderBoardArrayList;

    public AdapterLeaderBoard(Context context, ArrayList<ModelLeaderBoard> leaderBoardArrayList) {
        this.context = context;
        this.leaderBoardArrayList = leaderBoardArrayList;
    }

    @NonNull
    @Override
    public HolderLeaderBoard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_leaderboard_items, parent, false);
        return new HolderLeaderBoard(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderLeaderBoard holder, int position) {
        ModelLeaderBoard modelLeaderBoard = leaderBoardArrayList.get(position);
        String name = modelLeaderBoard.getName();
        long coins = modelLeaderBoard.getCoins();
        String profileImage = modelLeaderBoard.getProfileImage();

        holder.coinsTv.setText(String.valueOf(coins));
        holder.nameTv.setText(name);
        holder.rankTv.setText(String.format("#%d", position+1));
        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_profile_white).into(holder.profileIv);
        } catch (Exception e) {
            holder.profileIv.setImageResource(R.drawable.ic_profile_white);
        }
    }

    @Override
    public int getItemCount() {
        return leaderBoardArrayList.size();
    }

    public class HolderLeaderBoard extends RecyclerView.ViewHolder {

        private TextView nameTv, coinsTv, rankTv;
        private ImageView profileIv;

        public HolderLeaderBoard(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            rankTv = itemView.findViewById(R.id.rankTv);
            coinsTv = itemView.findViewById(R.id.coinsTv);
            profileIv = itemView.findViewById(R.id.profileIv);
        }
    }
}
