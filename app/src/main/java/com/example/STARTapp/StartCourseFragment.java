package com.example.STARTapp;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.example.STARTapp.SQLConnection.CAPITOLE_TABLE;
import static com.example.STARTapp.SQLConnection.COURSESUSERS_TABLE;
import static com.example.STARTapp.SQLConnection.COURSES_TABLE;

/**
 * Fragment inceput curs
 */
public class StartCourseFragment extends Fragment {
    public StartCourseFragment() {
    }

    Connection connect = SQLConnection.getConnection();
    private static int course_id;
    private static int numberOfQuestions;
    private static int capitol_id;
    private static int nrCapitole;
    private static int nrIntrebariCurs;
    private boolean chapter2Locked = true;
    private boolean chapter3Locked = true;

    public static StartCourseFragment newInstance(int courseId) {
        StartCourseFragment fragment = new StartCourseFragment();
        course_id = courseId;
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
        View view = inflater.inflate(R.layout.fragment_startcourse, container, false);

        ((HomePage) requireActivity()).updateStatusBarColor("#7A1A85");

        ImageButton btnChapter1 = view.findViewById(R.id.btn_node1);
        ImageButton btnChapter2 = view.findViewById(R.id.btn_node2);
        ImageButton btnChapter3 = view.findViewById(R.id.btn_node3);
        TextView chapterProgress = view.findViewById(R.id.course_progress1);
        TextView tvProgress = view.findViewById(R.id.progress_percent);
        ImageView lock2 = view.findViewById(R.id.lock2);
        ImageView lock3 = view.findViewById(R.id.lock3);

        setClickable(lock2, lock3, btnChapter2, btnChapter3);

        btnChapter1.setOnClickListener(v -> {
            capitol_id = 1;
            addUserToCoursesUsersTable();
            getCapitol(1);
            getQuestions();
        });

        btnChapter2.setOnClickListener(v -> {
            if (!chapter2Locked) {
                capitol_id = 2;
                addUserToCoursesUsersTable();
                getCapitol(2);
                getQuestions();
            } else
                Toast.makeText(getContext(), "Completeaza capitolul anterior", Toast.LENGTH_SHORT).show();
        });

        btnChapter3.setOnClickListener(v -> {
            if (!chapter3Locked) {
                capitol_id = 3;
                addUserToCoursesUsersTable();
                getCapitol(3);
                getQuestions();
            } else
                Toast.makeText(getContext(), "Completeaza capitolul anterior", Toast.LENGTH_SHORT).show();
        });

        int capitoleTerminate = CoursesFragment.getCapitol();
        if (capitoleTerminate > 0)
            capitoleTerminate--;

        chapterProgress.setText(capitoleTerminate + "/" + getNumarCapitole());
        tvProgress.setText((int) (HomePage.user.getProgress()) + "%");

        return view;
    }

    /**
     * Functie pentru schimbare status elemente curs
     *
     * @param lock2 iconita lacat
     * @param lock3 iconita lacat
     * @param ch2   transparenta capitol
     * @param ch3   transparenta capitol
     */
    private void setClickable(ImageView lock2, ImageView lock3, ImageView ch2, ImageView ch3) {
        int capitol = CoursesFragment.getCapitol();
        if (capitol >= 2) {
            chapter2Locked = false;
            lock2.setVisibility(View.INVISIBLE);
            ch2.setAlpha(1.0f);
            if (capitol >= 3) {
                chapter3Locked = false;
                lock3.setVisibility(View.INVISIBLE);
                ch3.setAlpha(1.0f);
            }
        }
    }

    /**
     * Functie pentru capitolul curent
     *
     * @param index numar capitol
     */
    private void getCapitol(int index) {
        try {
            if (connect != null) {
                PreparedStatement checkUsernameStatement = connect.prepareStatement("SELECT * FROM " + CAPITOLE_TABLE + " WHERE id = ?");
                checkUsernameStatement.setInt(1, index);
                ResultSet resultSet = checkUsernameStatement.executeQuery();
                resultSet.next();
                if (resultSet.getString(1) != null) {
                    numberOfQuestions = resultSet.getInt(5);
                }
            }
        } catch (Exception e) {
            Log.e("getCapitol: ", e.toString());
        }
    }

    /**
     * Functie pentru introducerea datelor in baza de de date
     */
    private void addUserToCoursesUsersTable() {
        try {
            if (connect != null) {
                PreparedStatement checkUsernameStatement = connect.prepareStatement("SELECT * FROM " + COURSESUSERS_TABLE + " WHERE Username = ? AND CourseId = ?");
                checkUsernameStatement.setString(1, HomePage.user.getUsername());
                checkUsernameStatement.setInt(2, course_id);
                ResultSet resultSet = checkUsernameStatement.executeQuery();
                resultSet.next();

                if (resultSet.getString(2) == null) {
                    PreparedStatement createProfileStatement = connect.prepareStatement("INSERT INTO " + COURSESUSERS_TABLE + " (Username, CourseId, Progress, Capitol, LastQuestion) VALUES (?,?, ?, ?, ?)");
                    createProfileStatement.setString(1, HomePage.user.getUsername());
                    createProfileStatement.setInt(2, course_id);
                    createProfileStatement.setInt(3, 0);
                    createProfileStatement.setInt(4, 1);
                    createProfileStatement.setInt(5, 0);
                    createProfileStatement.execute();
                }
            }
        } catch (Exception e) {
            Log.e("addUserToCoursesTable: ", e.toString());
        }
    }

    /**
     * Functie pentru pornirea cursului
     */
    private void getQuestions() {
        int lastQuestion = CoursesFragment.getLastQuestion();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, QuestionnaireFragment.newInstance(lastQuestion + 1, capitol_id, numberOfQuestions));
        transaction.commit();
    }

    /**
     * Metoda pentru numarul de capitole disponibile
     *
     * @return intoarce numarul de capitole sau 0
     */
    private int getNumarCapitole() {
        try {
            if (connect != null) {
                PreparedStatement statement = connect.prepareStatement("SELECT NumarCapitole, NumarIntrebari FROM " + COURSES_TABLE + " WHERE id = ?");
                statement.setInt(1, course_id);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                nrCapitole = resultSet.getInt(1);
                nrIntrebariCurs = resultSet.getInt(2);
                return nrCapitole;
            }
        } catch (Exception e) {
            Log.e("getCourse: ", e.toString());
        }
        return 0;
    }

    public static int getCourse_id() {
        return course_id;
    }

    public static int getNrCapitole() {
        return nrCapitole;
    }

    public static int getNrIntrebariCurs() {
        return nrIntrebariCurs;
    }

}
