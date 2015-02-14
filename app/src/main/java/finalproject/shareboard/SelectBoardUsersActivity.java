package finalproject.shareboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import finalproject.shareboard.framework.ApiConnector;
import finalproject.shareboard.framework.Globals;
import finalproject.shareboard.model.Board;
import finalproject.shareboard.model.User;


public class SelectBoardUsersActivity extends ShareBoardActivity {

    private TextView tvContact1Number;
    private TextView tvContact1Name;
    private TextView tvContact2Number;
    private TextView tvContact2Name;
    private TextView tvContact3Number;
    private TextView tvContact3Name;
    private TextView tvContact4Number;
    private TextView tvContact4Name;
    private TextView tvContact5Number;
    private TextView tvContact5Name;
    private TextView tvContact6Number;
    private TextView tvContact6Name;
    private List<Integer> lstBoardUsers = new ArrayList<Integer>();

    private Button buttonPickContact1;
    private LinearLayout llAddContact1;
    private Button buttonPickContact2;
    private LinearLayout llAddContact2;
    private Button buttonPickContact3;
    private LinearLayout llAddContact3;
    private Button buttonPickContact4;
    private LinearLayout llAddContact4;
    private Button buttonPickContact5;
    private LinearLayout llAddContact5;
    private Button buttonPickContact6;
    private LinearLayout llAddContact6;

    private Spinner spContact1Auth;
    private Spinner spContact2Auth;
    private Spinner spContact3Auth;
    private Spinner spContact4Auth;
    private Spinner spContact5Auth;
    private Spinner spContact6Auth;

    ArrayList<User> boardUsers = new ArrayList<User>();
    ArrayList<Spinner> userAuths = new ArrayList<Spinner>();

    private LinearLayout llMaxUsersPerBoardText;

    private RelativeLayout rlCreateBoard;
    private Button btnCreateBoard;

    RelativeLayout.LayoutParams rlCreateBoardParams;

    int boardType;
    String boardName;
    int usersCount = 0;

    private Board boardToCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_board_users);

        Bundle extras = getIntent().getExtras();
        boardType = extras.getInt("BoardType");
        boardName = extras.getString("BoardName");


        User currUser = ((ShareBoardApplication)getApplication()).getCurrUser();

        boardToCreate = new Board(boardName, currUser, Globals.boardTypes.fromOrdinal(boardType));

        initializeComponents();

    }

    private void initializeComponents() {
        TextView tvBoardName = (TextView) findViewById(R.id.tvBoardName);
        tvBoardName.append(boardName + ":");

        tvContact1Number = (TextView) findViewById(R.id.tvContact1Phone);
        tvContact1Name = (TextView) findViewById(R.id.tvContact1Name);
        tvContact2Number = (TextView) findViewById(R.id.tvContact2Phone);
        tvContact2Name = (TextView) findViewById(R.id.tvContact2Name);
        tvContact3Number = (TextView) findViewById(R.id.tvContact3Phone);
        tvContact3Name = (TextView) findViewById(R.id.tvContact3Name);
        tvContact4Number = (TextView) findViewById(R.id.tvContact4Phone);
        tvContact4Name = (TextView) findViewById(R.id.tvContact4Name);
        tvContact5Number = (TextView) findViewById(R.id.tvContact5Phone);
        tvContact5Name = (TextView) findViewById(R.id.tvContact5Name);
        tvContact6Number = (TextView) findViewById(R.id.tvContact6Phone);
        tvContact6Name = (TextView) findViewById(R.id.tvContact6Name);

        llAddContact1 = (LinearLayout) findViewById(R.id.llAddContact1);
        buttonPickContact1 = (Button) findViewById(R.id.addContact1);
        llAddContact2 = (LinearLayout) findViewById(R.id.llAddContact2);
        buttonPickContact2 = (Button) findViewById(R.id.addContact2);
        llAddContact3 = (LinearLayout) findViewById(R.id.llAddContact3);
        buttonPickContact3 = (Button) findViewById(R.id.addContact3);
        llAddContact4 = (LinearLayout) findViewById(R.id.llAddContact4);
        buttonPickContact4 = (Button) findViewById(R.id.addContact4);
        llAddContact5 = (LinearLayout) findViewById(R.id.llAddContact5);
        buttonPickContact5 = (Button) findViewById(R.id.addContact5);
        llAddContact6 = (LinearLayout) findViewById(R.id.llAddContact6);
        buttonPickContact6 = (Button) findViewById(R.id.addContact6);

        spContact1Auth = (Spinner) findViewById(R.id.spContact1Auth);
        spContact2Auth = (Spinner) findViewById(R.id.spContact2Auth);
        spContact3Auth = (Spinner) findViewById(R.id.spContact3Auth);
        spContact4Auth = (Spinner) findViewById(R.id.spContact4Auth);
        spContact5Auth = (Spinner) findViewById(R.id.spContact5Auth);
        spContact6Auth = (Spinner) findViewById(R.id.spContact6Auth);

        llMaxUsersPerBoardText = (LinearLayout) findViewById(R.id.llMaxUsersPerBoardText);

        rlCreateBoard = (RelativeLayout) findViewById(R.id.rlCreateBoard);
        btnCreateBoard = (Button) findViewById(R.id.btnCreateBoard);

        rlCreateBoardParams = (RelativeLayout.LayoutParams) rlCreateBoard.getLayoutParams();

        View.OnClickListener btnPickContactClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        };

        buttonPickContact1.setOnClickListener(btnPickContactClickListener);
        buttonPickContact2.setOnClickListener(btnPickContactClickListener);
        buttonPickContact3.setOnClickListener(btnPickContactClickListener);
        buttonPickContact4.setOnClickListener(btnPickContactClickListener);
        buttonPickContact5.setOnClickListener(btnPickContactClickListener);
        buttonPickContact6.setOnClickListener(btnPickContactClickListener);

        btnCreateBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBoard();
            }
        });
    }

    private void saveBoard() {
        if (checkBoardValidity()) {
            for (int userIndex = 0; userIndex < usersCount; userIndex++) {
                boardToCreate.addUserToBoard(boardUsers.get(userIndex),
                        Globals.userAuthType.fromOrdinal(userAuths.get(userIndex).getSelectedItemPosition()));
            }
            boardToCreate.addUserToBoard(new User(((ShareBoardApplication)getApplication()).getCurrUser().getUserId()),
                    Globals.userAuthType.Admin);
            dialog.show();
            new insertNewBoard(boardToCreate).execute(new ApiConnector());
        }
    }

    private boolean checkBoardValidity() {
        if (usersCount < 0) {
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                usersCount++;
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (number.length() > 10) {
                    number = "0" + number.substring(number.length() - 9);
                }

                User userToAdd = new User(Integer.valueOf(number));
                boardUsers.add(userToAdd);

                revealNextContactButton(name, number);
            }
        }
    }

    private void revealNextContactButton(String name, String number) {
        switch (usersCount) {
            case (1):
            {
                tvContact1Name.setText(name);
                tvContact1Number.setText(number);
                buttonPickContact1.setVisibility(View.INVISIBLE);
                llAddContact1.setVisibility(View.VISIBLE);
                userAuths.add(spContact1Auth);
                buttonPickContact2.setVisibility(View.VISIBLE);
                rlCreateBoardParams.addRule(RelativeLayout.BELOW, R.id.llAddContact2);
                rlCreateBoard.setVisibility(View.VISIBLE);
                break;
            }
            case (2):
            {
                tvContact2Name.setText(name);
                tvContact2Number.setText(number);
                buttonPickContact2.setVisibility(View.INVISIBLE);
                llAddContact2.setVisibility(View.VISIBLE);
                userAuths.add(spContact2Auth);
                buttonPickContact3.setVisibility(View.VISIBLE);
                rlCreateBoardParams.addRule(RelativeLayout.BELOW, R.id.llAddContact3);
                break;
            }
            case (3):
            {
                tvContact3Name.setText(name);
                tvContact3Number.setText(number);
                buttonPickContact3.setVisibility(View.INVISIBLE);
                llAddContact3.setVisibility(View.VISIBLE);
                userAuths.add(spContact3Auth);
                buttonPickContact4.setVisibility(View.VISIBLE);
                rlCreateBoardParams.addRule(RelativeLayout.BELOW, R.id.llAddContact4);
                break;
            }
            case (4):
            {
                tvContact4Name.setText(name);
                tvContact4Number.setText(number);
                buttonPickContact4.setVisibility(View.INVISIBLE);
                llAddContact4.setVisibility(View.VISIBLE);
                userAuths.add(spContact4Auth);
                buttonPickContact5.setVisibility(View.VISIBLE);
                rlCreateBoardParams.addRule(RelativeLayout.BELOW, R.id.llAddContact5);
                break;
            }
            case (5):
            {
                tvContact5Name.setText(name);
                tvContact5Number.setText(number);
                buttonPickContact5.setVisibility(View.INVISIBLE);
                llAddContact5.setVisibility(View.VISIBLE);
                userAuths.add(spContact5Auth);
                buttonPickContact6.setVisibility(View.VISIBLE);
                rlCreateBoardParams.addRule(RelativeLayout.BELOW, R.id.llAddContact6);
                break;
            }
            case (6):
            {
                tvContact6Name.setText(name);
                tvContact6Number.setText(number);
                buttonPickContact6.setVisibility(View.INVISIBLE);
                llAddContact6.setVisibility(View.VISIBLE);
                userAuths.add(spContact6Auth);
                llMaxUsersPerBoardText.setVisibility(View.VISIBLE);
                rlCreateBoardParams.addRule(RelativeLayout.BELOW, R.id.llMaxUsersPerBoardText);
                break;
            }
            default:
            {
                break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        backButtonPressedDialog.setMessage("Are you sure you want to dismiss this board?");
        backButtonPressedDialog.show();
    }

    private class insertNewBoard extends AsyncTask<ApiConnector, Long, Integer> {
        private Board boardToBeAdded;

        protected insertNewBoard(Board BoardToBeAdded) {
            super();
            boardToBeAdded = BoardToBeAdded;
        }

        @Override

        protected Integer doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].InsertBoard(boardToBeAdded);
        }

        @Override
        protected void onPostExecute(Integer boardID) {
            if (boardID > 0) {
                boardToBeAdded.setBoardID(boardID);
                new insertUsersToBoard(boardToBeAdded).execute(new ApiConnector());
            } else {
                dialog.dismiss();
            }
        }
    }

    private class insertUsersToBoard extends AsyncTask<ApiConnector, Long, Integer> {
        private Board boardToBeAltered;

        protected insertUsersToBoard(Board BoardToBeAdded) {
            super();
            boardToBeAltered = BoardToBeAdded;
        }

        @Override

        protected Integer doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].InsertUsersToBoard(boardToBeAltered);
        }

        @Override
        protected void onPostExecute(Integer usersCommited) {
            if (usersCommited == usersCount + 1) {
                Intent myBoardActivity = new Intent(SelectBoardUsersActivity.this, MyBoardActivity.class);
                Bundle BoardDetails = new Bundle();
                BoardDetails.putInt("BoardID", boardToBeAltered.getBoardID());
                myBoardActivity.putExtras(BoardDetails);
                startActivity(myBoardActivity);
                SelectBoardUsersActivity.this.finish();
            }
            dialog.dismiss();
        }
    }
}
