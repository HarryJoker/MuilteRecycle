[![](https://jitpack.io/v/HarryJoker/MuilteRecycle.svg)](https://jitpack.io/#HarryJoker/MuilteRecycle)

# MuilteRecycle：多级折叠列表（无限级折叠列表）

MuilteRecycler，使用FastJSON对象作为列表数据源，只需要配置子列表key及默认的每层展开position，MuilteRecyclerAdapter就可以自动进行换算完全实现多级折叠，使用简单且无需进行数据的处理操作！

# 示例：
二级列表
![image](https://github.com/HarryJoker/MuilteRecycle/blob/master/image/device-2020-01-01-205903_Two_.gif)

三级列表
![image](https://github.com/HarryJoker/MuilteRecycle/blob/master/image/device-2020-01-01-204735_Three_.gif)

四级列表
![image](https://github.com/HarryJoker/MuilteRecycle/blob/master/image/device-2020-01-01-204830_Four_.gif)

# 使用
```
maven { url 'https://jitpack.io' }

```
```
dependencies {
	implementation 'com.github.HarryJoker:MuilteRecycle:1.0.4'
}
```

# 使用方法

1，继承MuliteRecycleAdapter实现自己的Adapter

2，实现换算需要的Key和position方法：
```
//每层的字列表key
@Override
public void onConfigGroupKeys(List<String> mChildGroupKeys) {
    mChildGroupKeys.add("months");
    mChildGroupKeys.add("weeks");
    mChildGroupKeys.add("weekDays");
}

//默认选中每层的position
@Override
public void onSelectedGroupIndex(List<Integer> mExpandPositions) {
    mExpandPositions.add(0);
    mExpandPositions.add(0);
    mExpandPositions.add(0);
}
```

3，实现ViewHolder
```
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
```


### License
<pre>
Copyright 2020 HarryJoker

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
