package com.group7.pawdicted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.*;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_GOOGLE_SIGN_IN = 1001;
    private static final String TAG = "LOGIN";
    private static final String PREFS = "prefs", KEY_USER = "phone", KEY_REMEMBER = "remember";

    private EditText edtPhone, edtPassword;
    private CheckBox chkRemember;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CallbackManager fbCallbackManager;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_login);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        chkRemember = findViewById(R.id.ckbRememberInfo);
        findViewById(R.id.btnLogin).setOnClickListener(v -> loginWithPhone());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupGoogle();
        setupFacebook();
        loadPrefs();
    }

    private void loadPrefs() {
        SharedPreferences p = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (p.getBoolean(KEY_REMEMBER, false)) {
            edtPhone.setText(p.getString(KEY_USER, ""));
            chkRemember.setChecked(true);
        }
    }

    private void savePrefs(String phone, boolean remember) {
        SharedPreferences.Editor ed = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        if (remember) {
            ed.putString(KEY_USER, phone).putBoolean(KEY_REMEMBER, true);
        } else {
            ed.remove(KEY_USER).remove(KEY_REMEMBER);
        }
        ed.apply();
    }

    private void loginWithPhone() {
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại và mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("customers")
                .whereEqualTo("phone_number", phone)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        Toast.makeText(this, "Không tìm thấy số điện thoại!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DocumentSnapshot userDoc = snapshot.getDocuments().get(0);
                    String email = userDoc.getString("customer_email");

                    if (email == null || email.isEmpty()) {
                        Toast.makeText(this, "Tài khoản không có email hợp lệ!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    savePrefs(phone, chkRemember.isChecked());
                                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    navigateToHome();
                                } else {
                                    Toast.makeText(this, "Sai mật khẩu hoặc email không đúng!", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Login error", task.getException());
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi kết nối đến Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Firestore error", e);
                });
    }

    private void setupGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        ((ImageButton) findViewById(R.id.imgGoogle)).setOnClickListener(v -> {
            googleSignInClient.signOut().addOnCompleteListener(t ->
                    startActivityForResult(googleSignInClient.getSignInIntent(), RC_GOOGLE_SIGN_IN)
            );
        });
    }

    private void setupFacebook() {
        fbCallbackManager = CallbackManager.Factory.create();
        ((ImageButton) findViewById(R.id.imgFacebook)).setOnClickListener(v -> {
            LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<>() {
                @Override public void onSuccess(LoginResult res) { facebookAuth(res.getAccessToken()); }
                @Override public void onCancel() {}
                @Override public void onError(FacebookException e) { Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show(); }
            });
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        });
    }

    private void facebookAuth(AccessToken token) {
        AuthCredential c = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(c).addOnCompleteListener(t -> {
            if (t.isSuccessful()) navigateToHome();
            else Toast.makeText(this, "Facebook login failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void googleAuth(String idToken) {
        AuthCredential c = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(c).addOnCompleteListener(t -> {
            if (t.isSuccessful()) navigateToHome();
            else Toast.makeText(this, "Google login failed", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int rc, int rr, @Nullable Intent data) {
        super.onActivityResult(rc, rr, data);
        fbCallbackManager.onActivityResult(rc, rr, data);
        if (rc == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> t = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                googleAuth(t.getResult(ApiException.class).getIdToken());
            } catch (Exception e) { Log.e(TAG, "Google error", e); }
        }
    }

    private void navigateToHome() {
        startActivity(new Intent(this, HomepageActivity.class));
        finish();
    }

    public void open_signup(View v) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    public void open_forgot_password(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }
}
