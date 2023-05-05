package com.example.STARTapp;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;


import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * Fragment pentru clasament
 */
public class LeaderboardFragment extends Fragment {

    TextView placement;
    TextView user;
    TextView score;
    LinearLayout linearlayout;

    public LeaderboardFragment() {
    }

    public static LeaderboardFragment newInstance() {
        return new LeaderboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        linearlayout = view.findViewById(R.id.llLeaderboard);

        ((HomePage) requireActivity()).updateStatusBarColor("#00B2E5");

        setData();

        FloatingActionButton refresh = view.findViewById(R.id.fab_refresh);

        new Handler().postDelayed(() -> refresh.setOnClickListener(v -> {
            HomePage.leaderboard.refresh();
            linearlayout.removeAllViews();
            setData();
        }), 5000);

        return view;
    }

    /**
     * Functie pentru obtinerea datelor din baza de date
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setData() {
        LinkedHashMap<String, Integer> user = HomePage.leaderboard.getMap();
        makeConstraint("Utilizator", "Xp", "Pos");
        for (int index = 0; index < user.size(); index++) {
            Integer xp = (new ArrayList<>(user.values())).get(index);
            String name = (new ArrayList<>(user.keySet())).get(index);
            makeConstraint(name, xp + "xp", "#" + (index + 1));
        }
    }

    /**
     * Functie pentru aranjarea in pagina a elementelor clasamentului
     *
     * @param username nume utilizator
     * @param xp       experienta
     * @param index    numarul in clasament
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void makeConstraint(String username, String xp, String index) {
        ConstraintLayout constraintLayout = new ConstraintLayout(requireContext());
        ConstraintSet constraintSet = new ConstraintSet();
        placement = new TextView(getContext());
        user = new TextView(getContext());
        score = new TextView(getContext());

        setTextView(placement);
        setTextView(user);
        setTextView(score);

        setText(username, xp, index);

        addViewsToConstraintLayout(constraintLayout, placement);
        addViewsToConstraintLayout(constraintLayout, user);
        addViewsToConstraintLayout(constraintLayout, score);

        constraintSet.clone(constraintLayout);
        constraintSet.connect(placement.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, pxToDp(10));
        constraintSet.connect(placement.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(placement.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(user.getId(), ConstraintSet.LEFT, placement.getId(), ConstraintSet.RIGHT, pxToDp(35));
        constraintSet.connect(user.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(user.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(score.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, pxToDp(10));
        constraintSet.connect(score.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(score.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);
        linearlayout.addView(constraintLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxToDp(60)));

    }

    private void setText(String username, String xp, String index) {
        placement.setText(index);
        user.setText(username);
        score.setText(xp);
    }

    private void addViewsToConstraintLayout(ConstraintLayout constraintLayout, TextView tv) {
        constraintLayout.addView(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * Functie pentru datele din TextView
     *
     * @param tv widget TextView
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setTextView(TextView tv) {
        tv.setId(View.generateViewId());
        tv.setGravity(Gravity.CENTER);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setTextSize(30);
        Typeface tf = getResources().getFont(R.font.tommy_soft);
        tv.setTypeface(tf);
    }

    private int pxToDp(int dp) {
        final float scale = requireContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
