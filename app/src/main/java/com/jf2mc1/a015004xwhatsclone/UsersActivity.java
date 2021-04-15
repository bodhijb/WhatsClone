package com.jf2mc1.a015004xwhatsclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener {

    private ListView listViewUsers;
    ArrayList<String> mArrayListUsers;
    private ArrayAdapter mArrayAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        setTitle("WhatsApp Users!");

        swipeRefreshLayout = findViewById(R.id.ua_swipelayout);
        listViewUsers = findViewById(R.id.lv_users);
        mArrayListUsers = new ArrayList<>();
        mArrayAdapter = new ArrayAdapter(UsersActivity.this,
                android.R.layout.simple_list_item_1, mArrayListUsers);

        listViewUsers.setOnItemClickListener(UsersActivity.this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                updateUsers();
               // swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        FancyToast.makeText(UsersActivity.this,
                "Welcome " + ParseUser.getCurrentUser().getUsername(),
                FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();

        showWhatsAppUsers();

    }

    private void updateUsers() {
        try {

            ParseQuery<ParseUser> parseUserParseQuery = ParseUser.getQuery();
            if (ParseUser.getCurrentUser() != null) {
                parseUserParseQuery.whereNotEqualTo("username",
                        ParseUser.getCurrentUser().getUsername());

//                exclude users already in the arraylist & listview, only new users
                parseUserParseQuery.whereNotContainedIn("username", mArrayListUsers);

                parseUserParseQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> users, ParseException e) {

                        if (users.size() > 0) {
                            if (e == null) {
                                for (ParseUser whatAppUser : users) {
                                    // add to existing arraylist names
                                    mArrayListUsers.add(whatAppUser.getUsername());

                                }
                                // same as resetting the listview adapter
                                mArrayAdapter.notifyDataSetChanged();
                                // stop refreshing
                                if(swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }

                        } else {
                            // the list returned is empty, so... cancel refreshing
                            if(swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }

                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }

    private void showWhatsAppUsers() {

        try {

            ParseQuery<ParseUser> parseUserParseQuery = ParseUser.getQuery();
            if (ParseUser.getCurrentUser() != null) {
                parseUserParseQuery.whereNotEqualTo("username",
                        ParseUser.getCurrentUser().getUsername());

                parseUserParseQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> users, ParseException e) {
                        if (users.size() > 0 && e == null) {

                            for (ParseUser whatAppUser : users) {
                                mArrayListUsers.add(whatAppUser.getUsername());

                            }
                            listViewUsers.setAdapter(mArrayAdapter);

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

                FancyToast.makeText(UsersActivity.this,
                        ParseUser.getCurrentUser().getUsername() +
                                " thanks, bye :)",
                        FancyToast.LENGTH_LONG, FancyToast.DEFAULT, true).show();

                if (ParseUser.getCurrentUser().getUsername() != null) {

                    ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                startActivity(new Intent(UsersActivity.this,
                                        MainActivity.class));
                                finish();
                            }
                        }
                    });
                } else {
                    FancyToast.makeText(UsersActivity.this,
                            ParseUser.getCurrentUser().getUsername() +
                                    " is null",
                            FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                }
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // go to the chat screen with the message directed at the item clicked on

        Intent intentToChatActivity = new Intent(UsersActivity.this, ChatActivity.class);
        intentToChatActivity.putExtra("waTargetRecipient", mArrayListUsers.get(position));
        startActivity(intentToChatActivity);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UsersActivity.this, MainActivity.class));
        finish();

    }


}