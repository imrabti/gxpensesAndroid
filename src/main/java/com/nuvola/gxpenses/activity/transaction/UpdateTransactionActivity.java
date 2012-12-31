package com.nuvola.gxpenses.activity.transaction;

import java.util.Calendar;
import java.util.List;

import android.app.Dialog;
import android.app.DialogFragment;
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.TransactionRequest;
import com.nuvola.gxpenses.client.request.proxy.TransactionProxy;
import com.nuvola.gxpenses.shared.type.TransactionType;
import com.nuvola.gxpenses.util.Constants;

import com.nuvola.gxpenses.util.LoadingDialog;
import com.nuvola.gxpenses.util.SuggestionListFactory;
import com.nuvola.gxpenses.util.ValueListFactory;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import com.google.web.bindery.requestfactory.shared.Receiver;

import javax.inject.Inject;

@ContentView(R.layout.add_transaction_ui)
public class UpdateTransactionActivity extends RoboActivity {
	public static final String TAG = UpdateTransactionActivity.class.getName();
	public static final boolean DEBUG = Constants.DEBUG;
	
	@Inject
    GxpensesRequestFactory requestFactory;
    @Inject
    SuggestionListFactory suggestionListFactory;
    @Inject
    ValueListFactory valueListFactory;

	@InjectView(R.id.transaction_payee)
    AutoCompleteTextView transactionPayee;
	@InjectView(R.id.transaction_date)
    EditText transactionDate;
	@InjectView(R.id.transaction_amount)
    EditText transactionAmount;
	@InjectView(R.id.transaction_type)
    Spinner transactionType;
	@InjectView(R.id.transaction_tags)
    MultiAutoCompleteTextView transactionTags;
	
	private LoadingDialog loadingDialog;
	private TransactionRequest context;
	private TransactionProxy transactionProxy;
	private Double oldAmount;
	
	private int mYear;
	private int mMonth;
	private int mDayOfMonth;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(DEBUG) Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		context = requestFactory.transactionService();
		
		//Setting Up TransactionType Spinner
		ArrayAdapter<TransactionType> types = new ArrayAdapter<TransactionType>(this,
                android.R.layout.simple_spinner_item, TransactionType.getValues());
		types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		transactionType.setAdapter(types);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		//Setting Up Suggestions List
		transactionTags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		setUpAutocompletePayee();
		setUpAutocompleteTags();
		
		//Setting Up Date Field
		transactionDate.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialogFragment datePicker = new DatePickerDialogFragment();
                    datePicker.show(getFragmentManager(), "datePicker");
				}
				return false;
			}
		});
		
		loadingDialog = new LoadingDialog(this);
		Long transactionId = (Long) getIntent().getSerializableExtra("transactionId");
		new LoadTransactionTask().execute(transactionId);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.update_transaction_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			setResult(RESULT_CANCELED);
			finish();
			return true;
			
		case R.id.update_transaction_menu:
            oldAmount = -1 * transactionProxy.getAmount();
			context = requestFactory.transactionService();
            transactionProxy = context.edit(transactionProxy);

            transactionProxy.setPayee(transactionPayee.getText().toString());
            transactionProxy.setTags(transactionTags.getText().toString());
            transactionProxy.setType((TransactionType)transactionType.getSelectedItem());
			if(transactionAmount.getText().toString().length() > 0) {
                Double amount = Double.parseDouble(transactionAmount.getText().toString());
                Integer multiplier = transactionProxy.getType() == TransactionType.EXPENSE ? -1 : 1;
                transactionProxy.setAmount(amount * multiplier);
            } else {
                transactionProxy.setAmount(0d);
            }
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(transactionProxy.getDate());
			calendar.set(Calendar.YEAR, mYear);
			calendar.set(Calendar.MONTH, mMonth);
			calendar.set(Calendar.DAY_OF_MONTH, mDayOfMonth);
            transactionProxy.setDate(calendar.getTime());
			
			new UpdateTransactionTask().execute(transactionProxy);
			
			return true;
			
		case R.id.delete_transaction_menu:
			AlertDialog.Builder builder = new AlertDialog.Builder(this); 
			builder.setMessage("Are you sure you want to remove this transaction?")
				   .setTitle("Are you sure?")
				   .setCancelable(false)
				   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							new DeleteTransactionTask().execute();
						}
				   })
				   .setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
				   });
			AlertDialog alert = builder.create();
			alert.show();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);		
		}
	}
	
	private void setUpAutocompletePayee() {
		if(DEBUG) Log.d(TAG, "Setting up Payee suggestion list");
		List<String> listPayee = suggestionListFactory.getListPayee();
		transactionPayee.setAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_dropdown_item_1line, listPayee));
	}
	
	private void setUpAutocompleteTags() {
		if(DEBUG) Log.d(TAG, "Setting up Tags suggestion list");
		List<String> listTags = suggestionListFactory.getListTags();
		transactionTags.setAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_dropdown_item_1line, listTags));
	}
	
	private void updateTransactionDateDisplay() {
		transactionDate.setText(new StringBuilder()
				.append(mMonth + 1).append("-").append(mDayOfMonth).append("-")
				.append(mYear).append(" "));
	}
	
	private DatePickerDialog.OnDateSetListener dateCallback = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDayOfMonth = dayOfMonth;
			updateTransactionDateDisplay();
		}
	};

    private class DatePickerDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), dateCallback, mYear, mMonth, mDayOfMonth);
        }
    }
	
	private class LoadTransactionTask extends AsyncTask<Long, Void, Void> {
		@Override
		protected void onPreExecute() {
			loadingDialog.show();
		}

		@Override
		protected Void doInBackground(Long... params) {
			context.findByTransactionId(params[0]).fire(new Receiver<TransactionProxy>() {
				@Override
				public void onSuccess(TransactionProxy result) {
					if (DEBUG) Log.d(TAG, "Transaction information loaded =>" + result.getId());
					transactionProxy = result;
				}
			});
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			loadingDialog.dismiss();
			transactionPayee.setText(transactionProxy.getPayee());
			transactionAmount.setText(String.valueOf(Math.abs(transactionProxy.getAmount())));
			transactionTags.setText(transactionProxy.getTags());
			if(transactionProxy.getType() == TransactionType.EXPENSE) {
				transactionType.setSelection(0);
            } else {
				transactionType.setSelection(1);
            }
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(transactionProxy.getDate());
			mYear = calendar.get(Calendar.YEAR);
			mMonth = calendar.get(Calendar.MONTH);
			mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			updateTransactionDateDisplay();
			
			if(transactionProxy.getDestTransaction() != null) {
				transactionPayee.setEnabled(false);
				transactionType.setEnabled(false);
			}
		}

		@Override
		public void onCancelled() {
			if (DEBUG) Log.d(TAG, "Operation Canceled...");
			loadingDialog.dismiss();
		}
	}
	
	private class DeleteTransactionTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			loadingDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			requestFactory.transactionService().removeTransaction(transactionProxy.getId()).fire(new Receiver<Void>() {
				@Override
				public void onSuccess(Void result) {
					if (DEBUG) Log.d(TAG, "Transaction removed =>" + transactionProxy.getId());
				}
			});
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			loadingDialog.dismiss();
			
			//Close activity 
			Intent data = new Intent();
            data.putExtra("amount", transactionProxy.getAmount());
			setResult(RESULT_OK, data);
			finish();
		}
		
		@Override
		public void onCancelled() {
			if (DEBUG) Log.d(TAG, "Operation Canceled...");
			loadingDialog.dismiss();
		}
	}
	
	private class UpdateTransactionTask extends AsyncTask<TransactionProxy, Void, Void> {
		private TransactionProxy transaction;
		
		@Override
		protected void onPreExecute() {
			loadingDialog.show();
		}

		@Override
		protected Void doInBackground(TransactionProxy... params) {
			transaction = params[0];
			
			context.updateTransaction(transaction).fire(new Receiver<Void>() {
				@Override
				public void onSuccess(Void result) {
					Log.d(TAG, "Transaction Updated with success");
					context = requestFactory.transactionService();
                    suggestionListFactory.updatePayeeList(transaction.getPayee());
                    suggestionListFactory.updateTagsList(transaction.getTags());
				}
			});
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			loadingDialog.dismiss();
			
			//Close activity 
			Intent data = new Intent();
            data.putExtra("amount", transaction.getAmount());
			data.putExtra("oldAmount", oldAmount);
			setResult(RESULT_OK, data);
			finish();
		}
		
		@Override
		public void onCancelled() {
			if (DEBUG) Log.d(TAG, "Operation Canceled...");
			loadingDialog.dismiss();
		}
	}
}
