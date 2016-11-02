package com.rushabh.meena;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText etQuery;

    List<ApplicationInfo> list;

    @BindView(R.id.recycler_contact)
    RecyclerView contactScroller;

    @BindView(R.id.tv_calculation_result)
    TextView tvCalculationResult;

    ContactsAdapter contactsAdapter;

    ContentResolver resolver;

    int maxHeightOfContactScroller;
    int maxHeightForResult;
    Handler handler=new Handler();
    private final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

    private String SELECTION =
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? OR " +
                    ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        etQuery = (EditText) findViewById(R.id.et_text_query);


        PackageManager packageManager = getPackageManager();
        list = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        resolver = getContentResolver();


        maxHeightForResult=Utility.dpToPx(300);
        maxHeightOfContactScroller=Utility.dpToPx(400);
        contactsAdapter = new ContactsAdapter(null, this);

        contactScroller.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        contactScroller.setAdapter(contactsAdapter);

        etQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                handler.removeCallbacks(searchRunnable);
                handler.postDelayed(searchRunnable,100);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    void applySearchAlgo(String query) {
        if (query.trim().length() > 0) {
            boolean isMathematical = isMathematical(query);
            if (isMathematical) {
                showCalculation(solveEquation(query) + "");
            } else {
                hideResult();
            }
            searchContacts(query);

        } else {
            hideResult();
            hideContacts();
        }
    }

    private void hideContacts() {
//        hideWithAnimation(contactScroller,maxHeightOfContactScroller);
    }

    void hideWithAnimation(final View view, int fromHeight){
        if(view.getLayoutParams().height>0){
            final ValueAnimator va = ValueAnimator.ofInt(fromHeight, 0);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {

                    Integer value = (Integer) animation.getAnimatedValue();
                    view.getLayoutParams().height = value;
                    view.requestLayout();

                }
            });
            va.setInterpolator(new AnticipateOvershootInterpolator());
            va.start();
        }
    }

    void hideResult() {
        hideWithAnimation(tvCalculationResult,300);
    }

    void showWithAnimation(final View view, int maxHeight){
        if (tvCalculationResult.getHeight() == 0) {
            final ValueAnimator va = ValueAnimator.ofInt(0, maxHeight);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {

                    Integer value = (Integer) animation.getAnimatedValue();
                    view.getLayoutParams().height = value;
                    view.requestLayout();

                }
            });

            va.setInterpolator(new AnticipateOvershootInterpolator());
            va.start();
        }
    }

    void searchContacts(String query) {
        if(getLoaderManager().getLoader(0)==null){
            getLoaderManager().initLoader(0, null, this);
        }
        else{
            getLoaderManager().restartLoader(0, null, this);
        }


//        showWithAnimation(contactScroller,maxHeightOfContactScroller);
    }

    boolean isMathematical(String searchQuery) {
        String regex = "^([-+/*]?\\d+(\\.\\d+)?)*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(searchQuery);
        return m.matches();
    }


    void showCalculation(String result) {

        tvCalculationResult.setText(result);
        showWithAnimation(tvCalculationResult,300);
    }

    BigDecimal solveEquation(String query) {

        BigDecimal result = null;
        Expression expression = new Expression(query);
        result = expression.eval();
        return result;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String query = "%" + etQuery.getText().toString() + "%";

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        return new CursorLoader(this, uri, PROJECTION, SELECTION,
                new String[]{query, query}, null);

    }

    Runnable searchRunnable=new Runnable() {
        @Override
        public void run() {
            applySearchAlgo(etQuery.getText().toString());
        }
    };
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor applicationInfo) {
        contactsAdapter.changeCursor(applicationInfo);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
