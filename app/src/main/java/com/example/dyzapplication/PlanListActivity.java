package com.example.dyzapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dyzapplication.database.PlanEntity;
import java.util.ArrayList;
import java.util.List;

public class PlanListActivity extends AppCompatActivity {
    private ListView listView;
    private Button btnBack;
    private PlanManager planManager;
    private PlanAdapter adapter;
    private List<PlanEntity> plans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);

        // 初始化组件
        listView = findViewById(R.id.list_plans);
        btnBack = findViewById(R.id.bt_back);

        // 获取用户名
        String userName = getIntent().getStringExtra("userName");
        
        // 初始化计划管理器
        planManager = new PlanManager(this, userName);
        
        // 初始化适配器
        adapter = new PlanAdapter();
        listView.setAdapter(adapter);
        
        // 加载计划列表
        loadPlans();

        // 返回按钮点击事件
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadPlans() {
        planManager.getAllPlans(new PlanManager.DataCallback<List<PlanEntity>>() {
            @Override
            public void onSuccess(List<PlanEntity> data) {
                runOnUiThread(() -> {
                    plans.clear();
                    plans.addAll(data);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    // 处理错误
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (planManager != null) {
            planManager.close();
        }
    }

    private class PlanAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return plans.size();
        }

        @Override
        public PlanEntity getItem(int position) {
            return plans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(PlanListActivity.this)
                    .inflate(R.layout.item_plan, parent, false);
                holder = new ViewHolder();
                holder.tvTitle = convertView.findViewById(R.id.tv_plan_title);
                holder.tvDateTime = convertView.findViewById(R.id.tv_plan_datetime);
                holder.tvContent = convertView.findViewById(R.id.tv_plan_content);
                holder.tvState = convertView.findViewById(R.id.tv_plan_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            PlanEntity plan = getItem(position);
            holder.tvTitle.setText(plan.getTitle());
            holder.tvDateTime.setText(plan.getDate() + " " + plan.getTime());
            holder.tvContent.setText(plan.getContent());
            holder.tvState.setText(plan.getState());

            return convertView;
        }
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvDateTime;
        TextView tvContent;
        TextView tvState;
    }
}