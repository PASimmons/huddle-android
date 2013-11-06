package com.huddle.handle.android;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Member;
import com.huddle.handle.client.model.Members;
import com.huddle.handle.client.model.Workspaces;

public class UserActivity extends AbstractHuddleActivity {
	
	public static final String PARENT_WORKSPACE_ID = "parentWorkspaceId";
	public static final String PARENT_WORKSPACE_NAME = "parentWorkspaceName";
	public static final String PARENT_WORKSPACE = "parentWorkspace";
	
	Members ms;
	Member m;
	ImageView picture;
	ImageView contact;
	ImageView email;
	static final int CONTACT = 100;
	static final int EMAIL = 101;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		title = getAndSetViewAndHeader(this, R.layout.user, getString(R.string.user));

		contact = (ImageView)findViewById(R.id.add_contact);
		email = (ImageView)findViewById(R.id.email_contact);
		contact.setOnClickListener(contactListener);
		email.setOnClickListener(emailListener);
		picture = (ImageView)findViewById(R.id.picture);
	}    

	View.OnClickListener emailListener = new View.OnClickListener()
	{
		public void onClick(View view) {
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			String[] recipients = new String[]{m.getEmail(), "",};
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
			emailIntent.setType("text/plain");
			startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."), EMAIL);
		}
	};

	View.OnClickListener contactListener = new View.OnClickListener()
	{
		public void onClick(View view) {
	
			Intent intent = new Intent(Intent.ACTION_INSERT);
			intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
			
			if (m != null) {
				intent.putExtra(ContactsContract.Intents.Insert.NAME, m.getDisplayName());
				intent.putExtra(ContactsContract.Intents.Insert.EMAIL, m.getEmail());
				startActivityForResult(intent, CONTACT);
			}
		}
	};
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
	  super.onActivityResult(reqCode, resultCode, data);

	  switch (reqCode) {
	    case (CONTACT) :
	      if (resultCode == Activity.RESULT_OK) {
	    	  userNotify(m.getDisplayName() + " added to Contacts");
	      } else {
	    	  userNotify(m.getDisplayName() + " not added to Contacts");
	      }
	      break;
	    case (EMAIL) :
	      if (resultCode == Activity.RESULT_OK) {
	    	  userNotify("Email sent");
	      } else {
	    	  userNotify("Email not sent");
	      }
	      break;
	  }
	}

	public void run() {

		try {
			if (getIntent().getExtras() != null && getIntent().getExtras().getString("targetId") != null) {
				String targetId = getIntent().getExtras().getString("targetId");
				String workspaceId = getIntent().getExtras().getString("workspaceId");
				ms = new Workspaces(true).findWorkspace(workspaceId).getMembers(true);
				m = ms.findMember(targetId);
				if (m == null) {
					ms.refresh();
					m = ms.findMember(targetId);
				}
			} else if (PeopleActivity.clickedParent == null) {
				ms = new Members(WorkspacesActivity.clickedParent, true);
				Log.d(this.getClass().getName(), "Got members for workspace");
				if (ms != null) {
					m = ms.findMember(WhatsNewActivity.clickedItem.getTargetId());
				}
			} else {
				m = PeopleActivity.clickedParent;
			}
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception getting Members " + e.getMessage());

			networkError(e);
		}

		postToGui();
	}


	@Override
	void updateGui() {
		if (m != null) {
			TextView name = (TextView)findViewById(R.id.username);
			name.setText(m.getDisplayName());
			name = (TextView)findViewById(R.id.username2);
			name.setText(m.getDisplayName());
			name = (TextView)findViewById(R.id.email);
			name.setText(m.getEmail());
			ImageView contact = (ImageView)findViewById(R.id.add_contact);
			contact.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.addressbook));
			ImageView email = (ImageView)findViewById(R.id.email_contact);
			email.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.email));

			Thread pictureThread = new Thread() {
				public void run() {
					ImageView picture = (ImageView)findViewById(R.id.picture);
					picture.setImageBitmap(m.getBitmap());
				}
			};
			guiThread.post(pictureThread);				
		}
	}

	@Override
	void clear() {
		
	}
}

