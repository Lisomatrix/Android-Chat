package pt.lisomatrix.chatapplication.retrofit.api;

import java.util.List;

import io.reactivex.Single;
import pt.lisomatrix.chatapplication.model.Message;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface MessagesService {

    @GET("me/message")
    Single<List<Message>> getMessages(@Header("authorization") String token);
}
