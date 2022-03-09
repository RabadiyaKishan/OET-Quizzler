package com.oet.quiz.Adapter;

import com.google.android.gms.ads.AdListener;

public class CustomAdListener extends AdListener {
    @Override
    public void onAdLoaded() {
        System.out.println("ad onAdLoaded:");
        // Code to be executed when an ad finishes loading.
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        System.out.println("ad onAdFailedToLoad:" + errorCode);
        // Code to be executed when an ad request fails.
    }

    @Override
    public void onAdOpened() {
        System.out.println("ad onAdOpened:");
        // Code to be executed when an ad opens an overlay that
        // covers the screen.
    }

    @Override
    public void onAdClicked() {

        // Code to be executed when the user clicks on an ad.
    }

    @Override
    public void onAdLeftApplication() {
        // Code to be executed when the user has left the app.
    }

    @Override
    public void onAdClosed() {
        // Code to be executed when the user is about to return
        // to the app after tapping on an ad.
    }
}