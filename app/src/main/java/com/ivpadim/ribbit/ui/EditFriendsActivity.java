package com.ivpadim.ribbit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.ivpadim.ribbit.adapters.UserAdapter;
import com.ivpadim.ribbit.utils.ParseConstants;
import com.ivpadim.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends Activity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_grid);

        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mGridView.setEmptyView(emptyTextView);

        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if(e==null){
                    mUsers = parseUsers;
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this,
                                mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mUsers);
                    }

                    addCheckMarks();

                }
                else{
                    Log.e(TAG, e.getMessage());
                    showError(e.getMessage());
                }
            }
        });
    }

    private void addCheckMarks() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e==null){
                    for(int i = 0; i < mUsers.size(); i++){
                        ParseUser user = mUsers.get(i);
                        for(ParseUser friend : parseUsers){
                            if(friend.getObjectId().equals(user.getObjectId())){
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }
                }
                else{
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(R.string.error_title)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //super.onListItemClick(l, v, position, id);
        if(mGridView.isItemChecked(position)){
            mFriendsRelation.add(mUsers.get(position));
        }
        else {
            mFriendsRelation.remove(mUsers.get(position));
        }

        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.e(TAG, e.getMessage());
                }
            }
        });


    }
}
