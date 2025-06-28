//package com.group7.pawdicted;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.util.Patterns;
//import android.view.View;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.google.android.gms.auth.api.signin.*;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.*;
//import com.google.firebase.firestore.*;
//import com.group7.pawdicted.mobile.models.Customer;
//
//import java.util.*;
//
//public class SignupActivity extends AppCompatActivity implements SuccessSignupDialogFragment.OnSignupListener {
//    private static final int RC_GOOGLE_SIGN_IN = 1001;
//    private static final String TAG = "SIGNUP";
//
//    private EditText edtUsername, edtEmail, edtPhone, edtPassword;
//    CheckBox chkAgree;
//    private CallbackManager fbCallbackManager;
//    private GoogleSignInClient googleSignInClient;
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
//
//        edtUsername = findViewById(R.id.edtUsername);
//        edtEmail    = findViewById(R.id.edtEmail);
//        edtPhone    = findViewById(R.id.edtPhone);
//        edtPassword = findViewById(R.id.edtEnterPassword);
//        chkAgree = findViewById(R.id.ckbAgree);
//
//        findViewById(R.id.btnLogin).setOnClickListener(v -> registerUser());
//
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//        setupFacebook();
//        setupGoogle();
//    }
//
//    private void setupFacebook() {
//        fbCallbackManager = CallbackManager.Factory.create();
//        findViewById(R.id.imgFacebook).setOnClickListener(v -> {
//            LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<>() {
//                @Override public void onSuccess(LoginResult res) { firebaseAuthWithFacebook(res.getAccessToken()); }
//                @Override public void onCancel() {}
//                @Override public void onError(FacebookException e) {
//                    Toast.makeText(SignupActivity.this, "FB lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
//        });
//    }
//
//    private void setupGoogle() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        googleSignInClient = GoogleSignIn.getClient(this, gso);
//        findViewById(R.id.imgGoogle).setOnClickListener(v -> {
//            googleSignInClient.signOut().addOnCompleteListener(t -> {
//                startActivityForResult(googleSignInClient.getSignInIntent(), RC_GOOGLE_SIGN_IN);
//            });
//        });
//    }
//
//    private void registerUser() {
//        String u = edtUsername.getText().toString().trim();
//        String e = edtEmail.getText().toString().trim();
//        String p = edtPhone.getText().toString().trim();
//        String pass = edtPassword.getText().toString().trim();
//
//        if (u.isEmpty()||e.isEmpty()||p.isEmpty()||pass.isEmpty()) {
//            Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show(); return;
//        }
//        if (!chkAgree.isChecked()) {
//            Toast.makeText(this, "Bạn phải đồng ý với điều khoản!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
//            Toast.makeText(this,"Email không hợp lệ!", Toast.LENGTH_SHORT).show(); return;
//        }
//        if (!p.matches("\\d+")) {
//            Toast.makeText(this,"Phone phải là số!", Toast.LENGTH_SHORT).show(); return;
//        }
//        if (pass.length() < 6) {
//            Toast.makeText(this,"Password ≥ 6 ký tự!", Toast.LENGTH_SHORT).show(); return;
//        }
//
//        db.collection("customers")
//                .whereEqualTo("customer_email", e).get()
//                .addOnSuccessListener(q -> {
//                    if (!q.isEmpty()) {
//                        Toast.makeText(this,"Email đã tồn tại!", Toast.LENGTH_SHORT).show(); return;
//                    }
//                    db.collection("customers")
//                            .whereEqualTo("phone_number", p).get()
//                            .addOnSuccessListener(q2 -> {
//                                if (!q2.isEmpty()) {
//                                    Toast.makeText(this,"Phone đã tồn tại!", Toast.LENGTH_SHORT).show(); return;
//                                }
//                                createFirebaseUser(u,e,p,pass);
//                            });
//                });
//    }
//
//    private void createFirebaseUser(String u, String e, String p, String pass) {
//        mAuth.createUserWithEmailAndPassword(e,pass)
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Toast.makeText(this,"Signup lỗi: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    FirebaseUser f = mAuth.getCurrentUser();
//                    if (f==null) return;
//
//                    Customer cust = new Customer(
//                            f.getUid(), u, e, u, p, "", "Male", new Date(), new Date(), "", "Customer"
//                    );
//
//                    db.collection("customers").document(f.getUid())
//                            .set(cust)
//                            .addOnSuccessListener(a -> showSuccessDialog())
//                            .addOnFailureListener(err -> Toast.makeText(this,"Lỗi lưu: "+err.getMessage(),Toast.LENGTH_SHORT).show());
//                });
//    }
//
//    private void firebaseAuthWithGoogle(String idToken) {
//        AuthCredential cred = GoogleAuthProvider.getCredential(idToken,null);
//        mAuth.signInWithCredential(cred)
//                .addOnCompleteListener(this, t -> {
//                    if (t.isSuccessful()) saveSocialUser(mAuth.getCurrentUser());
//                    else Toast.makeText(this,"Google login lỗi",Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void firebaseAuthWithFacebook(AccessToken token) {
//        AuthCredential cred = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(cred)
//                .addOnCompleteListener(t -> {
//                    if (t.isSuccessful()) saveSocialUser(mAuth.getCurrentUser());
//                    else Toast.makeText(this,"FB login lỗi",Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void saveSocialUser(FirebaseUser user) {
//        if (user==null) return;
//        String uid = user.getUid(), e = user.getEmail();
//
//        db.collection("customers")
//                .whereEqualTo("customer_email", e).get()
//                .addOnSuccessListener(q -> {
//                    if (!q.isEmpty()) {
//                        Toast.makeText(this,"Email đã tồn tại!",Toast.LENGTH_SHORT).show();
//                        mAuth.signOut();
//                        googleSignInClient.signOut();
//                        return;
//                    }
//                    Customer cust = new Customer(
//                            uid,
//                            user.getDisplayName(),
//                            e,
//                            user.getDisplayName(),
//                            user.getPhoneNumber()!=null?user.getPhoneNumber():"",
//                            "",
//                            "Male",
//                            new Date(),
//                            new Date(),
//                            user.getPhotoUrl()!=null?user.getPhotoUrl().toString():"",
//                            "Customer"
//                    );
//                    db.collection("customers").document(uid)
//                            .set(cust)
//                            .addOnSuccessListener(a -> showSuccessDialog())
//                            .addOnFailureListener(err -> Toast.makeText(this,"Lưu social lỗi: "+err.getMessage(),Toast.LENGTH_SHORT).show());
//                });
//    }
//
//    @Override
//    protected void onActivityResult(int req,int res,@Nullable Intent data) {
//        super.onActivityResult(req,res,data);
//        fbCallbackManager.onActivityResult(req,res,data);
//        if (req==RC_GOOGLE_SIGN_IN && data!=null) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                firebaseAuthWithGoogle(task.getResult(ApiException.class).getIdToken());
//            } catch (Exception e) {
//                Log.e(TAG,"Google sign-in error",e);
//            }
//        }
//    }
//
//    private void showSuccessDialog() {
//        new SuccessSignupDialogFragment().show(getSupportFragmentManager(),"SuccessDialog");
//    }
//
//    @Override public void onSignupComplete() {
//        startActivity(new Intent(this,LoginActivity.class));
//        finish();
//    }
//
//    public void open_login(View v){ startActivity(new Intent(this,LoginActivity.class)); finish(); }
//    public void goBack(View v){ finish(); }
//
//    public void open_policy(View view) {
//        startActivity(new Intent(this,PolicynSecurityActivity.class)); finish();
//    }
//}

package com.group7.pawdicted;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.group7.pawdicted.mobile.models.Customer;

import java.util.*;

public class SignupActivity extends AppCompatActivity implements SuccessSignupDialogFragment.OnSignupListener {
    private static final int RC_GOOGLE_SIGN_IN = 1001;
    private static final String TAG = "SIGNUP";

    private EditText edtUsername, edtEmail, edtPhone, edtPassword;
    private CheckBox chkAgree;
    private ProgressBar progressBar;
    private View overlay;
    private CallbackManager fbCallbackManager;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtEnterPassword);
        chkAgree = findViewById(R.id.ckbAgree);
        progressBar = findViewById(R.id.progressBar);
        overlay = findViewById(R.id.overlay);

        findViewById(R.id.btnLogin).setOnClickListener(v -> registerUser());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupFacebook();
        setupGoogle();
    }

    private void setupFacebook() {
        fbCallbackManager = CallbackManager.Factory.create();
        findViewById(R.id.imgFacebook).setOnClickListener(v -> {
            Toast.makeText(SignupActivity.this, R.string.feature_in_development, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.imgGoogle).setOnClickListener(v -> {
            googleSignInClient.signOut().addOnCompleteListener(t -> {
                startActivityForResult(googleSignInClient.getSignInIntent(), RC_GOOGLE_SIGN_IN);
            });
        });
    }

    private void registerUser() {
        String u = edtUsername.getText().toString().trim();
        String e = edtEmail.getText().toString().trim();
        String p = edtPhone.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        if (u.isEmpty() || e.isEmpty() || p.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, R.string.title_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!chkAgree.isChecked()) {
            Toast.makeText(this, R.string.title_agree_terms, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            Toast.makeText(this, R.string.title_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!p.matches("\\d+")) {
            Toast.makeText(this, R.string.title_invalid_phone, Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(this, R.string.title_password_too_short, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        db.collection("customers")
                .whereEqualTo("customer_email", e).get()
                .addOnSuccessListener(q -> {
                    if (!q.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        Toast.makeText(this, R.string.title_email_exists, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.collection("customers")
                            .whereEqualTo("phone_number", p).get()
                            .addOnSuccessListener(q2 -> {
                                if (!q2.isEmpty()) {
                                    progressBar.setVisibility(View.GONE);
                                    overlay.setVisibility(View.GONE);
                                    Toast.makeText(this, R.string.title_phone_exists, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                createFirebaseUser(u, e, p, pass);
                            })
                            .addOnFailureListener(err -> {
                                progressBar.setVisibility(View.GONE);
                                overlay.setVisibility(View.GONE);
                                Toast.makeText(this, R.string.title_check_phone_error + err.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(err -> {
                    progressBar.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);
                    Toast.makeText(this, R.string.title_check_email_error + err.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createFirebaseUser(String u, String e, String p, String pass) {
        progressBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(e, pass)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        Toast.makeText(this, R.string.signup_failed + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    FirebaseUser f = mAuth.getCurrentUser();
                    if (f == null) {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        return;
                    }

                    Customer cust = new Customer(
                            f.getUid(), u, e, u, p, "", "Male", new Date(), new Date(), "", "Customer"
                    );

                    db.collection("customers").document(f.getUid())
                            .set(cust)
                            .addOnSuccessListener(a -> {
                                progressBar.setVisibility(View.GONE);
                                overlay.setVisibility(View.GONE);
                                showSuccessDialog();
                            })
                            .addOnFailureListener(err -> {
                                progressBar.setVisibility(View.GONE);
                                overlay.setVisibility(View.GONE);
                                Toast.makeText(this, "Lỗi lưu: " + err.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        AuthCredential cred = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(cred)
                .addOnCompleteListener(this, t -> {
                    if (t.isSuccessful()) {
                        saveSocialUser(mAuth.getCurrentUser());
                    } else {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        Toast.makeText(this, R.string.signup_google_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        AuthCredential cred = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(cred)
                .addOnCompleteListener(t -> {
                    if (t.isSuccessful()) saveSocialUser(mAuth.getCurrentUser());
                    else Toast.makeText(this, "FB login lỗi", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveSocialUser(FirebaseUser user) {
        if (user == null) {
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            return;
        }
        String uid = user.getUid(), e = user.getEmail();

        db.collection("customers")
                .whereEqualTo("customer_email", e).get()
                .addOnSuccessListener(q -> {
                    if (!q.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                        Toast.makeText(this, R.string.title_email_exists, Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        googleSignInClient.signOut();
                        return;
                    }
                    Customer cust = new Customer(
                            uid,
                            user.getDisplayName(),
                            e,
                            user.getDisplayName(),
                            user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                            "",
                            "Male",
                            new Date(),
                            new Date(),
                            user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "",
                            "Customer"
                    );
                    db.collection("customers").document(uid)
                            .set(cust)
                            .addOnSuccessListener(a -> {
                                progressBar.setVisibility(View.GONE);
                                overlay.setVisibility(View.GONE);
                                showSuccessDialog();
                            })
                            .addOnFailureListener(err -> {
                                progressBar.setVisibility(View.GONE);
                                overlay.setVisibility(View.GONE);
                                Toast.makeText(this, R.string.signup_failed + err.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(err -> {
                    progressBar.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);
                    Toast.makeText(this, R.string.title_invalid_email + err.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);
        if (req == RC_GOOGLE_SIGN_IN && data != null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                firebaseAuthWithGoogle(task.getResult(ApiException.class).getIdToken());
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
                Log.e(TAG, "Google sign-in error", e);
                Toast.makeText(this, R.string.signup_google_failed + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showSuccessDialog() {
        new SuccessSignupDialogFragment().show(getSupportFragmentManager(), "SuccessDialog");
    }

    @Override
    public void onSignupComplete() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void open_login(View v) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void goBack(View v) {
        finish();
    }

    public void open_policy(View view) {
        startActivity(new Intent(this, PolicynSecurityActivity.class));
        finish();
    }
}