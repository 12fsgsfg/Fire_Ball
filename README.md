# 🔥 FireballPlugin - 火焰彈插件

一個功能豐富的 Minecraft Bukkit/Spigot 插件，讓玩家可以發射火焰彈並擁有完整的開關控制功能。

## ✨ 功能特色

- 🎯 **火焰彈發射** - 玩家可以發射具有可配置威力的火焰彈
- 🔄 **開關控制** - 每個玩家都可以獨立控制火焰彈功能的開啟/關閉
- ⏱️ **冷卻系統** - 可配置的發射冷卻時間，防止濫用
- 🖱️ **右鍵發射** - 支援按住 Shift + 右鍵快速發射火焰彈
- 🎨 **特效系統** - 豐富的發射和爆炸特效
- 🌍 **多語言支援** - 支援繁體中文、簡體中文和英文
- 🔧 **權限系統** - 完整的權限控制，確保伺服器安全
- 🎮 **Tab 補全** - 完整的指令自動補全功能
- 🔥 **火球物品檢查** - 可選的火球物品需求，增加遊戲平衡性

## 📦 安裝說明

### 前置需求
- Minecraft 1.21+ 伺服器
- Bukkit/Spigot/Paper 伺服器核心

### 安裝步驟
1. 下載 `Fire_Ball-1.0-SNAPSHOT.jar` 檔案
2. 將檔案放入伺服器的 `plugins` 資料夾
3. 重啟伺服器或使用 `/reload` 指令
4. 插件會自動生成配置檔案

## 🎮 使用方式

### 基本指令

| 指令 | 權限 | 說明 |
|------|------|------|
| `/fireball` | `fireballplugin.use` | 發射一個火焰彈 |
| `/fireball on` | `fireballplugin.toggle` | 啟用火焰彈功能 |
| `/fireball off` | `fireballplugin.toggle` | 禁用火焰彈功能 |
| `/fireball toggle` | `fireballplugin.toggle` | 切換火焰彈功能開關 |
| `/fireball status` | `fireballplugin.status` | 查看火焰彈功能狀態 |
| `/fireball reload` | `fireballplugin.reload` | 重新載入插件配置 |

### 指令別名
- `/fb` - 火焰彈指令的簡短別名
- `/fireballplugin` - 完整名稱別名

### 右鍵發射
- 按住 **Shift** 鍵
- 右鍵點擊空氣或方塊
- 需要權限：`fireballplugin.rightclick`
- 如果啟用火球物品需求，需要手持火球

### Tab 補全
- 輸入 `/fireball` 後按 Tab 鍵可看到所有可用選項
- 根據權限自動過濾可用的指令選項

## ⚙️ 配置說明

### 主要配置 (config.yml)

```yaml
# 火球設定
fireball:
  # 火球爆炸威力 (預設: 1.0)
  power: 1.0
  # 火球速度倍數 (預設: 2.0)
  speed: 2.0
  # 是否造成方塊損壞 (預設: true)
  damage-blocks: true

# 冷卻時間設定 (秒)
cooldown: 5

# 功能開關
features:
  # 是否啟用右鍵發射火球功能
  right-click: true
  # 是否啟用特殊效果
  special-effects: true
  # 玩家預設是否啟用火焰彈功能 (預設: true)
  default-enabled: true
  # 是否需要拿着火球物品才能發射 (預設: false)
  require-fireball-item: false

# 預設語言
language: "zh_TW"
```

### 語言檔案

插件支援三種語言：
- `zh_TW.yml` - 繁體中文 (預設)
- `zh_CN.yml` - 簡體中文
- `en_US.yml` - 英文

## 🔐 權限節點

| 權限節點 | 預設值 | 說明 |
|----------|--------|------|
| `fireballplugin.use` | `op` | 允許使用火焰彈指令 |
| `fireballplugin.toggle` | `op` | 允許切換火焰彈功能開關 |
| `fireballplugin.status` | `op` | 允許查看火焰彈功能狀態 |
| `fireballplugin.reload` | `op` | 允許重新載入插件配置 |
| `fireballplugin.rightclick` | `op` | 允許通過右鍵點擊發射火焰彈 |
| `fireballplugin.*` | `op` | 允許使用所有火焰彈插件功能 |

## 🎯 使用範例

### 啟用火焰彈功能
```
/fireball on
```
系統回應：`[火球插件] 火焰彈功能已啟用！`

### 查看功能狀態
```
/fireball status
```
系統回應：`[火球插件] 你的火焰彈功能狀態: 啟用`

### 發射火焰彈
```
/fireball
```
系統回應：`[火球插件] 你發射了一個火球！`

### 需要火球物品時
```
/fireball
```
系統回應：`[火球插件] 你需要拿着火球才能發射！`

## 🔧 開發資訊

### 技術規格
- **Java 版本**: 17+
- **API 版本**: 1.21
- **依賴**: Bukkit/Spigot API

### 專案結構
```
Fire_Ball/
├── src/main/java/com/example/fireballplugin/
│   └── FireballPlugin.java
├── src/main/resources/
│   ├── config.yml
│   ├── plugin.yml
│   └── lang/
│       ├── zh_TW.yml
│       ├── zh_CN.yml
│       └── en_US.yml
├── pom.xml
└── README.md
```

## 🐛 故障排除

### 常見問題

**Q: 插件無法載入？**
A: 請確認伺服器版本為 1.21+，並檢查 Java 版本是否為 17+

**Q: 權限指令無效？**
A: 請確認已正確設定權限節點，或使用 OP 權限測試

**Q: 語言顯示異常？**
A: 請檢查 `config.yml` 中的 `language` 設定是否正確

**Q: 右鍵發射不工作？**
A: 請確認已啟用 `features.right-click` 並擁有 `fireballplugin.rightclick` 權限

**Q: Tab 補全不工作？**
A: 請確認插件已正確載入，並檢查權限設定

**Q: 提示需要火球物品？**
A: 如果啟用了 `features.require-fireball-item`，需要手持火球才能發射

## 📝 更新日誌

### v1.0
- ✨ 新增火焰彈開關功能
- 🌍 新增繁體中文語言支援
- 🔧 完善權限系統
- 🎨 優化特效系統
- 📚 新增完整文檔
- 🎮 新增 Tab 補全功能
- 🔥 新增火球物品檢查功能
- 📦 更新 Maven 配置

## 🤝 貢獻

歡迎提交 Issue 和 Pull Request 來改善這個插件！

## 📄 授權

本專案採用 MIT 授權條款。

## 📞 聯絡資訊

如有任何問題或建議，請透過以下方式聯絡：
- 提交 GitHub Issue
- 發送 Email

---

**享受您的火焰彈冒險！** 🔥
