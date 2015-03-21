/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.share.gta.car;

import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.service.media.MediaBrowserService;

import com.share.gt.model.Category;
import com.share.gt.model.Product;
import com.share.gta.R;

import java.util.ArrayList;
import java.util.List;

public class OrderService extends MediaBrowserService {

    private static final String TAG = LogHelper.makeLogTag(OrderService.class.getSimpleName());

    private static final String ANDROID_AUTO_PACKAGE_NAME = "com.google.android.projection.gearhead";
    private static final String ANDROID_AUTO_SIMULATOR_PACKAGE_NAME = "com.google.android.mediasimulator";

    private static final String ID_ROOT = "__ROOT__";
    private static final String ID_PAYMENT = "_PAYMENT_";
    private static final String ID_HISTORY = "_HISTORY_";
    private static final String ID_CATEGORY = "__CATEGORY__";
    private static final String ID_PRODUCT = "__PRODUCT__";

    private static final String CUSTOM_ACTION_THUMBS_UP = "custom_action1";

    private MediaSession mSession;
    private MenuProvider menuProvider;
    private MediaPlayer mMediaPlayer;

    /*
     * (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.d(TAG, "onCreate");
        // Start a new MediaSession
        mSession = new MediaSession(this, "MusicService");
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Use these extras to reserve space for the corresponding actions, even when they are disabled
        // in the playbackstate, so the custom actions don't reflow.
        Bundle extras = new Bundle();
        extras.putBoolean(
                "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT",
                true);
        extras.putBoolean(
                "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS",
                true);
        // If you want to reserve the Queue slot when there is no queue
        // (mSession.setQueue(emptylist)), uncomment the lines below:
        // extras.putBoolean(
        //   "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE",
        //   true);
        mSession.setExtras(extras);

        updatePlaybackState("AHA!");

        menuProvider = MenuProvider.getInstance(getBaseContext());

    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        LogHelper.d(TAG, "onDestroy");

        mSession.release();
    }


    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        LogHelper.d(TAG, "OnGetRoot: clientPackageName=" + clientPackageName,
                "; clientUid=" + clientUid + " ; rootHints=", rootHints);
        // To ensure you are not allowing any arbitrary app to browse your app's contents, you
        // need to check the origin:
        if (!PackageValidator.isCallerAllowed(this, clientPackageName, clientUid)) {
            // If the request comes from an untrusted package, return null. No further calls will
            // be made to other media browsing methods.
            LogHelper.w(TAG, "OnGetRoot: IGNORING request from untrusted package "
                    + clientPackageName);
            return null;
        }
        if (ANDROID_AUTO_PACKAGE_NAME.equals(clientPackageName)) {
            // Optional: if your app needs to adapt ads, music library or anything else that
            // needs to run differently when connected to the car, this is where you should handle
            // it.
        }
        return new BrowserRoot(ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(final String parentMediaId, final Result<List<MediaItem>> result) {
        loadChildrenImpl(parentMediaId, result);
    }

    /**
     * Actual implementation of onLoadChildren that assumes that MusicProvider is already
     * initialized.
     */
    private void loadChildrenImpl(final String parentMediaId,
                                  final Result<List<MediaItem>> result) {
        LogHelper.d(TAG, "OnLoadChildren: parentMediaId=", parentMediaId);

        List<MediaItem> mediaItems = new ArrayList<>();

        if (ID_ROOT.equals(parentMediaId)) {
            LogHelper.d(TAG, "OnLoadChildren.ROOT");
            mediaItems.add(new MediaItem(
                    new MediaDescription.Builder()
                        .setMediaId(ID_PAYMENT)
                        .setTitle(getString(R.string.menu_payment))
                        .setIconUri(Uri.parse("android.resource://com.share.gta/drawable/payment_icon"))
                        .build(), MediaItem.FLAG_BROWSABLE
            ));
            mediaItems.add(new MediaItem(
                    new MediaDescription.Builder()
                            .setMediaId(ID_HISTORY)
                            .setTitle(getString(R.string.menu_history))
                            .setIconUri(Uri.parse("android.resource://com.share.gta/drawable/history_icon"))
                            .build(), MediaItem.FLAG_BROWSABLE
            ));

        } else if (ID_HISTORY.equals(parentMediaId)) {
            LogHelper.d(TAG, "OnLoadChildren.MENU");
            for (Category category : menuProvider.getMenu()) {
                MediaItem item = new MediaItem(
                        new MediaDescription.Builder()
                                .setMediaId(ID_CATEGORY + category.getId())
                                .setTitle(category.getName())
                                .build(), MediaItem.FLAG_BROWSABLE
                );
                mediaItems.add(item);
            }
        } else if (parentMediaId.startsWith(ID_CATEGORY)) {
            long id = Long.valueOf(parentMediaId.substring(ID_CATEGORY.length(), parentMediaId.length()));
            Category category = menuProvider.getCategoryById(id);
            for (Product product : category.getProductList()) {
                MediaMetadata track = new MediaMetadata.Builder()
                        .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, "ID")
                        .putString(MediaMetadata.METADATA_KEY_ALBUM, "ALBUM")
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, "ARTIST")
                        .putLong(MediaMetadata.METADATA_KEY_DURATION, 100)
                        .putString(MediaMetadata.METADATA_KEY_GENRE, "GENRE")
                        .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, "ALBUM ART")
                        .putString(MediaMetadata.METADATA_KEY_TITLE, product.getName())
                        .putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, 1)
                        .putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, 10)
                        .build();
                MediaItem item = new MediaItem(track.getDescription(), MediaItem.FLAG_PLAYABLE);
                mediaItems.add(item);
            }
        }
//        } else if (parentMediaId.startsWith(MEDIA_ID_MUSICS_BY_GENRE)) {
//            String genre = extractBrowseCategoryFromMediaID(parentMediaId)[1];
//            LogHelper.d(TAG, "OnLoadChildren.SONGS_BY_GENRE  genre=", genre);
//            for (MediaMetadata track: mMusicProvider.getMusicsByGenre(genre)) {
//                // Since mediaMetadata fields are immutable, we need to create a copy, so we
//                // can set a hierarchy-aware mediaID. We will need to know the media hierarchy
//                // when we get a onPlayFromMusicID call, so we can create the proper queue based
//                // on where the music was selected from (by artist, by genre, random, etc)
//                String hierarchyAwareMediaID = MediaIDHelper.createTrackMediaID(
//                        MEDIA_ID_MUSICS_BY_GENRE, genre, track);
//                MediaMetadata trackCopy = new MediaMetadata.Builder(track)
//                        .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
//                        .build();
//                MediaItem bItem = new MediaItem(
//                        trackCopy.getDescription(), MediaItem.FLAG_PLAYABLE);
//                mediaItems.add(bItem);
//            }
//        } else {
//            LogHelper.w(TAG, "Skipping unmatched parentMediaId: ", parentMediaId);
//        }
            result.sendResult(mediaItems);


    }

    private void updatePlaybackState(String error) {
        PlaybackState.Builder stateBuilder = new PlaybackState.Builder()
                .setActions(getAvailableActions());

        if (error != null) {
            stateBuilder.setErrorMessage(error);
            stateBuilder.setState(PlaybackState.STATE_ERROR, 0, 0);
        }
        setCustomAction(stateBuilder);

        mSession.setPlaybackState(stateBuilder.build());
    }

    private long getAvailableActions() {
        long actions = PlaybackState.ACTION_PLAY | PlaybackState.ACTION_SET_RATING ;

        return actions;
    }

    private void setCustomAction(PlaybackState.Builder stateBuilder) {
//        stateBuilder.addCustomAction(CUSTOM_ACTION_THUMBS_UP, getString(R.string.custom_action1),
//                R.drawable.ic_star_on);
    }

    private void createMediaPlayerIfNeeded() {
        LogHelper.d(TAG, "createMediaPlayerIfNeeded. needed? " + (mMediaPlayer==null));
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

//            // we want the media player to notify us when it's ready preparing,
//            // and when it's done playing:
//            mMediaPlayer.setOnPreparedListener(this);
//            mMediaPlayer.setOnCompletionListener(this);
//            mMediaPlayer.setOnErrorListener(this);
        } else {
            mMediaPlayer.reset();
        }
    }

    private void playCurrentSong() {
//        MediaMetadata track = getCurrentPlayingMusic();
//        if (track == null) {
//            LogHelper.e(TAG, "playSong:  ignoring request to play next song, because cannot" +
//                    " find it." +
//                    " currentIndex=" + mCurrentIndexOnQueue +
//                    " playQueue.size=" + (mPlayingQueue==null?"null": mPlayingQueue.size()));
//            return;
//        }
            updatePlaybackState(null);
            updateMetadata();
    }

    private void updateMetadata() {
        updatePlaybackState("This is the meta-data");
        MediaMetadata track = new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, "ID")
                //.putString(CUSTOM_METADATA_TRACK_SOURCE, source)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, "ALBUM")
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "ARTIST")
                .putLong(MediaMetadata.METADATA_KEY_DURATION, 100)
                .putString(MediaMetadata.METADATA_KEY_GENRE, "GENRE")
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, "ALBUM ART")
                .putString(MediaMetadata.METADATA_KEY_TITLE, "TITLE")
                .putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, 1)
                .putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, 10)
                .build();

        mSession.setMetadata(track);
    }

    private final class MediaSessionCallback extends MediaSession.Callback {
        @Override
        public void onPlay() {
            mSession.setQueueTitle("onPlay");
            playCurrentSong();
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            mSession.setQueueTitle("onPlayFromMediaId");
            playCurrentSong();
        }

        @Override
        public void onPause() {
        }

        @Override
        public void onStop() {
        }

        @Override
        public void onSkipToNext() {
        }

        @Override
        public void onSkipToPrevious() {
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
//            if (CUSTOM_ACTION_THUMBS_UP.equals(action)) {
//                LogHelper.i(TAG, "onCustomAction: favorite for current track");
//                MediaMetadata track = getCurrentPlayingMusic();
//                if (track != null) {
//                    String mediaId = track.getString(MediaMetadata.METADATA_KEY_MEDIA_ID);
//                    mMusicProvider.setFavorite(mediaId, !mMusicProvider.isFavorite(mediaId));
//                }
//                updatePlaybackState(null);
//            } else {
                LogHelper.e(TAG, "Unsupported action: ", action);
//            }

        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            mSession.setQueueTitle("onPlayFromSearch");
        }
    }

}
