package com.example.phonebook;

import static android.widget.Toast.LENGTH_SHORT;

import static java.util.Collections.sort;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SavingLoadingData {

    public static void save_data(Context context, ArrayList<Person> contact_list) throws IOException{
        Gson gson = new Gson();
        String json= gson.toJson(contact_list);
        SharedPreferences sharedPreferences= context.getSharedPreferences("CONTACT",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("Contacts",json).apply();
    }

    public static  ArrayList<Person> load_data(Context context) throws IOException{

        Gson gson = new Gson();
        SharedPreferences sharedPreferences= context.getSharedPreferences("CONTACT",Context.MODE_PRIVATE);
        String json= sharedPreferences.getString("Contacts","");
        if (json.isEmpty()) {
            Toast.makeText(context, "No contacts found", Toast.LENGTH_SHORT).show();
            return new ArrayList<>(); // Return an empty ArrayList when no contacts are found
        }

        Type type = new TypeToken<ArrayList<Person>>() {}.getType();
        ArrayList<Person> contact_list = gson.fromJson(json, type);

        if (contact_list == null) {
            Toast.makeText(context, "Error loading contacts", Toast.LENGTH_SHORT).show();
            return new ArrayList<>(); // Return an empty ArrayList when there is an error loading contacts
        }

        for (Person person : contact_list) {
            String firstName = person.getFname();
            if (firstName != null && !firstName.isEmpty()) {
                String firstLetter = firstName.substring(0, 1);
                String remainingLetters = firstName.substring(1);
                person.setFname(firstLetter.toUpperCase() + remainingLetters);
            }
            String lastName = person.getFname();
            if (lastName != null && !lastName.isEmpty()) {
                String firstLetter = lastName.substring(0, 1);
                String remainingLetters = lastName.substring(1);
                person.setFname(firstLetter.toUpperCase() + remainingLetters);
            }
        }

        contact_list.sort(Comparator.comparing(Person::getFname));
        return contact_list;
    }}
