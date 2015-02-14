package finalproject.shareboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import finalproject.shareboard.framework.ApiConnector;


public class MainMenuActivity extends ShareBoardActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEdit;

    Dialog registerUserDialog;
    Dialog adValidDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        InitializeComponents();

        preferences =
                getSharedPreferences("ShareBoard",MODE_PRIVATE);
        prefEdit = preferences.edit();
        prefEdit.clear().apply();
        ((ShareBoardApplication)getApplication()).setUserID(preferences.getInt("UserID", 0));

        if (((ShareBoardApplication)getApplication()).getUserID() == 0) {
            initalizeRegisterUserDialog();
        }
//         dialog.show();
//        new GetAllUsers().execute(new ApiConnector());
    }

    private void InitializeComponents() {
        final Button AddAdButton = (Button) findViewById(R.id.btn_add_ad);
        final Button CreateButton = (Button) findViewById(R.id.btnCreate);
        final Button MyBoardsButton = (Button) findViewById(R.id.myBoards);
        AddAdButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OpenAdActivity();
            }
        });

        CreateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OpenCreateActivity();
            }
        });

        MyBoardsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OpenMyBoardsActivity();
            }
        });

        AlertDialog.Builder adValidDialogBuilder = new AlertDialog.Builder(this);
        adValidDialogBuilder.setMessage("Oops! Something went wrong... Please try correct your details or try again later")
                .setCancelable(false)
                .setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        adValidDialog = adValidDialogBuilder.create();
    }

    private void OpenMyBoardsActivity() {
        Intent MyBoardsActivity = new Intent(this, MyBoardListActivity.class);
        startActivity(MyBoardsActivity);
    }

    private void initalizeRegisterUserDialog() {
        registerUserDialog = new Dialog(MainMenuActivity.this);
        registerUserDialog.setContentView(R.layout.register_user);
        registerUserDialog.setTitle("Registration");

        final EditText registerUserInput = (EditText) registerUserDialog.findViewById(R.id.etRegisterUser);
        Button registerUserSaveButton = (Button) registerUserDialog.findViewById(R.id.btnRegisterUserSave);

        registerUserSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer UserID = Integer.parseInt(registerUserInput.getText().toString());
                registerNewUser(UserID);
            }
        });

        registerUserDialog.setCancelable(false);
        registerUserDialog.show();
    }

    private void registerNewUser(Integer NewUserID) {
        dialog.show();
        new insertNewUser(NewUserID).execute(new ApiConnector());
    }

    private void getUser(JSONArray jsonArray) {
        EditText Test = (EditText) findViewById(R.id.Test);

        String user = "";

        for (int i=0; i<jsonArray.length();i++) {
            JSONObject curr = null;
            try {
                curr = jsonArray.getJSONObject(i);
                user = curr.getString("UserID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Test.setText(user);
        dialog.dismiss();
    }

    private class GetAllUsers extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].getAllUsers();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {

        }
    }

    private class insertNewUser extends AsyncTask<ApiConnector, Long, Integer> {
        private Integer UserId;

        protected insertNewUser(Integer UserIDToAdd) {
            super();
            UserId = UserIDToAdd;
        }

        @Override

        protected Integer doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].InsertUser(UserId);
        }

        @Override
        protected void onPostExecute(Integer success) {
            if (success != 0) {
                ((ShareBoardApplication) getApplication()).setUserID(UserId);
                ((EditText) findViewById(R.id.Test)).setText(((ShareBoardApplication) getApplication()).getUserID().toString());
                prefEdit.putInt("UserID", UserId).apply();
                registerUserDialog.dismiss();
            } else {
                adValidDialog.show();
            }
            dialog.dismiss();
        }
    }

    private void OpenAdActivity() {
        Intent AddAdActivity = new Intent(this, AdActivity.class);
        startActivity(AddAdActivity);
    }

    private void OpenCreateActivity() {
        Intent AddCreateActivity = new Intent(this, CreateBoardActivity.class);
        startActivity(AddCreateActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getActionBar().setTitle(R.string.welcome_activity_title);
    }
}
