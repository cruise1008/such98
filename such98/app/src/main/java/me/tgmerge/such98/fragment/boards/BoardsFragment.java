package me.tgmerge.such98.fragment.boards;

import android.os.Bundle;
import android.view.MenuItem;

import org.apache.http.Header;

import java.util.Arrays;

import me.tgmerge.such98.fragment.base.RecyclerSwipeAdapter;
import me.tgmerge.such98.fragment.base.RecyclerSwipeFragment;
import me.tgmerge.such98.util.APIUtil;
import me.tgmerge.such98.util.HelperUtil;
import me.tgmerge.such98.util.XMLUtil;


public class BoardsFragment extends RecyclerSwipeFragment {

    // consts for mParamId;
    public static final int PARAM_ID_ROOT = 0;
    public static final int PARAM_ID_CUSTOM = -1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramId  The fragment will show board #paramId (PARAM_ID_ROOT and PARAM_ID_CUSTOM is also valid)
     * @param paramPos Items from #paramPos will be shown at the beginning
     * @return A new instance of fragment BoardsFragment.
     */
    public static BoardsFragment newInstance(int paramId, int paramPos) {
        BoardsFragment fragment = new BoardsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_ID, paramId);
        args.putInt(ARG_PARAM_POS, paramPos);
        fragment.setArguments(args);
        return fragment;
    }

    private final XMLUtil.BoardInfo mBoardInfo = new XMLUtil.BoardInfo();

    protected RecyclerSwipeAdapter createAdapter() {
        return new BoardsAdapter(null);
    }

    protected void initialLoad() {

        final BoardsAdapter boardsAdapter = (BoardsAdapter) mAdapter;

        if (mParamId == PARAM_ID_CUSTOM) {
            mBoardInfo.Id = PARAM_ID_CUSTOM;
            mBoardInfo.ChildBoardCount = 1000;
            mBoardInfo.Name = "个人定制";
            getActivity().setTitle(mBoardInfo.Name);
            mPreviousPage = -1;
            mNextPage = 0;
            loadNextPage(boardsAdapter);
        } else if (mParamId == PARAM_ID_ROOT) {
            mBoardInfo.Id = PARAM_ID_ROOT;
            mBoardInfo.ChildBoardCount = 1000;
            mBoardInfo.Name = "主版面";
            getActivity().setTitle(mBoardInfo.Name);
            mPreviousPage = -1;
            mNextPage = 0;
            loadNextPage(boardsAdapter);
        } else {
            new APIUtil.GetBoard(getActivity(), mParamId, new APIUtil.APICallback() {
                @Override
                public void onSuccess(int statCode, Header[] headers, byte[] body) {
                    try {
                        mBoardInfo.parse(new String(body));
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(-1, headers, body, e);
                        return;
                    }

                    // todo prevent rotate screen -> crash
                    if (getActivity() == null) {
                        return;
                    }

                    getActivity().setTitle(mBoardInfo.Name);
                    mRecyclerView.setEnabled(true);
                    setProgressFinished();

                    int maxPage = mBoardInfo.ChildBoardCount / ITEM_PER_PAGE;

                    // set initial loading position
                    if (mParamPos == PARAM_POS_BEGINNING) {
                        mPreviousPage = -1;
                        mNextPage = 0;
                        loadNextPage(boardsAdapter);
                    } else if (mParamPos == PARAM_POS_END) {
                        mPreviousPage = maxPage;
                        mNextPage = maxPage + 1;
                        loadPreviousPage(boardsAdapter);
                    } else {
                        mNextPage = mParamPos/ITEM_PER_PAGE;
                        mPreviousPage = mNextPage - 1;
                        loadNextPage(boardsAdapter);
                    }

                    if(!isLoaded) {
                        mSwipeLayout.setEnabled(true);
                        isLoaded = true;
                    }
                }

                @Override
                public void onFailure(int statCode, Header[] headers, byte[] body, Throwable error) {
                    HelperUtil.errorToast(getActivity(), "Error: " + "code=" + statCode + ", error=" + error.toString());
                }
            }).execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Activity activity = getActivity();
        //int containerId = ((View) mThisView.getParent()).getId();
        return super.onOptionsItemSelected(item);
    }
    // - - -

    protected int getMaxPosToLoad() {
        return mBoardInfo.ChildBoardCount - 1;
    }


    protected void load(final boolean loadPrevious, final RecyclerSwipeAdapter adapter, final int posToLoad, final int sizeToLoad) {

        final BoardsAdapter boardsAdapter = (BoardsAdapter) adapter;

        class Callback implements APIUtil.APICallback {
            @Override
            public void onSuccess(int statCode, Header[] headers, byte[] body) {
                XMLUtil.ArrayOf<XMLUtil.BoardInfo> boardsInfo = new XMLUtil.ArrayOf<>(XMLUtil.BoardInfo.class);
                try {
                    boardsInfo.parse(new String(body));
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(-1, headers, body, e);
                    return;
                }

                if (!loadPrevious && boardsInfo.size() == 0) {
                    mHasNextPage = false;
                }

                if (loadPrevious) {
                    boardsAdapter.appendDataFront(boardsInfo);
                    mPreviousPage --;
                } else {
                    boardsAdapter.appendData(boardsInfo);
                    mNextPage ++;
                }
                setProgressFinished();
            }

            @Override
            public void onFailure(int statCode, Header[] headers, byte[] body, Throwable error) {
                setProgressFinished();
                HelperUtil.errorToast(getActivity(), "Error, code=" + statCode + ", error=" + error.toString());
            }
        }

        HelperUtil.debugToast(getActivity(), "Loading #" + posToLoad + " - #" + (posToLoad + sizeToLoad) + "...");
        if (mParamId == PARAM_ID_ROOT) {
            // Show root board
            new APIUtil.GetRootBoard(getActivity(), posToLoad, null, sizeToLoad, new Callback()).execute();

        } else if (mParamId == PARAM_ID_CUSTOM) {
            // Show custom boards
            new APIUtil.GetCustomBoardsMe(getActivity(), new APIUtil.APICallback() {
                @Override
                public void onSuccess(int statCode, Header[] headers, byte[] body) {
                    XMLUtil.ArrayOfint boardIdVec = new XMLUtil.ArrayOfint();
                    try {
                        boardIdVec.parse(new String(body));
                    } catch (Exception e) {
                        e.printStackTrace();
                        onFailure(-1, headers, body, e);
                        return;
                    }

                    int[] ids = new int[boardIdVec.values.size()];
                    for (int i = 0; i < boardIdVec.values.size(); i++) {
                        ids[i] = boardIdVec.values.get(i);
                    }
                    int[] rangedIds = Arrays.copyOfRange(ids, posToLoad, posToLoad + sizeToLoad);

                    new APIUtil.GetMultiBoards(getActivity(), rangedIds, 0, null, sizeToLoad, new Callback()).execute();
                }

                @Override
                public void onFailure(int statCode, Header[] headers, byte[] body, Throwable error) {
                    setProgressFinished();
                    HelperUtil.errorToast(getActivity(), "Error, code=" + statCode + ", error=" + error.toString());
                }
            }).execute();

        } else {
            // Show board by id
            new APIUtil.GetSubBoards(getActivity(), mParamId, posToLoad, null, sizeToLoad, new Callback()).execute();
        }
    }
}
