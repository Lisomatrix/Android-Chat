package pt.lisomatrix.chatapplication.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import pt.lisomatrix.chatapplication.model.Message;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM message")
    Flowable<List<Message>> getAll();

    @Query("SELECT * FROM message WHERE (destiny_id = :destiny AND sender_id = :sender) OR (destiny_id = :sender AND sender_id = :destiny) ;")
    Flowable<List<Message>> getConversationMessages(String destiny, String sender);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Message... messages);

    @Delete
    Completable delete(Message message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<Message> messages);
}
