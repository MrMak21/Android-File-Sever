package gr.slg.uploadserverfiles.Retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {


    String BASE_URL = "http://10.3.209.37:8080/";

    //this is our multipart request
    //we have two parameters on is name and other one is description
    @Multipart
    @POST("uploadFile")
    Call<MyResponse> uploadImage(@Part("image\"; filename=\"file\" ") RequestBody file);

    @Multipart
    @POST("uploadFile")
    Call<MyResponse> uploadImagePart(@Part MultipartBody.Part file);

    void setBaseUrl(String ip);


}
