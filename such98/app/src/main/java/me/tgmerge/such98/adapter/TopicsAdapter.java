package me.tgmerge.such98.adapter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.tgmerge.such98.R;
import me.tgmerge.such98.custom.SuchApp;
import me.tgmerge.such98.util.TextUtil;
import me.tgmerge.such98.viewholder.TopicViewHolder;
import me.tgmerge.such98.util.XMLUtil;

public class TopicsAdapter extends RecyclerSwipeAdapter<XMLUtil.TopicInfo, TopicViewHolder> {

    private XMLUtil.ArrayOf<XMLUtil.TopicInfo> mData;

    private boolean mIsNeverLoaded = true;
    private SwipeRefreshLayout mSwipeLayout = null;

    public final void setSwipeLayout(SwipeRefreshLayout swipeLayout) {
        mSwipeLayout = swipeLayout;
    }

    public final void appendData(XMLUtil.ArrayOf<XMLUtil.TopicInfo> data) {
        int oldItemCount = 0;
        if (mData == null) {
            mData = data;
        } else {
            oldItemCount = mData.size();
            mData.append(data);
        }
        notifyItemRangeInserted(oldItemCount, data.size());
    }

    public final void appendDataFront(XMLUtil.ArrayOf<XMLUtil.TopicInfo> data) {
        if (mData == null) {
            mData = data;
        } else {
            mData.appendFront(data);
        }
        notifyItemRangeInserted(0, data.size());
    }

    public TopicsAdapter(XMLUtil.ArrayOf<XMLUtil.TopicInfo> data) {
        mData = data;
    }


    @Override
    public TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false);
        return new TopicViewHolder(itemLayoutView);
    }


    @Override
    public void onBindViewHolder(TopicViewHolder viewHolder, int position) {

        if (mIsNeverLoaded) {
            if (mSwipeLayout != null) {
                mSwipeLayout.setEnabled(true);
            }
            mIsNeverLoaded = false;
        }

        XMLUtil.TopicInfo dataItem = mData.get(position);

        viewHolder.title.setText(dataItem.Title);
        viewHolder.authorName.setText(dataItem.AuthorName);
        viewHolder.postTime.setText(" - " + TextUtil.abbrTimeString(dataItem.CreateTime));
        viewHolder.data_topicId = dataItem.Id;

        if (dataItem instanceof XMLUtil.HotTopicInfo) {
            XMLUtil.HotTopicInfo hotItem = (XMLUtil.HotTopicInfo) dataItem;
            viewHolder.boardName.setVisibility(View.VISIBLE);
            viewHolder.boardName.setText(hotItem.BoardName);
            viewHolder.replyInfo.setText(SuchApp.getStr(R.string.adapter_topics_reply_info_hot, hotItem.ParticipantCount, hotItem.ReplyCount, hotItem.HitCount));
            viewHolder.icon.setImageResource(R.drawable.ic_comment_fire_outline_white_36dp);
        } else {
            viewHolder.replyInfo.setText(SuchApp.getStr(R.string.adapter_topics_reply_info, dataItem.ReplyCount, dataItem.HitCount));
            viewHolder.icon.setImageResource(dataItem.TopState.equals(XMLUtil.TopicInfo.TOPSTATE_NONE)
                    ? R.drawable.ic_comment_text_outline_white_36dp
                    : R.drawable.ic_comment_alert_outline_white_36dp);
        }
    }

    @Override
    public int getItemCount() {
        return (mData == null) ? 0 : mData.size();
    }
}
