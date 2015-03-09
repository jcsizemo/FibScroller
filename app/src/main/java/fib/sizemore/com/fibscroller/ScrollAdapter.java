package fib.sizemore.com.fibscroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// We use a custom adapter because we want custom views in our ListView. Our custom views will
// include a labeling mechanism to denote which Fibonacci number we display, as well as a
// loading wheel when we hit the bottom of the list.
public class ScrollAdapter extends BaseAdapter {

    // This inflater is used to create our custom views.
    LayoutInflater inflater;
    // Our master list for our Fibonacci numbers.
    ArrayList<String> fibs = new ArrayList<>();

    public ScrollAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    // Add our new list of numbers to our master list.
    public void append(ArrayList<String> fibs) {
        this.fibs.addAll(fibs);
        // This function updates our UI to show the new additions.
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fibs.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return fibs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // This view always goes on the end of our list. If the user actually gets there,
        // he/she will see a loading wheel so they don't get discouraged and possibly start crying.
        if (position == fibs.size())
            return inflater.inflate(R.layout.loading_layout,null);

        // Otherwise, we use a custom view for labeling and displaying our Fibonacci number.
        // This view consists of just two TextViews.
        convertView = inflater.inflate(R.layout.fib_layout,null);
        ((TextView) convertView.findViewById(R.id.fibId)).setText("FIBONACCI NUMBER " + position);
        ((TextView) convertView.findViewById(R.id.fibValue)).setText(fibs.get(position));
        return convertView;
    }
}
