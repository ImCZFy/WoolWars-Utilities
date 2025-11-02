

# WoolWars Utilities

---
## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
---

**版本**: 1.0-SNAPSHOT  
**适用版本**: Minecraft 1.21+ / Paper & Spigot

## 简介

**WoolWars Utilities** 是一款专为 **[羊毛战争（WoolWars）](https://www.spigotmc.org/resources/wool-wars-%E2%AD%95-custom-kits-and-abilities-%E2%AD%90-power-ups-%E2%AD%90-jump-pads-%E2%AD%90-portals-%E2%9C%85-1-8-1-21-10.105548/)** 设计的工具插件，依赖于 **WoolWars**, **PlaceholderAPI**, **voicechat（可选）** 插件。

---

## 功能

- **玩家等级系统**
    - 每位玩家拥有独立的等级和经验
    - 等级经验值自动计算到下一等级所需经验
    - 支持显示进度条
    - 等级信息可用颜色代码渲染
    - 玩家数据异步加载，确保服务器主线程不卡顿。
    - 支持回调函数，命令执行完毕后可直接显示玩家数据。

- **游戏内语音分配**
  - 进入游戏自动分配队伍语音组
  - 退出游戏自动离开队伍语音组
  - 游戏结束自动删除队伍语音组


- **MySQL 数据库支持**
    - 自动连接 MySQL 数据库，存储玩家 UUID、名称、等级和经验。
    - 自动创建表格，如果玩家不存在则自动插入默认数据。
    - 异步操作，避免阻塞服务器主线程。

- 仅 `woolwarsutilities.admin` 可使用本插件的命令。

---

## 安装

1. 将 `WoolWarsUtilities.jar` 放入服务器 `plugins` 文件夹。
2. 启动服务器，插件会自动创建数据库表（确保 MySQL 配置正确）。
3. 配置权限：`woolwarsutilities.admin` 可使用插件管理命令。


## 翻译

目前暂不提供 I18N 支持，请使用源码自行修改。