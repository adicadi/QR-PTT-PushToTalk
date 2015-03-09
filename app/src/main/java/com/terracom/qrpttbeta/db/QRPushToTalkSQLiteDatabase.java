package com.terracom.qrpttbeta.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.terracom.jumble.model.Server;
import com.terracom.qrpttbeta.Constants;

import java.util.ArrayList;
import java.util.List;

public class QRPushToTalkSQLiteDatabase extends SQLiteOpenHelper implements QRPushToTalkDatabase {
    public static final String DATABASE_NAME = "mumble.db";

    public static final String TABLE_SERVER = "server";
    public static final String SERVER_ID = "_id";
    public static final String SERVER_NAME = "name";
    public static final String SERVER_HOST = "host";
    public static final String SERVER_PORT = "port";
    public static final String SERVER_USERNAME = "username";
    public static final String SERVER_PASSWORD = "password";
    public static final String TABLE_SERVER_CREATE_SQL = "CREATE TABLE IF NOT EXISTS `" + TABLE_SERVER + "` ("
            + "`" + SERVER_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`" + SERVER_NAME + "` TEXT NOT NULL,"
            + "`" + SERVER_HOST + "` TEXT NOT NULL,"
            + "`" + SERVER_PORT + "` INTEGER,"
            + "`" + SERVER_USERNAME + "` TEXT NOT NULL,"
            + "`" + SERVER_PASSWORD + "` TEXT"
            + ");";

    public static final String TABLE_FAVOURITES = "favourites";
    public static final String FAVOURITES_ID = "_id";
    public static final String FAVOURITES_CHANNEL = "channel";
    public static final String FAVOURITES_SERVER = "server";
    public static final String TABLE_FAVOURITES_CREATE_SQL = "CREATE TABLE IF NOT EXISTS `" + TABLE_FAVOURITES + "` ("
            + "`" + FAVOURITES_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`" + FAVOURITES_CHANNEL + "` TEXT NOT NULL,"
            + "`" + FAVOURITES_SERVER + "` INTEGER NOT NULL"
            + ");";

    public static final String TABLE_TOKENS = "tokens";
    public static final String TOKENS_ID = "_id";
    public static final String TOKENS_VALUE = "value";
    public static final String TOKENS_SERVER = "server";
    public static final String TABLE_TOKENS_CREATE_SQL = "CREATE TABLE IF NOT EXISTS `" + TABLE_TOKENS + "` ("
            + "`" + TOKENS_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "`" + TOKENS_VALUE + "` TEXT NOT NULL,"
            + "`" + TOKENS_SERVER + "` INTEGER NOT NULL"
            + ");";

    public static final String TABLE_COMMENTS = "comments";
    public static final String COMMENTS_WHO = "who";
    public static final String COMMENTS_COMMENT = "comment";
    public static final String COMMENTS_SEEN = "seen";
    public static final String TABLE_COMMENTS_CREATE_SQL = "CREATE TABLE IF NOT EXISTS `" + TABLE_COMMENTS + "` ("
            + "`" + COMMENTS_WHO + "` TEXT NOT NULL,"
            + "`" + COMMENTS_COMMENT + "` TEXT NOT NULL,"
            + "`" + COMMENTS_SEEN + "` DATE NOT NULL"
            + ");";

    public static final String TABLE_LOCAL_MUTE = "local_mute";
    public static final String LOCAL_MUTE_SERVER = "server";
    public static final String LOCAL_MUTE_USER = "user";
    public static final String TABLE_LOCAL_MUTE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCAL_MUTE + " ("
            + "`" + LOCAL_MUTE_SERVER + "` INTEGER NOT NULL,"
            + "`" + LOCAL_MUTE_USER + "` INTEGER NOT NULL,"
            + "CONSTRAINT server_user UNIQUE(" + LOCAL_MUTE_SERVER + "," + LOCAL_MUTE_USER + ")"
            + ");";

    public static final String TABLE_LOCAL_IGNORE = "local_ignore";
    public static final String LOCAL_IGNORE_SERVER = "server";
    public static final String LOCAL_IGNORE_USER = "user";
    public static final String TABLE_LOCAL_IGNORE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCAL_IGNORE + " ("
            + "`" + LOCAL_IGNORE_SERVER + "` INTEGER NOT NULL,"
            + "`" + LOCAL_IGNORE_USER + "` INTEGER NOT NULL,"
            + "CONSTRAINT server_user UNIQUE(" + LOCAL_IGNORE_SERVER + "," + LOCAL_IGNORE_USER + ")"
            + ");";

    public static final Integer PRE_FAVOURITES_DB_VERSION = 2;
    public static final Integer PRE_TOKENS_DB_VERSION = 3;
    public static final Integer PRE_COMMENTS_DB_VERSION = 4;
    public static final Integer PRE_LOCAL_MUTE_DB_VERSION = 5;
    public static final Integer PRE_LOCAL_IGNORE_DB_VERSION = 6;
    public static final Integer CURRENT_DB_VERSION = 7;

    public QRPushToTalkSQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_DB_VERSION);
    }

    public QRPushToTalkSQLiteDatabase(Context context, String name) {
        super(context, name, null, CURRENT_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_SERVER_CREATE_SQL);
        db.execSQL(TABLE_FAVOURITES_CREATE_SQL);
        db.execSQL(TABLE_TOKENS_CREATE_SQL);
        db.execSQL(TABLE_COMMENTS_CREATE_SQL);
        db.execSQL(TABLE_LOCAL_MUTE_CREATE_SQL);
        db.execSQL(TABLE_LOCAL_IGNORE_CREATE_SQL);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {
        Log.w(Constants.TAG, "Database upgrade from " + oldVersion + " to " + newVersion);
        if (oldVersion <= PRE_FAVOURITES_DB_VERSION) {
            db.execSQL(TABLE_FAVOURITES_CREATE_SQL);
        }

        if (oldVersion <= PRE_TOKENS_DB_VERSION) {
            db.execSQL(TABLE_TOKENS_CREATE_SQL);
        }

        if (oldVersion <= PRE_COMMENTS_DB_VERSION) {
            db.execSQL(TABLE_COMMENTS_CREATE_SQL);
        }

        if (oldVersion <= PRE_LOCAL_MUTE_DB_VERSION) {
            db.execSQL(TABLE_LOCAL_MUTE_CREATE_SQL);
        }

        if (oldVersion <= PRE_LOCAL_IGNORE_DB_VERSION) {
            db.execSQL(TABLE_LOCAL_IGNORE_CREATE_SQL);
        }
    }

    @Override
    public void open() {
    }

    @Override
    public List<Server> getServers() {
        Cursor c = getReadableDatabase().query(
                TABLE_SERVER,
                new String[]{SERVER_ID, SERVER_NAME, SERVER_HOST,
                        SERVER_PORT, SERVER_USERNAME, SERVER_PASSWORD},
                null,
                null,
                null,
                null,
                null);

        List<Server> servers = new ArrayList<Server>();

        c.moveToFirst();
        while (!c.isAfterLast()) {
            Server server = new Server(c.getInt(c.getColumnIndex(SERVER_ID)),
                    c.getString(c.getColumnIndex(SERVER_NAME)),
                    c.getString(c.getColumnIndex(SERVER_HOST)),
                    c.getInt(c.getColumnIndex(SERVER_PORT)),
                    c.getString(c.getColumnIndex(SERVER_USERNAME)),
                    c.getString(c.getColumnIndex(SERVER_PASSWORD)));
            servers.add(server);
            c.moveToNext();
        }

        c.close();

        return servers;
    }

    @Override
    public void addServer(Server server) {
        ContentValues values = new ContentValues();
        values.put(SERVER_NAME, server.getName());
        values.put(SERVER_HOST, server.getHost());
        values.put(SERVER_PORT, server.getPort());
        values.put(SERVER_USERNAME, server.getUsername());
        values.put(SERVER_PASSWORD, server.getPassword());

        server.setId(getWritableDatabase().insert(TABLE_SERVER, null, values));
    }

    @Override
    public void updateServer(Server server) {
        ContentValues values = new ContentValues();
        values.put(SERVER_NAME, server.getName());
        values.put(SERVER_HOST, server.getHost());
        values.put(SERVER_PORT, server.getPort());
        values.put(SERVER_USERNAME, server.getUsername());
        values.put(SERVER_PASSWORD, server.getPassword());
        getWritableDatabase().update(
                TABLE_SERVER,
                values,
                SERVER_ID + "=?",
                new String[]{Long.toString(server.getId())});
    }

    @Override
    public void removeServer(Server server) {
        getWritableDatabase().delete(TABLE_SERVER, SERVER_ID + "=?",
<<<<<<< HEAD
                new String[]{String.valueOf(server.getId())});
=======
                new String[] { String.valueOf(server.getId()) });
>>>>>>> 07bc5cde7e6dce7050a44aecffed1740735184c0
        getWritableDatabase().delete(TABLE_FAVOURITES, FAVOURITES_SERVER + "=?",
                new String[]{String.valueOf(server.getId())});
        getWritableDatabase().delete(TABLE_TOKENS, TOKENS_SERVER + "=?",
                new String[]{String.valueOf(server.getId())});
        getWritableDatabase().delete(TABLE_LOCAL_MUTE, LOCAL_MUTE_SERVER + "=?",
                new String[]{String.valueOf(server.getId())});
        getWritableDatabase().delete(TABLE_LOCAL_IGNORE, LOCAL_IGNORE_SERVER + "=?",
                new String[]{String.valueOf(server.getId())});
    }

    public List<Integer> getPinnedChannels(long serverId) {

        final Cursor c = getReadableDatabase().query(
                TABLE_FAVOURITES,
                new String[]{FAVOURITES_CHANNEL},
                FAVOURITES_SERVER + "=?",
                new String[]{String.valueOf(serverId)},
                null,
                null,
                null);

        List<Integer> favourites = new ArrayList<Integer>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            favourites.add(c.getInt(0));
            c.moveToNext();
        }

        c.close();

        return favourites;
    }

    @Override
    public List<String> getAccessTokens(long serverId) {
        Cursor cursor = getReadableDatabase().query(TABLE_TOKENS, new String[]{TOKENS_VALUE}, TOKENS_SERVER + "=?", new String[]{String.valueOf(serverId)}, null, null, null);
        cursor.moveToFirst();
        List<String> tokens = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            tokens.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return tokens;
    }

    @Override
    public List<Integer> getLocalMutedUsers(long serverId) {
        Cursor cursor = getReadableDatabase().query(TABLE_LOCAL_MUTE,
                new String[]{LOCAL_MUTE_USER},
                LOCAL_MUTE_SERVER + "=?",
                new String[]{String.valueOf(serverId)},
                null, null, null);
        cursor.moveToNext();
        List<Integer> users = new ArrayList<Integer>();
        while (!cursor.isAfterLast()) {
            users.add(cursor.getInt(0));
            cursor.moveToNext();
        }
        return users;
    }

    @Override
    public void addLocalMutedUser(long serverId, int userId) {
        ContentValues values = new ContentValues();
        values.put(LOCAL_MUTE_SERVER, serverId);
        values.put(LOCAL_MUTE_USER, userId);
        getWritableDatabase().insert(TABLE_LOCAL_MUTE, null, values);
    }

    @Override
    public void removeLocalMutedUser(long serverId, int userId) {
        getWritableDatabase().delete(TABLE_LOCAL_MUTE,
                LOCAL_MUTE_SERVER + "=? AND " + LOCAL_MUTE_USER + "=?",
                new String[]{String.valueOf(serverId), String.valueOf(userId)});
    }

    @Override
    public List<Integer> getLocalIgnoredUsers(long serverId) {
        Cursor cursor = getReadableDatabase().query(TABLE_LOCAL_IGNORE,
                new String[]{LOCAL_IGNORE_USER},
                LOCAL_IGNORE_SERVER + "=?",
                new String[]{String.valueOf(serverId)},
                null, null, null);
        cursor.moveToFirst();
        List<Integer> users = new ArrayList<Integer>();
        while (!cursor.isAfterLast()) {
            users.add(cursor.getInt(0));
            cursor.moveToNext();
        }
        return users;
    }

    @Override
    public void addLocalIgnoredUser(long serverId, int userId) {
        ContentValues values = new ContentValues();
        values.put(LOCAL_IGNORE_SERVER, serverId);
        values.put(LOCAL_IGNORE_USER, userId);
        getWritableDatabase().insert(TABLE_LOCAL_IGNORE, null, values);
    }

    @Override
    public void removeLocalIgnoredUser(long serverId, int userId) {
        getWritableDatabase().delete(TABLE_LOCAL_IGNORE,
                LOCAL_IGNORE_SERVER + "=? AND " + LOCAL_IGNORE_USER + "=?",
<<<<<<< HEAD
                new String[]{String.valueOf(serverId), String.valueOf(userId)});
=======
                new String[] { String.valueOf(serverId), String.valueOf(userId) });
>>>>>>> 07bc5cde7e6dce7050a44aecffed1740735184c0
    }
}
