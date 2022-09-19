package com.example.mrplabelgeneration;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.aem.api.AEMWifiPrinter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity  implements IAemCardScanner, IAemScrybe {
    //   BT 9000
    //   USB 8500

    int effectivePrintWidth = 48;
    AEMScrybeDevice m_AemScrybeDevice;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;
    CardReader m_cardReader = null;
    AEMPrinter m_AemPrinter = null;
    ArrayList<String> printerList;
    String creditData;
    ProgressDialog m_WaitDialogue;
    CardReader.CARD_TRACK cardTrackType;
    int glbPrinterWidth;
    int numChars = glbPrinterWidth;
    int labelPrinterWidth;
    EditText  edittext_printerSeries;
    private PrintWriter printOut;
    private Socket socketConnection;
    private String txtIP = "";
    Spinner spinner;
    String encoding = "US-ASCII";
    EditText edtName, edtPin;
    String data;
    private String strShow = "";
    Bitmap mBitmap;
    String[] responseArray = new String[1];
    char[] end = new char[]{0x1B, 0x7E, 0x42, 0x50, 0x7C, 0x47, 0x45, 0x54, 0x7C, 0x45, 0x4E, 0x44, 0x5F, 0x50, 0x52, 0x5E};
    char[] start = new char[]{0x1B, 0x7E, 0x42, 0x50, 0x7C, 0x47, 0x45, 0x54, 0x7C, 0x53, 0x54, 0x41, 0x52, 0x54, 0x50, 0x5E};
    char[] labelFullCut = new char[]{0x1D, 0x56, 0x00};
    char[] labelHalfCut = new char[]{0x1D, 0x56, 0x01};
    char[] labelBarCodeCut = new char[]{0x1D, 0x48, 0x00};
    public Handler mHandler;
    String text;

    public static final byte FONT_001 = 0X03;
    public static final byte FONT_002 = 0X14;
    public static final byte FONT_003 = 0X16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printerList = new ArrayList<String>();
        creditData = new String();
        m_AemScrybeDevice = new AEMScrybeDevice(this);
        edittext_printerSeries=(EditText) findViewById(R.id.edittext_printerSeries);
        spinner=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Printer_List, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                if(position==0) {
                    glbPrinterWidth=0;
                    onSetPrinterType(view);
                } else if (position==1){
                    glbPrinterWidth=1;
                    onSetPrinterType(view);
                } else if (position==2){
                    glbPrinterWidth=2;
                    onSetPrinterType(view);
                } else if (position==3){
                    glbPrinterWidth=3;
                    onSetPrinterType(view);
                } else if (position==4){
                    glbPrinterWidth=4;
                    onSetPrinterType(view);
                }else if (position==5){
                    glbPrinterWidth=5;
                    onSetPrinterType(view);
                }else if (position==6){
                    glbPrinterWidth=6;
                    onSetPrinterType(view);
                }else if (position==7){
                    glbPrinterWidth=7;
                    onSetPrinterType(view);
                }else if (position==8){
                    glbPrinterWidth=8;
                    onSetPrinterType(view);
                } else if (position==9){
                    glbPrinterWidth=9;
                    onSetPrinterType(view);
                } else if (position==10){
                    glbPrinterWidth=10;
                    onSetPrinterType(view);
                }

                else if (position==11){
                    glbPrinterWidth=11;
                    onSetPrinterType(view);
                }

                else if (position==12){
                    glbPrinterWidth=12;
                    onSetPrinterType(view);
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        Button discoverButton = (Button) findViewById(R.id.pairing);
        registerForContextMenu(discoverButton);
    }

    private void onSetPrinterType(View view) {
        if(glbPrinterWidth == 0) {
            glbPrinterWidth = 0;
        }else if (glbPrinterWidth==1){
            glbPrinterWidth = 1;
        }else if (glbPrinterWidth==2){
            glbPrinterWidth = 2;
        }else if (glbPrinterWidth==3){
            glbPrinterWidth = 3;
        }else if (glbPrinterWidth==4){
            glbPrinterWidth =4;
        }else if (glbPrinterWidth==5){
            glbPrinterWidth =5;
        }else if (glbPrinterWidth==6){
            glbPrinterWidth =6;
        }else if (glbPrinterWidth==7){
            glbPrinterWidth =7;
        }else if (glbPrinterWidth==8){
            glbPrinterWidth =8;
        }else if (glbPrinterWidth==9){
            glbPrinterWidth =9;
        }else if (glbPrinterWidth==10){
            glbPrinterWidth =10;
        }else if (glbPrinterWidth==11){
            glbPrinterWidth =11;
        }else if (glbPrinterWidth==12){
            glbPrinterWidth =12;
        }
    }

    public void onPrintBill(View view) throws IOException {
        text = edittext_printerSeries.getText().toString();
        onPrintBillBluetooth(glbPrinterWidth,text);
    }

    private void onPrintBillBluetooth(int glbPrinterWidth, String text) throws IOException {
        if (m_AemPrinter == null) {
            Toast.makeText(MainActivity.this, "Printer not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        if (glbPrinterWidth==0){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model: SAMPANN ECONOMY\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Battery : 1100mAh        MRP: Rs.5000/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();




        } else if (glbPrinterWidth==1){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model : SAMPANN ECONOMY\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Battery : 1500mAh           MRP: Rs.6000/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();



        }else if (glbPrinterWidth==2){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model : SAMPANN REGULAR\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Battery : 1200mAh           MRP: Rs.5500/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();



        }else if (glbPrinterWidth==3){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model : SAMPANN REGULAR\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Battery : 2600mAh             MRP: Rs.6500/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();



        }else if (glbPrinterWidth==4){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model : SAMPANN PREMIUM\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Battery : 1100mAh            MRP: Rs.7000/-\n";
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();



        }else if (glbPrinterWidth==5){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model : SAMPANN PREMIUM\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Battery : 1500mAh            MRP: Rs.8000/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();

        }else if (glbPrinterWidth==6){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model : PRAGATI ECONOMY\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Battery : 2000mAh              MRP: Rs.7500/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();


        }else if (glbPrinterWidth==7){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model :PRAGATI REGULAR\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Battery : 2600mAh               MRP: Rs.8500/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();

        }

         else if (glbPrinterWidth==8){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "Model :UTKARSH REGULAR          MRP: Rs.8500/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Communication : BT/USB\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();

        }else if (glbPrinterWidth==9){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model :UTKARSH ULTRA PREMIUM     MRP: Rs.10000/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Communication : BT/USB/LAN/ETHERNET\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();


        }
         else if (glbPrinterWidth==10){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model :VRIDDHI\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Battery : 1100mAh           MRP: Rs.8500/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();


        }

        else if (glbPrinterWidth==11){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model :BPDL24-DT                MRP: Rs.10000/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data ="Communication : BT/USB/LAN\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();


        } else if (glbPrinterWidth==12){
            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"Model :UTKARSH PREMIUM          MRP: Rs.9500/-\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = 	"MRP: Rs.9500/-\n";
            m_AemPrinter.print(data);
            data ="Communication : BT/USB/LAN\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            m_AemPrinter.printBarcodeThreeInch(text, AEMPrinter.BARCODE_TYPE.CODE39, AEMPrinter.BARCODE_HEIGHT.DOUBLEDENSITY_FULLHEIGHT);
            data ="Support No +918447141431\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            data ="Email :- support@bluprints.in\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.print(data);
            data = "www.bluprints.in                MADE IN INDIA\n";
            m_AemPrinter.POS_Font_bold_ThreeInch();
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
        }
    }

    private void RasterBT(Bitmap resizedBitmap) throws IOException {
     //   m_AemPrinter.POS_FontThreeInchRIGHT();
        m_AemPrinter.POS_FontThreeInchRIGHT();
        m_AemPrinter.printImageThreeInch(resizedBitmap);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Printer to connect");
        for (int i = 0; i < printerList.size(); i++) {
            menu.add(0, v.getId(), 0, printerList.get(i));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        String printerName = item.getTitle().toString();
        try {
            m_AemScrybeDevice.connectToPrinter(printerName);
            m_cardReader = m_AemScrybeDevice.getCardReader(this);
            m_AemPrinter = m_AemScrybeDevice.getAemPrinter();
            Toast.makeText(MainActivity.this, "Connected with " + printerName, Toast.LENGTH_SHORT).show();

            //  m_cardReader.readMSR();
        } catch (IOException e) {
            if (e.getMessage().contains("Service discovery failed")) {
                Toast.makeText(MainActivity.this, "Not Connected\n" + printerName + " is unreachable or off otherwise it is connected with other device", Toast.LENGTH_SHORT).show();
            } else if (e.getMessage().contains("Device or resource busy")) {
                Toast.makeText(MainActivity.this, "the device is already connected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    public void onShowPairedPrinters(View view) {
        String p = m_AemScrybeDevice.pairPrinter("BTprinter0314");
        // showAlert(p);
        printerList = m_AemScrybeDevice.getPairedPrinters();
        if (printerList.size() > 0)
            openContextMenu(view);
     //   else
     //       showAlert("No Paired Printers found");
    }

    @Override
    public void onScanMSR(String buffer, CardReader.CARD_TRACK cardtrack) {

    }

    @Override
    public void onScanDLCard(String buffer) {

    }

    @Override
    public void onScanRCCard(String buffer) {

    }

    @Override
    public void onScanRFD(String buffer) {

    }

    @Override
    public void onScanPacket(String buffer) {

    }

    @Override
    public void onDiscoveryComplete(ArrayList<String> aemPrinterList) {

    }



         //   if(numChars == 48){
/*
        try
        {
               */
/* m_AemPrinter.setFontType(ESCAPE_SEQ); //esc
                m_AemPrinter.setFontType(ESCAPE_a); //a
                m_AemPrinter.setFontType(ESCAPE_CENTER); //0x01
                m_AemPrinter.setFontType(ESCAPE_SEQ); //esc
                m_AemPrinter.setFontType(ESCAPE_EXCL); //!
                m_AemPrinter.setFontType(ESCAPE_DOUBLE_HEIGHT); //esc*//*



             */
/*   InputStream is = getAssets().open("bluprintlogo1.jpg");
                Bitmap inputBitmap = BitmapFactory.decodeStream(is);
                Bitmap resizedBitmap = null;
                resizedBitmap = Bitmap.createScaledBitmap(inputBitmap, 400, 400, false);
                RasterBT(resizedBitmap);
                m_AemPrinter.setCarriageReturn();*//*



            data = "BluPrints Smart Thermal Printer Series\n";
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setLineFeed(1);

            data = 	"Model Name:\n";
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);

            data = 	"MRP:\n";
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();


            data = 	"BIS: R93002968 \n";
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);

            data = 	"Customer Support: +918447141431 support@bluprins.in \n";
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);

            data = 	"www.bluprints.in            Made in India\n";
            m_AemPrinter.POS_FontThreeInchLEFT();
            m_AemPrinter.print(data);
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();
            m_AemPrinter.setCarriageReturn();


            */
/*    data = 	"support@bluprins.in\n";
                m_AemPrinter.POS_FontThreeInchRIGHT();
                m_AemPrinter.print(data);*//*





            //   onPrintBarcodeBT()

        }
        catch (IOException e) {
            if (e.getMessage().contains("socket closed"))
                Toast.makeText(MainActivity.this,"Printer not connected", Toast.LENGTH_SHORT).show();
        }
*/
}
