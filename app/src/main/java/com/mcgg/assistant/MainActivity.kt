
package com.mcgg.assistant

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.text.Editable
import android.text.TextWatcher
import org.json.JSONArray
import org.json.JSONObject

data class HeroItem(
    val name: String,
    val gold: Int,
    val stars: Int
)

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("mcgg_settings", MODE_PRIVATE)
    }

    private val heroes = mutableListOf<HeroItem>()
    private lateinit var heroList: LinearLayout
    private lateinit var search: EditText
    private var autoRoll = true
    private var reserveGold = 20

    private val bg = Color.rgb(16, 24, 39)
    private val panel = Color.rgb(31, 43, 62)
    private val goldColor = Color.rgb(255, 212, 119)
    private val white = Color.WHITE
    private val muted = Color.rgb(185, 198, 218)

    private fun dp(n: Int) =
        (n * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = 0

        loadData()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(24))
            setBackgroundColor(bg)
        }

        fun text(
            value: String,
            size: Float,
            color: Int = white,
            bold: Boolean = false
        ) = TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(7), 0, dp(7))
        }

        fun section(value: String) {
            root.addView(text(value, 17f, goldColor, true))
        }

        fun makeSpinner(values: List<String>): Spinner {
            return Spinner(this).apply {
                adapter = object : ArrayAdapter<String>(
                    this@MainActivity,
                    android.R.layout.simple_spinner_item,
                    values
                ) {
                    private fun styled(position: Int): TextView {
                        return TextView(this@MainActivity).apply {
                            text = getItem(position)
                            textSize = 15f
                            setTextColor(white)
                            setPadding(dp(12), dp(12), dp(8), dp(12))
                        }
                    }

                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View = styled(position)

                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View = styled(position)
                }
                setPopupBackgroundDrawable(
                    android.graphics.drawable.ColorDrawable(panel)
                )
            }
        }

        fun buttonStyle(button: Button) {
            button.backgroundTintList = ColorStateList.valueOf(panel)
            button.setTextColor(goldColor)
            button.textSize = 14f
        }

        root.addView(text("⚔ MCGG AUTO ASSISTANT", 23f, goldColor, true))
        root.addView(text("OFFLINE LINEUP MANAGER • 0.2.0", 12f, muted))

        section("PENGATURAN")

        root.addView(text("Minimal gold disimpan", 14f))
        val reserve = makeSpinner(
            listOf("20 Gold", "30 Gold", "40 Gold", "50 Gold")
        )
        reserve.setSelection(
            listOf(20, 30, 40, 50).indexOf(reserveGold)
                .coerceAtLeast(0)
        )
        reserve.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    reserveGold = listOf(20, 30, 40, 50)[position]
                    saveData()
                }
            }
        root.addView(reserve)

        val roll = Switch(this).apply {
            text = "Auto Roll"
            textSize = 16f
            setTextColor(white)
            isChecked = autoRoll
            setOnCheckedChangeListener { _, checked ->
                autoRoll = checked
                saveData()
            }
        }
        root.addView(roll)

        section("LINEUP BUILDER")
        root.addView(text("Nama hero", 14f))

        val nameInput = EditText(this).apply {
            hint = "Contoh: Sora"
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 16f
            singleLine = true
        }
        root.addView(nameInput)

        root.addView(text("Harga hero", 14f))
        val tier = makeSpinner(
            listOf("1 Gold", "2 Gold", "3 Gold", "4 Gold", "5 Gold")
        )
        root.addView(tier)

        root.addView(text("Target bintang", 14f))
        val star = makeSpinner(
            listOf("2 Bintang", "3 Bintang")
        )
        root.addView(star)

        val add = Button(this).apply {
            text = "TAMBAH HERO"
            buttonStyle(this)
            setOnClickListener {
                val name = nameInput.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Masukkan nama hero",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (heroes.any {
                        it.name.equals(name, true)
                    }) {
                    Toast.makeText(
                        this@MainActivity,
                        "Hero sudah ada di lineup",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    heroes.add(
                        HeroItem(
                            name,
                            tier.selectedItemPosition + 1,
                            star.selectedItemPosition + 2
                        )
                    )
                    nameInput.text.clear()
                    saveData()
                    renderHeroes()
                }
            }
        }
        root.addView(add)

        section("DAFTAR LINEUP")

        search = EditText(this).apply {
            hint = "Cari hero dalam lineup..."
            setHintTextColor(muted)
            setTextColor(white)
            singleLine = true
        }
        root.addView(search)

        heroList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        root.addView(heroList)

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}
            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                renderHeroes()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        section("STATUS")
        root.addView(
            text(
                "Database dan lineup disimpan secara lokal. " +
                "Belum terhubung dengan shop game.",
                13f,
                muted
            )
        )

        val info = text(
            "Versi 0.2.0 adalah pengelola lineup offline. " +
            "Auto Buy dan Auto Roll belum diaktifkan.",
            12f,
            goldColor
        )
        root.addView(info)

        renderHeroes()

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
            addView(root)
        }
        setContentView(scroll)
    }

    private fun loadData() {
        autoRoll = prefs.getBoolean("auto_roll", true)
        reserveGold = prefs.getInt("reserve_gold", 20)

        val array = JSONArray(prefs.getString("heroes", "[]"))

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            heroes.add(
                HeroItem(
                    obj.getString("name"),
                    obj.getInt("gold"),
                    obj.getInt("stars")
                )
            )
        }
    }

    private fun saveData() {
        val array = JSONArray()

        heroes.forEach {
            array.put(
                JSONObject().apply {
                    put("name", it.name)
                    put("gold", it.gold)
                    put("stars", it.stars)
                }
            )
        }

        prefs.edit()
            .putString("heroes", array.toString())
            .putBoolean("auto_roll", autoRoll)
            .putInt("reserve_gold", reserveGold)
            .apply()
    }

    private fun renderHeroes() {
        if (!::heroList.isInitialized) return

        heroList.removeAllViews()

        val keyword = if (::search.isInitialized) {
            search.text.toString().trim()
        } else ""

        val filtered = heroes.filter {
            it.name.contains(keyword, true)
        }

        if (filtered.isEmpty()) {
            heroList.addView(
                TextView(this).apply {
                    text = if (heroes.isEmpty())
                        "Belum ada hero. Tambahkan hero di atas."
                    else
                        "Hero tidak ditemukan."
                    setTextColor(muted)
                    textSize = 13f
                    setPadding(0, dp(12), 0, dp(12))
                }
            )
        }

        filtered.forEach { hero ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(10), dp(8), dp(5), dp(8))
                setBackgroundColor(panel)
            }

            val details = TextView(this).apply {
                text = "${hero.name}\n${hero.gold} Gold • ${hero.stars} Bintang"
                textSize = 14f
                setTextColor(white)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val remove = Button(this).apply {
                text = "HAPUS"
                textSize = 11f
                setTextColor(Color.rgb(255, 160, 160))
                backgroundTintList =
                    ColorStateList.valueOf(bg)

                setOnClickListener {
                    heroes.remove(hero)
                    saveData()
                    renderHeroes()
                }
            }

            row.addView(details)
            row.addView(remove)

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dp(6)
            heroList.addView(row, params)
        }
    }
}
