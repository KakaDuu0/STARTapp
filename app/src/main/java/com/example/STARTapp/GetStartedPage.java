package com.example.STARTapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.example.STARTapp.SQLConnection.ACCOUNTS_TABLE;

/**
 * Pagina de login
 */
public class GetStartedPage extends AppCompatActivity {

    public static final String PREFS_NAME = "RememberMe";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_EMAIL = "email";

    private EditText eName;
    private EditText ePassword;
    private CheckBox eCheckBox;
    private String email;
    private String username;
    private long pressedTime;
    private boolean isValid = false;

    Connection connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started_page);

        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedUsername = pref.getString(PREF_USERNAME, null);
        String savedEmail = pref.getString(PREF_EMAIL, null);

        Button eRegister = findViewById(R.id.btnCreate);
        eName = findViewById(R.id.etName);
        ePassword = findViewById(R.id.etPassword);
        eCheckBox = findViewById(R.id.cb_rememberme);
        Button eLogin = findViewById(R.id.btnLogin);

        if (savedUsername != null && savedEmail != null)
            redirect(savedUsername, savedEmail);

        eLogin.setOnClickListener(v -> {
            String inputName = eName.getText().toString().trim();
            String inputPassword = ePassword.getText().toString().trim();

            if (inputName.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(GetStartedPage.this, "Completati toate formularele!", Toast.LENGTH_SHORT).show();
            } else {
                eLogin.setClickable(false);
                isValid = checkCred(inputName, inputPassword);

                if (!isValid) {
                    Toast.makeText(GetStartedPage.this, "Date Introduse Invalide", Toast.LENGTH_LONG).show();
                    eLogin.setClickable(true);
                } else {
                    if (eCheckBox.isChecked())
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                                .edit()
                                .putString(PREF_USERNAME, username)
                                .putString(PREF_EMAIL, email)
                                .apply();
                    eName.getText().clear();
                    redirect(username, email);
                }
                ePassword.getText().clear();

            }
        });

        eRegister.setOnClickListener(v -> {
            Intent intent = new Intent(GetStartedPage.this, RegisterPage.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            String close = getResources().getString(R.string.close_app);
            Toast.makeText(getBaseContext(), close, Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    /**
     * Verifica datele introduse pentru login
     *
     * @param name     numele/emailul utilizatorului
     * @param password verifica parola utilizatorului folosind salt hash
     * @return intoarce true daca reuseste sau false daca apar probleme
     */
    private boolean checkCred(String name, String password) {
        try {
            connect = SQLConnection.getConnection();
            if (connect == null) {
                Toast.makeText(GetStartedPage.this, "Verificati Conexiunea", Toast.LENGTH_LONG).show();
                return false;
            }
            String type;
            if (name.contains("@"))
                type = "Email";
            else
                type = "Username";

            PreparedStatement statement = connect.prepareStatement("SELECT * from " + ACCOUNTS_TABLE + " WHERE " + type + " = ?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            password = PasswordHash.generate(password, resultSet.getBytes(4));
            if (password.equals(resultSet.getString(3))) {
                email = resultSet.getString(2);
                username = resultSet.getString(1);
                return true;
            }
            return false;
        } catch (Exception ex) {
            Toast.makeText(GetStartedPage.this, "Eroare", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     * Functie pentru redirectionarea catre activitatea HomePage
     *
     * @param username numele utilizatorului
     * @param email    emailul utilizatorului
     */
    private void redirect(String username, String email) {
        Intent intent = new Intent(GetStartedPage.this, HomePage.class);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        startActivity(intent);
    }
}
