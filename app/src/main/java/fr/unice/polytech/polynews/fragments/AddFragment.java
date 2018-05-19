package fr.unice.polytech.polynews.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.InputStream;
import java.util.Date;

import fr.unice.polytech.polynews.Database;
import fr.unice.polytech.polynews.R;
import fr.unice.polytech.polynews.ViewAndAddActivity;
import fr.unice.polytech.polynews.models.Mishap;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class AddFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, AdapterView.OnItemSelectedListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private double latitude;
    private double longitude;
    private boolean putLocation;
    private ImageView cameraView;
    static private String email;
    private String urgency;
    private String category;

    public AddFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AddFragment newInstance(int sectionNumber, String email) {
        AddFragment.email = email;
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        Log.i("PlaceHolder", "message");
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.add, container, false);
        TextView textTitle = rootView.findViewById(R.id.textTitle);
        TextView textDescription = rootView.findViewById(R.id.textDescription);
        TextView textCategory = rootView.findViewById(R.id.textCategory);
        TextView textPlace = rootView.findViewById(R.id.textPlace);
        TextView textPhoneNb = rootView.findViewById(R.id.textPhoneNb);
        TextView textMandatory = rootView.findViewById(R.id.textMandatory);
        textTitle.setText(R.string.add_title);
        textDescription.setText(R.string.add_description);
        textCategory.setText(R.string.add_category);
        textPlace.setText(R.string.add_place);
        textPhoneNb.setText(R.string.add_phone_number);
        textMandatory.setText(R.string.add_mandatory);

        Spinner editCategory = rootView.findViewById(R.id.editCategory);
        String [] categories = new String[] {"Autre", "Manque", "Casse", "Dysfonctionnement", "Propreté"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        editCategory.setAdapter(dataAdapter);
        editCategory.setOnItemSelectedListener(this);

        final CheckBox addLocation = rootView.findViewById(R.id.addLocation);
        addLocation.setText(R.string.add_location_checkbox);
        addLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                putLocation = b;
            }
        });

        urgency = "Low";
        final RadioButton low = rootView.findViewById(R.id.low);
        RadioButton medium = rootView.findViewById(R.id.medium);
        RadioButton high = rootView.findViewById(R.id.high);
        low.setText(R.string.radio_low);
        medium.setText(R.string.radio_medium);
        high.setText(R.string.radio_high);
        final RadioGroup urgencyRadioGroup = rootView.findViewById(R.id.urgency);
        urgencyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.low:
                        urgency = "Low";
                        break;
                    case R.id.medium:
                        urgency = "Medium";
                        break;
                    case R.id.high:
                        urgency = "High";
                        break;
                }
            }
        });

        //TODO user = ...
        //user = new User(0, "test@gmail.com", "bon", "jean", "06", "mdp");

        Button buttonAdd = rootView.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(AddFragment.this);
        ImageButton imageView = rootView.findViewById(R.id.image1);
        imageView.setOnClickListener(AddFragment.this);
        imageView = rootView.findViewById(R.id.image2);
        imageView.setOnClickListener(AddFragment.this);
        imageView = rootView.findViewById(R.id.image3);
        imageView.setOnClickListener(AddFragment.this);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean cameraViewUp = false;

        if (data == null) return;
        try {
            InputStream imageStream = getContext().getContentResolver().openInputStream(data.getData());
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            cameraView.setImageBitmap(selectedImage);
            cameraViewUp = true;
        } catch (Exception ignored) {
        }
        if (!cameraViewUp && data.getExtras() != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            cameraView.setImageBitmap(bitmap);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        category = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        category = "Autre";
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buttonAdd:
                onClickAdd(view);
                break;

            case R.id.image1:
                cameraView = rootView.findViewById(R.id.image1);
                onClickCamera();
                break;
            case R.id.image2:
                cameraView = rootView.findViewById(R.id.image2);
                onClickCamera();
                break;
            case R.id.image3:
                cameraView = rootView.findViewById(R.id.image3);
                onClickCamera();
                break;
        }
    }

    private void onClickAdd(View view) {
        EditText editTitle = rootView.findViewById(R.id.editTitle);
        String title = editTitle.getText().toString();
        EditText editPlace = rootView.findViewById(R.id.editPlace);
        String place = editPlace.getText().toString();
        EditText editDescription = rootView.findViewById(R.id.editDescription);
        String description = editDescription.getText().toString();
        EditText editPhone = rootView.findViewById(R.id.editPhoneNb);
        String phone = editPhone.getText().toString();

        double lati = 0, longi = 0;
        if (putLocation) {
            lati = latitude;
            longi = longitude;
        }

        Database database = new Database(getContext());
        Mishap mishap = new Mishap(0, title, category, description, lati, longi, urgency,
                email, "TO DO", new Date().toString(), phone, place);

        long res = database.addMishap(mishap);
        if (res != -1) {
            Intent intent = new Intent(getContext(), ViewAndAddActivity.class);
            intent.putExtra("email", email);
            startActivityForResult(intent, 0);
            getActivity().finish(); //Si on appuie sur la touche retour on revient sur la connexion
        }

    }

    private void onClickCamera() {
        Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intentGalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooser = new Intent(Intent.createChooser(intentGalery, "Open with"));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentTakePhoto});
        startActivityForResult(chooser,0);

        //startActivityForResult(intentTakePhoto, 0);
        //startActivityForResult(intentGalery, 1);
    }

    @Override
    public void onConnected(Bundle bundle) {
        TextView textLocation = rootView.findViewById(R.id.location);
        textLocation.setText(R.string.add_location_cantfind);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; //Not enough permissions
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            textLocation.setText(getString(R.string.add_location_now, latitude, longitude));
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), 9000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            TextView textLocation = rootView.findViewById(R.id.location);
            mLocation = location;
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
            textLocation.setText(getString(R.string.add_location_now, latitude, longitude));
        }
    }
}