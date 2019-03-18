package com.example.bucketlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BucketListActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener, BucketListAdapter.BucketListListener {

    private RecyclerView rvBucketList;

    private BucketListAdapter mBucketListAdapter;
    private List<BucketListItem> bucketList = new ArrayList<>();

    private Snackbar mSnackBar;
    private BucketListItem mBucketListItem;

    private BucketListRoomDatabase db;
    private Executor executor = Executors.newSingleThreadExecutor();
    private GestureDetector mGestureDetector;

    public static final String NEW_BUCKETITEM_KEY = "newBucketItem";
    public static final int REQUEST_CODE_OK = 200;
    private final int DEFAULT_STATUS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvBucketList = findViewById(R.id.bucketList);
        db = BucketListRoomDatabase.getDatabase(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BucketListActivity.this, AddBucketListItem.class);
                startActivityForResult(intent, REQUEST_CODE_OK);
            }
        });

        rvBucketList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvBucketList.setHasFixedSize(true);
        rvBucketList.addOnItemTouchListener(this);
        rvBucketList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        mBucketListAdapter = new BucketListAdapter(bucketList, this);
        rvBucketList.setAdapter(mBucketListAdapter);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                View child = rvBucketList.findChildViewUnder(e.getX(), e.getY());

                if (child != null) {
                    int adapterPosition = rvBucketList.getChildAdapterPosition(child);

                    mSnackBar = Snackbar.make(findViewById(android.R.id.content), "Item deleted", Snackbar.LENGTH_LONG);
                    mSnackBar.show();

                    deleteBucketListItem(bucketList.get(adapterPosition));
                }
            }
        });

        getAllBucketListItems();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_OK) {
                mBucketListItem = data.getParcelableExtra(NEW_BUCKETITEM_KEY);

                mSnackBar = Snackbar.make(findViewById(android.R.id.content), "Item added", Snackbar.LENGTH_LONG);
                mSnackBar.show();

                insertBucketListItem(mBucketListItem);
            }
        }
    }

    private void updateUI(List<BucketListItem> bucketListItems) {
        bucketList.clear();
        bucketList.addAll(bucketListItems);
        mBucketListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete_item) {
            deleteAllBucketListItems(bucketList);
            Toast.makeText(this, "Items deleted", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        mGestureDetector.onTouchEvent(motionEvent);
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    private void getAllBucketListItems() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final List<BucketListItem> bucketListItems = db.bucketListItemDao().getAllBucketListItems();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(bucketListItems);
                    }
                });
            }
        });
    }

    private void insertBucketListItem(final BucketListItem bucketListItem) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.bucketListItemDao().insert(bucketListItem);
                getAllBucketListItems();
            }
        });
    }

    private void updateBucketListItem(final BucketListItem bucketListItem) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.bucketListItemDao().update(bucketListItem);
                getAllBucketListItems();
            }
        });
    }

    private void deleteBucketListItem(final BucketListItem bucketListItem) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.bucketListItemDao().delete(bucketListItem);
                getAllBucketListItems();
            }
        });
    }

    private void deleteAllBucketListItems(final List<BucketListItem> bucketListItems) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.bucketListItemDao().delete(bucketListItems);
                getAllBucketListItems();
            }
        });
    }

    @Override
    public void onCheckBoxClick(BucketListItem bucketListItem) {
        bucketListItem.setFinished((bucketListItem.getFinished() == DEFAULT_STATUS ? 1 : 0));
        updateBucketListItem(bucketListItem);

        mSnackBar = Snackbar.make(findViewById(android.R.id.content), "Item updated", Snackbar.LENGTH_LONG);
        mSnackBar.show();

        getAllBucketListItems();
    }
}