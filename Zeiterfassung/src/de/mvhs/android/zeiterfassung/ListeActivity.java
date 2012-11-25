package de.mvhs.android.zeiterfassung;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class ListeActivity extends ListActivity {
	
		// Neue Klassen werden erzeugt über Rechtsklick auf den Packagenamen -> Neu -> Neu Klasse, Name 
		// der Klasse eingeben - Superklasse auswählen hier android.ListActivity (extends)

		/*
		 *  Globale variablen
		 *  
		 */
		private DBHelper  _DBHelper;
		private SQLiteDatabase  _Db;
		private Cursor  _Cursor;
		
		// onCr schreiben und die Methode vervollständigen lassen
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			/*
			 *  Bei dem Layout handelt es sich um eine Android Liste, darum muss die ID im XML File
			 *  auch eine @android:id/list sein
			 *  Zuweisen des Layouts
			 */
			setContentView(R.layout.activity_liste);
			
		}
		
		/*
		 *  Problem mit der Datenbank
		 *  Bei den anderen Klassen haben wir die Datenbank geöffnet, Daten geholt und die Datenbank wieder geschlossen
		 *  
		 *  In dieser Klasse müssen wir die Datenbank offen lassen, sie darf nicht geschlossen werden
		 *  Die Datenbank und der Cursor auf die Daten müssen global verfügbar sein
		 */
		
		protected void onStart() {
			super.onStart();
			
			// Initialisieren der Datenbank und der Daten
			_DBHelper = new DBHelper(this);
			_Db = _DBHelper.getReadableDatabase();
			
			/*
			 *  Wir muessen die Zeittabelle erweitern mit einer Methode die uns hier die Daten liefert
			 *  	public static cursor LiefereAlleDaten 
			 *  die brauchen wir hier in der listeActivity
			 */
			_Cursor = ZeitTabelle.LiefereAlleDaten(_Db);
			
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(
					this,															// Context
					android.R.layout.simple_list_item_2,							// Layout für die Zeile
					_Cursor,														// Daten	
					new String[] {ZeitTabelle.STARTZEIT,ZeitTabelle.ENDZEIT},		// Anzahl der Datensätze
					new int[] {android.R.id.text1, android.R.id.text2},				// ID's der Datensätze
					0); 															// Flag ist nicht besetzt
			
			setListAdapter(adapter);
		}
		
		protected void onStop() {
			super.onStop();
			
			// Wider Schliessen der Resourcen
			if (_Cursor != null) {
				_Cursor.close();
			}
			
			if (_Db != null) {
				_Db.close();
			}
			
			if (_DBHelper != null) {
				_DBHelper.close();
			}
		}
		
		// Das Icon muss Klickbar und Sichtbar gemacht werden
		// Wieder nur onCr tippen und vervollständigen lassen
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getActionBar().setHomeButtonEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			return super.onCreateOptionsMenu(menu);
		}
		
		/*
		 *  Zurück Knopf einfügen damit man wieder auf die Main Activity zurück kommt
		 *  Das Icon der App wird klickbar, Ein Links-Pfeil kommt dazu
		 */
		
		// onOp tippen und vervolstängigen lassen
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			/*
			 *  die Parameter über einen switch auswerten und Methoden aufrufen
			 *  swit antippen und vervollstängigen lassen
			 */
			
			switch (item.getItemId()) {
			
			// Standardname für den Homeknopf übergeben
			case android.R.id.home:
					Intent homeIntent  = new Intent(this, MainActivity.class);
					startActivity(homeIntent);
				break;

			default:
				break;
			}
			
		return super.onOptionsItemSelected(item);
		}

}