package com.jf2mc1.a015004xwhatsclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView listViewChats;
    private EditText editTextChats;
    private Button btnSend;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<String> chatsList;
    private String targetRecipient = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listViewChats = findViewById(R.id.ca_listview);
        editTextChats = findViewById(R.id.ca_et_message);
        btnSend = findViewById(R.id.ca_btn_send);
        chatsList = new ArrayList<>();

        mArrayAdapter = new ArrayAdapter(ChatActivity.this,
                android.R.layout.simple_list_item_1, chatsList);

        Intent getRecipient = getIntent();
        Bundle bundle = getRecipient.getExtras();
        if (bundle != null) {
            targetRecipient = (String) bundle.get("waTargetRecipient");
        }

        setTitle(ParseUser.getCurrentUser().getUsername().toUpperCase() +"'s chats with " + targetRecipient.toUpperCase());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAChat();
            }
        });

        showChatsWithOtherUser();

    }

    private void sendAChat() {

        if (editTextChats.getText() != null) {
            String currentUser = ParseUser.getCurrentUser().getUsername();
            String toastMessage = editTextChats.getText().toString();

            try {
                // save to back4app
                ParseObject myMessage = new ParseObject("Chat");
                //sender
                myMessage.put("waSender", ParseUser.getCurrentUser().getUsername());
                //recipient
                myMessage.put("waTargetRecipient", targetRecipient);
                //message
                myMessage.put("waMessage", toastMessage);

                final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                myMessage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            FancyToast.makeText(ChatActivity.this,
                                    currentUser + "'s message (" + toastMessage + ") is saved!!",
                                    FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                        } else {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                        showChatsWithOtherUser();
                        editTextChats.setText("");
                    }

                });
            } catch (Exception ex) {
                ex.printStackTrace();
                ex.getMessage();
            }


        } else {
            FancyToast.makeText(ChatActivity.this,
                    "Add some text..",
                    FancyToast.LENGTH_LONG, FancyToast.DEFAULT, true).show();
        }


    }


    private void showChatsWithOtherUser() {
        // parse query for all messages where recipient == tr && sender == current user
        // add it to the arraylist
        try {


            if (ParseUser.getCurrentUser() != null) {

                ParseQuery<ParseObject> parseQueryTo = ParseQuery.getQuery("Chat");
                parseQueryTo.whereEqualTo("waSender", ParseUser.getCurrentUser().getUsername());
                parseQueryTo.whereEqualTo("waTargetRecipient", targetRecipient);

                ParseQuery<ParseObject> parseQueryFrom = ParseQuery.getQuery("Chat");
                parseQueryFrom.whereEqualTo("waSender", targetRecipient);
                parseQueryFrom.whereEqualTo("waTargetRecipient", ParseUser.getCurrentUser().getUsername());

                List<ParseQuery<ParseObject>> compoundQuery = new ArrayList<ParseQuery<ParseObject>>();
                compoundQuery.add(parseQueryTo);
                compoundQuery.add(parseQueryFrom);

                ParseQuery<ParseObject> mainQuery = ParseQuery.or(compoundQuery);
                mainQuery.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> results, ParseException e) {
                        // results has the list of players that win a lot or haven't won much.
                    }
                });

                chatsList.clear();

                mainQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> messagesList, ParseException e) {
                        if(messagesList.size() > 0) {
                            if(e == null) {

                                for(ParseObject messageObject: messagesList) {
                                    chatsList.add(messageObject.get("waSender") + ": " + messageObject.get("waMessage"));
                                }
                                listViewChats.setAdapter(mArrayAdapter);
                            }
                        } else {
                            FancyToast.makeText(ChatActivity.this,
                                    "No messages yet..",
                                    FancyToast.LENGTH_LONG, FancyToast.DEFAULT, true).show();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_logout_user:

                FancyToast.makeText(ChatActivity.this,
                        ParseUser.getCurrentUser().getUsername() +
                                " thanks, bye :)",
                        FancyToast.LENGTH_LONG, FancyToast.DEFAULT, true).show();

                if (ParseUser.getCurrentUser().getUsername() != null) {

                    ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                startActivity(new Intent(ChatActivity.this,
                                        MainActivity.class));
                                finish();
                            }
                        }
                    });
                } else {
                    FancyToast.makeText(ChatActivity.this,
                            ParseUser.getCurrentUser().getUsername() +
                                    " is null",
                            FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                }
                break;
            case R.id.menu_item_refresh:
                showChatsWithOtherUser();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChatActivity.this, MainActivity.class));
        finish();

    }
}