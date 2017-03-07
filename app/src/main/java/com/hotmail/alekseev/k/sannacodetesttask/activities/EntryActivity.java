package com.hotmail.alekseev.k.sannacodetesttask.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hotmail.alekseev.k.sannacodetesttask.Utils;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;

        if (Utils.getCurrentUserGoogleId(getApplicationContext()) != null){
            intent = new Intent(EntryActivity.this, ContactsActivity.class);
        } else {
            intent = new Intent(EntryActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
