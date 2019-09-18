package com.tuto.xmlparsertest;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tuto.xmlparsertest.utils.CipherUtil;
import com.tuto.xmlparsertest.utils.FileUtil;
import com.tuto.xmlparsertest.utils.XmlUtil;

import java.io.File;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private final int REQ_CD_INIT_VERSION_CODE = 0;
    private final int REQ_CD_UPDATE_VERSION_CODE = 1;

    private final String XML_FILE_TAG = "test";
    private final String XML_FILE_PATH = "/test/test.xml";

    private TextView tvVersion;

    private EditText etVersion;

    private Button btnVersion;

    private File xmlFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        xmlFile = new File(getExternalFilesDir(null) + XML_FILE_PATH);

        tvVersion = findViewById(R.id.tv_version);

        etVersion = findViewById(R.id.et_version);

        btnVersion = findViewById(R.id.btn_version);
        btnVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkStoragePermission()) {
                    updateVersionCode();
                } else {
                    requestStoragePermission(REQ_CD_UPDATE_VERSION_CODE);
                }
            }
        });
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(checkStoragePermission()) {
            decryptXmlFile();
            loadVersionCode();
        } else {
            requestStoragePermission(REQ_CD_INIT_VERSION_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        if(checkStoragePermission()) {
            encryptXmlFile();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(String permission : permissions) {
            if(permission.equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Storage permission is granted", Toast.LENGTH_SHORT).show();

                    switch (requestCode) {
                        case REQ_CD_INIT_VERSION_CODE:
                            decryptXmlFile();
                            loadVersionCode();
                            break;
                        case REQ_CD_UPDATE_VERSION_CODE:
                            decryptXmlFile();
                            updateVersionCode();
                            break;
                        default:
                            break;
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Storage permission is denied", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    private boolean checkStoragePermission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext() ,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext() , android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestStoragePermission(int requestCode) {
        ActivityCompat.requestPermissions( MainActivity.this , new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE , android.Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
    }

    private void updateVersionCode() {
        if (etVersion.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please input version code", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            com.tuto.xmlparsertest.utils.XmlUtil.resetVersionCode(xmlFile.getAbsolutePath(), XML_FILE_TAG, etVersion.getText().toString());
            loadVersionCode();
            Toast.makeText(getApplicationContext(), "The version code is edited", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadVersionCode() {
        HashMap<String, Object> versionMap = XmlUtil.parseVersionCode(xmlFile.getAbsolutePath(), XML_FILE_TAG);
        String versionCode = (String)versionMap.get("version");

        tvVersion.setText("The version code of this xml file is " + versionCode);
    }

    private void encryptXmlFile() {
        try {
            byte[] byteForXmlFile = FileUtil.readFileToByte(xmlFile.getAbsolutePath());
            byteForXmlFile = CipherUtil.encryptAES(byteForXmlFile);
            FileUtil.writeFile(xmlFile.getAbsolutePath(), byteForXmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decryptXmlFile() {
        try {
            byte[] byteForXmlFile = FileUtil.readFileToByte(xmlFile.getAbsolutePath());
            byteForXmlFile = CipherUtil.decryptAESToByte(byteForXmlFile);
            FileUtil.writeFile(xmlFile.getAbsolutePath(), byteForXmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
