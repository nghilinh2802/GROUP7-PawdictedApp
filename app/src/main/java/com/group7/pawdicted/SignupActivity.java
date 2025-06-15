package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.*;

import com.group7.pawdicted.mobile.models.Customer;

import java.util.ArrayList;
import java.util.Date;

public class SignupActivity extends AppCompatActivity implements SuccessSignupDialogFragment.OnSignupListener {
    private static final int RC_GOOGLE_SIGN_IN = 1001;
    private static final String TAG = "SIGNUP";

    EditText edtUsername, edtEmail, edtPhone, edtPassword;
    CheckBox chkAgree;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    CallbackManager fbCallbackManager;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtEnterPassword);
        chkAgree = findViewById(R.id.ckbAgree);
        findViewById(R.id.btnLogin).setOnClickListener(v -> registerUser());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("customers");

        // --- Facebook setup ---
        fbCallbackManager = CallbackManager.Factory.create();
        ImageButton btnFb = findViewById(R.id.imgFacebook);
        btnFb.setOnClickListener(v -> {
            LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
                @Override public void onSuccess(LoginResult result) {
                    Log.d(TAG, "FB login success: " + result.getAccessToken().getUserId());
                    firebaseAuthWithFacebook(result.getAccessToken());
                }
                @Override public void onCancel() { Log.d(TAG, "FB login canceled"); }
                @Override public void onError(FacebookException error) {
                    Toast.makeText(SignupActivity.this, "FB lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            LoginManager.getInstance().logInWithReadPermissions(this, java.util.Arrays.asList("email", "public_profile"));
        });

        // --- Google setup ---
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        ImageButton btnG = findViewById(R.id.imgGoogle);
        btnG.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        });
    }

    public void open_login(View view) { startActivity(new Intent(this, LoginActivity.class)); }
    public void goBack(View view) { finish(); }

    /** Email/password signup logic (unchanged) **/
    private void registerUser() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!chkAgree.isChecked()) {
            Toast.makeText(this, "Chưa đồng ý điều khoản!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone.matches("[0-9]+")) {
            Toast.makeText(this, "Phone phải là số!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password ≥ 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Duplicate check
        mDatabase.orderByChild("customer_email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapEmail) {
                        if (snapEmail.exists()) {
                            Toast.makeText(SignupActivity.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mDatabase.orderByChild("phone_number").equalTo(phone)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override public void onDataChange(@NonNull DataSnapshot snapPhone) {
                                        if (snapPhone.exists()) {
                                            Toast.makeText(SignupActivity.this, "Phone đã dùng!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        createFirebaseUser(username, email, phone, password);
                                    }
                                    @Override public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "Check phone err", error.toException());
                                    }
                                });
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Check email err", error.toException());
                    }
                });
    }

    private void createFirebaseUser(String u, String e, String p, String pass) {
        mAuth.createUserWithEmailAndPassword(e, pass).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            FirebaseUser f = mAuth.getCurrentUser();
            if (f == null) return;
            String uid = f.getUid();

            Customer cust = new Customer(uid, u, e, u, null, p, "", "Male",
                    new Date(), new Date(), "", "Customer",
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            mDatabase.child(uid).setValue(cust)
                    .addOnSuccessListener(a -> showSuccessDialog())
                    .addOnFailureListener(err -> Toast.makeText(this, "Lỗi lưu: " + err.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void showSuccessDialog() {
        new SuccessSignupDialogFragment().show(getSupportFragmentManager(), "SuccessDialog");
    }

    @Override public void onSignupComplete() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /** OAuth callbacks **/
    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);
        fbCallbackManager.onActivityResult(req, res, data);

        if (req == RC_GOOGLE_SIGN_IN && data != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                String idToken = task.getResult(ApiException.class).getIdToken();
                Log.d(TAG, "Google idToken=" + idToken);
                firebaseAuthWithGoogle(idToken);
            } catch (Exception e) {
                Log.e(TAG, "Google sign-in error", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential cred = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(cred).addOnCompleteListener(this, t -> {
            if (t.isSuccessful() && mAuth.getCurrentUser() != null) {
                Log.d(TAG, "Google sign-in success");
                saveSocialUser(mAuth.getCurrentUser());
            } else {
                Log.e(TAG, "GOOGLE_AUTH fail", t.getException());
            }
        });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        AuthCredential cred = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(cred).addOnCompleteListener(this, t -> {
            if (t.isSuccessful() && mAuth.getCurrentUser() != null) {
                Log.d(TAG, "Facebook sign-in success");
                saveSocialUser(mAuth.getCurrentUser());
            } else {
                Log.e(TAG, "FB_AUTH fail", t.getException());
            }
        });
    }

    /**
     * Lưu hoặc bỏ qua nếu social user đã tồn tại.
     */
    private void saveSocialUser(FirebaseUser user) {
        String uid = user.getUid();
        String email = user.getEmail();
        Log.d(TAG, "saveSocialUser: uid=" + uid + " email=" + email);

        // Kiểm tra email đã tồn tại chưa
        mDatabase.orderByChild("customer_email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Nếu đã có tài khoản (có thể email/Google cũ), báo lỗi
                            Toast.makeText(SignupActivity.this,
                                    "Email này đã được đăng ký trước đó!",
                                    Toast.LENGTH_SHORT).show();
                            // Logout Google/Facebook để tránh tự động login lại
                            mAuth.signOut();
                            googleSignInClient.signOut();
                        } else {
                            // Email mới => tạo user
                            Log.d(TAG, "New social user -> writing to DB");
                            Customer cust = new Customer(
                                    uid,
                                    user.getDisplayName(),
                                    email,
                                    user.getDisplayName(),
                                    null,
                                    user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                                    user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "",
                                    "Male",
                                    new Date(),
                                    new Date(),
                                    user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "",
                                    "Customer",
                                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
                            );
                            mDatabase.child(uid)
                                    .setValue(cust)
                                    .addOnSuccessListener(a -> showSuccessDialog())
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "saveSocialUser fail", e);
                                        Toast.makeText(SignupActivity.this,
                                                "Lỗi lưu user: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "saveSocialUser DB check cancelled", error.toException());
                        Toast.makeText(SignupActivity.this,
                                "Lỗi kiểm tra dữ liệu: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
