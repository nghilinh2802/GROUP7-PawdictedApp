package com.group7.pawdicted;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class SuccessSignupDialogFragment extends DialogFragment {

    private OnSignupListener listener;

    public interface OnSignupListener {
        void onSignupComplete();  // Event listener for signup completion
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnSignupListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnSignupListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Material_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_success_signup, null);

        dialog.setContentView(view);

        // Customize the window to show it with a dim background
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.7f;  // Dim the background
            params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);
        }

        // Make the dialog cancelable when clicking on the background (dimmed area)
        dialog.setCancelable(true); // Allow closing by tapping outside the dialog
        dialog.setCanceledOnTouchOutside(true); // Close dialog when tapping outside of it

        // Handle the Login button
        Button btnLoginWithNewAccount = view.findViewById(R.id.btnLoginWithNewAccount);
        btnLoginWithNewAccount.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSignupComplete();  // Call the event to notify signup completion
            }
            dismiss();  // Close the dialog
        });

        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onSignupComplete();  // Call the event when dialog is dismissed (clicked outside)
        }
    }
}
