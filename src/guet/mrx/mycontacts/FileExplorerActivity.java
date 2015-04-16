package guet.mrx.mycontacts;


import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class FileExplorerActivity extends ListActivity {
	private List<String> items;
	private List<String> paths;
	private TextView mPath;
	public static final int REQUEST_CODE = 3;
	private final String rootPath = Util.getSDDir();
	private String curPath = Util.getSDDir();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_explorer);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		mPath = (TextView) findViewById(R.id.mPath);
		getFileDir(rootPath);
	}

	private void getFileDir(String filePath) {
		mPath.setText(filePath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File curFile = new File(filePath);
		File[] files = curFile.listFiles();
		List<String> fileitems = new ArrayList<String>();
		List<String> filepaths = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (Util.isNormalDir(file)) {
				items.add(file.getName());
				paths.add(file.getPath());
			} else if (Util.isFileFormat(file.getName(), "txt") || Util.isFileFormat(file.getName(), "xls")) {
				fileitems.add(file.getName());
				filepaths.add(file.getPath());
			}
		}
		Collections.sort(items, Collator.getInstance(java.util.Locale.CHINA));
		Collections.sort(paths, Collator.getInstance(java.util.Locale.CHINA));
		Collections.sort(fileitems,
				Collator.getInstance(java.util.Locale.CHINA));
		Collections.sort(filepaths,
				Collator.getInstance(java.util.Locale.CHINA));

		if (!filePath.equals(rootPath)) {
			items.add(0, "b1");
			paths.add(0, rootPath);
			items.add(1, "b2");
			paths.add(1, curFile.getParent());
		}
		items.addAll(fileitems);
		paths.addAll(filepaths);

		setListAdapter(new FileListAdapter(this, items, paths));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String filePath = paths.get(position);
		File file = new File(filePath);
		if (file.isDirectory()) {
			curPath = filePath;
			getFileDir(filePath);
		} else {
			Intent data = new Intent(FileExplorerActivity.this,
					ImportActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("file", filePath);
			data.putExtras(bundle);
			setResult(REQUEST_CODE, data);
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			if (curPath.equals(rootPath)) {
				finish();
			} else {
				curPath = paths.get(1);
				getFileDir(paths.get(1));
			}
		}
		return true;
	}
}