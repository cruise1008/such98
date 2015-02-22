package me.tgmerge.such98.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import me.tgmerge.such98.R;
import me.tgmerge.such98.fragment.boards.BoardsFragment;
import me.tgmerge.such98.fragment.NavDrawerFragment;

public class ShowBoardsActivity extends ActionBarActivity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavDrawerFragment mNavigationDrawerFragment;

    public static final String INTENT_KEY_ID = "id";
    public static final String INTENT_KEY_START_POS = "pos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_base);

        mNavigationDrawerFragment = (NavDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Set up the content.
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.container, BoardsFragment.newInstance(
                getIntent().getIntExtra(INTENT_KEY_ID, BoardsFragment.PARAM_ID_ROOT),
                getIntent().getIntExtra(INTENT_KEY_START_POS, 0))
        );
        transaction.commit();
    }

}
