package com.example.myapplication;

import java.util.Random;
import java.util.concurrent.Callable;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.util.Log;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.constraintlayout.widget.ConstraintLayout;


public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_first, container, false);
        return v;
    }

    private class WebViewTimingStats {
        public long beforeNs = 0;
        public long afterInitNs = 0;
        public long afterAddViewNs = 0;
        public long afterLoadUrlNs = 0;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ConstraintLayout layout = (ConstraintLayout)view;

        view.findViewById(R.id.button_load_webview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        int tid = android.os.Process.myTid();
                        Log.i("FIRST_FRAG", "[" + tid + "] Meanwhile, on another thread...");
                        Log.i("FIRST_FRAG", "[" + tid + "] Loading WebView...");
                        long beforeNs = System.nanoTime();
                        WebSettings.getDefaultUserAgent(getActivity().getApplicationContext());
                        long afterNs = System.nanoTime();
                        long totalWarmupMs = (afterNs - beforeNs) / 1000 / 1000;
                        Log.i("FIRST_FRAG", "[" + tid + "] Done loading WebView after " + totalWarmupMs + " ms");
                    }
                }).start();
            }
        });

        final View animateButtonView = view.findViewById(R.id.button_first);
        // construct the value animator and define the range

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 500f);
        //repeats the animation 2 times
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator()); // increase the speed first and then decrease
        // animate over the course of 1000 milliseconds
        valueAnimator.setDuration(5000);
        // define how to update the view at each "step" of the animation
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                animateButtonView.setRotationX(progress);

            }
        });
        valueAnimator.start();

        animateButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }
}