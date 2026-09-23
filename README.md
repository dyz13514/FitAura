# FitAura

**软件名称：** FitAura 健身训练辅助系统  
**版本号：** V1.0  
**开发者：** 戴元哲  
**开发语言：** Java / Kotlin  
**运行平台：** Android 9.0（API 28）及以上  

FitAura 是一款面向个人健身场景的 Android 应用程序，用于辅助制定训练计划、执行训练动作、查看健身知识，以及记录和回顾训练数据。

## 主要功能

1. **用户注册与登录**  
   支持本地账号注册、登录，以及个人资料、昵称和头像管理。

2. **今日训练**  
   按肩部、背部、胸部、腿部循环推荐训练部位，可选择训练强度，进入训练计时与动作勾选流程。

3. **自定义训练**  
   可自选训练部位与强度，并添加自定义动作后完成训练打卡。

4. **健身知识**  
   提供分部位训练文章、视频入口与当日训练提示。

5. **训练记录**  
   以日历查看历史训练，支持查看详情、补充记录和计划管理。

6. **个人中心**  
   展示累计训练次数、总时长与平均强度等统计信息。

7. **背景音乐播放**  
   训练过程中可通过前台服务播放本地音乐。

## 技术说明

- 界面：AndroidX AppCompat、Material Components、Fragment 底部导航
- 本地存储：SharedPreferences、SQLite / Room
- 网络同步：OkHttp、Retrofit、Gson（训练记录上传与拉取接口预留）
- 构建工具：Android Gradle Plugin 8.9.1，Java 11，compileSdk 34

## 运行环境

- Android Studio（建议 Ladybug / 2024.2 或更高版本）
- JDK 11
- Android SDK 34
- 真机或模拟器：Android 9.0 及以上

## 编译与运行

1. 使用 Android Studio 打开本仓库根目录。
2. 等待 Gradle 同步完成。
3. 连接 Android 设备或启动模拟器。
4. 运行 `app` 模块即可安装 FitAura。

首次进入应用后，请先注册账号并登录，再使用首页训练、知识、记录和个人中心等功能。

## 项目结构

```
FitAura/
├── app/src/main/java/com/example/dyzapplication/
│   ├── SplashActivity.java          # 启动页
│   ├── LogInActivity.java / RegActivity.java   # 登录注册
│   ├── MainFragmentActivity.java    # 主界面（四个功能页）
│   ├── IndexFragment.java           # 今日训练
│   ├── SecondFragment.java          # 健身知识
│   ├── ThirdFragment.java           # 训练记录
│   ├── FourthFragment.java          # 个人中心
│   ├── WorkoutActivity.java         # 训练执行
│   ├── WorkoutPlanManager.java      # 训练计划轮换
│   ├── database/                    # Room 数据实体与访问接口
│   └── api/                         # 训练记录网络同步
├── app/src/main/res/                # 布局、图片与主题资源
├── app/build.gradle                 # 应用模块依赖
└── settings.gradle
```

## 仓库说明

本仓库用于保存 FitAura V1.0 源代码及工程配置，便于版本管理与软件著作权登记材料归档。
