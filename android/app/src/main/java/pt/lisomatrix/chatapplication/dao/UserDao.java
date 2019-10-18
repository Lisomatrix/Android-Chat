package pt.lisomatrix.chatapplication.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import pt.lisomatrix.chatapplication.model.User;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    Flowable<List<User>> getAll();

    @Query("SELECT * FROM user WHERE userId IN (:userIds)")
    Maybe<List<User>> loadAllByIds(int[] userIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(User... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(List<User> users);

    @Delete
    Completable delete(User user);
}
