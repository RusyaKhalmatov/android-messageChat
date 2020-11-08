package uz.rusya.messagechat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DialogActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendBut;
    private EditText textMessage;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private ArrayList<String> messages = new ArrayList<String>();
    private TextView testText;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private TextView result,result2;
    private ArrayList<String> IDs;
    private String lastID;
    String choosenContact;
    static final String FILENAME_MESSAGES = "MessageIDs";
    static final String FILENAME_PARTY = "PartyIDs";
    static final String FILENANE_TABLECHAT = "ChatsTable";
    final String LOG_TAG = "myLogs";
  static final String FILENAME_MESSAGESTATUS = "MessageStatusTable";
    private  String tableChatID;
    private String partyID;
    private Message message;
    private String chatID;
    private ArrayList<String> partyIdList = new ArrayList<String>();
    private ArrayList<String> messagesIdList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        Intent intent = getIntent();

        choosenContact = intent.getStringExtra("contactEmail");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        init();
        mAuth = FirebaseAuth.getInstance();
        result = findViewById(R.id.Result);
        result.setText(choosenContact);
        result2 = findViewById(R.id.Result2);
        currentUser = mAuth.getCurrentUser();


        final DataAdapter dataAdapter = new DataAdapter(this,messages);
        recyclerView.setAdapter(dataAdapter);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // readFileID();
               /*String key = String.valueOf(dataSnapshot.child("messages").child(lastID).getValue()); ///success
                result.setText(key);*/

              /*  Message message = dataSnapshot.child("messages").child(lastID).getValue(Message.class);
                String textShouldBeSet = message.authorID + ": \n" + message.getContent() + "\n" + message.getDateCreated();
                messages.add(textShouldBeSet);*/
                readFile(FILENAME_PARTY,partyIdList); //get all NODE's IDs of table party
                Boolean matched = false;
                Boolean chatMatched = false;
                for(int i= 0; i<partyIdList.size();i++)
                {
                    String opponent = String.valueOf(dataSnapshot.child("party").child(partyIdList.get(i)).child("user_id").getValue());
                    Log.d("PartyList",partyIdList.get(i));
                    if(opponent.equals(choosenContact))
                    {
                        matched = true;
                        partyID = partyIdList.get(i);
                        chatID = String.valueOf(dataSnapshot.child("party").child(partyIdList.get(i)).child("chat_id").getValue());
                        break;
                    }
                    else
                        matched = false;

                }

                if(matched)
                {
                    Toast.makeText(DialogActivity.this,"I found matching",Toast.LENGTH_SHORT).show();

                }else
                {
                    Toast.makeText(DialogActivity.this,"I have NOT found matching",Toast.LENGTH_SHORT).show();
                    partyID = mDatabase.push().getKey();
                    chatID = mDatabase.push().getKey();
                    Toast.makeText(DialogActivity.this,"Created new chat ID and party ID",Toast.LENGTH_SHORT).show();

                }

                readFile(FILENAME_MESSAGES,messagesIdList);

                for(int i =0;i<messagesIdList.size();i++)
                {
                    String chatId = String.valueOf(dataSnapshot.child("messages").child(messagesIdList.get(i)).child("chatID").getValue());

                    if(chatId.equals(chatID))
                    {
                        message = dataSnapshot.child("messages").child(messagesIdList.get(i)).getValue(Message.class);
                        String textShouldBeSet = message.authorID + ": \n" + message.getContent() + "\n" + message.getDateCreated();
                        messages.add(textShouldBeSet);
                    }

                }

                dataAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messages.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendBut.setOnClickListener(DialogActivity.this);
    }

    public void init()
    {

        sendBut = findViewById(R.id.send_message_b);
        textMessage = findViewById(R.id.message_input);
        recyclerView = findViewById(R.id.messages_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        IDs = new ArrayList<String>();
        tableChatID = mDatabase.push().getKey();
    }

    @Override
    public void onClick(View view) {

        String messageItself = textMessage.getText().toString();
        String messageID = mDatabase.push().getKey();
        String timeCreated = getCurrentTimeStamp();
        String messageStatusTable = mDatabase.push().getKey();
        Message message = new Message(currentUser.getEmail(),messageID,chatID,messageItself,timeCreated);
        Toast.makeText(this,"Message ID: " + messageID,Toast.LENGTH_SHORT).show();
        mDatabase.child("messages").child(messageID).setValue(message);
        mDatabase.child("party").child(partyID).child("chat_id").setValue(chatID);
        mDatabase.child("party").child(partyID).child("user_id").setValue(choosenContact);
        mDatabase.child("chat").child(tableChatID).child("chat_id").setValue(chatID);
        mDatabase.child("chat").child(tableChatID).child("chat_name").setValue(choosenContact);
        mDatabase.child("chat").child(tableChatID).child("user_name").setValue(currentUser.getEmail());
        mDatabase.child("message_status").child(messageStatusTable).child("is_read").setValue("false");
        mDatabase.child("message_status").child(messageStatusTable).child("message_id").setValue(messageID);
        mDatabase.child("message_status").child(messageStatusTable).child("user_id").setValue(currentUser.getUid());

        writeFile(messageID,FILENAME_MESSAGES);
        writeFile(tableChatID,FILENANE_TABLECHAT);
        writeFile(partyID,FILENAME_PARTY);
        writeFile(messageStatusTable,FILENAME_MESSAGESTATUS);



    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }


    void writeFile(String messageID, String FILENAME) {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_APPEND)));

            // пишем данные
            bw.write("\n" + messageID);
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void readFileID() {

        try {
            ArrayList<String> IDs = new ArrayList<String>();
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME_MESSAGES)));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(LOG_TAG, str);
                IDs.add(str);
            }
            lastID = IDs.get(IDs.size()-1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
