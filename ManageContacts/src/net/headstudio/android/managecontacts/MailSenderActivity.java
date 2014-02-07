package net.headstudio.android.managecontacts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MailSenderActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sender);
        
        // Get extras from parent activity
        Bundle extras = getIntent().getExtras();
        HashMap<String,String> numbers; // Don't use HashTable, use HashMap instead.
        Object o = extras.get("phones");
        numbers = (HashMap<String,String>)o; // That's the reason, HashTable = ClassCastException

        Iterator<Entry<String, String>> it = numbers.entrySet().iterator();
		
        while (it.hasNext()) {
        	Entry<String, String> e = it.next();
        	Log.e("ManageContacts", "INTENT - Name: " + e.getKey());
        	Log.e("ManageContacts", "INTENT - Number: " + e.getValue());
        }
        
        // Click on Send Email button
        final Button send = (Button) this.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) { 
        		Intent i = new Intent(Intent.ACTION_SEND);
        		i.setType("message/rfc822");
        		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"papamicho@gmail.com"});
        		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        		i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        		try {
        		    startActivity(Intent.createChooser(i, "Send mail..."));
        		} catch (android.content.ActivityNotFoundException ex) {
        		    Toast.makeText(MailSenderActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        		}
        	}
        });
    }
}