package guet.mrx.mycontacts;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class MainActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
	private SearchView searchView;
	private List<String> tables;
	private DAO dao;
	private int current_position;
	private AlertDialog.Builder aboutMe;
	public static final int REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dao = new DAO(this);
		tables = dao.getCurrentTables();
		setContentView(R.layout.activity_main);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		aboutMe = new AlertDialog.Builder(this);
		aboutMe.setTitle(getString(R.string.about))
				.setMessage(getString(R.string.about_me))
				.setPositiveButton(getString(R.string.positive), null);
	}

	private void refreshView() {
		tables = dao.getCurrentTables();
		mTitle = tables.get(current_position);
		getActionBar().setTitle(mTitle);
		mNavigationDrawerFragment.refreshView();
		tables = dao.getCurrentTables();
		updateView(current_position, "");
	}

	private void updateView(int position, String condition) {
		String table = tables.get(position);
		List<Person> data = new ArrayList<Person>();
		switch (table) {
		case "":
			break;
		case "所有联系人":
			for (int i = 1; i < tables.size(); i++) {
				List<Person> d = dao.queryforPersons(tables.get(i),
						condition);
				data.addAll(d);
			}
			break;
		default:
			data = dao.queryforPersons(table, condition);
			break;
		}
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(data, position + 1))
				.commit();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		updateView(position, "");
		this.current_position = position;
	}

	public void onSectionAttached(int number) {
		mTitle = tables.get(number - 1);

	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.menu_main, menu);
			searchView = (SearchView) menu.findItem(R.id.action_search)
					.getActionView();
			searchView.setQueryHint(getString(R.string.action_search));
			searchView.setOnQueryTextListener(new OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String query) {
					updateView(current_position, query);
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					updateView(current_position, newText);
					return false;
				}
			});
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE
				&& resultCode == ImportActivity.REQUEST_CODE) {
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null
					&& (bundle.getInt("isChanged") == 1)) {
				refreshView();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_import:
			startActivityForResult(new Intent(MainActivity.this,
					ImportActivity.class), REQUEST_CODE);
			break;
		case R.id.action_clean:
			deleteTable();
			break;
		case R.id.action_about:
			aboutMe.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void deleteTable() {
		String table = tables.get(current_position);
		switch (table) {
		case "":
			Toast.makeText(this, getString(R.string.notable),
					Toast.LENGTH_SHORT).show();
			break;
		case "所有联系人":
			Toast.makeText(this, getString(R.string.can_no_del),
					Toast.LENGTH_SHORT).show();
			break;
		default:
			dao.cleanTB(table);
			Toast.makeText(this, table + getString(R.string.deleted),
					Toast.LENGTH_SHORT).show();
			current_position = 0;
			refreshView();
			break;
		}
	}

}