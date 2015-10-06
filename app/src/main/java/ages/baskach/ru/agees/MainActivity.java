package ages.baskach.ru.agees;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button myButton;
    EditText myAge;
    ImageView myImageView;
    ProgressBar bar;
    static final int GALLERY_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "X8JOopjvz3Ypg0FVAOzM1YU8l94iqAcFx1E65hqU", "f6SwHD23G9hhTlyiZgkmv6aBhggVh99LgBUmkvEl");

        myButton = (Button)findViewById(R.id.loadPhoto);
        myImageView = (ImageView)findViewById(R.id.myAvatar);
        myAge = (EditText)findViewById(R.id.MyAge);
        bar = (ProgressBar)findViewById(R.id.loadPhotoBar);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        ParseFile file = null;
        Bitmap galleryPic = null;
        String age = myAge.getText().toString();
        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        galleryPic = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        Bitmap mealImageScaled = Bitmap.createScaledBitmap(galleryPic, 200, 200
                                * galleryPic.getHeight() / galleryPic.getWidth(), false);

                        // Override Android default landscape orientation and save portrait
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        Bitmap rotatedScaledMealImage = Bitmap.createBitmap(mealImageScaled, 0,
                                0, mealImageScaled.getWidth(), mealImageScaled.getHeight(),
                                matrix, true);

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                        byte[] scaledData = bos.toByteArray();
                        file = new ParseFile("avatar.JPG", scaledData);
                        file.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                // Handle success or failure here ...
                            }
                        }, new ProgressCallback() {
                            public void done(Integer percentDone) {
                                bar.setProgress(percentDone);
                            }
                        });
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    myImageView.setImageBitmap(galleryPic);
                    ParseObject jobApplication = new ParseObject("Photo");
                    jobApplication.put("Login", "Joe Smith");
                    jobApplication.put("Avatar", file);
                    jobApplication.put("Age", age);
                    jobApplication.saveInBackground();
                }
        }
    }


}
