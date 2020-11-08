package uz.rusya.messagechat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Dialogs extends AppCompatActivity {

    private TextView text;
    private String currentUserId;
    private ListView dialogsList;
    private ArrayAdapter<String> listAdapter;
    private DatabaseReference mDatabase;
    private ArrayList<String> chatList;
    private ArrayList<String> chatIdList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);
        Intent intent = getIntent();
        currentUserId = intent.getStringExtra("currentUseId");
        dialogsList = findViewById(R.id.dialogs_list);
        text = findViewById(R.id.id_text);
        text.setText(currentUserId);
        chatList = new ArrayList<String>();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_addContact: //
                Intent intent = new Intent(Dialogs.this, CreateContactActivity.class);
                intent.putExtra("currentUseId", currentUserId);
                startActivity(intent);
                return true;
            case R.id.contactList:
                Intent intent2 = new Intent(Dialogs.this,ContactsActivity.class);
                intent2.putExtra("currentUseId", currentUserId);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }

    public void setAdapter(ArrayList<String> array)
    {

        /*adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,array);
        contactsList.setAdapter(adapter);*/
    }

    void readFile(String FILENAME, ArrayList<String> array) {

        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                array.add(str);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
