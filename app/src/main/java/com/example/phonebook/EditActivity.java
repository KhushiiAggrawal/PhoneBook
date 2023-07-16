package com.example.phonebook;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {
    private static final int EDIT_REQUEST_CODE = 1;
    Uri photoUri = Uri.parse(""),savedImageUri;
    String savedImagePath;
    Button submitbtn;
    ArrayList<Person> contact_list;
    ContactAdapter adapter;
    Toolbar mActionBarToolbar;
    EditText firstname,lastname,company,phoneno,email;
    TextView dobs;
    ImageView imagebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);

        mActionBarToolbar =findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Edit contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firstname= findViewById(R.id.firstname);
        lastname=findViewById(R.id.lastname);
        company=findViewById(R.id.company);
        phoneno=findViewById(R.id.phoneno);
        email=findViewById(R.id.email);
        dobs=findViewById(R.id.dob);
        submitbtn=findViewById(R.id.submitbtn);
        imagebtn=findViewById(R.id.imgebtn);
        imagebtn.setClipToOutline(true);
        imagebtn.setPadding(0,0,0,0);

        firstname.setText(getIntent().getExtras().getString("fname"));
        lastname.setText(getIntent().getExtras().getString("lname"));
        company.setText(getIntent().getExtras().getString("cname"));
        phoneno.setText(getIntent().getExtras().getString("pnumber"));
        email.setText(getIntent().getExtras().getString("email"));
        dobs.setText(getIntent().getExtras().getString("dob"));
        imagebtn.setImageURI(Uri.parse(getIntent().getExtras().getString("uri")));
        int position= getIntent().getIntExtra("position",-1);

        try {
            contact_list = SavingLoadingData.load_data(getApplicationContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentimg = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intentimg);
            }});

        adapter = new ContactAdapter(EditActivity.this, contact_list);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name1=getIntent().getExtras().getString("fname");
                String name2=getIntent().getExtras().getString("lname");
                String companyname=getIntent().getExtras().getString("cname");
                String phonenumber=getIntent().getExtras().getString("pnumber");
                String emailiid=getIntent().getExtras().getString("emailid");
                String dob=getIntent().getExtras().getString("dobs");
                String uri=getIntent().getExtras().getString("uri");

                name1= firstname.getText().toString();
                name2= lastname.getText().toString();
                companyname= company.getText().toString();
                phonenumber= phoneno.getText().toString();
                emailiid= email.getText().toString();
                dob= dobs.getText().toString();


                if (!photoUri.toString().isEmpty()) {
                    savedImagePath = saveImageToInternalStorage(photoUri, firstname.getText().toString());
                    savedImageUri = getSavedImageUri(savedImagePath);
                    imagebtn.setPadding(0,0,0,0);
                    uri = savedImageUri.toString();
                }
                Person person= new Person(position,name1,name2,companyname,phonenumber,emailiid,dob,uri);
                contact_list.set(position,person);
                try {
                    SavingLoadingData.save_data(getApplicationContext(),contact_list);
                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position);

                    //sending back the updated data
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updated_fname", name1);
                    resultIntent.putExtra("updated_lname", name2);
                    resultIntent.putExtra("updated_cname", companyname);
                    resultIntent.putExtra("updated_pnumber", phonenumber);
                    resultIntent.putExtra("updated_emailid", emailiid);
                    resultIntent.putExtra("updated_dobs", dob);
                    resultIntent.putExtra("updated_uri", uri);

                    setResult(RESULT_OK, resultIntent);
                    finish();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK
                        && result.getData() != null) {
                    photoUri = result.getData().getData();
                    imagebtn.setImageURI(photoUri);
                }
            });
    private String saveImageToInternalStorage(Uri imageUri, String imageName) {
        String filePath = null;
        try {
            // Open an input stream from the image URI
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            // Create the directory path within internal storage
            File directory = new File(getFilesDir(), "MyImages");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            // Create a file in the custom directory with the person's name as the file name
            File file = new File(directory, imageName + ".jpg");

            // Create an output stream to write the image data to the file
            OutputStream outputStream = new FileOutputStream(file);

            // Copy the image data from the input stream to the output stream
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
            // Show a toast message indicating the error in saving the image
            Toast.makeText(EditActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }return filePath;}
    private Uri getSavedImageUri(String savedImagePath) {
        File savedImageFile = new File(savedImagePath);
        return Uri.fromFile(savedImageFile);}

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}