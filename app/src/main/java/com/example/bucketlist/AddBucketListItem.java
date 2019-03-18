package com.example.bucketlist;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

public class AddBucketListItem extends AppCompatActivity {

    private Snackbar mSnackBar;
    private final int DEFAULT_STATUS = 0;
    private TextInputEditText mTitleInput;
    private TextInputEditText mDescriptionInput;
    private Button mAddButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bucket_item);

        mTitleInput = findViewById(R.id.titleInput);
        mDescriptionInput = findViewById(R.id.descriptionInput);
        mAddButton = findViewById(R.id.addItemBtn);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mTitleInput.getText()) || TextUtils.isEmpty(mDescriptionInput.getText())) {
                    mSnackBar = Snackbar.make(v, getString(R.string.fields_required), Snackbar.LENGTH_SHORT);
                    mSnackBar.show();
                } else {
                    BucketListItem bucketListItem = new BucketListItem(mTitleInput.getText().toString(),
                            mDescriptionInput.getText().toString(), DEFAULT_STATUS);

                    Intent data = new Intent();
                    data.putExtra(BucketListActivity.NEW_BUCKETITEM_KEY, bucketListItem);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });
    }
}
