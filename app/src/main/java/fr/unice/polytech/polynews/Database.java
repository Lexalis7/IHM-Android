package fr.unice.polytech.polynews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import fr.unice.polytech.polynews.models.Mishap;
//import fr.unice.polytech.polyblem.model.Photo;

public class Database extends SQLiteOpenHelper {

    private static final String Mishap_ID = "idMishap";
    private static final String Mishap_TITLE = "titleMishap";
    private static final String Mishap_CATEGORY = "category";
    private static final String Mishap_DESCRIPTION = "description";
    private static final String Mishap_LOCATION = "location";
    private static final String Mishap_LOCATIONDETAILS = "locationDetails";
    private static final String Mishap_URGENCY = "urgency";
    private static final String Mishap_EMAIL = "email";
    private static final String Mishap_DATE = "dateMishap";

    private static final String Mishap_TABLE_NAME = "Mishap";

    private static final String Mishap_CREATE_TABLE =
            "CREATE TABLE "+ Mishap_TABLE_NAME +" ( "+ Mishap_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            Mishap_TITLE+" TEXT NOT NULL,"+
            Mishap_CATEGORY + " TEXT CHECK (category IN ('Manque','Casse','Dysfonctionnement', 'Propreté', 'Autre')), "+
            Mishap_DESCRIPTION + " TEXT," +
            Mishap_LOCATION + " TEXT,"+
            Mishap_LOCATIONDETAILS +" TEXT,"+
            Mishap_URGENCY + " TEXT CHECK (urgency IN ('Faible','Moyen','Forte')),"+
            Mishap_EMAIL+" TEXT,"+
            Mishap_DATE + " TEXT)";

    private static final String Mishap_INSERT = "INSERT INTO Mishap(titleMishap, category, description, location, locationdetails, urgency, email, dateMishap) " +
            "VALUES ('Mishap1', 'Casse', null,'Bat O', '355', 'Faible', 'marion@etu.fr', '16/05/18');";
    private static final String Mishap_INSERT2 =  " INSERT INTO Mishap(titleMishap, category, description, location,locationdetails, urgency, email, dateMishap)" +
            "VALUES ('Mishap2', 'Propreté', null,'Bat E', '235', 'Forte', 'florian@etu.fr', '10/05/18');";
    private static final String Mishap_INSERT3 = "INSERT INTO Mishap(titleMishap, category, description, location,locationdetails, urgency, email, dateMishap)" +
            "VALUES ('Mishap3', 'Autre', null, 'Bat W', '235', 'Moyen', 'quentin@etu.fr', '14/05/18');";


    private static final String PHOTO_TABLE_NAME = "photos";
    private static final String PHOTO_ID = "idPhoto";
    private static final String PHOTO_URL = "url";

    private static final String PHOTOS_CREATE_TABLE =
            "CREATE TABLE "+ PHOTO_TABLE_NAME +" ( "+ PHOTO_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            Mishap_ID +" INTEGER NOT NULL,"+
            PHOTO_URL + " TEXT NOT NULL)";


    private static final String Mishap_DROP_TABLE = "DROP TABLE IF EXISTS " + Mishap_TABLE_NAME+";";


    private static String DB_NAME = "polynews_database";
    private final Context myContext;

    public Database(Context context){
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(Mishap_CREATE_TABLE);
        db.execSQL(PHOTOS_CREATE_TABLE);
        db.execSQL(Mishap_INSERT);
        db.execSQL(Mishap_INSERT2);
        db.execSQL(Mishap_INSERT3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(Mishap_DROP_TABLE);
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null)
            db.close();
        super.close();
    }

    public Mishap getMishap(Cursor c){
        int idMishap = c.getInt(0);
        String titleMishap = c.getString(1);
        String category= c.getString(2);
        String description= c.getString(3);
        String location= c.getString(4);
        String locationDetails= c.getString(5);
        String urgency= c.getString(6);
        String email= c.getString(7);
        String date= c.getString(8);
        return new Mishap(idMishap,titleMishap,category,description,location,locationDetails,urgency,
                email, date);
    }

    public Mishap getMishap(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Mishap WHERE " + Mishap_ID  + " = " + id, null);
        c.moveToFirst();
        return getMishap(c);
    }

    public List<Mishap> getAllMishaps(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Mishap", null);
        c.moveToFirst();
        List<Mishap> Mishaps = new ArrayList<>();
        while(!c.isAfterLast()){
            Mishaps.add(getMishap(c));
            c.moveToNext();
        }
        c.close();
        return Mishaps;
    }

    public long addMishap(Mishap Mishap){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(Mishap_TITLE, Mishap.getTitleMishap());
        values.put(Mishap_CATEGORY, Mishap.getCategory());
        values.put(Mishap_DESCRIPTION, Mishap.getDescription());
        values.put(Mishap_LOCATION, Mishap.getLocation());
        values.put(Mishap_LOCATIONDETAILS, Mishap.getLocationDetails());
        values.put(Mishap_URGENCY, Mishap.getUrgency());
        values.put(Mishap_EMAIL, Mishap.getEmail());
        values.put(Mishap_DATE, Mishap.getDate());

        long id  = db.insert(Mishap_TABLE_NAME, null, values);
        db.close();
        return id;
    }
/*
    public List<Photo> getPictures(Mishap Mishap){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM photos WHERE " + Mishap_ID + "=" + Mishap.getIdMishap(), null);
        c.moveToFirst();
        List<Photo> photos = new ArrayList<>();
        while(!c.isAfterLast()){
            long idPhoto = c.getLong(0);
            int idMishap = c.getInt(1);
            String url= c.getString(2);
            Photo photo = new Photo((int)idPhoto,idMishap, url);
            photos.add(photo);
            c.moveToNext();
        }
        c.close();
        return photos;
    }
*/
    public void addPicture(long id, String url){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(Mishap_ID, id);
        values.put(PHOTO_URL, url);

        db.insert(PHOTO_TABLE_NAME, null ,values);
        db.close();
    }

    public void deleteMishap(Mishap Mishap){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(Mishap_TABLE_NAME, Mishap_ID +"=?", new String[]{String.valueOf(Mishap.getIdMishap())});
        db.close();
    }
}