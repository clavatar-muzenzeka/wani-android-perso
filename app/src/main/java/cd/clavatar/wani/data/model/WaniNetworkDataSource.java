package cd.clavatar.wani.data.model;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/* renamed from: cd.clavatar.wani.data.model.WaniNetworkDataSource */
public interface WaniNetworkDataSource {

    /* renamed from: t */
    public static final String f86t = WaniNetworkAccess.TOKEN;

    @POST("/user/action/?name=change_password")
    Call<LoginResult> changePassword(@Body ChangePasswordBody changePasswordBody);

    @GET("checkapi/")
    Call<JSONObject> checkApi();

    @GET("avi/sync/user")
    Call<List<User>> getSyncableUsers();

    @GET("avi/sync/all")
    Call<List<JsonElement>> getSyncables();

    @GET("paiement/?full=yes")
    Call<List<CompactPaiement>> loaPaiments();

    @GET("carnet/?full=yes")
    Call<List<CompactCarnet>> loadCarnets();

    @GET("syncglobal/?full=yes&sync=yes&stagered=yes")
    Call<GlobalState> loadGlobalState(@Query("first") boolean z);

    @GET("paiementtype/")
    Call<List<PaimentType>> loadPaiementTypes();

    @GET("check/?full=yes&sync=yes")
    Call<ArrayList<Check>> loadSyncableChecks(@Query("first") boolean z);

    @GET("carnet/?full=yes&sync=yes")
    Call<ArrayList<CompactCarnet>> loadSyncableCompactCarnets(@Query("first") boolean z);

    @GET("paiement/?full=yes&sync=yes")
    Call<ArrayList<CompactPaiement>> loadSyncableCompactPaiements(@Query("first") boolean z);

    @GET("carnetstatus/?full=yes&sync=yes")
    Call<ArrayList<CompactStatus>> loadSyncableCompactStatus(@Query("first") boolean z);

    @GET("user/?full=yes&sync=yes")
    Call<ArrayList<CompactUser>> loadSyncableCompactUsers(@Query("first") boolean z);

    @GET("location/?full=yes&sync=yes")
    Call<ArrayList<Location>> loadSyncableLocations(@Query("first") boolean z);

    @GET("paiementtype/?full=yes&sync=yes")
    Call<ArrayList<PaimentType>> loadSyncablePaimentTypes(@Query("first") boolean z);

    @GET("people/?full=yes&sync=yes")
    Call<ArrayList<People>> loadSyncablePeoples(@Query("first") boolean z);

    @GET("picture/?full=yes&sync=yes")
    Call<ArrayList<Picture>> loadSyncablePictures(@Query("first") boolean z);

    @GET("story/?full=yes&sync=yes")
    Call<ArrayList<Story>> loadSyncableStory(@Query("first") boolean z);

    @GET("vaccinoccurence/?full=yes&sync=yes")
    Call<ArrayList<VaccinOccurence>> loadSyncableVaccinOccurences(@Query("first") boolean z);

    @GET("vaccin/?full=yes&sync=yes")
    Call<ArrayList<Vaccin>> loadSyncableVaccins(@Query("first") boolean z);

    @GET("parameter/?full=yes&sync=yes")
    Call<ArrayList<WaniParameter>> loadSyncableWaniParameters(@Query("first") boolean z);

    @GET("vaccin/")
    Call<List<Vaccin>> loadVaccins();

    @POST("/user/login")
    Call<LoginResult> login(@Body LoginBody loginBody);

    @POST("carnet/")
    Call<CompactCarnet> postCarnet(@Body CompactCarnet compactCarnet);

    @POST("check/")
    Call<Check> postCheck(@Body Check check);

    @POST("paiement/")
    Call<CompactPaiement> postPaiement(@Body CompactPaiement compactPaiement);

    @POST("people/")
    Call<People> postPeople(@Body People people);

    @POST("picture/")
    Call<Picture> postPicture(@Body Picture picture);

    @POST("check/?sync=yes")
    Call<Check> postSyncableCheck(@Body Check check, @Query("first") boolean z);

    @POST("carnet/?sync=yes")
    Call<CompactCarnet> postSyncableCompactCarnet(@Body CompactCarnet compactCarnet, @Query("first") boolean z);

    @POST("paiement/?sync=yes")
    Call<CompactPaiement> postSyncableCompactPaiement(@Body CompactPaiement compactPaiement, @Query("first") boolean z);

    @POST("carnetstatus/?sync=yes")
    Call<CompactStatus> postSyncableCompactStatus(@Body CompactStatus compactStatus, @Query("first") boolean z);

    @POST("user/?sync=yes")
    Call<CompactUser> postSyncableCompactUser(@Body CompactUser compactUser, @Query("first") boolean z);

    @POST("location/?sync=yes")
    Call<Location> postSyncableLocation(@Body Location location, @Query("first") boolean z);

    @POST("paiementtype/?sync=yes")
    Call<PaimentType> postSyncablePaimentType(@Body PaimentType paimentType, @Query("first") boolean z);

    @POST("people/?sync=yes")
    Call<People> postSyncablePeople(@Body People people, @Query("first") boolean z);

    @POST("picture/?sync=yes")
    Call<Picture> postSyncablePicture(@Body Picture picture, @Query("first") boolean z);

    @POST("story/?sync=yes")
    Call<Story> postSyncableStory(@Body Story story, @Query("first") boolean z);

    @POST("vaccin/?sync=yes")
    Call<Vaccin> postSyncableVaccin(@Body Vaccin vaccin, @Query("first") boolean z);

    @POST("vaccinoccurence/?sync=yes")
    Call<VaccinOccurence> postSyncableVaccinOccurence(@Body VaccinOccurence vaccinOccurence, @Query("first") boolean z);

    @PUT("carnet/{id}")
    Call<CompactCarnet> putCarnet(@Path("id") String str, @Body CompactCarnet compactCarnet);

    @PUT("people/{id}")
    Call<People> putPeople(@Path("id") String str, @Body People people);

    @PUT("check/{id}/?sync=yes")
    Call<Check> putSyncableCheck(@Path("id") String str, @Body Check check, @Query("first") boolean z);

    @PUT("carnet/{id}/?sync=yes")
    Call<CompactCarnet> putSyncableCompactCarnet(@Path("id") String str, @Body CompactCarnet compactCarnet, @Query("first") boolean z);

    @PUT("paiement/{id}/?sync=yes")
    Call<CompactPaiement> putSyncableCompactPaiement(@Path("id") String str, @Body CompactPaiement compactPaiement, @Query("first") boolean z);

    @PUT("carnetstatus/{id}/?sync=yes")
    Call<CompactStatus> putSyncableCompactStatus(@Path("id") String str, @Body CompactStatus compactStatus, @Query("first") boolean z);

    @PUT("user/{id}/?sync=yes")
    Call<CompactUser> putSyncableCompactUser(@Path("id") String str, @Body CompactUser compactUser, @Query("first") boolean z);

    @PUT("location/{id}/?sync=yes")
    Call<Location> putSyncableLocation(@Path("id") String str, @Body Location location, @Query("first") boolean z);

    @PUT("paiementtype/{id}/?sync=yes")
    Call<PaimentType> putSyncablePaimentType(@Path("id") String str, @Body PaimentType paimentType, @Query("first") boolean z);

    @PUT("people/{id}/?sync=yes")
    Call<People> putSyncablePeople(@Path("id") String str, @Body People people, @Query("first") boolean z);

    @PUT("picture/{id}/?sync=yes")
    Call<Picture> putSyncablePicture(@Path("id") String str, @Body Picture picture, @Query("first") boolean z);

    @PUT("story/{id}/?sync=yes")
    Call<Story> putSyncableStory(@Path("id") String str, @Body Story story, @Query("first") boolean z);

    @PUT("vaccin/{id}/?sync=yes")
    Call<Vaccin> putSyncableVaccin(@Path("id") String str, @Body Vaccin vaccin, @Query("first") boolean z);

    @PUT("vaccinoccurence/{id}/?sync=yes")
    Call<VaccinOccurence> putSyncableVaccinOccurence(@Path("id") String str, @Body VaccinOccurence vaccinOccurence, @Query("first") boolean z);

    @POST("signup")
    Call<User> signUp(@Body HashMap<String, Object> hashMap);

    @GET("validatesync/{lastCandidate}/{syncpath}")
    Call<Object> validateSync(@Path("lastCandidate") String str, @Path("syncpath") String str2);

    @POST("validateusername")
    Call<Boolean> validateUsername(@Body HashMap<String, String> hashMap);
}
