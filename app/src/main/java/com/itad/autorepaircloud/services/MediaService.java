package com.itad.autorepaircloud.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itad.autorepaircloud.models.MediaModel;
import com.itad.autorepaircloud.utils.HttpRequestHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MediaService {

    public static void sendMedia(String data, String format, String keyId){
        /* concern
        String table = "request";
        String mediaRecordId = "176315";// - concernId
        String startMediaUrl = (format.equals("mp4")?"/media/":"/"); //- for image '/', other '/media/'
        */
        /* inspection
        String table = "bodydamage";
        String mediaRecordId = "1215";// - id marker
        String startMediaUrl = (format.equals("mp4")?"/media/":"/");
        */

        String table = "bodydamage";
        String mediaRecordId = "1215";
        String startMediaUrl = (format.equals("mp4")?"/media/":"/");

        MediaModel mediaMode
        String mediaURL = startMediaUrl + (format.equals("mp4")?"video":"image")+"/"+table+"/";
        mediaModel.setRecordTable(table);
        mediaModel.setRecordId(mediaRecordId);
        mediaModel.setMediaType(format);
        mediaModel.setFileFormat(format);
        mediaModel.setFileLocation(mediaURL + createMediaNode(data, format, keyId, table));
        mediaModel.setFileCreated(System.currentTimeMillis());
        mediaModel.setStatus("active");
        mediaModel.setSequenceNum(5);
        uploadAttachment(mediaModel);
    }

    private static void uploadAttachment(MediaModel mediaModel){
        ObjectMapper objectMapper = new ObjectMapper();
        String mediaModelJSON="";
        try {
            mediaModelJSON = objectMapper.writeValueAsString(mediaModel);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequestHelper.getInstance().sendPost(
                "application/json; charset=utf8",
                mediaModelJSON,
                "https://qa.autorepaircloud.com/auto-rest/mediaAttachment/",
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            final String exception = System.currentTimeMillis() + " - Unexpected code " + response;
                            throw new IOException(exception);
                        }
                        System.out.println("Success load - "+response.body().string());
                    }
                });
    }

    private static String createMediaNode(String data, String format, String keyId, String table){
        final String[] responseMediaName = {""};
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        HttpRequestHelper.getInstance().sendPost(
                "application/octet-stream",
                data,
                "https://qa.autorepaircloud.com/auto-rest/mediaAttachment/uploadMedia/"+table+"?keyId="+keyId+"&format="+format,
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                        countDownLatch.countDown();
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            final String exception = System.currentTimeMillis() + " - Unexpected code " + response;
                            throw new IOException(exception);
                        }
                        responseMediaName[0] = response.body().string().replace("\"", "");
                        countDownLatch.countDown();
                    }
                });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return responseMediaName[0];
    }
}
