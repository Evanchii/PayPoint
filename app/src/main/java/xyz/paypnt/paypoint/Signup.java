package xyz.paypnt.paypoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.paypoint.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAut;
    private DatabaseReference fdb;
    private EditText userUsername;
    private EditText userEmail;
    private EditText password;
    private EditText confirmPassword;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.signup);

        mAut=FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance().getReference().child("Users");

        error = (TextView) findViewById(R.id.signup_error);

        Button signIn=(Button)findViewById(R.id.signup_signup);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

    }

    public void Login(View view) {
        startActivity(new Intent(Signup.this, Login.class));
        finish();
    }
    private void SignIn(){
        userUsername=(EditText)findViewById(R.id.signup_username);
        userEmail=(EditText)findViewById(R.id.signup_email);
        password=(EditText)findViewById(R.id.signup_password);
        confirmPassword=(EditText)findViewById(R.id.signup_confirm);

        if (!userUsername.getText().toString().trim().equals("") && !userEmail.getText().toString().trim().equals("") && !password.getText().toString().trim().equals("") && !confirmPassword.getText().toString().trim().equals("") ){
            System.out.print(userUsername.getText());
            if(String.valueOf(password.getText()).equals(String.valueOf(confirmPassword.getText()))){

                SignUpGetterAndSetter sign = new SignUpGetterAndSetter();
                HashMap<String,Object> signUp= new HashMap<>();
                sign.setSignUp(signUp);
                signUp.put("Username",userUsername.getText());
                signUp.put("Email",userEmail.getText());
                signUp.put("Password",password.getText());

                System.out.println("VALUE:\t"+signUp);

                mAut.createUserWithEmailAndPassword(String.valueOf(signUp.get("Email")),String.valueOf(signUp.get("Password"))).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String userID=mAut.getCurrentUser().getUid();
                            DatabaseReference signupDbRef = fdb.child(userID);
                            signupDbRef.child("Username").setValue(String.valueOf(signUp.get("Username")));

                            Intent intent= new Intent(Signup.this,Login.class);
                            startActivity(intent);
                        }
                    }
                });

            }else {
                System.out.println("Error Password");
                error.setText("Password didn't match");
                error.setVisibility(View.VISIBLE);
            }
        }else{
            System.out.println("Error Empty");
            error.setText("Enter all required data!");
            error.setVisibility(View.VISIBLE);
        }

    }
}