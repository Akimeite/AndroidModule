package com.djangoogle.module.network;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Djangoogle on 2019/05/18 15:32 with Android Studio.
 */
public interface ZhuangbiApi {

	@GET("/get")
	Observable<List<ZhuangbiImage>> search(@Query("q") String query);
}
