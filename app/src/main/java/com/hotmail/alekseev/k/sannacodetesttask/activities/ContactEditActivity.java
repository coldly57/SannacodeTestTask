package com.hotmail.alekseev.k.sannacodetesttask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hotmail.alekseev.k.sannacodetesttask.AppConst;
import com.hotmail.alekseev.k.sannacodetesttask.R;
import com.hotmail.alekseev.k.sannacodetesttask.Utils;
import com.hotmail.alekseev.k.sannacodetesttask.dao.ContactsDao;
import com.hotmail.alekseev.k.sannacodetesttask.dao.ContactsDaoDataBaseImpl;
import com.hotmail.alekseev.k.sannacodetesttask.model.TestContact;
import com.rey.material.app.Dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.hotmail.alekseev.k.sannacodetesttask.AppConst.RESULT_EDIT;

public class ContactEditActivity extends AppCompatActivity {
    private static final String TAG = ContactEditActivity.class.getSimpleName();

    private ContactsDao mContactDao;

    private TestContact mContact;

    private List<EditText> mEmailsEditTexts;
    private List<EditText> mPhoneNumbersEditTexts;

    private LinearLayout mEmailsLayout;
    private LinearLayout mPhoneNumbersLayout;

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailOrigin;
    private EditText mPhoneNumberOrigin;

    private Dialog mSaveChangesDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        initData(savedInstanceState);

        initViews();

        initActionBar();

        initSaveChangesDialog();

        initContact();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_edit, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save:
                if (saveContact()) {
                    finish();
                }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mSaveChangesDialog.show();
    }

    /**
     * Initializes activity data.
     */
    private void initData(Bundle savedInstanceState) {
        mContactDao = ContactsDaoDataBaseImpl.getInstance(getApplicationContext());
        mEmailsEditTexts = new ArrayList<>();
        mPhoneNumbersEditTexts = new ArrayList<>();

        if (getIntent() != null) {
            mContact = (TestContact) getIntent().getSerializableExtra(AppConst.EXTRA_CONTACT);
        } else if (savedInstanceState != null) {
            mContact = (TestContact) savedInstanceState.getSerializable(AppConst.EXTRA_CONTACT);
        }
    }

    /**
     * Initializes activity views.
     */
    private void initViews() {
        mEmailsLayout = (LinearLayout) findViewById(R.id.emails_layout);
        mPhoneNumbersLayout = (LinearLayout) findViewById(R.id.phone_numbers_layout);

        mFirstName = (EditText) findViewById(R.id.contact_first_name);
        mLastName = (EditText) findViewById(R.id.contact_last_name);
        mEmailOrigin = (EditText) findViewById(R.id.contact_email_origin);
        mPhoneNumberOrigin = (EditText) findViewById(R.id.contact_phone_number_origin);

        ImageView addEmailButton = (ImageView) findViewById(R.id.add_email);
        addEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmailEditText(null);
            }
        });

        ImageView addPhoneNumberButton = (ImageView) findViewById(R.id.add_phone_number);
        addPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoneNumberEditText(null);
            }
        });
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (mContact != null) {
                actionBar.setTitle(String.format(Locale.getDefault(), "%s %s",
                        mContact.getFirstName(), mContact.getLastName()));
            } else {
                actionBar.setTitle(getString(R.string.contact_new_contact));
            }
        }
    }

    /**
     * Adds email edit text field.
     */
    private void addEmailEditText(String value) {
        EditText et = new EditText(ContactEditActivity.this);

        mEmailsEditTexts.add(et);
        et.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        et.setId(View.generateViewId());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = Utils.dpAsPixels(getApplicationContext(), 1);
        layoutParams.setMargins(0, 0, 0, margin);
        et.setLayoutParams(layoutParams);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        et.setTextSize(18);
        et.setHint(String.format(Locale.getDefault(), "%s #%d",
                getString(R.string.contact_email), mEmailsEditTexts.size() + 1));
        int padding = Utils.dpAsPixels(getApplicationContext(), 15);
        et.setPadding(padding, padding, padding, padding);

        if (value != null && !value.equals("")) {
            et.setText(value);
        }

        mEmailsLayout.addView(et);
    }

    /**
     * Adds phone number edit text field.
     */
    private void addPhoneNumberEditText(String value) {
        EditText et = new EditText(ContactEditActivity.this);

        mPhoneNumbersEditTexts.add(et);
        et.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        et.setId(View.generateViewId());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = Utils.dpAsPixels(getApplicationContext(), 1);
        layoutParams.setMargins(0, 0, 0, margin);
        et.setLayoutParams(layoutParams);
        et.setInputType(InputType.TYPE_CLASS_PHONE);
        et.setTextSize(18);
        et.setHint(String.format(Locale.getDefault(), "%s #%d",
                getString(R.string.contact_phone_number), mPhoneNumbersEditTexts.size() + 1));
        int padding = Utils.dpAsPixels(getApplicationContext(), 15);
        et.setPadding(padding, padding, padding, padding);

        if (value != null && !value.equals("")) {
            et.setText(value);
        }

        mPhoneNumbersLayout.addView(et);
    }

    /**
     * Initializes views and data related to information about contact.
     */
    private void initContact() {
        if (mContact != null) {
            mFirstName.setText(mContact.getFirstName());
            mLastName.setText(mContact.getLastName());

            if (mContact.getEmails().size() > 0) {
                mEmailOrigin.setText(mContact.getEmails().get(0));

                for (int i = 1; i < mContact.getEmails().size(); i++) {
                    addEmailEditText(mContact.getEmails().get(i));
                }
            }

            if (mContact.getPhoneNumbers().size() > 0) {
                mPhoneNumberOrigin.setText(mContact.getPhoneNumbers().get(0));

                for (int i = 1; i < mContact.getPhoneNumbers().size(); i++) {
                    addPhoneNumberEditText(mContact.getPhoneNumbers().get(i));
                }
            }
        }
    }

    /**
     * Saves contact instance according to filled data.
     */
    private boolean saveContact() {
        if (mFirstName.getText().toString().equals("")
                && mLastName.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.contact_empty_fields), Toast.LENGTH_SHORT).show();

            return false;
        }

        createContact();

        if (mContact.getId() == -1) {
            addContact();
        } else {
            updateContact();
        }

        setActivityResult(true);

        return true;
    }

    /**
     * Creates contact instance according to filled data.
     */
    private void createContact() {
        if (mContact == null) {
            mContact = new TestContact();
        }
        mContact.setUserGoogleId(Utils.getCurrentUserGoogleId(getApplicationContext()));

        mContact.setFirstName(mFirstName.getText().toString());
        mContact.setLastName(mLastName.getText().toString());

        mContact.getEmails().clear();
        mContact.getPhoneNumbers().clear();

        if (!mEmailOrigin.getText().toString().equals("")) {
            mContact.addEmail(mEmailOrigin.getText().toString());
        }

        if (!mPhoneNumberOrigin.getText().toString().equals("")) {
            mContact.addPhoneNumber(mPhoneNumberOrigin.getText().toString());
        }

        for (EditText et : mEmailsEditTexts) {
            if (!et.getText().toString().equals("")) {
                mContact.addEmail(et.getText().toString());
            }
        }

        for (EditText et : mPhoneNumbersEditTexts) {
            if (!et.getText().toString().equals("")) {
                mContact.addPhoneNumber(et.getText().toString());
            }
        }
    }

    /**
     * Adds {@link TestContact} instance to database.
     */
    private void addContact() {
        try {
            mContact = mContactDao.addContact(mContact);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Adding contact failed. Exception message: " + e.getMessage());
        }
    }

    /**
     * Updates {@link TestContact} instance in data source.
     */
    private void updateContact() {
        mContactDao.updateContact(mContact);
    }

    /**
     * Sets result of activity to be transferred to previous one.
     *
     * @param isSaved indicated if contact was saved.
     */
    private void setActivityResult(boolean isSaved) {
        if (isSaved) {
            Intent intent = new Intent();
            intent.putExtra(AppConst.EXTRA_CONTACT, mContact);
            setResult(RESULT_EDIT, intent);
        }
    }

    /**
     * Initializes dialog asking user if he wants save changes.
     */
    private void initSaveChangesDialog() {
        mSaveChangesDialog = new Dialog(ContactEditActivity.this);
        mSaveChangesDialog.contentView(R.layout.dialog_save_changes)
                .positiveAction(getString(R.string.yes))
                .negativeAction(getString(R.string.no))
                .negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSaveChangesDialog.dismiss();
                        setActivityResult(false);
                        finish();
                    }
                })
                .positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSaveChangesDialog.dismiss();
                        if (saveContact()) {
                            finish();
                        }
                    }
                });
    }
}