package me.tgmerge.such98.viewholder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.tgmerge.such98.R;
import me.tgmerge.such98.custom.SuchApp;
import me.tgmerge.such98.util.ActivityUtil;
import me.tgmerge.such98.util.HelperUtil;
import me.tgmerge.such98.util.TextUtil;
import me.tgmerge.such98.util.XMLUtil;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView avatar;
    public TextView title;
    public TextView floorInfo;
    public TextView authorName;
    public TextView replyInfo;
    public TextView postTime;
    public TextView content;

    public ImageView imgCopy;
    public ImageView imgReply;
    public ImageView imgQuote;

    public LinearLayout postActionBar;
    public ImageView imgShowPostAction;

    public XMLUtil.TopicInfo data_topicInfo;
    public XMLUtil.PostInfo data_postInfo;


    public PostViewHolder(View itemLayoutView) {
        super(itemLayoutView);
        avatar = (ImageView) itemLayoutView.findViewById(R.id.img);
        title = (TextView) itemLayoutView.findViewById(R.id.text_title);
        floorInfo = (TextView) itemLayoutView.findViewById(R.id.text_floor_info);
        authorName = (TextView) itemLayoutView.findViewById(R.id.text_author_name);
        replyInfo = (TextView) itemLayoutView.findViewById(R.id.text_replyInfo);
        postTime = (TextView) itemLayoutView.findViewById(R.id.text_post_time);
        content = (TextView) itemLayoutView.findViewById(R.id.text_content);
        imgCopy = (ImageView) itemLayoutView.findViewById(R.id.image_copy);
        imgReply = (ImageView) itemLayoutView.findViewById(R.id.image_reply);
        imgQuote = (ImageView) itemLayoutView.findViewById(R.id.image_quote);
        postActionBar = (LinearLayout) itemLayoutView.findViewById(R.id.post_action_bar);
        imgShowPostAction = (ImageView) itemLayoutView.findViewById(R.id.show_post_action);

        // set item click listener
        avatar.setOnClickListener(this);
        imgCopy.setOnClickListener(this);
        imgReply.setOnClickListener(this);
        imgQuote.setOnClickListener(this);
        imgShowPostAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        HelperUtil.generalDebug("PostsFragment", "onClick " + v.toString());
        switch (v.getId()) {
            case R.id.img:
                ActivityUtil.openUserInfoDialog(v.getContext(), data_postInfo.UserName);
                break;
            case R.id.image_copy:
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Post", data_postInfo.Title + "\n" + data_postInfo.Content);
                clipboard.setPrimaryClip(clip);
                HelperUtil.debugToast(SuchApp.getStr(R.string.view_holder_post_copied));
                break;
            case R.id.image_reply:
                String replyTitle = (data_postInfo.Floor == 1) ? "" : SuchApp.getStr(R.string.view_holder_post_reply_title, data_postInfo.UserName, data_postInfo.Floor);
                ActivityUtil.openNewPostDialog(v.getContext(), data_topicInfo.Id, replyTitle, "");
                break;
            case R.id.image_quote:
                String quoteTitle = SuchApp.getStr(R.string.view_holder_post_reply_title, data_postInfo.UserName, data_postInfo.Floor);
                ActivityUtil.openNewPostDialog(v.getContext(), data_topicInfo.Id, quoteTitle, SuchApp.getStr(R.string.view_holder_post_quote_content, data_postInfo.UserName, TextUtil.longTimeString(data_postInfo.Time), data_postInfo.Floor, data_postInfo.Content));
                break;
            case R.id.show_post_action:
                imgShowPostAction.setVisibility(View.GONE);
                postActionBar.setVisibility(View.VISIBLE);
                break;
        }
    }
}