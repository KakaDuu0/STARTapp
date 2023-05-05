package com.example.STARTapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import static com.example.STARTapp.SQLConnection.ACCOUNTS_TABLE;
import static com.example.STARTapp.SQLConnection.PROFILES_TABLE;

/**
 * Pagina de inregistrare user nou
 */
public class RegisterPage extends AppCompatActivity {

    private EditText eEmail;
    private EditText eName;
    private EditText ePassword;
    private EditText ePasswordConfirm;

    Connection connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        eEmail = findViewById(R.id.etEmail);
        eName = findViewById(R.id.etName);
        ePassword = findViewById(R.id.etPassword);
        ePasswordConfirm = findViewById(R.id.etPasswordConfirm);
        Button eRegister = findViewById(R.id.btnRegister);
        Button eReturn = findViewById(R.id.register_btnReturn);

        eRegister.setOnClickListener(v -> {
            String inputEmail = eEmail.getText().toString();
            String inputName = eName.getText().toString();
            String inputPassword = ePassword.getText().toString();
            String inputPasswordConfirm = ePasswordConfirm.getText().toString();
            eRegister.setClickable(false);
            if (CheckCreds(inputEmail, inputName, inputPassword, inputPasswordConfirm)) {
                if (SubmitRegister(inputName, inputEmail, inputPassword) && CreateProfile(inputName)) {
                    Toast.makeText(RegisterPage.this, "Inregistrat!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterPage.this, GetStartedPage.class);
                    startActivity(intent);
                }
            } else {
                eRegister.setClickable(true);
                ePassword.getText().clear();
                ePasswordConfirm.getText().clear();
            }
        });

        eReturn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterPage.this, GetStartedPage.class);
            startActivity(intent);
        });
    }

    private long pressedTime;

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
     * Metoda pentru a introduce userul inregistrat in baza de date
     *
     * @param usr   username
     * @param email email
     * @param pass  parola
     * @return intoarce true
     */
    public boolean SubmitRegister(String usr, String email, String pass) {
        try {
            connect = SQLConnection.getConnection();
            if (connect != null) {
                pass = pass.trim();
                pass = PasswordHash.generate(pass, null);
                PreparedStatement stmt = connect.prepareStatement("INSERT INTO " + ACCOUNTS_TABLE + "(Username, Email, Password, Salt) VALUES (?, ?, ?, ?)");
                stmt.setString(1, usr.trim());
                stmt.setString(2, email.trim());
                stmt.setString(3, pass);
                stmt.setBytes(4, PasswordHash.Salt);

                stmt.executeUpdate();
                return true;
            } else {
                Toast.makeText(RegisterPage.this, "Connection Failed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(RegisterPage.this, "Connection Error!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Metoda pentru verificarea datelor introduse de utilizator
     *
     * @param email
     * @param name
     * @param password
     * @param passwordConfirm
     * @return intoarce true daca toate datele sunt valide sau false in caz contrar
     */
    private boolean CheckCreds(String email, String name, String password, String passwordConfirm) {
        boolean err = false;
        if (!checkName(name)) {
            Toast.makeText(RegisterPage.this, "Numele contine caractere speciale!", Toast.LENGTH_LONG).show();
            err = true;
        }
        if (!(email.contains("@") && email.contains(".") && email.length() > 4)) {
            Toast.makeText(RegisterPage.this, "Email invalid!", Toast.LENGTH_LONG).show();
            err = true;
        }
        if (!EmailCheck(email, name)) {
            err = true;
        }
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(RegisterPage.this, "Parolele nu coincid!", Toast.LENGTH_LONG).show();
            err = true;
        }
        if (!(password.length() > 7)) {
            Toast.makeText(RegisterPage.this, "Introduceti o parola de macar 8 caractere", Toast.LENGTH_LONG).show();
            err = true;
        }
        return !err;
    }

    /**
     * Metoda pentru verificarea numelui
     *
     * @param name
     * @return intoarce true daca este valid sau false in caz contrar
     */
    private boolean checkName(String name) {
        for (int i = 0; i < name.length(); ++i)
            if (!Character.isLetterOrDigit(name.charAt(i)))
                return false;
        return true;
    }

    /**
     * Metoda pentru verificarea emailului si a numelui
     *
     * @param email
     * @param name
     * @return intoarce true daca este valid sau false in caz contrar
     */
    private boolean EmailCheck(String email, String name) {
        try {
            connect = SQLConnection.getConnection();
            if (connect == null)
                return false;
            String query = "Select * from " + ACCOUNTS_TABLE;
            Statement st = connect.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                if (email.equals(rs.getString(2))) {
                    Toast.makeText(RegisterPage.this, "Email deja inregistrat!", Toast.LENGTH_LONG).show();
                    eEmail.getText().clear();
                    return false;
                } else if (name.equals(rs.getString(1))) {
                    Toast.makeText(RegisterPage.this, "Nume Utilizator Existent!", Toast.LENGTH_LONG).show();
                    eName.getText().clear();
                    return false;
                }
            }
        } catch (Exception ex) {
            Toast.makeText(RegisterPage.this, "Connection Failed.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void createUserProfile(String username) {
        CreateProfile(username);
    }

    /**
     * Functie pentru a introduce datele noului utilizator in baza de date
     *
     * @param username
     * @return intoarce true daca a reusit sau false in caz contrar
     */
    private boolean CreateProfile(String username) {
        try {
            connect = SQLConnection.getConnection();
            if (connect == null)
                return false;
            PreparedStatement stmt = connect.prepareStatement("INSERT INTO " + PROFILES_TABLE + "(Username, Courses, Image, Xp, Lastlogin, Streakcounter) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, username);
            stmt.setInt(2, 0);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.defaultpic);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bArray = bos.toByteArray();
            stmt.setBytes(3, bArray);
            stmt.setInt(4, 0);
            stmt.setInt(5, Calendar.DAY_OF_YEAR);
            stmt.setInt(6, 0);
            stmt.executeUpdate();

            return true;
        } catch (Exception ex) {
            Toast.makeText(RegisterPage.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
