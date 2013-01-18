package eu.boss.hosteditor;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class NewHostActivity extends SherlockActivity implements OnClickListener {

	boolean isNew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_host);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		isNew = getIntent().getExtras().getBoolean("isNew");
		EditText etHost = (EditText) findViewById(R.id.etHost);
		EditText etIp = (EditText) findViewById(R.id.etIp);
		Button btnConfirm = (Button) findViewById(R.id.btnConfirm);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		Button btnDelete = (Button) findViewById(R.id.btnDelete);
		btnCancel.setOnClickListener(this);
		btnConfirm.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConfirm:
			break;
		case R.id.btnCancel:
			break;
		case R.id.btnDelete:
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// This uses the imported MenuItem from ActionBarSherlock
		if (item.getTitle().toString().compareTo(getTitle().toString()) == 0) finish();
		return true;
	}
}
