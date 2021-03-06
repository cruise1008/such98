package me.tgmerge.such98.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.http.Header;

import me.tgmerge.such98.R;
import me.tgmerge.such98.custom.SuchApp;
import me.tgmerge.such98.util.APIUtil;
import me.tgmerge.such98.util.ActivityUtil;
import me.tgmerge.such98.util.CacheUtil;
import me.tgmerge.such98.util.HelperUtil;
import me.tgmerge.such98.util.ImageUtil;
import me.tgmerge.such98.util.XMLUtil;

public class NewTopicDialogFragment extends DialogFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_BOARD_ID = "boardId";
    private static final String ARG_PARAM_TITLE = "title";
    private static final String ARG_PARAM_CONTENT = "content";

    // TODO: Rename and change types of parameters
    private int mParamBoardId;
    private String mParamTitle;
    private String mParamContent;

    View mThisView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static NewTopicDialogFragment newInstance(int paramBoardId, String paramTitle, String paramContent) {
        NewTopicDialogFragment fragment = new NewTopicDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_BOARD_ID, paramBoardId);
        args.putString(ARG_PARAM_TITLE, paramTitle);
        args.putString(ARG_PARAM_CONTENT, paramContent);
        fragment.setArguments(args);
        return fragment;
    }

    public NewTopicDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamBoardId = getArguments().getInt(ARG_PARAM_BOARD_ID);
            mParamTitle = getArguments().getString(ARG_PARAM_TITLE);
            mParamContent = getArguments().getString(ARG_PARAM_CONTENT);
        }
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mThisView = inflater.inflate(R.layout.dialog_fragment_new_topic, container, false);

        // clicking outside won't close dialog
        getDialog().setCanceledOnTouchOutside(false);

        // Register on click listener
        mThisView.findViewById(R.id.new_post_send).setOnClickListener(this);

        return mThisView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mParamTitle != null) {
            ((EditText) mThisView.findViewById(R.id.new_post_title)).setText(mParamTitle);
        }
        if (mParamContent != null) {
            ((EditText) mThisView.findViewById(R.id.new_post_content)).setText(mParamContent);
        }

        final ImageView imgAvatar = (ImageView) mThisView.findViewById(R.id.new_post_avatar);

        new APIUtil.GetMe(getActivity(), new APIUtil.APICallback() {
            @Override
            public void onSuccess(int statCode, Header[] headers, byte[] body) {
                XMLUtil.UserInfo info = new XMLUtil.UserInfo();
                try {
                    info.parse(new String(body));
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(-1, headers, body, e);
                    return;
                }
                String avaUrl = info.PortraitUrl.startsWith("http") ? info.PortraitUrl : ("http://www.cc98.org/" + info.PortraitUrl);
                CacheUtil.id_avaUrlCache.put(info.Id, avaUrl);
                ImageUtil.setImage(getActivity(), imgAvatar, 80, avaUrl);
            }

            @Override
            public void onFailure(int statCode, Header[] headers, byte[] body, Throwable error) {
                ActivityUtil.defaultOnApiFailure(getActivity(), statCode, headers, body, error);
            }
        }).execute();
    }

    private boolean isLoading = false;

    private final void setProgressLoading() {
        isLoading = true;
        mThisView.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
    }

    private final void setProgressFinished() {
        mThisView.findViewById(R.id.progress_bar).setVisibility(View.GONE);
        isLoading = false;
    }

    private void sendTopic() {
        String topicTitle = ((EditText) mThisView.findViewById(R.id.new_post_title)).getText().toString();
        String topicContent = ((EditText) mThisView.findViewById(R.id.new_post_content)).getText().toString();

        if (topicContent.equals("")) {
            HelperUtil.errorToast(SuchApp.getStr(R.string.fragment_new_topic_warning_empty_content_or_title));
            return;
        }

        if (mParamBoardId <= 0) {
            HelperUtil.errorToast(SuchApp.getStr(R.string.fragment_new_topic_warning_invalid_board_id, mParamBoardId));
            return;
        }

        setProgressLoading();
        new APIUtil.PostBoardTopic(getActivity(), mParamBoardId, topicTitle, topicContent, new APIUtil.APICallback() {
            @Override
            public void onSuccess(int statCode, Header[] headers, byte[] body) {
                HelperUtil.debugToast(SuchApp.getStr(R.string.fragment_new_topic_sent));
                // close dialog
                dismiss();
            }

            @Override
            public void onFailure(int statCode, Header[] headers, byte[] body, Throwable error) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(SuchApp.getStr(R.string.fragment_new_topic_failure_title))
                        .setMessage(SuchApp.getStr(R.string.general_on_api_failure_toast_text, statCode, error.toString()))
                        .setPositiveButton(SuchApp.getStr(R.string.fragment_new_topic_failure_OK_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                setProgressFinished();
            }
        }).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_post_send:
                sendTopic();
        }
    }
}
