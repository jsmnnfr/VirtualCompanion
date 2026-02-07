package com.example.virtualcompanion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper
 *
 * This class:
 * - Creates the database
 * - Creates all tables
 * - Handles upgrades
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database file name
    private static final String DB_NAME = "virtual_companion.db";

    // Change this if you modify tables later
    private static final int DB_VERSION = 8; // Incremented for accessory table

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called automatically when DB is created first time
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // ================= USER TABLE =================
        // Stores main player data
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS user (" +

                        // Unique ID (auto-generated)
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        // User / pet name (cannot be empty)
                        "name TEXT NOT NULL, " +
                        // User coins (default 0)
                        "coins INTEGER NOT NULL DEFAULT 0, " +
                        // Pet gender (only allowed values)
                        "pet_gender TEXT NOT NULL CHECK " +
                        "(pet_gender IN ('male','female'))" +
                        ");"
        );

        // ================= ACCESSORY TABLE =================
        // Stores shop & equipped items
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS accessory (" +

                        // Unique ID
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        // Drawable resource ID
                        "image INTEGER NOT NULL, " +
                        // Item price
                        "price INTEGER NOT NULL, " +
                        // Category
                        "type TEXT NOT NULL CHECK " +
                        "(type IN ('top','bottom','hat','glasses')), " +
                        // 0 = not owned, 1 = owned
                        "owned INTEGER NOT NULL DEFAULT 0 CHECK (owned IN (0,1)), " +
                        // 0 = not equipped, 1 = equipped
                        "equipped INTEGER NOT NULL DEFAULT 0 CHECK (equipped IN (0,1))" +
                        ");"
        );

        // ================= QUEST TABLE =================
        // Stores quest progress
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS quest (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        // Quest title
                        "title TEXT NOT NULL, " +
                        // Quest description
                        "description TEXT, " +
                        // Coins reward
                        "reward INTEGER NOT NULL DEFAULT 0, " +
                        // Timer duration in minutes
                        "timer_minutes INTEGER NOT NULL DEFAULT 5, " +
                        "progress INTEGER NOT NULL DEFAULT 0, " +
                        // 0 = not done, 1 = done
                        "rewarded INTEGER NOT NULL DEFAULT 0 CHECK (rewarded IN (0,1)), " +
                        // Mood category (neutral, happy, sad, angry, anxious)
                        "mood TEXT NOT NULL CHECK " +
                        "(mood IN ('neutral','happy','sad','angry','anxious'))" +
                        ");"
        );

        // ================= MOOD TABLE =================
        // Stores mood history
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS mood (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        // Mood value (1 to 5 only)
                        "value INTEGER NOT NULL CHECK (value BETWEEN 1 AND 5), " +
                        // Date string
                        "date TEXT NOT NULL" +
                        ");"
        );

        // Insert default values
        insertDefaults(db);
    }

    /**
     * Insert starting data (runs once)
     */
    private void insertDefaults(SQLiteDatabase db) {

        db.execSQL(
                "INSERT OR IGNORE INTO user (id, name, coins, pet_gender) " +
                        "VALUES (1,'',150,'male');"
        );


        db.execSQL(
                "INSERT OR IGNORE INTO quest (title, description, reward, timer_minutes, mood) VALUES " +

                        // ========== NEUTRAL (21 quests) - Grounding and present awareness ==========

                        "('Box Breathing','Breathe in for 4 counts. Hold for 4. Breathe out for 4. Hold for 4. Repeat this 4 times. This calms your body and mind.',30,1,'neutral')," +
                        "('Water Sipping','Get a glass or bottle of water. Take 5 small sips. Feel the water travel down your throat each time. Notice the sensation.',30,1,'neutral')," +
                        "('Three Good Things','Think of three things from today. One thing you saw. One thing you heard. One thing you felt. Name each one.',30,1,'neutral')," +
                        "('Neck Stretch','Drop your chin toward your chest. Hold 5 seconds. Return to center. Tilt your head back gently. Hold 5 seconds. Return to center. Repeat 3 times.',30,1,'neutral')," +
                        "('Mindful Steps','Walk around your space for 1 minute. Feel your heel touch down first, then your toes. Count each step. Notice the rhythm.',30,1,'neutral')," +
                        "('Quick Reset','Look around your space. Spot 5 things out of place. Pick them up one at a time. Return each one to where it belongs.',50,2,'neutral')," +
                        "('Body Check','Close your eyes. Notice your toes, legs, belly, chest, arms, and head. Just observe how each part feels. No need to change anything.',30,1,'neutral')," +
                        "('Shoulder Release','Lift both shoulders toward your ears. Hold tight for 5 seconds. Drop them suddenly. Feel the release. Repeat 5 times.',30,1,'neutral')," +
                        "('Touch Textures','Find something rough. Touch it for 10 seconds. Find something smooth. Touch it for 10 seconds. Find something soft. Touch it for 10 seconds.',30,1,'neutral')," +
                        "('Mindful Bite','Choose one small food item you have. Look at it closely. Smell it. Place it in your mouth. Chew 15 times. Notice the flavors before swallowing.',30,1,'neutral')," +
                        "('Hand Warmth','Rub your hands together for 15 seconds. Stop. Hold them 1 inch apart. Feel the warmth between them. Move them closer and farther apart.',30,1,'neutral')," +
                        "('Full Body Stretch','Reach both arms up. Lean gently right. Return to center. Lean gently left. Roll shoulders back 5 times. Take 3 deep breaths.',30,1,'neutral')," +
                        "('Window Watch','Look outside for 90 seconds. Find 5 things moving and 5 things still. Just observe without thinking too much.',50,2,'neutral')," +
                        "('Face Release','Scrunch your whole face tight for 5 seconds. Release everything. Notice the difference. Repeat 3 times.',30,1,'neutral')," +
                        "('Sip and Pause','Get any drink you have. Take 7 small sips over 2 minutes. Pause between each sip. Focus on the taste and sensation.',50,2,'neutral')," +
                        "('Listen to Music','Play one song you enjoy. Close your eyes. Try to hear each instrument. When your mind wanders, gently bring it back to the music.',50,3,'neutral')," +
                        "('Free Drawing','Get paper and a pen. Draw continuous lines for 2 minutes without lifting your pen. No pictures needed. Just move your hand freely.',50,2,'neutral')," +
                        "('Tension Check','Check your jaw, shoulders, hands, and stomach. Notice where you feel tension. Just observe it. Take a breath.',30,1,'neutral')," +
                        "('Body Shake','Shake your right hand for 10 seconds. Left hand for 10 seconds. Right foot. Left foot. Then shake your whole body for 10 seconds.',30,1,'neutral')," +
                        "('One Small Task','Pick the smallest task you can. Reply to one message. Wash one dish. Fold one item. Do just that one thing. Notice the completion.',50,2,'neutral')," +
                        "('Kind Words to Self','Say something kind to yourself out loud or silently. For example: I am doing my best. I am enough. I deserve kindness. Choose words that feel right for you.',30,1,'neutral')," +

                        // ========== HAPPY (21 quests) - Building and savoring positive emotions ==========

                        "('Power Smile','Smile as wide as you can for 60 seconds. Even a forced smile helps improve your mood.',30,1,'happy')," +
                        "('Freedom Dance','Play an upbeat song. Dance however you want for 90 seconds. No rules. No judgments. Just move freely.',50,2,'happy')," +
                        "('Sing or Hum','Pick any song or just make sounds. Sing out loud or hum for 1 minute. Making sounds helps release good feelings.',30,1,'happy')," +
                        "('Happy Memory','Close your eyes. Recall one happy moment in detail. Picture who was there, what you saw, what you heard. Stay with this for 90 seconds.',50,2,'happy')," +
                        "('Laughter Break','Watch one short funny video for 2 minutes. Puppies, comedy, whatever makes you laugh. Laughter helps reduce stress.',50,2,'happy')," +
                        "('Victory Move','Stand up. Do your best victory celebration. Fist pump, jump, dance, cheer. Your body creates the feeling through movement.',30,1,'happy')," +
                        "('Fresh Air','Open a window or step outside if possible. Take 5 deep breaths. Notice the air moving in and out of your lungs.',30,1,'happy')," +
                        "('Joy Song','Play your happiest song. Sing along if you want. Feel the energy and positivity of the music.',50,2,'happy')," +
                        "('Specific Gratitude','Name 3 things you are grateful for. For each one, say exactly why. Be specific about what makes it meaningful.',30,1,'happy')," +
                        "('Energy Burst','Do 15 jumping jacks or march in place for 45 seconds. Movement helps boost your mood and energy.',30,1,'happy')," +
                        "('Positive Journal','Write 2 or 3 sentences about something good from today. Include how it made you feel.',50,2,'happy')," +
                        "('Cuteness Therapy','Look at pictures or videos of baby animals for 90 seconds. Cute images help improve your mood.',50,2,'happy')," +
                        "('Savor a Bite','Choose something you enjoy eating. Take tiny bites. Chew many times. Focus only on the taste. Make it last.',30,1,'happy')," +
                        "('Self-Kindness','Do one kind thing for yourself right now. Give yourself a compliment. Let yourself rest. Treat yourself well.',30,1,'happy')," +
                        "('Confidence Pose','Stand tall with hands on hips or arms raised. Hold this for 60 seconds. This posture helps build confidence.',30,1,'happy')," +
                        "('Future Joy','Close your eyes. Picture one fun thing coming up this week. Imagine it happening. Feel the excitement.',30,1,'happy')," +
                        "('Celebrate Yourself','Say out loud: One thing I did well today was... Complete the sentence with your own words. Then say: I am proud of that.',30,1,'happy')," +
                        "('Joy Photo','Take a photo of something that makes you smile right now. Look at it for 30 seconds. Send it to someone if you want.',30,1,'happy')," +
                        "('Best Memory','Think of your favorite memory ever. Close your eyes. Relive it fully for 90 seconds. What made it so special?',50,2,'happy')," +
                        "('Mini Win Celebration','Think of one small thing you accomplished today. Stand up. Celebrate it with a gesture or sound. Small wins matter.',30,1,'happy')," +
                        "('Playful Movement','Skip, hop, spin, twirl, or move playfully for 60 seconds. Playful movement helps reduce stress and increase joy.',30,1,'happy')," +

                        // ========== SAD (21 quests) - Self-compassion and gentle support ==========

                        "('Extended Exhale','Breathe in for 4 counts. Breathe out for 6 counts. Longer breaths out help calm your body. Do this 6 times.',30,1,'sad')," +
                        "('Name Your Feeling','Complete this sentence in your own words: Right now I feel... Say it out loud or write it. Naming emotions makes them feel smaller.',30,1,'sad')," +
                        "('Comfort Song','Play one song that makes you feel understood. Music that meets you where you are. Just listen.',50,2,'sad')," +
                        "('Gentle Walk','Walk around your space for 90 seconds. No rush. No destination. Just gentle movement to help process what you feel.',50,2,'sad')," +
                        "('Self-Soothing Touch','Place one hand on your chest, one on your belly. Feel them rise and fall with your breath. This brings comfort.',30,1,'sad')," +
                        "('Permission to Feel','Say something that gives you permission to feel. For example: My feelings are valid. It is okay to struggle. I do not have to be okay right now. Use your own words.',30,1,'sad')," +
                        "('Compassion Words','Say something compassionate to yourself 3 times. For example: I am having a hard time. May I be kind to myself. This is difficult and I deserve care. Choose words that feel right.',30,1,'sad')," +
                        "('Physical Comfort','Put on soft comfortable clothes or wrap yourself in a blanket. Physical comfort supports emotional comfort.',30,1,'sad')," +
                        "('Water Comfort','Splash water on your face and wrists. Pat dry gently. Temperature change helps reset how you feel.',30,1,'sad')," +
                        "('Hope Anchor','Find one photo from a time you felt okay or happy. Look at it. Remind yourself you have felt different before.',30,1,'sad')," +
                        "('Soothing Drink','Get any drink you have. Sip it gently and mindfully. Focus on the taste and sensation with each sip.',50,2,'sad')," +
                        "('Soothing Sounds','Play gentle rain, ocean waves, or soft music for 2 minutes. Close your eyes. Let the sound surround you.',50,2,'sad')," +
                        "('Weighted Comfort','Hold a pillow against your chest or place a blanket on your lap. Gentle pressure brings calm.',30,1,'sad')," +
                        "('Kind Reading','Read one compassionate quote or write something kind to yourself. For example: I deserve kindness, especially from myself. Use words that comfort you.',30,1,'sad')," +
                        "('Safe Space','Go to your most comfortable spot. Sit or lie down. Just be here. You do not need to do anything else.',30,1,'sad')," +
                        "('Small Hope','Name one tiny thing you might look forward to. It can be very small. A meal. A show. Rest. Anything at all.',30,1,'sad')," +
                        "('Tender Stretch','Do one very gentle stretch. Roll your neck. Reach your arms up softly. Be kind to your body.',30,1,'sad')," +
                        "('Emotional Release','Write whatever you feel for 2 minutes. No editing. No judging. Just let words flow onto paper.',50,2,'sad')," +
                        "('Gentle Watching','Watch something calming. Clouds moving. A candle. Fish swimming. Leaves blowing. Watch for 90 seconds.',50,2,'sad')," +
                        "('Strength Reminder','Say something that reminds you of your strength. For example: I have survived hard times before. I have made it through every difficult day. I have resilience. Use your own words.',30,1,'sad')," +
                        "('Self-Compassion Break','Place your hand over your heart. Say something compassionate to yourself. For example: This is hard. I am not alone. May I give myself compassion. Choose words that feel right.',30,1,'sad')," +

                        // ========== ANGRY (21 quests) - Safe energy release and regulation ==========

                        "('Power Breathing','Sharp breath in through your nose. Forceful breath out through your mouth. Do 8 rounds. This releases built-up tension.',30,1,'angry')," +
                        "('Energy Release','Run in place as fast as you can for 45 seconds. Physical movement helps burn off anger.',30,1,'angry')," +
                        "('Pillow Punch','Get a pillow. Hit it or punch it as hard as you want for 30 seconds. This is a safe way to release anger.',30,1,'angry')," +
                        "('Scream Release','Press a pillow against your face. Scream into it as loud as you need. Letting sound out helps regulate your system.',30,1,'angry')," +
                        "('Anger Writing','Write furiously for 2 minutes. Say exactly how you feel. Hold nothing back. Tear it up after if you want.',50,2,'angry')," +
                        "('Cold Water Splash','Splash cold water on your face, neck, and wrists if available. Cold helps activate calm in your body.',30,1,'angry')," +
                        "('Countdown Reset','Count backwards from 50 out loud. Only numbers. This interrupts the anger pattern in your mind.',50,2,'angry')," +
                        "('Intense Music','Play loud or intense music that matches your energy. Let the music hold your anger for 2 minutes.',50,2,'angry')," +
                        "('Channel Energy','Scrub something hard for 90 seconds. Dishes, counters, anything. Turn anger into productive action.',50,2,'angry')," +
                        "('Power Punches','Punch into the air as hard as you can. Do 25 fast punches. Make sounds if you want. Release the energy.',30,1,'angry')," +
                        "('Intense Grip','Squeeze your fists as tight as you can. Hold for 30 seconds. Release. Repeat. Strong tension followed by release helps.',30,1,'angry')," +
                        "('Stomp It Out','Stomp your feet hard. March and stomp for 60 seconds. Make noise. Let the impact release the feeling.',30,1,'angry')," +
                        "('Paper Destruction','Get scrap paper or old magazines. Rip them into pieces. Rip as much as you want. Safe destruction helps.',30,1,'angry')," +
                        "('Voice It','Say out loud why you are angry. All of it. Speaking it helps release it from your body.',30,1,'angry')," +
                        "('Squeeze and Release','Squeeze both fists tight. Hold 10 seconds. Release completely. Feel the difference. Repeat 4 times.',30,1,'angry')," +
                        "('Physical Distance','Walk to another room or space. Moving your body away creates mental distance too.',30,1,'angry')," +
                        "('Opposite Image','Close your eyes. Picture a calm peaceful place in detail. Beach, forest, meadow. Opposite scenes help regulate.',50,2,'angry')," +
                        "('Anger Recording','Record yourself saying everything you are angry about. Say it all. Delete it after if you want.',50,2,'angry')," +
                        "('Wall Push','Face a wall. Push against it hard for 15 seconds. Rest 10 seconds. Repeat 3 times. This releases tension.',30,1,'angry')," +
                        "('One Solution','Write this in your own words: One small thing I can control is... Then write one action you can actually take.',50,2,'angry')," +
                        "('Explosive Jumps','Do 20 jumps. Jump as hard and high as you can. Big movements help release anger from your body.',30,1,'angry')," +

                        // ========== ANXIOUS (21 quests) - Grounding and calming regulation ==========

                        "('4-7-8 Breathing','Breathe in for 4 counts. Hold for 7. Breathe out for 8. Do 4 full rounds. This helps activate calm.',50,2,'anxious')," +
                        "('5-4-3-2-1 Grounding','Say out loud: 5 things you see. 4 things you hear. 3 things you touch. 2 things you smell. 1 thing you taste.',50,2,'anxious')," +
                        "('Worry Dump','Write every worry on paper for 2 minutes. Do not organize. Just empty your anxious mind completely.',50,2,'anxious')," +
                        "('Body Location','Scan your body. Where exactly do you feel anxiety? Chest? Throat? Stomach? Just notice the spot.',30,1,'anxious')," +
                        "('Nature Sounds','Play rain, ocean, or forest sounds for 2 minutes. Close your eyes. Let natural sounds soothe you.',50,2,'anxious')," +
                        "('Label It','Complete this sentence in your own words: I feel anxious about... Naming anxiety makes it feel more manageable.',30,1,'anxious')," +
                        "('Progressive Relaxation','Squeeze fists tight. Hold 7 seconds. Release completely. Notice the difference. Repeat 4 times.',30,1,'anxious')," +
                        "('Safe Place','Close your eyes. Picture the safest place you know. Notice every detail. Stay there for 90 seconds.',50,2,'anxious')," +
                        "('Mindful Sipping','Get any drink you have. Take 10 very small sips over 2 minutes. Focus on each sip completely.',50,2,'anxious')," +
                        "('Cool Water','Drink water one small sip at a time. Cool sensations can help calm anxiety.',30,1,'anxious')," +
                        "('Evidence of Survival','Say something that reminds you of your past strength. For example: I have felt anxious before and survived. I have gotten through this every time. Use your own words.',30,1,'anxious')," +
                        "('Reality Check','Ask yourself: Is this thought completely true? What real evidence do I have? Separate thought from fact.',30,1,'anxious')," +
                        "('Grounding Steps','Walk gently for 90 seconds. Feel each foot touch the ground. Count your steps. Focus only on walking.',50,2,'anxious')," +
                        "('Palm Pressure','Press palms together firmly in front of your chest. Hold 45 seconds. Feel the pressure. This brings you into your body.',30,1,'anxious')," +
                        "('Weighted Grounding','Place something heavy on your lap. A book, blanket, or pillow. Weight helps bring calm.',30,1,'anxious')," +
                        "('Scent Focus','Smell something with a strong scent. Soap, lotion, food, anything available. Focus only on the smell. Scent can override anxiety.',30,1,'anxious')," +
                        "('Safety Statement','Say something that reminds you that you are safe. For example: I am safe right now. This moment is okay. I can handle this. Repeat 10 times. Use your own words.',30,1,'anxious')," +
                        "('Pattern Drawing','Draw repetitive patterns. Circles, spirals, lines, or dots. Repetition helps calm the anxious mind.',50,2,'anxious')," +
                        "('Control Focus','Write this in your own words: One thing I can actually control right now is... Then write one small action within your power.',30,1,'anxious')," +
                        "('Cool Hands','Run your hands under cool water for 45 seconds if available. Cool sensations on hands help activate calm.',30,1,'anxious')," +
                        "('Mental Math','Count backwards from 100 by 7s out loud. Say each number clearly. This interrupts anxious thought loops.',50,2,'anxious');" +
                        ""
        );
    }

    /**
     * Handle database upgrades
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Add timer column if upgrading from version 6 to 7
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE quest ADD COLUMN timer_minutes INTEGER NOT NULL DEFAULT 5");
        }

        // Add accessory table if upgrading to version 8
        if (oldVersion < 8) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS accessory (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "image INTEGER NOT NULL, " +
                            "price INTEGER NOT NULL, " +
                            "type TEXT NOT NULL CHECK " +
                            "(type IN ('top','bottom','hat','glasses')), " +
                            "owned INTEGER NOT NULL DEFAULT 0 CHECK (owned IN (0,1)), " +
                            "equipped INTEGER NOT NULL DEFAULT 0 CHECK (equipped IN (0,1))" +
                            ");"
            );
        }
    }
}