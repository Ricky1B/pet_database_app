package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class PetContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";
    private PetContract(){

    }

    public static final class PetEntry implements BaseColumns{
        public final static String TABLE_NAME = "pets";
        public final static String  _ID  = BaseColumns._ID;
        public final static String  COLUMN_PET_NAME  = "name";
        public final static String COLUMN_PET_BREED ="breed";
        public final static String COLUMN_PET_GENDER ="gender";
        public final static String COLUMN_PET_WEIGHT ="weight";

        public final static int GENDER_UNKNOWN = 0;
        public final static int GENDER_MALE = 1;
        public final static int GENDER_FEMALE = 2;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        public static boolean isValidGender(int gender){
           if(gender==GENDER_UNKNOWN || gender==GENDER_MALE || gender==GENDER_FEMALE)
               return true;
           else
               return false;
        }

    }
}
