package net.bluehack.bluescanner;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import net.bluehack.bluescanner.firebase.FirebaseDatabaseHelper;
import net.bluehack.bluescanner.model.Barcode;
import net.bluehack.bluescanner.util.UiUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BarcodeManager {

    private static final BarcodeManager INSTANCE = new BarcodeManager();
    private static final String BARCODE_TEST = "barcode_test";
    public static BarcodeManager getInstance() {
        return INSTANCE;
    }

//    private Barcode barcode;
//    private Map<String, String> barcodeList = new HashMap<>();

    private BarcodeManager(){}

    public interface BarcodeListener {
        void onResult(String barcodeDate);
    }

    public void getFirebaseBarcode(final String barcodeNum, final BarcodeListener barcodeListener) {

        FirebaseDatabaseHelper.getInstance().getDatabaseBlocks()
                .child(BARCODE_TEST)
                .child(barcodeNum)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Barcode barcode = dataSnapshot.getValue(Barcode.class);
//                        Log.e("barcodeTest:", String.valueOf(barcode.date));
                        String date = String.valueOf(dataSnapshot.getValue());
                        barcodeListener.onResult(date);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void writeFirebaseBarcode(final String barcodeNum) {

        String date = UiUtil.getDateFormat();

        FirebaseDatabaseHelper.getInstance().getDatabaseBlocks()
                .child(BARCODE_TEST)
                .child(barcodeNum)
                .setValue(date);
    }

}
