package com.hotmail.alekseev.k.sannacodetesttask;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.hotmail.alekseev.k.sannacodetesttask.data.DataBaseHelper;
import com.hotmail.alekseev.k.sannacodetesttask.model.TestContact;

import java.util.Map;

public class DbTesting extends AndroidTestCase {
    private static final String LOG = DbTesting.class.getSimpleName();

    private TestContact mContact1;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mContact1 = new TestContact("Konstantin", "Alekseev");

        mContact1.setId(1);

        mContact1.setUserGoogleId("fffffaaaaaa");

        mContact1.addEmail("aaa");
        mContact1.addEmail("fff");

        mContact1.addPhoneNumber("111");
        mContact1.addPhoneNumber("222");
    }

    public void testCreateDb() {
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());
        dbHelper.close();
    }

    public void testDeleteDatabase() {
        mContext.deleteDatabase(DataBaseHelper.DATABASE_NAME);
    }

    public void testAddContact() {
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(mContext);

        TestContact contact = dbHelper.addContact(mContact1);

        assertTrue(contact.getId() != -1);

        dbHelper.close();

        Log.d(LOG, "Contact first name: " + contact.getFirstName());
        Log.d(LOG, "Contact last name: " + contact.getLastName());
        Log.d(LOG, "Contact id: " + contact.getId());
        Log.d(LOG, "Contact google id: " + contact.getUserGoogleId());
        Log.d(LOG, "Contact emails size: " + contact.getEmails().size());
        Log.d(LOG, "Contact phones size: " + contact.getPhoneNumbers().size());

        for (String email : contact.getEmails()){
            Log.d(LOG, "Contact email: " + email);
        }

        for (String phone : contact.getPhoneNumbers()){
            Log.d(LOG, "Contact phone number: " + phone);
        }
    }

    public void testContactsSize() {
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(mContext);

        Map<Long, TestContact> contacts = dbHelper.getContacts(mContact1.getUserGoogleId());

        assertTrue(contacts.size() > 0);

        dbHelper.close();

        Log.d(LOG, "Contact testContactSize passed, size is: " + contacts.size());
    }

    public void testDeleteContact() {
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(mContext);

        int effectedRows = dbHelper.deleteContact(mContact1);

        assertTrue(effectedRows > 0);

        dbHelper.close();

        Log.d(LOG, "Contact testDeleteContact passed, rows number: " + effectedRows);
    }

    public void testUpdateContact() {
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(mContext);

        mContact1.setFirstName("Danil");

        int effectedRows = dbHelper.updateContact(mContact1);

        assertTrue(effectedRows > 0);

        for (TestContact contact : dbHelper.getContacts(mContact1.getUserGoogleId()).values()){
            Log.d(LOG, "Contact new contact name: " + contact.getFirstName());
        }

        dbHelper.close();

        Log.d(LOG, "testUpdateCar passed, rows number: " + effectedRows);
    }
}
