package com.example.safetyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SosSettingsActivity extends AppCompatActivity {

    TextView contact1, contact2, contact3;
    Button doneBtn;

    int PICK_CONTACT1 = 1;
    int PICK_CONTACT2 = 2;
    int PICK_CONTACT3 = 3;

    String name1="", name2="", name3="";
    String number1="", number2="", number3="";

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_settings);

        contact1 = findViewById(R.id.contact1Btn);
        contact2 = findViewById(R.id.contact2Btn);
        contact3 = findViewById(R.id.contact3Btn);
        doneBtn = findViewById(R.id.doneBtn);

        // ✅ Get logged user
        SharedPreferences loginPref =
                getSharedPreferences("LOGIN_PREF", MODE_PRIVATE);

        username = loginPref.getString("username", null);

        // ✅ IMPORTANT FIX
        if(username != null && !username.isEmpty()){
            loadContacts();
        }else{
            // 🔥 New user → empty UI
            contact1.setText("Select Contact");
            contact2.setText("Select Contact");
            contact3.setText("Select Contact");
        }

        contact1.setOnClickListener(v -> pickContact(PICK_CONTACT1));
        contact2.setOnClickListener(v -> pickContact(PICK_CONTACT2));
        contact3.setOnClickListener(v -> pickContact(PICK_CONTACT3));

        doneBtn.setOnClickListener(v -> saveContacts());
    }

    void pickContact(int requestCode){

        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);

        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){

            Cursor cursor = getContentResolver().query(
                    data.getData(), null, null, null, null);

            if(cursor != null && cursor.moveToFirst()){

                int nameIndex = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                int numberIndex = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex).replace(" ","");

                if(requestCode == PICK_CONTACT1){
                    contact1.setText(name);
                    name1 = name;
                    number1 = number;
                }

                if(requestCode == PICK_CONTACT2){
                    contact2.setText(name);
                    name2 = name;
                    number2 = number;
                }

                if(requestCode == PICK_CONTACT3){
                    contact3.setText(name);
                    name3 = name;
                    number3 = number;
                }

                cursor.close();
            }
        }
    }

    void saveContacts(){

        if(username == null || username.isEmpty()){
            Toast.makeText(this,"User error",Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp =
                getSharedPreferences("SOS_PREFS_" + username, MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putString("name1", name1);
        editor.putString("name2", name2);
        editor.putString("name3", name3);

        editor.putString("num1", number1);
        editor.putString("num2", number2);
        editor.putString("num3", number3);

        editor.apply();

        Toast.makeText(this,"SOS Contacts Saved", Toast.LENGTH_SHORT).show();

        finish();
    }

    void loadContacts(){

        SharedPreferences sp =
                getSharedPreferences("SOS_PREFS_" + username, MODE_PRIVATE);

        // 🔥 NEW FIX: if no data → show default
        if(!sp.contains("name1")){
            contact1.setText("Select Contact");
            contact2.setText("Select Contact");
            contact3.setText("Select Contact");
            return;
        }

        name1 = sp.getString("name1", "Select Contact");
        name2 = sp.getString("name2", "Select Contact");
        name3 = sp.getString("name3", "Select Contact");

        number1 = sp.getString("num1", "");
        number2 = sp.getString("num2", "");
        number3 = sp.getString("num3", "");

        contact1.setText(name1);
        contact2.setText(name2);
        contact3.setText(name3);
    }
}