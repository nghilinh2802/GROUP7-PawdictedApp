package com.group7.pawdicted;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class OtpDialogFragment extends DialogFragment {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button btnVerify;
    private TextView tvResend;
    private CountDownTimer countDownTimer;
    private OnOtpVerifiedListener listener;

    public interface OnOtpVerifiedListener {
        void onOtpVerified(String otp);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnOtpVerifiedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnOtpVerifiedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Material_Dialog); // Sử dụng theme tùy chỉnh
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_otp, null);

        otp1 = view.findViewById(R.id.otp1);
        otp2 = view.findViewById(R.id.otp2);
        otp3 = view.findViewById(R.id.otp3);
        otp4 = view.findViewById(R.id.otp4);
        otp5 = view.findViewById(R.id.otp5);
        otp6 = view.findViewById(R.id.otp6);
        btnVerify = view.findViewById(R.id.btnVerify);
        tvResend = view.findViewById(R.id.tvResend);

        dialog.setContentView(view);

        // Tùy chỉnh Window để hiển thị toàn màn hình với nền đen trong suốt
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Nền trong suốt
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Toàn màn hình
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.7f; // Độ mờ của nền (0.0f đến 1.0f, 0.7f tạo nền đen mờ)
            params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);
        }

        startTimer(180000);
        setupOtpInputs();

        btnVerify.setOnClickListener(v -> {
            String otp = getOtp();
            if (otp.length() == 6) {
                if (listener != null) listener.onOtpVerified(otp);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter a 6-digit code", Toast.LENGTH_SHORT).show();
            }
        });

        tvResend.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            startTimer(180000);
            clearOtpInputs();
            Toast.makeText(getContext(), "Recover code resent", Toast.LENGTH_SHORT).show();
        });

        return dialog;
    }

    private void setupOtpInputs() {
        EditText[] otpFields = {otp1, otp2, otp3, otp4, otp5, otp6};
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < 5) otpFields[index + 1].requestFocus();
                    else if (s.length() == 0 && index > 0) otpFields[index - 1].requestFocus();
                }
            });
        }
    }

    private String getOtp() {
        return otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() +
                otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
    }

    private void clearOtpInputs() {
        otp1.setText(""); otp2.setText(""); otp3.setText(""); otp4.setText("");
        otp5.setText(""); otp6.setText(""); otp1.requestFocus();
    }

    private void startTimer(long millisInFuture) {
        tvResend.setEnabled(false);
        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override public void onTick(long millisUntilFinished) {
                tvResend.setText(String.format("Resend recover code (%d)", millisUntilFinished / 1000));
            }
            @Override public void onFinish() {
                tvResend.setText("Resend recover code"); tvResend.setEnabled(true);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}