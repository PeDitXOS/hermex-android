# Hermex Android — Parity & Polish Goal List

Comparing `hermex-android` (ComputerByte) vs `hermex-ios` (uzairansaruzi/hermex).
Build via **GitHub Actions only** — no local compilation on this server.

---

## Phase 1: Chat — Edit, Steer & Voice (اولویت اول)

### 1.1 Edit Message (موجود ولی ناقص)
Android editMessage فقط truncate + pre-fill composer می‌کنه.
iOS یه Edit Sheet جداگانه باzTextEditor نشون می‌ده.

- [ ] **Edit Sheet**: اضافه کردن EditMessageSheet (bottom sheet با TextEditor) به جای pre-fill composer
- [ ] **Edit warning**: نشون دادن هشدار "Editing will discard N later messages" مثل iOS
- [ ] **User messages only**: فقط پیام‌های user قابل edit باشن
- [ ] **Disabled during stream**: Edit غیرفعال باشه وقتی response داره stream می‌شه

### 1.2 Steer (کاملاً缺失)
Android: `TODO(v0.3.0 audit): steer-while-running NOT implemented`
iOS: `/steer` slash command + `POST /api/chat/steer` + `StreamingSendBehavior` setting

- [ ] **API endpoint**: اضافه کردن `steerChat(sessionID, text)` به HermexApi
- [ ] **StreamingSendBehavior setting**: تنظیم رفتار ارسال در حین streaming:
  - `steer` (پیش‌فرض) — پیام رو به عنوان steer بفرست
  - `cancel-and-send` — استریم رو cancel کن بعد بفرست  
  - `queue` — توی صف منتظر بذار
- [ ] **Slash command**: `/steer <message>` به CommandRegistry اضافه شه
- [ ] **UI integration**: وقتی response داره stream می‌شه و user پیام می‌فرسته، steer فعال باشه
- [ ] **SSE event**: `pending_steer_leftover` event رو handle کن

### 1.3 Fork / Branch (کاملاً缺失)
iOS: "Fork From Here" در message context menu → `POST /api/chat/branch`

- [ ] **API**: اضافه کردن `branchSession(sessionID)` endpoint
- [ ] **Fork From Here**: در long-press menu پیام‌ها اضافه شه
- [ ] **Session navigation**: بعد از fork، کاربر به session جدید هدایت شه
- [ ] **Slash command**: `/fork` و `/branch` اضافه شه

### 1.4 Message Context Menu کامل
iOS 6 action داره، Android فقط 3 تا:

| Action | iOS | Android |
|--------|-----|---------|
| Copy | ✅ | ✅ |
| Edit Message | ✅ (sheet) | ⚠️ (pre-fill only) |
| Fork From Here | ✅ | ❌ |
| Regenerate Response | ✅ | ⚠️ (retry last only) |
| Listen (TTS) | ✅ | ❌ |
| Select Text | ✅ | ❌ |

- [ ] **Regenerate from point**: Regenerate از یه پیام خاص (نه فقط last user message)
- [ ] **Listen / TTS**: پخش صوتی پاسخ assistant (port InlineAudioPlayerView + TTS API)
- [ ] **Select Text**: متن پاسخ رو قابل انتخاب کن
- [ ] **Context menu UI**: منوی long-press رو با آیکون‌ها و disabled states کامل کن

### 1.5 Slash Commands (4 vs 26)
iOS: help, clear, model, workspace, reasoning, new, stop, title, personality, skills, compress, compact, retry, undo, branch, fork, queue, steer, interrupt, status, goal, btw, background, bg
Android: /edit, /continue, /summarize, /search

- [ ] اضافه کردن دستورات گمشده: `/stop`, `/steer`, `/fork`, `/branch`, `/retry`, `/undo`, `/compress`, `/compact`, `/status`, `/goal`, `/new`, `/clear`, `/model`, `/skills`, `/personality`, `/reasoning`, `/title`, `/interrupt`, `/queue`, `/btw`, `/background`, `/bg`

---

## Phase 2: Voice & Audio (صدا)

### 2.1 Voice Note Recording (缺失)
iOS: `ComposerVoiceNoteRecorder` — hold-to-talk, AAC/M4A recording, slide-to-cancel
Android: فقط basic `VoiceInputHandler` (Android SpeechRecognizer)

- [ ] **VoiceNoteRecorder**: کلاس ضبط صدا با MediaRecorder (AAC/M4A)
- [ ] **Hold-to-talk gesture**: Long press روی mic → ضبط، slide up → cancel
- [ ] **Upload voice note**: ضبط شده رو به عنوان attachment آپلود کن
- [ ] **Elapsed timer**: نمایش مدت ضبط
- [ ] **Duration limits**: min 0.5s (accidental tap), max 5min

### 2.2 Inline Audio Player (缺失)
iOS: `InlineAudioPlayerView` — Telegram-style audio player with scrubber
Android: هیچی

- [ ] **Audio player UI**: player با play/pause, scrubber, time display
- [ ] **AVAudioPlayer equivalent**: استفاده از MediaPlayer یا ExoPlayer
- [ ] **Playback center**: همزمان فقط یه کلیپ پخش شه
- [ ] **Audio in chat**: نمایش audio attachments به صورت inline player

### 2.3 TTS / Listen (缺失)
iOS: "Listen" in message context menu + server TTS API
Android: هیچی

- [ ] **Server TTS**: `POST /api/tts` → دریافت audio و پخش
- [ ] **Listen button**: در context menu پیام assistant
- [ ] **Stop listening**: توقف پخش

### 2.4 Server-side STT Enhancement
iOS: Server-side transcription API
Android: فقط on-device SpeechRecognizer

- [ ] **STT provider selector**: on-device vs server (تنظیم در settings)
- [ ] **Server STT API**: اضافه کردن `POST /api/transcribe`

---

## Phase 3: Design & UI Fixes (مشکلات طراحی)

### 3.1 Material 3 Overhaul
- [ ] **FAB**: M3 Extended FAB (tonal surface color, ambient shadow)
- [ ] **Surface elevation**: M3 tonal elevation (Surface / Surface Container)
- [ ] **Spacing**: افزایش padding — session list، chat، workspace فشرده‌ان
- [ ] **Color system**: جایگزینی yellow accent با M3 color scheme (WCAG fail)
- [ ] **Typography**: جایگزین monospace metadata با M3 type scale

### 3.2 Navigation Drawer
- [ ] آیکون به Recents items اضافه شه
- [ ] Fix alignment inconsistency
- [ ] "+ New Chat" از drawer bottom به FAB منتقل شه

### 3.3 Chat Screen
- [ ] Composer input area: افزایش height و padding
- [ ] Fix low-contrast text input boundary
- [ ] Streaming text fade-in animation
- [ ] Context window indicator
- [ ] Math/LaTeX rendering
- [ ] Link previews
- [ ] RTL layout support
- [ ] Marker message cards

### 3.4 Model Picker
- [ ] حذف "terminal" controls مبهم
- [ ] Visual tier grouping با section headers
- [ ] Touch target spacing بهبود

### 3.5 Settings Screen
- [ ] Fix low-contrast yellow headings
- [ ] Clean version string (حذف `-dirty` و commit hash)
- [ ] Card alignment بهبود

### 3.6 Workspace / File Browser
- [ ] Hidden files پیش‌فرض مخفی باشن
- [ ] "Folder" text labels ← icon
- [ ] File size + date metadata
- [ ] Fix Git error: "read-onlyNot a git repository"

---

## Phase 4: Feature Parity (Feature‌های گمشده iOS)

### 4.1 Git Write Actions
- [ ] Commit, discard, checkout, push, pull
- [ ] Branch picker, diff viewer, inline commit
- [ ] Turn-level changes card

### 4.2 Kanban Board
- [ ] Kanban feature state, card editor, card detail view
- [ ] Real-time event stream

### 4.3 Providers Management
- [ ] Providers list screen + health indicators

### 4.4 Model Favorites
- [ ] Favorite/unfavorite models, favorites section in picker

### 4.5 Session Export
- [ ] Export as markdown/text, share via Android share sheet

### 4.6 Settings: 15+ Missing Toggles
- [ ] Default Model Picker (full screen)
- [ ] Default Profile Picker
- [ ] CLI/Claude Code/Cron/Subagent Sessions toggles
- [ ] Message Count, Workspace display toggles
- [ ] Thinking/Tool Cards toggles
- [ ] Streamed Text Animation toggle
- [ ] Response Timestamps toggle
- [ ] Wrap Code Block Lines toggle
- [ ] Hide Attachment Paths toggle
- [ ] Identity editor (display name + initials)
- [ ] Archived Sessions screen
- [ ] Primary action tint toggle
- [ ] Streaming Lab (debug)

### 4.7 Onboarding Enhancement
- [ ] Multi-page flow (Welcome → Features → Connect → Agent Prompt)
- [ ] Tailscale guide

### 4.8 File Preview & Export
- [ ] In-app file preview (text, images, PDF)
- [ ] File export/share

### 4.9 App Shortcuts (Android)
- [ ] Android App Shortcuts for "New Chat"
- [ ] Quick Settings tile

### 4.10 Session Display
- [ ] Session row components (consistent styling)
- [ ] Session haptics
- [ ] Session navigation state

### 4.11 Haptic Feedback
- [ ] HapticButton equivalent (Android VibrationEffect)
- [ ] ChatHaptics for interactions

---

## Phase 5: Polish

### 5.1 Streaming
- [ ] Stream reconnect with enhanced replay
- [ ] Context window compression indicator

### 5.2 Chat
- [ ] Tool activity group view
- [ ] Goal controls
- [ ] Pending action coordinator

### 5.3 Server
- [ ] Server update check from app
- [ ] Server panels API

---

## CI/Build

- [ ] GitHub Actions workflow verifies all new features
- [ ] Lint step (ktlint / detekt)
- [ ] R8/minification works with new dependencies

---

**Total: 110+ items across 5 phases**
**Priority: Phase 1 (edit/steer/voice) → Phase 2 (audio) → Phase 3 (design) → Phase 4 (features) → Phase 5 (polish)**
