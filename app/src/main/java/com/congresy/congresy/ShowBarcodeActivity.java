package com.congresy.congresy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.w3c.dom.Text;

public class ShowBarcodeActivity extends BaseActivity {

    ImageView image;

    TextView nameActor;
    TextView date;
    TextView price;
    TextView countryAndCity;
    TextView address;
    TextView details;
    TextView conferenceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ticket");
        loadDrawer(R.layout.activity_show_barcode);

        Intent myIntent = getIntent();

        image = findViewById(R.id.image);
        nameActor = findViewById(R.id.nameActor);
        date = findViewById(R.id.dateAndTime);
        price = findViewById(R.id.price);
        countryAndCity = findViewById(R.id.countryAndCity);
        address = findViewById(R.id.address);
        details = findViewById(R.id.details);
        conferenceName = findViewById(R.id.conferenceName);

        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);

        String nameActor_ = sp.getString("Name", "not found");
        String date_ = myIntent.getStringExtra("date");
        String price_ = myIntent.getStringExtra("price") + " euros";
        String countryAndCity_ = myIntent.getStringExtra("countryAndCity");
        String address_ = myIntent.getStringExtra("address");
        String details_ = myIntent.getStringExtra("details");
        String conferenceName_ = myIntent.getStringExtra("nameConference");

        nameActor.setText(nameActor_);
        date.setText(date_);
        price.setText(price_);
        countryAndCity.setText(countryAndCity_);
        address.setText(address_);
        details.setText(details_);
        conferenceName.setText(conferenceName_);

        String text = getIntent().getExtras().get("idConference").toString() + sp.getString("Id", "not found");
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {

            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
