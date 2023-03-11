package com.maytemur.mindgap.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maytemur.mindgap.R
import com.maytemur.mindgap.adapter.MindAdaptor
import com.maytemur.mindgap.model.Paylasim
import kotlinx.android.synthetic.main.activity_mind.*

class MindActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    var paylasimListesi = ArrayList<Paylasim>()
    private lateinit var recyclerViewAdaptor: MindAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind)
        auth = Firebase.auth        //initialize yaptık
        firebaseVerileriAl()

        val layoutMuduru= LinearLayoutManager(this)
        recyclerView.layoutManager = layoutMuduru
        recyclerViewAdaptor = MindAdaptor(paylasimListesi)
        recyclerView.adapter = recyclerViewAdaptor
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.ana_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cikis_yap) {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else if (item.itemId == R.id.paylasim_yap) {
            val intent = Intent(this, PaylasimActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    fun firebaseVerileriAl(){
        db.collection("Paylasimlar").orderBy("tarih",
            Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null) {
                    if (!snapshot.isEmpty) {
                        val dokumanlar = snapshot.documents
                        paylasimListesi.clear()

                        for (document in dokumanlar) {
                            val kullaniciName = document.get("kullaniciAdi") as String
                            val kullaniciYorum = document.get("paylasilanYorum") as String
                            val gorselUrl= document.get("gorselUrl") as String?
//                            println(kullaniciName)
//                            println(kullaniciYorum)
                            var indirilenPaylasim= Paylasim(kullaniciName,kullaniciYorum,gorselUrl)
                            paylasimListesi.add(indirilenPaylasim)
                        }
                        recyclerViewAdaptor.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
/* firebase database storage rules - dökümantasyonda security rules var
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null; //authantication ok ise okuyup yazabilir
      // allow write: if false // Auth olsa bile yazamaz ayrıca ** bütün alt klasörleri
      //dahil edeceğinden çok dikkatli kullanılmalı
    }
  }
}
*/