/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.boliu.android.viewtrip.session;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;


import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.boliu.android.viewtrip.R;
import org.boliu.android.viewtrip.ui.widget.ObservableScrollView;
import org.boliu.android.viewtrip.util.ImageLoader;
import org.boliu.android.viewtrip.util.LogUtils;
import org.boliu.android.viewtrip.util.UIUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SessionDetailFragment extends Fragment
        implements ObservableScrollView.Callbacks {

    private static final String TAG = LogUtils.makeLogTag(SessionDetailFragment.class);

    private static HashSet<String> sDismissedFeedbackCard = new HashSet<>();

    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;

    private View mAddScheduleButtonContainer;

    private int mAddScheduleButtonContainerHeightPixels;

    private View mScrollViewChild;

    private TextView mTitle;

    private TextView mSubtitle;

    private ObservableScrollView mScrollView;

    private TextView mAbstract;

    private ImageView mPlusOneIcon;

    private ImageView mTwitterIcon;

    private TextView mLiveStreamVideocamIconAndText;

    private TextView mLiveStreamPlayIconAndText;

    private LinearLayout mTags;

    private ViewGroup mTagsContainer;

    private TextView mRequirements;

    private View mHeaderBox;

    private View mDetailsContainer;

    private int mPhotoHeightPixels;

    private int mHeaderHeightPixels;

    private boolean mHasPhoto;

    private View mPhotoViewContainer;

    private ImageView mPhotoView;

    private float mMaxHeaderElevation;

    private float mFABElevation;

    private Runnable mTimeHintUpdaterRunnable = null;

    private List<Runnable> mDeferredUiOperations = new ArrayList<>();

    private Handler mHandler;

    private boolean mAnalyticsScreenViewHasFired;

    private ImageLoader mNoPlaceholderImageLoader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAnalyticsScreenViewHasFired = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session_detail_frag, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler = new Handler();
//        initViews();
//        initViewListeners();
    }


    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = mScrollView.getScrollY();

        float newTop = Math.max(mPhotoHeightPixels, scrollY);
        mHeaderBox.setTranslationY(newTop);
        mAddScheduleButtonContainer.setTranslationY(newTop + mHeaderHeightPixels
                - mAddScheduleButtonContainerHeightPixels / 2);

        float gapFillProgress = 1;
        if (mPhotoHeightPixels != 0) {
            gapFillProgress = Math.min(Math.max(UIUtils.getProgress(scrollY,
                    0,
                    mPhotoHeightPixels), 0), 1);
        }

        ViewCompat.setElevation(mHeaderBox, gapFillProgress * mMaxHeaderElevation);
        ViewCompat.setElevation(mAddScheduleButtonContainer, gapFillProgress * mMaxHeaderElevation
                + mFABElevation);

        // Move background photo (parallax effect)
        mPhotoViewContainer.setTranslationY(scrollY * 0.5f);
    }

    private void recomputePhotoAndScrollingMetrics() {
        mHeaderHeightPixels = mHeaderBox.getHeight();

        mPhotoHeightPixels = 0;
        if (mHasPhoto) {
            mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
            mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight() * 2 / 3);
        }

        ViewGroup.LayoutParams lp;
        lp = mPhotoViewContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                mDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
        }

        onScrollChanged(0, 0); // trigger scroll handling
    }

    private void initViews() {
        mFABElevation = getResources().getDimensionPixelSize(R.dimen.fab_elevation);
        mMaxHeaderElevation = getResources().getDimensionPixelSize(
                R.dimen.session_detail_max_header_elevation);

        mScrollView = (ObservableScrollView) getActivity().findViewById(R.id.scroll_view);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
//        if (vto.isAlive()) {
//            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
//        }

        mScrollViewChild = getActivity().findViewById(R.id.scroll_view_child);
        mScrollViewChild.setVisibility(View.INVISIBLE);

        mDetailsContainer = getActivity().findViewById(R.id.details_container);
        mHeaderBox = getActivity().findViewById(R.id.header_session);
        mTitle = (TextView) getActivity().findViewById(R.id.session_title);
        mSubtitle = (TextView) getActivity().findViewById(R.id.session_subtitle);
        mPhotoViewContainer = getActivity().findViewById(R.id.session_photo_container);
        mPhotoView = (ImageView) getActivity().findViewById(R.id.session_photo);

        mAbstract = (TextView) getActivity().findViewById(R.id.session_abstract);

        mPlusOneIcon = (ImageView) getActivity().findViewById(R.id.gplus_icon_box);
        mTwitterIcon = (ImageView) getActivity().findViewById(R.id.twitter_icon_box);

        //Find view that shows a Videocam icon if the session is being live streamed.
        mLiveStreamVideocamIconAndText = (TextView) getActivity().findViewById(
                R.id.live_stream_videocam_icon_and_text);

        // Find view that shows a play button and some text for the user to watch the session live stream.
        mLiveStreamPlayIconAndText = (TextView) getActivity().findViewById(
                R.id.live_stream_play_icon_and_text);

        mRequirements = (TextView) getActivity().findViewById(R.id.session_requirements);
        mTags = (LinearLayout) getActivity().findViewById(R.id.session_tags);
        mTagsContainer = (ViewGroup) getActivity().findViewById(R.id.session_tags_container);

        ViewCompat.setTransitionName(mPhotoView, SessionDetailConstants.TRANSITION_NAME_PHOTO);

        mNoPlaceholderImageLoader = new ImageLoader(getContext());
    }

    private void displaySessionData(final SessionDetailModel data) {
        mTitle.setText(data.getSessionTitle());
        mSubtitle.setText(data.getSessionSubtitle());

        mPhotoViewContainer
                .setBackgroundColor(UIUtils.scaleSessionColorToDefaultBG(data.getSessionColor()));

        if (data.hasPhotoUrl()) {
            mHasPhoto = true;
            mNoPlaceholderImageLoader.loadImage(data.getPhotoUrl(), mPhotoView, new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, Target<Bitmap> target,
                                           boolean isFirstResource) {
                    mHasPhoto = false;
                    recomputePhotoAndScrollingMetrics();
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                               boolean isFromMemoryCache, boolean isFirstResource) {
                    // Trigger image transition
                    recomputePhotoAndScrollingMetrics();
                    return false;
                }
            });
            recomputePhotoAndScrollingMetrics();
        } else {
            mHasPhoto = false;
            recomputePhotoAndScrollingMetrics();
        }

        tryExecuteDeferredUiOperations();

        //displayTags(data);

        if (!data.isKeynote()) {
            showStarredDeferred(data.isInSchedule(), false);
        }

        if (!TextUtils.isEmpty(data.getSessionAbstract())) {
            UIUtils.setTextMaybeHtml(mAbstract, data.getSessionAbstract());
            mAbstract.setVisibility(View.VISIBLE);
        } else {
            mAbstract.setVisibility(View.GONE);
        }

        // Build requirements section
        final View requirementsBlock = getActivity().findViewById(R.id.session_requirements_block);
        final String sessionRequirements = data.getRequirements();
        if (!TextUtils.isEmpty(sessionRequirements)) {
            UIUtils.setTextMaybeHtml(mRequirements, sessionRequirements);
            requirementsBlock.setVisibility(View.VISIBLE);
        } else {
            requirementsBlock.setVisibility(View.GONE);
        }

        final ViewGroup relatedVideosBlock = (ViewGroup) getActivity().findViewById(
                R.id.related_videos_block);
        relatedVideosBlock.setVisibility(View.GONE);

        if (data.getLiveStreamVideoWatched()) {
            mPhotoView.setColorFilter(getContext().getResources().getColor(
                    R.color.video_scrim_watched));
            mLiveStreamPlayIconAndText.setText(getString(R.string.session_replay));
        }

        if (data.hasLiveStream()) {
            mLiveStreamPlayIconAndText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String videoId = YouTubeUtils.getVideoIdFromSessionData(data.getYouTubeUrl(),
                            data.getLiveStreamId());
                    YouTubeUtils.showYouTubeVideo("Z9FXpMD6JO8", getActivity());
                }
            });
        }

        fireAnalyticsScreenView(data.getSessionTitle());

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onScrollChanged(0, 0); // trigger scroll handling
                mScrollViewChild.setVisibility(View.VISIBLE);
                //mAbstract.setTextIsSelectable(true);
            }
        });

        mTimeHintUpdaterRunnable = new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    // Do not post a delayed message if the activity is detached.
                    return;
                }
                updateTimeBasedUi(data);
                mHandler.postDelayed(mTimeHintUpdaterRunnable,
                        SessionDetailConstants.TIME_HINT_UPDATE_INTERVAL);
            }
        };
        mHandler.postDelayed(mTimeHintUpdaterRunnable,
                SessionDetailConstants.TIME_HINT_UPDATE_INTERVAL);
    }

    private void tryExecuteDeferredUiOperations() {
        for (Runnable r : mDeferredUiOperations) {
            r.run();
            mDeferredUiOperations.clear();
        }
    }

}
