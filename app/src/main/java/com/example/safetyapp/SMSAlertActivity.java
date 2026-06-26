package com.example.safetyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SMSAlertActivity extends AppCompatActivity {

    Button btnSelect, btnSendSMS, btnSendEmail;
    TextView txtContact;

    String phoneNumber = "";

    // EMAIL SETTINGS
    String receiverEmail = "dharshmika1019@gmail.com";

    String senderEmail = "safetyappalert10@gmail.com";
    String appPassword = "ueot jpqz ehwg wgsy";

    // CONTACT PICKER
    ActivityResultLauncher<Intent> contactPicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode()==RESULT_OK && result.getData()!=null){
                    Cursor cursor = getContentResolver().query(result.getData().getData(),null,null,null,null);
                    if(cursor!=null && cursor.moveToFirst()){
                        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                        String name = cursor.getString(nameIndex);
                        phoneNumber = cursor.getString(numberIndex).replace(" ","");

                        txtContact.setText(name + "\n" + phoneNumber);
                        cursor.close();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsalert);

        txtContact = findViewById(R.id.txtContact);
        btnSelect = findViewById(R.id.btnSelectContact);
        btnSendSMS = findViewById(R.id.btnSendSMS);
        btnSendEmail = findViewById(R.id.btnSendEmail);

        btnSelect.setOnClickListener(v -> pickContact());
        btnSendSMS.setOnClickListener(v -> sendSMS());
        btnSendEmail.setOnClickListener(v -> {
            sendSMS();
            sendEmail();
        });
    }

    // 📞 PICK CONTACT
    void pickContact(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        contactPicker.launch(intent);
    }

    // 📩 SEND SMS
    private void sendSMS() {

        if(phoneNumber.isEmpty()){
            Toast.makeText(this,"Select Contact First",Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 1);
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null,
                "🚨 EMERGENCY! I need help!", null, null);

        Toast.makeText(this, "SMS Sent Successfully ✅", Toast.LENGTH_SHORT).show();
    }

    // 📧 SEND EMAIL
    private void sendEmail() {

        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");

                Session session = Session.getDefaultInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(senderEmail, appPassword);
                            }
                        });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverEmail));
                message.setSubject("🚨 EMERGENCY ALERT");
                message.setText("I am in danger! Please help me.");

                Transport.send(message);

                runOnUiThread(() ->
                        Toast.makeText(this,"Email Sent Successfully ✅",Toast.LENGTH_LONG).show());

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this,"Email Failed ❌",Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}