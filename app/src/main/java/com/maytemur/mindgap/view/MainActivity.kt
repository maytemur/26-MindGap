package com.maytemur.mindgap.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.maytemur.mindgap.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth         // Initialize Firebase Auth

        val guncelKullanici = auth.currentUser
        if (guncelKullanici != null) {
            val intent = Intent(this, MindActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun kayitOl(view: View) {
        val email = emailText.text.toString()
        val parola = parolaText.text.toString()
        val kullaniciAdi = kullaniciAdiText.text.toString()

        auth.createUserWithEmailAndPassword(email, parola).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //kullanıcı adını güncelle
                val guncelKullanici = auth.currentUser
                val profilGuncellemeIstegi = userProfileChangeRequest {
                    displayName = kullaniciAdi
                }
                if (guncelKullanici != null) {
                    guncelKullanici.updateProfile(profilGuncellemeIstegi)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Profil adı güncellendi",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
                Toast.makeText(applicationContext, "Kullanıcı Oluşturuldu", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(this, MindActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun girisYap(view: View) {
        val email = emailText.text.toString()
        val parola = parolaText.text.toString()
        if (email != "" && parola != "") {

            auth.signInWithEmailAndPassword(email, parola).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val guncelKullanici = auth.currentUser?.displayName.toString()
                    Toast.makeText(
                        applicationContext,
                        "Hoşgeldin: ${guncelKullanici}",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(this, MindActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}