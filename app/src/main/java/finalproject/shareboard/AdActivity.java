package finalproject.shareboard;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import finalproject.shareboard.framework.ApiConnector;
import finalproject.shareboard.framework.Globals;
import finalproject.shareboard.model.Ad;


public class AdActivity extends ShareBoardActivity {
    Integer AdID = null;
    Integer BoardID = null;
    Globals.userAuthType userAuth = null;
    String adTitle = null;
    String adDesc = null;
    Globals.adType AdType = null;
    Globals.adPriority AdPriority = null;
    String adFrom = null;
    String adTo = null;

    private TextView tvAdId;

    private Spinner spAdType;
    private EditText etAdTitle;
    private Spinner spAdPriority;
    private EditText etAdDesc;
    private EditText etAdFromDate;
    private EditText etAdToDate;

    private Button btnAdSave;
    private Button btnAdCancel;

    private LinearLayout llGeneralAd;
    private LinearLayout llEventAd;
    private LinearLayout llEditAdButtons;

    AlertDialog adValidDialog;
    AlertDialog deleteAdDialog;

    DatePickerDialog FromdatePicker;
    DatePickerDialog TodatePicker;

    Ad adToAddOrUpdate;

    boolean bIsAtEditMode = false;

    private Calendar calendar;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        Bundle extras = getIntent().getExtras();
        AdID = extras.getInt("AdID");
        adTitle = extras.getString("AdTitle");
        AdType =  Globals.adType.fromOrdinal(extras.getInt("AdType"));
        adDesc = extras.getString("AdDesc");
        AdPriority = Globals.adPriority.fromOrdinal(extras.getInt("AdPriority"));
        adFrom = extras.getString("AdFromDate");
        adTo = extras.getString("AdToDate");
        BoardID = extras.getInt("BoardID");
        userAuth = Globals.userAuthType.fromOrdinal(extras.getInt("UserAuth"));

        invalidateOptionsMenu();

        InitializeComponent();
    }

    private void InitializeComponent() {
        tvAdId = (TextView) findViewById(R.id.tvAdIdText);

        spAdType = (Spinner) findViewById(R.id.spAdTypes);
        etAdTitle = (EditText) findViewById(R.id.etAdTitle);
        spAdPriority = (Spinner) findViewById(R.id.spAdPriority);
        etAdDesc = (EditText) findViewById(R.id.etAdDesc);
        etAdFromDate = (EditText) findViewById(R.id.etAdFromDate);
        etAdToDate = (EditText) findViewById(R.id.etAdToDate);

        btnAdSave = (Button) findViewById(R.id.btnAdSave);
        btnAdCancel = (Button) findViewById(R.id.btnAdCancel);

        llGeneralAd = (LinearLayout) findViewById(R.id.llGeneralAdSection);
        llEventAd = (LinearLayout) findViewById(R.id.llEventAdSection);
        llEditAdButtons = (LinearLayout) findViewById(R.id.llAdEditButtons);

        spAdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == Globals.adType.General.ordinal()) {
                    llGeneralAd.setVisibility(View.VISIBLE);
                    llEventAd.setVisibility(View.INVISIBLE);
                    etAdFromDate.setText("");
                    etAdToDate.setText("");
                } else if (position == Globals.adType.Event.ordinal()){
                    llGeneralAd.setVisibility(View.INVISIBLE);
                    llEventAd.setVisibility(View.VISIBLE);
                    etAdDesc.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener FromDateListener
                = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                etAdFromDate.setText(String.valueOf(day) + "/" + String.valueOf(month + 1)
                        + "/" +String.valueOf(year));
            }
        };

        DatePickerDialog.OnDateSetListener ToDateListener
                = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                etAdToDate.setText(String.valueOf(day) + "/" + String.valueOf(month + 1)
                        + "/" +String.valueOf(year));
            }
        };

        FromdatePicker = new DatePickerDialog(this, FromDateListener, year, month, day);
        TodatePicker = new DatePickerDialog(this, ToDateListener, year, month, day);

        etAdFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FromdatePicker.show();
            }
        });

        etAdToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TodatePicker.show();
            }
        });

        btnAdSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AdID != null && AdID != 0) {
                    insertUpdateAd(true);
                } else {
                    insertUpdateAd(false);
                }
            }
        });

        btnAdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelEditAd();
            }
        });

        if (AdID != null && AdID != 0) {
            tvAdId.setText(getResources().getText(R.string.ad_id_text) + " " + AdID.toString());
            spAdType.setSelection(AdType.ordinal());
            etAdTitle.setText(adTitle);
            spAdPriority.setSelection(AdPriority.ordinal());
            etAdDesc.setText(adDesc);
            etAdFromDate.setText(adFrom);
            etAdToDate.setText(adTo);
        } else {
            setAdForInsertOrUpdate(false);
            tvAdId.setVisibility(View.INVISIBLE);
            llEditAdButtons.setVisibility(View.VISIBLE);
        }

        AlertDialog.Builder adValidDialogBuilder = new AlertDialog.Builder(this);
        adValidDialogBuilder.setMessage("Oops! Something went wrong... Please correct ad details")
                .setCancelable(false)
                .setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        adValidDialog = adValidDialogBuilder.create();

        AlertDialog.Builder deleteAdDialogBuilder = new AlertDialog.Builder(this);
        deleteAdDialogBuilder.setMessage("Are you sure you want to delete this ad?")
                .setCancelable(false)
                .setPositiveButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int id) {
                                dialogInterface.cancel();
                            }
                        })
                .setNegativeButton("Yes", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int id) {
                                dialog.show();
                                new DeleteAd().execute(new ApiConnector());
                            }
                        });

        deleteAdDialog = deleteAdDialogBuilder.create();
    }

    private void cancelEditAd() {
        backButtonPressedDialog.show();
    }

    private void setAdForInsertOrUpdate(boolean isEdit) {
        if (!isEdit) {
            spAdType.setClickable(true);
            spAdPriority.setClickable(true);
        }

        etAdTitle.setEnabled(true);
        etAdDesc.setEnabled(true);
        etAdFromDate.setEnabled(true);
        etAdToDate.setEnabled(true);
        llEditAdButtons.setVisibility(View.VISIBLE);
    }

    private void insertUpdateAd(boolean isEdit) {
        if (checkAdValidity()) {
            Integer UserId = ((ShareBoardApplication)getApplication()).getCurrUser().getUserId();
            Globals.adType AdType = Globals.adType.fromOrdinal(spAdType.getSelectedItemPosition());
            String AdTitle = etAdTitle.getText().toString();
            Globals.adPriority AdPriority = Globals.adPriority.fromOrdinal(spAdPriority.getSelectedItemPosition());
            String AdDesc = etAdDesc.getText().toString();
            String FromDate = etAdFromDate.getText().toString();
            String ToDate = etAdToDate.getText().toString();

            adToAddOrUpdate = new Ad(AdID, BoardID, UserId, AdType, AdTitle, AdPriority, AdDesc, null, FromDate,ToDate, null, UserId);

            if ((AdID == null) || (AdID == 0)) {
                dialog.show();
                new InsertAd().execute(new ApiConnector());
            } else {
                dialog.show();
                new UpdateAd().execute(new ApiConnector());
            }
        } else {
            adValidDialog.show();
        }
    }

    private boolean checkAdValidity() {
        if (etAdTitle.getText().toString().length() == 0) {
            return false;
        }

        if ((spAdType.getSelectedItemPosition() == Globals.adType.General.ordinal()) &&
                etAdDesc.getText().toString().length() == 0) {
            return false;
        } else if ((spAdType.getSelectedItemPosition() == Globals.adType.Event.ordinal() &&
                ((etAdFromDate.getText().toString().length() == 0) || (etAdToDate.getText().toString().length() == 0)))) {
            return false;
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ad, menu);

        if (AdID != null && AdID != 0) {
            if (Globals.userAuthType.Admin.compareTo(userAuth) == 0) {
                if (!bIsAtEditMode) {
                    menu.findItem(R.id.itmEditAd).setVisible(true);
                    menu.findItem(R.id.itmDeleteAd).setVisible(true);
                } else {
                    menu.findItem(R.id.itmEditAd).setVisible(false);
                    menu.findItem(R.id.itmDeleteAd).setVisible(false);
                }
            } else if (Globals.userAuthType.Edit.compareTo(userAuth) == 0) {
                if (!bIsAtEditMode) {
                    menu.findItem(R.id.itmEditAd).setVisible(true);
                } else {
                    menu.findItem(R.id.itmEditAd).setVisible(false);
                }
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.itmEditAd) {
            llEditAdButtons.setVisibility(View.VISIBLE);
            bIsAtEditMode = true;
            setAdForInsertOrUpdate(true);
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.itmDeleteAd) {
            deleteAdDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private class InsertAd extends AsyncTask<ApiConnector, Long, Integer> {
        @Override
        protected Integer doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].InsertAd(adToAddOrUpdate);
        }

        @Override
        protected void onPostExecute(Integer adID) {


            dialog.dismiss();
            finish();
        }
    }

    private class DeleteAd extends AsyncTask<ApiConnector, Long, Integer> {
        @Override
        protected Integer doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].DeleteAd(AdID);
        }

        @Override
        protected void onPostExecute(Integer success) {
            if(success == 1) {
                dialog.dismiss();
                finish();
            }
        }
    }

    private class UpdateAd extends AsyncTask<ApiConnector, Long, Integer> {
        @Override
        protected Integer doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].UpdateAd(adToAddOrUpdate);
        }

        @Override
        protected void onPostExecute(Integer success) {
            if(success == 1) {
                dialog.dismiss();
                finish();
            }
        }
    }
}
