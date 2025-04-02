package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CoursePayment extends Fragment {

    private static final String ARG_COURSE_ID = "courseId"; // Correct key name
    private String courseId;
    FirebaseAuth mAuth;
    Button QrPay, UpiPay;
    TextInputEditText UpiId;
    TextView QrCounter;
    ImageView QrCode;
    CountDownTimer countDownTimer;
    RelativeLayout PaymentProgress;
    public CoursePayment(String courseId) {
        this.courseId = courseId;
    }
    public String getCourseId() {
        return this.courseId;
    }


    public static CoursePayment newInstance(String courseId) {
        CoursePayment fragment = new CoursePayment(null);
        Bundle args = new Bundle();
        args.putString(ARG_COURSE_ID, courseId); // Correct key name
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            courseId = getArguments().getString(ARG_COURSE_ID); // Use correct key
        }
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_payment, container, false);

        QrPay = view.findViewById(R.id.btnScanQR);
        QrCode = view.findViewById(R.id.qrCode);
        UpiId = view.findViewById(R.id.upiId);
        UpiPay = view.findViewById(R.id.btnUpi);
        QrCounter = view.findViewById(R.id.qrCounter);
        PaymentProgress = view.findViewById(R.id.paymentProgress);

        UpiPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String upi_id = UpiId.getText().toString();
                if(upi_id.isEmpty()){
                    Toast.makeText(requireContext(), R.string.please_enter_upi_id, Toast.LENGTH_SHORT).show();
                } else {
                    setPaymentProgress();
                }
            }
        });
        QrPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (QrCode.getVisibility() == View.VISIBLE) {
                    QrCode.setVisibility(View.GONE);
                    QrCounter.setVisibility(View.INVISIBLE);
                    countDownTimer.cancel();
                } else {
                    QrCode.setVisibility(View.VISIBLE);
                    QrCounter.setVisibility(View.VISIBLE);
                    countDownTimer = new CountDownTimer(10000, 1000) { // 30 seconds, updating every second
                        @SuppressLint("SetTextI18n")
                        public void onTick(long millisUntilFinished) {
                            QrCounter.setText(millisUntilFinished / 1000 + " " + getString(R.string.seconds_left));
                        }
                        public void onFinish() {
                            QrCounter.setVisibility(View.INVISIBLE);
                            setPaymentProgress();
                        }
                    }.start();
                }
            }
        });
        return view;
    }

    public void setPaymentProgress() {
        PaymentProgress.setVisibility(View.VISIBLE); // Show progress
        closeKeyboard(); // Corrected method call
        String language = getString(R.string.lang);
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses")
                .child(language)
                .child(courseId);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            courseRef.child("enrolledUsers").child(userId).setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.payment_successfull, Toast.LENGTH_SHORT).show();
                    moveToEnrolled();
                } else {
                    Toast.makeText(requireContext(), R.string.payment_failed, Toast.LENGTH_SHORT).show();
                }
                PaymentProgress.setVisibility(View.GONE); // Hide after 5 seconds
            });
        }, 5000); // 5 seconds
    }

    public void closeKeyboard() { // Removed Fragment parameter
        View view = requireView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void moveToEnrolled() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity(); // Get the activity directly
        EnrolledCourse enrolledCourseFragment = new EnrolledCourse();
        Bundle bundle = new Bundle();
        bundle.putString("courseId", courseId);
        enrolledCourseFragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, enrolledCourseFragment)
                .addToBackStack(null) // Enables back navigation
                .commit();
    }
}