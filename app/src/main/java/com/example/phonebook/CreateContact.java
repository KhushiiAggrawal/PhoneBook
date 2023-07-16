package com.example.phonebook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;
public class CreateContact extends AppCompatActivity {
    private static final int EDIT_REQUEST_CODE = 1;
    Uri photoUri = Uri.parse(""),savedImageUri;
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> launcher;
    EditText firstname,lastname,company,phoneno,email;
    TextView dob;
    Toolbar mActionBarToolbar;
    String savedImagePath="";
    Button submitbtn,imagebtn2;
    ImageView imagebtn;
    ArrayList<Person> contact_list;
    ContactAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);


        mActionBarToolbar =findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Create contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable dr = getResources().getDrawable(R.drawable.baseline_error_24);
        firstname= findViewById(R.id.firstname);
        lastname=findViewById(R.id.lastname);
        company=findViewById(R.id.company);
        phoneno=findViewById(R.id.phoneno);
        email=findViewById(R.id.email);
        dob=findViewById(R.id.dob);
        submitbtn=findViewById(R.id.submitbtn);
        imagebtn=findViewById(R.id.imgebtn);
        imagebtn2=findViewById(R.id.imgebtn2);
        imagebtn.setClipToOutline(true);

        try {
            contact_list = SavingLoadingData.load_data(getApplicationContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateContact.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                                dob.setText(selectedDate);
                            }
                        }, year, month, dayOfMonth);

                // Show the date picker dialog
                datePickerDialog.show();
            }
        });
        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                cameraLauncher.launch(cameraIntent);
                Intent intent = new Intent(CreateContact.this,ImageSelection.class);
                startActivityForResult(intent,EDIT_REQUEST_CODE);
            }});

        imagebtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intentimg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                launcher.launch(intentimg);
                Intent intent = new Intent(CreateContact.this,ImageSelection.class);
                startActivityForResult(intent,EDIT_REQUEST_CODE);
            }});

        phoneno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneno.setError(null);
                dr.setBounds(0, 0, 0, 0);
                phoneno.setCompoundDrawables(null,null,null,null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter.LengthFilter(10);
                phoneno.setFilters(filters);
                if(!isValidMobile(s.toString())){
                  //  phoneno.setError("10 digits");
                    //dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
//                    phoneno.setCompoundDrawables(null,null,dr,null);
                  }
                else{

                    phoneno.setError(null);
                    dr.setBounds(0, 0, 0, 0);
                    phoneno.setCompoundDrawables(null,null,null,null);
                    }

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!isValidEmail(s.toString())){
                    email.setError("enter valid email");
                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                    email.setCompoundDrawables(null,null,dr,null);}
                else{
                    email.setError(null);
                    dr.setBounds(0, 0, 0, 0);
                }
            }
        });

        firstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    firstname.setCompoundDrawables(null,null,null,null);}
            }
        });

        lastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    lastname.setCompoundDrawables(null,null,null,null);}
            }
        });

        company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()){
                    company.setCompoundDrawables(null,null,null,null);}
            }
        });

        adapter = new ContactAdapter(CreateContact.this, contact_list);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fname="",lname="",cname="",pnumber="",emailid="",dobs="",uri="";
                if(firstname.getText().toString().equals("")){
                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                    firstname.setCompoundDrawables(null,null,dr,null);
                    Toast.makeText(getApplicationContext(),"required",Toast.LENGTH_SHORT).show();
                }

                if(phoneno.getText().toString().equals("")){
                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                    phoneno.setCompoundDrawables(null,null,dr,null);
                    Toast.makeText(getApplicationContext(),"required",Toast.LENGTH_SHORT).show();
                }

//                if(photoUri.toString().equals("")){
//                    Toast.makeText(getApplicationContext(),"image required",Toast.LENGTH_SHORT).show();
//                }
//                else {
//
//                        if(!isValidMobile(phoneno.getText().toString())){
//                            Toast.makeText(getApplicationContext(),"Enter valid phone no.",Toast.LENGTH_SHORT).show();
//                            phoneno.setError("",getResources().getDrawable(R.drawable.baseline_error_24));
//                        }
                    else
                    {
                            fname= firstname.getText().toString();
                            lname= lastname.getText().toString();
                            cname= company.getText().toString();
                            pnumber= phoneno.getText().toString();
                            emailid= email.getText().toString();
                            dobs= dob.getText().toString();

                        phoneno.setCompoundDrawables(null,null,null,null);
//                        if(!email.getText().toString().equals("")){
//                            if (!isValidEmail(email.getText().toString())) {
//                                dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
//                                email.setCompoundDrawables(null,null,dr,null);
//                                email.setText("");
////                            email.setError("invalid");
//                            }
//                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
//                    email.setCompoundDrawables(null,null,dr,null);
//                    Toast.makeText(getApplicationContext(),"required",Toast.LENGTH_SHORT).show();

                            // Assuming you have the saved image path or URI as savedImagePath
                            if (!photoUri.toString().isEmpty()) {
//                                Toast.makeText(getApplicationContext(),photoUri.toString(),Toast.LENGTH_SHORT).show();
//                                savedImagePath = saveImageToInternalStorage(photoUri, firstname.getText().toString());
//                                savedImageUri = getSavedImageUri(savedImagePath);
//                                uri = savedImageUri.toString();
                                uri = photoUri.toString();

//                                Toast.makeText(getApplicationContext(),"uri converted",Toast.LENGTH_SHORT).show();

                            }
                            else{
                                uri="android.resource://com.example.phonebook/drawable/baseline_person_24";
                            }

                            //now here we are saving data in shared preferences in a file named bmi history
                            contact_list.add(new Person(contact_list.size(),fname,lname,cname,pnumber,emailid,dobs,uri));

                            try {
                                SavingLoadingData.save_data(getApplicationContext(),contact_list);
//                                Toast.makeText(getApplicationContext(),"saved data",Toast.LENGTH_SHORT).show();
                                adapter.notifyItemInserted(contact_list.size()-1);
                                finish();
                            } catch (IOException e) {
//                                Toast.makeText(getApplicationContext(),"not saved data",Toast.LENGTH_SHORT).show();
                                throw new RuntimeException(e);
                            }
                        }
                }
        });

//        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//            @Override
//            public void onActivityResult(ActivityResult result) {
//                Bundle extras = result.getData().getExtras();
//                Bitmap imgbitmap = (Bitmap) extras.get("data");
//                photoUri = saveCameraImage(imgbitmap,CreateContact.this);
//                imagebtn.setImageURI(photoUri);
//                imagebtn.setPadding(0,0,0,0);
//            }
//        });
//
//        launcher= registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK
//                            && result.getData() != null) {
//                        photoUri = result.getData().getData();
//                        imagebtn.setImageURI(photoUri);
//                        imagebtn.setPadding(0,0,0,0);
//                    }
//                });
      }
//    private Uri saveCameraImage(Bitmap imgbitmap,Context context ){
//        File imagesFolder = new File(context.getCacheDir(),"images");
//        Uri uri = null;
//        try{
//            if (!imagesFolder.exists()) {
//                imagesFolder.mkdirs();
//            }
//            File file = new File(imagesFolder + "captured_image.jpg");
//            FileOutputStream outputStream = new FileOutputStream(file);
//            imgbitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream );
//            outputStream.flush();
//            outputStream.close();
//            uri = FileProvider.getUriForFile(context.getApplicationContext(),"com.example.phonebook"+ ".provider",file);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return uri;
//    }

    private String saveImageToInternalStorage (Uri imageUri, String imageName){
        String filePath = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File directory = new File(getFilesDir(), "MyImages");
            if (!directory.exists()) {
                directory.mkdirs();
            }
//            Toast.makeText(getApplicationContext(),"File created",Toast.LENGTH_SHORT).show();
            File file = new File(directory, imageName + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            filePath = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
//            Toast.makeText(CreateContact.this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
        return filePath;
    }
    private Uri getSavedImageUri (String savedImagePath){
        File savedImageFile = new File(savedImagePath);
        return Uri.fromFile(savedImageFile);
    }
    private boolean isValidMobile (String phone){
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 10 && phone.length() <= 12;
        }
        return false;
    }

    private boolean isValidEmail (String email){
        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        return Pattern.compile(EMAIL_STRING).matcher(email).matches();
    }
    public boolean onOptionsItemSelected (MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onPause () {
        super.onPause();
        firstname.setCompoundDrawables(null,null,null,null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            String uri = data.getStringExtra("photouri");
            photoUri= Uri.parse(uri);
            imagebtn.setImageURI(photoUri);
//            Toast.makeText(getApplicationContext(),"uri set",Toast.LENGTH_SHORT).show();
            imagebtn.setPadding(0,0,0,0);
        }
    }
}
