package com.mcgg.assistant

import android.app.Activity
import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    private var autoRoll = true
    private var reserveGold = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 32, 28, 24)
            setBackgroundColor(0xFF101827.toInt())
        }

        fun label(text: String, size: Float, color: Int = 0xFFFFFFFF.toInt(), bold: Boolean = false): TextView =
            TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(color)
                if (bold) setTypeface(typeface, Typeface.BOLD)
                setPadding(0, 10, 0, 10)
            }

        root.addView(label("⚔ MCGG AUTO ASSISTANT", 22f, 0xFFFFD477.toInt(), true))
        root.addView(label("Offline Lineup Manager • Prototype 0.1.0", 13f, 0xFFB8C4D8.toInt()))
        root.addView(label("PENGATURAN", 14f, 0xFFFFD477.toInt(), true))

        val reserve = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item,
                listOf("Cadangan 20 Gold", "Cadangan 30 Gold", "Cadangan 40 Gold", "Cadangan 50 Gold"))
            setSelection(0)
            onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    reserveGold = listOf(20, 30, 40, 50)[position]
                }
            }
        }
        root.addView(label("Minimal gold disimpan", 14f))
        root.addView(reserve)

        val roll = Switch(this).apply {
            text = "Auto Roll"
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            isChecked = autoRoll
            setOnCheckedChangeListener { _, checked -> autoRoll = checked }
        }
        root.addView(roll)

        root.addView(label("LINEUP", 14f, 0xFFFFD477.toInt(), true))
        root.addView(label("Database hero lengkap akan dimasukkan setelah roster dan portrait diverifikasi.", 14f, 0xFFB8C4D8.toInt()))

        val heroName = EditText(this).apply {
            hint = "Ketik nama hero"
            setHintTextColor(0xFF8794A8.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setSingleLine(true)
        }
        root.addView(heroName)

        val stars = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item,
                listOf("Target 2 bintang", "Target 3 bintang"))
        }
        root.addView(stars)

        val add = Button(this).apply {
            text = "Tambah ke Lineup"
            setOnClickListener {
                val name = heroName.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Masukkan nama hero terlebih dahulu", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "$name ditambahkan (prototype)", Toast.LENGTH_SHORT).show()
                    heroName.text.clear()
                }
            }
        }
        root.addView(add)

        root.addView(label("STATUS", 14f, 0xFFFFD477.toInt(), true))
        root.addView(label("Belum terhubung ke game. Fitur scan shop dan kontrol otomatis belum diaktifkan.", 13f, 0xFFB8C4D8.toInt()))

        val start = Button(this).apply {
            text = "START (PROTOTIPE)"
            isEnabled = false
        }
        root.addView(start)

        root.addView(label("Catatan: aplikasi ini belum melakukan pembelian atau roll otomatis. Ini kerangka awal untuk menguji UI dan penyimpanan konfigurasi.", 12f, 0xFFFFC2A8.toInt()))
        setContentView(ScrollView(this).apply { addView(root) })
    }
}
