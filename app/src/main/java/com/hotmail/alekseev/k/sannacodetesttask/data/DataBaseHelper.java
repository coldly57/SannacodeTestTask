package com.hotmail.alekseev.k.sannacodetesttask.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hotmail.alekseev.k.sannacodetesttask.model.TestContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hotmail.alekseev.k.sannacodetesttask.data.DataBaseContract.ContactsEntry;
import static com.hotmail.alekseev.k.sannacodetesttask.data.DataBaseContract.EmailsEntry;
import static com.hotmail.alekseev.k.sannacodetesttask.data.DataBaseContract.PhoneEntry;

/**
 * Handles application operation with {@link SQLiteDatabase}.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = DataBaseHelper.class.getSimpleName();

    private static volatile DataBaseHelper sInstance;

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "sannacodetest.db";

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DataBaseHelper getInstance(Context context) {
        DataBaseHelper localInstance = sInstance;
        if (localInstance == null) {
            synchronized (DataBaseHelper.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new DataBaseHelper(context);
                }
            }
        }

        return localInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createContactsTable(sqLiteDatabase);
        createEmailsTable(sqLiteDatabase);
        createPhoneNumbersTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    /**
     * Creates table with contacts information in current database.
     *
     * @param sqLiteDatabase is current database.
     */
    private void createContactsTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + ContactsEntry.TABLE_NAME + " (" +
                ContactsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                ContactsEntry.COLUMN_FIRST_NAME + " TEXT, " +
                ContactsEntry.COLUMN_LAST_NAME + " TEXT, " +
                ContactsEntry.COLUMN_USER_GOOGLE_ID + " TEXT NOT NULL " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * Creates table with emails information in current database.
     *
     * @param sqLiteDatabase is current database.
     */
    private void createEmailsTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + EmailsEntry.TABLE_NAME + " (" +
                EmailsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                EmailsEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                EmailsEntry.COLUMN_CONTACT_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + EmailsEntry.COLUMN_CONTACT_ID + ") REFERENCES " +
                ContactsEntry.TABLE_NAME + " (" + ContactsEntry._ID + ") " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * Creates table with phone numbers information in current database.
     *
     * @param sqLiteDatabase is current database.
     */
    private void createPhoneNumbersTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + PhoneEntry.TABLE_NAME + " (" +
                PhoneEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                PhoneEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
                PhoneEntry.COLUMN_CONTACT_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + PhoneEntry.COLUMN_CONTACT_ID + ") REFERENCES " +
                ContactsEntry.TABLE_NAME + " (" + ContactsEntry._ID + ") " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }


    /**
     * Gets all {@link TestContact} instances stored in database for certain user.
     *
     * @param userGoogleId is Id of user in Google+.
     * @return map of {@link TestContact} instances in which key is {@link TestContact} instance id.
     */
    public Map<Long, TestContact> getContacts(String userGoogleId) {
        SQLiteDatabase db = getReadableDatabase();

        Map<Long, TestContact> contacts = new HashMap<>();
        Cursor cursor = db.query(ContactsEntry.TABLE_NAME, null, ContactsEntry.COLUMN_USER_GOOGLE_ID + "=?",
                new String[]{userGoogleId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                TestContact contact = new TestContact();
                contact.setId(cursor.getLong(cursor.getColumnIndex(ContactsEntry._ID)));
                contact.setFirstName(cursor.getString(cursor.getColumnIndex(ContactsEntry.COLUMN_FIRST_NAME)));
                contact.setLastName(cursor.getString(cursor.getColumnIndex(ContactsEntry.COLUMN_LAST_NAME)));
                contact.setEmails(getEmailsByContactId(contact.getId()));
                contact.setPhoneNumbers(getPhoneNumbersByContactId(contact.getId()));

                contacts.put(contact.getId(), contact);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return contacts;
    }

    /**
     * Adds {@link TestContact} instance to database.
     *
     * @param contact is {@link TestContact} instance to be added.
     * @return result of operation.
     */
    public TestContact addContact(TestContact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ContactsEntry.COLUMN_FIRST_NAME, contact.getFirstName());
        values.put(ContactsEntry.COLUMN_LAST_NAME, contact.getLastName());
        values.put(ContactsEntry.COLUMN_USER_GOOGLE_ID, contact.getUserGoogleId());

        for (String email : contact.getEmails()) {
            addEmail(email, contact);
        }

        for (String phone : contact.getPhoneNumbers()) {
            addPhone(phone, contact);
        }

        long contactId = db.insert(ContactsEntry.TABLE_NAME, null, values);

        contact.setId(contactId);

        return contact;
    }

    /**
     * Updates {@link TestContact} instance in database.
     *
     * @param contact is {@link TestContact} instance to be updated.
     * @return returns result of operation.
     */
    public int updateContact(TestContact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ContactsEntry.COLUMN_FIRST_NAME, contact.getFirstName());
        values.put(ContactsEntry.COLUMN_LAST_NAME, contact.getLastName());

        updateEmails(contact);

        updatePhoneNumbers(contact);

        return db.update(ContactsEntry.TABLE_NAME, values, ContactsEntry._ID + "=?", new String[]{Long.toString(contact.getId())});
    }

    /**
     * Deletes {@link TestContact} instance from database.
     *
     * @param contact is {@link TestContact} instance to be saved.
     * @return returns result of operation.
     */
    public int deleteContact(TestContact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (String email : contact.getEmails()) {
            deleteEmail(email, contact);
        }

        for (String phone : contact.getPhoneNumbers()) {
            deletePhone(phone, contact);
        }

        return db.delete(ContactsEntry.TABLE_NAME, ContactsEntry._ID + "=?",
                new String[]{Long.toString(contact.getId())});
    }

    /**
     * Gets list of emails from database by contact id.
     *
     * @param contactId is id of contact.
     * @return list of emails.
     */
    private List<String> getEmailsByContactId(long contactId) {
        SQLiteDatabase db = getReadableDatabase();

        List<String> emails = new ArrayList<>();
        Cursor cursor = db.query(EmailsEntry.TABLE_NAME, null, EmailsEntry.COLUMN_CONTACT_ID + "=?",
                new String[]{Long.toString(contactId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                emails.add(cursor.getString(cursor.getColumnIndex(EmailsEntry.COLUMN_EMAIL)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return emails;
    }

    /**
     * Adds email to database.
     *
     * @param email   is email address to be added.
     * @param contact is contact to which email is linked
     * @return result of operation.
     */
    private long addEmail(String email, TestContact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EmailsEntry.COLUMN_CONTACT_ID, contact.getId());
        values.put(EmailsEntry.COLUMN_EMAIL, email);

        return db.insert(EmailsEntry.TABLE_NAME, null, values);
    }

//    /**
//     * Updates email in database.
//     *
//     * @param email is email address to be updated.
//     * @param contact is contact to which email is linked
//     * @return returns result of operation.
//     */
//    private int updateEmail(String email, TestContact contact) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(EmailsEntry.COLUMN_EMAIL, email);
//
//        return db.update(EmailsEntry.TABLE_NAME, values, EmailsEntry.COLUMN_CONTACT_ID + "=?",
//                new String[]{Long.toString(contact.getId())});
//    }

    /**
     * Deletes email from database.
     *
     * @param email is email address to be deleted.
     * @param contact is contact to which email is linked
     * @return returns result of operation.
     */
    private int deleteEmail(String email, TestContact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(EmailsEntry.TABLE_NAME,
                EmailsEntry.COLUMN_CONTACT_ID + "=? AND " + EmailsEntry.COLUMN_EMAIL + "=?",
                new String[]{Long.toString(contact.getId()), email});
    }

    /**
     * Gets list of phone numbers from database by contact id.
     *
     * @param contactId is id of contact.
     * @return list of phone numbers.
     */
    private List<String> getPhoneNumbersByContactId(long contactId) {
        SQLiteDatabase db = getReadableDatabase();

        List<String> phoneNumbers = new ArrayList<>();
        Cursor cursor = db.query(PhoneEntry.TABLE_NAME, null, PhoneEntry.COLUMN_CONTACT_ID + "=?",
                new String[]{Long.toString(contactId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                phoneNumbers.add(cursor.getString(cursor.getColumnIndex(PhoneEntry.COLUMN_PHONE_NUMBER)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return phoneNumbers;
    }

    /**
     * Adds phone number to database.
     *
     * @param phone is phone number to be added.
     * @param contact is contact to which phone number is linked
     * @return result of operation.
     */
    private long addPhone(String phone, TestContact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PhoneEntry.COLUMN_CONTACT_ID, contact.getId());
        values.put(PhoneEntry.COLUMN_PHONE_NUMBER, phone);

        return db.insert(PhoneEntry.TABLE_NAME, null, values);
    }

//    /**
//     * Updates {@link TestPhone} instance in database.
//     *
//     * @param phone is {@link TestPhone} instance to be updated.
//     * @return returns result of operation.
//     */
//    private int updatePhone(TestPhone phone) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(PhoneEntry.COLUMN_PHONE_NUMBER, phone.getPhoneNumber());
//
//        return db.update(PhoneEntry.TABLE_NAME, values, PhoneEntry._ID + "=?",
//                new String[]{Long.toString(phone.getId())});
//    }

    /**
     * Deletes phone number from database.
     *
     * @param phone is phone number to be deleted.
     * @param contact is contact to which phone number is linked
     * @return returns result of operation.
     */
    private int deletePhone(String phone, TestContact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(PhoneEntry.TABLE_NAME,
                PhoneEntry.COLUMN_CONTACT_ID + "=? AND " + PhoneEntry.COLUMN_PHONE_NUMBER + "=?",
                new String[]{Long.toString(contact.getId()), phone});
    }

    /**
     * Checks if there are any emails to add/delete to/from database.
     * @param contact is contact to which emails are linked.
     */
    private void updateEmails(TestContact contact){
        List<String> emails = getEmailsByContactId(contact.getId());

        // Check if there are any emails to delete from database
        for (String email : emails){
            if (!contact.getEmails().contains(email)){
                deleteEmail(email, contact);
            }
        }

        // Check if there are any emails to add to database
        for (String email : contact.getEmails()){
            if (!emails.contains(email)){
                addEmail(email, contact);
            }
        }
    }

    /**
     * Checks if there are any phone numbers to add/delete to/from database.
     * @param contact is contact to which phone numbers are linked.
     */
    private void updatePhoneNumbers(TestContact contact){
        List<String> phoneNumbers = getPhoneNumbersByContactId(contact.getId());

        // Check if there are any phone numbers to delete from database
        for (String phoneNumber : phoneNumbers){
            if (!contact.getPhoneNumbers().contains(phoneNumber)){
                deletePhone(phoneNumber, contact);
            }
        }

        // Check if there are any phone numbers to add to database
        for (String phoneNumber : contact.getPhoneNumbers()){
            if (!phoneNumbers.contains(phoneNumber)){
                addPhone(phoneNumber, contact);
            }
        }
    }
}
