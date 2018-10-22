package com.lwkandroid.indexbardemo;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import com.lwkandroid.rcvadapter.RcvSectionAdapter;
import com.lwkandroid.rcvadapter.bean.RcvSectionWrapper;
import com.lwkandroid.rcvadapter.holder.RcvHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人适配器
 */

public class UserAdapter extends RcvSectionAdapter<String, User>
{
    private Map<String, Integer> mPositionMap = new HashMap<>();

    public UserAdapter(Context context, List<RcvSectionWrapper<String, User>> datas)
    {
        super(context, datas);
        //记录下所有Section位置
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onChanged()
            {
                super.onChanged();
                recordSection();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount)
            {
                super.onItemRangeChanged(positionStart, itemCount);
                recordSection();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload)
            {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                recordSection();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                recordSection();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount)
            {
                super.onItemRangeRemoved(positionStart, itemCount);
                recordSection();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount)
            {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                recordSection();
            }
        });
    }

    private void recordSection()
    {
        mPositionMap.clear();
        for (int i = 0, count = mDataList.size(); i < count; i++)
        {
            RcvSectionWrapper<String, User> wrapper = mDataList.get(i);
            if (wrapper.isSection())
                mPositionMap.put(wrapper.getSection(), i + getHeadCounts());
        }
    }

    public int getSectionP(String s)
    {
        return mPositionMap.containsKey(s) ? mPositionMap.get(s) : -1;
    }

    @Override
    public int getSectionLayoutId()
    {
        return R.layout.layout_section;
    }

    @Override
    public void onBindSectionView(RcvHolder holder, String section, int position)
    {
        holder.setTvText(R.id.tv_section, section);
    }

    @Override
    public int getDataLayoutId()
    {
        return R.layout.layout_item;
    }

    @Override
    public void onBindDataView(RcvHolder holder, User data, int position)
    {
        holder.setTvText(R.id.tv_item, data.getName());
    }
}
