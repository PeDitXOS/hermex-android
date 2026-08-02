# Hermex Android

> **Hermes Agent** — the mobile client for your AI coding partner. Full iOS feature parity, Material 3 polish, Persian RTL support.

---

## ✨ Features

### Chat Core
- **Streaming responses** — token-by-token with debounced auto-scroll (100ms intervals)
- **Steer while streaming** — guide the model mid-response without canceling
- **Edit & Fork** — truncate history at any user message and branch off
- **Regenerate** — re-run the last assistant turn
- **Context window indicator** — live token budget bar
- **Copy Code** — one-tap copy for code blocks
- **Timestamps** — per-message time display

### Voice & Audio
- **Hold-to-talk voice notes** — AAC/M4A, max 5 min, visual timer
- **TTS playback** — per-message Listen/Stop with MediaPlayer cleanup
- **Inline audio player** — streaming playback with seek

### Markdown & Formatting
- **GFM support** — bold, italic, inline code, fenced blocks, lists, tables, strikethrough
- **Syntax highlighting** — via Markwon + custom theme
- **Link previews** — URL metadata cards
- **LaTeX math** — KaTeX-style rendering
- **RTL/LTR auto-detect** — Persian/Arabic/Hebrew layouts via `rtlChatLayoutEnabled` setting

### Slash Commands (26)
| Command | Description |
|---------|-------------|
| `/help` | Show all commands |
| `/clear` | Clear session |
| `/compact` | Summarize & compress |
| `/steer` | Guide streaming response |
| `/fork` | Branch from message |
| `/edit` | Edit last user message |
| `/regenerate` | Re-run last response |
| `/listen` | TTS current message |
| `/stop` | Cancel stream |
| `/model` | Pick model |
| `/profile` | Switch profile |
| `/workspace` | Change workspace |
| `/project` | Assign project |
| `/export` | Export markdown |
| `/search` | Search messages |
| `/think` | Toggle reasoning |
| `/tools` | Toggle tool calls |
| `/context` | Show context window |
| `/kanban` | Open Kanban board |
| `/git` | Git write actions |
| `/shortcuts` | App shortcuts |
| `/settings` | Open settings |
| `/voice` | Record voice note |
| `/image` | Attach image |
| `/file` | Attach file |
| `/cancel` | Cancel operation |

### Git Write (Full UI)
- **Commit** — staged changes with message
- **Push / Pull** — remote sync
- **Checkout** — branch switching
- **Discard** — revert local changes
- **Status** — live diff preview

### Kanban Board
- Columns: Backlog → Todo → In Progress → Review → Done
- Drag-drop cards, persist to server

### Settings (16 toggles)
| Category | Toggles |
|----------|---------|
| **Streaming** | Steer behavior, auto-scroll, token count |
| **Display** | Markdown, LaTeX, compact mode, timestamps, group by date |
| **Haptics/Sound** | Vibration on send/receive, sound on send/receive |
| **Screen** | Keep screen on, RTL layout |
| **Auto** | Auto-title, context window indicator |

### Onboarding
- 3-page flow (Welcome, Permissions, First Session)
- First-launch tracking via DataStore

### Export & Share
- Markdown export with metadata
- Android Share Sheet integration

### File Preview
- Text/Markdown/Code viewer
- Image preview with zoom

### App Shortcuts
- New Chat, New Voice Chat, Open Workspace

### Identity Editor
- Display name + initials
- Per-device identity

### Model Favorites
- Per-server favorite models
- Quick-pick from composer

### Provider Health
- Live status indicators (green/yellow/red)
- Latency display

### Design System
- **AdaptiveGlass** — frosted glass cards
- **HapticFeedback** — typed vibration patterns
- **ThemePicker** — Light/Dark/System + custom accent

### Architecture
- **MVVM + StateFlow** — unidirectional data flow
- **Retrofit + SSE** — streaming via OkHttp
- **Room + DataStore** — offline cache + preferences
- **KSP + Room** — compile-time SQL verification
- **Compose Material 3** — modern UI toolkit

---

## 📦 Build

### Prerequisites
- JDK 21
- Android SDK 36 (API 36)
- Gradle 8.10+
- Release keystore (for signed artifacts)

### Local Debug Build
```bash
./gradlew assembleDebug
```

### Signed Release (GitHub Actions)
1. Add secrets to repository:
   - `KEYSTORE_BASE64` — `base64 -w0 < release.keystore`
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`

2. Push tag:
```bash
git tag -a v1.0.4 -m "Release v1.0.4"
git push origin v1.0.4
```

3. Workflow builds `app-release.aab` → creates GitHub Release (not draft)

### Versioning
| Version | Code | Notes |
|---------|------|-------|
| 1.0.4 | 104 | Full iOS parity, RTL, Markwon, Git Write, Kanban, Onboarding |
| 1.0.3 | 103 | Package rename to com.peditx.hermex, network security config |
| 1.0.2 | 102 | Initial feature parity push |

---

## 🔧 Configuration

### Network Security
- `usesCleartextTraffic=false` — HTTPS only
- `network_security_config.xml` — pinned domains (9router, GitHub, API endpoints)

### Package
```
com.peditx.hermex
```
No conflict with upstream `com.hermex.android`

### Deep Links
- `hermex://` — primary
- `hermes-agent://` — iOS parity

### FileProvider
- Authority: `${applicationId}.fileprovider`
- Paths: `res/xml/file_paths.xml`

---

## 📱 Screenshots

| Chat | Composer | Settings | Kanban |
|------|----------|----------|--------|
| ![Chat](.github/assets/chat.png) | ![Composer](.github/assets/composer.png) | ![Settings](.github/assets/settings.png) | ![Kanban](.github/assets/kanban.png) |

---

## 🤝 Contributing

1. Fork `PeDitXOS/hermex-android`
2. Create feature branch
3. Commit with conventional messages
4. Push → PR to `feature/ios-parity`

### Code Style
- Kotlin 2.2.20 + Compose Compiler 1.6.10
- `ktlint` + `detekt` (CI)
- Ponytail philosophy: YAGNI → stdlib → native → minimal code

---

## 📄 License

MIT — see [LICENSE](LICENSE)

---

## 🙏 Credits

- **Hermes Agent** — Nous Research
- **iOS Reference** — uzairansaruzi/hermex
- **Original Android** — ComputerByte/hermex-android
- **Icons** — Material Icons, Material 3
- **Markdown** — Markwon (noties.io)

---

**Built with ❤️ by PeDitXOS**  
`https://github.com/PeDitXOS/hermex-android`