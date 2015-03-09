package fib.sizemore.com.fibscroller;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

/*  Greetings! This class is the entry point to the Fibonacci
    scrolling app. As if you didn't already know that.
 */
public class ScrollActivity extends Activity {

    // Load a new page when we are within a threshold's distance from the bottom.
    int threshold = 20;
    // Number of Fibonacci numbers we add per loading sequence.
    int itemsPerPage = 100;
    // Determines what page of numbers we are on. Note that, if we only printed numbers on the
    // screen, we wouldn't need this. We only use this variable to denote which Fibonacci
    // number we are printing.
    int page = 0;

    // Reference to our adapter; we append to this in our async task.
    private ScrollAdapter adapter;

    // Use BigDecimal for precision, although we could use doubles if it didn't matter.
    // Both approaches work, but doubles exchange precision for a little bit of speed.
    // Because doubles are only so many bits long, eventually the precision problems will compound
    // and get worse for higher Fibonacci numbers. However, one would have to scroll for thousands
    // upon thousands of numbers before the precision issue skewed the result to the point
    // where it couldn't be considered a valid approximation. Nevertheless, I went with BigDecimal.
    // I figured it was better to be completely right than mostly right.
    // I could have used BigIntegers, but I've found that this approach is slightly faster,
    // and creating the string for use with my ListView is slightly easier this way.
    BigDecimal f1 = new BigDecimal(0.0, MathContext.UNLIMITED);
    BigDecimal f2 = new BigDecimal(1.0, MathContext.UNLIMITED);

    // This variable denotes how many places we would have to use the decimal point to the right
    // in our variables above to get our actual Fibonacci numbers. Note that I could have
    // used the size() function with BigDecimal or BigInteger. This variable makes things easier
    // since it can be used to reconstruct the Fibonacci number with doubles, and I can use it
    // as an input to the movePointRight function in the BigDecimal class.
    int exponent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

        // Instantiate our adapter and set it to our ListView.
        adapter = new ScrollAdapter(this);
        ListView lv = (ListView) findViewById(R.id.fibView);
        lv.setAdapter(adapter);

        // Instantiate an async task to load a page of Fibonacci numbers. The input argument
        // is simply the starting Fibonacci number for the page.
        new FibonacciTask().execute(0);

        // Scroll listener for the list view. We don't care when the state changes; we only care
        // about the case where any visible items are within our loading threshold.
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // This if statement checks if any visible item is within our loading
                // threshold. If so, we load another page of numbers. The input argument
                // to the async task is the starting Fibonacci number. Remember that this
                // number is only used for labeling; if we only printed the Fibonacci
                // numbers themselves, the async task would take no arguments.
                // This is because we don't need to know what page we are on; the lazy loading
                // procedure is itself iterative and follows the Fibonacci calculation, and our
                // variables f1 and f2 retain state across the application's lifetime.
                if (totalItemCount - (firstVisibleItem + visibleItemCount) <= threshold) {
                    page++;
                    new FibonacciTask().execute(page*itemsPerPage);
                }
            }
        });
    }

    // A helper function to transform Fibonacci variables into String equivalents
    // for display purposes
    String trim(BigDecimal d) {
        if (exponent > 18) return d.toString().substring(0,20) + "e+" + exponent;
        else return Long.toString(d.movePointRight(exponent).longValueExact());
    }

    // Our async task. Loads a new page of numbers to the list view.
    private class FibonacciTask extends AsyncTask<Integer, Void, Void> {

        // Our list of Fibonacci numbers to add. We can't just add to the adapter in
        // doInBackground, because that method executes on a different thread. Only the UI thread
        // can modify UI elements, and Android will throw an exception if we attempt otherwise.
        ArrayList<String> fibs = new ArrayList<>();

        @Override
        protected Void doInBackground(Integer... n) {

            // The first Fibonacci number we are adding. Again, this is only used for labeling.
            int start = n[0];
                // Add a page's worth of numbers.
                for (int i = start; i < start + itemsPerPage; i++) {
                    fibs.add(trim(f1));

                    // Keep our Fibonacci numbers under 10. Update the exponent by one
                    // if f2 exceeds 10. We then divide both f1 and f2 by 10 so they remain
                    // accurate to our calculation.
                    if (f2.doubleValue() >= 10) {
                        exponent++;
                        f1 = f1.movePointLeft(1);
                        f2 = f2.movePointLeft(1);
                    }
                    BigDecimal temp = f1;
                    f1 = f2;
                    f2 = f2.add(temp);
                }
            return null;
        }

        // This method executes on the UI thread, so we are free to update our adapter here.
        @Override
        protected void onPostExecute(Void result) {
            adapter.append(fibs);
        }
    }

}
