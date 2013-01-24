package eu.boss.hosteditor;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
		Intent i = new Intent(MainActivity.this, HostActivity.class);
		i.putExtra(Config.IS_NEW, true);
		startActivityForResult(i, Config.NEW_HOST);
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

			if (!mMultipleItemsSelected) {
				menu.add(getString(R.string.editKey)).setIcon(R.drawable.ic_edit)
						.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
				mMultipleItemsSelected = false;
			}

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
				for (int position = 0; position < adapter.getCheckedState().length; position++) {
					if (adapter.getCheckedState()[position] == true) {
						editHost(position);
						break;
					}
				}
			} else {
				displayPopupMessage(getString(R.string.confirmDeleteMsg));
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
		Intent intent = new Intent(MainActivity.this, HostActivity.class);
		intent.putExtra(Config.IS_NEW, false);
		intent.putExtra(Config.HOST, hostList.get(position).getHostName());
		intent.putExtra(Config.IP, hostList.get(position).getIpAddress());
		startActivityForResult(intent, Config.EDIT_HOST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle extras;
		if (data != null) {
			//get new host added in hostActivity
			extras = data.getExtras();
			if ((requestCode == Config.NEW_HOST) && (resultCode == RESULT_OK)) {
				hostList.add(new Host(extras.getString(Config.IP), extras.getString(Config.HOST)));
			}

			//remove edited host and replace it by the new one
			else if ((requestCode == Config.EDIT_HOST) && (resultCode == RESULT_OK)) {
				hostList.remove(uniqueItemSelected);
				hostList.add(uniqueItemSelected,
						new Host(extras.getString(Config.IP), extras.getString(Config.HOST)));
			}

			else if (resultCode == Config.RESULT_DELETE) hostList.remove(uniqueItemSelected);

			uniqueItemSelected = -1;
			saveHosts();
		}
	}

	/**
	 * Loads hosts from file. No need root because host file is reading mode.
	 * Just opens the file and read it
	 */
	private void loadHosts() {
		try {
			FileInputStream objFile = new FileInputStream(Config.HOST_FILE);
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

	// saves host into host file. Mount the system folder to allows write into the file. 
	// Reverse operation at least to get back the file state
	private void saveHosts() {
		Process p;
		try {
			// Perform su to get root privileges
			p = Runtime.getRuntime().exec("su");

			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("mount -o rw,remount -t yaffs2 /dev/block/mtdblock3 /system\n");
			os.writeBytes("echo '' > /system/etc/hosts\n");
			Iterator<Host> it = hostList.iterator();
			while (it.hasNext()) {
				os.writeBytes("echo '" + it.next().toString() + "' >> /system/etc/hosts\n");
			}
			os.writeBytes("mount -o ro,remount -t yaffs2 /dev/block/mtdblock3 /system\n");
			os.writeBytes("exit\n");
			os.flush();

			try {
				p.waitFor();
				if (p.exitValue() != 255) {
					// Code to run on success
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
	}

	public void displayPopupMessage(String message) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(getString(R.string.confirmKey));
		adb.setMessage(message);
		adb.setPositiveButton(getString(R.string.okKey), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if (!mMultipleItemsSelected) hostList.remove(uniqueItemSelected);
				else for (int position = 0; position < adapter.getCheckedState().length; position++) {
					if (adapter.getCheckedState()[position] == true) {
						hostList.remove(position);
					}
				}
				saveHosts();
			}
		});
		adb.setNegativeButton(getString(R.string.cancelKey), null);
		adb.show();
	}

	public void displayErrorMessage(String message) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(getString(R.string.errorKey));
		adb.setMessage(message);
		adb.setPositiveButton(getString(R.string.okKey), null);
		adb.setNegativeButton(getString(R.string.cancelKey), null);
		adb.show();
	}
}