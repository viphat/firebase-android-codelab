package com.google.firebase.codelab.friendlychat.ui.messages;

import android.databinding.ObservableField;

import com.google.firebase.codelab.friendlychat.model.FriendlyMessage;
import com.google.firebase.codelab.friendlychat.ui.base.BaseViewModel;

/**
 * Created by viphat on 19/06/2016.
 */
public class MessageViewModel extends BaseViewModel {

  public ObservableField<String> avatar = new ObservableField<>();

  public ObservableField<String> name = new ObservableField<>();

  public ObservableField<String> text = new ObservableField<>();

  public MessageViewModel() {
  }

  public void bind(FriendlyMessage message) {
    name.set(message.getName());
    text.set(message.getText());
    avatar.set(message.getPhotoUrl());
  }

}
