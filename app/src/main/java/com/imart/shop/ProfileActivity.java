package com.imart.shop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.imart.shop.app.myapp;
import com.imart.shop.util.Constant;
import com.imart.shop.util.GPSTracker;
import com.imart.shop.util.SessionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private ProgressDialog loading;
    EditText edtnama, edtEmail, edtPhone, edtAlamat;
    String nama, email, phone, alamat, sex, Message;//variable editext
    String userid, usernama, useremail, useralamat, nama_jalan,userphone,usersex,userakses,userpoin,lat,long_lat,fcmid;
    String URL_PROFIL;
    double latitude, longitude;

    Button btnSave;
    HashMap<String, String> user;
    SessionManager session;
    RadioGroup RdGrup;
    RadioButton RdSex,Rdfemale,RdMale;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        getSupportActionBar().setTitle("My Profile");
        session = new SessionManager(getApplicationContext());
        user = session.getUserDetails();
        userid = user.get(SessionManager.KEY_PASSENGER_ID);
        usernama = user.get(SessionManager.KEY_NAME);
        useremail = user.get(SessionManager.KEY_EMAIL);
        userpoin = user.get(SessionManager.KEY_POIN);
        useralamat = user.get(SessionManager.KEY_ALAMAT);
        userphone = user.get(SessionManager.KEY_TELP);
        usersex =user.get(SessionManager.KEY_SEX);
        userakses =user.get(SessionManager.KEY_AKSES);
        fcmid = user.get(SessionManager.KEY_FCM);
        edtnama = (EditText) findViewById(R.id.edtnama);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtAlamat = (EditText) findViewById(R.id.edtAlamat);
        Rdfemale =(RadioButton)findViewById(R.id.rdFemale) ;
        RdMale =(RadioButton)findViewById(R.id.rdMale);
        RdGrup = (RadioGroup) findViewById(R.id.rdGroup);
        setProfileDisplay();

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        edtAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getAlamat = edtAlamat.getText().toString();
                if(getAlamat.matches("")){
                    AmbilAlamat();
                }else{
                    edtAlamat.setFocusableInTouchMode(true);
                    edtAlamat.setFocusable(true);
                }
            }
        });
    }

    private void AmbilAlamat() {
        loading = ProgressDialog.show(this, "Alamat", "Sedang mengambil alamat", false, false);
        GPSTracker gpstracker = new GPSTracker(this);
        if (gpstracker.canGetLocation()) {
            latitude = gpstracker.getLatitude();
            longitude = gpstracker.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder();
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    nama_jalan = strReturnedAddress.toString();
                } else {
                    Toast.makeText(ProfileActivity.this, "Alamat tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(ProfileActivity.this, "Terjadi kesalahan saat mengambil alamat", Toast.LENGTH_SHORT).show();
            }
            edtAlamat.setText(nama_jalan);
            loading.dismiss();
        } else {
            loading.dismiss();
            gpstracker.showSettingsAlert();
        }
    }

    void setProfileDisplay() {
        edtnama.setText(usernama);
        edtEmail.setText(useremail);
        edtAlamat.setText(useralamat);
        edtPhone.setText(userphone);
        if(usersex.equals("female")){
            Rdfemale.setChecked(true);
            RdMale.setChecked(false);
        }else{
            Rdfemale.setChecked(false);
            RdMale.setChecked(true);
        }
        Log.e(TAG,"ini latitude dari pref"+user.get(SessionManager.KEY_LAT));
    }

    private void validate() {
        lat = Double.toString(latitude);
        long_lat = Double.toString(longitude);
        nama = edtnama.getText().toString();
        email = edtEmail.getText().toString();
        phone = edtPhone.getText().toString();
        alamat = edtAlamat.getText().toString();
        int selec = RdGrup.getCheckedRadioButtonId();
        RdSex = (RadioButton) findViewById(selec);
        sex = RdSex.getText().toString();
        if (nama.length() == 0) {
            edtnama.setError("Nama harus diisi");
            edtnama.requestFocus();
        } else if (email.length() == 0) {
            edtEmail.setError("Email harus diisi");
            edtEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email tidak valid");
            edtEmail.requestFocus();
        } else if (phone.length() == 0) {
            edtPhone.setError("Nomor Telepon harus diisi");
            edtPhone.requestFocus();
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            edtPhone.setError("Nomor Telepon tidak valid");
            edtPhone.requestFocus();
        } else if (alamat.equals("")) {
            edtAlamat.setError("Alamat harus diisi");
            edtAlamat.requestFocus();
        } else {
            URL_PROFIL = Constant.URLADMIN + "api/save_profile.php";
            loading = ProgressDialog.show(this, "Profil", "Sedang menyimpan profil", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PROFIL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            loading.dismiss();
                            session.updateProfile(nama,email,phone,sex,alamat,lat,long_lat,fcmid,userpoin);
                            Intent intent = getIntent();
                            finish();
                            overridePendingTransition(0,0);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            Toast.makeText(ProfileActivity.this,s,Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog
                            loading.dismiss();
                            Toast.makeText(ProfileActivity.this,"Gagal Update,silakan ulangi",Toast.LENGTH_SHORT).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //Converting Bitmap to String
                    //Creating parameters
                    Map<String,String> params = new Hashtable<String, String>();
                    //Adding parameters
                    params.put("user_id", userid);
                    params.put("alamat", alamat);
                    params.put("telp", phone);
                    params.put("sex", sex);
                    params.put("nama", nama);
                    params.put("email", email);
                    params.put("lat", lat);
                    params.put("long", long_lat);
                    params.put("key", Constant.KEY);

                    //returning parameters
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            myapp.getInstance().addToRequestQueue(stringRequest);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // app icon in action bar clicked; go home
            this.finish();
            overridePendingTransition(R.anim.open_main, R.anim.close_next);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
}
