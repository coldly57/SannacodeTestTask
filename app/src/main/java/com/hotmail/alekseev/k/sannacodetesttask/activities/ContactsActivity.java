package com.hotmail.alekseev.k.sannacodetesttask.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.hotmail.alekseev.k.sannacodetesttask.AppConst;
import com.hotmail.alekseev.k.sannacodetesttask.R;
import com.hotmail.alekseev.k.sannacodetesttask.Utils;
import com.hotmail.alekseev.k.sannacodetesttask.adapters.ContactsRecyclerAdapter;
import com.hotmail.alekseev.k.sannacodetesttask.dao.ContactsDao;
import com.hotmail.alekseev.k.sannacodetesttask.dao.ContactsDaoDataBaseImpl;
import com.hotmail.alekseev.k.sannacodetesttask.enums.SortingType;
import com.hotmail.alekseev.k.sannacodetesttask.model.TestContact;
import com.rey.material.app.Dialog;

import java.util.HashMap;
import java.util.Map;

import static com.hotmail.alekseev.k.sannacodetesttask.AppConst.EXTRA_CONTACT;
import static com.hotmail.alekseev.k.sannacodetesttask.AppConst.REQUEST_CODE_EDIT_CONTACT;
import static com.hotmail.alekseev.k.sannacodetesttask.AppConst.REQUEST_CODE_VIEW_CONTACT;
import static com.hotmail.alekseev.k.sannacodetesttask.AppConst.RESULT_DELETE;
import static com.hotmail.alekseev.k.sannacodetesttask.AppConst.RESULT_EDIT;

public class ContactsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = ContactsActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    private ContactsDao mContactsDao;

    private Map<Long, TestContact> mContacts = new HashMap<>();

    private ContactsRecyclerAdapter mListRecyclerAdapter;

    private TextView mTapToAddContact;

    private Dialog mLogoutDialog;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(AppConst.ACTION_CONTACTS_LOADED)) {
                mContacts = mContactsDao.getContacts();

                mListRecyclerAdapter.updateContactsList(mContacts);

                showNoContactsMessage();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initGoogleApiClient();

        initViews();

        initData();

        initActionBar();

        initLogoutDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_exit:
                mLogoutDialog.show();
                break;
            case R.id.action_sort:
                showSortingDialog();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mReceiver, makeIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_VIEW_CONTACT || requestCode == REQUEST_CODE_EDIT_CONTACT) {
            if (resultCode == RESULT_EDIT) {
                TestContact contact = (TestContact) data.getSerializableExtra(EXTRA_CONTACT);
                mContacts.put(contact.getId(), contact);
            } else if (resultCode == RESULT_DELETE) {
                TestContact contact = (TestContact) data.getSerializableExtra(EXTRA_CONTACT);
                mContacts.remove(contact.getId());
            }

            showNoContactsMessage();

            mListRecyclerAdapter.updateContactsList(mContacts);
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Utils.saveCurrentUserGoogleId(getApplicationContext(), null);
                            startLoginActivity();
                            finish();
                        }
                        Log.d(TAG, "Sign out result: " + status.getStatusMessage());
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * Initializes activity data.
     */
    private void initData() {
        mContactsDao = ContactsDaoDataBaseImpl.getInstance(getApplicationContext());
        mContactsDao.loadContacts(Utils.getCurrentUserGoogleId(getApplicationContext()));

    }

    /**
     * Initializes view elements of activity.
     */
    private void initViews() {
        mListRecyclerAdapter = new ContactsRecyclerAdapter(getApplicationContext(), mContacts, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        RecyclerView listRecyclerView = (RecyclerView) findViewById(R.id.contacts_recycler_view);
        listRecyclerView.setLayoutManager(mLayoutManager);

        listRecyclerView.setAdapter(mListRecyclerAdapter);

        mTapToAddContact = (TextView) findViewById(R.id.tap_to_add_contact);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_contact_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactEditActivity.class);
                startActivityForResult(intent, AppConst.REQUEST_CODE_EDIT_CONTACT);
            }
        });
    }

    /**
     * Makes intent filter for {@link BroadcastReceiver} with certain actions.
     *
     * @return created intent filter.
     */
    private IntentFilter makeIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConst.ACTION_CONTACTS_LOADED);

        return filter;
    }

    /**
     * Checks if list of contacts is empty and show message.
     */
    private void showNoContactsMessage() {
        if (mContacts.size() > 0) {
            mTapToAddContact.setVisibility(View.GONE);
        } else {
            mTapToAddContact.setVisibility(View.VISIBLE);
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.contacts);
        }
    }

    private void showSortingDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_sorting, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customView);

        final RadioButton alpabeticAscending = (RadioButton) customView.findViewById(R.id.alphabetic_ascending);
        final RadioButton alpabeticDescending = (RadioButton) customView.findViewById(R.id.alphabetic_descending);
        final RadioButton emailsAscending = (RadioButton) customView.findViewById(R.id.emails_count_ascending);
        final RadioButton emailsDescending = (RadioButton) customView.findViewById(R.id.emails_count_descending);
        final RadioButton phonesAscending = (RadioButton) customView.findViewById(R.id.phones_count_ascending);
        final RadioButton phonesDescending = (RadioButton) customView.findViewById(R.id.phones_count_descending);

        builder.setPositiveButton(getString(R.string.apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (alpabeticAscending.isChecked()) {
                    mListRecyclerAdapter.sort(SortingType.ALPHABETIC_ASCENDING);
                } else if (alpabeticDescending.isChecked()) {
                    mListRecyclerAdapter.sort(SortingType.ALPHABETIC_DESCENDING);
                } else if (emailsAscending.isChecked()) {
                    mListRecyclerAdapter.sort(SortingType.EMAILS_COUNT_ASCENDING);
                } else if (emailsDescending.isChecked()) {
                    mListRecyclerAdapter.sort(SortingType.EMAILS_COUNT_DESCENDING);
                } else if (phonesAscending.isChecked()) {
                    mListRecyclerAdapter.sort(SortingType.PHONES_COUNT_ASCENDING);
                } else if (phonesDescending.isChecked()) {
                    mListRecyclerAdapter.sort(SortingType.PHONES_COUNT_DESCENDING);
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(ContactsActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Initializes dialog warning user that {@link TestContact} instance is going to be deleted.
     */
    private void initLogoutDialog() {
        mLogoutDialog = new Dialog(ContactsActivity.this);
        mLogoutDialog.contentView(R.layout.dialog_logout)
                .positiveAction(getString(R.string.ok))
                .negativeAction(getString(R.string.cancel))
                .negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLogoutDialog.dismiss();
                    }
                })
                .positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signOut();
                    }
                });
    }
}
