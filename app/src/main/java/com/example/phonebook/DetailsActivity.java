package com.example.phonebook;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.InCallService;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
public class DetailsActivity extends AppCompatActivity {
    public static final int DELETE_MENU_ID = R.id.delete;
    private static final int EDIT_REQUEST_CODE = 1;
    TextView fname,lname,num1,num2,num3,num4;
    ImageButton editbtn,deletebtn,favouritesbtn;
    ImageView img,watext,call1,text1;
    int position=-1;
    Toolbar mActionBarToolbar;
    ArrayList<Person> contact_list,favouritesList;
    ContactAdapter adapter;
    boolean favourites;
    LinearLayout call,text,videocall,wamsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // finding the views
        fname=findViewById(R.id.fname);
        lname=findViewById(R.id.lname);
        editbtn=findViewById(R.id.editbtn);
        favouritesbtn=findViewById(R.id.favouritesbtn);
        num1=findViewById(R.id.num1);
        num2=findViewById(R.id.num2);
        num3=findViewById(R.id.num3);
        num4=findViewById(R.id.num4);
        img=findViewById(R.id.img);
        call=findViewById(R.id.call);
        text=findViewById(R.id.text);
        videocall=findViewById(R.id.videocall);
        mActionBarToolbar =findViewById(R.id.toolbar);
        call1=findViewById(R.id.call1);
        wamsg=findViewById(R.id.wamsg);
        text1=findViewById(R.id.text1);

        //setting toolbar
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set text to be displayed
        fname.setText(getIntent().getExtras().getString("fname"));
        lname.setText(getIntent().getExtras().getString("lname"));
        num1.setText(getIntent().getExtras().getString("pnumber"));
        num2.setText("Message +91" + (getIntent().getExtras().getString("pnumber")));
        num3.setText("Voice call +91" + (getIntent().getExtras().getString("pnumber")));
        num4.setText("Video call +91" + (getIntent().getExtras().getString("pnumber")));
        img.setImageURI(Uri.parse(getIntent().getExtras().getString("uri")));

        //fetching data from intent
        String cname =getIntent().getExtras().getString("cname");
        String pnumber =getIntent().getExtras().getString("pnumber");
        String email =getIntent().getExtras().getString("email");
        String dob =getIntent().getExtras().getString("dob");
        String uri= getIntent().getExtras().getString("uri");
//        position= getIntent().getIntExtra("position",-1);
        favourites= getIntent().getBooleanExtra("favourites",false);
        Person person= (Person) getIntent().getSerializableExtra("object");
        //loading data
        try {
            contact_list = SavingLoadingData.load_data(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
            contact_list = new ArrayList<>();
        }

        position= contact_list.indexOf(person);
        if(position!=-1){
//            Toast.makeText(getApplicationContext(),  String.valueOf(position),Toast.LENGTH_SHORT).show();
        }
        else{
//            Toast.makeText(getApplicationContext(),"position=-1",Toast.LENGTH_SHORT).show();
        }


        //setting favourite persons
        if(favourites || person.isFavourite()){
            favouritesbtn.setImageResource(R.drawable.baseline_star_24);
        }
        else {
            favouritesbtn.setImageResource(R.drawable.baseline_star_outline_24);
        }

        adapter = new ContactAdapter(DetailsActivity.this, contact_list);

        //setting edit button and moving to edit activity
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(DetailsActivity.this, EditActivity.class);
                intent.putExtra("fname",fname.getText().toString());
                intent.putExtra("lname",lname.getText().toString());
                intent.putExtra("position",position);
                intent.putExtra("cname",cname);
                intent.putExtra("pnumber",pnumber);
                intent.putExtra("email",email);
                intent.putExtra("dob",dob);
                intent.putExtra("uri",uri);
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });

        //setting favourites button
        favouritesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(favourites || person.isFavourite()){
                        favourites=false;
                        person.setFavourite(false);
                        favouritesbtn.setImageResource(R.drawable.baseline_star_outline_24);
                    }
                    else{
                        favourites=true;
                        person.setFavourite(true);
                        favouritesbtn.setImageResource(R.drawable.baseline_star_24);
                    }

                    contact_list.set(position,person);

                try {
                    SavingLoadingData.save_data(getApplicationContext(),contact_list);
//                    Toast.makeText(getApplicationContext(),"updated",Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //setting the call button
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent call_intent= new Intent(Intent.ACTION_DIAL);
                call_intent.setData(Uri.parse("tel:" + contact_list.get(position).pnumber));
                startActivity(call_intent);
            }
        });

        call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                String waNumber = contact_list.get(position).pnumber.replace("+", "");
                sendIntent.putExtra("jid", waNumber + "@s.whatsapp.net");
                startActivity(sendIntent);
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + contact_list.get(position).pnumber));
                startActivity(sendIntent);
            }
        });

        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + contact_list.get(position).pnumber));
                startActivity(sendIntent);
            }
        });

        wamsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri mUri = Uri.parse("sms:" +contact_list.get(position).pnumber);
                Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
                mIntent.setPackage("com.whatsapp");
                mIntent.putExtra("sms_body", "The text goes here");
                mIntent.putExtra("chat",true);
                startActivity(mIntent);
            }
        });

        videocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent videoCall = new Intent("com.android.phone.videocall");
                videoCall.putExtra("videoCall", true);
                videoCall.setData(Uri.parse("tel:" + contact_list.get(position).pnumber));
                startActivity(videoCall);
            }
        });

    }

    //after coming back from activity this method should work
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            String finame = data.getStringExtra("updated_fname");
            String laname = data.getStringExtra("updated_lname");
            String cname = data.getStringExtra("updated_cname");
            String emailid = data.getStringExtra("updated_emailid");
            String pnumber = data.getStringExtra("updated_pnumber");
            String uri = data.getStringExtra("updated_uri");
            String dob = data.getStringExtra("updated_dobs");
            Log.d("DetailsActivity", "Received updated data: " +
                    "FirstName=" + finame +
                    ", LastName=" + laname +
                    ", PhoneNumber=" + pnumber +
                    ", Uri=" + uri);
            Person person= new Person(contact_list.get(position).id,finame,laname,cname,pnumber,emailid,dob,uri);
            contact_list.set(position,person);
            try {
                SavingLoadingData.save_data(getApplicationContext(),contact_list);
//                    Toast.makeText(getApplicationContext(),"updated",Toast.LENGTH_SHORT).show();
                adapter.notifyItemChanged(position);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//          Update the displayed data with the updated values
            fname.setText(contact_list.get(position).fname);
            lname.setText(contact_list.get(position).lname);
            num1.setText(contact_list.get(position).pnumber);
            num2.setText(contact_list.get(position).pnumber);
            num3.setText(contact_list.get(position).pnumber);
            num4.setText(contact_list.get(position).pnumber);
            img.setImageURI(Uri.parse(contact_list.get(position).uri));
        }
    }


    //overflow menu in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        int id= item.getItemId();
        if (id == R.id.delete){
                AlertDialog.Builder builder= new AlertDialog.Builder(DetailsActivity.this, R.style.CustomAlertDialogTheme)
                        .setTitle("Delete Contact?")
                        .setMessage("This contact will be removed from all your synced devices.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                contact_list.remove(position);
                                adapter.notifyDataSetChanged();
                                // Save the updated data
                                try {
                                    SavingLoadingData.save_data(DetailsActivity.this, contact_list);
                                    Toast.makeText(DetailsActivity.this, "Contact removed", Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(DetailsActivity.this, "Failed to remove data", Toast.LENGTH_SHORT).show();
                                }

                                // Notify the adapter about the data change

                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();}
            else{
                return super.onOptionsItemSelected(item);
        }
        return true;
        }
}
