package com.tachyon.bindaas.SimpleClasses;

import android.content.SharedPreferences;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Variables {


    public static final String device = "android";

    public static int screen_width;
    public static int screen_height;

    public static final String SelectedAudio_MP3 = "SelectedAudio.mp3";
    public static final String SelectedAudio_AAC = "SelectedAudio.aac";

    public static final String root = Environment.getExternalStorageDirectory().toString();
    public static final String app_hidden_folder =root+"/.HiddenBindaas/";
    public static final String app_folder = root + "/Bindaas/";
    public static final String draft_app_folder = app_folder + "Draft/";


    public static int max_recording_duration = 30000;
    public static int recording_duration =30000;
    public static int longVideoDuration = 30000;
    public static int mediumVideoDuration = 20000;
    public static int shortVideoDuration = 10000;
    public static int min_time_recording=3000;
    public static long auto_scroll_duration = 15000;
    public static int maxVideoWidth = 720;
    public static int maxVideoHeight = 1280;
    public static double bitRateMultiplier = 4;
    public static int min_draft_duration = 5000;


    public static String output_frontcamera= app_folder + "output_frontcamera.mp4";
    public static String outputfile = app_folder + "output.mp4";
    public static String outputfile2 = app_folder + "output2.mp4";
    public static String output_filter_file = app_folder + "output-filtered.mp4";
    public static String output_compressed_file = app_folder + "";

    public static String gallery_trimed_video = app_folder + "gallery_trimed_video.mp4";
    public static String gallery_resize_video = app_folder + "gallery_resize_video.mp4";


    public static SharedPreferences sharedPreferences;
    public static final String pref_name = "pref_name";
    public static final String u_id = "u_id";
    public static final String u_name = "u_name";
    public static final String u_pic = "u_pic";
    public static final String f_name = "f_name";
    public static final String l_name = "l_name";
    public static final String gender = "u_gender";
    public static final String islogin = "is_login";
    public static final String device_token = "device_token";
    public static final String api_token = "api_token";
    public static final String device_id = "device_id";
    public static final String uploading_video_thumb="uploading_video_thumb";
    public static final String auto_scroll_key = "auto_scroll_key";
    public static final String show_preview_key = "show_preview_key";
    public static final String bio = "bio_key";
    public static final String fb_link = "fb_link";
    public static final String insta_link = "insta_link";
    public static final String language = "language";
    public static final String anyone_can_message = "anyone_can_message";
    public static final String who_can_tagme = "who_can_tagme";
    public static final String is_first_time_launch = "is_first_time_launch";

    public static String user_id;
    public static String user_name;
    public static String user_pic;


    public static String tag = "bindaas ";

    public static String Selected_sound_id = "null";

    public static boolean Reload_my_videos = false;
    public static boolean Reload_my_videos_inner = false;
    public static boolean Reload_my_likes_inner = false;
    public static boolean Reload_my_notification = false;


    public static final String gif_firstpart = "https://media.giphy.com/media/";
    public static final String gif_secondpart = "/100w.gif";

    public static final String gif_firstpart_chat = "https://media.giphy.com/media/";
    public static final String gif_secondpart_chat = "/200w.gif";


    public static final SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static final SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);


    public static final boolean is_secure_info = false;
    public static final boolean is_remove_ads = true;
    public static final boolean is_show_gif=true;

    public static final boolean is_demo_app=false;

    // if you want to add a duet function into our project you have to set this value to "true"
    // and also get the extended apis
    public static final boolean is_enable_duet=true;

    public final static int permission_camera_code = 786;
    public final static int permission_write_data = 788;
    public final static int permission_Read_data = 789;
    public final static int permission_Recording_audio = 790;
    public final static int Pick_video_from_gallery = 791;


    public static String gif_api_key1 = "giphy_api_key_here";

    //public static final String privacy_policy="https://www.privacypolicygenerator.info/live.php?";



    //public static final String main_domain = "http://bindaasonline.com/";
    //public static final  String main_domain="http://funworld.sciflock.com/";
    public static final  String main_domain="http://bindaasonlinedev.com/";

    //public static final String base_url=main_domain+"API/funworld/";
    public static final String base_url = main_domain + "coreapi/";

    //public static final String api_domain = "http://3.6.175.12/API/index.php?p=";
    public static final String api_domain = base_url + "index.php?p=";

    public static final String privacy_policy = main_domain + "privacypolicy.php";
    public static final String termsOfUse = main_domain + "tnc.php";


    public static final String SignUp = api_domain + "signup";
    public static final String uploadVideo = api_domain + "uploadVideo";
    public static final String showAllVideos = api_domain + "showAllVideos";
    public static final String showMyAllVideos = api_domain + "showMyAllVideos";
    public static final String likeDislikeVideo = api_domain + "likeDislikeVideo";
    public static final String updateVideoView = api_domain + "updateVideoView";
    public static final String allSounds = api_domain + "allSounds";
    public static final String favSound = api_domain + "favSound";
    public static final String myFavSound = api_domain + "myFavSound";
    public static final String myLikedVideo = api_domain + "myLikedVideo";
    public static final String followUsers = api_domain + "followUsers";
    public static final String discover = api_domain + "discover";
    public static final String showVideoComments = api_domain + "showVideoComments";
    public static final String postComment = api_domain + "postComment";
    public static final String editProfile = api_domain + "editProfile";
    public static final String getUserData = api_domain + "get_user_data";
    public static final String getFollowers = api_domain + "getFollowers";
    public static final String getFollowings = api_domain + "getFollowings";
    public static final String SearchByHashTag = api_domain + "SearchByHashTag";
    public static final String sendPushNotification = api_domain + "sendPushNotification";
    public static final String uploadImage = api_domain + "uploadImage";
    public static final String DeleteVideo = api_domain + "DeleteVideo";
    public static final String search = api_domain + "search";
    public static final String getNotifications = api_domain + "getNotifications";
    public static final String getVerified = api_domain + "getVerified";
    public static final String downloadFile = api_domain + "downloadFile";

    public static final String DOWNLOAD_VIDEO = main_domain + "watch.php?id=";

    public static final String FLAG_VIDEO = api_domain + "flagVideo";
    public static final String FLAG_COMMENT = api_domain + "flagComment";
    public static final String SIGN_UP = api_domain + "localSignup";
    public static final String DELETE_COMMENT = api_domain + "deleteComment";
    public static final String POST_AUDIO = api_domain + "uploadLocalSound";
    public static final String SHARE_VIDEO = api_domain + "shareVideo";
    public static final String NEWS_FEED = api_domain + "getNewsFeed";
    public static final String HASH_TAGS = api_domain + "getHashTags";
    public static final String SAVE_PREFERENCES = api_domain + "getPreference";
    public static final String GET_MUTUAL_FOLLOWERS = api_domain + "getMutualFollowers";
    public static final String EDIT_COMMENT = api_domain + "getEditComment";

}
