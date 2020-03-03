//package gr.slg.uploadserverfiles.Retrofit
//
//import android.telecom.Call
//import okhttp3.RequestBody
//import retrofit2.http.Multipart
//import retrofit2.http.POST
//import retrofit2.http.Part
//
//
//class Api {
//
//    var BASE_URL = "http://10.3.209.37:8080/"
//
//    @Multipart
//    @POST("uploadFile/{file}")
//    open fun uploadImage(@Part("image\"; filename=\"myfile.jpg\" ") file: RequestBody?): Call<MyResponse?>?
//
//}