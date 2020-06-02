package com.chentir.mopublaredoutelink;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });


    new Thread(new Runnable() {
      @Override public void run() {
        try {
          MainActivity.resolveUrl(
              "https://www.laredoute.fr/ppdp/prod-537718858.aspx?cod=MDC00091059FR&ectrans=1");
        } catch (Exception e) {
        }
      }
    }).start();
  }

  public static void resolveUrl(String urlString) throws IOException, URISyntaxException {
    URL url = new URL(urlString);
    HttpURLConnection httpUrlConnection = null;
    String resolvedUrl;

    try {
      httpUrlConnection = (HttpURLConnection) url.openConnection();
      httpUrlConnection.setInstanceFollowRedirects(false);

      resolvedUrl = resolveRedirectLocation(urlString, httpUrlConnection);
    } finally {
      if (httpUrlConnection != null) {
        InputStream is = null;
        try {
          is = httpUrlConnection.getInputStream();
        } catch (Exception e) {
          Log.e("MopubLaRedouteLink", "" + e);
        }

        if (is != null) {
          try {
            is.close();
          } catch (IOException e) {
          }
        }
        httpUrlConnection.disconnect();
      }
    }

    System.out.println("Resolved URL = " + resolvedUrl);
  }

  private static String resolveRedirectLocation(final String baseUrl,
      final HttpURLConnection httpUrlConnection) throws IOException, URISyntaxException {
    final URI baseUri = new URI(baseUrl);
    final int responseCode = httpUrlConnection.getResponseCode();
    final String redirectUrl = httpUrlConnection.getHeaderField("location");
    String result = null;

    if (responseCode >= 300 && responseCode < 400) {
      try {
        // If redirectUrl is a relative path, then resolve() will correctly complete the path;
        // otherwise, resolve() will return the redirectUrl
        result = baseUri.resolve(redirectUrl).toString();
      } catch (IllegalArgumentException e) {
        // Ensure the request is cancelled instead of resolving an intermediary URL
        System.out.println(
            "Invalid URL redirection. baseUrl=" + baseUrl + "\n redirectUrl=" + redirectUrl);
        throw new URISyntaxException(redirectUrl, "Unable to parse invalid URL");
      } catch (NullPointerException e) {
        System.out.println(
            "Invalid URL redirection. baseUrl=" + baseUrl + "\n redirectUrl=" + redirectUrl);
        throw e;
      }
    }

    return result;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}