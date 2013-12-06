/*
 * This file is part of Birthday Widget.
 *
 * Birthday Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Birthday Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Birthday Widget.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) Lukas Marek, 2011.
 */
package cz.krtinec.birthday.ui;

import android.widget.FrameLayout;
import com.flurry.android.*;
import cz.krtinec.birthday.R;
import roboguice.inject.InjectView;

/**
 */
public class BannerActivity extends TracedActivity implements FlurryAdListener {

    @InjectView(R.id.banner)
    private FrameLayout mBanner;
    private String adSpace = "MediatedBannerBottom";

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAds.setAdListener(this);
        FlurryAds.fetchAd(this, adSpace, mBanner, FlurryAdSize.BANNER_BOTTOM);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAds.removeAd(this, adSpace, mBanner);
        FlurryAgent.onEndSession(this);
    }

    @Override
    public void spaceDidReceiveAd(String adSpace) {
        // called when the ad has been prepared, ad can be displayed:
        FlurryAds.displayAd(this, adSpace, mBanner);
        // instead of displaying the ad here, you can check
        // FlurryAds.isAdReady(adSpace)
        // and display the ad when ready to do so in your Activity.
    }

    @Override
    public void onVideoCompleted(final String s) {
    }

    @Override
    public void onAdOpened(final String s) {
    }

    @Override
    public void onAdClicked(final String s) {
    }

    @Override
    public void onAdClosed(final String s) {
    }

    @Override
    public void onRendered(final String s) {
    }

    @Override
    public void onRenderFailed(final String s) {
    }

    @Override
    public void onApplicationExit(final String s) {
    }

    @Override
    public void spaceDidFailToReceiveAd(final String s) {
    }

    @Override
    public boolean shouldDisplayAd(final String s, final FlurryAdType type) {
        return true;
    }

}
