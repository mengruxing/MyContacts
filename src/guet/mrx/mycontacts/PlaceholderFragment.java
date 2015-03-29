package guet.mrx.mycontacts;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PlaceholderFragment extends Fragment {
	private static final String ARG_SECTION_NUMBER = "section_number";
	private List<Person> data;

	public static PlaceholderFragment newInstance(List<Person> data,
			int sectionNumber) {
		PlaceholderFragment fragment = new PlaceholderFragment(data);
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public PlaceholderFragment(List<Person> data) {
		this.data = data;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		ListView listView = (ListView) rootView.findViewById(R.id.listview);
		listView.setAdapter(new MyBaseAdapter(getActivity(), data));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String name = ((TextView) view.findViewById(R.id.person_name))
						.getText().toString();
				String number = ((TextView) view
						.findViewById(R.id.person_number)).getText().toString();
				makeDialog(name, number);
			}
		});
		return rootView;
	}

	protected void makeDialog(String name, final String number) {
		AlertDialog.Builder ask = new AlertDialog.Builder(getActivity());
		ask.setTitle(name).setMessage(number);
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_NEGATIVE:
					startActivity(new Intent(Intent.ACTION_CALL,
							Uri.parse("tel:" + number)));
					break;
				case DialogInterface.BUTTON_POSITIVE:
					startActivity(new Intent(Intent.ACTION_VIEW,
							Uri.parse("smsto:" + number)));
					break;
				}
			}
		};
		ask.setNegativeButton(R.string.dialog_call, listener);
		ask.setPositiveButton(R.string.dialog_sms, listener);
		ask.show();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}