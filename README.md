# Terminal AI Plugin for IntelliJ IDEA

一个IntelliJ IDEA插件，可以在右键菜单中快速打开项目目录并使用 OpenCode 或 ClaudeCode。

## 功能特性

- 右键点击项目视图中的文件夹，显示"使用终端AI打开项目"菜单
- 二级菜单包含：OpenCode 和 ClaudeCode
- 点击后自动打开系统终端并执行相应的启动命令
- 支持 Windows、macOS 和 Linux
- 兼容 IntelliJ IDEA 2020.3 ~ 2024.3 (203 ~ 243.*)

## 构建

### 前置要求
- Java 11 或更高版本
- IntelliJ IDEA (推荐) 或 Gradle

### 方法一：使用 IntelliJ IDEA 构建（推荐）

1. 打开 IntelliJ IDEA
2. 选择 File → Open，打开 `terminal-ai-plugin` 目录
3. 等待 Gradle 同步完成
4. 打开右侧的 Gradle 工具窗口
5. 展开 Tasks → intellij
6. 双击 `buildPlugin`
7. 构建产物位于 `build/distributions/TerminalAIPlugin-1.0.0.zip`

### 方法二：命令行构建

使用项目中已包含的 Gradle Wrapper：

**Windows:**
```cmd
gradlew.bat buildPlugin
```

**Linux/macOS:**
```bash
./gradlew buildPlugin
```

构建产物位于 `build/distributions/TerminalAIPlugin-1.0.0.zip`

## 安装

1. 打开 IntelliJ IDEA
2. 进入 Settings/Preferences → Plugins
3. 点击齿轮图标 → Install Plugin from Disk...
4. 选择 `build/distributions/TerminalAIPlugin-1.0.0.zip`
5. 重启 IDE

## 使用

1. 在项目视图中右键点击任意文件夹
2. 选择"使用终端AI打开项目"
3. 选择"OpenCode"或"ClaudeCode"
4. 系统终端将自动打开并执行相应命令
