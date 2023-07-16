package com.example.phonebook;

import static android.widget.Toast.LENGTH_SHORT;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    ImageButton addbtn,favouritesbtn,contactbtn,fixmanagebtn;
    private ContactAdapter adapter;
    private SearchView searchView;
    private ArrayList<Person> contact_list, filteredList,favouritesList,list;
    private RecyclerView recyclerView;
    private boolean isContactBtnClicked = false;
    private boolean isfavouritesBtnClicked = false;
    private boolean isFixManageBtnClicked = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addbtn=findViewById(R.id.addbtn);
        searchView=findViewById(R.id.searchView);
        favouritesbtn=findViewById(R.id.favouritesbtn);
        contactbtn=findViewById(R.id.contactbtn);
        fixmanagebtn=findViewById(R.id.fixmanagebtn);
        searchView.clearFocus();
        contact_list = new ArrayList<>();
        filteredList = new ArrayList<>(contact_list);
        favouritesList = new ArrayList<>(contact_list);

        contactbtn.setBackgroundResource(R.drawable.button_shadow);

        try {
            contact_list = SavingLoadingData.load_data(getApplicationContext());
            //Toast.makeText(getApplicationContext(),"loaded", LENGTH_SHORT).show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        list=contact_list;



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}
            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
            @SuppressLint("NotifyDataSetChanged")
            private void filterList(String newText) {
                filteredList.clear();
                for(Person person: contact_list){
                        if(person.getFname().toLowerCase().contains(newText.toLowerCase())){
                            filteredList.add(person);
                        }
                    }
                // Update the adapter with the filteredList
                adapter.setFilteredList(filteredList);
                adapter.notifyDataSetChanged();

                if (filteredList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE); // Hide the RecyclerView
                    Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView.setVisibility(View.VISIBLE); // Show the RecyclerView
                }
            }

        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent icreate =  new Intent(MainActivity.this, CreateContact.class);
                startActivity(icreate);
            }
        });

        contactbtn.setOnTouchListener((v, event) -> {
            isContactBtnClicked=true;
            isfavouritesBtnClicked=false;
            isFixManageBtnClicked=false;
            addbtn.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
            contactbtn.setImageResource(R.drawable.baseline_person_24);
            contactbtn.setBackgroundResource(R.drawable.button_shadow);
            favouritesbtn.setImageResource(R.drawable.baseline_star_outline_24);
            favouritesbtn.setBackground(null);
            fixmanagebtn.setBackground(null);
            if (contact_list == null || contact_list.isEmpty()) {
//                Toast.makeText(getApplicationContext(), "No Contacts found", LENGTH_SHORT).show();
            } else {
                adapter = new ContactAdapter(MainActivity.this, contact_list);
                recyclerView.setAdapter(adapter);
            }
            adapter.setContactList(contact_list);
            adapter.notifyDataSetChanged();
            return true;
        });

        favouritesbtn.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint({"ClickableViewAccessibility", "NotifyDataSetChanged"})
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isfavouritesBtnClicked=true;
                isContactBtnClicked=false;
                isFixManageBtnClicked=false;
                searchView.setVisibility(View.VISIBLE);
                contactbtn.setImageResource(R.drawable.baseline_person_outline_24);
                favouritesbtn.setImageResource(R.drawable.baseline_star_24);
                favouritesbtn.setBackgroundResource(R.drawable.button_shadow);
                contactbtn.setBackground(null);
                fixmanagebtn.setBackground(null);
                favouritesList.clear();
                for(Person person :contact_list)
                {
                    if(person.isFavourite())
                    {
                        favouritesList.add(person);
                    }
                }
                adapter.setFavouritesList(favouritesList);
                adapter.notifyDataSetChanged();
                if (favouritesList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE); // Hide the RecyclerView
//                    Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView.setVisibility(View.VISIBLE); // Show the RecyclerView
                }
                return true;
            }
        });

        fixmanagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addbtn.setVisibility(View.VISIBLE);
                isFixManageBtnClicked=true;
                isContactBtnClicked=false;
                isfavouritesBtnClicked=false;
                contactbtn.setImageResource(R.drawable.baseline_person_outline_24);
                favouritesbtn.setImageResource(R.drawable.baseline_star_outline_24);
                fixmanagebtn.setBackgroundResource(R.drawable.button_shadow);
                contactbtn.setBackground(null);
                favouritesbtn.setBackground(null);
                searchView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            }
        });

        recyclerView = findViewById(R.id.Contactview);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    }
    public void onItemClick(Person person) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("person", person);
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        try {
            contact_list = SavingLoadingData.load_data(getApplicationContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (contact_list.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            favouritesList.clear();
            for (Person person : contact_list) {
                if (person.isFavourite()) {
                    favouritesList.add(person);
                }
            }
            if(isContactBtnClicked){
                contactbtn.performClick();
                list=contact_list;}
                else if(isfavouritesBtnClicked){
                    favouritesbtn.performClick();
                list=favouritesList;}
                else if(isFixManageBtnClicked){fixmanagebtn.performClick();}
                adapter=new ContactAdapter(MainActivity.this,list);
                recyclerView.setAdapter(adapter);
            }


    }
}