package de.mvhs.android.zeiterfassung;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ParseException;

public class ZeitTabelle {
	// Konstanten
	/*
	 final static bedeutet dass die Konstante schon zur Kompelierzeit erzeugt wird (muss also zur Laufzeit nicht mehr erstellt werden)
	 Dadurch ist sie viel schneller verfuegbar
	 
	 _KONSTANTENNAME (mit UNTERSTRICH) weil diese konstante privat ist und nur hier verfuegbar ist.
	 Zeit wird als ISO Format (Text) in der Datenbank gespeichert
	 2012-11-12T 08:15:30
	 Text ISO ist gut lesbar, und gut rechenbar (zb: wenn die Zeitdifferenz (minus) ausgerechnet werden muss)
	 Wir brauchen:
	 Bei Druecken Start: Neuer Datensatz mit Startzeit
	 Bei Druecken Beenden: Aktualisieren mit Endzeit, und zwar:
	 Hausaufgabe: 
	 den letzten Datensatz mit Enddatum suchen und								
	*/
	
	// SQL Abfragen auf Konstanten gelegt.
	private final static String _CREATE_TABLE =
			"CREATE TABLE zeit (_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"
			+ "startzeit TEXT NOT NULL , endzeit TEXT)";
	
	private final static String _DROP_TABLE = "DROP TABLE IF EXISTS zeit";
	
	private final static String _SQL_EMPTY_ENDTIME = "SELECT _id FROM zeit WHERE IFNULL(endzeit, '') = ''";
	
	// ?1 und ?2 sind Ersetzungsparameter, siehe unten
	private final static String _SQL_UPDATE_ENDTIME = "UPDATE zeit SET endzeit = ?1 WHERE _id = ?2";
	
	// fuer die Feldnamen der Tabelle
	/**
	 * Tabellenname
	 */
	public final static String TABELLENNAME = "zeit";
	/**
	 * ID Spalte der Tabelle
	 */
	public static final String ID = "_id";
	/**
	 * Spalte fuer die Startzeit
	 */
	public static final String STARTZEIT = "startzeit";
	/**
	 * Spalte fuer die Endzeit
	 */
	public static final String ENDZEIT = "endzeit";
	
	/*
	 * Konvertierung fuer das Datum in einen String
	 * Konstante angelegt, da wir den Konverter noch oefters brauchen
	 * Datum in ISO Format bringen
	 */
	public static final SimpleDateFormat _DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

	/**
	 * Erzeugen der neuen Tabelle
	 * @param db Datenbank-Referenz
	 */
	public static void CreateTable(SQLiteDatabase db){
		db.execSQL(_CREATE_TABLE);
	}
	
	/**
	 * Loeschen der Tabelle
	 * @param db Datenbak-Referenz
	 */
	public static void DropTable(SQLiteDatabase db){
		//loeschen der Datenbank
		db.execSQL(_DROP_TABLE);
	}
	
	/**
	 * Speichern der Startzeit als ein neuen Datensatz
	 * @param db Referenz auf die Datenbank
	 * @param startZeit Startzeit
	 * @return ID des neuen Datensatzes
	 */
	public static long SpeichereStartzeit(SQLiteDatabase db, Date startZeit){
		// hier Rückgabe der ID -1, heist nichts gefunden, da die ID«s immer mit 1 anfangen
		long returnValue = -1;
		
		//Erzeugen von Key-Value Paaren (Schluessel und Wert) 
		ContentValues values = new ContentValues();
		
		/*
		 * Startzeit 		= Key
		 * formatierte Zeit = Wert
		 * mit put wird das key-value Paar erzeugt
		 */
		values.put(STARTZEIT, _DF.format(startZeit));
		
		// mit insert wird das Key-Value Paar in die Datenbank geschrieben
		// wenn das Key-Value Paar leer ist, dann brauchen wir den Hack mit null
		returnValue = db.insert(TABELLENNAME, null, values);
		
		return returnValue;
	}
	
	/**
	 * Schueler Loesung
	 * 
	 * Aktualisieren der Enzeit
	 * @param db Referenz auf die Datenbank
	 * @param endZeit Endzeit
	 * @param id ID des zu aktualisirenden Datensatzes
	 * @return Anzahl der aktualisierten Datensaetze
	 */
	/*
	public static int AktualisiereEndzeit(SQLiteDatabase db, long id, Date endZeit){
		int returnValue = 0;
		ContentValues values = new ContentValues();
		
		values.put(ENDZEIT, _DF.format(endZeit));
		returnValue = db.update(TABELLENNAME, values, ID + " = ?", new String[] { Long.toString(id)} );
		
		return returnValue;
	} 
	*/
	/**
	 * Lehrer Loesung
	 * 
	 * Aktualisieren der Enzeit
	 * @param db Referenz auf die Datenbank
	 * @param id ID des zu aktualisirenden Datensatzes
	 * @param endZeit Endzeit
	 * @return Anzahl der aktualisierten Datensaetze
	 */
	
	public static int AktualisiereEndzeit(SQLiteDatabase db, long id, Date endZeit){
		int returnValue = 0;
		
		// Vorkompilierte SQL Abfrage erzeugen
		SQLiteStatement updateStatement = db.compileStatement(_SQL_UPDATE_ENDTIME);

		// Parameter an das SQL Statement binden und uebergeben
		// 1 und 2 sind Ersetzungsparameter in der SQL Konstante _SQL_UPDATE_ENDTIME
		updateStatement.bindString(1, _DF.format(endZeit));
		updateStatement.bindLong(2, id);
		
		// Aktualisierung durchführen
		returnValue = updateStatement.executeUpdateDelete();
		
		return returnValue;
	}
	
	
	/**
	 * Suche einen Datensatz mit leeren Endzeit
	 * @param Referenz auf die Datenbank
	 * @return ID des Datensatzes
	 */
	public static long SucheLeereId(SQLiteDatabase db){
		long returnValue = -1;
		
		/* 
		 * Abfrage wird hier mit einem Cursor durchgefuehrt
		 * Datenbank wird nach leerem Eintrag abgefragt, Es werden alle Ergebnisse 
		 * gesucht, wo die Endzeit ist gleich null oder leer ist
		 * Der Cursor steht auf -1
		 */
		Cursor data = db.rawQuery(_SQL_EMPTY_ENDTIME, null);
		
		/*
		 * Prüfen ob Ergebnisse vorliegen
		 * Der Index bei Autoincrement bei SQL fängt bei -1 an
		 * Index oder ID nicht in einer Schleife ermitteln, sondern vorher ermitteln und dann in der Schleife weitere Bearbeitung
		 * der Datenbank vornehmen. Ansonsten kann es sehr lange dauern, bei Tausenden von Datensätzen
		 */
		if (data.moveToFirst()) {
			/*
			 * wenn ja, dann die ID auslesen
			 * Das Ergebnis aus SQL ist ein Integer und muss zu java Long umgewandelt werden
			 */
			returnValue = data.getLong(data.getColumnIndex(ID));
			
		}
		
		return returnValue;
	}
	
	/**
	 * Ausgabe der Startzeit eines Datensatzes als Datums- Objekt
	 * @param db Datenbank Instanz
	 * @param id ID des Datensatzes
	 * @return Startdatum des Datensatzes (NULL, wenn kein Datensatz gefunden wurde)
	 * @throws java.text.ParseException 
	 */
	
	public static Date GebeStartzeitAus(SQLiteDatabase db, long id) throws java.text.ParseException {
		Date returnValue = null;
		
		/*
		 * Auslesen der Startzeit aus der Datenbank
		 * Hier wird die Abfrage mit einem SQL Statement durchgeführt
		 * Wir gehen mit dem Cursor duch die Datenbank
		 */
		
		Cursor data = db.query(
				_CREATE_TABLE,						// Tabellenname
				new String[] {STARTZEIT},			// Spalten
				ID + "=?",							// Bedingung ist die ID des Datensatzes
				new String[] {String.valueOf(id)},  // Parameter für die Bedingung
				null,								// Group BY wird nicht genutzt
				null,								// Having wird nicht genutz
				null);								// Sortierung wird nicht genutz
		
		/*
		 *  Prüfen ob die Datensätze im Ergebnis vorhanden sind und nehme dann den Ersten
		 */
		if (data.moveToFirst()) {
			// Auslesen der Startzeit Spalte
			String startzeit = data.getString(data.getColumnIndex(STARTZEIT));
			
			// Wenn unsere Datenbank keinen leeren Startzeit Eintrag hat, dann mach was
			if (startzeit != null && !startzeit.isEmpty()) {
				
				/*
				 *  Konverierung des Strings in ein Datums Objekt
				 *  try-catch ist beim Parsen der Daten auf einen anderen Typ immer nötig
				 */
				try {
					returnValue = _DF.parse(startzeit);
				} catch(ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return returnValue;
	}
	
	
	
	
	//TESTeintrag 20121124
	
	
	
	
}
