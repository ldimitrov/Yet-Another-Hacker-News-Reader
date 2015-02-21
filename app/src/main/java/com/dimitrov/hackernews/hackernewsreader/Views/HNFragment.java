package com.dimitrov.hackernews.hackernewsreader.Views;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimitrov.hackernews.hackernewsreader.R;
import com.dimitrov.hackernews.hackernewsreader.Interfaces.FetchedPosts;
import com.dimitrov.hackernews.hackernewsreader.Models.Post;
import com.dimitrov.hackernews.hackernewsreader.Jobs.FetchPostsJob;

import java.util.ArrayList;
import java.util.List;

public class HNFragment extends ListFragment {

    private ListView mListView;
    private RelativeLayout mProgressBar;
    private ArrayList<Post> mPosts;

    public static final int VIEW_IN_BROWSER_ID = Menu.FIRST + 1;
    public static final int SHARE_ID = Menu.FIRST + 2;
    private static final String LOG_TAG = HNFragment.class.getSimpleName();

    /**
     * Callback object to notify which item is clicked.
     */
    private Callbacks mCallbacks = sPostCallbacks;

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    /**
     * Notify all activities that contain this fragment on item clicks
     */
    public interface Callbacks {
        public void onItemClick(String postUrl);
    }

    /**
     * Callbacks interface implementation.
     */
    private static Callbacks sPostCallbacks = new Callbacks() {

        @Override
        public void onItemClick(String postUrl) {

        }
    };

    public HNFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_list, container, false);
        mListView = (ListView) rootView.findViewById(android.R.id.list);
        mProgressBar = (RelativeLayout) rootView.findViewById(android.R.id.empty);

        loadHackerNewsPosts();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Enforce use of Callbacks interface.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity does not  implement the callbacks interface.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sPostCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        Post post = mPosts.get(position);
        mCallbacks.onItemClick(post.getUrl());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, VIEW_IN_BROWSER_ID, 0, R.string.open_browser);
        menu.add(0, SHARE_ID, 0, R.string.action_share);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean result = super.onContextItemSelected(item);
        Intent intent;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Post post = mPosts.get(info.position);
        switch (item.getItemId()) {
            case HNFragment.SHARE_ID:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, post.getUrl());
                startActivity(Intent.createChooser(intent, getString(R.string.action_share_title)));
                break;
            case HNFragment.VIEW_IN_BROWSER_ID:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(post.getUrl()));
                startActivity(intent);
                break;
        }
        return result;
    }

    public void loadHackerNewsPosts() {
        mListView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        FetchedPosts fetchedPosts = new FetchedPosts() {
            @Override
            public void FetchedPosts(List<Post> postList) {
                populateListView(postList);
            }
        };
        FetchPostsJob fetchPostsJob = new FetchPostsJob(fetchedPosts);
        fetchPostsJob.execute();
    }

    public void populateListView(final List<Post> postList) {

        mPosts = (ArrayList<Post>) postList;

        ArrayAdapter<Post> postListAdapter = new ArrayAdapter<Post> (
                getActivity(),
                R.layout.post_list_item,
                R.id.item_number,
                postList)
        {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                Post post = postList.get(position);

                ((TextView) view.findViewById(R.id.item_number))
                        .setText(Integer.toString(post.getNumber()));
                ((TextView) view.findViewById(R.id.item_title))
                        .setText(post.getTitle());
                ((TextView) view.findViewById(R.id.item_url))
                        .setText(post.getPrettyUrl());

                String postDetails = "";

                // Calculate when was the post posted
                CharSequence postedAgo = DateUtils.getRelativeTimeSpanString(
                        post.getTime() * 1000,
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS);

                // Get the number of comments
                int numComments = 0;
                if (post.getKids() != null) {
                    numComments = post.getKids().size();
                }

                postDetails = postDetails + "" + postedAgo;
                postDetails = postDetails + "  " + numComments + " comments";

                ((TextView) view.findViewById(R.id.post_details))
                        .setText(postDetails);

                return view;
            }
        };

        try {
            mListView.setAdapter(postListAdapter);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

}