package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import xyz.paypnt.paypoint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference loginDbRef;
    private EditText login_email;
    private EditText login_password;
    private TextView error;
    private Button login;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.login);

        mAuth=FirebaseAuth.getInstance();
        loginDbRef = FirebaseDatabase.getInstance().getReference().child("Users");

        error = (TextView) findViewById(R.id.login_error);
        login=(Button)findViewById(R.id.login_login);
        login.setOnClickListener(view -> Login());
    }

    public void Signup(View view) {
        startActivity(new Intent(Login.this, Signup.class));
        finish();
    }

    private void Login(){
        Log.d(String.valueOf(this), "Logging in");
        ScrollView scr = (ScrollView) findViewById(R.id.login_scr);
        login_email=(EditText)findViewById(R.id.login_email);
        login_password=(EditText)findViewById(R.id.login_password);

        if(!login_email.getText().toString().trim().equals("") && !login_password.getText().toString().trim().equals("")){
            dialog = ProgressDialog.show(Login.this, "Please wait","Logging in...", true);
            mAuth.signInWithEmailAndPassword(String.valueOf(login_email.getText()),String.valueOf(login_password.getText())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Check user exist
                        String userID = mAuth.getCurrentUser().getUid();
                        loginDbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild(userID)){
                                    if(mAuth.getCurrentUser().isEmailVerified()) {
                                        startActivity(new Intent(Login.this, Dashboard.class));
                                        dialog.dismiss();
                                        loginDbRef.removeEventListener(this);
                                        finish();
                                    }
                                    else {
                                        AlertDialog.Builder confEmail = new AlertDialog.Builder(Login.this);
                                        confEmail.setTitle("We sent you an email")
                                                .setMessage("Please check your inbox/spam to confirm your email address.")
                                                .setPositiveButton("OK", (dialog, which) -> mAuth.signOut())
                                                .setNegativeButton("Resend Email", (dialog, which) -> {mAuth.getCurrentUser().sendEmailVerification(); mAuth.signOut();})
                                                .setCancelable(false).show();
                                        dialog.dismiss();
                                    }
                                }else{
                                    System.out.println("You don't have Account yet");
                                    error.setText("Account doesn't exist!");
                                    scr.smoothScrollTo(0,0);
                                    error.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }else {
                        System.out.println("Error Login");
                        error.setText("Wrong Email/Password");
                        scr.smoothScrollTo(0,0);
                        error.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                }
            });
        } else {
            System.out.println("Error Empty");
            error.setText("Enter all required data!");
            scr.smoothScrollTo(0,0);
            error.setVisibility(View.VISIBLE);
        }
    }

    public void forgotPassword(View view) {
        float scale = getResources().getDisplayMetrics().density;

        EditText reset = new EditText(view.getContext());
        reset.setPadding((int) ( 16*scale + 0.5f),0,(int) ( 16*scale + 0.5f),0);
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(view.getContext());
        resetDialog.setTitle("Password Reset");
        resetDialog.setMessage("Enter your email");
        resetDialog.setView(reset);

        resetDialog.setPositiveButton("Reset", (dialog, which) -> {
            String email = reset.getText().toString().trim();
            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> Toast.makeText(Login.this, "Password Reset Email sent!", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> Toast.makeText(Login.this, "An error has occured!", Toast.LENGTH_LONG).show());
        }).setNegativeButton("Cancel", (dialog, which) -> {});
        resetDialog.create().show();
    }
}