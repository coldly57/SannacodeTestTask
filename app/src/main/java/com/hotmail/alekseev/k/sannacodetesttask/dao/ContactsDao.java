package com.hotmail.alekseev.k.sannacodetesttask.dao;

import com.hotmail.alekseev.k.sannacodetesttask.model.TestContact;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Regulates operations with contacts information data sources.
 */
public interface ContactsDao {
    /**
     * Loads all {@link TestContact} instances from specific data source.
     *
     * @param userGoogleId is user id from google+ by which contacts are selected.
     */
    void loadContacts(String userGoogleId);

    /**
     * Gets all {@link TestContact} instances to UI after it has been loaded from data source.
     * @return map of cars in which key is {@link TestContact} instance id.
     */
    Map<Long, TestContact> getContacts();

    /**
     * Adds {@link TestContact} instance to specific data source.
     * @param contact is {@link TestContact} instance to be added.
     * @return save contact.
     */
    TestContact addContact(TestContact contact) throws ExecutionException, InterruptedException;

    /**
     * Updates {@link TestContact} instance in specific data source.
     * @param contact is {@link TestContact) instance to be updated.
     */
    void updateContact(TestContact contact);

    /**
     * Deletes {@link TestContact} instance from specific data source.
     * @param contact is {@link TestContact} instance to be deleted.
     */
    void deleteContact(TestContact contact);
}
