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

import android.app.Activity;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.widget.Toast;

import com.anypresence.masterpass_android_library.MPManager;
import com.anypresence.masterpass_android_library.dto.RestaurantRequest;
import com.anypresence.masterpass_android_library.interfaces.FutureCallback;
import com.anypresence.masterpass_android_library.util.ConnectionUtil;
import com.anypresence.masterpass_android_library.xml.StackOverflowXmlParser;
import com.anypresence.sdk.gadget_app_sample.models.User;
import com.share.gt.model.Category;
import com.share.gt.model.Product;
import com.share.gta.GadgetShopApplication;
import com.share.gta.R;
import com.share.gta.helper.LocationHelper_;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService extends MediaBrowserService {

    private static final String TAG = LogHelper.makeLogTag(OrderService.class.getSimpleName());

    private static final String ANDROID_AUTO_PACKAGE_NAME = "com.google.android.projection.gearhead";
    private static final String ANDROID_AUTO_SIMULATOR_PACKAGE_NAME = "com.google.android.mediasimulator";

    private static final String ID_ROOT = "__ROOT__";
    private static final String ID_PAYMENT = "_PAYMENT_";
    private static final String ID_HISTORY = "_HISTORY_";
    private static final String ID_RESTAURANTS = "_RESTAURANTS_";
    private static final String ID_CATEGORY = "__CATEGORY__";
    private static final String ID_PRODUCT = "__PRODUCT__";

    private static final String CUSTOM_ACTION_SHOPPING_CART = "custom_action_shopping_cart";
    private static final String CUSTOM_ACTION_CHECKOUT = "custom_action_checkout";
    private static final String CUSTOM_ACTION_PREVIOUS = "custom_action_previous";
    private static final String CUSTOM_ACTION_NEXT = "custom_action_next";

    private static final String RESOURCE_URI_PATH = "android.resource://com.share.gta/";

    private MediaSession mSession;
    private MenuProvider menuProvider;
    private MediaPlayer mMediaPlayer;

    private List<Product> shoppingCart = new ArrayList<>();
    private List<StackOverflowXmlParser.Entry> listRestaurants = null;

    private enum State {
        STATE_PRODUCT_DISPLAY, STATE_SHOPPING_CART, STATE_CHECKOUT
    }
    private State state = State.STATE_PRODUCT_DISPLAY;

    private int currentProductIndex = 0;

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
//        Bundle extras = new Bundle();
//        extras.putBoolean(
//                "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT",
//                true);
//        extras.putBoolean(
//                "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS",
//                true);
//        // If you want to reserve the Queue slot when there is no queue
//        // (mSession.setQueue(emptylist)), uncomment the lines below:
//        // extras.putBoolean(
//        //   "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE",
//        //   true);
//        mSession.setExtras(extras);

        menuProvider = MenuProvider.getInstance(getBaseContext());
        Product product = menuProvider.getProducts().get(0);
        displayProductPage(product);
        getRestaurantList();

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
            mediaItems.add(new MediaItem(
                    new MediaDescription.Builder()
                            .setMediaId(ID_RESTAURANTS)
                            .setTitle(getString(R.string.menu_restaurants))
                            //.setIconUri(Uri.parse("android.resource://com.share.gta/drawable/history_icon"))
                            .build(), MediaItem.FLAG_BROWSABLE
            ));

        } else if (ID_HISTORY.equals(parentMediaId)) {
            LogHelper.d(TAG, "OnLoadChildren.MENU");
//            for (Category category : menuProvider.getMenu()) {
//                MediaItem item = new MediaItem(
//                        new MediaDescription.Builder()
//                                .setMediaId(ID_CATEGORY + category.getId())
//                                .setTitle(category.getName())
//                                .build(), MediaItem.FLAG_BROWSABLE
//                );
//                mediaItems.add(item);
//            }
        } else if(ID_RESTAURANTS.equals(parentMediaId)) {
            if (listRestaurants != null) {
                for (StackOverflowXmlParser.Entry entry : listRestaurants) {
                    MediaItem item = new MediaItem(
                            new MediaDescription.Builder()
                                    .setMediaId(ID_RESTAURANTS + entry.id)
                                    .setTitle(entry.name)
                                    .build(), MediaItem.FLAG_PLAYABLE
                    );
                    mediaItems.add(item);
                }
            }
        } else if (parentMediaId.startsWith(ID_CATEGORY)) {
            long id = Long.valueOf(parentMediaId.substring(ID_CATEGORY.length(), parentMediaId.length()));
            Category category = menuProvider.getCategoryById(id);
            for (Product product : category.getProductList()) {
                MediaMetadata track = createMediaMetadata(product.getName(), "$" + product.getPrice(), "");
                MediaItem item = new MediaItem(track.getDescription(), MediaItem.FLAG_PLAYABLE);
                mediaItems.add(item);
            }
        }

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
        long actions = PlaybackState.ACTION_PLAY_PAUSE;

        return actions;
    }

    private void setCustomAction(PlaybackState.Builder stateBuilder) {
        boolean atBeginning = currentProductIndex == 0;
        boolean atEnd = currentProductIndex >= menuProvider.getProducts().size() - 1;

        Bundle extras = new Bundle();
        if (atBeginning) {
            extras.putBoolean(
                    "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_PREVIOUS",
                    true);
        } else if (atEnd) {
            extras.putBoolean(
                    "com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_SKIP_TO_NEXT",
                    true);
        }
        extras.putBoolean("com.google.android.gms.car.media.ALWAYS_RESERVE_SPACE_FOR.ACTION_QUEUE", true);
        mSession.setExtras(extras);

        //stateBuilder.addCustomAction(CUSTOM_ACTION_SHOPPING_CART, "Shopping Cart", R.drawable.icon_cart);
        if (!atBeginning) {
            stateBuilder.addCustomAction(CUSTOM_ACTION_PREVIOUS, "Previous", R.drawable.icon_back);
        }
        if (!atEnd) {
            stateBuilder.addCustomAction(CUSTOM_ACTION_NEXT, "Next", R.drawable.icon_next);
        }

        if (state != State.STATE_CHECKOUT && shoppingCart.size() > 0) {
            stateBuilder.addCustomAction(CUSTOM_ACTION_CHECKOUT, "Checkout", R.drawable.icon_paynext);
        }

    }

    private void displayPage(String line1, String line2, String backgroundUrl) {
        if (backgroundUrl == null || "".equals(backgroundUrl))
            backgroundUrl = "android.resource://com.share.gta/drawable/bg";
        updateMetadata(line1, line2, backgroundUrl);
    }

    private void displayProductPage(Product product) {
        state = State.STATE_PRODUCT_DISPLAY;
        displayPage(product.getName(), "$" + product.getPrice(), product.getImageUrl());
    }

    private void updateMetadata(String line1, String line2, String backgroundUrl) {
        updatePlaybackState(null);
        MediaMetadata track = createMediaMetadata(line1, line2, backgroundUrl);

        mSession.setMetadata(track);
    }

    private MediaMetadata createMediaMetadata(String line1, String line2, String backgroundUrl) {
        return new MediaMetadata.Builder()
                    .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, "ID")
//                    .putString(CUSTOM_METADATA_TRACK_SOURCE, source)
//                    .putString(MediaMetadata.METADATA_KEY_ALBUM, "ALBUM")
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, line2)
//                    .putLong(MediaMetadata.METADATA_KEY_DURATION, 100)
//                    .putString(MediaMetadata.METADATA_KEY_GENRE, "GENRE")
                    .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, backgroundUrl)
                    .putString(MediaMetadata.METADATA_KEY_TITLE, line1)
//                    .putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, 1)
//                    .putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, 10)
                    .build();
    }

    private final class MediaSessionCallback extends MediaSession.Callback {
        @Override
        public void onPlay() {
            mSession.setQueueTitle("onPlay");
            doMainButton();
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            mSession.setQueueTitle("onPlayFromMediaId");
            //get Product information here...
            displayPage("line1", "line2", "");
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
            if (CUSTOM_ACTION_CHECKOUT.equals(action)) {
                doCheckout();
            } else if (CUSTOM_ACTION_SHOPPING_CART.equals(action)) {
                doShoppingCart();
            } else if (CUSTOM_ACTION_PREVIOUS.equals(action)) {
                doPrevious();
            } else if (CUSTOM_ACTION_NEXT.equals(action)) {
                doNext();
            } else {
                LogHelper.e(TAG, "Unsupported action: ", action);
            }

        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            mSession.setQueueTitle("onPlayFromSearch");
        }
    }

    private void doNext() {
        List<Product> productList = menuProvider.getProducts();
        currentProductIndex++;
        if (currentProductIndex >= productList.size()) {
            currentProductIndex = productList.size() - 1;
        }
        displayProductPage(productList.get(currentProductIndex));
    }

    private void doPrevious() {
        List<Product> productList = menuProvider.getProducts();
        currentProductIndex--;
        if (currentProductIndex < 0) {
            currentProductIndex = 0;
        }
        displayProductPage(productList.get(currentProductIndex));
    }

    private void doMainButton() {
        if (state == null)
            state = State.STATE_PRODUCT_DISPLAY;

        switch (state) {
            case STATE_PRODUCT_DISPLAY:
                addToShoppingCart();
                break;
            case STATE_SHOPPING_CART:
                break;
            case STATE_CHECKOUT:
                break;
        }
    }

    private void addToShoppingCart() {
        Product product = menuProvider.getProducts().get(currentProductIndex);
        shoppingCart.add(product);
        updateShoppingCartQueue();
        updatePlaybackState("");
        Toast.makeText(getBaseContext(), product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    private void doShoppingCart() {
        state = State.STATE_SHOPPING_CART;
        //show items in queue
    }

    private void doCheckout() {
        state = State.STATE_CHECKOUT;
        double total = calculateTotalAmount();
        displayPage("$" + total + " TOTAL", shoppingCart.size() + " item(s)", "android.resource://com.share.gta/drawable/buy_with_masterpass2");
    }

    private double calculateTotalAmount() {
        double total = 0;
        for (Product product : shoppingCart) {
            total += product.getPrice();
        }
        return total;
    }

    private void updateShoppingCartQueue() {
        List<MediaSession.QueueItem> queueItems = new ArrayList<>();
        if (shoppingCart.size() > 0) {
            int count = 0;
            MediaMetadata track0 = createMediaMetadata("TOTAL: $" + calculateTotalAmount(), shoppingCart.size() + " item(s)", "");
            MediaSession.QueueItem item0 = new MediaSession.QueueItem(
                    track0.getDescription(), count++);
            queueItems.add(item0);

            for (Product product : shoppingCart) {
                MediaMetadata track = createMediaMetadata(product.getName(), "$" + product.getPrice(), product.getImageUrl());
                MediaSession.QueueItem item = new MediaSession.QueueItem(
                        track.getDescription(), count++);
                queueItems.add(item);
            }
        }
        mSession.setQueue(queueItems);
        mSession.setQueueTitle("Shopping Cart");
    }

    private void getRestaurantList() {
        FutureCallback<String> listener = new FutureCallback<String>() {
            @Override
            public void onSuccess(String response) {

                Log.d(TAG, "Approved Pairing Request: " + response);

                StackOverflowXmlParser soxp = new StackOverflowXmlParser();
                try {
                    listRestaurants = soxp.parse(response);
                } catch (Exception e) {
                    Log.d(TAG, "Approved Pairing Request: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e("Error Pairing Request: ", error.toString());
            }
        };

        String lat = LocationHelper_.getInstance_(getBaseContext()).getLat();
        String lon = LocationHelper_.getInstance_(getBaseContext()).getLon();
        String locationApiUrl = "http://dmartin.org:8021/restaurants/v1/restaurant?PageOffset=0&PageLength=10&Latitude="+lat+"&Longitude="+lon;
        String sessionId = "";
        User user = GadgetShopApplication.getInstance().getUser();
        if (user != null) {
            sessionId = user.getXSessionId();
        }
        ConnectionUtil.call(locationApiUrl, sessionId, null, listener);
    }

    private void clearShoppingCart() {
        shoppingCart.clear();
        updateShoppingCartQueue();
    }
}
