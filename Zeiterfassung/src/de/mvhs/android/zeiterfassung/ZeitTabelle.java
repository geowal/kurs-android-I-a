package de.mvhs.android.zeiterfassung;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ZeitTabelle {
	// Konstanten
	private final static String _CREATE_TABLE =
			"CREATE TABLE zeit (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"
			+ "startzeit TEXT NOT NULL , endzeit TEXT)";
	
	private final static String _DROP_TABLE = "DROP TABLE IF EXISTS zeit";
	
	/**
	 * Tabellenname
	 */
	public final static String TABELLENNAME = "zeit";
	/**
	 * ID Spalte der Tabelle
	 */
	public static final String ID = "_id";
	/**
	 * Spalte f�r die Startzeit
	 */
	public static final String STARTZEIT = "startzeit";
	/**
	 * Spalte f�r die Endzeit
	 */
	public static final String ENDZEIT = "endzeit";
	
	/**
	 * Konvertierung f�r das Datum
	 */
	public static final SimpleDateFormat _DF =
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	
	/**
	 * Erzeugen der neuen Tabelle
	 * @param db Datenbank-Referenz
	 */
	public static void CreateTable(SQLiteDatabase db){
		db.execSQL(_CREATE_TABLE);
	}
	
	/**
	 * L�schen der Tabelle
	 * @param db Datenbak-Referenz
	 */
	public static void DropTable(SQLiteDatabase db){
		db.execSQL(_DROP_TABLE);
	}
	
	/**
	 * Speichern der Startzeit als ein neuer Datensatz
	 * @param startZeit Startzeit
	 * @param db Referenz auf die Datenbank
	 * @return ID des neuen Datensatzes
	 */
	public static long SpeichereStartzeit(SQLiteDatabase db, Date startZeit){
		long returnValue = -1;
		
		ContentValues values = new ContentValues();
		values.put(STARTZEIT, _DF.format(startZeit));
		
		returnValue = db.insert(TABELLENNAME, null, values);
		
		return returnValue;
	}
	
	/**
	 * Aktualisieren der Enzeit
	 * @param db Referenz auf die Datenbank
	 * @param endZeit Endzeit
	 * @param id ID des zu aktualisirenden Datensatzes
	 * @return Anzahl der aktualisierten Datens�tze
	 */
	public static int AktualisiereEndzeit(SQLiteDatabase db, long id, Date endZeit){
		int returnValue = 0;
		
		return returnValue;
	}
	
	/**
	 * Suche einen Datensatz mit leeren Endzeit
	 * @param Referenz auf die Datenbank
	 * @return ID des Datensatzes
	 */
	public static long SucheLeereId(SQLiteDatabase db){
		long returnValue = -1;
		
		return returnValue;
	}
}
