package com.ivpadim.ribbit.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ivpadim.ribbit.adapters.MessageAdapter;
import com.ivpadim.ribbit.utils.ParseConstants;
import com.ivpadim.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends ListFragment{

    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getMessages();
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_refresh_1,
                R.color.swipe_refresh_2,
                R.color.swipe_refresh_3,
                R.color.swipe_refresh_4);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessages();

    }

    private void getMessages() {
        getActivity().setProgressBarIndeterminate(true);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENTS_IDS, ParseUser.getCurrentUser().getObjectId());
        query.orderByDescending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                getActivity().setProgressBarIndeterminate(false);
                if(e==null){
                    mMessages = messages;
                    if(getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(),
                                mMessages);
                        setListAdapter(adapter);
                    }
                    else{
                        ((MessageAdapter) getListView().getAdapter()).refill(mMessages);
                    }
                }

            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if(messageType.equals(ParseConstants.TYPE_IMAGE)){
            Intent viewImageIntent = new Intent(getActivity(), ViewImageActivity.class);
            viewImageIntent.setData(fileUri);
            startActivity(viewImageIntent);
        }
        else{
            Intent viewVideoIntent = new Intent(Intent.ACTION_VIEW, fileUri);
            viewVideoIntent.setDataAndType(fileUri,"video/*");
            startActivity(viewVideoIntent);
        }

        //Delete it
        List<String> recipients = message.getList(ParseConstants.KEY_RECIPIENTS_IDS);
        if(recipients.size() == 1){
            message.deleteInBackground();
        }
        else{
            recipients.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> toRemove = new ArrayList<String>();
            toRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(ParseConstants.KEY_RECIPIENTS_IDS, toRemove);
            message.saveInBackground();
        }
    }

}
