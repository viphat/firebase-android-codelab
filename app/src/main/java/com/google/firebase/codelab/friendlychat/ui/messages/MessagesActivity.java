/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat.ui.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.codelab.friendlychat.R;
import com.google.firebase.codelab.friendlychat.ui.base.BaseActivity;
import com.google.firebase.codelab.friendlychat.ui.signin.SignInActivity;
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessagesActivity extends BaseActivity
    implements GoogleApiClient.OnConnectionFailedListener {

  private DatabaseReference mFirebaseDatabaseReference;
  private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;

  public static Intent newIntent(Context context) {
    return new Intent(context, SignInActivity.class);
  }

  private static final String TAG = "MainActivity";
  public static final String MESSAGES_CHILD = "messages";
  public static final String ANONYMOUS = "anonymous";

  private String mUsername;
  private String mPhotoUrl;
  private FirebaseAuth mFirebaseAuth;

  private Button mSendButton;
  private RecyclerView mMessageRecyclerView;
  private LinearLayoutManager mLinearLayoutManager;
  private ProgressBar mProgressBar;
  private EditText mMessageEditText;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    // Initialize ProgressBar and RecyclerView.
    mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
    mLinearLayoutManager = new LinearLayoutManager(this);
    mLinearLayoutManager.setStackFromEnd(true);
    mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage,
        MessageViewHolder>(
        FriendlyMessage.class,
        R.layout.item_message,
        MessageViewHolder.class,
        mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

      @Override
      protected void populateViewHolder(MessageViewHolder viewHolder,
                                        FriendlyMessage friendlyMessage, int position) {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        viewHolder.bind(friendlyMessage);
      }
    };

    mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override
      public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        int friendlyMessageCount = mFirebaseAdapter.getItemCount();
        int lastVisiblePosition =
            mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
        // If the recycler view is initially being loaded or the
        // user is at the bottom of the list, scroll to the bottom
        // of the list to show the newly added message.
        if (lastVisiblePosition == -1 ||
            (positionStart >= (friendlyMessageCount - 1) &&
                lastVisiblePosition == (positionStart - 1))) {
          mMessageRecyclerView.scrollToPosition(positionStart);
        }
      }
    });


    mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
    mMessageRecyclerView.setAdapter(mFirebaseAdapter);

    mMessageEditText = (EditText) findViewById(R.id.messageEditText);

    mMessageEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().trim().length() > 0) {
          mSendButton.setEnabled(true);
        } else {
          mSendButton.setEnabled(false);
        }
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });

    mSendButton = (Button) findViewById(R.id.sendButton);
    mSendButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(),
            mUsername,
            mPhotoUrl
        );
        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
        mMessageEditText.setText("");
      }
    });
    
  }

  @Override
  public void onStart() {
    super.onStart();
    // Check if user is signed in.
    // TODO: Add code to check if user is signed in.
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.sign_out_menu:
        mFirebaseAuth.signOut();
        mUsername = ANONYMOUS;
        startActivity(new Intent(this, SignInActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    // An unresolvable error has occurred and Google APIs (including Sign-In) will not
    // be available.
    Log.d(TAG, "onConnectionFailed:" + connectionResult);
    Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
  }

}
