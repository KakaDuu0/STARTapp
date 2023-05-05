package com.example.STARTapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.example.STARTapp.SQLConnection.ACCOUNTS_TABLE;
import static com.example.STARTapp.SQLConnection.COURSESUSERS_TABLE;
import static com.example.STARTapp.SQLConnection.PROFILES_TABLE;


/**
 * Fragment pentru setari
 */
public class SettingsFragment extends Fragment {
    private static User USER = null;
    private SwitchCompat eNotifsbtn;
    private String newUsername;
    private String newPassword;
    private String feedbackText;

    Connection connect = SQLConnection.getConnection();

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance(User user) {
        SettingsFragment fragment = new SettingsFragment();
        USER = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ((HomePage) requireActivity()).updateStatusBarColor("#00B2E5");

        TextView eNotifs = view.findViewById(R.id.tv_notifs);
        eNotifsbtn = view.findViewById(R.id.sw_notifs);
        TextView eUsername = view.findViewById(R.id.tv_username);
        TextView ePassword = view.findViewById(R.id.tv_password);
        TextView eFeedback = view.findViewById(R.id.tv_feedback);
        TextView eLogout = view.findViewById(R.id.tv_logout);

        eNotifs.setOnClickListener(v -> eNotifsbtn.setChecked(!eNotifsbtn.isChecked()));

        funcLogout(inflater, eLogout);
        funcPassword(inflater, ePassword);
        funcFeedback(inflater, eFeedback);
        funcUsername(inflater, eUsername);

        return view;
    }

    /**
     * Functie pentru trimitere feedback
     *
     * @param inflater
     * @param eFeedback butonul pentru feedback
     */
    private void funcFeedback(LayoutInflater inflater, TextView eFeedback) {
        eFeedback.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
            View dialogLayout = inflater.inflate(R.layout.feedback_popup, null);
            final AlertDialog dialog = builder.create();
            setDialog(dialog, dialogLayout);

            Button btnChange = dialogLayout.findViewById(R.id.btnChange);
            Button btnCancel = dialogLayout.findViewById(R.id.btnCancel);
            EditText etText = dialogLayout.findViewById(R.id.etText);

            btnChange.setOnClickListener(v1 -> {
                feedbackText = etText.getText().toString();
                trySend(feedbackText);
                dialog.dismiss();
            });
            btnCancel.setOnClickListener(v2 -> dialog.dismiss());

            builder.setView(dialogLayout);
            dialog.show();
        });
    }

    /**
     * Functie pentru schimbare username
     *
     * @param inflater
     * @param eUsername butonul pentru username
     */
    @SuppressLint("SetTextI18n")
    private void funcUsername(LayoutInflater inflater, TextView eUsername) {
        eUsername.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
            View dialogLayout = inflater.inflate(R.layout.datachange_popup, null);
            final AlertDialog dialog = builder.create();
            setDialog(dialog, dialogLayout);

            Button btnChange = dialogLayout.findViewById(R.id.btnChange);
            Button btnCancel = dialogLayout.findViewById(R.id.btnCancel);
            TextView tvData = dialogLayout.findViewById(R.id.tv_datashow);
            EditText etData = dialogLayout.findViewById(R.id.etNewData);
            TextView tvTitle = dialogLayout.findViewById(R.id.tv_title);

            tvData.setText(USER.getUsername());
            builder.setView(tvData);

            etData.setHint("Nume de utilizator nou");
            builder.setView(etData);

            tvTitle.setText("Schimba numele de utilizator");
            builder.setView(tvTitle);

            btnChange.setOnClickListener(v1 -> {
                newUsername = etData.getText().toString();
                trySubmit(USER.getUsername(), newUsername);
                dialog.dismiss();
            });
            btnCancel.setOnClickListener(v2 -> dialog.dismiss());

            builder.setView(dialogLayout);
            dialog.show();
        });
    }

    /**
     * Functie pentru delogare
     *
     * @param inflater
     * @param eLogout  butonul pentru logout
     */
    private void funcLogout(LayoutInflater inflater, TextView eLogout) {
        eLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
            View dialogLayout = inflater.inflate(R.layout.logout_popup, null);
            final AlertDialog dialog = builder.create();
            setDialog(dialog, dialogLayout);

            Button btnLogout = dialogLayout.findViewById(R.id.btnLogout);
            Button btnCancel = dialogLayout.findViewById(R.id.btnCancel);

            btnLogout.setOnClickListener(v1 -> {
                dialog.dismiss();
                resetPref();
                Intent intent = new Intent(getActivity(), GetStartedPage.class);
                startActivity(intent);
            });
            btnCancel.setOnClickListener(v2 -> dialog.dismiss());

            builder.setView(dialogLayout);
            dialog.show();
        });
    }

    /**
     * Functie pentru schimbare parola
     *
     * @param inflater
     * @param ePassword butonul pentru parola
     */
    @SuppressLint("SetTextI18n")
    private void funcPassword(LayoutInflater inflater, TextView ePassword) {
        ePassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
            View dialogLayout = inflater.inflate(R.layout.datachange_popup, null);
            final AlertDialog dialog = builder.create();
            setDialog(dialog, dialogLayout);

            Button btnChange = dialogLayout.findViewById(R.id.btnChange);
            Button btnCancel = dialogLayout.findViewById(R.id.btnCancel);
            TextView tvData = dialogLayout.findViewById(R.id.tv_datashow);
            EditText etData = dialogLayout.findViewById(R.id.etNewData);
            TextView tvTitle = dialogLayout.findViewById(R.id.tv_title);

            tvData.setVisibility(View.GONE);

            etData.setHint("Parola Noua");
            builder.setView(etData);

            tvTitle.setText("Schimba parola");
            builder.setView(tvTitle);

            btnChange.setOnClickListener(v1 -> {
                newPassword = etData.getText().toString();
                changePass(newPassword);
                dialog.dismiss();
            });
            btnCancel.setOnClickListener(v2 -> dialog.dismiss());

            builder.setView(dialogLayout);
            dialog.show();
        });
    }

    /**
     * Functie pentru schimbul parolei
     *
     * @param pass
     */
    private void changePass(String pass) {
        pass = pass.trim();
        if (pass.length() > 7) {
            try {
                pass = PasswordHash.generate(pass, null);
                PreparedStatement stmt = connect.prepareStatement("UPDATE " + ACCOUNTS_TABLE + " SET Password = ?, Salt = ? WHERE Username = ?");
                stmt.setString(1, pass);
                stmt.setBytes(2, PasswordHash.Salt);
                stmt.setString(3, USER.getUsername());
                stmt.executeUpdate();
            } catch (Exception e) {
                Log.e("changePass: ", e.toString());
            }
        }
    }

    private void setDialog(AlertDialog dialog, View dialogLayout) {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.setView(dialogLayout, 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.BOTTOM;
    }

    /**
     * Schimbare username in toate tabelele din database
     *
     * @param currentData username inainte de schimbare
     * @param newData     username dupa schimbare
     */
    private void trySubmit(String currentData, String newData) {
        if (newData.length() < 5) {
            Toast.makeText(getActivity(), "introduceti macar 5 caractere", Toast.LENGTH_LONG).show();
            return;
        }
        if (currentData.equals(newData)) {
            Toast.makeText(getActivity(), "date identice", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (connect == null)
                return;
            PreparedStatement checkUsernameStatement = connect.prepareStatement("SELECT Username from " + ACCOUNTS_TABLE + " WHERE Username = ?");
            PreparedStatement changeProfilesUsernameStatement = connect.prepareStatement("UPDATE " + PROFILES_TABLE + " SET Username = ? WHERE Username = ?");
            PreparedStatement changeCoursesUsernameStatement = connect.prepareStatement("UPDATE " + COURSESUSERS_TABLE + " SET Username = ? WHERE Username = ?");
            PreparedStatement changeAccountsUsernameStatement = connect.prepareStatement("UPDATE " + ACCOUNTS_TABLE + " SET Username = ? WHERE Username = ?");

            checkUsernameStatement.setString(1, currentData);
            ResultSet resultSet = checkUsernameStatement.executeQuery();

            if (resultSet.next()) {
            changeProfilesUsernameStatement.setString(1, newData);
            changeProfilesUsernameStatement.setString(2, currentData);
            changeProfilesUsernameStatement.execute();

            changeCoursesUsernameStatement.setString(1, newData);
            changeCoursesUsernameStatement.setString(2, currentData);
            changeCoursesUsernameStatement.executeUpdate();
            }

            changeAccountsUsernameStatement.setString(1, newData);
            changeAccountsUsernameStatement.setString(2, currentData);
            try {
                changeAccountsUsernameStatement.executeUpdate();
                Toast.makeText(getContext(), "Succes", Toast.LENGTH_LONG).show();
                USER.setUsername(newData);
                requireActivity().getSharedPreferences(GetStartedPage.PREFS_NAME, Context.MODE_PRIVATE).edit().putString(GetStartedPage.PREF_USERNAME, newData).apply();
            } catch (Exception e) {
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Trimitere feedback la adresa de mail a echipei Vsauce4
     *
     * @param Data Textul de feedback
     */
    private void trySend(String Data) {
        if (Data.length() > 10) {
            final String to = "startappv4@gmail.com";
            String subject = "New Feedback from " + USER.getUsername();

            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
            email.putExtra(Intent.EXTRA_SUBJECT, subject);
            email.putExtra(Intent.EXTRA_TEXT, Data);

            email.setType("message/rfc822");

            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        } else Toast.makeText(getActivity(), "Textul e prea scurt!", Toast.LENGTH_SHORT).show();
    }

    private void resetPref() {
        requireActivity().getSharedPreferences(GetStartedPage.PREFS_NAME, 0)
                .edit()
                .putString(GetStartedPage.PREF_USERNAME, null)
                .apply();
    }
}
