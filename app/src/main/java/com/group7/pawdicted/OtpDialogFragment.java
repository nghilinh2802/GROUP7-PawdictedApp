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
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class OtpDialogFragment extends DialogFragment {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button btnVerify;
    private TextView tvResend;
    private CountDownTimer countDownTimer;
    private OnOtpVerifiedListener listener;
    private String sentOtpCode;

    public interface OnOtpVerifiedListener {
        void onOtpVerified(String otp);
    }

    public static OtpDialogFragment newInstance(String otpCode, String phoneNumber) {
        OtpDialogFragment frag = new OtpDialogFragment();
        Bundle args = new Bundle();
        args.putString("otpCode", otpCode);
        frag.setArguments(args);
        return frag;
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
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Material_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_otp, null);

        sentOtpCode = getArguments().getString("otpCode");

        otp1 = view.findViewById(R.id.otp1);
        otp2 = view.findViewById(R.id.otp2);
        otp3 = view.findViewById(R.id.otp3);
        otp4 = view.findViewById(R.id.otp4);
        otp5 = view.findViewById(R.id.otp5);
        otp6 = view.findViewById(R.id.otp6);
        btnVerify = view.findViewById(R.id.btnVerify);
        tvResend = view.findViewById(R.id.tvResend);

        dialog.setContentView(view);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        startTimer(180000);
        setupOtpInputs();

        btnVerify.setOnClickListener(v -> {
            String otp = getOtp();
            if (otp.length() == 6) {
                listener.onOtpVerified(otp);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đủ 6 chữ số OTP", Toast.LENGTH_SHORT).show();
            }
        });

        tvResend.setOnClickListener(v -> Toast.makeText(getContext(), "Vui lòng quay lại và yêu cầu lại OTP", Toast.LENGTH_SHORT).show());

        return dialog;
    }

    private void setupOtpInputs() {
        EditText[] fields = {otp1, otp2, otp3, otp4, otp5, otp6};
        for (int i = 0; i < fields.length; i++) {
            int index = i;
            fields[i].addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < 5) fields[index + 1].requestFocus();
                    else if (s.length() == 0 && index > 0) fields[index - 1].requestFocus();
                }
            });
        }
    }

    private String getOtp() {
        return otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() +
                otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
    }

    private void startTimer(long duration) {
        tvResend.setEnabled(false);
        countDownTimer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                tvResend.setText("Gửi lại mã khôi phục (" + millisUntilFinished / 1000 + "s)");
            }
            public void onFinish() {
                tvResend.setText("Gửi lại mã khôi phục");
                tvResend.setEnabled(true);
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        if (countDownTimer != null) countDownTimer.cancel();
        super.onDestroy();
    }
}
