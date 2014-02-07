package net.headstudio.android.managecontacts;

import java.util.Hashtable;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	public TextView outputText;
	public Hashtable<String,String> numbers, emails, emailTypes, notes;
	public Hashtable<String,String> poBoxes, postalCodes, streets, cities, states, countries;
	public Hashtable<String,String> addresses, imNames, imTypes, orgNames;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Init class variables
		numbers = emails = emailTypes = notes = new Hashtable<String, String>();
		addresses = imTypes = imNames = orgNames = new Hashtable<String, String>();
		poBoxes = postalCodes = streets = cities = states = countries = new Hashtable<String, String>();
		
		outputText = (TextView) findViewById(R.id.textView1);
		
		readContacts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
     * Method to show menu options
     */
    @Override 
    public boolean onOptionsItemSelected(MenuItem item){
   	 	if(item.getItemId() == R.id.Email){
   	 		Intent servicios = new Intent(this, MailSenderActivity.class);
   	 		servicios.putExtra("phones", numbers);
   	 		servicios.putExtra("emails", emails);
   	 		servicios.putExtra("ims", imNames);
   	 		startActivity(servicios);
   	 		return true;
   	 	}

    	return super.onOptionsItemSelected(item);
    }
    
    public void readContacts(){
    	
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        Log.e("ManageContacts", "Contacts: " + cur.getCount());
        if (cur.getCount() > 0) {
           while (cur.moveToNext()) {
               String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
               String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
               Log.e("ManageContacts", "id: " + id);
               Log.e("ManageContacts", "name: " + name);
               Log.e("ManageContacts", "int: " + Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))));
               if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                   getPhoneNumbers(cr, id, name);
                   getEmails(cr, id, name);
                   getNote(cr, id, name);
                   //getPostalAddress(cr, id, name);
                   getInstantMessenger(cr, id, name);
                   getOrganizations(cr, id, name);

               }
           }
      }
   }
    
    // Get the contact phone number by the id and pu it into 'numbers' HashMap 
    private void getPhoneNumbers(ContentResolver cr, String id, String name){
        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                               ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                               new String[]{id}, null);
        while (pCur.moveToNext()) {
              String phone = pCur.getString(
                     pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
              System.out.println("phone" + phone);
              Log.e("ManageContacts", "phone: " + phone);
              numbers.put(name, phone);
        }
        pCur.close();
    }
    
    private void getEmails(ContentResolver cr, String id, String name){
    	Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{id}, null);
        while (emailCur.moveToNext()) {
            // This would allow you get several email addresses
            // if the email addresses were stored in an array
            String email = emailCur.getString(
                          emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

            //int type = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
            //String customLabel = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
            //CharSequence CustomemailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(this.getResources(), type, customLabel);

            emails.put(name, email);
            emailTypes.put(name, emailType);
        }
        emailCur.close();
    }
    
    private void getNote(ContentResolver cr, String id, String name){
    	// Get note.......
        String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ?";
        String[] noteWhereParams = new String[]{id,
        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};

         Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, 
                 noteWhere, noteWhereParams, null);
        if (noteCur.moveToFirst()) {
            String note = noteCur.getString(
            noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
            
            notes.put(name, note);
        }
        noteCur.close();
    }
    
    private void getPostalAddress(ContentResolver cr, String id, String name){
    	 //Get Postal Address....

        //String addrWhere = ContactsContract.Data.CONTACT_ID 
    	//        + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
    	//String[] addrWhereParams = new String[]{id,
    	//    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
        Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    null, null, null, null);
        while(addrCur.moveToNext()) {
            String poBox = addrCur.getString(
                         addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
            String street = addrCur.getString(
                         addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
            String city = addrCur.getString(
                         addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
            String state = addrCur.getString(
                         addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
            String postalCode = addrCur.getString(
                         addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
            String country = addrCur.getString(
                         addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
            //String type = addrCur.getString(
            //             addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

            if(!poBox.isEmpty()) poBoxes.put(name, poBox);
            if(!postalCode.isEmpty()) postalCodes.put(name, postalCode);
            if(!street.isEmpty()) streets.put(name, street);
            if(!city.isEmpty()) cities.put(name, city);
            if(!state.isEmpty()) states.put(name, state);
            if(!country.isEmpty()) countries.put(name, country);
        }
        addrCur.close();
    }
    
    private void getInstantMessenger(ContentResolver cr, String id, String name){
    	// Get Instant Messenger.........
        String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " 
        + ContactsContract.Data.MIMETYPE + " = ?";
        String[] imWhereParams = new String[]{id,
            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
        Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                null, imWhere, imWhereParams, null);
        if (imCur.moveToFirst()) {
            String imName = imCur.getString(
                     imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
            String imType;
            imType = imCur.getString(
                     imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
            
            imNames.put(name, imName);
            imTypes.put(name, imType);
        }
        imCur.close();
    }
    
    private void getOrganizations(ContentResolver cr, String id, String name){
    	// Get Organizations.........

        String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] orgWhereParams = new String[]{id,
            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
        Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                    null, orgWhere, orgWhereParams, null);
        if (orgCur.moveToFirst()) {
            String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
            //String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
            orgNames.put(name, orgName);
        }
        orgCur.close();
    }
    

}