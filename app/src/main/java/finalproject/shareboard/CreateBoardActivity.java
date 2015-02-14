package finalproject.shareboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import finalproject.shareboard.framework.Globals;


public class CreateBoardActivity extends ShareBoardActivity {

    private RadioGroup boardType;
    private RadioButton radFridge, radWood, radBulletin, radWhiteboard;

    private Globals.boardTypes currType = Globals.boardTypes.Fridge;

    private EditText etBoardName;

    private Button btnCancel;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);

        initializeComponents();
    }

    private void initializeComponents() {
        boardType = (RadioGroup) findViewById(R.id.rgBoardType);
        radFridge = (RadioButton) findViewById(R.id.radioFridge);
        radWood = (RadioButton) findViewById(R.id.radioWood);
        radBulletin = (RadioButton) findViewById(R.id.radioBulletin);
        radWhiteboard = (RadioButton) findViewById(R.id.radioWhite);

        etBoardName = (EditText) findViewById(R.id.etBoardName);

        btnCancel = (Button) findViewById(R.id.btnCreateBoardCancel);
        btnNext = (Button) findViewById(R.id.btnCreateBoardNext);

        radFridge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                radFridge.setChecked(true);
                radWood.setChecked(false);
                radBulletin.setChecked(false);
                radWhiteboard.setChecked(false);

                currType = Globals.boardTypes.Fridge;
            }
        });

        radWood.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                radFridge.setChecked(false);
                radWood.setChecked(true);
                radBulletin.setChecked(false);
                radWhiteboard.setChecked(false);

                currType = Globals.boardTypes.Wood;
            }
        });

        radBulletin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                radFridge.setChecked(false);
                radWood.setChecked(false);
                radBulletin.setChecked(true);
                radWhiteboard.setChecked(false);

                currType = Globals.boardTypes.Bulletin;
            }
        });

        radWhiteboard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                radFridge.setChecked(false);
                radWood.setChecked(false);
                radBulletin.setChecked(false);
                radWhiteboard.setChecked(true);

                currType = Globals.boardTypes.WhiteBoard;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenSelectBoardUsersActivity();
                finish();
            }
        });
    }

    private void OpenSelectBoardUsersActivity() {
        Intent selectBoardUsers = new Intent(this, SelectBoardUsersActivity.class);
        Bundle BoardDetails = new Bundle();
        BoardDetails.putInt("BoardType", currType.ordinal());
        BoardDetails.putString("BoardName", etBoardName.getText().toString());
        selectBoardUsers.putExtras(BoardDetails);
        startActivity(selectBoardUsers);
    }

    @Override
    public void onBackPressed() {
        backButtonPressedDialog.setMessage("Are you sure you want to dismiss this board?");
        backButtonPressedDialog.show();
    }
}
