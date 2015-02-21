package com.dimitrov.hackernews.hackernewsreader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dimitrov.hackernews.hackernewsreader.Views.WebViewActivity;
import com.firebase.client.Firebase;
import com.dimitrov.hackernews.hackernewsreader.Views.HNFragment;
import com.dimitrov.hackernews.hackernewsreader.Views.WebViewFragment;

public class MainActivity extends ActionBarActivity implements HNFragment.Callbacks {

    public static final String TAG = MainActivity.class.getSimpleName();

    /**
     * In case of table, grid view should be used
     */
    private boolean mTabletMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);

        if (isNetworkAvailable() == false) {
            Toast.makeText(this, "Check your network connection", Toast.LENGTH_LONG).show();
            return;
        }

        if (findViewById(R.id.web_view) != null) {
            mTabletMode = true;
            ((HNFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.post_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_fragment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            HNFragment fragment = ((HNFragment) getSupportFragmentManager().findFragmentById(R.id.post_list));
            fragment.loadHackerNewsPosts();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(String postUrl) {

        if (mTabletMode) {
            Bundle arguments = new Bundle();
            arguments.putString(WebViewFragment.KEY_URL, postUrl);
            WebViewFragment fragment = new WebViewFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.web_view, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.setData(Uri.parse(postUrl));
            startActivity(intent);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
