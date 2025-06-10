package com.group7.pawdicted;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SuccessPasswordDialogFragment extends DialogFragment {

    private OnLoginWithNewPasswordListener listener;

    public interface OnLoginWithNewPasswordListener {
        void onLoginWithNewPassword();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnLoginWithNewPasswordListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnLoginWithNewPasswordListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Material_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_success_password, null);

        dialog.setContentView(view);

        // Tùy chỉnh Window để hiển thị toàn màn hình với nền đen mờ
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.7f; // Nền đen mờ
            params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);
        }

        // Xử lý nút Login With New Password
        Button btnLoginWithNewPassword = view.findViewById(R.id.btnLoginWithNewPassword);
        btnLoginWithNewPassword.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLoginWithNewPassword();
            }
            dismiss();
        });

        return dialog;
    }
}