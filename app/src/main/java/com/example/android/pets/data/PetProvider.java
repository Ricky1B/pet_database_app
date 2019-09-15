package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class PetProvider extends ContentProvider {
    private PetDbHelper dbHelper;
    private static final int PETS =100;
    private static final int PETS_ID=101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PETS_ID);
    }
    @Override
    public boolean onCreate() {
        dbHelper= new PetDbHelper(getContext());
        return true;
    }


    public Cursor query( Uri uri,  String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {
        // Get readable database
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TODO: Perform database query on pets table
                cursor=database.query(PetContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }



    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        //Data Validation
        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        if(name ==null)
            throw new IllegalArgumentException("Pet requires a name");

        Integer gender=values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        if(gender ==null || !PetContract.PetEntry.isValidGender(gender)){
            throw new IllegalArgumentException(("Pet requires valid gender"));
        }

        Integer weight= values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if(weight!=null && weight<0){
            throw new IllegalArgumentException("Weight cannot be negative!");
        }

        SQLiteDatabase db= dbHelper.getWritableDatabase();
        // TODO: Insert a new pet into the pets database table with the given ContentValues
        long id= db.insert(PetContract.PetEntry.TABLE_NAME,null,values);
        if (id == -1) {
            Log.e("Error", "Failed to insert row for " + uri);
            return null;
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete( Uri uri, String selection,  String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            case PETS_ID:
                // Delete a single row given by the ID in the URI
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        
    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);


        }
    }
        private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

            // TODO: Update the selected pets in the pets database table with the given ContentValues
            // check that the name value is not null.
            if (values.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)) {
                String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("Pet requires a name");
                }
            }

            // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
            // check that the gender value is valid.
            if (values.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)) {
                Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
                if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
                    throw new IllegalArgumentException("Pet requires valid gender");
                }
            }

            // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
            // check that the weight value is valid.
            if (values.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)) {
                // Check that the weight is greater than or equal to 0 kg
                Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
                if (weight != null && weight < 0) {
                    throw new IllegalArgumentException("Pet requires valid weight");
                }
            }
            // If there are no values to update, then don't try to update the database
            if (values.size() == 0) {
                return 0;
            }

            // Otherwise, get writeable database to update the data
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            // Returns the number of database rows affected by the update statement
            return database.update(PetContract.PetEntry.TABLE_NAME, values, selection, selectionArgs);
        }
}
