# ECHO - Virtual Companion App

A wellness-focused Android application featuring a customizable virtual companion that supports emotional well-being through mood tracking, personalized quests, and interactive features.

---

## 📱 About the App

ECHO is a virtual companion app designed to help users track their mood, complete wellness quests, and customize their companion's appearance. The app uses a SQLite database to persist user data, mood logs, quest progress, and accessory purchases.

### Key Features
- **Mood Tracking**: Log daily moods using a 1-5 Likert scale
- **Emotional Companion**: Virtual pet that responds to your mood
- **Wellness Quests**: Complete challenges to earn coins
- **Customization Shop**: Purchase and equip accessories (tops, bottoms, hats, glasses)
- **Profile Management**: Customize companion name and gender
- **Background Music**: Seamless looping music with toggle control

---

## 🗂️ Project Structure

### Java Files (`app/src/main/java/com/example/virtualcompanion/`)

**Core Activities:**
- `OpeningActivity.java` - Splash screen and app entry point
- `CustomizeActivity.java` - Companion name and gender setup
- `MoodActivity.java` - Daily mood logging interface
- `MoodResultActivity.java` - Displays emotional feedback
- `QuestsActivity.java` - Shows and manages wellness quests
- `SettingsActivity.java` - Profile and app settings

**Customization System:**
- `CustomTopActivity.java` - Top clothing customization
- `CustomBottomActivity.java` - Bottom clothing selection
- `CustomHatActivity.java` - Hat accessories
- `CustomGlassesActivity.java` - Glasses management

**Utilities:**
- `BaseActivity.java` - Parent activity with music management
- `MyApplication.java` - Application-level initialization
- `MusicManager.java` - Background music controller

**Database:**
- `DatabaseHelper.java` - SQLite database schema and creation
- `DatabaseManager.java` - CRUD operations handler

**Adapters:**
- `ShopItemAdapter.java` - RecyclerView adapter for shop items
- `QuestsAdapter.java` - RecyclerView adapter for quest cards

### Layout Files (`app/src/main/res/layout/`)

- `activity_opening.xml` - Opening screen
- `activity_customize.xml` - Name and gender input
- `activity_mood.xml` - Mood logging interface
- `activity_mood_result.xml` - Emotional response display
- `activity_quests.xml` - Quest list screen
- `activity_custom_top.xml` - Top accessories shop
- `activity_custom_bottom.xml` - Bottom clothing shop
- `activity_custom_hat.xml` - Hat shop
- `activity_custom_glasses.xml` - Glasses shop
- `activity_settings.xml` - Settings screen
- `item_shop_item.xml` - Shop item card layout
- `item_quest.xml` - Quest card layout

---

## 🗄️ Database Schema

### Table: `user`
Stores user profile and game progress.

| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PRIMARY KEY | User ID |
| name | TEXT | Companion name |
| gender | TEXT | Companion gender (Boy/Girl) |
| coins | INTEGER | User's currency |
| last_mood_date | TEXT | Last mood entry date |

### Table: `mood`
Records daily mood entries.

| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PRIMARY KEY | Entry ID |
| user_id | INTEGER | Foreign key to user |
| mood_value | INTEGER | Mood rating (1-5) |
| date | TEXT | Entry date |
| timestamp | TEXT | Entry timestamp |

### Table: `quest`
Stores wellness quests and completion status.

| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PRIMARY KEY | Quest ID |
| title | TEXT | Quest title |
| description | TEXT | Quest details |
| reward_coins | INTEGER | Coin reward |
| done | INTEGER | Completion (0=incomplete, 1=complete) |
| date | TEXT | Quest date |

### Table: `accessory`
Manages clothing and accessory items.

| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PRIMARY KEY | Accessory ID |
| name | TEXT | Item name |
| type | TEXT | Category (top/bottom/hat/glasses) |
| price | INTEGER | Cost in coins |
| owned | INTEGER | Ownership (0=not owned, 1=owned) |
| equipped | INTEGER | Equipped status (0=not equipped, 1=equipped) |
| gender | TEXT | Gender compatibility |
| image_resource | TEXT | Drawable resource name |

---

## 🎯 App Flow

### First Launch
1. **OpeningActivity** → Tap to start
2. **CustomizeActivity** → Enter companion name and gender → Save to `user` table
3. **MoodActivity** → Log mood (1-5) → Save to `mood` table
4. **MoodResultActivity** → See companion's emotional response

### Daily Use
1. **OpeningActivity** → Check if mood logged today
    - If not logged → **MoodActivity**
    - If already logged → **MoodResultActivity**
2. Navigate between:
    - **Home** (MoodResultActivity)
    - **Quests** (QuestsActivity)
    - **Customize** (CustomTopActivity)

### Customization Flow
1. **CustomTopActivity** (or Bottom/Hat/Glasses)
2. Browse accessories in horizontal RecyclerView
3. Check if owned:
    - If owned → Equip/Unequip (update `accessory.equipped`)
    - If not owned → Purchase (deduct `user.coins`, set `accessory.owned = 1`)
4. View changes on pet display

---

## 🎵 Background Music

**Implementation:**
- `MusicManager.java` - Singleton pattern for music control
- `MyApplication.java` - Starts music on app launch
- `BaseActivity.java` - All activities extend this for music continuity
- `background_music.mp3` - Located in `res/raw/`

**Features:**
- Seamless looping with no gaps
- Continues playing between activities
- Toggle on/off in Settings
- Volume set to 40%

---

## 🚀 Setup Instructions

1. **Clone the repository**
2. **Open in Android Studio**
3. **Add music file:**
    - Place `background_music.mp3` in `app/src/main/res/raw/`
4. **Sync Gradle**
5. **Run on emulator or device**

---

## 📋 Requirements

- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34
- **Language:** Java
- **Database:** SQLite

---

## 🎮 How to Use

1. **First Launch:**
    - Tap anywhere on opening screen
    - Enter your companion's name
    - Select gender (Male/Female)

2. **Daily Mood Logging:**
    - Select your mood (1-5 emojis)
    - Press Submit
    - View companion's emotional response

3. **Complete Quests:**
    - Navigate to Quests tab
    - Complete wellness activities
    - Check completion box to earn coins

4. **Customize Companion:**
    - Navigate to Customize tab
    - Browse categories (Top/Bottom/Hat/Glasses)
    - Purchase with coins or equip owned items
    - See changes instantly on your companion

5. **Settings:**
    - Access via settings icon
    - Change companion name/gender
    - Toggle background music
    - Save changes

---

## 📝 Notes

- Mood can only be logged once per day
- All navigation buttons are disabled until mood is logged
- Accessories are gender-specific
- Quest rewards are added to coins immediately upon completion
- Background music continues seamlessly between screens

---

## 📄 License

This project is for educational purposes.
