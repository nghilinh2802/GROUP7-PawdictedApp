package com.group7.pawdicted.mobile.services;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.IOException;
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

public class SearchService {
    private static final String TAG = "SearchService";
    private static final String API_BASE_URL = "https://linhldn22411c-smart-search.hf.space";
    private static final String SEARCH_ENDPOINT = "/search";

    private OkHttpClient client;
    private Gson gson;

    public SearchService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public interface SearchCallback {
        void onSuccess(List<String> productIds);
        void onError(String error);
    }

    public void searchProducts(String query, SearchCallback callback) {
        if (query == null || query.trim().isEmpty()) {
            callback.onError("Query cannot be empty");
            return;
        }

        try {
            // Tạo JSON request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("query", query.trim());
            requestBody.addProperty("top_k", 50); // Số lượng kết quả tối đa

            String json = gson.toJson(requestBody);
            Log.d(TAG, "Sending search request: " + json);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json
            );

            Request request = new Request.Builder()
                    .url(API_BASE_URL + SEARCH_ENDPOINT)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "API call failed", e);
                    callback.onError("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "API response: " + responseBody);

                        if (response.isSuccessful()) {
                            List<String> productIds = parseSearchResponse(responseBody);
                            callback.onSuccess(productIds);
                        } else {
                            Log.e(TAG, "API error: " + response.code() + " - " + responseBody);
                            callback.onError("API error: " + response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        callback.onError("Error parsing response: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
            callback.onError("Error creating request: " + e.getMessage());
        }
    }

    private List<String> parseSearchResponse(String responseBody) {
        List<String> productIds = new ArrayList<>();

        try {
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            // Kiểm tra structure của response
            if (jsonResponse.has("results")) {
                JsonArray results = jsonResponse.getAsJsonArray("results");

                for (int i = 0; i < results.size(); i++) {
                    JsonObject result = results.get(i).getAsJsonObject();

                    // Giả sử API trả về product_id trong kết quả
                    if (result.has("id")) {
                        String productId = result.get("id").getAsString();
                        productIds.add(productId);
                    }
                }
            } else if (jsonResponse.has("product_ids")) {
                // Nếu API trả về trực tiếp danh sách product_ids
                JsonArray productIdsArray = jsonResponse.getAsJsonArray("product_ids");

                for (int i = 0; i < productIdsArray.size(); i++) {
                    String productId = productIdsArray.get(i).getAsString();
                    productIds.add(productId);
                }
            } else {
                Log.w(TAG, "Unknown response format: " + responseBody);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing search response", e);
        }

        return productIds;
    }

    public void shutdown() {
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }
}