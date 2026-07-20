package com.spacecar.imyself.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TrackingDao_Impl implements TrackingDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyLog> __insertionAdapterOfDailyLog;

  private final EntityInsertionAdapter<StreakMilestone> __insertionAdapterOfStreakMilestone;

  private final EntityInsertionAdapter<PersonalLog> __insertionAdapterOfPersonalLog;

  private final EntityDeletionOrUpdateAdapter<PersonalLog> __deletionAdapterOfPersonalLog;

  private final EntityDeletionOrUpdateAdapter<PersonalLog> __updateAdapterOfPersonalLog;

  private final SharedSQLiteStatement __preparedStmtOfClearAllDailyLogs;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLogByDate;

  private final SharedSQLiteStatement __preparedStmtOfClearUnlockedMilestones;

  private final SharedSQLiteStatement __preparedStmtOfClearAllPersonalLogs;

  public TrackingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyLog = new EntityInsertionAdapter<DailyLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_log` (`date`,`state`,`notes`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyLog entity) {
        statement.bindString(1, entity.getDate());
        statement.bindString(2, __LogState_enumToString(entity.getState()));
        if (entity.getNotes() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getNotes());
        }
      }
    };
    this.__insertionAdapterOfStreakMilestone = new EntityInsertionAdapter<StreakMilestone>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `streak_milestone` (`id`,`targetDay`,`isLocked`,`dateReached`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StreakMilestone entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTargetDay());
        final int _tmp = entity.isLocked() ? 1 : 0;
        statement.bindLong(3, _tmp);
        if (entity.getDateReached() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDateReached());
        }
      }
    };
    this.__insertionAdapterOfPersonalLog = new EntityInsertionAdapter<PersonalLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `personal_logs` (`id`,`timestamp`,`moodEmoji`,`note`,`isFellLog`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PersonalLog entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTimestamp());
        statement.bindString(3, entity.getMoodEmoji());
        statement.bindString(4, entity.getNote());
        final int _tmp = entity.isFellLog() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__deletionAdapterOfPersonalLog = new EntityDeletionOrUpdateAdapter<PersonalLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `personal_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PersonalLog entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfPersonalLog = new EntityDeletionOrUpdateAdapter<PersonalLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `personal_logs` SET `id` = ?,`timestamp` = ?,`moodEmoji` = ?,`note` = ?,`isFellLog` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PersonalLog entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTimestamp());
        statement.bindString(3, entity.getMoodEmoji());
        statement.bindString(4, entity.getNote());
        final int _tmp = entity.isFellLog() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfClearAllDailyLogs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_log";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteLogByDate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_log WHERE date = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearUnlockedMilestones = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM streak_milestone WHERE isLocked = 0";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllPersonalLogs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM personal_logs";
        return _query;
      }
    };
  }

  @Override
  public Object insertAllDailyLogs(final List<DailyLog> logs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyLog.insert(logs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertLog(final DailyLog log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyLog.insert(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMilestone(final StreakMilestone milestone,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStreakMilestone.insert(milestone);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAllPersonalLogs(final List<PersonalLog> logs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPersonalLog.insert(logs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPersonalLog(final PersonalLog log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPersonalLog.insert(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePersonalLog(final PersonalLog log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPersonalLog.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePersonalLog(final PersonalLog log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPersonalLog.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllDailyLogs(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllDailyLogs.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllDailyLogs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLogByDate(final String date, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLogByDate.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, date);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteLogByDate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearUnlockedMilestones(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearUnlockedMilestones.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearUnlockedMilestones.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllPersonalLogs(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllPersonalLogs.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllPersonalLogs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyLog>> getAllLogs() {
    final String _sql = "SELECT * FROM daily_log ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_log"}, new Callable<List<DailyLog>>() {
      @Override
      @NonNull
      public List<DailyLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<DailyLog> _result = new ArrayList<DailyLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyLog _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final LogState _tmpState;
            _tmpState = __LogState_stringToEnum(_cursor.getString(_cursorIndexOfState));
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new DailyLog(_tmpDate,_tmpState,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllLogsDirectly(final Continuation<? super List<DailyLog>> $completion) {
    final String _sql = "SELECT * FROM daily_log ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyLog>>() {
      @Override
      @NonNull
      public List<DailyLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<DailyLog> _result = new ArrayList<DailyLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyLog _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final LogState _tmpState;
            _tmpState = __LogState_stringToEnum(_cursor.getString(_cursorIndexOfState));
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new DailyLog(_tmpDate,_tmpState,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLogByDate(final String date, final Continuation<? super DailyLog> $completion) {
    final String _sql = "SELECT * FROM daily_log WHERE date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyLog>() {
      @Override
      @Nullable
      public DailyLog call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final DailyLog _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final LogState _tmpState;
            _tmpState = __LogState_stringToEnum(_cursor.getString(_cursorIndexOfState));
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _result = new DailyLog(_tmpDate,_tmpState,_tmpNotes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLatestFellLog(final Continuation<? super DailyLog> $completion) {
    final String _sql = "SELECT * FROM daily_log WHERE state IN ('ORANGE', 'RED') ORDER BY date DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyLog>() {
      @Override
      @Nullable
      public DailyLog call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final DailyLog _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final LogState _tmpState;
            _tmpState = __LogState_stringToEnum(_cursor.getString(_cursorIndexOfState));
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _result = new DailyLog(_tmpDate,_tmpState,_tmpNotes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getOldestLog(final Continuation<? super DailyLog> $completion) {
    final String _sql = "SELECT * FROM daily_log ORDER BY date ASC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyLog>() {
      @Override
      @Nullable
      public DailyLog call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final DailyLog _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final LogState _tmpState;
            _tmpState = __LogState_stringToEnum(_cursor.getString(_cursorIndexOfState));
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _result = new DailyLog(_tmpDate,_tmpState,_tmpNotes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<StreakMilestone>> getAllMilestones() {
    final String _sql = "SELECT * FROM streak_milestone ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"streak_milestone"}, new Callable<List<StreakMilestone>>() {
      @Override
      @NonNull
      public List<StreakMilestone> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTargetDay = CursorUtil.getColumnIndexOrThrow(_cursor, "targetDay");
          final int _cursorIndexOfIsLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isLocked");
          final int _cursorIndexOfDateReached = CursorUtil.getColumnIndexOrThrow(_cursor, "dateReached");
          final List<StreakMilestone> _result = new ArrayList<StreakMilestone>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StreakMilestone _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpTargetDay;
            _tmpTargetDay = _cursor.getInt(_cursorIndexOfTargetDay);
            final boolean _tmpIsLocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLocked);
            _tmpIsLocked = _tmp != 0;
            final String _tmpDateReached;
            if (_cursor.isNull(_cursorIndexOfDateReached)) {
              _tmpDateReached = null;
            } else {
              _tmpDateReached = _cursor.getString(_cursorIndexOfDateReached);
            }
            _item = new StreakMilestone(_tmpId,_tmpTargetDay,_tmpIsLocked,_tmpDateReached);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getActiveMilestone(final Continuation<? super StreakMilestone> $completion) {
    final String _sql = "SELECT * FROM streak_milestone WHERE isLocked = 0 ORDER BY id ASC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<StreakMilestone>() {
      @Override
      @Nullable
      public StreakMilestone call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTargetDay = CursorUtil.getColumnIndexOrThrow(_cursor, "targetDay");
          final int _cursorIndexOfIsLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isLocked");
          final int _cursorIndexOfDateReached = CursorUtil.getColumnIndexOrThrow(_cursor, "dateReached");
          final StreakMilestone _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpTargetDay;
            _tmpTargetDay = _cursor.getInt(_cursorIndexOfTargetDay);
            final boolean _tmpIsLocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLocked);
            _tmpIsLocked = _tmp != 0;
            final String _tmpDateReached;
            if (_cursor.isNull(_cursorIndexOfDateReached)) {
              _tmpDateReached = null;
            } else {
              _tmpDateReached = _cursor.getString(_cursorIndexOfDateReached);
            }
            _result = new StreakMilestone(_tmpId,_tmpTargetDay,_tmpIsLocked,_tmpDateReached);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PersonalLog>> getAllPersonalLogs() {
    final String _sql = "SELECT * FROM personal_logs ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"personal_logs"}, new Callable<List<PersonalLog>>() {
      @Override
      @NonNull
      public List<PersonalLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMoodEmoji = CursorUtil.getColumnIndexOrThrow(_cursor, "moodEmoji");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsFellLog = CursorUtil.getColumnIndexOrThrow(_cursor, "isFellLog");
          final List<PersonalLog> _result = new ArrayList<PersonalLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PersonalLog _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMoodEmoji;
            _tmpMoodEmoji = _cursor.getString(_cursorIndexOfMoodEmoji);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsFellLog;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFellLog);
            _tmpIsFellLog = _tmp != 0;
            _item = new PersonalLog(_tmpId,_tmpTimestamp,_tmpMoodEmoji,_tmpNote,_tmpIsFellLog);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllPersonalLogsDirectly(
      final Continuation<? super List<PersonalLog>> $completion) {
    final String _sql = "SELECT * FROM personal_logs ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PersonalLog>>() {
      @Override
      @NonNull
      public List<PersonalLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMoodEmoji = CursorUtil.getColumnIndexOrThrow(_cursor, "moodEmoji");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsFellLog = CursorUtil.getColumnIndexOrThrow(_cursor, "isFellLog");
          final List<PersonalLog> _result = new ArrayList<PersonalLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PersonalLog _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMoodEmoji;
            _tmpMoodEmoji = _cursor.getString(_cursorIndexOfMoodEmoji);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsFellLog;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFellLog);
            _tmpIsFellLog = _tmp != 0;
            _item = new PersonalLog(_tmpId,_tmpTimestamp,_tmpMoodEmoji,_tmpNote,_tmpIsFellLog);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __LogState_enumToString(@NonNull final LogState _value) {
    switch (_value) {
      case GREEN: return "GREEN";
      case YELLOW: return "YELLOW";
      case ORANGE: return "ORANGE";
      case RED: return "RED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private LogState __LogState_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "GREEN": return LogState.GREEN;
      case "YELLOW": return LogState.YELLOW;
      case "ORANGE": return LogState.ORANGE;
      case "RED": return LogState.RED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
