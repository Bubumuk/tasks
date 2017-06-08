package org.tasks.caldav;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.todoroo.astrid.activity.TaskListActivity;
import com.todoroo.astrid.activity.TaskListFragment;
import com.todoroo.astrid.api.CaldavFilter;
import com.todoroo.astrid.api.Filter;
import com.todoroo.astrid.data.CaldavAccount;

import org.tasks.R;
import org.tasks.injection.FragmentComponent;

import static android.app.Activity.RESULT_OK;
import static org.tasks.caldav.CalDAVSettingsActivity.EXTRA_CALDAV_DATA;

public class CaldavListFragment extends TaskListFragment {

    public static TaskListFragment newCaldavListFragment(CaldavFilter filter, CaldavAccount account) {
        CaldavListFragment fragment = new CaldavListFragment();
        fragment.filter = filter;
        fragment.account = account;
        return fragment;
    }

    private static final String EXTRA_CALDAV_ACCOUNT = "extra_caldav_account";
    private static final int REQUEST_ACCOUNT_SETTINGS = 10101;

    protected CaldavAccount account;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.account = savedInstanceState.getParcelable(EXTRA_CALDAV_ACCOUNT);
        }
    }

    @Override
    protected void inflateMenu(Toolbar toolbar) {
        super.inflateMenu(toolbar);
        toolbar.inflateMenu(R.menu.menu_caldav_list_fragment);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_caldav_list_fragment:
                Intent intent = new Intent(getActivity(), CalDAVSettingsActivity.class);
                intent.putExtra(EXTRA_CALDAV_DATA, account);
                startActivityForResult(intent, REQUEST_ACCOUNT_SETTINGS);
                return true;
            default:
                return super.onMenuItemClick(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ACCOUNT_SETTINGS) {
            if (resultCode == RESULT_OK) {
                TaskListActivity activity = (TaskListActivity) getActivity();
                String action = data.getAction();
                if (CalDAVSettingsActivity.ACTION_DELETED.equals(action)) {
                    activity.onFilterItemClicked(null);
                } else if (CalDAVSettingsActivity.ACTION_RELOAD.equals(action)) {
                    activity.getIntent().putExtra(TaskListActivity.OPEN_FILTER,
                            (Filter) data.getParcelableExtra(TaskListActivity.OPEN_FILTER));
                    activity.recreate();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_CALDAV_ACCOUNT, account);
    }

    @Override
    protected boolean hasDraggableOption() {
        return false;
    }

    @Override
    public void inject(FragmentComponent component) {
        component.inject(this);
    }
}
