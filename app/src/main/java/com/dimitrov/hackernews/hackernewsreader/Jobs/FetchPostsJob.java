package com.dimitrov.hackernews.hackernewsreader.Jobs;

import android.os.AsyncTask;

import com.dimitrov.hackernews.hackernewsreader.Utils.Constants;
import com.dimitrov.hackernews.hackernewsreader.Interfaces.FetchedPosts;
import com.dimitrov.hackernews.hackernewsreader.Models.Post;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FetchPostsJob extends AsyncTask<Long, Void, Void> {

    private FetchedPosts mPostsListener;
    private static final String HACKER_NEWS_ITEM_URL = "https://news.ycombinator.com/item?id=";

    public FetchPostsJob(FetchedPosts listener) {
        mPostsListener = listener;
    }

    @Override
    protected Void doInBackground(Long... params) {

        Firebase topPosts = new Firebase(Constants.KEY_TOP_POSTS);

        Query query = topPosts;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList children = (ArrayList) snapshot.getValue();

                final ArrayList<Post> postList = new ArrayList<Post>();

                final int[] index = {1};
                for (Iterator<Long> i = children.iterator(); i.hasNext(); ) {
                    Firebase itemsRef = new Firebase(Constants.KEY_ITEM_URL + i.next());
                    itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            HashMap<String, Object> item = (HashMap<String, Object>) snapshot.getValue();

                            Post post = new Post((Long) item.get(Constants.KEY_ID), index[0]);
                            post.setKids((ArrayList<String>) item.get(Constants.KEY_KIDS));
                            post.setTime((Long) item.get(Constants.KEY_TIME));
                            post.setTitle((String) item.get(Constants.KEY_TITLE));

                            String url = (String) item.get(Constants.KEY_URL);
                            if (url.isEmpty()) {
                                url = HACKER_NEWS_ITEM_URL + post.getId();
                            }
                            post.setUrl(url);

                            String prettyUrl = url;
                            String[] splitUrl = prettyUrl.split("/");
                            if (splitUrl.length > 2) {
                                prettyUrl = splitUrl[2];
                            }
                            post.setPrettyUrl(prettyUrl);

                            postList.add(post);
                            index[0] = index[0] + 1;

                            mPostsListener.FetchedPosts(postList);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("FAILed to read: " + firebaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("FAILed to read: " + firebaseError.getMessage());
            }
        });

        return null;
    }
}
