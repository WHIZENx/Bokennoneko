package com.cmu.project.bokennoneko.MainActivity.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cmu.project.bokennoneko.Model.Score;
import com.cmu.project.bokennoneko.Model.Users;
import com.cmu.project.bokennoneko.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.MyViewHolder> {

    private Context mContext;
    private List<Score> mData ;

    public ScoresAdapter(Context mContext, List<Score> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recycle_score, parent, false);
        return new ScoresAdapter.MyViewHolder(view);
    }

    private static final int HEADER = 0;
    private static final int ITEM = 1;

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER : ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Score score = mData.get(position);

        if (holder.getItemViewType() == ITEM) {
            holder.first.setVisibility(View.INVISIBLE);
        } else {
            holder.first.setVisibility(View.VISIBLE);
        }

        if (position < 100) {
            holder.rankview.setText("" + (position + 1));
        } else {
            holder.rankview.setText("99+");
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(score.getId());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);

                if(isValidContextForGlide(mContext)) {
                    Glide.with(mContext).load(users.getImageURL()).into(holder.profile_img);
                }
                holder.username.setText(users.getUsername());
                holder.showscore.setText("Max Score: "+score.getMaxscore());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView showscore;
        TextView username;
        CircleImageView profile_img;
        ImageView first;
        TextView rankview;

        public MyViewHolder(View itemView) {
            super(itemView);

            showscore = itemView.findViewById(R.id.showscore);
            username = itemView.findViewById(R.id.username);
            profile_img = itemView.findViewById(R.id.profile_img);
            first = itemView.findViewById(R.id.first);
            rankview = itemView.findViewById(R.id.rank_view);

        }
    }
}
