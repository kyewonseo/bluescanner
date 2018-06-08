package net.bluehack.bluescanner;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;


import net.bluehack.bluescanner.fragment.BarcodeReaderFragment;
import net.bluehack.bluescanner.util.UiUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import io.swagger.client.ApiException;
import io.swagger.client.api.FoodApi;
import io.swagger.client.model.GetFoodsResponse;

public class BarcodeActivity extends AppCompatActivity implements BarcodeReaderFragment.BarcodeReaderListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Context context;
    private BarcodeReaderFragment barcodeReader;
    private TextView barcode_number;
    private TextView barcode_status;
    private TextView register_date_barcode;
    private FrameLayout btn_register_barcode;

    private String barcodeNumber;
    private boolean isFirebaseBarcode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        context = this;

        barcodeReader = (BarcodeReaderFragment) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);
        barcode_number = (TextView) findViewById(R.id.barcode_number);
        barcode_status = (TextView) findViewById(R.id.barcode_status);
        register_date_barcode = (TextView) findViewById(R.id.register_date_barcode);
        btn_register_barcode = (FrameLayout) findViewById(R.id.btn_register_barcode);
        btn_register_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getBarcodeNumber() != null) {
                    if ( (!UiUtil.isNotValidKeyString(getBarcodeNumber())) && (!isFirebaseBarcode)) {
                        BarcodeManager.getInstance().writeFirebaseBarcode(getBarcodeNumber());
                        Toast.makeText(context, getString(R.string.confirm_barcode), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, getString(R.string.no_confirm_barcode), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        initUIStatus();

        /**
         * Pausing / resuming barcode reader. This will be useful when you want to
         * do some foreground user interaction while leaving the barcode
         * reader in background
         * */
        // barcodeReader.pauseScanning();
        // barcodeReader.resumeScanning();

        //TODO: API TEST
//        FoodApi apiInstance = new FoodApi();
//        try {
//            Integer offset = 0; // Integer |
//            Integer limit = 56; // Integer |
//            String foodType = "ALL"; // String |
//            String searchField = "barcode"; // String |
//            String searchValue = "8801045931470";
//            GetFoodsResponse result = null;
//            try {
//                apiInstance.addHeader("DS-ApplicationKey","c9598e7b-4aca-49ff-8e26-3ecb37486370");
////                apiInstance.addHeader("DS-AccessToken","rocketview_test");
//                result = apiInstance.getFoods(offset, limit, foodType, searchField, searchValue);
//                Log.e("apiTest:", result.getItems().toString());
//            } catch (TimeoutException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } catch (ApiException e) {
//            System.err.println("Exception when calling FoodApi#getFoods");
//            e.printStackTrace();
//        }
    }

    @Override
    public void onScanned(final Barcode barcode) {
        Log.e(TAG, "onScanned: " + barcode.displayValue);
//        barcodeReader.playBeep();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(getApplicationContext(), "Barcode: " + barcode.displayValue, Toast.LENGTH_SHORT).show();

                setBarcodeNumber(barcode.displayValue);
                barcode_number.setText(barcode.displayValue);

                if (UiUtil.isNotValidKeyString(barcode.displayValue)) {
                    offBarcodeUIStatus();

                } else {
                    BarcodeManager.getInstance().getFirebaseBarcode(barcode.displayValue, new BarcodeManager.BarcodeListener() {
                        @Override
                        public void onResult(String barcodeDate) {

                            if (barcodeDate == null || barcodeDate == "null") {
                                offBarcodeUIStatus();
                                register_date_barcode.setText(R.string.is_not_exist_barcode_date);
                            } else {
                                onBarcodeUIStatus();
                                register_date_barcode.setText(barcodeDate);
                            }
                        }
                    });
                }

            }
        });
    }

    private void initUIStatus() {
        isFirebaseBarcode = false;
        barcode_status.setText(getString(R.string.is_barcode_default));
        barcode_status.setTextColor(ContextCompat.getColor(context, R.color.black));
        register_date_barcode.setText(R.string.is_not_exist_barcode_date);
    }

    private void onBarcodeUIStatus() {
        isFirebaseBarcode = true;
        barcode_status.setText(getString(R.string.is_exist_barcode));
        barcode_status.setTextColor(ContextCompat.getColor(context, R.color.isBarcode));
    }

    private void offBarcodeUIStatus() {
        isFirebaseBarcode = false;
        barcode_status.setText(getString(R.string.is_not_exist_barcode));
        barcode_status.setTextColor(ContextCompat.getColor(context, R.color.isNotBarcode));
    }



    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        Log.e(TAG, "onScannedMultiple: " + barcodes.size());

        String codes = "";
        for (Barcode barcode : barcodes) {
            codes += barcode.displayValue + ", ";
        }

        final String finalCodes = codes;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //TODO: after
//                Toast.makeText(getApplicationContext(), "Barcodes: " + finalCodes, Toast.LENGTH_SHORT).show();
//                barcode_number.setText(finalCodes);
            }
        });
    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getApplicationContext(), "Camera permission denied!", Toast.LENGTH_LONG).show();
        finish();
    }

    public String getBarcodeNumber() {
        return barcodeNumber;
    }

    public void setBarcodeNumber(String barcodeNumber) {
        this.barcodeNumber = barcodeNumber;
    }
}
