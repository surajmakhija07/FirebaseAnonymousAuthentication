package app;

import android.app.Application;

import com.android.letsnurture.firebaseanonymousauthentication.R;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Suraj Makhija on 29/6/16.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.str_twitter_key), getString(R.string.str_twitter_secret));
        Fabric.with(this, new Twitter(authConfig));
    }
}
