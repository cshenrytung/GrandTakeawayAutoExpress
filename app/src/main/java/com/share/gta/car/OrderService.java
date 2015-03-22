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

import android.content.Intent;
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

import com.anypresence.masterpass_android_library.dto.CreditCard;
import com.anypresence.masterpass_android_library.dto.Order;
import com.anypresence.masterpass_android_library.dto.PairCheckoutResponse;
import com.anypresence.masterpass_android_library.dto.PreCheckoutResponse;
import com.anypresence.rails_droid.IAPFutureCallback;
import com.share.gt.model.Category;
import com.share.gt.model.Product;
import com.share.gta.GadgetShopApplication;
import com.share.gta.MPConstants;
import com.share.gta.R;
import com.share.gta.activity.BaseActivity;
import com.share.gta.activity.CheckoutActivity;
import com.share.gta.adapter.ProductAdapter;
import com.share.gta.domain.dto.ToCart;
import com.share.gta.util.MPECommerceManager;

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

    private static final String CUSTOM_ACTION_SHOPPING_CART = "custom_action_shopping_cart";
    private static final String CUSTOM_ACTION_CHECKOUT = "custom_action_checkout";
    private static final String CUSTOM_ACTION_PREVIOUS = "custom_action_previous";
    private static final String CUSTOM_ACTION_NEXT = "custom_action_next";

    private static final String RESOURCE_URI_PATH = "android.resource://com.share.gta/";

    private MediaSession mSession;
    private MenuProvider menuProvider;
    private MediaPlayer mMediaPlayer;

    private List<Product> shoppingCart = new ArrayList<>();
    private List<com.anypresence.sdk.gadget_app_sample.models.Product> products;

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
        stateBuilder.addCustomAction(CUSTOM_ACTION_CHECKOUT, "Checkout", R.drawable.icon_paynext);

    }

    private void displayPage(String line1, String line2, String backgroundUrl) {
        if (backgroundUrl == null || "".equals(backgroundUrl))
            backgroundUrl = "android.resource://com.share.gta/drawable/bg";
        updateMetadata(line1, line2, backgroundUrl);
    }

    private void displayProductPage(Product product) {
        state = State.STATE_PRODUCT_DISPLAY;
        displayPage(product.getName(), "$" + product.getPrice(), product.getImageUrl());

//        initProductList();
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
//        if (products != null && products.size() >= currentProductIndex && products.get(currentProductIndex) != null) {
//            com.anypresence.sdk.gadget_app_sample.models.Product onlineProduct = products.get(currentProductIndex);
//            if (onlineProduct.getName() != null && onlineProduct.getName().length() > 0) {
//                LogHelper.d(TAG, "product:" + onlineProduct.getName());
//                addProduct(onlineProduct);
//            }
//        }

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
//        double total = calculateTotalAmount();
        double total = getTotalPrice();
        displayPage("$" + total + " TOTAL", shoppingCart.size() + " item(s)", "android.resource://com.share.gta/drawable/buy_with_masterpass2");
        masterPassClick();
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

    private void initProductList() {
        LogHelper.d(TAG, "initProductList");
        MPECommerceManager.getInstance().getAllProducts(allProductsCallback);
    }

    private void doListProducts(List<com.anypresence.sdk.gadget_app_sample.models.Product> products) {
        LogHelper.d(TAG, "doListProducts");
        this.products = products;
        if (products != null && products.size() > 0) {
//            addProduct(products.get(0));
        }
    }

    private void addProduct(com.anypresence.sdk.gadget_app_sample.models.Product product) {
        if (currentProductIndex >= 0) {
            ToCart toCart = new ToCart(product, currentProductIndex % 2, currentProductIndex / 2);
            ((ProductAdapter.IProduct) this).addProduct(toCart);
        }
    }

    private void showAllProducts(final List<com.anypresence.sdk.gadget_app_sample.models.Product> products) {
        doListProducts(products);
    }

    private double getTotalPrice() {
        double totalPrice = GadgetShopApplication.getInstance().getTotal();
        return totalPrice;
    }

    private CreditCard getFirstCreditCard() {
        return getCreditCard(0);
    }

    private CreditCard getCreditCard(int position) {
        PairCheckoutResponse pairCheckoutData = null;
        int MANUAL_CHECKOUT = 1;
        PreCheckoutResponse preCheckoutData = null;
        int countOfCards;

        if (preCheckoutData == null) {
            countOfCards = MANUAL_CHECKOUT;
        } else
            countOfCards = preCheckoutData.cards.size() + MANUAL_CHECKOUT;
        if (countOfCards - 1 != position) {
            CreditCard creditCard = preCheckoutData.cards.get(position);
            return creditCard;
        } else {
            if (pairCheckoutData != null) {
                CreditCard creditCard = pairCheckoutData.checkout.card;
                return creditCard;
            }
        }
        return null;
    }

    private void masterPassClick() {
        boolean pairing = BaseServiceLibrary.isAppPaired();
        if (pairing) {
//            baseActivity.showProgress();
            Order order = GadgetShopApplication.getInstance().getOrder();
            order.card = getFirstCreditCard();

            BaseActivity baseActivity = new BaseActivity() {
                @Override
                public void checkoutDidComplete(Boolean success, Throwable error) {
                    // pay success
                    LogHelper.d(TAG, success ? "MasterPassCheckoutComplete" : "MasterPassCheckoutCancelled");
                }

//                @Override
//                public void pairingDidComplete(Boolean success, Throwable error) {
//                    // pair success
//                    Log.d(TAG, success ? "ConnectedMasterPass" : "MasterPassConnectionCancelled");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            dismissProgress();
//                        }
//                    });
//                    setPairStatus(success);
//                }
            };

            if (BaseServiceLibrary.isExpressCheckoutEnabled()){
                BaseServiceLibrary.getMCLibrary(baseActivity).expressCheckoutForOrder(order, baseActivity);
            }
            else {
                BaseServiceLibrary.getMCLibrary(baseActivity).returnCheckout(order, baseActivity);
            }
        }
    }

    //Callback
    public IAPFutureCallback<List<com.anypresence.sdk.gadget_app_sample.models.Product>> allProductsCallback = new IAPFutureCallback<List<com.anypresence.sdk.gadget_app_sample.models.Product>>() {
        @Override
        public void finished(List<com.anypresence.sdk.gadget_app_sample.models.Product> products, Throwable throwable) {
            LogHelper.e(TAG, throwable.getMessage());
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
//            emptyLoadingView.setErrorView();
        }

        @Override
        public void onSuccess(List<com.anypresence.sdk.gadget_app_sample.models.Product> products) {
//            emptyLoadingView.setEmptyView();
            showAllProducts(products);
        }

        @Override
        public void onFailure(Throwable throwable) {
            LogHelper.e(TAG, throwable.toString());
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
//            emptyLoadingView.setErrorView();
        }
    };
//    private IAPFutureCallback<Integer> cartQuantityCallback = new IAPFutureCallback<Integer>() {
//        @Override
//        public void finished(Integer integer, Throwable throwable) {
//            LogHelper.e(TAG, throwable.toString());
////            showErrorNotification();
//        }
//
//        @Override
//        public void onSuccess(final Integer size) {
////            getActivity().runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    if (size != 0) {
////                        notification.setVisibility(View.VISIBLE);
////                        notification.setText(String.valueOf(size));
////                    } else
////                        notification.setVisibility(View.GONE);
////                    notificationProgress.setVisibility(View.GONE);
////                }
////            });
//        }
//
//        @Override
//        public void onFailure(Throwable throwable) {
//            LogHelper.e(TAG, throwable.toString());
////            showErrorNotification();
//        }
//    };

}
