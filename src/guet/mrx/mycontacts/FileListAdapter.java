package guet.mrx.mycontacts;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<String> items;
	private List<String> paths;

	public FileListAdapter(Context context, List<String> it, List<String> pa) {
		mInflater = LayoutInflater.from(context);
		items = it;
		paths = pa;
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.file_row, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		File f = new File(paths.get(position).toString());
		if (items.get(position).toString().equals("b1")) {
			holder.text.setText("返回根目录..");
		} else if (items.get(position).toString().equals("b2")) {
			holder.text.setText("返回上一层..");
		} else {
			holder.text.setText(f.getName());
		}
		return convertView;
	}

	private class ViewHolder {
		TextView text;
	}

}