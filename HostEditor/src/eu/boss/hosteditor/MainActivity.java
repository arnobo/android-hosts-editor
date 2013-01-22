package eu.boss.hosteditor;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity implements OnItemClickListener {

	private static final int NEW_HOST = 0;
	private static final int EDIT_HOST = 1;
	private static final String HOST_FILE = "/etc/hosts";
	private ArrayList<Host> hostList;
	private ListView lvHosts;
	private HostListAdapter adapter;
	private ActionMode mMode;
	private boolean mMultipleItemsSelected = false;
	private int uniqueItemSelected = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lvHosts = (ListView) findViewById(R.id.lvMain);
		loadHosts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(getString(R.string.newKey)).setIcon(R.drawable.ic_new)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent(MainActivity.this, NewHostActivity.class);
		i.putExtra("isNew", true);
		startActivityForResult(i, NEW_HOST);
		return true;
	}

	public void startActionMode() {
		boolean isOneItemSelected = false;
		for (int position = 0; position < adapter.getCheckedState().length; position++) {
			if (adapter.getCheckedState()[position] == true) {
				if (isOneItemSelected) {
					mMultipleItemsSelected = true;
					uniqueItemSelected = -1;
					break;
				} else {
					isOneItemSelected = true;
					uniqueItemSelected = position;
				}
			}
		}
		if (isOneItemSelected) mMode = startActionMode(new ActionModeHostSelected());
		else if (mMode != null) {
			mMode.finish();
		}
	}

	public void toastMessage(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

	private final class ActionModeHostSelected implements ActionMode.Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Used to put dark icons on light action bar

			if (!mMultipleItemsSelected) menu.add(getString(R.string.editKey))
					.setIcon(R.drawable.ic_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			else mMultipleItemsSelected = false;

			menu.add(getString(R.string.deleteKey)).setIcon(R.drawable.ic_garbage)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if (item.getTitle().toString().compareTo(getString(R.string.editKey)) == 0) {
				editHost(0);
			} else {

			}

			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		editHost(position);

	}

	private void editHost(int position) {
		Intent intent = new Intent(MainActivity.this, NewHostActivity.class);
		intent.putExtra("isNew", false);
		startActivityForResult(intent, EDIT_HOST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == NEW_HOST) && (resultCode == RESULT_OK)) saveHosts();

		else if ((requestCode == EDIT_HOST) && (resultCode == RESULT_OK)) {
		}
	}

	private void loadHosts() {
		try {
			Process p;

			FileInputStream objFile = new FileInputStream(HOST_FILE);
			InputStreamReader objReader = new InputStreamReader(objFile);
			BufferedReader objBufferReader = new BufferedReader(objReader);
			String strLine;
			hostList = new ArrayList<Host>();
			while ((strLine = objBufferReader.readLine()) != null) {
				if (strLine.compareTo("") != 0) {
					Host host = new Host(strLine);
					hostList.add(host);
				}
			}

			objFile.close();
			adapter = new HostListAdapter(this, hostList, lvHosts);
			lvHosts.setAdapter(adapter);
			lvHosts.setOnItemClickListener(this);

		} catch (IOException e) {
			// Code to run in input/output exception
			toastMessage(getString(R.string.notRootMsgKey));
		}
	}

	private void saveHosts() {
		Process p;
		try {
			// Preform su to get root privledges
			p = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("chmod 7 etc/hosts\n");

			OutputStream myOutput;
			try {
				myOutput = new BufferedOutputStream(new FileOutputStream(HOST_FILE, true));
				myOutput.write(new String("TEST test").getBytes());
				myOutput.flush();
				myOutput.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				os.writeBytes("chmod 5 etc/hosts\n");
				os.writeBytes("exit\n");
				os.flush();
			}
			try {
				p.waitFor();
				if (p.exitValue() != 255) {
					// Code to run on success
					toastMessage("root");
				} else {
					// Code to run on unsuccessful
					toastMessage(getString(R.string.notRootMsgKey));
				}
			} catch (InterruptedException e) {
				// Code to run in interrupted exception
				toastMessage(getString(R.string.notRootMsgKey));
			}
		} catch (IOException e) {
			// Code to run in interrupted exception
			toastMessage(getString(R.string.notRootMsgKey));
		}
		// while (true)
		// {
		// ArrayList localArrayList;
		// StringBuilder localStringBuilder2;
		// int j;
		// Log.e("HostsEditor", localIOException.getMessage(), localIOException);
		// continue;
		// localArrayList.add(localStringBuilder2.toString());
		// String[] arrayOfString1 = g.a();
		// Process localProcess = Runtime.getRuntime().exec("su");
		// DataOutputStream localDataOutputStream = new
		// DataOutputStream(localProcess.getOutputStream());
		// localDataOutputStream.writeBytes("mount -o rw,remount -t " + arrayOfString1[1] + " " +
		// arrayOfString1[0] + " /system\n");
		// localDataOutputStream.writeBytes("echo '' > /system/etc/hosts\n");
		// Iterator localIterator3 = localArrayList.iterator();
		// while (localIterator3.hasNext())
		// {
		// String str = (String)localIterator3.next();
		// localDataOutputStream.writeBytes("echo '" + str + "' >> /system/etc/hosts\n");
		// }
		// localDataOutputStream.writeBytes("mount -o ro,remount -t " + arrayOfString1[1] + " " +
		// arrayOfString1[0] + " /system\n");
		// localDataOutputStream.writeBytes("exit\n");
		// localDataOutputStream.flush();
		// localProcess.waitFor();
		// if (localProcess.exitValue() == 255)
		// continue;
		// Boolean localBoolean = Boolean.TRUE;
		// continue;
		// label521: int i = j;
		// }
	}
}
