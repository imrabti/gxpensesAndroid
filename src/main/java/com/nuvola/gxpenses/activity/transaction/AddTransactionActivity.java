package com.nuvola.gxpenses.activity.transaction;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.TransactionRequest;
import com.nuvola.gxpenses.client.request.proxy.TransactionProxy;
import com.nuvola.gxpenses.shared.type.TransactionType;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.LoadingDialog;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ContentView(R.layout.add_transaction_ui)
public class AddTransactionActivity extends RoboActivity {
    public static final String TAG = AddTransactionActivity.class.getName();
    public static final boolean DEBUG = Constants.DEBUG;
    public static final int DATE_DIALOG_ID = 0;

    @Inject
    GxpensesRequestFactory requestFactory;
    @Inject
    SharedPreferences sharedPreferences;

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
    private Date currentDate;
    private String accountId;

    private int mYear;
    private int mMonth;
    private int mDayOfMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        context = requestFactory.transactionService();

        //Setting Up TransactionType Spinner
        ArrayAdapter<TransactionType> types = new ArrayAdapter<TransactionType>(this,
                android.R.layout.simple_spinner_item, TransactionType.getValues());
        types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transactionType.setAdapter(types);

        //Get the accountID
        accountId = (String) getIntent().getSerializableExtra("accountId");

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        loadingDialog = new LoadingDialog(this);
        currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        updateTransactionDateDisplay();

        //Setting Up Suggestions List
        transactionTags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        setUpAutocompletePayee();
        setUpAutocompleteTags();

        //Setting Up Date Field
        transactionDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showDialog(DATE_DIALOG_ID);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_transaction_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;

            case R.id.save_transaction_menu:
                TransactionProxy newTransaction = context.create(TransactionProxy.class);
                context.edit(newTransaction);
                newTransaction.setPayee(transactionPayee.getText().toString());
                newTransaction.setTags(transactionTags.getText().toString());
                newTransaction.setType((TransactionType) transactionType.getSelectedItem());
                if (transactionAmount.getText().toString().length() > 0)
                    newTransaction.setAmount(Double.parseDouble(transactionAmount.getText().toString()));
                else
                    newTransaction.setAmount(0d);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, mYear);
                calendar.set(Calendar.MONTH, mMonth);
                calendar.set(Calendar.DAY_OF_MONTH, mDayOfMonth);
                newTransaction.setDate(calendar.getTime());

                new SaveTransactionTask().execute(newTransaction);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpAutocompletePayee() {
        if (DEBUG) Log.d(TAG, "Setting up Payee suggestion list");
        List<String> listPayee = new ArrayList<String>(sharedPreferences.getStringSet("payee", null));
        transactionPayee.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, listPayee));
    }

    private void setUpAutocompleteTags() {
        if (DEBUG) Log.d(TAG, "Setting up Tags suggestion list");
        List<String> listTags = new ArrayList<String>(sharedPreferences.getStringSet("tags", null));
        transactionTags.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, listTags));
    }

    private void updateTransactionDateDisplay() {
        transactionDate.setText(new StringBuilder()
                .append(mMonth + 1).append("-").append(mDayOfMonth).append("-")
                .append(mYear).append(" "));
    }

    private DatePickerDialog.OnDateSetListener dateCallback =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDayOfMonth = dayOfMonth;
                    updateTransactionDateDisplay();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateCallback, mYear, mMonth, mDayOfMonth);
        }
        return null;
    }

    private class SaveTransactionTask extends AsyncTask<TransactionProxy, Void, Boolean> {
        private TransactionProxy transaction;

        @Override
        protected void onPreExecute() {
            loadingDialog.show();
        }

        @Override
        protected Boolean doInBackground(TransactionProxy... params) {
            transaction = params[0];

            context.createNewTransaction(transaction).fire(new Receiver<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Transaction Created with success");
                    context = requestFactory.transactionService();
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(Boolean accounts) {
            loadingDialog.dismiss();

            //Close activity
            Intent data = new Intent();
            if (transaction.getType() == TransactionType.INCOME) {
                data.putExtra("amount", transaction.getAmount());
            } else {
                data.putExtra("amount", -transaction.getAmount());
            }
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
