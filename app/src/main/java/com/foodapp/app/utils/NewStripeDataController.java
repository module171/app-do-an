package com.foodapp.app.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.foodapp.app.api.ApiClient;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

public class NewStripeDataController {
    CallBackSuccess callBackSuccess;
    public NewStripeDataController(Activity context){
        callBackSuccess= (CallBackSuccess) context;
    }
    public void CreateToken(final Card card, Context context) {
        callBackSuccess.onstart();
        Stripe stripe = new Stripe(context, ApiClient.INSTANCE.getStripe());
        stripe.createCardToken(
            card,
            new ApiResultCallback<Token>() {
                public void onSuccess(Token token) {
                    Log.e("Stripe Token", token.getId());
                    callBackSuccess.success(token);
                }
                public void onError(Exception error) {
                    callBackSuccess.failer(error);
                }
            }
        );
    }
}
