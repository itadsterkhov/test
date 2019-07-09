package com.itad.autorepaircloud.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itad.autorepaircloud.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itad.autorepaircloud.utils.holders.AuthHolder;
import com.itad.autorepaircloud.utils.Base64Helper;
import com.itad.autorepaircloud.utils.holders.ExtendedDataHolder;
import com.itad.autorepaircloud.utils.FileConverter;
import com.itad.autorepaircloud.utils.HttpRequestHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.itad.autorepaircloud.services.MediaService.sendMedia;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    static final int REQUEST_LICENSE_PLATE_CAPTURE = 1;
    static final int REQUEST_SPEECH = 2;
    static final int REQUEST_BARCODE_SCANNER = 3;
    static final int REQUEST_PHOTO_CAPTURE = 4;
    static final int REQUEST_VIDEO_CAPTURE = 5;

    private TextView license_plate_textview;
    private ImageView license_plate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        license_plate = (ImageView) findViewById(R.id.license_plate);
        license_plate_textview = (TextView) findViewById(R.id.license_plate_textview);

        findViewById(R.id.scan_button).setOnClickListener(this);
        findViewById(R.id.license_plat_search_owner).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.send_photo).setOnClickListener(this);
        findViewById(R.id.send_video).setOnClickListener(this);
        findViewById(R.id.speech_converter).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_button: {
                scanBarcode();
                break;
            }
            case R.id.license_plat_search_owner: {
                licensePlateCapture();
                break;
            }
            case R.id.login: {
                login();
                break;
            }
            case R.id.send_photo: {
                sendPhoto();
                break;
            }
            case R.id.send_video: {
                sendVideo();
                break;
            }
            case R.id.speech_converter: {
                speechConverter();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_BARCODE_SCANNER:
                if (resultCode == Activity.RESULT_OK){
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (result != null) {
                        if (result.getContents() == null) {
                            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;

            case REQUEST_SPEECH:
                if (requestCode == REQUEST_SPEECH && resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    license_plate_textview.setText(result.get(0));
                }
                break;

            case REQUEST_LICENSE_PLATE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    ExtendedDataHolder extras = ExtendedDataHolder.getInstance();
                    byte[] bytes = (byte[]) extras.getExtra("imageBytes");
                    Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                    license_plate.setImageBitmap(bitmapImage);
                    searchLicencePlate(bitmapImage);
                    extras.clear();
                }
                break;

            case REQUEST_VIDEO_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    ExtendedDataHolder extras = ExtendedDataHolder.getInstance();
                    String videoPath = (String) extras.getExtra("videoPath");
                    try {
                        File file = new File(videoPath);
                        String fileContent = Base64Helper.encode(FileConverter.read(file));
                        sendMedia(fileContent,"mp4", "176157");
                        file.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    extras.clear();
                }
                break;

            case REQUEST_PHOTO_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    ExtendedDataHolder extras = ExtendedDataHolder.getInstance();
                    byte[] bytes = (byte[]) extras.getExtra("imageBytes");
                    try {
                        String fileContent = Base64Helper.encode(bytes);
                        /* concern
                        keyId - repairId
                        for test 13159
                         */
                        /* inspection
                        keyId - vehicleId
                        for test 23446
                         */
                        sendMedia(fileContent,"jpg", "23446");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    extras.clear();
                }
                break;
        }
    }

    private void speechConverter() {
        try {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
            startActivityForResult(intent, REQUEST_SPEECH);
        } catch(ActivityNotFoundException e) {
            String appPackageName = "com.google.android.googlequicksearchbox";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void licensePlateCapture(){
        Intent intent = new Intent(this, Camera2LicensePlateActivity.class);
        intent.putExtra("zoom", 30);
        startActivityForResult(intent, REQUEST_LICENSE_PLATE_CAPTURE);
    }

    private void sendVideo() {
        Intent intent = new Intent(this, Camera2VideoActivity.class);
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
    }

    private void sendPhoto() {
        Intent intent = new Intent(this, Camera2BasicActivity.class);
        startActivityForResult(intent, REQUEST_PHOTO_CAPTURE);
    }

    private void login(){
        HttpRequestHelper.getInstance().sendPostWithoutToken(
                "application/json",
                "{\"phone\":\"79183636554\",\"code\":\"Qwer1234\"}",
                "https://qa.autorepaircloud.com/auto-rest/auth/worker/login",
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                        final String exception = System.currentTimeMillis() + " - " + e.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                license_plate_textview.setText(exception);
                            }
                        });
                    }

                    @Override public void onResponse(Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            final String exception = System.currentTimeMillis() + " - Unexpected code " + response;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    license_plate_textview.setText(exception);
                                }
                            });
                            throw new IOException(exception);
                        }
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseBody);
                            final String plate = jsonObject.getString("token");
                            AuthHolder.getInstance().setToken(plate);
                            System.out.println(plate);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    license_plate_textview.setText("Authorized");
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void scanBarcode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_39);
        integrator.setCaptureActivity(CustomScannerActivity.class);
        integrator.setRequestCode(REQUEST_BARCODE_SCANNER);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    private void searchLicencePlate(Bitmap bitmap){
        license_plate_textview.setText("LicensePlate: ");
        HttpRequestHelper.getInstance().sendPost(
                "application/octet-stream",
                Base64Helper.encode(bitmap),
                "https://qa.autorepaircloud.com/auto-rest/vehicle/findVehicleByLPR",
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                        final String exception = System.currentTimeMillis() + " - " + e.getMessage();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                license_plate_textview.setText(exception);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            final String exception = System.currentTimeMillis() + " - Unexpected code " + response;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    license_plate_textview.setText(exception);
                                }
                            });
                            throw new IOException(exception);
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                final String plate = jsonObject.getString("licensePlate");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        license_plate_textview.setText("licensePlate: " + plate);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
