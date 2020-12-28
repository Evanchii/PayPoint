package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

        login.setOnLongClickListener(v -> {
            startActivity(new Intent(Login.this, Dashboard.class));
            finish();
            return false;
        });
    }

    public void Signup(View view) {
        startActivity(new Intent(Login.this, Signup.class));
        finish();
    }
    private void Login(){
        error.setVisibility(View.GONE);
        login_email=(EditText)findViewById(R.id.login_email);
        login_password=(EditText)findViewById(R.id.login_password);
        login.setClickable(false);

        if(!login_email.getText().toString().trim().equals("") && !login_password.getText().toString().trim().equals("")){
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
                                    System.out.println("Welcome Dashboard");

                                    Intent intent = new Intent( Login.this ,Dashboard.class);
                                    startActivity(intent);

                                }else{
                                    System.out.println("You don't have Account yet");
                                    error.setText("Account doesn't exist!");
                                    error.setVisibility(View.VISIBLE);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }else {
                        System.out.println("Error Login");
                        error.setText("Wrong Email/Password");
                        error.setVisibility(View.VISIBLE);
                    }
                }
            });

        } else {
            System.out.println("Error Empty");
            error.setText("Enter all required data!");
            error.setVisibility(View.VISIBLE);
        }
        login.setClickable(true);
    }

    public void forgotPassword(View view) {
        EditText reset = new EditText(view.getContext());
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(view.getContext());
        resetDialog.setTitle("Password Reset");
        resetDialog.setMessage("Enter your email");
        resetDialog.setView(reset);

        resetDialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = reset.getText().toString().trim();
                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Login.this, "Password Reset Email sent!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "An error has occured!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        resetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        resetDialog.create().show();
    }
}