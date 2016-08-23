/**
 * Activity - Login
 *
 * Relationship:
 *   Parent Activity - no
 *   Next Activity   - MainMenuActicity / RegistActivity
 *
 * Action:
 *   Click - Button - Login
 *   Click - TextField - To registration
 *
 * Logical Processes
 *   1 - check validation of input value (empty)
 *   2 - encrypt password
 *   3 - check whether the user has registered
 *   4 - switch to MainMenuActicity and finish this acticity / switch to registration
 */
package monash.fit5046.assign.assignmentpaindiary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.FormatValidation;
import monash.fit5046.assign.assignmentpaindiary.BusinessLogic.RestClient;
import monash.fit5046.assign.assignmentpaindiary.entities.Registration;

public class LoginActivity extends AppCompatActivity {

    private ScrollView sv_content;
    private EditText et_username;
    private EditText et_password;
    private Button btn_login;
    private TextView btn_regist;

    // For animation
    private ProgressBar pb_loadspinner;
    private int mShortAnimationDuration;

    private String xml_username;
    private String xml_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout
        // 1 - Hide the title - must run this statement before setContentView
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.img_bg_login);
        /**
         * Toolbar Setting
         *
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
         */
        initiateWidgets();

        // Actions
        // 1 - Button (login)
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide the soft keyboard
                InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                if (FormatValidation.isValidString(et_username, et_password)) {
                    xml_username = et_username.getText().toString();
                    xml_password = et_password.getText().toString();

                    // For animation
                    sv_content.setVisibility(View.GONE);
                    pb_loadspinner.setVisibility(View.VISIBLE);
                    pb_loadspinner.animate()
                            .alpha(1f)
                            .setDuration(mShortAnimationDuration)
                            .setListener(null);

                    userLogin(xml_username, xml_password);
                } else {
                    Toast.makeText(LoginActivity.this, FormatValidation.errorNotice(11), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 2 - TextField (Switch to RegistActivity)
        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(intent);
            }
        });

        /* Remove floating mail icon
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /**
     * Initiate all Widgets of content_login.xml
     */
    private void initiateWidgets() {
        sv_content = (ScrollView) findViewById(R.id.la_sv_content);
        et_username = (EditText) findViewById(R.id.la_et_username);
        et_password = (EditText) findViewById(R.id.la_et_password);
        btn_login = (Button) findViewById(R.id.la_btn_login);
        btn_regist = (TextView) findViewById(R.id.la_btn_regist);

        // For animation
        pb_loadspinner = (ProgressBar) findViewById(R.id.la_pb_loadspinner);
        pb_loadspinner.setAlpha(0f);
        pb_loadspinner.setVisibility(View.GONE);

        // Cache the config_shortAnimTime system property in a member variable.
        // This property defines a standard "short" duration for the animation.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    /**
     * User login. Pass user name ane password to server and check if there is a valid user existing.
     * If there is a valid user existing, switches activity to MainMenuActivity, and finish
     * LoginAcitvity so that user can not return back to login screen. Else remain LoginAcitvity.
     *
     * @param username
     * @param password
     */
    private void userLogin(String username, String password) {
        new AsyncTask<String, Void, Registration>() {
            @Override
            protected Registration doInBackground(String... params) {
                return RestClient.findUser(params[0], params[1]);
            }

            @Override
            protected void onPostExecute(Registration loginUser) {
                if (loginUser == null) {
                    // Animation
                    pb_loadspinner.setVisibility(View.GONE);
                    sv_content.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Error: No such user", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Welcome back, " + loginUser.getUsername(), Toast.LENGTH_SHORT).show();
                    // Animation
                    pb_loadspinner.animate()
                            .alpha(0f)
                            .setDuration(mShortAnimationDuration)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    pb_loadspinner.setVisibility(View.GONE);
                                }
                            });

                    // Switch activity to Main Menu
                    Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                    intent.putExtra("login_user", loginUser);
                    startActivity(intent);
                    // Once user login successfully, finish LoginAcitvity so that user can not roll back.
                    finish();
                }
            }
        }.execute(username, password);
    }

    /* Remove setting icon
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
    }*/
}
