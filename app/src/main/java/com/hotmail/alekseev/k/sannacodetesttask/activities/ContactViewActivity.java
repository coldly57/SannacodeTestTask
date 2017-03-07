package com.hotmail.alekseev.k.sannacodetesttask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotmail.alekseev.k.sannacodetesttask.AppConst;
import com.hotmail.alekseev.k.sannacodetesttask.R;
import com.hotmail.alekseev.k.sannacodetesttask.Utils;
import com.hotmail.alekseev.k.sannacodetesttask.dao.ContactsDao;
import com.hotmail.alekseev.k.sannacodetesttask.dao.ContactsDaoDataBaseImpl;
import com.hotmail.alekseev.k.sannacodetesttask.model.TestContact;
import com.rey.material.app.Dialog;

import java.util.Locale;

import static com.hotmail.alekseev.k.sannacodetesttask.AppConst.REQUEST_CODE_EDIT_CONTACT;
import static com.hotmail.alekseev.k.sannacodetesttask.AppConst.RESULT_DELETE;
import static com.hotmail.alekseev.k.sannacodetesttask.AppConst.RESULT_EDIT;

public class ContactViewActivity extends AppCompatActivity {
    private static final String TAG = ContactViewActivity.class.getSimpleName();

    private ContactsDao mContactDao;

    private TestContact mContact;

    private LinearLayout mEmailsLayout;
    private LinearLayout mPhoneNumbersLayout;

    private TextView mFirstName;
    private TextView mLastName;

    private Dialog mDeleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);

        initData(savedInstanceState);

        initViews();

        initActionBar();

        initDeleteDialog();

        initContact();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact_view, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_edit:
                editContact();
                break;
            case R.id.action_delete:
                mDeleteDialog.show();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_CONTACT && resultCode == RESULT_EDIT) {
            mContact = (TestContact) data.getSerializableExtra(AppConst.EXTRA_CONTACT);

            initContact();
        }
    }

    @Override
    public void onBackPressed() {
        setActivityResult(RESULT_EDIT);

        super.onBackPressed();
    }

    /**
     * Initializes activity data.
     */
    private void initData(Bundle savedInstanceState) {
        mContactDao = ContactsDaoDataBaseImpl.getInstance(getApplicationContext());

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

        mFirstName = (TextView) findViewById(R.id.contact_first_name);
        mLastName = (TextView) findViewById(R.id.contact_last_name);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(String.format(Locale.getDefault(), "%s %s",
                    mContact.getFirstName(), mContact.getLastName()));
        }
    }

    /**
     * Adds email edit text field.
     */
    private void addEmailTextView(String value) {
        TextView tv = new TextView(ContactViewActivity.this);

        tv.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        tv.setId(View.generateViewId());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setTextSize(18);
        int padding = Utils.dpAsPixels(getApplicationContext(), 15);
        tv.setPadding(padding, padding, padding, padding);

        if (value != null && !value.equals("")) {
            tv.setText(value);
        }

        mEmailsLayout.addView(tv);
    }

    /**
     * Adds phone number edit text field.
     */
    private void addPhoneNumberTextView(String value) {
        TextView tv = new TextView(ContactViewActivity.this);

        tv.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        tv.setId(View.generateViewId());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setTextSize(18);
        int padding = Utils.dpAsPixels(getApplicationContext(), 15);
        tv.setPadding(padding, padding, padding, padding);

        if (value != null && !value.equals("")) {
            tv.setText(value);
        }

        mPhoneNumbersLayout.addView(tv);
    }

    /**
     * Initializes views and data related to information about contact.
     */
    private void initContact() {
        if (mContact != null) {
            mFirstName.setText(mContact.getFirstName());
            mLastName.setText(mContact.getLastName());

            if (mContact.getEmails().size() > 0) {
                for (int i = 0; i < mContact.getEmails().size(); i++) {
                    addEmailTextView(mContact.getEmails().get(i));
                }
            }

            if (mContact.getPhoneNumbers().size() > 0) {
                for (int i = 0; i < mContact.getPhoneNumbers().size(); i++) {
                    addPhoneNumberTextView(mContact.getPhoneNumbers().get(i));
                }
            }
        }
    }

    /**
     * Starts contact edit activity.
     */
    private void editContact(){
        Intent intent = new Intent(ContactViewActivity.this, ContactEditActivity.class);
        intent.putExtra(AppConst.EXTRA_CONTACT, mContact);
        startActivityForResult(intent, AppConst.REQUEST_CODE_EDIT_CONTACT);
    }

    /**
     * Deletes contact.
     */
    private void deleteContact(){
        mContactDao.deleteContact(mContact);
    }

    /**
     * Sets activity result.
     *
     * @param resultCode indicates what to do with current contact.
     */
    private void setActivityResult(int resultCode) {
        Intent intent = new Intent();
        intent.putExtra(AppConst.EXTRA_CONTACT, mContact);
        setResult(resultCode, intent);
    }

    /**
     * Initializes dialog warning user that {@link TestContact} instance is going to be deleted.
     */
    private void initDeleteDialog() {
        mDeleteDialog = new Dialog(ContactViewActivity.this);
        mDeleteDialog.contentView(R.layout.dialog_contact_delete)
                .positiveAction(getString(R.string.delete))
                .negativeAction(getString(R.string.cancel))
                .negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDeleteDialog.dismiss();
                    }
                })
                .positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteContact();

                        mDeleteDialog.dismiss();
                        setActivityResult(RESULT_DELETE);
                        finish();
                    }
                });
    }
}