package cdioil.feedbackmonkey.authz;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cdioil.feedbackmonkey.BuildConfig;
import cdioil.feedbackmonkey.R;
import cdioil.feedbackmonkey.application.ListSurveyActivity;
import cdioil.feedbackmonkey.application.services.SurveyService;
import cdioil.feedbackmonkey.application.services.SurveyServiceController;
import cdioil.feedbackmonkey.restful.exceptions.RESTfulException;
import cdioil.feedbackmonkey.restful.utils.FeedbackMonkeyAPI;
import cdioil.feedbackmonkey.restful.utils.RESTRequest;
import cdioil.feedbackmonkey.restful.utils.json.UserProfileJSONService;
import cdioil.feedbackmonkey.utils.GenericFileProvider;
import cdioil.feedbackmonkey.utils.ToastNotification;
import okhttp3.Response;

/**
 * Activity that presents the user's profile.
 *
 * @author <a href="1160936@isep.ipp.pt">Gil Durão</a>
 */
public class UserProfileActivity extends AppCompatActivity {
    /**
     * Camera Request code.
     */
    private static final int CAMERA_REQUEST = 1888;
    /**
     * Permission to use camera code.
     */
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    /**
     * ImageView that may hold the user's profile picture.
     */
    private ImageView profilePhotoImageView;
    /**
     * TextView that holds the name of the user.
     */
    private TextView nameTextView;
    /**
     * TextView that holds the age of the user.
     */
    private TextView ageTextView;
    /**
     * TextView that holds the location of the user.
     */
    private TextView locationTextView;
    /**
     * TextView that holds information about the user's badges.
     */
    private TextView badgeTextView;
    /**
     * Button that on click will take the user to a list of their reviewed surveys.
     */
    private Button reviewedSurveysButton;
    /**
     * Button that on click will take the user to a list of their contests and badges.
     */
    private Button contestBadgeListButton;
    /**
     * Button that on click will take the user to a list of their suggestions.
     */
    private Button suggestionListButton;
    /**
     * String that holds the profile picture path.
     */
    private String pictureImagePath;
    /**
     * String that holds the users authenticationToken.
     */
    private String authenticationToken;
    /**
     * Int that holds the rest request code.
     */
    private int restResponseCode;
    /**
     * String that holds the rest request body.
     */
    private String restResponseBody;

    /**
     * Activity will execute this code when its created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        configureActivityView();
    }

    /**
     * Configures the Activity's View.
     */
    private void configureActivityView() {
        profilePhotoImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        ageTextView = findViewById(R.id.ageTextView);
        locationTextView = findViewById(R.id.locationTextView);
        badgeTextView = findViewById(R.id.badgeExpertTextView);
        reviewedSurveysButton = findViewById(R.id.reviewListButton);
        contestBadgeListButton = findViewById(R.id.contestBadgeListButton);
        suggestionListButton = findViewById(R.id.suggestionListButton);
        authenticationToken = getIntent().getExtras().getString("authenticationToken");
        checkIfProfilePictureExists();
        configureImageAndTextViews();
        configureButtons();
    }

    /**
     * Checks if a profile picture was already taken or not ands sets it to the image view if it already exists.
     */
    private void checkIfProfilePictureExists() {
        pictureImagePath = PreferenceManager.getDefaultSharedPreferences(this).getString("profilePicture",
                getString(R.string.no_profile_picture));
        if (!pictureImagePath.equals(getString(R.string.no_profile_picture))) {
            setPictureOnImageView();
        }
    }

    /**
     * Sets an image to profilePhotoImageView.
     */
    private void setPictureOnImageView() {
        File imageFile = new File(pictureImagePath);
        if (imageFile.exists()) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            try {
                ExifInterface exifInterface = new ExifInterface(imageFile.getAbsolutePath());
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        1);
                Matrix matrix = new Matrix();
                if (orientation == 8) {
                    matrix.postRotate(270);
                } else if (orientation == 6) {
                    matrix.postRotate(90);
                }
                Bitmap rotatedImageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0,
                        imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
                profilePhotoImageView.setImageBitmap(rotatedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Configures the image and text views of this activity.
     */
    private void configureImageAndTextViews() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!authenticationToken.equals(getString(R.string.no_authentication_token))) {
            UserProfileJSONService userProfileJSONService = getUserProfileJSONService();
            if (userProfileJSONService != null) {
                sharedPreferences.edit().putString("name",userProfileJSONService.getName()).apply();
                sharedPreferences.edit().putString("age",userProfileJSONService.getAge()).apply();
                sharedPreferences.edit().putString("location",userProfileJSONService.getLocation()).apply();
                nameTextView.setText(userProfileJSONService.getName());
                ageTextView.setText(userProfileJSONService.getAge() + "    Anos");
                locationTextView.setText(userProfileJSONService.getLocation());
                badgeTextView.setText(R.string.info_not_available);
            } else {
                nameTextView.setText("Nome");
                ageTextView.setText("Idade  ");
                locationTextView.setText("Localidade  ");
                badgeTextView.setText("Badges");
            }
        } else {
                nameTextView.setText(sharedPreferences.getString("name","Nome"));
                ageTextView.setText(sharedPreferences.getString("age","Idade") + "    Anos");
                locationTextView.setText(sharedPreferences.getString("location","Localidade"));
                badgeTextView.setText(R.string.info_not_available);
        }
        setProfilePicture();
    }

    /**
     * Creates a new Thread to request a UserProfileJSONService from the server.
     *
     * @return UserProfileJSONService
     */
    private UserProfileJSONService getUserProfileJSONService() {
        UserProfileJSONService userProfileJSONService = null;
        Thread connectionThread = new Thread(() -> {
            try {
                Response response = RESTRequest.create(BuildConfig.SERVER_URL.
                        concat(FeedbackMonkeyAPI.getAPIEntryPoint().
                                concat(FeedbackMonkeyAPI.getResourcePath("User Profile").
                                        concat(FeedbackMonkeyAPI.getSubResourcePath("User Profile",
                                                "Get User Info").concat("/" + authenticationToken))))).
                        withMediaType(RESTRequest.RequestMediaType.JSON).
                        GET();
                restResponseCode = response.code();
                restResponseBody = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        connectionThread.start();

        try {
            connectionThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (restResponseCode == HttpsURLConnection.HTTP_OK) {
            userProfileJSONService = new Gson().fromJson(restResponseBody, UserProfileJSONService.class);
        }
        return userProfileJSONService;
    }

    /**
     * Configures the buttons of this activity.
     */
    private void configureButtons() {
        reviewedSurveysButton.setOnClickListener(view -> {
            prepareListSurveyActivityStart();
        });
        contestBadgeListButton.setOnClickListener(view -> {
            ToastNotification.show(this, getString(R.string.functionality_not_implemented));
        });
        suggestionListButton.setOnClickListener(view -> {
            ToastNotification.show(this, getString(R.string.functionality_not_implemented));
        });
    }

    /**
     * Runs a new Thread that prepares the list survey activity start.
     */
    private void prepareListSurveyActivityStart() {
        new Thread(fetchUserAnsweredSurveys()).start();
    }

    /**
     * Fetches the user answered survey list.
     *
     * @return Runnable that executes a REST Request to get all the surveys that have been fully answered by a user
     */
    private Runnable fetchUserAnsweredSurveys() {
        return () -> {
            try {
                List<SurveyService> surveyServiceList = new SurveyServiceController(authenticationToken).getUserAnsweredSurveys((short) 0);
                startListSurveyActivity(surveyServiceList);
            } catch (RESTfulException restfulException) {
                switch (restfulException.getCode()) {
                    case HttpsURLConnection.HTTP_BAD_REQUEST:
                        ToastNotification.show(UserProfileActivity.this, "Ainda não realizou nenhuma avaliação.");
                        break;
                    case HttpsURLConnection.HTTP_UNAUTHORIZED:
                        //TODO: Authentication Token is not valid anymore, Go back to LoginActivity
                        break;
                    default:
                        ToastNotification.show(UserProfileActivity.this, "Erro de conexão ao servidor, tente mais tarde");
                        break;
                }
            } catch (IOException e) {
                ToastNotification.show(UserProfileActivity.this, getString(R.string.no_internet_connection));
            }
        };
    }

    /**
     * Creates an intent to start ListSurveyActivity
     *
     * @param surveyServiceList list of the surveys a user has fully answered that are to be listed in ListSurveyActivity
     */
    private void startListSurveyActivity(List<SurveyService> surveyServiceList) {
        Intent listSurveyActivityIntent = new Intent(UserProfileActivity.this, ListSurveyActivity.class);
        Bundle bundle = new Bundle(surveyServiceList.size() + 2);
        bundle.putString("authenticationToken", authenticationToken);
        bundle.putString("sentFromProfileActivity", this.getClass().getSimpleName());
        for (int i = 0; i < surveyServiceList.size(); i++) {
            bundle.putSerializable("" + i, surveyServiceList.get(i));
        }
        listSurveyActivityIntent.putExtra(ListSurveyActivity.class.getSimpleName(), bundle);
        startActivity(listSurveyActivityIntent);
    }

    /**
     * Sets an on click listener to the image view so the user can take his profile picture
     * TODO Check if a picture already exists in the app's folders.
     */
    private void setProfilePicture() {
        profilePhotoImageView.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                }
            }
            Uri outputFileUri = getOutputFileUri();
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });
    }

    /**
     * Handles camera permission request to take a profile picture
     *
     * @param requestCode  code of the request
     * @param permissions  array with the set of permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Uri outputFileUri = getOutputFileUri();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }

    /**
     * Saves a file with the profile picture in the app's folder.
     *
     * @return Uri of the file
     */
    private Uri getOutputFileUri() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_PROFILE_PICTURE.jpg";
        File storageDir = getFilesDir();
        File imageDir = new File(storageDir, "images");
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        File imageFile = new File(imageDir, imageFileName);
        try {
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pictureImagePath = imageFile.getAbsolutePath();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("profilePicture",
                pictureImagePath).apply();
        return GenericFileProvider.getUriForFile(getApplicationContext(),
                "cdioil.feedbackmonkey.utils.GenericFileProvider", imageFile);
    }

    /**
     * Retrieves the photo taken with the camera and sets it on the image view
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        intent with the photo
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            setPictureOnImageView();
        }
    }
}
