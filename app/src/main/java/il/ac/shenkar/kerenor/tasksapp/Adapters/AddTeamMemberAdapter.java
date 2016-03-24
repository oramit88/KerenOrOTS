package il.ac.shenkar.kerenor.tasksapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import il.ac.shenkar.kerenor.tasksapp.DataAccess.TeamMember;
import il.ac.shenkar.kerenor.tasksapp.R;
import java.util.List;

/**
 * AddTeamMemberAdapter.java - a class that control the RecyclerView of the team members
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class AddTeamMemberAdapter extends RecyclerView.Adapter<AddTeamMemberAdapter.ViewHolder> {
    private List<TeamMember> membersList;
    private Context context;


    public AddTeamMemberAdapter(Context context, List<TeamMember> membersList) {
        this.membersList = membersList;
        this.context = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView create_team_email_field;
        TextView create_team_phone_field;
        TextView create_team_email_text;
        TextView create_team_phone_text;
        ImageView manage_team_member_image;


        public ViewHolder(View parentView) {
            super(parentView);
            create_team_email_field = (TextView)parentView.findViewById(R.id.create_team_email_field);
            create_team_phone_field = (TextView)parentView.findViewById(R.id.create_team_phone_field);
            create_team_email_text = (TextView)parentView.findViewById(R.id.create_team_email_text);
            create_team_phone_text = (TextView)parentView.findViewById(R.id.create_team_phone_text);
            manage_team_member_image = (ImageView)parentView.findViewById(R.id.manage_team_member_image);
        }


    }


    @Override
    public AddTeamMemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_member, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(AddTeamMemberAdapter.ViewHolder holder, int position) {
        final TeamMember teamMember = membersList.get(position);

        if (!teamMember.isRegistered()) { // display trash icon
            holder.manage_team_member_image.setImageResource(R.drawable.black_white_android_delete);
            holder.manage_team_member_image.setScaleX(0.6f);
            holder.manage_team_member_image.setScaleY(0.6f);
        } else {
            holder.manage_team_member_image.setImageResource(R.mipmap.ic_user_icon);
        }
        holder.create_team_email_field.setText(teamMember.getEMail());
        holder.create_team_phone_field.setText(teamMember.getPhoneNumber());
    }


    @Override
    public int getItemCount() {
        return membersList.size();
    }


    public List<TeamMember> getMembersList() {
        return membersList;
    }


    public void addMemberToList (String userEmail, String userPhone, boolean ifRegister)
    {
        // check that the eMail doesn't already exist on the list
        if (this.membersList.size() > 0) {
            if (!checkIfEmailExist(userEmail)) {
                Log.i("memberAdapter", "member added to list");
                TeamMember teamMember = new TeamMember(userEmail, userPhone, ifRegister);
                this.membersList.add(teamMember);
            }
        }
        else {
            TeamMember teamMember = new TeamMember(userEmail, userPhone, ifRegister);
            this.membersList.add(teamMember);
        }
    }


    public void removeMemberFromList (int position)
    {
        this.membersList.remove(position);
    }



    public boolean checkIfEmailExist(String userEmail)
    {
        for (int i = 0; i < this.membersList.size(); i++) {
            if (userEmail.equals(this.membersList.get(i).getEMail())) {
                Toast.makeText(context, "eMail already exist", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }












}
















