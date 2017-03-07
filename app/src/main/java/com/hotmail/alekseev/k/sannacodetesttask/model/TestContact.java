package com.hotmail.alekseev.k.sannacodetesttask.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestContact implements Serializable {
    private long id = -1;
    private String firstName;
    private String lastName;
    private String userGoogleId;
    private List<String> emails;
    private List<String> phoneNumbers;

    public TestContact() {
        emails = new ArrayList<>();
        phoneNumbers = new ArrayList<>();
    }

    public TestContact(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

        emails = new ArrayList<>();
        phoneNumbers = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String secondName) {
        this.lastName = secondName;
    }

    public String getUserGoogleId() {
        return userGoogleId;
    }

    public void setUserGoogleId(String userGoogleId) {
        this.userGoogleId = userGoogleId;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public void addEmail(String email) {
        this.emails.add(email);
    }

    public void addPhoneNumber(String phone) {
        this.phoneNumbers.add(phone);
    }

    public static class Comparators {
        public static Comparator<TestContact> getAscendingAlphabetComparator() {
            return new Comparator<TestContact>() {
                @Override
                public int compare(TestContact contact1, TestContact contact2) {
                    if (contact1.firstName.equals("")){
                        return contact1.lastName.compareTo(contact2.firstName);
                    } else if (contact2.firstName.equals("")){
                        return contact1.firstName.compareTo(contact2.lastName);
                    } else {
                        return contact1.firstName.compareTo(contact2.firstName);
                    }
                }
            };
        }

        public static Comparator<TestContact> getDescendingAlphabetComparator() {
            return new Comparator<TestContact>() {
                @Override
                public int compare(TestContact contact1, TestContact contact2) {
                    if (contact1.firstName.equals("")){
                        return contact2.lastName.compareTo(contact1.firstName);
                    } else if (contact2.firstName.equals("")){
                        return contact2.firstName.compareTo(contact1.lastName);
                    } else {
                        return contact2.firstName.compareTo(contact1.firstName);
                    }
                }
            };
        }
        public static Comparator<TestContact> getAscendingEmailsCountComparator() {
            return new Comparator<TestContact>() {
                @Override
                public int compare(TestContact contact1, TestContact contact2) {
                    return contact1.emails.size() - contact2.emails.size();
                }
            };
        }

        public static Comparator<TestContact> getDescendingEmailsCountComparator() {
            return new Comparator<TestContact>() {
                @Override
                public int compare(TestContact contact1, TestContact contact2) {
                    return contact2.emails.size() - contact1.emails.size();
                }
            };
        }

        public static Comparator<TestContact> getAscendingPhoneNumbersCountComparator() {
            return new Comparator<TestContact>() {
                @Override
                public int compare(TestContact contact1, TestContact contact2) {
                    return contact1.phoneNumbers.size() - contact2.phoneNumbers.size();
                }
            };
        }

        public static Comparator<TestContact> getDescendingPhoneNumbersCountComparator() {
            return new Comparator<TestContact>() {
                @Override
                public int compare(TestContact contact1, TestContact contact2) {
                    return contact2.phoneNumbers.size() - contact1.phoneNumbers.size();
                }
            };
        }
    }
}
