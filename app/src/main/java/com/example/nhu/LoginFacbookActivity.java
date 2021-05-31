package com.example.nhu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginFacbookActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private static final String LOG_TAG = LoginFacbookActivity.class.getName();
    LoginButton loginButton;
    TextView logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_facbook);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        // Setup login button
        if (isLoggedIn()) {
            // Set the view as the user has logged in
            getUserInfo(AccessToken.getCurrentAccessToken());
        } else {
            // Set the login button function
            getSupportActionBar().setTitle("Login");
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setReadPermissions("email");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Toast.makeText(LoginFacbookActivity.this, "This is a message success!", Toast.LENGTH_SHORT).show();
                    getUserInfo(AccessToken.getCurrentAccessToken());
                    getSupportActionBar().setTitle("Profile");

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
        }
        logout = findViewById(R.id.logout_ok);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                findViewById(R.id.layout_login).setVisibility(View.VISIBLE);
                findViewById(R.id.detail).setVisibility(View.GONE);

            }
        });
        LoginManager.getInstance().logOut();
    }

    private void getUserInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            JSONObject data = response.getJSONObject();
            try {
                // Get the data from response
                String id = data.getString("id");
                String name = data.getString("name");

                // Get the JWT from the server


                ImageView userAvatar = findViewById(R.id.avatar);
                TextView userName = findViewById(R.id.username);
                userName.setText(name);

                // Update the avatar
                if (data.has("picture")) {
                    String pictureUrl = data.getJSONObject("picture")
                            .getJSONObject("data")
                            .getString("url");
                    Glide.with(getApplicationContext())
                            .applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                            .load(Uri.parse(pictureUrl)).into(userAvatar);
                }

                // Show the user info in the navigation header
                findViewById(R.id.detail).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_login).setVisibility(View.GONE);
                getSupportActionBar().setTitle("Profile");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.toString());
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", TextUtils.join(",", Arrays.asList("id", "name", "picture")));
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        getUserInfo(AccessToken.getCurrentAccessToken());
        super.onActivityResult(requestCode, resultCode, data);
    }
    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && !accessToken.isExpired();
    }

    // Go back to the MainActivity when up button in action bar is clicked on.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    }
