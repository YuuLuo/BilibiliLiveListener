# Bilibili Live Listener

这是一个Android应用，用于监听Bilibili直播状态变化并通知用户。

## 功能

- 监听指定Bilibili主播的直播状态。
- 当主播开始直播时弹出通知提示。
- 用户可自定义设置是否使应用保持后台运行。

## 如何使用

在 [Release](https://github.com/YuuLuo/BilibiliLiveListener/releases) 下安装APK文件，在主页面输入主播UID后添加，启用监听即可开始直播提醒。

右上角设置中可以调整查询间隔，单位为秒，默认值是10.

注意：建议开启“启用后台常驻”，并在设置中关闭该应用的电池优化以防止后台被冻结。
如果开启“启用后台常驻”后任务栏没有通知，可以尝试关闭后再开启。

## 使用图示

![Screenshot_Home](https://github.com/YuuLuo/BilibiliLiveListener/assets/69673808/2bdb1836-6e8c-4608-9e11-c7e8b3a40003)
![Screenshot_Notification](https://github.com/YuuLuo/BilibiliLiveListener/assets/69673808/0cdc0c0d-9580-4723-91d6-fc983ff05af2)

## 技术栈

- Kotlin
- Retrofit for network requests
- Glide for image loading
- MVVM architecture

## 许可证

本项目采用 [GNU General Public License version 3 (GPLv3)](LICENSE)。GPLv3 是一个广泛使用的自由软件许可证，它确保最终用户能够自由地运行、研究、共享和修改软件。

更多关于这个许可证的详细信息，请查阅 [LICENSE](LICENSE) 文件。
