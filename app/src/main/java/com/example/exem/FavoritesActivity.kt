package com.example.exem


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: FavoritesAdapter
    private lateinit var store: FavoritesStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.favToolbar)
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        toolbar.setNavigationOnClickListener { finish() }

        store = FavoritesStore(this)

        recycler = findViewById(R.id.favRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = FavoritesAdapter(emptyList()) { text ->
            lifecycleScope.launch { store.remove(text) }
        }
        recycler.adapter = adapter

        lifecycleScope.launch {
            store.favoritesFlow.collectLatest { set ->
                adapter.submit(set.toList().sorted())
            }
        }
    }
}

