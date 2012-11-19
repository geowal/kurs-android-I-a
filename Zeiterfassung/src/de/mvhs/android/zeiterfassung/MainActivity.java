package de.mvhs.android.zeiterfassung;

import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialisierung der Buttons fuer die Listener-Zuweisung
        Button cmdStart = (Button)findViewById(R.id.starten);
        Button cmdEnde = (Button)findViewById(R.id.beenden);
        
        // Zuweisung der Click-Listener
        cmdStart.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onStarten();
			}
		});
        
        cmdEnde.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onBeenden();
			}
		});
    }
    
    /**
     * Aufnahme der Startzeit
     */
    private void onStarten(){
    		EditText txtStartzeit = (EditText)findViewById(R.id.startzeit);
    		
    		// Aktuelle Uhrzeit
    		Date jetzt = new Date();
    		
    		// Ausgabe der Zeit
    		txtStartzeit.setText(jetzt.toString());
    		
    		// Speichern der zeit in der Datenbank
    		// Der Kontext this muss uebergeben werden, da die Datenbank in den Speicher der app gespeichert wird
    		DBHelper helper = new DBHelper(this);
    		SQLiteDatabase db = helper.getWritableDatabase();
    		
    		ZeitTabelle.SpeichereStartzeit(db, jetzt);
    		
    		// Ressourcen schliessen
    		// In java muss man Streams explizit Oeffnen und Schliesen, ansonsten gibt es Exceptions, die das Programm langsam machen
    		db.close();
    		helper.close();
    }
    
    /**
     * Aufnahme der Endzeit
     */
    private void onBeenden(){
    		EditText txtEndzeit = (EditText)findViewById(R.id.endzeit);
    		
    		// Aktuelle Uhrzeit
    		Date jetzt = new Date();
    		
    		// Ausgabe der Zeit
    		txtEndzeit.setText(jetzt.toString());
    }
}
