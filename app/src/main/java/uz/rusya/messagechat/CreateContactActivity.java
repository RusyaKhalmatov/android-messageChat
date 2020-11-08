package uz.rusya.messagechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;


public class CreateContactActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText emailContactField,nameContactField;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);

        emailContactField = findViewById(R.id.emailCreateField);
        nameContactField= findViewById(R.id.nameCreateField);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        currentUserId = intent.getStringExtra("currentUseId");



    }

    public void addContact(View view) {
        String emailContact = emailContactField.getText().toString();
        String nameContact = nameContactField.getText().toString();
        String idContact = mDatabase.push().getKey();
        Toast.makeText(CreateContactActivity.this,"Contact created",Toast.LENGTH_SHORT).show();
        Contact contact = new Contact(emailContact,nameContact);
        mDatabase.child("contacts").child(currentUserId).child(idContact).setValue(contact.getEmail());



    }



}
