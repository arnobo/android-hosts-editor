package eu.boss.hosteditor;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class create an adapter to fill the ListViewHistory
 */
public class HostListAdapter extends BaseAdapter {

	private ArrayList<Host> mHostList;
	private LayoutInflater mInflater;
	private ListView mLvHosts;
	private boolean[] mCheckedState;
	private MainActivity mActivity;

	public HostListAdapter(MainActivity activity, ArrayList<Host> hostList, ListView lvHosts) {
		mInflater = LayoutInflater.from(activity);
		mActivity = activity;
		this.mHostList = hostList;
		this.mLvHosts = lvHosts;
		try {
			mCheckedState = new boolean[hostList.size()];
		} catch (NullPointerException e) {
			mCheckedState = new boolean[0];
		}
	}

	@Override
	public int getCount() {
		return mHostList.size();
	}

	@Override
	public Object getItem(int position) {
		return mHostList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {

		TextView tvName;
		TextView tvIp;
		CheckBox cbSelected;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listviewitem, null);
			holder.cbSelected = (CheckBox) convertView.findViewById(R.id.cbSelected);
			holder.tvIp = (TextView) convertView.findViewById(R.id.tvIp);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvIp.setText(mHostList.get(position).getIpAddress());
		holder.tvName.setText(mHostList.get(position).getHostName());
		holder.cbSelected.setOnCheckedChangeListener(mCheckedChanceChangeListener);
		return convertView;
	}

	public void addAll(ArrayList<Host> list) {
		this.mHostList.addAll(list);
		notifyDataSetChanged();
	}

	public void clear() {
		mHostList.clear();
	}

	public void replace(ArrayList<Host> list) {
		mCheckedState = new boolean[list.size()];
		clear();
		addAll(list);
	}

	private OnCheckedChangeListener mCheckedChanceChangeListener = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// Keeping checkbox state in the array
			final int position = mLvHosts.getPositionForView(buttonView);
			if (position != ListView.INVALID_POSITION) {
				mCheckedState[position] = isChecked;
				mActivity.startActionMode();
			}
		}
	};

	public boolean[] getCheckedState() {
		return mCheckedState;
	}

}