package com.maytemur.mindgap.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.maytemur.mindgap.R
import kotlinx.android.synthetic.main.activity_paylasim.*
import java.util.*
import java.util.jar.Manifest

class PaylasimActivity : AppCompatActivity() {
    val db = Firebase.firestore
    val storage = Firebase.storage
    private var auth = Firebase.auth
    var gorselUri: Uri? = null
    var gorselBitmap: Bitmap? = null
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {                        //handle uri
            gorselUri = it
            imageView.visibility = View.VISIBLE
            imageView.setImageURI(it)
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paylasim)
        auth = Firebase.auth
    }

    fun paylas(view: View) {
        if (gorselUri != null) {
            val reference = storage.reference
            val uuidRandom = UUID.randomUUID()
            var resimIsmi = "${uuidRandom}.jpg"

            val gorselReference = reference.child("gorseller").child(resimIsmi)
            gorselReference.putFile(gorselUri!!).addOnSuccessListener { task ->
                //Url alınacak
                val yuklenenGorselReference = reference.child("gorseller").child(resimIsmi)
                yuklenenGorselReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    veritabaninaKaydet(downloadUrl)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            veritabaninaKaydet(null)
        }
    }

    private fun veritabaninaKaydet(downloadUrl: String?) {
        val paylasilanYorum = paylasimText.text.toString()
        val kullaniciAdi = auth.currentUser!!.displayName.toString()
        val tarih = Timestamp.now()
        val paylasimMap =
            hashMapOf<String, Any>() //key,value eşleşmesi için hashmap'e ekledik. Değer tarafı belli olmadığı
        //için Any yaptık
        paylasimMap.put("paylasilanYorum", paylasilanYorum)
        paylasimMap.put("kullaniciAdi", kullaniciAdi)
        paylasimMap.put("tarih", tarih)
        if (downloadUrl != null) {
            paylasimMap.put("gorselUrl", downloadUrl)
        }

        db.collection("Paylasimlar").add(paylasimMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                .show()
        }
    }

    fun gorselEkle(view: View) {
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //izin verilmemiş istememiz gerekiyor
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1
            )
        } else {
            //izin zateen verilmiş
            getContent.launch("image/*")
            /*println("gorselSecClick  ${gorselUri.toString()}")
                if (gorselUri != null) {
                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(this.contentResolver,gorselUri!!)
                        gorselBitmap = ImageDecoder.decodeBitmap(source)
                        imageView.setImageBitmap(gorselBitmap)
                    }else {
                        gorselBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,gorselUri)
                        imageView.setImageBitmap(gorselBitmap)
                    }
                }*/
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //izin verilince yapılacaklar

                getContent.launch("image/*")
                println("onrequest  ${gorselUri.toString()}")
//                if (gorselUri != null) {
//                    if (Build.VERSION.SDK_INT >= 28) {
//                        val source = ImageDecoder.createSource(this.contentResolver,gorselUri!!)
//                        gorselBitmap = ImageDecoder.decodeBitmap(source)
//                        imageView.setImageBitmap(gorselBitmap)
//                    }else {
//                        gorselBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,gorselUri)
//                        imageView.setImageBitmap(gorselBitmap)
//                    }
//
//                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}