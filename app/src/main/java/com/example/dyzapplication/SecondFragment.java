package com.example.dyzapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.BaseAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView lvArticles;
    private GridView lvVideos;
    private TextView tvTodayTips;
    
    // 肩部训练文章数据
    private final String[][] SHOULDER_ARTICLES = {
            {"肩部训练完全指南", "全面介绍肩部肌肉构造、功能及科学训练方法，帮助你打造强壮圆润的肩部。"},
            {"哑铃肩部训练计划", "只需一对哑铃，7个动作高效训练三角肌前中后束，适合家庭和健身房环境。"},
            {"如何避免肩部训练伤害", "详解肩部训练中常见错误姿势与预防措施，保护肩袖免受损伤。"},
            {"增大肩部宽度的4个关键动作", "专注于侧平举和上斜杠铃推举等动作，快速增加肩部宽度。"},
            {"肩部训练常见问题解答", "解答肩部训练中的常见疑问，包括训练频率、重量选择和动作衔接。"}
    };
    
    // 背部训练文章数据
    private final String[][] BACK_ARTICLES = {
            {"背部训练完全指南", "全面介绍背部肌肉结构和功能，从宽阔的背阔肌到细节的菱形肌，打造完美背部。"},
            {"硬拉技术详解", "正确的硬拉姿势和技巧分析，包括常规硬拉、相扑式硬拉和罗马尼亚硬拉。"},
            {"引体向上进阶指南", "从辅助引体向上到加重引体向上，逐步提升你的背部力量和肌肉。"},
            {"背部训练中的常见错误", "避免这些常见错误，确保你的背部训练既安全又有效。"},
            {"5个最佳背部拉伸动作", "训练后正确拉伸背部肌肉，促进恢复和减少疼痛。"}
    };
    
    // 胸部训练文章数据
    private final String[][] CHEST_ARTICLES = {
            {"胸肌训练完全指南", "详解胸大肌的解剖结构与功能，以及针对上、中、下胸的不同训练策略。"},
            {"卧推技术详解", "标准卧推的正确姿势和技巧，以及上斜、下斜和窄握卧推变化。"},
            {"不用卧推的胸肌训练", "五个不需要平板卧推的有效胸肌训练动作，适合家庭和旅行。"},
            {"增大胸肌的营养策略", "配合训练的饮食计划，促进胸肌生长和恢复。"},
            {"女性的胸肌训练指南", "专为女性设计的胸肌训练计划，提升胸部力量和形态。"}
    };
    
    // 腿部训练文章数据
    private final String[][] LEG_ARTICLES = {
            {"腿部训练完全指南", "全面解析股四头肌、腘绳肌和小腿肌群的训练方法，打造强壮下肢。"},
            {"深蹲技术详解", "标准深蹲的正确姿势和常见变体，包括前蹲、高脚杯蹲和分腿蹲。"},
            {"膝盖保护指南", "如何在进行强度腿部训练的同时保护你的膝关节健康。"},
            {"增强腿部爆发力", "专注于提升腿部爆发力和运动表现的训练方法。"},
            {"小腿训练特辑", "针对难以增长的小腿肌群，提供有效的训练策略和方法。"}
    };
    
    // 视频教程数据 - 根据不同部位提供不同视频
    private Map<String, String[][]> BODY_PART_VIDEOS = new HashMap<>();
    
    // 训练动作指导数据
    private final Map<String, String[][]> EXERCISES_GUIDE = new HashMap<>();
    
    // 当前显示的训练部位
    private String currentBodyPart = "chest"; // 默认显示胸部训练
    
    private WorkoutPlanManager workoutPlanManager;

    private Button btnShoulder;
    private Button btnBack;
    private Button btnChest;
    private Button btnLegs;
    private Button btnViewGuide;
    private String bodyPartSelected;

    public SecondFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondFragment newInstance(String param1, String param2) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        
        // 初始化视频数据
        initVideoData();
        
        // 初始化训练动作指南数据
        initExerciseGuideData();
    }

    /**
     * 初始化视频数据
     */
    private void initVideoData() {
        // 肩部训练视频
        BODY_PART_VIDEOS.put("shoulders", new String[][] {
            {"肩部训练全套动作详解", "https://www.bilibili.com/video/BV1Ga4y1e7Z2"},
            {"20分钟肩部训练（初学者适用）", "https://www.bilibili.com/video/BV1sa411c7Jf"},
            {"哑铃肩部训练技巧", "https://www.bilibili.com/video/BV1Db4y1S7rq"},
            {"肩部后束训练专题", "https://www.bilibili.com/video/BV1zV411z7zV"},
            {"无器械肩部训练", "https://www.bilibili.com/video/BV1EA411e7ed"}
        });
        
        // 背部训练视频
        BODY_PART_VIDEOS.put("back", new String[][] {
            {"背部训练全套动作详解", "https://www.bilibili.com/video/BV17v411y7ns"},
            {"30分钟背部训练（中级）", "https://www.bilibili.com/video/BV1XK4y1K7pP"},
            {"家庭背部训练计划", "https://www.bilibili.com/video/BV1oZ4y1W7Xf"},
            {"背阔肌训练技巧", "https://www.bilibili.com/video/BV1ov411B7hK"},
            {"引体向上进阶指南", "https://www.bilibili.com/video/BV1tt4y1Q7SS"}
        });
        
        // 胸部训练视频
        BODY_PART_VIDEOS.put("chest", new String[][] {
            {"胸部训练全套动作详解", "https://www.bilibili.com/video/BV1QK4y1N7MM"},
            {"25分钟居家胸肌训练", "https://www.bilibili.com/video/BV1Ht4y117Tz"},
            {"哑铃胸肌训练指南", "https://www.bilibili.com/video/BV1dh411o7XF"},
            {"5个最佳胸肌训练动作", "https://www.bilibili.com/video/BV1da4y1W7mg"},
            {"胸肌上中下部位训练", "https://www.bilibili.com/video/BV1nv411B73y"}
        });
        
        // 腿部训练视频
        BODY_PART_VIDEOS.put("legs", new String[][] {
            {"腿部训练全套动作详解", "https://www.bilibili.com/video/BV1Xp4y1i7SC"},
            {"30分钟腿部训练（无器械）", "https://www.bilibili.com/video/BV1Y54y1L7WP"},
            {"深蹲技术指南", "https://www.bilibili.com/video/BV1iT4y177mJ"},
            {"腿部训练常见错误", "https://www.bilibili.com/video/BV1954y117Mx"},
            {"小腿训练专题", "https://www.bilibili.com/video/BV1bK4y1Y7Bu"}
        });
    }

    /**
     * 初始化训练动作指南数据
     */
    private void initExerciseGuideData() {
        // 肩部训练动作
        EXERCISES_GUIDE.put("shoulders", new String[][] {
            {"哑铃推举", "坐姿或站姿，双手握哑铃至肩部两侧，肘部弯曲呈90度。向上推举哑铃直至手臂伸直，然后缓慢下降回到起始位置。3-4组，每组8-12次。"},
            {"侧平举", "站立，双手各持一个哑铃自然下垂。保持手臂微微弯曲，向两侧平举至与肩同高，然后缓慢放下。3-4组，每组12-15次。"},
            {"前平举", "站立，双手各持一个哑铃自然下垂于身体前方。保持手臂微微弯曲，向前上方举起至与肩同高，然后缓慢放下。3-4组，每组12-15次。"},
            {"反向飞鸟", "俯身，上身与地面平行，双手持哑铃下垂。保持手臂微弯，向两侧抬起至与肩同高，感受肩后束收紧，然后缓慢放下。3-4组，每组12-15次。"},
            {"上斜哑铃推举", "在30-45度上斜卧推凳上，双手握哑铃至肩部两侧。向上推举哑铃直至手臂伸直，然后缓慢下降回到起始位置。3-4组，每组8-12次。"}
        });
        
        // 背部训练动作
        EXERCISES_GUIDE.put("back", new String[][] {
            {"引体向上", "双手抓握高于头顶的横杠，手距略宽于肩。从完全悬挂位置，拉起身体直到下巴超过横杠，然后控制下降回到起始位置。3-4组，每组尽可能多的次数。"},
            {"杠铃划船", "俯身，上身与地面平行，双手握杠铃自然下垂。将杠铃拉向腹部下方，感受背部肌肉收缩，然后缓慢放下。3-4组，每组8-12次。"},
            {"高位下拉", "坐在高位下拉器械上，双手握住拉杆，手距大于肩宽。将拉杆拉至胸前上方，感受背阔肌收缩，然后缓慢回到起始位置。3-4组，每组10-12次。"},
            {"坐姿划船", "坐在划船器械上，双腿微屈，上身挺直。抓住把手，拉向腹部，肘部向后引导，感受背部中间部位收缩，然后缓慢回到起始位置。3-4组，每组10-12次。"},
            {"单臂哑铃划船", "一手和同侧膝盖支撑在平凳上，另一手持哑铃自然下垂。将哑铃拉向腰部，感受背部收缩，然后缓慢放下。3-4组，每组10-12次。"}
        });
        
        // 胸部训练动作
        EXERCISES_GUIDE.put("chest", new String[][] {
            {"平板卧推", "仰卧在平板卧推凳上，双手握杠铃，距离略宽于肩。将杠铃从架上取下，缓慢下降至胸部，然后推回起始位置。3-4组，每组8-12次。"},
            {"上斜卧推", "仰卧在30-45度上斜卧推凳上，双手握杠铃，距离略宽于肩。将杠铃从架上取下，缓慢下降至上胸部，然后推回起始位置。3-4组，每组8-12次。"},
            {"哑铃飞鸟", "仰卧在平板卧推凳上，双手持哑铃伸展于胸部上方。保持手臂微弯，向两侧打开，感受胸部拉伸，然后恢复到起始位置。3-4组，每组12-15次。"},
            {"双杠臂屈伸", "在平行杠上，双手支撑身体，肘部弯曲下沉直到上臂与地面平行，然后推起回到起始位置。3-4组，每组尽可能多的次数。"},
            {"俯卧撑", "俯卧，双手撑地，距离略宽于肩。通过弯曲手肘，将身体下降至胸部几乎触地，然后推起回到起始位置。3-4组，每组尽可能多的次数。"}
        });
        
        // 腿部训练动作
        EXERCISES_GUIDE.put("legs", new String[][] {
            {"深蹲", "站立，双脚与肩同宽，脚尖略微向外。身体下蹲，保持背部挺直，直到大腿与地面平行或稍低，然后回到起始位置。3-4组，每组8-12次。"},
            {"硬拉", "站立，双脚与肩同宽，双手握杠铃于身前。屈髋和膝，保持背部挺直，将杠铃从地面拉起至大腿前侧，然后控制下降回到起始位置。3-4组，每组6-10次。"},
            {"腿举", "坐在腿举机上，双脚放在踏板上，距离与肩同宽。解锁安全装置，通过推动踏板将重量举起，然后控制下降直到膝盖接近胸部，再推回。3-4组，每组10-12次。"},
            {"弓步蹲", "站立，一脚向前迈一大步。保持上身挺直，下沉后腿直到前腿大腿与地面平行，膝盖不超过脚尖，然后回到起始位置。3-4组，每组10-12次/腿。"},
            {"小腿提踵", "站立，脚尖站在台阶或重物上，脚跟悬空。提起脚跟，尽量高，感受小腿收缩，然后缓慢放下直到脚跟低于台阶。3-4组，每组15-20次。"}
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        
        // 初始化控件
        lvArticles = view.findViewById(R.id.list_articles);
        lvVideos = view.findViewById(R.id.list_videos);
        tvTodayTips = view.findViewById(R.id.tv_today_tips);
        
        // 初始化WorkoutPlanManager
        workoutPlanManager = new WorkoutPlanManager(getActivity());
        
        // 获取今日训练部位
        String todayBodyPart = workoutPlanManager.getTodayBodyPart();
        currentBodyPart = todayBodyPart;
        
        // 更新训练提示
        tvTodayTips.setText("今日训练部位: " + workoutPlanManager.getBodyPartChineseName(todayBodyPart) + 
                          "\n推荐强度: " + workoutPlanManager.getIntensityDescription(workoutPlanManager.getRecommendedIntensity()) +
                          "\n训练状态: " + (workoutPlanManager.isTodayWorkoutCompleted() ? "已完成" : "未完成"));
        
        // 更新UI内容
        updateContent(todayBodyPart);
        
        // 设置视频点击事件
        lvVideos.setOnItemClickListener((parent, view1, position, id) -> {
            String[][] videos = BODY_PART_VIDEOS.get(currentBodyPart);
            if (videos != null && position < videos.length) {
                String url = videos[position][1];
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        
        // 设置文章点击事件
        lvArticles.setOnItemClickListener((parent, view1, position, id) -> {
            // 获取文章内容，显示在对话框中
            String[][] articles = getArticlesByBodyPart(currentBodyPart);
            if (articles != null && position < articles.length) {
                showArticleDialog(articles[position][0], articles[position][1]);
            }
        });
        
        // 设置训练动作指南按钮点击事件
        view.findViewById(R.id.btn_exercise_guide).setOnClickListener(v -> {
            showExercisesGuide(currentBodyPart);
        });
        
        // 部位切换按钮
        view.findViewById(R.id.btn_shoulders).setOnClickListener(v -> updateContent("shoulders"));
        view.findViewById(R.id.btn_back).setOnClickListener(v -> updateContent("back"));
        view.findViewById(R.id.btn_chest).setOnClickListener(v -> updateContent("chest"));
        view.findViewById(R.id.btn_legs).setOnClickListener(v -> updateContent("legs"));
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化视图 - 这个方法调用可能需要放在这里而不是onCreateView中
        initViews(view);
        
        // 初始化按钮状态
        if (btnShoulder != null) {
            // 设置肩部训练为默认选择
            bodyPartSelected = currentBodyPart;
            
            // 根据当前选择的部位更新按钮状态
            switch (currentBodyPart) {
                case "shoulders":
                    updateSelectedButton(btnShoulder);
                    break;
                case "back":
                    updateSelectedButton(btnBack);
                    break;
                case "chest":
                    updateSelectedButton(btnChest);
                    break;
                case "legs":
                    updateSelectedButton(btnLegs);
                    break;
                default:
                    updateSelectedButton(btnShoulder);
                    break;
            }
            
            // 设置身体部位按钮点击事件
            setBodyPartButtonListeners();
            
            // 设置查看动作指南按钮点击事件
            if (btnViewGuide != null) {
                btnViewGuide.setOnClickListener(v -> showExercisesGuide(currentBodyPart));
            }
        }
    }

    // 初始化视图组件
    private void initViews(View view) {
        // 身体部位选择按钮
        btnShoulder = view.findViewById(R.id.btn_shoulders);
        btnBack = view.findViewById(R.id.btn_back);
        btnChest = view.findViewById(R.id.btn_chest);
        btnLegs = view.findViewById(R.id.btn_legs);
        
        // 查看动作指南按钮
        btnViewGuide = view.findViewById(R.id.btn_exercise_guide);
    }

    // 设置身体部位按钮的点击事件
    private void setBodyPartButtonListeners() {
        btnShoulder.setOnClickListener(v -> {
            bodyPartSelected = "shoulders";
            updateSelectedButton(btnShoulder);
            updateContent("shoulders");
        });
        
        btnBack.setOnClickListener(v -> {
            bodyPartSelected = "back";
            updateSelectedButton(btnBack);
            updateContent("back");
        });
        
        btnChest.setOnClickListener(v -> {
            bodyPartSelected = "chest";
            updateSelectedButton(btnChest);
            updateContent("chest");
        });
        
        btnLegs.setOnClickListener(v -> {
            bodyPartSelected = "legs";
            updateSelectedButton(btnLegs);
            updateContent("legs");
        });
    }

    // 更新选中按钮的样式
    private void updateSelectedButton(Button selectedButton) {
        // 重置所有按钮样式
        if (btnShoulder != null) {
            btnShoulder.setBackgroundResource(R.drawable.button_normal);
            btnBack.setBackgroundResource(R.drawable.button_normal);
            btnChest.setBackgroundResource(R.drawable.button_normal);
            btnLegs.setBackgroundResource(R.drawable.button_normal);
            
            // 设置选中按钮样式
            selectedButton.setBackgroundResource(R.drawable.button_selected);
        }
    }

    // 根据身体部位加载相应文章
    private void loadArticlesByBodyPart(String bodyPart) {
        String[][] articles;
        switch (bodyPart) {
            case "shoulders":
                articles = SHOULDER_ARTICLES;
                break;
            case "back":
                articles = BACK_ARTICLES;
                break;
            case "chest":
                articles = CHEST_ARTICLES;
                break;
            case "legs":
                articles = LEG_ARTICLES;
                break;
            default:
                articles = CHEST_ARTICLES; // 默认显示胸部文章
                break;
        }
        
        if (articles != null && lvArticles != null) {
            ArticleAdapter adapter = new ArticleAdapter(requireContext(), articles);
            lvArticles.setAdapter(adapter);
        }
    }

    // 根据身体部位加载相应视频
    private void loadVideosByBodyPart(String bodyPart) {
        String[][] videos = BODY_PART_VIDEOS.get(bodyPart);
        if (videos != null && lvVideos != null) {
            VideoAdapter adapter = new VideoAdapter(requireContext(), videos);
            lvVideos.setAdapter(adapter);
        }
    }

    // 显示训练动作指南
    private void showExercisesGuide(String bodyPart) {
        String[][] exercises = EXERCISES_GUIDE.get(bodyPart);
        if (exercises != null) {
            // 创建对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(getBodyPartTitle(bodyPart) + "训练动作指南");
            
            // 使用ListView显示训练动作
            ListView listView = new ListView(requireContext());
            ExerciseGuideAdapter adapter = new ExerciseGuideAdapter(requireContext(), exercises);
            listView.setAdapter(adapter);
            builder.setView(listView);
            
            // 设置关闭按钮
            builder.setPositiveButton("关闭", (dialog, which) -> dialog.dismiss());
            
            // 显示对话框
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    // 获取身体部位的中文名称
    private String getBodyPartTitle(String bodyPart) {
        switch (bodyPart) {
            case "shoulders":
                return "肩部";
            case "back":
                return "背部";
            case "chest":
                return "胸部";
            case "legs":
                return "腿部";
            default:
                return "";
        }
    }

    // 文章适配器
    private class ArticleAdapter extends BaseAdapter {
        private Context context;
        private String[][] articles;
        
        public ArticleAdapter(Context context, String[][] articles) {
            this.context = context;
            this.articles = articles;
        }
        
        @Override
        public int getCount() {
            return articles.length;
        }
        
        @Override
        public Object getItem(int position) {
            return articles[position];
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
            }
            
            TextView titleView = convertView.findViewById(R.id.tv_article_title);
            TextView descView = convertView.findViewById(R.id.tv_article_description);
            
            String[] article = articles[position];
            titleView.setText(article[0]);
            if (article.length > 1) {
                descView.setText(article[1]);
            }
            
            return convertView;
        }
    }

    // 视频适配器
    private class VideoAdapter extends BaseAdapter {
        private Context context;
        private String[][] videos;
        
        public VideoAdapter(Context context, String[][] videos) {
            this.context = context;
            this.videos = videos;
        }
        
        @Override
        public int getCount() {
            return videos.length;
        }
        
        @Override
        public Object getItem(int position) {
            return videos[position];
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
            }
            
            TextView titleView = convertView.findViewById(R.id.tv_video_title);
            String[] video = videos[position];
            titleView.setText(video[0]);
            
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video[1]));
                startActivity(intent);
            });
            
            return convertView;
        }
    }

    // 训练动作指南适配器
    private class ExerciseGuideAdapter extends BaseAdapter {
        private Context context;
        private String[][] exercises;
        
        public ExerciseGuideAdapter(Context context, String[][] exercises) {
            this.context = context;
            this.exercises = exercises;
        }
        
        @Override
        public int getCount() {
            return exercises.length;
        }
        
        @Override
        public Object getItem(int position) {
            return exercises[position];
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_exercise_guide, parent, false);
            }
            
            TextView nameView = convertView.findViewById(R.id.tv_exercise_name);
            TextView descView = convertView.findViewById(R.id.tv_exercise_description);
            
            String[] exercise = exercises[position];
            nameView.setText((position + 1) + ". " + exercise[0]);
            descView.setText(exercise[1]);
            
            return convertView;
        }
    }

    /**
     * 更新内容显示
     * @param bodyPart 身体部位
     */
    private void updateContent(String bodyPart) {
        currentBodyPart = bodyPart;
        
        // 加载文章
        loadArticlesByBodyPart(bodyPart);
        
        // 加载视频
        loadVideosByBodyPart(bodyPart);
    }

    /**
     * 显示文章内容对话框
     */
    private void showArticleDialog(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);
        
        // 创建滚动视图以便长内容可以滚动
        ScrollView scrollView = new ScrollView(requireContext());
        TextView textView = new TextView(requireContext());
        textView.setText(content);
        textView.setPadding(30, 30, 30, 30);
        textView.setTextSize(16);
        textView.setLineSpacing(0, 1.2f);
        scrollView.addView(textView);
        
        builder.setView(scrollView);
        builder.setPositiveButton("关闭", null);
        builder.show();
    }
    
    /**
     * 根据身体部位获取对应的文章数组
     */
    private String[][] getArticlesByBodyPart(String bodyPart) {
        switch (bodyPart) {
            case "shoulders":
                return SHOULDER_ARTICLES;
            case "back":
                return BACK_ARTICLES;
            case "chest":
                return CHEST_ARTICLES;
            case "legs":
                return LEG_ARTICLES;
            default:
                return CHEST_ARTICLES;
        }
    }
}