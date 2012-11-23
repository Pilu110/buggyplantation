package com.pidogames.buggyplantation;

import java.math.BigDecimal;
import java.util.Locale;

import android.content.Context;
import android.widget.Toast;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalPayment;

public class PayPalUtils {

	private static PayPal instance;
	
	private static final int server   = PayPal.ENV_LIVE; //PayPal.ENV_SANDBOX;
	private static final String appID = server==PayPal.ENV_LIVE ? "APP-2F1158416X663991E" : "APP-80W284485P519543T";
	
	public static PayPal getInstance(Context context) {
		if (instance == null) {
			instance = PayPal.initWithAppID(context, appID, server);
			instance.setShippingEnabled(false);
			instance.setLanguage(Locale.getDefault().toString());
			//instance.setLanguage("en_US");
		}
		return instance;
	}

	public static final int paypalRequestCode = 10;

	public static PayPalPayment getNewPayPalPayment(Context context, double amount) {
		PayPalPayment newPayment = new PayPalPayment();
		newPayment.setSubtotal(new BigDecimal(amount));
		newPayment.setCurrencyType("USD");
		newPayment.setMerchantName("Vincze Games - Buggy plantation");
		newPayment.setRecipient("vinczegames@gmail.com");
		//newPayment.setRecipient("james_1341466472_pre@gmail.com");
		return newPayment;
	}

	public static CheckoutButton getCheckoutButton(Context context) {
		return getInstance(context).getCheckoutButton(context, PayPal.BUTTON_294x45, CheckoutButton.TEXT_DONATE);
	}
	
	public static void notifyTheUser(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

}
