package com.anypresence.masterpass_android_library.interfaces;

import com.anypresence.masterpass_android_library.dto.PairCheckoutResponse;

/**
 * Created by diego.rotondale on 1/19/2015.
 * Copyright (c) 2015 AnyPresence, Inc. All rights reserved.
 */
public interface ILightBox {

    void pairingViewDidCompletePairing(ViewController pairingViewController, Boolean success, Throwable error);

    void lightBoxDidCompletePreCheckout(ViewController lightBoxViewController, Boolean success, PairCheckoutResponse data, Throwable error);

    void lightBoxDidCompleteCheckout(ViewController pairingViewController, Boolean success, Throwable error);
}
