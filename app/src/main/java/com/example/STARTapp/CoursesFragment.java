package com.example.STARTapp;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.example.STARTapp.SQLConnection.COURSESUSERS_TABLE;
import static com.example.STARTapp.SQLConnection.COURSES_TABLE;

/**
 * Fragment pentru selectarea cursurilor
 */
public class CoursesFragment extends Fragment {
    private static float progress = 0;
    Connection connect = SQLConnection.getConnection();
    private static int capitol;
    private static int lastQuestion;
    private int course_id;

    public CoursesFragment() {
        // Required empty public constructor
    }

    public static CoursesFragment newInstance() {
        CoursesFragment fragment = new CoursesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        ((HomePage) requireActivity()).updateStatusBarColor("#00B2E5");

        CardView cv1 = view.findViewById(R.id.cv_course1);
        ProgressBar financeProgressBar = view.findViewById(R.id.pb_course1);
        boolean isEnrolledFinance = checkEnrolled(getSet(COURSESUSERS_TABLE));
        if (isEnrolledFinance) {
            financeProgressBar.setVisibility(View.VISIBLE);
            financeProgressBar.setProgress((int) progress);
        }

        CardView cv2 = view.findViewById(R.id.cv_course2);
        cv2.setClickable(false);

        cv1.setOnClickListener(v -> {
            course_id = 1;
            if (!isEnrolledFinance)
                addUserToFinanceTable();
            getCourse(course_id);
            startCourse(course_id);
        });

        cv2.setOnClickListener(v -> Toast.makeText(getContext(), "Curs In Lucru", Toast.LENGTH_LONG).show());

        return view;
    }

    /**
     * Functie pentru pornirea cursului
     *
     * @param id index curs
     */
    private void startCourse(int id) {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, StartCourseFragment.newInstance(id));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Metoda care verifica daca userul este inscris in tabela pentru curs
     *
     * @param resultSet ofera datele tabelei
     * @return intoarce true daca userul este inscris in curs, false altfel
     */
    private boolean checkEnrolled(ResultSet resultSet) {
        try {
            if (resultSet == null)
                return false;
            capitol = resultSet.getInt(5);
            lastQuestion = resultSet.getInt(6);
            progress = resultSet.getFloat(4);
            HomePage.user.setProgress(progress);
            return true;
        } catch (Exception e) {
            Log.e("checkEnrolled: ", e.toString());
        }
        return false;
    }

    /**
     * Functie pentru aducerea datelor tabelei din baza de date
     *
     * @param table specifica tabela
     * @return intoarce datele daca le gaseste / null altfel
     */
    private ResultSet getSet(String table) {
        ResultSet resultSet = null;
        try {
            if (connect == null)
                return null;
            PreparedStatement statement = connect.prepareStatement("SELECT * FROM " + table + " WHERE Username = ?");
            statement.setString(1, HomePage.user.getUsername());
            resultSet = statement.executeQuery();
            if (resultSet.next())
                return resultSet;
        } catch (Exception e) {
            Log.e("checkEnrolled: ", e.toString());
        }
        return resultSet;
    }

    /**
     * Functie pentru punerea utilizatorul in tabela pentru curs
     */
    private void addUserToFinanceTable() {
        try {
            if (connect == null)
                return;
            PreparedStatement checkUsernameStatement = connect.prepareStatement("SELECT * FROM " + COURSESUSERS_TABLE + " WHERE Username = ? AND CourseId = ?");
            checkUsernameStatement.setString(1, HomePage.user.getUsername());
            checkUsernameStatement.setInt(2, course_id);
            ResultSet resultSet = checkUsernameStatement.executeQuery();


            if (!resultSet.next()) {
                PreparedStatement createProfileStatement = connect.prepareStatement("INSERT INTO " + COURSESUSERS_TABLE + " (Username, CourseId, Progress, Capitol, LastQuestion) VALUES (?,?, ?, ?, ?)");
                createProfileStatement.setString(1, HomePage.user.getUsername());
                createProfileStatement.setInt(2, course_id);
                createProfileStatement.setFloat(3, 0);
                createProfileStatement.setInt(4, 1);
                createProfileStatement.setInt(5, 0);
                createProfileStatement.execute();
            }
        } catch (Exception e) {
            Log.e("addUserToFinanceTable: ", e.toString());
        }
    }

    /**
     * Functie care aduce datele cursului
     *
     * @param id indexul cursului
     */
    private void getCourse(int id) {
        try {
            if (connect == null)
                return;
            PreparedStatement statement = connect.prepareStatement("SELECT * FROM " + COURSES_TABLE + " WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int numarCapitole = resultSet.getInt(4);
        } catch (Exception e) {
            Log.e("getCourse: ", e.toString());
        }
    }

    public static int getCapitol() {
        return capitol;
    }

    public static int getLastQuestion() {
        return lastQuestion;
    }
}
