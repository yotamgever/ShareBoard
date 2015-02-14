package finalproject.shareboard;

import android.app.Dialog;
import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        InitializeComponents();

        if (((ShareBoardApplication)getApplication()).getUserID() == "") {
            initalizeRegisterUserDialog();
        }
//        dialog.show();
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
    }

    private void OpenMyBoardsActivity() {
        Intent MyBoardsActivity = new Intent(this, MyBoardListActivity.class);
        startActivity(MyBoardsActivity);
    }

    private void initalizeRegisterUserDialog() {
        final Dialog registerUserDialog = new Dialog(MainMenuActivity.this);
        registerUserDialog.setContentView(R.layout.register_user);
        registerUserDialog.setTitle("Registration");

        final EditText registerUserInput = (EditText) registerUserDialog.findViewById(R.id.etRegisterUser);
        Button registerUserSaveButton = (Button) registerUserDialog.findViewById(R.id.btnRegisterUserSave);

        registerUserSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer UserID = Integer.parseInt(registerUserInput.getText().toString());
                registerNewUser(UserID);
                registerUserDialog.dismiss();
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

    private class insertNewUser extends AsyncTask<ApiConnector, Long, JSONObject> {
        private Integer UserId;

        protected insertNewUser(Integer UserIDToAdd) {
            super();
            UserId = UserIDToAdd;
        }

        @Override

        protected JSONObject doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].InsertUser(UserId);
        }

        @Override
        protected void onPostExecute(JSONObject jsonArray) {
            ((ShareBoardApplication)getApplication()).setUserID(UserId.toString());
            ((EditText)findViewById(R.id.Test)).setText(((ShareBoardApplication)getApplication()).getUserID());
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
