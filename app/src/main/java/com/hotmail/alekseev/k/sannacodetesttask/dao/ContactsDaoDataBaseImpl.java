package com.hotmail.alekseev.k.sannacodetesttask.dao;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.hotmail.alekseev.k.sannacodetesttask.AppConst;
import com.hotmail.alekseev.k.sannacodetesttask.data.DataBaseHelper;
import com.hotmail.alekseev.k.sannacodetesttask.model.TestContact;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Handles access to {@link TestContact} instances data stored in data base.
 */
public class ContactsDaoDataBaseImpl implements ContactsDao {
    private static Context sContext;
    private static volatile ContactsDaoDataBaseImpl sInstance;

    private Map<Long, TestContact> mContacts;

    private ContactsDaoDataBaseImpl(Context context) {
        sContext = context;
    }

    public static ContactsDaoDataBaseImpl getInstance(Context context){
        ContactsDaoDataBaseImpl localInstance = sInstance;
        if (localInstance == null){
            synchronized (ContactsDaoDataBaseImpl.class){
                localInstance = sInstance;
                if (localInstance == null){
                    sInstance = localInstance = new ContactsDaoDataBaseImpl(context);
                }
            }
        }

        return localInstance;
    }

    @Override
    public void loadContacts(String userGoogleId) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                DataBaseHelper dbHelper = DataBaseHelper.getInstance(sContext);
                mContacts = dbHelper.getContacts(params[0]);
                dbHelper.close();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                sContext.sendBroadcast(new Intent(AppConst.ACTION_CONTACTS_LOADED));
            }
        }.execute(userGoogleId);
    }

    @Override
    public Map<Long, TestContact> getContacts() {
        return this.mContacts;
    }

    @Override
    public TestContact addContact(TestContact contact) throws ExecutionException, InterruptedException {
        return new AsyncTask<TestContact, Void, TestContact>() {
            @Override
            protected TestContact doInBackground(TestContact... params) {
                DataBaseHelper dbHelper = DataBaseHelper.getInstance(sContext);
                TestContact newContact = dbHelper.addContact(params[0]);
                dbHelper.close();

                return newContact;
            }
        }.execute(contact).get();
    }

    @Override
    public void updateContact(TestContact contact) {
        new AsyncTask<TestContact, Void, Void>() {
            @Override
            protected Void doInBackground(TestContact... params) {
                DataBaseHelper dbHelper = DataBaseHelper.getInstance(sContext);
                dbHelper.updateContact(params[0]);
                dbHelper.close();

                return null;
            }
        }.execute(contact);
    }

    @Override
    public void deleteContact(TestContact contact) {
        new AsyncTask<TestContact, Void, Void>() {
            @Override
            protected Void doInBackground(TestContact... params) {
                DataBaseHelper dbHelper = DataBaseHelper.getInstance(sContext);
                dbHelper.deleteContact(params[0]);
                dbHelper.close();

                return null;
            }
        }.execute(contact);
    }
}
