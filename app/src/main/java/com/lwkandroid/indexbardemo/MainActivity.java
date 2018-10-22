package com.lwkandroid.indexbardemo;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.lwkandroid.rcvadapter.bean.RcvSectionWrapper;
import com.lwkandroid.rcvadapter.ui.RcvStickyLayout;
import com.lwkandroid.rcvadapter.utils.RcvLinearDecoration;
import com.lwkandroid.widget.indexbar.IndexBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.lwkandroid.indexbardemo.R.id.indexBar;

public class MainActivity extends AppCompatActivity implements IndexBar.OnIndexLetterChangedListener
{
    private static final CharSequence[] INDEX_ARRAY = new CharSequence[]{
            "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private UserAdapter mAdapter;
    private RcvStickyLayout mStickyLayout;
    private TextView mTvIndicate;
    private IndexBar mIndexBar;
    private Handler mHandler;
    private boolean mMoved;
    private int mSectionP;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler(getMainLooper());
        mIndexBar = (IndexBar) findViewById(indexBar);
        mIndexBar.setTextArray(INDEX_ARRAY);
        mTvIndicate = (TextView) findViewById(R.id.tv_indicate);
        mIndexBar.setOnIndexLetterChangedListener(this);

        mStickyLayout = (RcvStickyLayout) findViewById(R.id.stickyLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new RcvLinearDecoration(MainActivity.this, LinearLayoutManager.VERTICAL));
        mAdapter = new UserAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        mStickyLayout.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                //在这里进行第二次滚动来让Sction滚动到顶部
                if (mMoved)
                {
                    mMoved = false;
                    //获取要置顶的项在当前屏幕的位置，mSectionP是记录的要置顶项在RecyclerView中的位置
                    int n = mSectionP - mLayoutManager.findFirstVisibleItemPosition();
                    if (0 <= n && n < mRecyclerView.getChildCount())
                    {
                        //获取要置顶的项顶部离RecyclerView顶部的距离
                        int top = mRecyclerView.getChildAt(n).getTop();
                        //最后的移动
                        mRecyclerView.scrollBy(0, top);
                    }
                }
            }
        });

        initData();
    }

    private void initData()
    {
        //获取模拟数据
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                List<User> list = UserModel.getUserDatas();
                final List<RcvSectionWrapper<String, User>> resultList = new ArrayList<>();
                Set<String> charSet = new HashSet<>();

                for (User user : list)
                {
                    if (!charSet.contains(user.getFirstChar()))
                    {
                        resultList.add(new RcvSectionWrapper<String, User>(true, user.getFirstChar(), null));
                        charSet.add(user.getFirstChar());
                    }

                    resultList.add(new RcvSectionWrapper<String, User>(false, null, user));
                }

                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mAdapter.refreshDatas(resultList);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onTouched(boolean touched)
    {
        if (touched)
            mTvIndicate.setVisibility(View.VISIBLE);
        else
            mTvIndicate.setVisibility(View.GONE);
    }

    @Override
    public void onLetterChanged(CharSequence indexChar, int index, float y)
    {
        mTvIndicate.setText(indexChar);

        int p = mAdapter.getSectionP(String.valueOf(indexChar));
        if (p != -1)
        {
            mSectionP = p;
            //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
            int firstItem = mLayoutManager.findFirstVisibleItemPosition();
            int lastItem = mLayoutManager.findLastVisibleItemPosition();
            //然后区分情况
            if (p <= firstItem)
            {
                //当要置顶的项在当前显示的第一个项的前面时
                mRecyclerView.scrollToPosition(p);
            } else if (p <= lastItem)
            {
                //当要置顶的项已经在屏幕上显示时
                int top = mRecyclerView.getChildAt(p - firstItem).getTop();
                mRecyclerView.scrollBy(0, top);
            } else
            {
                //当要置顶的项在当前显示的最后一项的后面时
                mRecyclerView.scrollToPosition(p);
                //这里这个变量是用在RecyclerView滚动监听里面的
                mMoved = true;
            }
        }
    }
}
