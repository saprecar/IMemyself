package com.spacecar.imyself.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile TrackingDao _trackingDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_log` (`date` TEXT NOT NULL, `state` TEXT NOT NULL, `notes` TEXT, PRIMARY KEY(`date`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `streak_milestone` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `targetDay` INTEGER NOT NULL, `isLocked` INTEGER NOT NULL, `dateReached` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `personal_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `moodEmoji` TEXT NOT NULL, `note` TEXT NOT NULL, `isFellLog` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '386b03df6bffbfa9fe8acf8970777728')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `daily_log`");
        db.execSQL("DROP TABLE IF EXISTS `streak_milestone`");
        db.execSQL("DROP TABLE IF EXISTS `personal_logs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDailyLog = new HashMap<String, TableInfo.Column>(3);
        _columnsDailyLog.put("date", new TableInfo.Column("date", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyLog.put("state", new TableInfo.Column("state", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyLog.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyLog = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyLog = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyLog = new TableInfo("daily_log", _columnsDailyLog, _foreignKeysDailyLog, _indicesDailyLog);
        final TableInfo _existingDailyLog = TableInfo.read(db, "daily_log");
        if (!_infoDailyLog.equals(_existingDailyLog)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_log(com.spacecar.imyself.data.DailyLog).\n"
                  + " Expected:\n" + _infoDailyLog + "\n"
                  + " Found:\n" + _existingDailyLog);
        }
        final HashMap<String, TableInfo.Column> _columnsStreakMilestone = new HashMap<String, TableInfo.Column>(4);
        _columnsStreakMilestone.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreakMilestone.put("targetDay", new TableInfo.Column("targetDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreakMilestone.put("isLocked", new TableInfo.Column("isLocked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreakMilestone.put("dateReached", new TableInfo.Column("dateReached", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStreakMilestone = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStreakMilestone = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStreakMilestone = new TableInfo("streak_milestone", _columnsStreakMilestone, _foreignKeysStreakMilestone, _indicesStreakMilestone);
        final TableInfo _existingStreakMilestone = TableInfo.read(db, "streak_milestone");
        if (!_infoStreakMilestone.equals(_existingStreakMilestone)) {
          return new RoomOpenHelper.ValidationResult(false, "streak_milestone(com.spacecar.imyself.data.StreakMilestone).\n"
                  + " Expected:\n" + _infoStreakMilestone + "\n"
                  + " Found:\n" + _existingStreakMilestone);
        }
        final HashMap<String, TableInfo.Column> _columnsPersonalLogs = new HashMap<String, TableInfo.Column>(5);
        _columnsPersonalLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPersonalLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPersonalLogs.put("moodEmoji", new TableInfo.Column("moodEmoji", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPersonalLogs.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPersonalLogs.put("isFellLog", new TableInfo.Column("isFellLog", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPersonalLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPersonalLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPersonalLogs = new TableInfo("personal_logs", _columnsPersonalLogs, _foreignKeysPersonalLogs, _indicesPersonalLogs);
        final TableInfo _existingPersonalLogs = TableInfo.read(db, "personal_logs");
        if (!_infoPersonalLogs.equals(_existingPersonalLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "personal_logs(com.spacecar.imyself.data.PersonalLog).\n"
                  + " Expected:\n" + _infoPersonalLogs + "\n"
                  + " Found:\n" + _existingPersonalLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "386b03df6bffbfa9fe8acf8970777728", "4547ba2a343351982deef8f2ff016914");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "daily_log","streak_milestone","personal_logs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `daily_log`");
      _db.execSQL("DELETE FROM `streak_milestone`");
      _db.execSQL("DELETE FROM `personal_logs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TrackingDao.class, TrackingDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TrackingDao trackingDao() {
    if (_trackingDao != null) {
      return _trackingDao;
    } else {
      synchronized(this) {
        if(_trackingDao == null) {
          _trackingDao = new TrackingDao_Impl(this);
        }
        return _trackingDao;
      }
    }
  }
}
