package com.pidogames.buggyplantation;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalPayment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DonatePayPalActivity extends Activity {


	private LinearLayout content;

	private Button cancelBt;

	private ProgressDialog progressDialog;

	public Spinner amountSpinner;
	public EditText amountEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// load PayPal button
		this.progressDialog = ProgressDialog.show(this, "PayPal", getString(R.string.donate_loading_paypal), true);
		new LoadPayPalButtonTask(this).execute();
	}

	private void onCreateAfterLoading() {
		// set the UI
		setContentView(R.layout.donate_paypal_activity);
		// get UI elements
		this.content = (LinearLayout) findViewById(R.id.content);
		this.cancelBt = (Button) findViewById(R.id.cancel_bt);

	}

	private void showPayPalButtonDialog(CheckoutButton button) {
		this.progressDialog.dismiss();
		// finish loading the UI component
		onCreateAfterLoading();
		// add the text message
		TextView paypalMsgTv = new TextView(this, null, android.R.attr.textAppearanceLarge);
		paypalMsgTv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		paypalMsgTv.setText(R.string.donate_paypal_button_message);
		this.content.addView(paypalMsgTv);
		// add an empty space
		View view = new View(this);
		view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));
		this.content.addView(view);
		// add amount text view + spinner
		TextView amountTv = new TextView(this, null, android.R.attr.textAppearanceMedium);
		amountTv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		amountTv.setText(R.string.donate_amount);
		this.content.addView(amountTv);
		this.amountSpinner = new Spinner(this);
		this.amountSpinner.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.amountSpinner.setPrompt(getString(R.string.donate_select_amount));
		ArrayAdapter<CharSequence> amountAdapter = ArrayAdapter.createFromResource(DonatePayPalActivity.this,
		        R.array.donate_amount_titles, android.R.layout.simple_spinner_item);
		amountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.amountSpinner.setAdapter(amountAdapter);
		
		this.amountEditText = new EditText(this);
		this.amountEditText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.amountEditText.setSingleLine();
		this.amountEditText.setVisibility(View.GONE);
		
		this.amountSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String [] amounts = getResources().getStringArray(R.array.donate_amounts);				
				final float amount = position>=0 ? Float.parseFloat(amounts[position]) : -1;
				amountEditText.setVisibility(amount<0 ? View.VISIBLE : View.GONE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				amountEditText.setVisibility(View.GONE);
			}
		});
				
		this.content.addView(this.amountSpinner);
		this.content.addView(this.amountEditText);
		// add PayPal button
		// button.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		this.content.addView(button);
		// add empty space
		View view2 = new View(this);
		view2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));
		this.content.addView(view2);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String [] amounts = getResources().getStringArray(R.array.donate_amounts);				
				float amount = Float.parseFloat(amounts[amountSpinner.getSelectedItemPosition()]);
				
				if(amount<0){
					try {
						amount = Float.parseFloat(amountEditText.getText().toString().replace("\n", "").replace(" ","").replace(",", "."));
						if(amount<1) {
							PayPalUtils.notifyTheUser(DonatePayPalActivity.this, DonatePayPalActivity.this.getString(R.string.donate_wrong_amount));
							finish();
							return;
						}
					}
					catch(NumberFormatException e){
						PayPalUtils.notifyTheUser(DonatePayPalActivity.this, DonatePayPalActivity.this.getString(R.string.donate_wrong_amount));
						finish();
						return;
					}
				}
				
				final float final_amount = amount;
				
				PayPalUtils.notifyTheUser(DonatePayPalActivity.this, DonatePayPalActivity.this.getString(R.string.donate_loading_paypal));
				
				PayPalPayment payPalPayment = PayPalUtils.getNewPayPalPayment(DonatePayPalActivity.this, final_amount);
				Intent paypalIntent = PayPalUtils.getInstance(DonatePayPalActivity.this).checkout(payPalPayment,
				        DonatePayPalActivity.this);
				DonatePayPalActivity.this.startActivityForResult(paypalIntent, PayPalUtils.paypalRequestCode);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PayPalUtils.paypalRequestCode:
			switch (resultCode) {
			case Activity.RESULT_OK:
				PayPalUtils.notifyTheUser(this, this.getString(R.string.donate_payment_completed));
				//this.finish();
				Toast.makeText(this, "Thank you for supporting our page!", Toast.LENGTH_LONG).show();
				break;
			case Activity.RESULT_CANCELED:
				PayPalUtils.notifyTheUser(this, this.getString(R.string.donate_payment_cancelled));
				break;
			case PayPalActivity.RESULT_FAILURE:
				String errorID = data.getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
				String errorMessage = data.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
				Log.e("PAYPAL","ERROR: "+errorID+", "+errorMessage);
				PayPalUtils.notifyTheUser(this, this.getString(R.string.donate_payment_failure));
			}
			this.finish();
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void cancel(View v) {
		this.finish();
	}

	public class LoadPayPalButtonTask extends AsyncTask<String, String, CheckoutButton> {

		private Context context;

		public LoadPayPalButtonTask(Context context) {
			this.context = context;
		}

		@Override
		protected CheckoutButton doInBackground(String... params) {
			return PayPalUtils.getCheckoutButton(this.context);
		}

		@Override
		protected void onPostExecute(CheckoutButton result) {
			super.onPostExecute(result);
			showPayPalButtonDialog(result);
		}
	}
}
