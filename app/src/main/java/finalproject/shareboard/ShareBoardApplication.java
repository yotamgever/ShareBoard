package finalproject.shareboard;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import finalproject.shareboard.model.User;

public class ShareBoardApplication extends Application {

    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEdit;

    private String UserID;
    private User currUser;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
        prefEdit.putString("UserID", userID);
        currUser = new User(Integer.valueOf(UserID));
    }

    public User getCurrUser() { return currUser; }

    @Override
    public void onCreate() {
        super.onCreate();

        preferences =
            getSharedPreferences("ShareBoard",MODE_PRIVATE);
        prefEdit = preferences.edit();
        UserID = preferences.getString("UserID", "");
    }

}
