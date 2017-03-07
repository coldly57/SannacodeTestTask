package com.hotmail.alekseev.k.sannacodetesttask.data;

import android.provider.BaseColumns;

/**
 * Defines tables and columns names for the database.
 */
class DataBaseContract {

    /**
     * Defines the table contents of the contacts table.
     */
    static final class ContactsEntry implements BaseColumns {
        static final String TABLE_NAME = "contacts";

        static final String COLUMN_FIRST_NAME = "first_name";
        static final String COLUMN_LAST_NAME = "last_name";
        static final String COLUMN_USER_GOOGLE_ID = "user_google_id";
    }

    /**
     * Defines the table contents of the emails table.
     */
    static final class EmailsEntry implements BaseColumns {
        static final String TABLE_NAME = "emails";

        static final String COLUMN_EMAIL = "email";
        static final String COLUMN_CONTACT_ID = "contact_id";
    }

    /**
     * Defines the table contents of the phone numbers table.
     */
    static final class PhoneEntry implements BaseColumns {
        static final String TABLE_NAME = "phone_numbers";

        static final String COLUMN_PHONE_NUMBER = "phone_number";
        static final String COLUMN_CONTACT_ID = "contact_id";
    }
}
