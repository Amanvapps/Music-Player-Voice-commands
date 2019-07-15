package c.aman.aimusicplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        WebView browser = (WebView) findViewById(R.id.main_web_view);
        browser.setWebViewClient(new WebViewClient());
        browser.loadUrl("file:///android_asset/privacypolicy.html");
    }
}
