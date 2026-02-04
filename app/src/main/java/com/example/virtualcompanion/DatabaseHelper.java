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

                        // ========== NEUTRAL (21 quests) - Calm, grounding, present ==========
                        "('Calm Breathing','Breathe in slowly through your nose. Count to 4. Breathe out through your mouth. Count to 4. Do this 5 times.',30,1,'neutral')," +
                        "('Sip Water','Get a glass of water. Sit down. Take slow sips. Notice how it feels going down.',30,1,'neutral')," +
                        "('Three Good Things','Think of 3 small good things from today. Say them out loud or in your mind.',30,1,'neutral')," +
                        "('Neck Stretch','Tilt your head to the left. Hold 3 seconds. Tilt to the right. Hold 3 seconds. Do 3 times.',30,1,'neutral')," +
                        "('Short Walk','Walk slowly around your room or space for 1 minute. Look around as you walk.',30,1,'neutral')," +
                        "('Quick Tidy','Look around. Pick up 3 things that are out of place. Put them where they belong.',30,1,'neutral')," +
                        "('Close Your Eyes','Sit or lie down. Close your eyes. Breathe slowly and calmly for 1 minute.',30,1,'neutral')," +
                        "('Shoulder Rolls','Roll your shoulders backward 5 times slowly. Then roll them forward 5 times.',30,1,'neutral')," +
                        "('Touch Something Soft','Find something soft near you. A pillow, blanket, or clothing. Touch it and notice the texture.',30,1,'neutral')," +
                        "('Eat Mindfully','Get a small snack. Sit down. Chew slowly. Notice the taste and texture.',30,1,'neutral')," +
                        "('Warm Your Hands','Rub your hands together quickly for 10 seconds. Feel them get warm.',30,1,'neutral')," +
                        "('Stand and Stretch','Stand up. Stretch your arms up high above your head. Take one deep breath.',30,1,'neutral')," +
                        "('Look Outside','Go to a window. Look outside for 1 minute. Notice what you see.',30,1,'neutral')," +
                        "('Relax Your Face','Close your eyes. Relax your jaw. Relax your forehead. Relax around your eyes. Stay like this 1 minute.',30,1,'neutral')," +
                        "('Drink Something Warm','Make warm tea, hot water, or hot chocolate. Hold the cup. Sip it slowly.',50,2,'neutral')," +
                        "('Listen to One Song','Play one calm song you like. Sit or lie down. Close your eyes. Just listen.',50,2,'neutral')," +
                        "('Doodle Freely','Get paper and pen. Draw anything. Scribbles, shapes, lines. It does not need to be good.',50,2,'neutral')," +
                        "('Body Scan','Sit quietly. Close your eyes. Notice where your body feels tense or relaxed. Just notice.',50,2,'neutral')," +
                        "('Arm Stretch','Reach both arms up high. Hold 5 seconds. Reach them out to the sides. Hold 5 seconds.',30,1,'neutral')," +
                        "('One Tiny Task','Pick one very small task. Make your bed. Wash one dish. Reply to one message. Do it now.',30,1,'neutral')," +
                        "('Kind Self-Talk','Say this out loud or in your head: I am doing my best. I am enough.',30,1,'neutral')," +

                        // ========== HAPPY (21 quests) - Energizing, joyful, celebrating ==========
                        "('Big Smile','Go to a mirror. Look at yourself. Smile as big as you can. Hold it for 10 seconds.',30,1,'happy')," +
                        "('Dance Break','Play any upbeat song. Move your body however you want. Dance for 1 minute.',50,2,'happy')," +
                        "('Sing Out Loud','Pick any song you know. Sing it out loud for 1 minute. Loud or quiet, your choice.',30,1,'happy')," +
                        "('Happy Memory','Close your eyes. Think of one happy memory. Picture it clearly. Stay with that feeling for 1 minute.',30,1,'happy')," +
                        "('Watch Something Funny','Watch one short funny video. Let yourself laugh.',50,2,'happy')," +
                        "('Victory Dance','Stand up. Do a silly celebration dance for 30 seconds. You earned it.',30,1,'happy')," +
                        "('Fresh Air Moment','Open a window or step outside. Stand there. Breathe the fresh air for 1 minute.',30,1,'happy')," +
                        "('Happy Song','Play one song that always makes you happy. Sing along or just listen and enjoy.',50,2,'happy')," +
                        "('Gratitude Moment','Think of 3 things you are grateful for today. Say them out loud.',30,1,'happy')," +
                        "('Energy Burst','Do 10 jumping jacks or march in place for 30 seconds. Feel the energy.',30,1,'happy')," +
                        "('Write One Happy Thing','Get paper. Write one sentence about what made you happy today.',30,1,'happy')," +
                        "('Cute Animal Time','Look at cute animal photos or videos for 1 minute. Enjoy the cuteness.',30,1,'happy')," +
                        "('Enjoy a Treat','Get something small you like to eat. Sit down. Eat it slowly and enjoy every bite.',30,1,'happy')," +
                        "('Kind Act','Do one tiny kind thing for yourself. Compliment yourself. Give yourself a break.',30,1,'happy')," +
                        "('Power Pose','Stand with your arms raised high or hands on hips. Stand tall. Hold for 30 seconds.',30,1,'happy')," +
                        "('Plan Something Fun','Think of one fun thing you want to do this week. Picture yourself doing it.',30,1,'happy')," +
                        "('Self-Appreciation','Pat yourself on the back. Literally. Say: I did well today.',30,1,'happy')," +
                        "('Happy Photo','Take a photo of something that makes you smile right now. Look at it.',30,1,'happy')," +
                        "('Favorite Memory','Think of your favorite memory ever. Close your eyes. Remember every detail for 1 minute.',30,1,'happy')," +
                        "('Celebrate Small Win','Think of one small thing you accomplished today. Celebrate it. Do a little cheer.',30,1,'happy')," +
                        "('Happy Movement','Move your body in any happy way. Skip, hop, spin, wave arms. Do it for 1 minute.',30,1,'happy')," +

                        // ========== SAD (21 quests) - Gentle, comforting, self-compassionate ==========
                        "('Slow Breathing','Breathe in slowly for 4 counts. Breathe out slowly for 6 counts. Do this 5 times.',30,1,'sad')," +
                        "('Write Your Feelings','Get paper. Write one or two sentences about how you feel right now. Be honest.',30,1,'sad')," +
                        "('Comfort Song','Play one song that soothes you or makes you feel understood. Just listen.',50,2,'sad')," +
                        "('Slow Gentle Walk','Walk very slowly around your room for 1 minute. Be gentle with yourself.',30,1,'sad')," +
                        "('Self-Hug','Wrap your arms around yourself. Give yourself a gentle hug. Hold for 30 seconds.',30,1,'sad')," +
                        "('Permission to Cry','Sit or lie down. If you need to cry, let yourself. Crying is okay and healthy.',30,1,'sad')," +
                        "('Kind Words to Self','Say this slowly: I deserve kindness. It is okay to feel sad. I am allowed to struggle.',30,1,'sad')," +
                        "('Get Cozy','Put on soft comfortable clothes or wrap yourself in a soft blanket.',30,1,'sad')," +
                        "('Warm Water','Splash warm water gently on your face. Pat your face dry gently.',30,1,'sad')," +
                        "('Happy Photo Memory','Find one photo from a happy time. Look at it for 1 minute. Remember you felt joy before.',30,1,'sad')," +
                        "('Comfort Food or Drink','Make or get something warm and comforting. Tea, hot chocolate, soup. Sip or eat slowly.',50,2,'sad')," +
                        "('Soft Soothing Music','Play quiet, gentle, calming music. Close your eyes. Let it comfort you.',50,2,'sad')," +
                        "('Hold Something Soft','Get a pillow, stuffed animal, or soft blanket. Hold it. Squeeze it gently.',30,1,'sad')," +
                        "('Read Something Kind','Read one kind quote, affirmation, or comforting message. Read it slowly.',30,1,'sad')," +
                        "('Safe Space','Sit or lie down in your safest, most comfortable spot. Just be there for 1 minute.',30,1,'sad')," +
                        "('One Small Hope','Think of one tiny thing to look forward to. Even something very small counts.',30,1,'sad')," +
                        "('Gentle Body Stretch','Do one very slow, very gentle stretch. Be soft and kind to your body.',30,1,'sad')," +
                        "('Journaling','Write down everything you are feeling. No rules. No judgment. Just write for 2 minutes.',50,2,'sad')," +
                        "('Calm Animal Video','Watch a calming animal video. Puppies sleeping, cats purring, birds chirping. Watch for 1 minute.',30,1,'sad')," +
                        "('Remember Your Strength','Say this: I have gotten through hard times before. I got through them. I can do this.',30,1,'sad')," +
                        "('Self-Compassion','Say: I am struggling right now and that is okay. I deserve my own kindness.',30,1,'sad')," +

                        // ========== ANGRY (21 quests) - Safe release, grounding, solution-focused ==========
                        "('Power Breathing','Breathe in hard through your nose. Breathe out hard through your mouth. Do 10 times.',30,1,'angry')," +
                        "('Run in Place','Run in place as fast as you can for 30 seconds. Let the energy out.',30,1,'angry')," +
                        "('Hit a Pillow','Get a pillow. Punch it or hit it as hard as you want. Let it out.',30,1,'angry')," +
                        "('Scream in Pillow','Get a pillow. Press it to your face. Scream into it as loud as you need.',30,1,'angry')," +
                        "('Write Your Anger','Write down why you are angry. Write exactly how you feel. Do not hold back.',50,2,'angry')," +
                        "('Cold Water Splash','Splash cold water on your face and hands. Feel the shock of cold.',30,1,'angry')," +
                        "('Count Backwards','Count backwards from 30 out loud. Focus only on the numbers.',30,1,'angry')," +
                        "('Loud Intense Music','Play loud or intense music that matches your energy. Listen for 2 minutes.',50,2,'angry')," +
                        "('Scrub Hard','Get a dish, table, or surface. Scrub it hard for 1 minute. Put your anger into scrubbing.',30,1,'angry')," +
                        "('Air Punches','Stand up. Punch the air in front of you. Do 20 hard punches.',30,1,'angry')," +
                        "('Hold Ice','Hold an ice cube or something very cold for 30 seconds. Focus on the cold feeling.',30,1,'angry')," +
                        "('Stomp Your Feet','Stomp your feet hard on the ground. Walk and stomp for 30 seconds. Make noise.',30,1,'angry')," +
                        "('Rip Paper','Get scrap paper or old magazines. Rip them into small pieces. Rip as much as you want.',30,1,'angry')," +
                        "('Say It Out Loud','Say out loud why you are angry. No one needs to hear. Just say it.',30,1,'angry')," +
                        "('Squeeze and Release','Squeeze your fists as tight as you can. Hold 5 seconds. Release. Do 5 times.',30,1,'angry')," +
                        "('Walk Away','Walk to another room or space. Physically walk away from what made you angry.',30,1,'angry')," +
                        "('Picture Calm Place','Close your eyes. Picture a calm peaceful place. Beach, forest, mountains. Stay there 1 minute.',30,1,'angry')," +
                        "('Voice Recording','Record yourself on your phone saying why you are angry. Say everything. Delete after if you want.',50,2,'angry')," +
                        "('Wall Push','Stand facing a wall. Push against it as hard as you can for 10 seconds. Do 3 times.',30,1,'angry')," +
                        "('Solution Focus','Write down one small thing you can do to help fix this problem or make it better.',50,2,'angry')," +
                        "('Jump It Out','Do 15 jumps. Jumping jacks or just jump up and down. Get the anger out.',30,1,'angry')," +

                        // ========== ANXIOUS (21 quests) - Calming, grounding, present-focused ==========
                        "('4-7-8 Breathing','Breathe in for 4 counts. Hold for 7 counts. Breathe out for 8 counts. Do 3 times.',30,1,'anxious')," +
                        "('5 Senses Grounding','Say out loud: 5 things you see. 4 things you hear. 3 things you touch. 2 things you smell. 1 thing you taste.',50,2,'anxious')," +
                        "('Worry Dump','Get paper. Write down every single worry in your head. Get them all out.',50,2,'anxious')," +
                        "('Body Tension Check','Close your eyes. Notice where you feel tense. Your jaw? Shoulders? Stomach? Just notice. Do not fix it.',30,1,'anxious')," +
                        "('Calming Nature Sounds','Listen to rain sounds, ocean waves, or forest sounds for 2 minutes.',50,2,'anxious')," +
                        "('Name the Anxiety','Write this sentence: I am anxious about... Write what comes to mind.',30,1,'anxious')," +
                        "('Progressive Relaxation','Squeeze your fists tight. Hold 5 seconds. Let go completely. Do 5 times.',30,1,'anxious')," +
                        "('Safe Place Visualization','Close your eyes. Picture the safest place you know. Imagine being there. Stay 1 minute.',30,1,'anxious')," +
                        "('Warm Tea or Water','Make warm tea or water with honey. Hold the warm cup. Sip it very slowly.',50,2,'anxious')," +
                        "('Cold Water','Drink a glass of cold water slowly. Skip coffee or energy drinks right now.',30,1,'anxious')," +
                        "('Past Strength','Think: I have felt anxious before. I got through it. I can get through this too.',30,1,'anxious')," +
                        "('Reality Check','Ask yourself: Is this thought 100 percent true? What is the real evidence?',30,1,'anxious')," +
                        "('Slow Mindful Walking','Walk as slowly as possible for 1 minute. Feel each step. Feel your feet on the ground.',30,1,'anxious')," +
                        "('Palm Press','Press your palms together as hard as you can. Hold for 30 seconds. Feel the pressure.',30,1,'anxious')," +
                        "('Weighted Comfort','Put something heavy on your lap or legs. A blanket, pillow, or book. Feel the weight.',30,1,'anxious')," +
                        "('Smell Something','Smell soap, lotion, fresh air, or anything calming. Focus only on the smell.',30,1,'anxious')," +
                        "('Calming Affirmation','Say 10 times slowly: I am safe right now. I am okay. I can handle this.',30,1,'anxious')," +
                        "('Simple Coloring','Color one simple shape. A circle, square, or pattern. Focus only on coloring.',50,2,'anxious')," +
                        "('One Tiny Step','Write: One tiny thing I can do about this worry is... Write one small action.',30,1,'anxious')," +
                        "('Ice on Wrists','Hold ice or something cold on your wrists for 30 seconds. Focus on the cold.',30,1,'anxious')," +
                        "('Counting Focus','Count backwards from 50 by 3s. Say each number out loud. 50, 47, 44, 41... Focus only on counting.',50,2,'anxious');" +
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