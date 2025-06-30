package com.group7.pawdicted.mobile.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecommendationService {
    private static final String TAG = "RecommendationService";
    private static final String API_BASE_URL = "https://trinhhhh-pawdictedapp.hf.space";
    private static final String RECOMMEND_ENDPOINT = "/gradio_api/call/predict";
    private static final String PREFS_NAME = "RecommendationCache";
    private static final long CACHE_DURATION = TimeUnit.HOURS.toMillis(24); // 24 hours
    private static final int POLL_INTERVAL_MS = 1000; // Poll every 1 second
    private static final int MAX_POLL_ATTEMPTS = 10; // Max 10 seconds

    private OkHttpClient client;
    private Gson gson;
    private SharedPreferences prefs;

    public RecommendationService(Context context) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public interface RecommendationCallback {
        void onSuccess(List<String> productIds);
        void onError(String error);
    }

    public void getRecommendations(String customerId, int topK, RecommendationCallback callback) {
        if (customerId == null || customerId.trim().isEmpty()) {
            Log.e(TAG, "Customer ID is empty, cannot fetch recommendations");
            callback.onError("Customer ID cannot be empty");
            return;
        }

        // Check cache first
        String cacheKey = "recommendations_" + customerId + "_" + topK;
        String cachedJson = prefs.getString(cacheKey, null);
        long cacheTimestamp = prefs.getLong(cacheKey + "_timestamp", 0);
        long currentTime = System.currentTimeMillis();

        if (cachedJson != null && (currentTime - cacheTimestamp) < CACHE_DURATION) {
            Log.d(TAG, "Returning cached recommendations for customerId: " + customerId);
            List<String> productIds = parseCachedRecommendations(cachedJson);
            if (!productIds.isEmpty()) {
                callback.onSuccess(productIds);
                return;
            }
        }

        try {
            JsonObject requestBody = new JsonObject();
            JsonArray dataArray = new JsonArray();
            dataArray.add(customerId.trim());
            dataArray.add(topK);
            requestBody.add("data", dataArray);

            String json = gson.toJson(requestBody);
            Log.d(TAG, "Sending recommendation request: " + json);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json
            );

            Request request = new Request.Builder()
                    .url(API_BASE_URL + RECOMMEND_ENDPOINT)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "API POST call failed for customerId: " + customerId, e);
                    callback.onError("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            String responseBody = response.body() != null ? response.body().string() : "No response body";
                            Log.e(TAG, "API POST error for customerId: " + customerId + ": " + response.code() + " - " + responseBody);
                            callback.onError("API POST error: " + response.code());
                            return;
                        }

                        String responseBody = response.body().string();
                        Log.d(TAG, "API POST response for customerId: " + customerId + ": " + responseBody);

                        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                        if (jsonResponse.has("event_id")) {
                            String eventId = jsonResponse.get("event_id").getAsString();
                            Log.d(TAG, "Received event_id: " + eventId);
                            pollForResult(eventId, customerId, topK, callback, 0);
                        } else {
                            Log.e(TAG, "No event_id in response: " + responseBody);
                            callback.onError("Invalid API response: No event_id");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing POST response for customerId: " + customerId, e);
                        callback.onError("Error parsing POST response: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error creating POST request for customerId: " + customerId, e);
            callback.onError("Error creating request: " + e.getMessage());
        }
    }

    private void pollForResult(String eventId, String customerId, int topK, RecommendationCallback callback, int attempt) {
        if (attempt >= MAX_POLL_ATTEMPTS) {
            Log.e(TAG, "Max poll attempts reached for eventId: " + eventId);
            callback.onError("Max poll attempts reached");
            return;
        }

        Request request = new Request.Builder()
                .url(API_BASE_URL + RECOMMEND_ENDPOINT + "/" + eventId)
                .get()
                .addHeader("Accept", "text/event-stream") // Updated to expect event-stream
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Poll GET failed for eventId: " + eventId, e);
                callback.onError("Poll error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "No response body";
                        Log.e(TAG, "Poll GET error for eventId: " + eventId + ": " + response.code() + " - " + responseBody);
                        callback.onError("Poll error: " + response.code());
                        return;
                    }

                    String responseBody = response.body().string();
                    Log.d(TAG, "Poll GET response for eventId: " + eventId + ": " + responseBody);

                    // Parse event-stream response
                    List<String> productIds = parseEventStreamResponse(responseBody);
                    if (!productIds.isEmpty()) {
                        // Cache the results
                        String cacheKey = "recommendations_" + customerId + "_" + topK;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(cacheKey, gson.toJson(productIds));
                        editor.putLong(cacheKey + "_timestamp", System.currentTimeMillis());
                        editor.apply();
                        Log.d(TAG, "Cached recommendations for customerId: " + customerId);
                        callback.onSuccess(productIds);
                    } else {
                        Log.w(TAG, "No product_ids in response, polling again: " + responseBody);
                        try {
                            Thread.sleep(POLL_INTERVAL_MS);
                            pollForResult(eventId, customerId, topK, callback, attempt + 1);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Poll interrupted for eventId: " + eventId, e);
                            callback.onError("Poll interrupted: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing poll GET response for eventId: " + eventId, e);
                    callback.onError("Error processing poll response: " + e.getMessage());
                }
            }
        });
    }

    private List<String> parseEventStreamResponse(String responseBody) {
        List<String> productIds = new ArrayList<>();
        try {
            // Event-stream response looks like:
            // ‘event: complete\ndata: [{"product_ids": ["AC0001", ...]}]\n\n’
            String[] lines = responseBody.split("\n");
            String dataLine = null;
            for (String line : lines) {
                if (line.startsWith("data: ")) {
                    dataLine = line.substring(6); // Remove "data: " prefix
                    break;
                }
            }

            if (dataLine == null) {
                Log.w(TAG, "No data line found in event-stream response: " + responseBody);
                return productIds;
            }

            // Parse the JSON data
            JsonArray dataArray = gson.fromJson(dataLine, JsonArray.class);
            if (dataArray.size() > 0 && dataArray.get(0).isJsonObject()) {
                JsonObject result = dataArray.get(0).getAsJsonObject();
                if (result.has("product_ids")) {
                    JsonArray productIdsArray = result.getAsJsonArray("product_ids");
                    for (int i = 0; i < productIdsArray.size(); i++) {
                        productIds.add(productIdsArray.get(i).getAsString());
                    }
                    Log.d(TAG, "Parsed product IDs: " + productIds);
                } else if (result.has("error")) {
                    Log.w(TAG, "API returned error: " + result.get("error").getAsString());
                } else {
                    Log.w(TAG, "No product_ids in response: " + responseBody);
                }
            } else {
                Log.w(TAG, "Empty or invalid data array in response: " + responseBody);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing event-stream response", e);
        }
        return productIds;
    }

    private List<String> parseCachedRecommendations(String cachedJson) {
        List<String> productIds = new ArrayList<>();
        try {
            Type listType = new TypeToken<List<String>>(){}.getType();
            productIds = gson.fromJson(cachedJson, listType);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing cached recommendations", e);
        }
        return productIds;
    }

    public void shutdown() {
        if (client != null) {
            client.dispatcher().cancelAll();
            client.dispatcher().executorService().shutdown();
        }
    }
}