package com.example.infomanorserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.infomanorserver.Common.Common;
import com.example.infomanorserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

//import Common.Common;
//import Model.User;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSignIn;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword=(MaterialEditText)findViewById(R.id.edtPassword);
        edtPhone=(MaterialEditText)findViewById(R.id.edtPhone);
        btnSignIn=(Button)findViewById(R.id.btnSignIn);

        //Init Firebase
        db=FirebaseDatabase.getInstance();
        users=db.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInUser(edtPhone.getText().toString(),edtPassword.getText().toString());
            }
        });

    }

    private void SignInUser(String phone, String password) {
        final ProgressDialog mDialog= new ProgressDialog(SignIn.this);
        mDialog.setMessage("Please wait...");
        mDialog.show();

        final String localPhone= phone;
        final String localPassword= password;
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(localPhone).exists()){
                    mDialog.dismiss();

                    User user=dataSnapshot.child(localPhone).getValue(User.class);
                    assert user != null;
                    user.setPhone(localPhone);
                    if(Boolean.parseBoolean(user.getIsStaff())) {
                        if(user.getPassword().equals(localPassword)){
                              //Login ok
                            Toast.makeText(SignIn.this,"Login Sucessfull!",Toast.LENGTH_SHORT).show();
                            Intent login= new Intent(SignIn.this,Home.class);
                            Common.currentUser=user;
                            startActivity(login);
                            finish();
                        }
                        else{
                            Toast.makeText(SignIn.this,"Wrong Password!",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(SignIn.this,"Please login with vendor account",Toast.LENGTH_SHORT).show();
                    }


                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this,"User account does not exist in database!",Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
