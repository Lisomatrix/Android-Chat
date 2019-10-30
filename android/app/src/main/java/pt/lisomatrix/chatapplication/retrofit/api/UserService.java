package pt.lisomatrix.chatapplication.retrofit.api;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import pt.lisomatrix.chatapplication.model.Token;
import pt.lisomatrix.chatapplication.model.User;
import pt.lisomatrix.chatapplication.network.Authenticate;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

    @GET("user")
    Flowable<List<User>> getUsers(@Header("authorization") String token);

    @GET("user/{id}")
    Flowable<User> getUser(@Path("id") int id, @Header("authorization") String token);

    @POST("/register")
    Maybe<Response<Void>> register(@Body User user);

    @POST("/login")
    Maybe<Response<Token>> authenticate(@Body Authenticate authenticate);

    @GET("me")
    Single<User> getMe(@Header("authorization") String token);

}
