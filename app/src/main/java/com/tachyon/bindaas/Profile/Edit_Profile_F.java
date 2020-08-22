package com.tachyon.bindaas.Profile;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.gson.Gson;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.API_CallBack;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tachyon.bindaas.helper.CommonUtils;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.tachyon.bindaas.Main_Menu.MainMenuFragment.hasPermissions;


/**
 * A simple {@link Fragment} subclass.
 */
public class Edit_Profile_F extends RootFragment implements View.OnClickListener {

    View view;
    Context context;

    public Edit_Profile_F() {

    }

    Fragment_Callback fragment_callback;

    public Edit_Profile_F(Fragment_Callback fragment_callback) {
        this.fragment_callback = fragment_callback;
    }

    ImageView profile_image;
    EditText username_edit, firstname_edit, lastname_edit, user_bio_edit;

    RadioButton male_btn, female_btn, others_btn;
    RadioGroup genderGroup;

    private static final String TAG = "Edit_Profile_F";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        context = getContext();
        try {

            view.findViewById(R.id.Goback).setOnClickListener(this);
            view.findViewById(R.id.save_btn).setOnClickListener(this);
            view.findViewById(R.id.upload_pic_btn).setOnClickListener(this);


            username_edit = view.findViewById(R.id.username_edit);
            profile_image = view.findViewById(R.id.profile_image);
            firstname_edit = view.findViewById(R.id.firstname_edit);
            lastname_edit = view.findViewById(R.id.lastname_edit);
            user_bio_edit = view.findViewById(R.id.user_bio_edit);


            username_edit.setText(Variables.sharedPreferences.getString(Variables.u_name, ""));
            firstname_edit.setText(Variables.sharedPreferences.getString(Variables.f_name, ""));
            lastname_edit.setText(Variables.sharedPreferences.getString(Variables.l_name, ""));

            String user_pic = Variables.sharedPreferences.getString(Variables.u_pic, "");
            if (!user_pic.isEmpty())
                Picasso.with(context)
                        .load(user_pic)
                        .placeholder(R.drawable.profile_image_placeholder)
                        .resize(200, 200)
                        .centerCrop()
                        .into(profile_image);


            male_btn = view.findViewById(R.id.male_btn);
            female_btn = view.findViewById(R.id.female_btn);
            others_btn = view.findViewById(R.id.others_btn);
            genderGroup = view.findViewById(R.id.genderGroup);


            Call_Api_For_User_Details();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return view;
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.Goback:

                    getActivity().onBackPressed();
                    break;

                case R.id.save_btn:
                    if (Check_Validation()) {

                        Call_Api_For_Edit_profile();
                    }
                    break;

                case R.id.upload_pic_btn:
                    selectImage();
                    break;
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    // this method will show the dialog of selete the either take a picture form camera or pick the image from gallary
    private void selectImage() {
        try {
            final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

            builder.setTitle("Add Photo!");

            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int item) {

                    if (options[item].equals("Take Photo")) {
                        if(Functions.check_permissions(getActivity()))
                            openCameraIntent();

                    } else if (options[item].equals("Choose from Gallery")) {

                        if(Functions.check_permissions(getActivity())) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 2);
                        }
                    } else if (options[item].equals("Cancel")) {

                        dialog.dismiss();

                    }

                }

            });

            builder.show();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    // below three method is related with taking the picture from camera
    private void openCameraIntent() {
        try {
            Intent pictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                //Create a file to store the image
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(context.getApplicationContext(), getActivity().getPackageName() + ".fileprovider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(pictureIntent, 1);
                }
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public String getPath(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {

                if (requestCode == 1) {
                    Matrix matrix = new Matrix();
                    try {
                        ExifInterface exif = new ExifInterface(imageFilePath);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));

                    beginCrop(selectedImage);

                } else if (requestCode == 2) {
                    Uri selectedImage = data.getData();
                    beginCrop(selectedImage);

                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    handleCrop(result.getUri());
                }

            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    // this will check the validations like none of the field can be the empty
    public boolean Check_Validation() {

        String uname = username_edit.getText().toString();
        String firstname = firstname_edit.getText().toString();
        String lastname = lastname_edit.getText().toString();

        if (TextUtils.isEmpty(uname) || uname.length() < 2) {
            Toast.makeText(context, "Please enter correct username", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(firstname)) {
            Toast.makeText(context, "Please enter first name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(lastname)) {
            Toast.makeText(context, "Please enter last name", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    String image_bas64;

    private void beginCrop(Uri source) {

        CropImage.activity(source).start(getActivity());


    }

    private void handleCrop(Uri userimageuri) {

        InputStream imageStream = null;
        try {
            imageStream = getActivity().getContentResolver().openInputStream(userimageuri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

        String path = userimageuri.getPath();
        Matrix matrix = new Matrix();
        android.media.ExifInterface exif = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                exif = new android.media.ExifInterface(path);
                int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        image_bas64 = Functions.Bitmap_to_base64(getActivity(), rotatedBitmap);
        Log.d(TAG, "handleCrop: ");
        //Call_Api_For_image(userimageuri.getPath());
        uploadImageToFirebase(userimageuri);
    }

    byte[] image_byte_array;

    public void Save_Image() {
        try {
            Functions.Show_loader(context, false, false);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            String key = reference.push().getKey();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference filelocation = storageReference.child("User_image")
                    .child(key + ".jpg");

            filelocation.putBytes(image_byte_array).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        filelocation.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "onSuccess: " + uri.toString());
//                                Call_Api_For_image(uri.toString());
                                uploadImageToFirebase(uri);
                            }
                        });
                    } else {
                        Functions.cancel_loader();
                    }
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }

    public void uploadImageToFirebase(Uri imageFilePath){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore
                    .Images
                    .Media
                    .getBitmap(
                            context.getContentResolver(),
                            imageFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        profile_image.setImageBitmap(bitmap);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("User_image/"+ UUID.randomUUID().toString());

        Log.d(TAG, "uploadImageToFirebase: called");
        storage.putFile(imageFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(context, "Upload success", Toast.LENGTH_SHORT).show();
                        Call_Api_For_image(uri.toString());
                        Log.d(TAG, "onSuccess: download url "+uri.toString());
                    }
                });
                //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "failed to upload "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        /*Uri file_uri = imageFilePath;
        storage.putFile(file_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "upload successfull", Toast.LENGTH_SHORT).show();
                storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: Download url "+uri.toString());
                    }
                });
            }
        });*/
    }

    public void Call_Api_For_image(final String image_link) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("image_link", image_link);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Call_Api_For_image: "+new Gson().toJson(parameters));

        ApiRequest.Call_Api(context, Variables.uploadImage, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Log.d(TAG, "Responce: "+resp);
                Functions.cancel_loader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {

                        Variables.sharedPreferences.edit().putString(Variables.u_pic, image_link).commit();
                        Variables.user_pic = image_link;

                        if (Variables.user_pic != null && !Variables.user_pic.equals(""))
                            Picasso.with(context)
                                    .load(Variables.user_pic)
                                    .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                                    .resize(200, 200).centerCrop().into(profile_image);

                        Toast.makeText(context, R.string.image_uploaded_successfully, Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    // this will update the latest info of user in database
    public void Call_Api_For_Edit_profile() {
        try {
            Functions.Show_loader(context, false, false);

            String uname = username_edit.getText().toString().toLowerCase().replaceAll("\\s", "");
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("username", uname.replaceAll("@", ""));
                if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals("0")) {
                    parameters.put("user_id", CommonUtils.generateRandomID() + Calendar.getInstance().getTimeInMillis());
                } else {
                    parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
                }
                parameters.put("first_name", firstname_edit.getText().toString());
                parameters.put("last_name", lastname_edit.getText().toString());

//            if (male_btn.isChecked()) {
//                parameters.put("gender", "Male");
//
//            } else if (female_btn.isChecked()) {
//                parameters.put("gender", "Female");
//            }

                switch (genderGroup.getCheckedRadioButtonId()) {
                    case R.id.male_btn:
                        parameters.put("gender", "Male");
                        break;
                    case R.id.female_btn:
                        parameters.put("gender", "Female");
                        break;
                    case R.id.others_btn:
                        parameters.put("gender", "Prefer not to say");
                        break;
                }

                parameters.put("bio", user_bio_edit.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Call_Api_For_Edit_profile: "+new Gson().toJson(parameters));
            ApiRequest.Call_Api(context, Variables.editProfile, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    Functions.cancel_loader();
                    Log.d(TAG, "Responce: "+resp);
                    try {
                        JSONObject response = new JSONObject(resp);
                        String code = response.optString("code");
                        JSONArray msg = response.optJSONArray("msg");
                        if (code.equals("200")) {

                            SharedPreferences.Editor editor = Variables.sharedPreferences.edit();

                            String u_name = username_edit.getText().toString();
                            if (!u_name.contains("@"))
                                u_name = "@" + u_name;

                            editor.putString(Variables.u_name, u_name);
                            editor.putString(Variables.f_name, firstname_edit.getText().toString());
                            editor.putString(Variables.l_name, lastname_edit.getText().toString());
                            editor.commit();

                            Variables.user_name = u_name;

                            getActivity().onBackPressed();
                        } else {

                            if (msg != null) {
                                JSONObject jsonObject = msg.optJSONObject(0);
                                Toast.makeText(context, jsonObject.optString("response"), Toast.LENGTH_SHORT).show();
                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    // this will get the user data and parse the data and show the data into views
    public void Call_Api_For_User_Details() {
        try {
            Log.d(TAG, "Call_Api_For_User_Details: "+Variables.sharedPreferences.getString(Variables.u_id, ""));
            Functions.Show_loader(getActivity(), false, false);
            Functions.Call_Api_For_Get_User_data(getActivity(),
                    Variables.sharedPreferences.getString(Variables.u_id, ""),
                    new API_CallBack() {
                        @Override
                        public void ArrayData(ArrayList arrayList) {

                        }

                        @Override
                        public void OnSuccess(String responce) {
                            Functions.cancel_loader();
                            Parse_user_data(responce);

                        }

                        @Override
                        public void OnFail(String responce) {
                            Log.d(TAG, "OnFail:Call_Api_For_User_Details "+responce);
                        }
                    });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Parse_user_data(String responce) {
        Log.d(TAG, "Parse_user_data: "+responce);
        try {
            JSONObject jsonObject = new JSONObject(responce);

            String code = jsonObject.optString("code");

            if (code.equals("200")) {
                JSONArray msg = jsonObject.optJSONArray("msg");
                JSONObject data = msg.getJSONObject(0);

                firstname_edit.setText(data.optString("first_name"));
                lastname_edit.setText(data.optString("last_name"));
                username_edit.setText(data.optString("username"));

                String picture = data.optString("profile_pic");
                if (picture != null && !picture.equalsIgnoreCase(""))
                    Picasso.with(context)
                            .load(picture)
                            .placeholder(R.drawable.profile_image_placeholder)
                            .into(profile_image);

                String gender = data.optString("gender");

                switch (gender) {
                    case "Male":
                        male_btn.setChecked(true);
                        break;
                    case "Female":
                        female_btn.setChecked(true);
                        break;
                    default:
                        others_btn.setChecked(true);
                        break;
                }

                user_bio_edit.setText(data.optString("bio"));
            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (fragment_callback != null)
                fragment_callback.Responce(new Bundle());
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

}
