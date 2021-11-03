package uz.glight.hobee.distribuition.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;

import androidx.annotation.Nullable;

import uz.glight.hobee.distribuition.R;

public class AnimationHelper {
    public static void showAnimation(final View view, @Nullable Animation.AnimationListener listener){
        Animation animShow = AnimationUtils.loadAnimation(view.getContext(), R.anim.enter_anim);
        if (listener != null){
            animShow.setAnimationListener(listener);
        }
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animShow);
    }

    public static void hideAnimation(final View view, @Nullable Animation.AnimationListener listener){
        Animation animHide = AnimationUtils.loadAnimation(view.getContext(), R.anim.exit_anim);
        if (listener != null){
            animHide.setAnimationListener(listener);
        }
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animHide);
    }

}
