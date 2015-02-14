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



    private Integer UserID;
    private User currUser;

    public Integer getUserID() {
        return UserID;
    }

    public void setUserID(Integer userID) {
        UserID = userID;
        currUser = new User(UserID);
    }

    public User getCurrUser() { return currUser; }

    @Override
    public void onCreate() {
        super.onCreate();


    }

}
