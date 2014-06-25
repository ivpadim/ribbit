package com.ivpadim.ribbit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivpadim.ribbit.R;
import com.ivpadim.ribbit.utils.MD5Util;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends ArrayAdapter<ParseUser> {
    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.user_item, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();

            holder.userIcon = (ImageView) convertView.findViewById(R.id.userIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.checkIcon = (ImageView) convertView.findViewById(R.id.checkIcon);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();


        if(email.equals("")){
            holder.userIcon.setImageResource(R.drawable.avatar_empty);
        }
        else{
            String hash = MD5Util.md5Hex(email);
            String gravatar = "http://www.gravatar.com/avatar/" +
                              hash + "?s=204&d=404";

            Picasso.with(mContext)
                   .load(gravatar)
                   .placeholder(R.drawable.avatar_empty)
                   .into(holder.userIcon);
        }

        holder.nameLabel.setText(user.getUsername());

        GridView gridView = (GridView)parent;
        if(gridView.isItemChecked(position)){
            holder.checkIcon.setVisibility(View.VISIBLE);
        }
        else{
            holder.checkIcon.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        public ImageView userIcon;
        public ImageView checkIcon;
        public TextView nameLabel;
    }

    public void refill(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}
