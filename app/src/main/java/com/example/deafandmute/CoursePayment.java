package com.example.deafandmute;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class CoursePayment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    Button QrPay, UpiPay;
    TextInputEditText UpiId;
    TextView QrCounter;
    ImageView QrCode;
    CountDownTimer countDownTimer;

    public CoursePayment() {
        // Required empty public constructor
    }
    public static CoursePayment newInstance(String param1, String param2) {
        CoursePayment fragment = new CoursePayment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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



        UpiPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String upi_id = UpiId.getText().toString();
                if(upi_id.isEmpty()){
                    Toast.makeText(requireContext(), R.string.please_enter_upi_id, Toast.LENGTH_SHORT).show();
                } else {
                    
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
                        }
                    }.start();
                }
            }
        });
        return view;
    }
}