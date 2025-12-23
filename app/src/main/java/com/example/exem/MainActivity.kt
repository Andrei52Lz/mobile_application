package com.example.exem

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var chips: ChipGroup
    private lateinit var excuseText: TextView

    private lateinit var generateBtn: MaterialButton
    private lateinit var favBtn: MaterialButton
    private lateinit var copyBtn: MaterialButton
    private lateinit var shareBtn: MaterialButton

    private lateinit var store: FavoritesStore
    private lateinit var themeStore: ThemeStore

    private var currentExcuse: String = "Нажми «Сгенерировать», чтобы получить отговорку 🙂"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        store = FavoritesStore(this)
        themeStore = ThemeStore(this)

        toolbar = findViewById(R.id.toolbar)
        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }
                R.id.action_theme -> {
                    showThemeDialog()
                    true
                }
                else -> false
            }
        }

        chips = findViewById(R.id.categoryChips)
        excuseText = findViewById(R.id.excuseText)

        generateBtn = findViewById(R.id.generateBtn)
        favBtn = findViewById(R.id.favBtn)
        copyBtn = findViewById(R.id.copyBtn)
        shareBtn = findViewById(R.id.shareBtn)

        if (chips.checkedChipId == -1) chips.check(R.id.chipStudy)

        generateBtn.setOnClickListener {
            val cat = when (chips.checkedChipId) {
                R.id.chipStudy -> ExcuseRepository.Category.STUDY
                R.id.chipWork -> ExcuseRepository.Category.WORK
                else -> ExcuseRepository.Category.FAMILY
            }
            currentExcuse = ExcuseRepository.random(cat)

            excuseText.animate().cancel()
            excuseText.alpha = 0f
            excuseText.text = currentExcuse
            excuseText.animate().alpha(1f).setDuration(180).start()

            updateFavButton()
            updateShareCopyButtons()
        }

        favBtn.setOnClickListener {
            val text = currentExcuse
            if (!canUseText(text)) return@setOnClickListener

            lifecycleScope.launch {
                val isFav = store.isFavorite(text)
                if (isFav) store.remove(text) else store.add(text)
                updateFavButton()
            }
        }

        copyBtn.setOnClickListener {
            val text = currentExcuse
            if (!canUseText(text)) return@setOnClickListener

            val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("excuse", text))
            Toast.makeText(this, "Скопировано ✅", Toast.LENGTH_SHORT).show()
        }

        shareBtn.setOnClickListener {
            val text = currentExcuse
            if (!canUseText(text)) return@setOnClickListener

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(intent, "Поделиться"))
        }

        updateFavButton()
    }

    private fun canUseText(text: String): Boolean {
        return text.isNotBlank() && !text.startsWith("Нажми")
    }

    private fun updateShareCopyButtons() {
        val ok = canUseText(currentExcuse)
        copyBtn.isEnabled = ok
        shareBtn.isEnabled = ok
    }

    private fun updateFavButton() {
        val text = currentExcuse
        if (!canUseText(text)) {
            favBtn.isEnabled = false
            favBtn.text = "☆ В избранное"
            return
        }

        favBtn.isEnabled = true
        lifecycleScope.launch {
            val isFav = store.isFavorite(text)
            favBtn.text = if (isFav) "★ В избранном" else "☆ В избранное"
        }
    }

    private fun showThemeDialog() {
        lifecycleScope.launch {
            val current = themeStore.getMode() // system | light | dark
            val items = arrayOf("Как в системе", "Светлая", "Тёмная")
            val checked = when (current) {
                "light" -> 1
                "dark" -> 2
                else -> 0
            }

            AlertDialog.Builder(this@MainActivity)
                .setTitle("Тема")
                .setSingleChoiceItems(items, checked) { dialog, which ->
                    val mode = when (which) {
                        1 -> "light"
                        2 -> "dark"
                        else -> "system"
                    }
                    lifecycleScope.launch {
                        themeStore.setMode(mode)
                        val night = when (mode) {
                            "light" -> AppCompatDelegate.MODE_NIGHT_NO
                            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        }
                        AppCompatDelegate.setDefaultNightMode(night)
                        recreate()

                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        updateFavButton()
    }
}
