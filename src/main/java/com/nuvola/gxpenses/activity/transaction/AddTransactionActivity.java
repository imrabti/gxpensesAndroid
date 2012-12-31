package com.nuvola.gxpenses.activity.transaction;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import com.nuvola.gxpenses.R;
import com.nuvola.gxpenses.client.request.GxpensesRequestFactory;
import com.nuvola.gxpenses.client.request.TransactionRequest;
import com.nuvola.gxpenses.client.request.proxy.AccountProxy;
import com.nuvola.gxpenses.client.request.proxy.TransactionProxy;
import com.nuvola.gxpenses.shared.type.TransactionType;
import com.nuvola.gxpenses.util.Constants;
import com.nuvola.gxpenses.util.LoadingDialog;
import com.nuvola.gxpenses.util.SuggestionListFactory;
import com.nuvola.gxpenses.util.ValueListFactory;
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
    private Date currentDate;
    private AccountProxy account;

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

        //Get the account
        account = valueListFactory.getAccountById((Long) getIntent().getSerializableExtra("accountId"));

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
                    DatePickerDialogFragment datePicker = new DatePickerDialogFragment();
                    datePicker.show(getFragmentManager(), "datePicker");
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
                newTransaction.setPayee(transactionPayee.getText().toString());
                newTransaction.setTags(transactionTags.getText().toString());
                newTransaction.setType((TransactionType) transactionType.getSelectedItem());
                newTransaction.setAccount(context.edit(account));
                if (transactionAmount.getText().toString().length() > 0) {
                    Double amount = Double.parseDouble(transactionAmount.getText().toString());
                    Integer multiplier = newTransaction.getType() == TransactionType.EXPENSE ? -1 : 1;
                    newTransaction.setAmount(amount * multiplier);
                } else {
                    newTransaction.setAmount(0d);
                }

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
        List<String> listPayee = new ArrayList<String>(suggestionListFactory.getListPayee());
        transactionPayee.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, listPayee));
    }

    private void setUpAutocompleteTags() {
        if (DEBUG) Log.d(TAG, "Setting up Tags suggestion list");
        List<String> listTags = new ArrayList<String>(suggestionListFactory.getListTags());
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
                    suggestionListFactory.updatePayeeList(transaction.getPayee());
                    suggestionListFactory.updateTagsList(transaction.getTags());
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(Boolean accounts) {
            loadingDialog.dismiss();

            //Close activity
            Intent data = new Intent();
            data.putExtra("amount", transaction.getAmount());
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
