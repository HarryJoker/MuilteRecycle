package com.harry.joker.muilte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.harry.joker.muilte.library.MuliteRecycleAdapter;

import java.util.ArrayList;
import java.util.List;

public class FourMuilteActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muilte);

        getSupportActionBar().setTitle("四级列表");

        mRecyclerView = findViewById(R.id.rc_muilte);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new DateMuilteRecyleAdapter(this, makeYear()));
    }



    private JSONArray makeYear() {
        JSONArray array = new JSONArray();
        JSONArray months = makeMonth();
        for (int i = 1; i < 10; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("year", "第" + (2015 + i)+ "年");
            jsonObject.put("months", months);
            array.add(jsonObject);
        }
        return array;
    }

    private JSONArray makeMonth() {
        JSONArray array = new JSONArray();
        JSONArray weeks = makeWeek();
        for (int i = 1; i < 13; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("month", "第" + i+ "月");
            jsonObject.put("weeks", weeks);
            array.add(jsonObject);
        }
        return array;
    }

    private JSONArray makeWeek() {
        JSONArray array = new JSONArray();
        JSONArray weekDays = makeWeekDays();
        for (int i = 1; i < 5; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("week", "第" + i+ "周");
            jsonObject.put("weekDays", weekDays);
            array.add(jsonObject);
        }
        return array;
    }

    private JSONArray makeWeekDays() {
        JSONArray array = new JSONArray();
        for (int i = 1; i < 8; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("weekDay", "周" + i);
            array.add(jsonObject);
        }
        return array;
    }

    class DateMuilteRecyleAdapter extends MuliteRecycleAdapter {

        public DateMuilteRecyleAdapter(Activity context, JSONArray array) {
            super(context, array);
        }

        @Override
        public void onConfigGroupKeys(List<String> mChildGroupKeys) {
            mChildGroupKeys.add("months");
            mChildGroupKeys.add("weeks");
            mChildGroupKeys.add("weekDays");
        }

        @Override
        public void onSelectedGroupIndex(List<Integer> mExpandPositions) {
            mExpandPositions.add(0);
            mExpandPositions.add(0);
            mExpandPositions.add(0);
        }


        @Override
        public MuliteRecycleAdapter.BaseItemViewHolder onCreateMuliteViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new DateMuilteRecyleAdapter.YearViewHolder(getContext().getLayoutInflater().inflate(R.layout.item_year, parent, false));
                case 1:
                    return new DateMuilteRecyleAdapter.MonthViewHolder(getContext().getLayoutInflater().inflate(R.layout.item_month, parent, false));
                case 2:
                    return new DateMuilteRecyleAdapter.WeekViewHolder(getContext().getLayoutInflater().inflate(R.layout.item_week, parent, false));
                case 3:
                    return new DateMuilteRecyleAdapter.WeekDayViewHolder(getContext().getLayoutInflater().inflate(R.layout.item_day, parent, false));
            }
            return null;
        }

        @Override
        public void onBindMuliteViewHolder(@NonNull MuliteRecycleAdapter.BaseItemViewHolder holder, int position, JSONObject item) {
            holder.bindView(position);
        }

        class YearViewHolder extends BaseItemViewHolder {

            TextView content;

            public YearViewHolder(@NonNull View itemView) {
                super(itemView);
                content = itemView.findViewById(R.id.tv_name);
            }

            @Override
            public void bindView(int position) {
                JSONObject data = getItem(position);
                content.setText(data.getString("year"));
            }
        }

        class MonthViewHolder extends BaseItemViewHolder {

            TextView content;

            public MonthViewHolder(@NonNull View itemView) {
                super(itemView);
                content = itemView.findViewById(R.id.tv_name);
            }

            @Override
            public void bindView(int position) {
                JSONObject data = getItem(position);
                content.setText(data.getString("month"));
            }
        }

        class WeekViewHolder extends BaseItemViewHolder {

            TextView content;

            public WeekViewHolder(@NonNull View itemView) {
                super(itemView);
                content = itemView.findViewById(R.id.tv_name);
            }

            @Override
            public void bindView(int position) {
                JSONObject data = getItem(position);
                content.setText(data.getString("week"));
            }
        }

        class WeekDayViewHolder extends BaseItemViewHolder {

            TextView content;

            public WeekDayViewHolder(@NonNull View itemView) {
                super(itemView);
                content = itemView.findViewById(R.id.tv_name);
                itemView.findViewById(R.id.iv_indicator).setVisibility(View.GONE);
            }

            @Override
            public void bindView(int position) {
                JSONObject data = getItem(position);
                content.setText(data.getString("weekDay"));
            }
        }

    }
}
