package com.harry.joker.muilte.library;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: HarryJoker
 * Created on: 2020-01-01 14:03
 * Description:
 */
public abstract class MuliteRecycleAdapter extends RecyclerView.Adapter<MuliteRecycleAdapter.BaseItemViewHolder> {

    private Activity mContext;

    private JSONArray mJSONArray = new JSONArray();

    private List<String> mRankKeys = new ArrayList<String>();

    private List<Integer> mRankPositions = new ArrayList<Integer>();


    public abstract void onConfigGroupKeys(List<String> mChildGroupKeys);

    public abstract void onSelectedGroupIndex(List<Integer> mExpandPositions);

    public MuliteRecycleAdapter(Activity context) {
        this.mContext = context;
        onConfigGroupKeys(mRankKeys);
        if (mRankKeys.size() == 0) {
            throw new NullPointerException("onConfigGroupKeys Not has key, At last size 1");
        }
        onSelectedGroupIndex(mRankPositions);
    }

    public MuliteRecycleAdapter(Activity context, JSONArray datas) {
        this.mContext = context;
        onConfigGroupKeys(mRankKeys);
        onSelectedGroupIndex(mRankPositions);

        this.setJSONArray(datas);
    }

    public void setJSONArray(JSONArray array) {
        mJSONArray.clear();
        if (array != null && array.size() > 0) {
            mJSONArray.addAll(array);

            validateRankAndKeys();
        }
        notifyDataSetChanged();
    }

    private boolean validateRankAndKeys() {
        if (mRankPositions == null || mRankKeys == null || mRankPositions.size() > mRankKeys.size()) {
            throw new NumberFormatException("mRankPositions size can not mate mRainKeys size， Now mRankPositions：" + mRankPositions.size() + ", mRankKeys: " + mRankKeys.size());
        }
        for (int rank = 0; rank < mRankPositions.size(); rank++) {
            int rankPos = mRankPositions.get(rank);
            int rankSize = getChildSizeByRank(rank - 1);
            if (rankPos >= rankSize) {
                throw new NumberFormatException("Rank Position can not mat Array， Now rank：" + rank + ", position: " + rankPos + " can not mate array Size:" + rankSize);
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        int itemCount = sumChildsSizeByRank(-1);
        Logger.d("item count:" + itemCount);
        return itemCount;
    }


    @Override
    public int getItemViewType(int position) {
        int type = cacluteNestViewType(position);
        return type;
    }

    public Activity getContext() {
        return mContext;
    }

    public JSONObject getItem(int position) {
        int inRanksSize = getInRankSize(mRankPositions.size() - 1);
        JSONObject jsonObject = null;
        if (position < inRanksSize) {
            jsonObject = findItemInRanks(position);
        } else {
            jsonObject = findItemOutRanks(position);
        }
//        Log.i("item", "position:" + position + ", " + jsonObject);
        if (jsonObject == null) {
            throw new NullPointerException("Null Point, position can not mate item data");
        }
        return jsonObject;
    }

    /**
     * 设置展开列表的Position
     *
     * @param positions
     */
    public void setSelectedPositions(Integer[] positions) {
        if (positions == null) return;
        if (positions.length > mRankKeys.size()) {
            throw new NumberFormatException("Selected positions size can not index out of child keys");
        }
        mRankPositions.clear();
        mRankPositions.addAll(Arrays.asList(positions));
        notifyDataSetChanged();
    }

    /**
     * 从In Ranks里的位置里找item
     * 每层Rank的position对比，从小于Rank位置的position列表里拿出
     *
     * @param position
     * @return
     */
    private JSONObject findItemInRanks(int position) {
        for (int rank = 0; rank < mRankPositions.size(); rank++) {
            int inRankSize = getInRankSize(rank);
            if (position < inRankSize) {
                JSONArray array = getChildArrayByRank(rank - 1);
                int preRankSize = getInRankSize(rank - 1);
                int offsetPos = position - preRankSize;
                if (array != null && array.size() > 0 && offsetPos >= 0 && offsetPos < array.size()) {
                    return array.getJSONObject(offsetPos);
                }
            }
        }
        return null;
    }

    /**
     * 从Out Ranks的位置里找item
     * f:sum(rank) + f:sumChild(rank)的长度定位position所在rank
     * offsetPos = position - inRankSize - sumChild（rank - 1）
     *
     * @param position
     * @return
     */
    private JSONObject findItemOutRanks(int position) {
        for (int rank = mRankPositions.size() - 1; rank >= -1; rank--) {
            int inRankSize = getInRankSize(rank);
            int outRankSize = sumChildsSizeByRank(rank);
            int sumCount = inRankSize + outRankSize;
            if (position < sumCount) {
                int outRankChildSize = sumChildsSizeByRank(rank + 1);
                JSONArray array = getChildArrayByRank(rank);
                int offsetPos = position - inRankSize - outRankChildSize;
                if (array != null && array.size() > 0 && offsetPos >= 0 && offsetPos < array.size()) {
                    return array.getJSONObject(offsetPos);
                }
            }
        }
        return null;
    }

    /**
     * 计算In Rank的size
     *
     * @param rank
     * @return
     */
    private int getInRankSize(int rank) {
        if (rank >= mRankPositions.size()) return 0;
        if (rank < 0) return 0;
        int size = 0;
        for (int n = 0; n <= rank; n++) {
            size += mRankPositions.get(n) + 1;
        }
        return size;
    }

    /**
     * 计算position在列表处于的rank层，即：rank代表viewType
     * 二分查找：InRank与OutRank
     *
     * @param position
     * @return
     */
    private int cacluteNestViewType(int position) {
        int type = -1;
        int dividerCount = sumPositionByRank(mRankPositions.size() - 1);
        if (position == dividerCount) {
            type = mRankPositions.size() - 1;
        } else if (position < dividerCount) {
            type = calcluteInRankViewType(position);
        } else if (position > dividerCount) {
            type = calcluteOutRankViewType(position);
        }
        return type;
    }

    /**
     * 计算在Ranks内的Position对应type
     * type = rank = (Position < f:sumPos(rank))
     *
     * @param position
     * @return
     */
    private int calcluteInRankViewType(int position) {
        for (int rank = 0; rank < mRankPositions.size(); rank++) {
            if (position <= sumPositionByRank(rank)) {
                return rank;
            }
        }
        return -1;
    }

    /**
     * 计算Ranks外的position对应的type
     * type = rank = (position < f:sumPos(rank) + sumChildsSize(rank))
     *
     * @param position
     * @return
     */
    private int calcluteOutRankViewType(int position) {
        int type = -1;
        for (int rank = mRankPositions.size() - 1; rank >= -1; rank--) {
            int sumCount = sumPositionByRank(rank) + sumChildsSizeByRank(rank);
            if (position <= sumCount) return rank + 1;
        }
        return type;
    }


    /**
     * 计算Rank层的展开列表总长度
     * 当前展开位置的child的列表 + 该child的嵌套列表长度
     *
     * @param rank 第几层
     * @return
     */
    private int sumChildsSizeByRank(int rank) {
        if (mJSONArray.size() == 0) return 0;
        if (rank >= mRankPositions.size()) return 0;
        return getChildSizeByRank(rank) + sumChildsSizeByRank(rank + 1);
    }

    /**
     * 计算第rank层位置展开的子列表长度
     *
     * @param rank
     * @return
     */
    private int getChildSizeByRank(int rank) {
        if (rank < -1 || rank >= mRankPositions.size()) return 0;
        JSONArray array = getChildArrayByRank(rank);
        if (array != null) return array.size();
        return 0;
    }

    /**
     * 获取第rank层的子列表
     * -1取最顶层的列表，即：mJsonArray
     * mRankPositions.size-1取最后展开位置的最后一层列表
     *
     * @param rank (取值范围：[-1]至[mRankPositions.size-1])
     * @return
     */
    private JSONArray getChildArrayByRank(int rank) {
        JSONArray array = null;
        if (rank >= mRankPositions.size()) return array;
        for (int i = -1; i <= rank; i++) {
            if (i == -1) {
                array = mJSONArray;
            } else {
                array = getNextArray(array, i);
            }
        }
        return array;
    }

    /**
     * 计算第rank层展开位置的position
     *
     * @param rank
     * @return
     */
    private int sumPositionByRank(int rank) {
        if (rank <= -1 || rank >= mRankPositions.size()) return 0;
        int inGroupPosition = mRankPositions.get(rank);
        if (rank == 0) {
            return inGroupPosition;
        } else {
            return inGroupPosition + 1 + sumPositionByRank(--rank);
        }
    }

    /**
     * 取某一层数据中展开的子列表
     * 注：必须是数据源与层级相对应
     *
     * @param array 某层数据源
     * @param rank  对应的该层级（转换为该层级的展开位置）
     * @return
     */
    private JSONArray getNextArray(JSONArray array, int rank) {
        if (rank >= mRankKeys.size() || rank >= mRankPositions.size()) return null;
        int index = mRankPositions.get(rank);
        if (index >= array.size()) return null;
        JSONObject item = array.getJSONObject(index);
        String groupKey = mRankKeys.get(rank);
        if (!item.containsKey(groupKey)) return null;
        return item.getJSONArray(groupKey);
    }

    public int calcuteRankPosition(int rank, int position) {
        if (rank >= mRankPositions.size()) return 0;
        int inRanksSize = getInRankSize(rank);
        int offsetPos = -1;
        if (position < inRanksSize) {
            offsetPos = calcuteOffsetPosInRank(rank, position);
        } else {
            offsetPos = calcuteOffsetPosOutRank(rank, position);
        }
        Log.i("item", "position:" + position + ", offsetPos:" + offsetPos);
//        if (jsonObject == null) {
//            throw new NullPointerException("Null Point, position can not mate item data");
//        }
        return offsetPos;
    }

    /**
     * 从In Ranks里的位置里找item
     * 每层Rank的position对比，从小于Rank位置的position列表里拿出
     *
     * @param position
     * @return
     */
    private int calcuteOffsetPosInRank(int rank, int position) {
        int inRankSize = getInRankSize(rank);
        if (position < inRankSize) {
            int preRankSize = getInRankSize(rank - 1);
            return position - preRankSize;
        }
        return -1;
    }

    /**
     * 从Out Ranks的位置里找item
     * f:sum(rank) + f:sumChild(rank)的长度定位position所在rank
     * offsetPos = position - inRankSize - sumChild（rank - 1）
     *
     * @param position
     * @return
     */
    public int calcuteOffsetPosOutRank(int rank, int position) {
        int inRankSize = getInRankSize(rank - 1);
        int outRankSize = sumChildsSizeByRank(rank);
//        int sumCount = inRankSize + outRankSize;
        int offsetPos = position - inRankSize - outRankSize;
        return offsetPos;
    }

    @NonNull
    @Override
    public BaseItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateMuliteViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseItemViewHolder holder, int position) {
        holder.itemView.setTag(position);

        Logger.d("binder position:" + position + ", rankPositions:" + mRankPositions);


        if (holder.ivIndicator != null) {
            holder.ivIndicator.setImageResource(R.drawable.ext_arrow_right);

            //postion位于哪层：rank
            int type = getItemViewType(position);
            if (type < mRankPositions.size()) {
                //在rank层的position
                int offsetPos = calcuteRankPosition(type, position);
                int rankPosition = mRankPositions.get(type);
                if (rankPosition == offsetPos) {
                    holder.ivIndicator.setImageResource(R.drawable.ext_arrow_down);
                }
            }
        }
        onBindMuliteViewHolder(holder, position, getItem(position));
    }

    public abstract BaseItemViewHolder onCreateMuliteViewHolder(@NonNull ViewGroup parent, int viewType);

    public abstract void onBindMuliteViewHolder(@NonNull BaseItemViewHolder holder, int position, JSONObject item);

    public abstract class BaseItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIndicator;

        public BaseItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIndicator = itemView.findViewById(R.id.iv_indicator);
            itemView.setOnClickListener(mOnClickListener);
        }

        public abstract void bindView(int position);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof Integer) {
                Integer position = (Integer)tag;
                int type = getItemViewType(position);
                if (type < mRankKeys.size()) {
                    int offsetPos = calcuteRankPosition(type, position);
                    Log.d("click", "position:" + position + ", type:" + type + ", offsetPos:" + offsetPos);
                    updateRankPosition(type, offsetPos);
                } else {
                    if (!onItemClick(type, position, getItem(position))) {
                        mOnItemClickListener.onItemClick(type, position, getItem(position));
                    }
                }
            }
        }
    };


    private void updateRankPosition(int rank, int position) {
        if (rank < 0 || rank > mRankPositions.size()) return;
        List<Integer> tmp = new ArrayList<>();
        boolean isParent = false;
        for (int n = 0; n < mRankPositions.size(); n++) {
            if (rank == n) {
                isParent = true;
                int rankPos = mRankPositions.get(n);
                if (rankPos != position) {
                    tmp.add(position);
                } else {
                    tmp.add(0);
                }
            } else {
                tmp.add(isParent ? 0 : mRankPositions.get(n));
            }
        }
        mRankPositions.clear();
        mRankPositions.addAll(tmp);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int type, int position, JSONObject item) {

        }
    };

    protected boolean onItemClick(int type, int position, JSONObject item) {
        return false;
    }

    interface OnItemClickListener {
        void onItemClick(int type, int position, JSONObject item);
    }

}
