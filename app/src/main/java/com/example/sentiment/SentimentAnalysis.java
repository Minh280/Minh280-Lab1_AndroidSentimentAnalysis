package com.example.sentiment;

import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.*;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class SentimentAnalysis {
    private final OkHttpClient client = new OkHttpClient();
    private final String API_KEY = "AIzaSyDF2Lmq025qesr64f1PLefZOeEld_7na84";
    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-pro-exp-02-05:generateContent?key=AIzaSyAauv7NGbquind4UnFWoS0bWEvpQuHBNN4";

    public void analyze(@NotNull String text,@NotNull final Callback callback) {
        final String prompt = "Analyze the sentiment of this text: \"" + text + "\".\n" +
                "Reply ONLY with one word: Positive, Negative, or Neutral. No explanation.";


        JSONObject json = new JSONObject();
        try {
            JSONArray contents = new JSONArray();
            JSONObject parts = new JSONObject();
            parts.put("text", prompt);
            JSONArray partsArray = new JSONArray();
            partsArray.put(parts);
            JSONObject content = new JSONObject();
            content.put("parts", partsArray);
            contents.put(content);
            json.put("contents", contents);
        } catch (Exception e) {
            callback.onError("Error creating JSON request: " + e.getMessage());
            return;
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json.toString(), mediaType);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Error: " + response.code());
                    return;
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                Log.d("API_RESPONSE", responseBody);
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String sentimentResult = jsonResponse
                            .getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");
                    callback.onSuccess(sentimentResult);
                } catch (Exception e) {
                    callback.onError("Could not analyze");
                }
            }
        });
    }

    public interface Callback {
        void onSuccess(String sentiment);
        void onError(String error);
    }
}
