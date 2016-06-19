package com.google.firebase.codelab.friendlychat.ui.messages;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.codelab.friendlychat.databinding.ItemMessageBinding;
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by viphat on 19/06/2016.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {
  private final ItemMessageBinding mBinding;

  private final MessageViewModel mViewModel;

  public CircleImageView messengerImageView;

  public MessageViewHolder(View v) {
    super(v);
    mViewModel = new MessageViewModel();
    mBinding = DataBindingUtil.bind(itemView);
    mBinding.setViewModel(mViewModel);
  }

  public void bind(FriendlyMessage msg) {
    mViewModel.bind(msg);
  }
}
