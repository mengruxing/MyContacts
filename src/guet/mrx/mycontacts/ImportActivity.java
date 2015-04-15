package guet.mrx.mycontacts;

import java.io.IOException;

import jxl.read.biff.BiffException;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ImportActivity extends Activity implements OnClickListener {
	private EditText tableNameEditText;
	private String tableNameString;
	private EditText filePathEditText;
	private String filePathString;
	private Spinner encodingSpinner;
	private String encodingString;
	private Button explorFileButton;
	private Button startButton;
	private String sd_root_path;
	private AlertDialog.Builder aboutMe;
	private final String[] ENCODING = { "GBK", "UTF-8" };
	public static final int REQUEST_CODE = 2;
	private int isDBChanged = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		sd_root_path = Util.getSDDir() + "/";
		initView();
	}

	private void initView() {
		tableNameEditText = (EditText) findViewById(R.id.table_name);
		filePathEditText = (EditText) findViewById(R.id.file_path);
		explorFileButton = (Button) findViewById(R.id.explore);
		startButton = (Button) findViewById(R.id.start_import);
		encodingSpinner = (Spinner) findViewById(R.id.encoding);
		encodingSpinner.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, ENCODING));
		explorFileButton.setOnClickListener(this);
		startButton.setOnClickListener(this);
		aboutMe = new AlertDialog.Builder(this);
		aboutMe.setTitle(getString(R.string.about))
				.setMessage(getString(R.string.about_me))
				.setPositiveButton(getString(R.string.positive), null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.explore:
			startActivityForResult(new Intent(ImportActivity.this,
					FileExplorerActivity.class), REQUEST_CODE);
			break;
		case R.id.start_import:
			startImport();
			break;
		}
	}

	private boolean startImport() {
		tableNameString = tableNameEditText.getText().toString();
		filePathString = sd_root_path + filePathEditText.getText().toString();
		Log.i("导入数据", tableNameString);
		if (isRightful()) {
			if (Util.checkFilePath(filePathString, "txt")) {
				try {
					Util.importFromTXT(this, tableNameString, filePathString,
							ENCODING[encodingSpinner.getSelectedItemPosition()]);
					isDBChanged = 1;
					showSucceed();
				} catch (IOException e) {
					Toast.makeText(this, getString(R.string.import_failed),
							Toast.LENGTH_SHORT).show();
				}
			}else if (Util.checkFilePath(filePathString, "xls")) {
				try {
					Util.importFromExcel(this, tableNameString, filePathString);
					isDBChanged = 1;
					showSucceed();
				} catch (BiffException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				Toast.makeText(this, getString(R.string.check_file_path), Toast.LENGTH_LONG).show();
			}
		}
		return true;
	}

	private void showSucceed() {
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.complete))
				.setMessage(getString(R.string.complete_message))
				.setNegativeButton(R.string.back,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								backToMain();
							}
						}).setPositiveButton(R.string.stay, null).show();
	}

	private boolean isRightful() {
		if (tableNameString.indexOf(' ') != -1) {
			Toast.makeText(this, getString(R.string.text_hint_table),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (tableNameString.length() == 0) {
			Toast.makeText(this, getString(R.string.table_empty),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (Util.isStringLong(tableNameString.substring(0, 1))) {
			Toast.makeText(this, getString(R.string.number_start),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_import, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			backToMain();
			break;
		case R.id.action_about:
			aboutMe.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void backToMain() {
		Intent data = new Intent(ImportActivity.this, MainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("isChanged", isDBChanged);
		data.putExtras(bundle);
		setResult(REQUEST_CODE, data);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			backToMain();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_CODE == requestCode
				&& FileExplorerActivity.REQUEST_CODE == resultCode) {
			Bundle bundle = data.getExtras();
			if (data != null && bundle != null) {
				filePathEditText.setText(bundle.getString("file").substring(
						sd_root_path.length()));
			}
		}
	}

}