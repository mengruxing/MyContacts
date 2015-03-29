package guet.mrx.mycontacts;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyBaseAdapter extends BaseAdapter {
	private Context context;
	private List<Person> persons;

	MyBaseAdapter(Context context, List<Person> persons) {
		this.context = context;
		this.persons = persons;
	}

	@Override
	public int getCount() {
		return persons.size();
	}

	@Override
	public Object getItem(int position) {
		return persons.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = View.inflate(context, R.layout.person_item, null);
		Person person = persons.get(position);
		String name = person.getName();
		char c = ' ';
		for (int i = name.length() - 1; i >= 0; i--) {
			c = name.charAt(i);
			if (!isEn(c))
				break;
		}
		((TextView) item.findViewById(R.id.person_name)).setText(name);
		((TextView) item.findViewById(R.id.person_number)).setText(person
				.getNumber());
		TextView icon = (TextView) item.findViewById(R.id.person_icon);
		icon.setText(c + "");
		icon.setBackgroundColor(Util.material_color[c
				% Util.material_color.length]);
		return item;
	}

	private boolean isEn(char c) {
		if (c >= 65 && c <= 90) {
			return true;
		}
		if (c >= 97 && c <= 122) {
			return true;
		}
		return false;
	}

}