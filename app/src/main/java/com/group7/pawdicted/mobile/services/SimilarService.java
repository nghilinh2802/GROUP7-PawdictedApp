package com.group7.pawdicted.mobile.services;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.group7.pawdicted.mobile.models.Recommendation;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SimilarService {
    private static final String API_URL = "https://trinhhhh-pawdicted2.hf.space/gradio_api/call/predict";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface SimilarCallback {
        void onSuccess(List<Recommendation> recommendations);
        void onFailure(String error);
    }

    public void getSimilarProducts(String productId, int numRecommendations, SimilarCallback callback) {
        JsonObject payload = new JsonObject();
        JsonArray dataArray = new JsonArray();
        dataArray.add(productId);
        dataArray.add(numRecommendations);
        payload.add("data", dataArray);

        RequestBody body = RequestBody.create(JSON, gson.toJson(payload));
        Request postRequest = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(postRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onFailure("API Error: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "No response body";
                    Log.e("SimilarService", "POST failed: " + response.message() + ", body: " + responseBody);
                    mainHandler.post(() -> callback.onFailure("API Failed: " + response.message()));
                    response.close();
                    return;
                }

                String responseBody = response.body().string();
                Log.d("SimilarService", "POST response: " + responseBody);
                response.close();
                try {
                    JsonReader jsonReader = new JsonReader(new StringReader(responseBody));
                    jsonReader.setLenient(true);
                    JsonObject jsonResponse = JsonParser.parseReader(jsonReader).getAsJsonObject();
                    String eventId = jsonResponse.get("event_id").getAsString();

                    Request getRequest = new Request.Builder()
                            .url(API_URL + "/" + eventId)
                            .get()
                            .build();

                    client.newCall(getRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            mainHandler.post(() -> callback.onFailure("API Error: " + e.getMessage()));
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                String responseBody = response.body() != null ? response.body().string() : "No response body";
                                Log.e("SimilarService", "GET failed: " + response.message() + ", body: " + responseBody);
                                mainHandler.post(() -> callback.onFailure("API Failed: " + response.message()));
                                response.close();
                                return;
                            }

                            String resultBody = response.body().string();
                            Log.d("SimilarService", "GET raw response: " + resultBody);
                            response.close();

                            try {
                                String jsonData = extractJsonFromSSE(resultBody);
                                if (jsonData == null) {
                                    mainHandler.post(() -> callback.onFailure("Parsing Error: Invalid SSE response"));
                                    return;
                                }
                                Log.d("SimilarService", "Extracted JSON: " + jsonData);

                                JsonReader jsonReader = new JsonReader(new StringReader(jsonData));
                                jsonReader.setLenient(true);
                                JsonArray outerArray = JsonParser.parseReader(jsonReader).getAsJsonArray();
                                if (outerArray == null || outerArray.size() == 0) {
                                    mainHandler.post(() -> callback.onFailure("Parsing Error: Empty response array"));
                                    return;
                                }

                                // Handle nested array structure
                                JsonArray recArray = outerArray.get(0).isJsonArray() ? outerArray.get(0).getAsJsonArray() : outerArray;
                                if (recArray.size() == 0) {
                                    mainHandler.post(() -> callback.onFailure("Parsing Error: No recommendations found"));
                                    return;
                                }

                                List<Recommendation> recommendations = new ArrayList<>();
                                for (int i = 0; i < recArray.size(); i++) {
                                    JsonObject rec = recArray.get(i).getAsJsonObject();
                                    if (rec.has("error")) {
                                        mainHandler.post(() -> callback.onFailure("API Error: " + rec.get("error").getAsString()));
                                        return;
                                    }
                                    String productId = rec.get("Product ID").getAsString();
                                    String productName = rec.get("Product Name").getAsString();
                                    double distance = rec.get("Distance").getAsDouble();
                                    String imageUrl = rec.get("Image").getAsString();
                                    recommendations.add(new Recommendation(productId, productName, distance, imageUrl));
                                }

                                if (recommendations.isEmpty()) {
                                    mainHandler.post(() -> callback.onFailure("Parsing Error: No valid recommendations"));
                                    return;
                                }

                                mainHandler.post(() -> callback.onSuccess(recommendations));
                            } catch (Exception e) {
                                Log.e("SimilarService", "Parsing error: " + e.getMessage() + ", response: " + resultBody);
                                mainHandler.post(() -> callback.onFailure("Parsing Error: " + e.getMessage()));
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("SimilarService", "Parsing error in POST: " + e.getMessage() + ", response: " + responseBody);
                    mainHandler.post(() -> callback.onFailure("Parsing Error: " + e.getMessage()));
                }
            }
        });
    }

    private String extractJsonFromSSE(String sseResponse) {
        String[] lines = sseResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("data: ")) {
                return line.substring(6);
            }
        }
        return null;
    }
}