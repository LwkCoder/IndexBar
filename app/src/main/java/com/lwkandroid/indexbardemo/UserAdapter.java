package com.lwkandroid.indexbardemo;

import android.content.Context;

import com.lwkandroid.rcvadapter.RcvSectionSingleLabelAdapter;
import com.lwkandroid.rcvadapter.base.RcvBaseItemView;
import com.lwkandroid.rcvadapter.bean.RcvSectionWrapper;
import com.lwkandroid.rcvadapter.holder.RcvHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 联系人适配器
 */

public class UserAdapter extends RcvSectionSingleLabelAdapter<String, User>
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

    @Override
    protected RcvBaseItemView<RcvSectionWrapper<String, User>>[] createDataItemViews()
    {
        return new RcvBaseItemView[]{new DataItemView()};
    }

    @Override
    public int getSectionLabelLayoutId()
    {
        return R.layout.layout_section;
    }

    @Override
    public void onBindSectionLabelView(RcvHolder holder, String section, int position)
    {
        holder.setTvText(R.id.tv_section, section);
    }

    /**
     * 刷新所有Section所在的位置
     */
    private void recordSection()
    {
        mPositionMap.clear();
        for (int i = 0, count = getDatas().size(); i < count; i++)
        {
            RcvSectionWrapper<String, User> wrapper = mDataList.get(i);
            if (wrapper.isSection())
            {
                mPositionMap.put(wrapper.getSection(), i + getHeadCounts());
            }
        }
    }

    /**
     * 查找某个字符是否为Section，如果是则返回对应位置
     */
    public int getSectionP(String s)
    {
        return mPositionMap.containsKey(s) ? mPositionMap.get(s) : -1;
    }

    /**
     * 内容布局类型
     */
    private static class DataItemView extends RcvBaseItemView<RcvSectionWrapper<String, User>>
    {

        @Override
        public int getItemViewLayoutId()
        {
            return R.layout.layout_item;
        }

        @Override
        public boolean isForViewType(RcvSectionWrapper<String, User> item, int position)
        {
            return !item.isSection();
        }

        @Override
        public void onBindView(RcvHolder holder, RcvSectionWrapper<String, User> wrapper, int position)
        {
            holder.setTvText(R.id.tv_item, wrapper.getData().getName());
        }
    }

}
