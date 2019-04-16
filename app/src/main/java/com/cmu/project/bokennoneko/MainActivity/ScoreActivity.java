package com.cmu.project.bokennoneko.MainActivity;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.TextView;

import com.cmu.project.bokennoneko.MainActivity.Adapter.ScoresAdapter;
import com.cmu.project.bokennoneko.Model.Score;
import com.cmu.project.bokennoneko.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    RecyclerView recscore;
    ScoresAdapter scoresAdapter;
    List<Score> mScores;

    FirebaseUser fuser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_score);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        recscore  = findViewById(R.id.recscore);
        recscore.setHasFixedSize(true);
        recscore.setLayoutManager(linearLayoutManager);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Scores");

        scoreList();
    }

    private void scoreList() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mScores = new ArrayList<>();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                    Score score = postsnap.getValue(Score.class);
                    mScores.add(score);
                }

                Collections.sort(mScores, new Comparator<Score>() {
                    @Override
                    public int compare(Score obj1, Score obj2) {
                        if (obj1.getMaxscore() < obj2.getMaxscore()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });

                scoresAdapter = new ScoresAdapter(getApplicationContext(), mScores);
                recscore.setAdapter(scoresAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
