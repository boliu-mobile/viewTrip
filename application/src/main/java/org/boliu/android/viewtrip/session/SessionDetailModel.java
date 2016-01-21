package org.boliu.android.viewtrip.session;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import org.boliu.android.viewtrip.R;

import java.util.ArrayList;
import java.util.List;

import static org.boliu.android.viewtrip.util.LogUtils.makeLogTag;

/**
 * Created by boliu on 1/17/16.
 */
public class SessionDetailModel {


    protected final static String TAG = makeLogTag(SessionDetailModel.class);

//    /**
//     * The cursor fields for the links. The corresponding resource ids for the links descriptions
//     * are in  {@link #LINKS_DESCRIPTION_RESOURCE_IDS}.
//     */
//    private static final String[] LINKS_CURSOR_FIELDS = {
//            ScheduleContract.Sessions.SESSION_YOUTUBE_URL,
//            ScheduleContract.Sessions.SESSION_MODERATOR_URL,
//            ScheduleContract.Sessions.SESSION_PDF_URL,
//            ScheduleContract.Sessions.SESSION_NOTES_URL
//    };
//
//    /**
//     * The resource ids for the links descriptions. The corresponding cursor fields for the links
//     * are in {@link #LINKS_CURSOR_FIELDS}.
//     */
//    private static final int[] LINKS_DESCRIPTION_RESOURCE_IDS = {
//            R.string.session_link_youtube,
//            R.string.session_link_moderator,
//            R.string.session_link_pdf,
//            R.string.session_link_notes,
//    };

    private final Context mContext;

    private final SessionsHelper mSessionsHelper;

    private String mSessionId;

    private Uri mSessionUri;

    private boolean mSessionLoaded = false;

    private String mTitle;

    private String mSubtitle;

    private int mSessionColor;

    private boolean mInSchedule;

    private boolean mInScheduleWhenSessionFirstLoaded;

    private boolean mIsKeynote;

    private long mSessionStart;

    private long mSessionEnd;

    private String mSessionAbstract;

    private String mHashTag;

    private String mUrl = "";

    private String mRoomId;

    private String mRoomName;

    private String mTagsString;

    private String mLiveStreamId;

    private String mYouTubeUrl;

    private String mPhotoUrl;

    private boolean mHasLiveStream = false;

    private boolean mLiveStreamVideoWatched = false;

    private boolean mHasFeedback = false;

    private String mRequirements;

    private String mSpeakersNames;

    /**
     * Holds a list of links for the session. The first element of the {@code Pair} is the resource
     * id for the string describing the link, the second is the {@code Intent} to launch when
     * selecting the link.
     */
    private List<Pair<Integer, Intent>> mLinks = new ArrayList<Pair<Integer, Intent>>();


    private StringBuilder mBuffer = new StringBuilder();

    public SessionDetailModel(Uri sessionUri, Context context, SessionsHelper sessionsHelper) {
        mContext = context;
        mSessionsHelper = sessionsHelper;
        mSessionUri = sessionUri;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public String getSessionTitle() {
        return mTitle;
    }

    public String getSessionSubtitle() {
        return mSubtitle;
    }

    public String getSessionUrl() {
        return mUrl;
    }

    public String getLiveStreamId() {
        return mLiveStreamId;
    }

    public String getYouTubeUrl() {
        return mYouTubeUrl;
    }

    public int getSessionColor() {
        return mSessionColor;
    }

    public String getSessionAbstract() {
        return mSessionAbstract;
    }

    public boolean getLiveStreamVideoWatched() {
        return mLiveStreamVideoWatched;
    }

    public boolean hasLiveStream() {
        return mHasLiveStream || !TextUtils.isEmpty(mYouTubeUrl);
    }

    public boolean isInSchedule() {
        return mInSchedule;
    }

    public boolean isInScheduleWhenSessionFirstLoaded() {
        return mInScheduleWhenSessionFirstLoaded;
    }

    public boolean isKeynote() {
        return mIsKeynote;
    }

    public boolean hasFeedback() {
        return mHasFeedback;
    }

    public boolean hasPhotoUrl() {
        return !TextUtils.isEmpty(mPhotoUrl);
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public String getRequirements() {
        return mRequirements;
    }

    public String getHashTag() {
        return mHashTag;
    }

    public String getTagsString() {
        return mTagsString;
    }

    public List<Pair<Integer, Intent>> getLinks() {
        return mLinks;
    }

    public boolean hasSummaryContent() {
        return !TextUtils.isEmpty(mSessionAbstract) || !TextUtils.isEmpty(mRequirements);
    }
}
