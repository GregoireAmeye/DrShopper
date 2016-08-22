package be.howest.nmct.shopperio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.howest.nmct.shopperio.Admin.Globals;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail, etPassword, etPasswordConfirm;
    Button btnRegister;
    TextView tvError;
    TextView tvErrorPassword;
    TextView tvErrorConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitUiItems();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Register");
        setSupportActionBar(myToolbar);

    }

    private void InitUiItems() {
        etEmail = (EditText) findViewById(R.id.etEmail);

        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        tvError = (TextView) findViewById(R.id.tvErrorRegister);
        tvErrorPassword = (TextView) findViewById(R.id.tvErrorRegisterPassword);
        tvErrorConfirm = (TextView) findViewById(R.id.tvErrorRegisterConfirm);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogAndRegisterUser();

                if(etEmail.getText().toString().trim().equals("")) {
                    tvError.setVisibility(View.VISIBLE);
                } else {
                    tvError.setVisibility(View.INVISIBLE);
                }
                if(etPassword.getText().toString().trim().equals("")) {
                    tvErrorPassword.setVisibility(View.VISIBLE);
                } else {
                    tvErrorPassword.setVisibility(View.INVISIBLE);
                }
                if(etPasswordConfirm.getText().toString().trim().equals("")) {
                    tvErrorConfirm.setVisibility(View.VISIBLE);
                } else {
                    tvErrorConfirm.setVisibility(View.INVISIBLE);
                }
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isEmailValid(s.toString())) {
                    etEmail.setError("Invalid email address");
                } else {
                    etEmail.setError(null);
                    tvError.setVisibility(View.INVISIBLE);
                }


                ValidateButton();
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!isPasswordValidLength(s.toString())) {
                    etPassword.setError("Password must be at least 6 characters long and contain characters and numbers.");
                } else {
                    etPassword.setError(null);
                    tvErrorPassword.setVisibility(View.INVISIBLE);
                }


                if (!s.toString().equals(etPassword.getText().toString()))
                    etPasswordConfirm.setError("Passwords do not match");
                else {
                    etPasswordConfirm.setError(null);
                    tvErrorConfirm.setVisibility(View.INVISIBLE);
                }

                ValidateButton();
            }
        });

        etPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(etPassword.getText().toString()))
                    etPasswordConfirm.setError("Passwords do not match");
                else
                    etPasswordConfirm.setError(null);

                ValidateButton();
            }
        });


    }

    private void ShowDialogAndRegisterUser() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "Please wait", "Registering...", true);
        progressDialog.setCancelable(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                RegisterNewUser();
                progressDialog.dismiss();

            }
        }).start();

    }

    private void ValidateButton() {
        if (etEmail.getError() == null & etPassword.getError() == null & etPasswordConfirm.getError() == null && !etEmail.getText().toString().equals("") &&
                !etPassword.getText().toString().equals("") && !etPasswordConfirm.getText().toString().equals("") && !etEmail.getText().toString().equals("")) {
            btnRegister.setEnabled(true);
        } else {
            btnRegister.setEnabled(false);
        }


    }

    private void RegisterNewUser() {
        String TAG = "Error";
        try {

            Globals g = Globals.getInstance();


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(g.getAPIurl() + "/api/account/register");

            String email, password, confirmpassword;


            email = etEmail.getText().toString();
            password = etPassword.getText().toString();
            confirmpassword = etPasswordConfirm.getText().toString();

            List nameValuePairs = new ArrayList(3);

            nameValuePairs.add(new BasicNameValuePair("Password", password));
            nameValuePairs.add(new BasicNameValuePair("ConfirmPassword", confirmpassword));
            nameValuePairs.add(new BasicNameValuePair("Email", email));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statuscode = httpResponse.getStatusLine().getStatusCode();

            if (statuscode == 200) {
                Intent intent = new Intent();
                intent.putExtra(LoginActivity.EXTRA_EMAILADDRESS, etEmail.getText().toString());
                intent.putExtra(LoginActivity.EXTRA_PASSWORD, etPassword.getText().toString());
                setResult(Activity.RESULT_OK, intent);

                finish();

            } else if (statuscode == 400) {
                String responsebody = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = new JSONObject(responsebody);
                String error = jsonObject.getString("ModelState");
                int begin = error.indexOf("[\"");
                begin += 2;
                int einde = error.indexOf("\"]");
                error = error.substring(begin, einde);
                if (error.indexOf("\",\"") > 0) {
                    begin = error.indexOf("\",\"");
                    begin += 3;
                    einde = error.length();
                    error = error.substring(begin, einde);
                }

            }

        } catch (UnknownHostException ex) {
            Log.e(TAG, "No Internet Connection", ex);
            tvError.setEnabled(true);
            tvError.setText("Could not connect to the server. Please, make sure you have internet connection.");
        } catch (Exception ex) {
            Log.e(TAG, "Oops! Something went wrong: " + ex.toString(), ex);

        }
    }

    public boolean isPasswordValidLength(String password) {
        String regExpn = "\\S{6,}";

        CharSequence inputStr = password;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches() && password.matches(".*\\d.*") && password.matches(".*[a-zA-Z]+.*"))
            return true;
        else
            return false;
    }


    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

}
