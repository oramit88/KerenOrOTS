package il.ac.shenkar.kerenor.tasksapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import il.ac.shenkar.kerenor.tasksapp.Adapters.AddTeamMemberAdapter;
import il.ac.shenkar.kerenor.tasksapp.Controllers.ParseController;
import il.ac.shenkar.kerenor.tasksapp.DataAccess.TeamMember;
import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import il.ac.shenkar.kerenor.tasksapp.Listeners.RecyclerItemClickListener;
import il.ac.shenkar.kerenor.tasksapp.R;
import il.ac.shenkar.kerenor.tasksapp.Utils.AnalyticsApplication;
import il.ac.shenkar.kerenor.tasksapp.Utils.Utils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateEditTeamActivity.java - a class that let the manager create or edit the team
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class CreateEditTeamActivity extends AppCompatActivity {
    private RecyclerView membersRecyclerView;
    private AddTeamMemberAdapter membersAdapter;
    private RecyclerView.LayoutManager membersLayoutManager;
    private ParseController parseController;

    EditText userEmail;
    EditText userPhone;
    EditText teamName;
    List<TeamMember> membersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_team);

        /* google analytics code */
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Create / Edit team screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_general);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.create_task_team);

        /* change the headline font */
        Typeface chalkFont = Typeface.createFromAsset(this.getAssets(), "fonts/DK Cool Crayon.ttf");
        toolbar_title.setTypeface(chalkFont);

        /* on back pressed */
        ImageButton backImageButton = (ImageButton)findViewById(R.id.toolbar_back_button);
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TabLayoutActivity.class);
                startActivity(intent);
                finish();
            }
        });

        membersList = new ArrayList<>();

        membersRecyclerView = (RecyclerView) findViewById(R.id.team_members_container);
        membersRecyclerView.setHasFixedSize(true);
        membersLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        /* LinearLayoutManager before setAdapter */
        membersRecyclerView.setLayoutManager(membersLayoutManager);
        membersAdapter = new AddTeamMemberAdapter(this, membersList);
        membersRecyclerView.setAdapter(membersAdapter);
        parseController = new ParseController(getApplicationContext());

        userEmail = (EditText) findViewById(R.id.create_team_email_field);
        userPhone = (EditText) findViewById(R.id.create_team_phone_field);
        teamName = (EditText) findViewById(R.id.team_name_field);

        Intent intentTaskDetails = getIntent();
        int requestCode = intentTaskDetails.getExtras().getInt("requestCode");

        if (requestCode == Constants.UPDATE_TEAM) {
            teamName.setText(parseController.getTeamName());
            teamName.setEnabled(false);
            /* can't update the team's name because parse don't allow the manager to change employees details */

            /* get team members from parse */
            parseController.getMembersFromParse(parseController.getTeamName(), new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        for (ParseUser obj : objects) {
                            membersAdapter.addMemberToList(obj.get("email").toString(), obj.get("phone").toString(), true);
                            membersAdapter.notifyItemInserted(membersAdapter.getItemCount());
                            membersRecyclerView.scrollToPosition(membersAdapter.getItemCount() - 1);
                        }
                    } else {
                        Log.e("create team activity", "wasn't found: " + e.getMessage());
                    }
                }
            });
        }

        /* when we click "add member" floating button, we will add this member to the temp list of team members */
        FloatingActionButton addMemberButton = (FloatingActionButton)this.findViewById(R.id.create_team_add_member_button);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email_str = userEmail.getText().toString();
                String user_phone_str = userPhone.getText().toString();
                userEmail.setText("");
                userPhone.setText("");

                if ((!user_email_str.equals("")) && (!user_phone_str.equals("")) &&
                        (Utils.isValidEmail(user_email_str))) { // if not null and eMail valid
                    membersAdapter.addMemberToList(user_email_str, user_phone_str, false);
                    membersAdapter.notifyItemInserted(membersAdapter.getItemCount());
                    membersRecyclerView.scrollToPosition(membersAdapter.getItemCount() - 1);
                    // add that email at the end of the list
                } else {
                    Toast.makeText(getApplicationContext(),
                            "eMail is not valid. Please try again", Toast.LENGTH_SHORT).show();
                }

                /* to hide the keyboard after clickling add member */
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        /* when we click "done" floating button, we will add those members to the list of team members
        * and open the email app to send them 'google play' invitations */
        FloatingActionButton addMembersToTeamButton =
                (FloatingActionButton)this.findViewById(R.id.add_members_list_done_button);
        addMembersToTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText teamName = (EditText) findViewById(R.id.team_name_field);
                String newTeamNameStr = teamName.getText().toString();

                if (newTeamNameStr.equals("")) { // check here if team name is null
                    Toast.makeText(getApplicationContext(), "Team name is null. Please enter a valid name.", Toast.LENGTH_SHORT).show();
                } else {
                    parseController.setManagerTeamName(newTeamNameStr); /* AND activate account */
                    List<TeamMember> tempTeamMembersList = membersAdapter.getMembersList(); // new members
                    List<TeamMember> teamMembersList = new ArrayList<>(); /* already existing team members */

                    for (TeamMember member : tempTeamMembersList) {
                        if (!member.isRegistered())
                            teamMembersList.add(member);
                    }

                    String[] emailsArray = new String[teamMembersList.size()];
                    String[] phonesArray = new String[teamMembersList.size()];

                    /* take an array of eMails and an array of phones from the adapter */
                    for (int i = 0; i < teamMembersList.size(); i++) {
                        emailsArray[i] = teamMembersList.get(i).getEMail();
                        phonesArray[i] = teamMembersList.get(i).getPhoneNumber();
                    }

                    /* take the manager details from SharedPreferences so we can log his/her in again */
                    SharedPreferences sharedpreferences;
                    sharedpreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
                    final String current_username = sharedpreferences.getString("current_username", null);
                    final String current_password = sharedpreferences.getString("current_password", null);

                    for (int i = 0; i < teamMembersList.size(); i++) {
                        parseController.signMemberToApp(emailsArray[i], phonesArray[i], phonesArray[i],
                            teamName.getText().toString(), new SignUpCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) { /* if user sign up ok */
                                        parseController.logInToApp(current_username, current_password, new LogInCallback() {
                                            @Override
                                            public void done(ParseUser user, ParseException e) {
                                            if (user != null) {
                                                Log.i("create team activity", "manager re-logged in");
                                            } else {
                                                Log.e("create team activity", "manager re-login failed: " + e.getMessage());
                                            }
                                            }
                                        });
                                    } else {
                                        Log.e("create team", "create member error: " + e.getMessage());
                                    }
                                }
                            });
                    }
                    // Log.i("team", "connected as: " + ParseUser.getCurrentUser().getUsername());

                    if (teamMembersList.size() > 0) {
                        sendEmail(emailsArray);
                        Toast.makeText(getApplicationContext(), "users added to parse DB", Toast.LENGTH_SHORT).show();

                    } else {
                        Intent loginIntent = new Intent (getApplicationContext(), TabLayoutActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
            }
        }
    });

        /* when we click a temp team member we can remove him/her from the temp members list */
        membersRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
            new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (!membersAdapter.getMembersList().get(position).isRegistered()) {
                        membersAdapter.removeMemberFromList(position);
                        membersAdapter.notifyItemRemoved(position);
                    } else {
                        String toast = "User already exist, can not be removed from the list";
                        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        ));
    }



    /* send invitation the new team members - open a mail app */
    public void sendEmail(String[] emailsArray)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, emailsArray);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Invitation to join KerenOr OTS team");
        intent.putExtra(Intent.EXTRA_TEXT, "Hello, \n\n " +
                "You have been invited to be a team member in KerenOrOTS team created by me. \n\n" +
                "Use this link to download and install the App from Google Play :  \n" +
                "https://play.google.com/store/apps/details?id=il.ac.shenkar.kerenor.tasksapp \n\n" +
                "Please use your e-mail address as your username and your phone number as your password.");

        startActivityForResult(Intent.createChooser(intent, ""), Constants.SEND_EMAIL);
    }


    /* hide the keyboard when user touch the screen */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }



    /* return to this function after manager send eMail invitation to members */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == Constants.SEND_EMAIL)
        {
            Intent loginIntent = new Intent (getApplicationContext(), TabLayoutActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }





}


















