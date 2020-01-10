package teacherkothai.example.com.teacherkothai;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chattoolbar;

    private TextView chatBar_Name;
    private TextView chatBar_lastseen;
    private CircleImageView chatBar_Image;

    private EditText chatMessageET;
    private ImageButton chatSendMessageBtn;
    private String message = "";

    private RecyclerView chatMessageRecycler;
    private LinearLayoutManager messagesLinearLayout;
    private final List<ChatDataProvider> messagesList = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String currentID;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        currentID = firebaseAuth.getCurrentUser().getUid();
        userID = getIntent().getStringExtra("user_id");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        chattoolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(chattoolbar);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("User Login");
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_bar, null);
        actionBar.setCustomView(action_bar_view);

        chatBar_Name = (TextView) findViewById(R.id.chatbar_name);
        chatBar_Name.setText(getIntent().getStringExtra("user_name"));
        chatBar_lastseen = (TextView) findViewById(R.id.chatbar_lastseen);
        chatBar_Image = (CircleImageView) findViewById(R.id.chatbar_image);

        chatMessageET = (EditText) findViewById(R.id.chat_message);
        chatSendMessageBtn = (ImageButton) findViewById(R.id.chat_send);

        chatMessageRecycler = (RecyclerView) findViewById(R.id.chat_recycler);
        messagesLinearLayout = new LinearLayoutManager(this);
        chatMessageRecycler.setLayoutManager(messagesLinearLayout);
        chatAdapter = new ChatAdapter(messagesList);
        chatMessageRecycler.setAdapter(chatAdapter);

        loadMessages();

        chatSendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void loadMessages() {
        DatabaseReference messageReference = databaseReference.child("messages").child(currentID).child(userID);
        messageReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatDataProvider chatData = dataSnapshot.getValue(ChatDataProvider.class);
                messagesList.add(chatData);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        message = chatMessageET.getText().toString();
        String messageTime = DateFormat.getDateTimeInstance().format(new Date());
        if(!TextUtils.isEmpty(message)){

            DatabaseReference pushRef = databaseReference.child("messages").child(currentID).child(userID).push();
            String pushID  = pushRef.getKey();

            String currentRef = "messages/" + currentID + "/" + userID;
            String userRef = "messages/" + userID + "/" + currentID;

            Map messageMap = new HashMap<>();
            messageMap.put(currentRef + "/" + pushID + "/" + "message", message);
            messageMap.put(currentRef + "/" + pushID + "/" + "type", "sent");
            messageMap.put(currentRef + "/" + pushID + "/" + "time", messageTime);

            messageMap.put(userRef + "/" + pushID + "/" + "message", message);
            messageMap.put(userRef + "/" + pushID + "/" + "type", "recieved");
            messageMap.put(userRef + "/" + pushID + "/" + "time", messageTime);

            databaseReference.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Toast.makeText(ChatActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                    }
                    else{
                        chatMessageET.setText("");
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        }
        return super.onOptionsItemSelected(item);
    }
}

