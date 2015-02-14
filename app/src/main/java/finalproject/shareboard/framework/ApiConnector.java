package finalproject.shareboard.framework;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import finalproject.shareboard.model.Ad;
import finalproject.shareboard.model.Board;
import finalproject.shareboard.model.User;

public class ApiConnector {
    // Php Scripts URLs
    String getAllUsersScript = "http://shareboardapp.comuf.com/getAllUsers.php";
    String getUsersBoardsScript = "http://shareboardapp.comuf.com/getUserBoards.php";
    String getBoardScript = "http://shareboardapp.comuf.com/getBoard.php";
    String getUserBoardAuthScript = "http://shareboardapp.comuf.com/getBoardUserAuth.php";
    String getBoardAdsScript = "http://shareboardapp.comuf.com/getBoardAds.php";
    String insertUserScript = "http://shareboardapp.comuf.com/InsertUser.php";
    String insertBoardScript = "http://shareboardapp.comuf.com/InsertBoard.php";
    String insertAdScript = "http://shareboardapp.comuf.com/InsertAd.php";
    String insertUsersToBoardScript = "http://shareboardapp.comuf.com/InsertUsersToBoard.php";

    // Create a new HttpClient and Post Header
    DefaultHttpClient httpClient = new DefaultHttpClient();

    public JSONArray getAllUsers()
    {
        HttpEntity httpEntity = null;

        try {
            HttpGet httpGet = new HttpGet(getAllUsersScript);

            HttpResponse httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray allUsers = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response: ", entityResponse);

                allUsers = new JSONArray(entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return allUsers;
    }

    public JSONObject InsertUser(Integer UserID) {

        HttpPost httppost = new HttpPost(insertUserScript);

        HttpEntity httpEntity = null;

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("UserID", UserID.toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httppost);

            httpEntity = response.getEntity();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject success = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response: ", entityResponse);

                success = new JSONObject(entityResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public Integer InsertBoard(Board boardToAdd) {

        HttpPost httppost = new HttpPost(insertBoardScript);

        HttpEntity httpEntity = null;

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("boardName", boardToAdd.getBoardName()));
            nameValuePairs.add(new BasicNameValuePair("boardType", String.valueOf(boardToAdd.getBoardType().ordinal())));
            nameValuePairs.add(new BasicNameValuePair("creatorUser", ((User)boardToAdd.getCreator()).getUserId().toString()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httppost);

            httpEntity = response.getEntity();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject success = null;
        Integer boardID = 0;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response: ", entityResponse);

                success = new JSONObject(entityResponse);

                boardID = success.getInt("ID");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return boardID;
    }

    public Integer InsertUsersToBoard(Board boardToAdd) {

        HttpPost httppost = new HttpPost(insertUsersToBoardScript);

        HttpEntity httpEntity = null;

        try {
            //Create JSON string start
            String json_string ="{\"users_to_board\":[";

            for (User boardUser : boardToAdd.getUsers().keySet()) {
                JSONObject userToBoard = new JSONObject();
                userToBoard.put("boardID", boardToAdd.getBoardID().toString());
                userToBoard.put("UserID", boardUser.getUserId().toString());
                userToBoard.put("UserAuth", String.valueOf(boardToAdd.getUsers().get(boardUser).ordinal()));
                json_string = json_string + userToBoard.toString() + ",";
            }

            //Close JSON string
            json_string = json_string.substring(0, json_string.length()-1);
            json_string += "]}";

            httppost.setEntity(new ByteArrayEntity(json_string.getBytes("UTF8")));
            httppost.setHeader("json", json_string);

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httppost);

            httpEntity = response.getEntity();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        JSONObject success = null;
        Integer UsersCommited = 0;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response: ", entityResponse);

                success = new JSONObject(entityResponse);

                UsersCommited = success.getInt("UsersCommited");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return UsersCommited;
    }

    public JSONArray getUserBoards(Integer UserID)
    {
        HttpEntity httpEntity = null;

        try {
            HttpPost httpPost = new HttpPost(getUsersBoardsScript);

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("UserID", UserID.toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httpPost);

            httpEntity = response.getEntity();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray allBoards = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response: ", entityResponse);

                allBoards = new JSONArray(entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return allBoards;
    }

    public JSONArray getBoard(Integer BoardID)
    {
        HttpEntity httpEntity = null;

        try {
            HttpPost httpPost = new HttpPost(getBoardScript);

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("BoardID", BoardID.toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httpPost);

            httpEntity = response.getEntity();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray wantedBoard = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response: ", entityResponse);

                wantedBoard = new JSONArray(entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return wantedBoard;
    }

    public JSONArray getUserBoardAuth(Integer BoardID, Integer UserId)
    {
        HttpEntity httpEntity = null;

        try {
            HttpPost httpPost = new HttpPost(getUserBoardAuthScript);

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("BoardID", BoardID.toString()));
            nameValuePairs.add(new BasicNameValuePair("UserID", UserId.toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httpPost);

            httpEntity = response.getEntity();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray wantedAuth = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response: ", entityResponse);

                wantedAuth = new JSONArray(entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return wantedAuth;
    }

    public JSONArray getBoardAds(Integer BoardID)
    {
        HttpEntity httpEntity = null;

        try {
            HttpPost httpPost = new HttpPost(getBoardAdsScript);

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("BoardID", BoardID.toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httpPost);

            httpEntity = response.getEntity();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray boardAds = null;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response: ", entityResponse);

                boardAds = new JSONArray(entityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return boardAds;
    }

    public Integer InsertAd(Ad adToAdd) {

        HttpPost httppost = new HttpPost(insertAdScript);

        HttpEntity httpEntity = null;

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("BoardID", adToAdd.getBoardId().toString()));
            nameValuePairs.add(new BasicNameValuePair("UserID", adToAdd.getUserId().toString()));
            nameValuePairs.add(new BasicNameValuePair("AdType", String.valueOf(adToAdd.getAdType().ordinal())));
            nameValuePairs.add(new BasicNameValuePair("AdTitle", adToAdd.getAdTitle()));
            nameValuePairs.add(new BasicNameValuePair("AdPriority", String.valueOf(adToAdd.getAdPriority().ordinal())));
            nameValuePairs.add(new BasicNameValuePair("AdDesc", adToAdd.getAdDesc()));
            nameValuePairs.add(new BasicNameValuePair("AdFromDate", null));
            nameValuePairs.add(new BasicNameValuePair("AdToDate", null));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httppost);

            httpEntity = response.getEntity();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject success = null;
        Integer AdID = 0;

        if (httpEntity != null) {
            try {
                String entityResponse = EntityUtils.toString(httpEntity);

                Log.e("Entity Response: ", entityResponse);

                success = new JSONObject(entityResponse);

                AdID = success.getInt("ID");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return AdID;
    }
}
