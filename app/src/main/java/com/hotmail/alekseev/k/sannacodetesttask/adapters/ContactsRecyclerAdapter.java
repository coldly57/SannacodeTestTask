package com.hotmail.alekseev.k.sannacodetesttask.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.hotmail.alekseev.k.sannacodetesttask.AppConst;
import com.hotmail.alekseev.k.sannacodetesttask.R;
import com.hotmail.alekseev.k.sannacodetesttask.activities.ContactViewActivity;
import com.hotmail.alekseev.k.sannacodetesttask.enums.SortingType;
import com.hotmail.alekseev.k.sannacodetesttask.model.TestContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Connects recycler view with data to be displayed.
 */
public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ViewHolder> {
    private static final String LOG_TAG = ContactsRecyclerAdapter.class.getSimpleName();

    private Context mContext;
    private Activity mActivity;

    private ArrayList<TestContact> mContacts;

    public ContactsRecyclerAdapter(Context context, Map<Long, TestContact> contacts, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        this.mContacts = new ArrayList<>(contacts.values());
    }

    /**
     * Updates list of contacts to be displayed.
     *
     * @param contacts is updated map of contacts.
     */
    public void updateContactsList(Map<Long, TestContact> contacts) {
        this.mContacts = new ArrayList<>(contacts.values());

        notifyDataSetChanged();
    }

    @Override
    public ContactsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);

        return new ViewHolder(v);
    }

    public void sort(SortingType type){
        switch (type){
            case ALPHABETIC_ASCENDING:
                Collections.sort(this.mContacts, TestContact.Comparators.getAscendingAlphabetComparator());
                break;
            case ALPHABETIC_DESCENDING:
                Collections.sort(this.mContacts, TestContact.Comparators.getDescendingAlphabetComparator());
                break;
            case EMAILS_COUNT_ASCENDING:
                Collections.sort(this.mContacts, TestContact.Comparators.getAscendingEmailsCountComparator());
                break;
            case EMAILS_COUNT_DESCENDING:
                Collections.sort(this.mContacts, TestContact.Comparators.getDescendingEmailsCountComparator());
                break;
            case PHONES_COUNT_ASCENDING:
                Collections.sort(this.mContacts, TestContact.Comparators.getAscendingPhoneNumbersCountComparator());
                break;
            case PHONES_COUNT_DESCENDING:
                Collections.sort(this.mContacts, TestContact.Comparators.getDescendingPhoneNumbersCountComparator());
                break;
            default:
                break;
        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ContactsRecyclerAdapter.ViewHolder holder, int position) {
        final TestContact contact = mContacts.get(holder.getAdapterPosition());

        holder.contactName.setText(String.format(Locale.getDefault(), contact.getFirstName().equals("") ?
                        "%s%s" : "%s %s", contact.getFirstName(), contact.getLastName()));
        holder.emailsCount.setText(String.format(Locale.getDefault(), "Emails: %d",
                contact.getEmails().size()));
        holder.phoneNumbersCount.setText(String.format(Locale.getDefault(), "Phone numbers: %d",
                contact.getPhoneNumbers().size()));

        holder.contactItemRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ContactViewActivity.class);
                intent.putExtra(AppConst.EXTRA_CONTACT, contact);
                mActivity.startActivityForResult(intent, AppConst.REQUEST_CODE_VIEW_CONTACT);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * Hold place for data views.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private MaterialRippleLayout contactItemRipple;

        private TextView contactName;
        private TextView emailsCount;
        private TextView phoneNumbersCount;

        ViewHolder(View itemView) {
            super(itemView);
            contactItemRipple = (MaterialRippleLayout) itemView.findViewById(R.id.contact_item_ripple);

            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            emailsCount = (TextView) itemView.findViewById(R.id.emails_count);
            phoneNumbersCount = (TextView) itemView.findViewById(R.id.phone_numbers_count);
        }
    }
}
