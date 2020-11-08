package uz.rusya.messagechat;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity {

    private ListView contactsList;
    private ArrayList<String> contactsFromDatabase;
    private ArrayAdapter<String>adapter;
    private DatabaseReference mDatabase;
    private String currentUserId;
    private ArrayList<String> someArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Intent intent = getIntent();
        currentUserId = intent.getStringExtra("currentUseId");
        contactsList = findViewById(R.id.contacts_list);
        contactsFromDatabase = new ArrayList<String>();


        mDatabase = FirebaseDatabase.getInstance().getReference("contacts").child(currentUserId);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                someArray = new ArrayList<String>();

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getValue(String.class);
                    someArray.add(String.valueOf(ds.getValue(String.class)));
                }
                setAdapter(someArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ContactsActivity.this,"Some error occured",Toast.LENGTH_SHORT).show();
            }
        });

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(ContactsActivity.this, DialogActivity.class);
                intent.putExtra("contactEmail", someArray.get(i));
                startActivity(intent);
            }
        });

    }

    public void setAdapter(ArrayList<String> array)
    {

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,array);
        contactsList.setAdapter(adapter);
    }






}
