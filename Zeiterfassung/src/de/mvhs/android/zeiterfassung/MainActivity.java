package de.mvhs.android.zeiterfassung;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	/**
	 *  Konstanten für das Datumsformat. Formatiert in der Landeseinstellung des Gerätes
	 */
	private final DateFormat _DTF = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
	private EditText _Startzeit;
	private EditText _Endzeit;
	private Button _Start;
	private Button _Ende;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialisierung der Buttons fuer die Listener-Zuweisung
        _Start = (Button)findViewById(R.id.starten);
        _Ende = (Button)findViewById(R.id.beenden);
        
        // Initialisierung der Textfelder für die Ausgabe
        _Startzeit = (EditText)findViewById(R.id.startzeit);
        _Endzeit = (EditText)findViewById(R.id.endzeit);
        
        // Zuweisung der Click-Listener
        _Start.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onStarten();
			}
		});
        
        _Ende.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onBeenden();
			}
		});
        
        // Editierbarkeit der Textfelder deaktivieren, so kann der Nutzer nichts reinschreiben
        _Startzeit.setEnabled(false);
        _Endzeit.setEnabled(false);
    }
    
    /**
     *  Methode für Menü einfügen
     *  Der Aufruf der ActionBar erscheint Oben im Program
     */
    public boolean onCreateOptionsMenu(Menu menu) {
    	/**
    	 *  Einfügen eines Inflater
    	 *  Inflater ist eine Methode die XML Dateien in Java Code entfaltet, Es gibt Layout Inflater und
    	 *  MenuInflater
    	 */
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
    
    /*
     *  Methode die ausgeführt wird, wenn aus dem Menue ein Eintrag angeklickt wird
     *  onOp schreiben und onOPtionItemSelected einfügen
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Überprüfen welches Menu geklickt wurde
    	// swit antippen und vervollständigen lassen
    	switch (item.getItemId()) {
    	// Hier wird die ID der Opt_liste übergeben und abgearbeitet
		case R.id.opt_liste:
				/*
				 *  Ein Trick um etwas im Entwicklungsstadium anzeigen zu lassen, ist das Ausführen eines Toast
				 *  Mit LENGTH_LONG (lange) oder  LENGTH_SHORT (kurz) wird die Anzeigedauer des Toast bestimmt
				 *  Toast.makeText(this,"Auflistung kommt!", Toast.LENGTH_LONG).show();
				 */
				
				// startet die ListActivity
				// Eine Activity braucht eine oder mehrere Layouts (landscape, usw.) und eine Activity Klasse
				Intent listIntent = new Intent(this, ListeActivity.class);
				this.startActivity(listIntent);
			break;

		default:
			break;
		}
    	return super.onOptionsItemSelected(item);
    }
    
    
    
    /**
     * Aufnahme der Startzeit
     */
    private void onStarten(){
    		EditText txtStartzeit = (EditText)findViewById(R.id.startzeit);
    		
    		// Aktuelle Uhrzeit
    		Date jetzt = new Date();
    		
    		/*
    		 *  Ausgabe der Zeit
    		 *  Mit formatierter Zeit
    		 */ 
    		
    		_Startzeit.setText(_DTF.format(jetzt));
    		
    		// Speichern der Zeit in der Datenbank
    		// Der Kontext this muss uebergeben werden, da die Datenbank in den Speicher der app gespeichert wird
    		DBHelper helper = new DBHelper(this);
    		SQLiteDatabase db = helper.getWritableDatabase();
    		
    		ZeitTabelle.SpeichereStartzeit(db, jetzt);
    		
    		// Ressourcen schliessen
    		// In java muss man Streams explizit Oeffnen und Schliesen, ansonsten gibt es Exceptions, die das Programm langsam machen
    		db.close();
    		helper.close();
    		
    		// Andere Elemente steuern
    		_Endzeit.setText("");				// Textfeld Endzeit Leerstring setzen
    		_Start.setEnabled(false);			// Button Start ausführbar machen
    		_Ende.setEnabled(true);				// Button Ende ausgrauen
    }
    
    /**
     * Aufnahme der Endzeit
     */
    private void onBeenden(){
    		// Aktuelle Uhrzeit
    		Date jetzt = new Date();
    		
    		/*
    		 *  Ausgabe der Zeit
    		 *  Formatierte Zeit verwenden
    		 */
    		_Endzeit.setText(_DTF.format(jetzt));
    		 
    		//Speichern der Zeit in der Datenbank
    		DBHelper helper = new DBHelper(this);
    		SQLiteDatabase db = helper.getWritableDatabase();
    		
    		/*
    		 *  Leeren Eintrag in der Datenbank suchen
    		 */
    		long id = ZeitTabelle.SucheLeereId(db);
    		
    		if (id > 0 ) {
    			ZeitTabelle.AktualisiereEndzeit(db, id, jetzt);
    			
    			// Andere Elemente steuern
    			_Start.setEnabled(true);
    			_Ende.setEnabled(false);
    		}
    		
    		//Resourcen wieder schliessen
    		db.close();
    		helper.close();
    		
    }
    
    /**
     *  Methode zum Prüfen des Datenbank Zustandes
     */
    
    private void pruefeZustand() {
    	
    	// Datenbank öffnen
    	DBHelper helper = new DBHelper(this);
    	SQLiteDatabase db = helper.getReadableDatabase();
    	
    	// ID des offenen Datensatzes in der DB finden
    	long id = ZeitTabelle.SucheLeereId(db);
    	
    	// Prüfen, ob diese ID (Datensatz) vorhanden ist
    	if (id > 0) {
    		//Startzeit auslesen
    		Date startZeit = ZeitTabelle.GebeStartzeitAus(db, id);
    		
    		// Startzeit ausgeben
    		_Startzeit.setText(_DTF.format(startZeit));
    		
    		// Buttons einrichten
    		_Start.setEnabled(false);
    		_Ende.setEnabled(true);
    	}
    	else {
    		_Startzeit.setText("");
    		_Start.setEnabled(true);
    		_Ende.setEnabled(false);
    	}
    	
    	// Datenbank wieder schliessen
    	db.close();
    	helper.close();
    	
    }    
}
