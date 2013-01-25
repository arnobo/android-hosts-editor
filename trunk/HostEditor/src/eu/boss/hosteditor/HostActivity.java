package eu.boss.hosteditor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class HostActivity extends SherlockActivity implements OnClickListener {

	boolean isNew;
	EditText etHost, etIp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_host);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		isNew = getIntent().getExtras().getBoolean(Config.IS_NEW);
		etHost = (EditText) findViewById(R.id.etHost);
		etIp = (EditText) findViewById(R.id.etIp);

		Button btnConfirm = (Button) findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		Button btnDelete = (Button) findViewById(R.id.btnDelete);
		btnCancel.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnDelete.setOnClickListener(this);

		if (!isNew) {
			etHost.setText(getIntent().getExtras().getString(Config.HOST)
					.replaceAll("\\s", ""));
			etIp.setText(getIntent().getExtras().getString(Config.IP)
					.replaceAll("\\s", ""));
		} else
			btnDelete.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConfirm:
			String host = etHost.getText().toString();
			String ip = etIp.getText().toString();
			try {
				if (host.compareTo("") == 0)
					throw new Exception(getString(R.string.invalidHostKey));

				if ((!ip
						.matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$"))
						|| (ip.compareTo("") == 0))
					throw new Exception(getString(R.string.invalidIPKey));

				Intent intent = new Intent();
				intent.putExtra(Config.HOST, host);
				intent.putExtra(Config.IP, ip);
				setResult(RESULT_OK, intent);
				finish();

			} catch (Exception e) {
				displayErrorMessage(e.getMessage());
			}
			break;
		case R.id.btnCancel:
			finish();
			break;
		case R.id.btnDelete:
			displayPopupMessage(getString(R.string.confirmDeleteMsg));

			break;
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// This uses the imported MenuItem from ActionBarSherlock
		if (item.getTitle().toString().compareTo(getTitle().toString()) == 0)
			finish();
		return true;
	}

	public void displayPopupMessage(String message) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(getString(R.string.confirmKey));
		adb.setMessage(message);
		adb.setNegativeButton(getString(R.string.cancelKey), null);
		adb.setPositiveButton(getString(R.string.okKey),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						setResult(Config.RESULT_DELETE, intent);
						finish();
					}
				});

		adb.show();
	}

	public void displayErrorMessage(String message) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(getString(R.string.errorKey));
		adb.setMessage(message);
		adb.setPositiveButton(getString(R.string.okKey), null);
		adb.show();
	}

}
